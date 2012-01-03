package com.jonglen7.jugglinglab.ui;

import greendroid.app.GDActivity;
import greendroid.graphics.drawable.ActionBarDrawable;
import greendroid.widget.ActionBarItem;
import greendroid.widget.NormalActionBarItem;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.jonglen7.jugglinglab.R;

public class HomeActivity extends GDActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /** ActionBar. */
        setActionBarContentView(R.layout.activity_home);

        addActionBarItem(getActionBar()
                .newActionBarItem(NormalActionBarItem.class)
                .setDrawable(new ActionBarDrawable(this, R.drawable.gd_action_bar_info)), R.id.action_bar_view_info);
    }

    /** ActionBar. */
    @Override
    public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
        switch (item.getItemId()) {
            case R.id.action_bar_view_info:
                startActivity(new Intent(this, AboutActivity.class));
                return true;

            default:
                return super.onHandleActionBarItemClick(item, position);
        }
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
    	Intent i = new Intent(this, CollectionsActivity.class);
    	i.putExtra("IS_TUTORIAL", 1);
        startActivity(i);
    }
    
    /** Handle "Pattern List" action. */
    public void onPatternListClick(View v) {
    	Intent i = new Intent(this, CollectionsActivity.class);
    	i.putExtra("IS_TUTORIAL", 0);
        startActivity(i);
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