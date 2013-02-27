package com.jonglen7.jugglinglab.ui;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
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
	
	@Override
	protected void onStart() {
		super.onStart();
		/*
		colorPrefScreen = (PreferenceScreen) this.getPreferenceScreen().findPreference("colorPrefScreen");
		colorPrefScreen.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				return onPropColorPreferenceClick(preference);
			}
		});
		*/
	}
	
	private boolean onPropColorPreferenceClick(Preference pref) {
		Intent intent = new Intent(this, ColorPreferenceActivity.class);
		startActivityForResult(intent, org.superdry.util.colorpicker.lib.SuperdryColorPicker.ACTION_GETCOLOR);
		return true;
	}
}
