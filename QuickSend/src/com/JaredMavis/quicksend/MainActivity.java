package com.JaredMavis.quicksend;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import com.JaredMavis.quicksend.QuickEmail.QuickEmailListener;
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
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Primary activity. Will display a list of the user's email templates.
 * If given an intent bundle for an email it fill out the templates.
 * @author Jared Mavis
 */
public class MainActivity extends ActionBarActivity {
	private static final String TAG = "MainActivity";
	
	private static final String QUICK_EMAIL_STORAGE_KEY = "QUICK_EMAIL_STORAGE_KEY";
	
	private class QuickEmailList {
		List<QuickEmail> listOfEmails;
		
		public QuickEmailList(SharedPreferences sharedPrefs) {
			listOfEmails = this.getStoredQuickEmails(sharedPrefs);
			for (QuickEmail email : listOfEmails) {
				email.setEmailListener(quickMailListener());
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
						QuickEmail email = new QuickEmail(jsonEmails.getJSONObject(i));
						emails.add(email);
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

		public List<QuickEmail> getEmails() {
			return this.listOfEmails;
		}
		
		private QuickEmailListener quickMailListener() {
			return new QuickEmailListener() {
				@Override
				public void onSendEmailClick(Intent emailIntent) {
					try {
					    startActivity(emailIntent);
					} catch (android.content.ActivityNotFoundException ex) {
					    Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
					}
				}
			};
			
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Intent intent = getIntent();
	    String action = intent.getAction();
	    String type = intent.getType();
	    String subject = null;
	    String text = null;
		
	    if (Intent.ACTION_SEND.equals(action) && type != null) {
	        if ("text/plain".equals(type)) {
	        	text = intent.getStringExtra(Intent.EXTRA_TEXT);
	        	subject = intent.getStringExtra(Intent.EXTRA_SUBJECT);
			}
		}
		
		LayoutInflater emailTypesInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout emailTemplateList = (LinearLayout) findViewById(R.id.emailTemplateList);
		QuickEmailList emailList = new QuickEmailList(PreferenceManager.getDefaultSharedPreferences(this));
		List<QuickEmail> debugList = emailList.getEmails();
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
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
