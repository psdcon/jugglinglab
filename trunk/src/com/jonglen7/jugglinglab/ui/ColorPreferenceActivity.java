/******
 * File from SuperdryColorPickerApp
 * Under the MIT License
 * Copyright (c) 2011 superdry
 * 
 * Modifications have been brought to store all string in xml files
 * 
 */

package com.jonglen7.jugglinglab.ui;

import com.jonglen7.jugglinglab.R;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.DisplayMetrics;

public class ColorPreferenceActivity extends PreferenceActivity {

	private SharedPreferences sharedPreferences;
	private int changedColor;
	private int dpi = 0;
	private static final String COLORVALUE_KEY = "SelectedColor";
	private static final String COLORVIEW_KEY = "colorPicker";
	private static final int DEFAULT_COLOR_VALUE = 0xFFFF0000;
	ColorPickerPreference scp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (dpi == 0) {
			DisplayMetrics metrics = new DisplayMetrics();
			this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
			dpi = metrics.densityDpi;
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		this.addPreferencesFromResource(R.xml.settings_animation_color);

		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		changedColor = sharedPreferences.getInt(COLORVALUE_KEY,
				DEFAULT_COLOR_VALUE);
		scp = (ColorPickerPreference) this.getPreferenceScreen()
				.findPreference(COLORVIEW_KEY);
		scp.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				return onColorPreferenceClick(preference);
			}
		});
		scp.setDpi(dpi);
		scp.setColor(changedColor);
		scp.setSummary(color2String(changedColor));
	}

	private boolean onColorPreferenceClick(Preference pref) {
		Intent intent = new Intent(this,
				org.superdry.util.colorpicker.lib.SuperdryColorPicker.class);
		intent.putExtra(COLORVALUE_KEY, changedColor);
		startActivityForResult(
				intent,
				org.superdry.util.colorpicker.lib.SuperdryColorPicker.ACTION_GETCOLOR);
		return true;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == org.superdry.util.colorpicker.lib.SuperdryColorPicker.ACTION_GETCOLOR) {
			if (resultCode == RESULT_OK) {
				Bundle b = intent.getExtras();
				if (b != null) {
					changedColor = b.getInt(COLORVALUE_KEY);
				}
			} else if (resultCode == RESULT_CANCELED) {
				// Nothing
			}
			scp = (ColorPickerPreference) this.getPreferenceScreen()
					.findPreference(COLORVIEW_KEY);
			scp.setColor(changedColor);
			scp.setSummary(color2String(changedColor));

			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putInt(COLORVALUE_KEY, changedColor);
			editor.commit();
			
			// The 3 following line has been added 
			// in order to pass result to JMLPatternActivity
			SharedPreferences preferences = getSharedPreferences("com.jonglen7.jugglinglab_preferences", 0);
			Editor editor_ = preferences.edit();
			editor_.putInt(COLORVALUE_KEY, changedColor);
			editor_.commit();
		}
	}
	private String color2String(int color) {
		return String.format("#%02x%02x%02x", Color.red(color),
				Color.green(color), Color.blue(color));
	}
}
