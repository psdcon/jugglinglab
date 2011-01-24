package com.jonglen7.jugglinglab.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.jonglen7.jugglinglab.R;

public class HomeActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }
    
    /** Menu button. */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_home_settings:
            	startActivity(new Intent(this, SettingsHomeActivity.class));
                break;
            case R.id.menu_home_about:
            	startActivity(new Intent(this, AboutActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    
    /** Handle "Pattern Entry" action. */
    public void onPatternEntryClick(View v) {
        startActivity(new Intent(this, PatternEntryActivity.class));
    }
    
    /** Handle "Generator" action. */
    public void onGeneratorClick(View v) {
        startActivity(new Intent(this, GeneratorActivity.class));
    }
    
    /** Handle "Tutorials" action. */
    public void onTutorialsClick(View v) {
        startActivity(new Intent(this, TutorialsActivity.class));
    }
    
    /** Handle "My profile" action. */
    public void onMyProfileClick(View v) {
        startActivity(new Intent(this, MyProfileActivity.class));
    }
    
    /** Handle "Video" action. */
    public void onVideoClick(View v) {
        startActivity(new Intent(this, VideoActivity.class));
    }
}