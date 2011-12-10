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
        Intent intent = new Intent(this, MyProfileTabActivity.class);

        tabHost.addTab(tabHost.newTabSpec("tab_starred").setIndicator(getString(R.string.my_profile_tab_starred)).setContent(intent.putExtra("tab", "starred")));
        // TODO Romain: Uncomment when ready
//        tabHost.addTab(tabHost.newTabSpec("tab_goals").setIndicator(getString(R.string.my_profile_tab_goals)).setContent(intent.putExtra("tab", "goals")));
//        tabHost.addTab(tabHost.newTabSpec("tab_practicing").setIndicator(getString(R.string.my_profile_tab_practicing)).setContent(intent.putExtra("tab", "practicing")));

        tabHost.setCurrentTab(0);
	}

}
