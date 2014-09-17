package com.JaredMavis.quicksend;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
	private class QuickEmail {
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
		
		public LinearLayout generate(LayoutInflater inflater, ViewGroup root, String subject, String text){
			_subject = subject;
			_text = text;
			View generatedView = inflater.inflate(R.layout.email_template, root);
			TextView subjectText = (TextView) generatedView.findViewById(R.id.subjectContet);
			subjectText.setText( _subjectPrefix + text + _subjectSuffix);
			
			TextView emailLabel = (TextView) generatedView.findViewById(R.id.emailsLabel);
			emailLabel.setText(_emails.toString());
			
			Button debugSendButton = (Button) findViewById(R.id.sendDebugBtn);
			debugSendButton.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					sendEmail(_subject, _text);
				}
			});
			return (LinearLayout) generatedView;
		}
	}
	
	private List<QuickEmail> generateDebugSettings(){
		List<QuickEmail> debugEmailList = new ArrayList<QuickEmail>();
		String[] testersEmails = {"hellfire073@hotmail.com"};
		debugEmailList.add(new QuickEmail(testersEmails, "Hai", "Bye"));
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
	    String subject = "EXAMPLE";
		
	    if (Intent.ACTION_SEND.equals(action) && type != null) {
	        if ("text/plain".equals(type)) {
	        	subject = intent.getStringExtra(Intent.EXTRA_TEXT);
			}
		}
		
		LayoutInflater emailTypesInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout emailTemplateList = (LinearLayout) findViewById(R.id.emailTemplateList);
		List<QuickEmail> debugList = generateDebugSettings();
		for (QuickEmail emailTemplate : debugList){
			emailTemplateList.addView(emailTemplate.generate(emailTypesInflater, emailTemplateList, subject, "TEST-TEXT"));
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
