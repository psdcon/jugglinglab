package com.jonglen7.jugglinglab.ui;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

import com.jonglen7.jugglinglab.R;

public class SettingsActivity extends PreferenceActivity {

	PreferenceScreen colorPrefScreen;
	PreferenceScreen backgroundColorPicker;
	int newBackgroundColor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
	}
	
	@Override
	public void onBackPressed() {
		setResult(RESULT_OK);
		super.onBackPressed();
	}
}
