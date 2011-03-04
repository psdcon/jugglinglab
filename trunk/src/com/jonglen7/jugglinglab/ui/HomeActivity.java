package com.jonglen7.jugglinglab.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.jonglen7.jugglinglab.R;
import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.IntentAction;

public class HomeActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        /** ActionBar. */
        final ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
        actionBar.setHomeAction(new IntentAction(this, createIntent(this), R.drawable.ic_title_home_default));
    }

    /** ActionBar. */
    public static Intent createIntent(Context context) {
        Intent i = new Intent(context, HomeActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return i;
    }
    
    /** Menu button. */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
            	startActivity(new Intent(this, SettingsHomeActivity.class));
                break;
            case R.id.menu_about:
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
    
    /** Handle "Pattern List" action. */
    public void onPatternListClick(View v) {
        startActivity(new Intent(this, PatternListActivity.class));
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