package com.jonglen7.jugglinglab.ui;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TabHost;

import com.jonglen7.jugglinglab.R;

public class MyProfileActivity extends TabActivity {
	
	private TabHost tabHost;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        
        buildTabs();
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
    
	private void buildTabs(){
        tabHost = getTabHost();

        tabHost.addTab(tabHost.newTabSpec("tab_favs").setIndicator(getString(R.string.my_profile_tab_favs)).setContent(R.id.my_profile_content));
        tabHost.addTab(tabHost.newTabSpec("tab_practising").setIndicator(getString(R.string.my_profile_tab_practicing)).setContent(R.id.my_profile_content));
        tabHost.addTab(tabHost.newTabSpec("tab_juggler").setIndicator(getString(R.string.my_profile_tab_juggler)).setContent(R.id.my_profile_content));

        tabHost.setCurrentTab(0);
	}

}
