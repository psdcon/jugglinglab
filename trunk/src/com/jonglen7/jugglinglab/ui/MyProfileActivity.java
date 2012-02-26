package com.jonglen7.jugglinglab.ui;

import greendroid.app.GDTabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TabHost;

import com.jonglen7.jugglinglab.R;

public class MyProfileActivity extends GDTabActivity {
	
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
        }
        return super.onOptionsItemSelected(item);
    }
    
    /** Tabs. */
	private void buildTabs(){
        tabHost = getTabHost();

        tabHost.addTab(tabHost.newTabSpec("tab_starred")
        		.setIndicator(getString(R.string.my_profile_tab_starred))
        		.setContent(new Intent(this, MyProfileTabActivity.class)
        				.putExtra("tab", "starred")
        				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
        tabHost.addTab(tabHost.newTabSpec("tab_unsorted")
        		.setIndicator(getString(R.string.my_profile_tab_unsorted))
        		.setContent(new Intent(this, MyProfileTabActivity.class)
        				.putExtra("tab", "unsorted")
        				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
        // TODO Romain (Stats): Uncomment when ready (Goals, practicing)
//        tabHost.addTab(tabHost.newTabSpec("tab_goals")
//        		.setIndicator(getString(R.string.my_profile_tab_goals))
//        		.setContent(new Intent(this, MyProfileTabActivity.class)
//        				.putExtra("tab", "goals")
//        				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
//        tabHost.addTab(tabHost.newTabSpec("tab_training")
//        		.setIndicator(getString(R.string.my_profile_tab_training))
//        		.setContent(new Intent(this, MyProfileTabActivity.class)
//        				.putExtra("tab", "training")
//        				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));

        tabHost.setCurrentTab(0);
	}

}
