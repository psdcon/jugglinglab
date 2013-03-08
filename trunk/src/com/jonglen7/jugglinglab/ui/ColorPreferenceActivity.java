/******
 * File from SuperdryColorPickerApp
 * Under the MIT License
 * Copyright (c) 2011 superdry
 * 
 * Modifications have been brought to store all string in xml files
 * 
 */

package com.jonglen7.jugglinglab.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;

import com.jonglen7.jugglinglab.R;

public class ColorPreferenceActivity extends PreferenceActivity {

	private SharedPreferences sharedPreferences;
	private static final String COLORVALUE_KEY = "SelectedColor";
	private static final int DEFAULT_COLOR_VALUE = 0xFFFF0000;
	private int dpi = 0;
	
	
	
	private static final String COLORVIEW_KEY = "colorPicker";

	
	// To be given in Extras
	private String type = "Prop";			// "Prop" or "Juggler"
	private int nb_item = 5;				// nb_prop or nb_juggler
	
	private int changedColor[];
	private ColorPickerPreference scp[];
	private int currentColorPickerPreference = 0;
	
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Set dpi
		if (dpi == 0) {
			DisplayMetrics metrics = new DisplayMetrics();
			this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
			dpi = metrics.densityDpi;
		}
		
		// Initialize arrays
		// +1 for the OneColor case (stocked in [0])
		changedColor = new int[nb_item+1];
		scp = new ColorPickerPreference[nb_item+1];
		
		// Set the Preferences XML description
		if (type.compareTo("Prop") == 0) {
				this.addPreferencesFromResource(R.xml.settings_animation_prop_color);
		} else if (type.compareTo("Juggler") == 0) {
			this.addPreferencesFromResource(R.xml.settings_animation_juggler_color);
		} else {
		}
		
		// Initialize the ColorickerPreference for the All item color
		initAllItemColorPickerPreference();
		
		// Get The PreferenceCategory
		PreferenceGroup pg = (PreferenceGroup)this.getPreferenceScreen();
		PreferenceCategory pc = null;
		if (type.compareTo("Prop") == 0) {
			pc = (PreferenceCategory)pg.findPreference("prop_by_prop_category");
		} else if ((type.compareTo("Juggler") == 0) && (nb_item >1)) {
			pc = (PreferenceCategory)pg.findPreference("juggler_by_juggler_category");
		} else {
		}
		
		// TODO Fred: Use dependencies to display or not for each prop maybe regarding a check box "Display prop by Prop"?
		if (pc != null){
			for (int item=1; item<=nb_item; item++) {
				
				changedColor[item] = sharedPreferences.getInt(COLORVALUE_KEY + "_for_item_" + item,
						DEFAULT_COLOR_VALUE);
				
				ColorPickerPreference cpp = new ColorPickerPreference(this, null);
				cpp.setTitle(type + " #" + item);
				cpp.setSummary(color2String(changedColor[item]));
				cpp.setColor(changedColor[item]);
				cpp.setDpi(dpi);
				
				cpp.setOnPreferenceClickListener(new OnColorPreferenceClickListener(this, item));
				
				pc.addPreference(cpp);
				scp[item] = cpp;
				
			}		
			pg.addPreference(pc);
		}
	}
	

    
	/** Called when the activity is paused. */
    @Override
    public void onPause() {
    	super.onPause();
    }
    
	/** Called when the activity is resumed. */
    @Override
    public void onResume() {
    	super.onResume();
    }
    

	
	
	protected void initAllItemColorPickerPreference() {
		super.onStart();
		
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
			
		changedColor[0] = sharedPreferences.getInt(COLORVALUE_KEY + "_for_item_0",
				DEFAULT_COLOR_VALUE);
		scp[0] = (ColorPickerPreference) this.getPreferenceScreen()
				.findPreference(COLORVIEW_KEY);
		scp[0].setOnPreferenceClickListener(new OnColorPreferenceClickListener(this, 0));
		scp[0].setDpi(dpi);
		scp[0].setColor(changedColor[0]);
		scp[0].setSummary(color2String(changedColor[0]));
	}
	
	/*
	 * Listener on a ColorPickerPreference
	 */
	class OnColorPreferenceClickListener implements OnPreferenceClickListener {
		
		private Context context = null;
		private int position = 0;
		
		public OnColorPreferenceClickListener(Context context, int position) {
			this.context = context;
			this.position = position;
		}
		
		public boolean onPreferenceClick(Preference preference) {
			currentColorPickerPreference = position;
			Intent intent = new Intent(context,
					org.superdry.util.colorpicker.lib.SuperdryColorPicker.class);		
			intent.putExtra(COLORVALUE_KEY, changedColor[position]);
			startActivityForResult(
					intent,
					org.superdry.util.colorpicker.lib.SuperdryColorPicker.ACTION_GETCOLOR);
			return true;
		}
		
		
	}

	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == org.superdry.util.colorpicker.lib.SuperdryColorPicker.ACTION_GETCOLOR) {
			if (resultCode == RESULT_OK) {
				Bundle b = intent.getExtras();
				if (b != null) {
					changedColor[currentColorPickerPreference] = b.getInt(COLORVALUE_KEY, DEFAULT_COLOR_VALUE);
				}
			} else if (resultCode == RESULT_CANCELED) {
				// Nothing
			}
			
			scp[currentColorPickerPreference].setColor(changedColor[currentColorPickerPreference]);
			scp[currentColorPickerPreference].setSummary(color2String(changedColor[currentColorPickerPreference]));
			
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putInt(COLORVALUE_KEY + "_for_item_" + currentColorPickerPreference, changedColor[currentColorPickerPreference]);
			editor.commit();
			
			// The 3 following line has been added 
			// in order to pass result to JMLPatternActivity
			SharedPreferences preferences = getSharedPreferences("com.jonglen7.jugglinglab_preferences", 0);
			Editor editor_ = preferences.edit();
			editor_.putInt(COLORVALUE_KEY + "_for_item_" + currentColorPickerPreference, changedColor[currentColorPickerPreference]);
			editor_.commit();
			
			onContentChanged();
			
		}
	}
	
	
	
	
	private String color2String(int color) {
		return String.format("#%02x%02x%02x", Color.red(color),
				Color.green(color), Color.blue(color));
	}
}
