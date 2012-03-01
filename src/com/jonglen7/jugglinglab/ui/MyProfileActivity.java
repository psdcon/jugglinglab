package com.jonglen7.jugglinglab.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

import com.jonglen7.jugglinglab.R;

public class MyProfileActivity extends BaseTabActivity {
	
	private TabHost tabHost;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_my_profile);
        buildTabs();
    }

    @Override
    public int createLayout() {
    	return R.layout.activity_my_profile;
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
