package com.jonglen7.jugglinglab.ui;

import android.app.TabActivity;
import android.os.Bundle;
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
    
	private void buildTabs(){
        tabHost = getTabHost();

        tabHost.addTab(tabHost.newTabSpec("tab_test1").setIndicator(getString(R.string.my_profile_tab_favs)).setContent(R.id.my_profile_content));
        tabHost.addTab(tabHost.newTabSpec("tab_test2").setIndicator(getString(R.string.my_profile_tab_practicing)).setContent(R.id.my_profile_content));
        tabHost.addTab(tabHost.newTabSpec("tab_test3").setIndicator(getString(R.string.my_profile_tab_records)).setContent(R.id.my_profile_content));

        tabHost.setCurrentTab(0);
	}

}
