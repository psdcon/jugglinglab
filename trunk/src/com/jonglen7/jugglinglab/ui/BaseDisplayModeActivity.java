package com.jonglen7.jugglinglab.ui;

import greendroid.widget.ActionBarItem;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import com.jonglen7.jugglinglab.R;

public class BaseDisplayModeActivity extends BaseActivity {
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addActionBarItem(ActionBarItem.Type.Eye, R.id.action_bar_switch_display_mode);
    }

	/** Called when the activity is resumed. */
    @Override
    public void onResume() {
    	super.onResume();
    	switchDisplayMode();
    }

    /** ActionBar. */
    @Override
    public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
        switch (item.getItemId()) {
            case R.id.action_bar_switch_display_mode:
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                Editor editor = preferences.edit();
                boolean user_advanced_mode = !preferences.getBoolean("user_advanced_mode", false);
                editor.putBoolean("user_advanced_mode", user_advanced_mode);
                editor.commit();
                switchDisplayMode();
                Toast.makeText(this, getString(R.string.advanced_mode) + " " + (user_advanced_mode ? getString(R.string.activated) : getString(R.string.deactivated)), Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onHandleActionBarItemClick(item, position);
        }
    }

    /** Hide or show some parameters depending if the Advanced mode is selected. */
    public void switchDisplayMode() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int visibility = (preferences.getBoolean("user_advanced_mode", false))?View.VISIBLE:View.GONE;
        switchDisplayMode(visibility);
    }
    
    /** Hide or show some parameters depending if the Advanced mode is selected. */
    public void switchDisplayMode(int visibility) {}
    
}
