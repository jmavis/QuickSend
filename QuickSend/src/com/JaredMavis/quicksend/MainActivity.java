package com.JaredMavis.quicksend;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

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
		
		public void sendEmail(String subject, String text){
			Intent emailIntent = new Intent(Intent.ACTION_SEND);
			emailIntent.setType(",essage/rfc822");
			emailIntent.putExtra(Intent.EXTRA_EMAIL  , _emails);
			emailIntent.putExtra(Intent.EXTRA_SUBJECT, _subjectPrefix + subject + _subjectSuffix);
			emailIntent.putExtra(Intent.EXTRA_TEXT   , text);
			try {
			    startActivity(emailIntent);
			} catch (android.content.ActivityNotFoundException ex) {
			    Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
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
