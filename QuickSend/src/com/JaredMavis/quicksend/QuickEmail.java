package com.JaredMavis.quicksend;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class QuickEmail {
	private static final String defaultSubject = "SUBJECT";
	private static final String defaultText = "TEXT";
	final static String SUBJECT_PREFIX_KEY = "SUBJECT_PREFIX_KEY";
	final static String SUBJECT_SUFFIX_KEY = "SUBJECT_SUFFIX_KEY";
	final static String EMAILS_KEY = "SUBJECT_PREFIX_KEY";
	
	private QuickEmailListener _emailListener;
	
	String[] _emails;
	String _subjectPrefix = "";
	String _subjectSuffix = "";
	
	String _subject;
	String _text;
	
	public interface QuickEmailListener{
		public void onSendEmailClick(Intent emailIntent);
	}
	
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
	
	public void setEmailListener(QuickEmailListener emailListener) {
		_emailListener = emailListener;
	}
	
	public void sendEmail(String subject, String emailContent){
		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		emailIntent.setType("message/rfc822");
		emailIntent.putExtra(Intent.EXTRA_EMAIL  , _emails);
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, _subjectPrefix + subject + _subjectSuffix);
		emailIntent.putExtra(Intent.EXTRA_TEXT   , emailContent);
		if (_emailListener != null) {
			_emailListener.onSendEmailClick(emailIntent);
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
		_subject = subject != null ? subject : defaultSubject;
		_text = text != null ? text : defaultText;
		
		View generatedView = inflater.inflate(R.layout.email_template, null);
		TextView subjectText = (TextView) generatedView.findViewById(R.id.subjectContet);
		subjectText.setText( _subjectPrefix + _subject + _subjectSuffix);
		
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
			e.printStackTrace();
		}
		return jsonFormat.toString();
	}
}