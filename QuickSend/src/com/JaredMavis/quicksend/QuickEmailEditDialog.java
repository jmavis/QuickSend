package com.JaredMavis.quicksend;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

public class QuickEmailEditDialog extends DialogFragment {
	private EditText mEditText;
	
	public interface EditEmailDialogListener {
        void onFinishEditDialog(String inputText);
    }


    public QuickEmailEditDialog() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_email_template, container);
        mEditText = (EditText) view.findViewById(R.id.subjectEditText);
        getDialog().setTitle("Edit");

        return view;
    }

	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
	    if (EditorInfo.IME_ACTION_DONE == actionId) {
	    	EditEmailDialogListener activity = (EditEmailDialogListener) getActivity();
	        activity.onFinishEditDialog(mEditText.getText().toString());
	        this.dismiss();
	        return true;
	    }
	    return false;
	}
}

