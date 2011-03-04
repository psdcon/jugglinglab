package com.jonglen7.jugglinglab.ui;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.jonglen7.jugglinglab.R;

public class SettingsAnimationActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings_animation);
	}
	
	@Override
	public void onBackPressed() {
		setResult(RESULT_OK);
		super.onBackPressed();
	}
}
