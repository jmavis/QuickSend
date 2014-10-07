package com.JaredMavis.quicksend;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Primary activity. Will display a list of the user's email templates.
 * If given an intent bundle for an email it fill out the templates.
 * @author Jared Mavis
 */
public class MainActivity extends ActionBarActivity {
	private static final String defaultSubject = "SUBJECT";
	private static final String defaultText = "TEXT";
	
	private static final String QUICK_EMAIL_STORAGE_KEY = "QUICK_EMAIL_STORAGE_KEY";
	
	private class QuickEmail {
		final static String SUBJECT_PREFIX_KEY = "SUBJECT_PREFIX_KEY";
		final static String SUBJECT_SUFFIX_KEY = "SUBJECT_SUFFIX_KEY";
		final static String EMAILS_KEY = "SUBJECT_PREFIX_KEY";
		
		String[] _emails;
		String _subjectPrefix = "";
		String _subjectSuffix = "";
		
		String _subject;
		String _text;
		
		public QuickEmail(String[] emails){
			_emails = emails;
		}
		
		public QuickEmail(String[] emails, String prefix, String suffix){
			this(emails);
			_subjectPrefix = prefix;
			_subjectSuffix = suffix;
		}
		
		public QuickEmail(JSONObject jsonFormat) throws JSONException{
			_subjectPrefix = jsonFormat.getString(SUBJECT_PREFIX_KEY);
			_subjectSuffix = jsonFormat.getString(SUBJECT_SUFFIX_KEY);
			JSONArray storedEmails = jsonFormat.getJSONArray(EMAILS_KEY);
			_emails = new String[storedEmails.length()];
			for (int i = 0; i < storedEmails.length(); i++){
				_emails[i] = storedEmails.getString(i);
			}
		}
		
		public void sendEmail(String subject, String emailContent){
			Intent emailIntent = new Intent(Intent.ACTION_SEND);
			emailIntent.setType("message/rfc822");
			emailIntent.putExtra(Intent.EXTRA_EMAIL  , _emails);
			emailIntent.putExtra(Intent.EXTRA_SUBJECT, _subjectPrefix + subject + _subjectSuffix);
			emailIntent.putExtra(Intent.EXTRA_TEXT   , emailContent);
			try {
			    startActivity(emailIntent);
			} catch (android.content.ActivityNotFoundException ex) {
			    Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
			}
		}
		
		private OnClickListener editListener(){
			return new OnClickListener() {
				public void onClick(View v) {
					// TODO make this edit the current email template
				}
			};
		}
		
		private OnClickListener sendListener(){
			return new OnClickListener() {
				public void onClick(View v) {
					sendEmail(_subject, _text);
				}
			};
		}
		
		
		public LinearLayout generate(LayoutInflater inflater, String subject, String text){
			_subject = subject;
			_text = text;
			View generatedView = inflater.inflate(R.layout.email_template, null);
			TextView subjectText = (TextView) generatedView.findViewById(R.id.subjectContet);
			subjectText.setText( _subjectPrefix + text + _subjectSuffix);
			
			TextView emailLabel = (TextView) generatedView.findViewById(R.id.emailsLabel);
			
			String emailList = "";
			
			for (String email : _emails){
				emailList += email;
			}
			
			emailLabel.setText(emailList);
			
			Button debugSendButton = (Button) generatedView.findViewById(R.id.sendDebugBtn);

			if (_subject.equals(defaultSubject)){
				debugSendButton.setText("Edit");
				debugSendButton.setOnClickListener(editListener());
			} else {
				debugSendButton.setText("Send");
				debugSendButton.setOnClickListener(sendListener());
			}
			
			return (LinearLayout) generatedView;
		}
		
		public String toString() {
			JSONObject jsonFormat = new JSONObject();
			try {
				jsonFormat.put(SUBJECT_PREFIX_KEY, this._subjectPrefix);
				jsonFormat.put(SUBJECT_SUFFIX_KEY, this._subjectSuffix);
				JSONArray emailsArray = new JSONArray();
				for (String email : _emails){
					emailsArray.put(email);
				}
				jsonFormat.put(EMAILS_KEY, emailsArray);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return jsonFormat.toString();
		}
	}
	
	private List<QuickEmail> getStoredQuickEmails(SharedPreferences prefs) {
		String jsonStringEmails = prefs.getString(QUICK_EMAIL_STORAGE_KEY, "");
		if (jsonStringEmails == "") {
			return this.generateDebugSettings();
		} else {
			try {
				JSONArray jsonEmails = new JSONArray(jsonStringEmails);
				List<QuickEmail> emails = new ArrayList<QuickEmail>();
				for (int i = 0; i < jsonEmails.length(); i++) {
					emails.add(new QuickEmail(jsonEmails.getJSONObject(i)));
				}
				return emails;
			} catch (JSONException e) {
				return this.generateDebugSettings();
			}
		}
	}
	
	private void storeQuickEmails(SharedPreferences prefs, List<QuickEmail> emails) {
		Editor editor = prefs.edit();
		JSONArray jsonEmails = new JSONArray();
		for (QuickEmail email : emails) {
			jsonEmails.put(email.toString());
		}
		editor.putString(QUICK_EMAIL_STORAGE_KEY, jsonEmails.toString());
		editor.commit();
	}

	
	private List<QuickEmail> generateDebugSettings(){
		List<QuickEmail> debugEmailList = new ArrayList<QuickEmail>();
		String[] testersEmails = {"hellfire073@hotmail.com"};
		debugEmailList.add(new QuickEmail(testersEmails, "", ""));
		debugEmailList.add(new QuickEmail(testersEmails, "No Suffix", ""));
		debugEmailList.add(new QuickEmail(testersEmails, "", "No Prefix"));
		return debugEmailList;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Intent intent = getIntent();
	    String action = intent.getAction();
	    String type = intent.getType();
	    String subject = defaultSubject;
	    String text = defaultText;
		
	    if (Intent.ACTION_SEND.equals(action) && type != null) {
	        if ("text/plain".equals(type)) {
	        	text = intent.getStringExtra(Intent.EXTRA_TEXT);
	        	subject = intent.getStringExtra(Intent.EXTRA_SUBJECT);
			}
		}
		
		LayoutInflater emailTypesInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout emailTemplateList = (LinearLayout) findViewById(R.id.emailTemplateList);
		List<QuickEmail> debugList = getStoredQuickEmails(PreferenceManager.getDefaultSharedPreferences(this));
		for (QuickEmail emailTemplate : debugList){
			emailTemplateList.addView(emailTemplate.generate(emailTypesInflater, subject, text));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
