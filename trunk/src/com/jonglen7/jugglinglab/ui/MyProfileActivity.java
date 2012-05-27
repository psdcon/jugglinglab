package com.jonglen7.jugglinglab.ui;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

import com.jonglen7.jugglinglab.R;

/**
 * The My Profile Activity.
 */
public class MyProfileActivity extends BaseTabActivity {
	
	private TabHost tabHost;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildTabs();
    }

    @Override
    public int createLayout() {
    	return R.layout.activity_my_profile;
    }
    
    /** Tabs. */
	private void buildTabs(){
        tabHost = getTabHost();
        Resources res = getResources();

        tabHost.addTab(tabHost.newTabSpec("tab_starred")
        		.setIndicator(getString(R.string.my_profile_tab_starred), res.getDrawable(R.drawable.gd_action_bar_star))
        		.setContent(new Intent(this, MyProfileTabActivity.class)
        				.putExtra("tab", "starred")
        				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
        tabHost.addTab(tabHost.newTabSpec("tab_unsorted")
        		.setIndicator(getString(R.string.my_profile_tab_unsorted), res.getDrawable(R.drawable.gd_action_bar_help))
        		.setContent(new Intent(this, MyProfileTabActivity.class)
        				.putExtra("tab", "unsorted")
        				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
        // TODO Romain (Stats): Uncomment when ready (Goals, practicing)
//        tabHost.addTab(tabHost.newTabSpec("tab_goals")
//        		.setIndicator(getString(R.string.my_profile_tab_goals), res.getDrawable(R.drawable.gd_action_bar_help))
//        		.setContent(new Intent(this, MyProfileTabActivity.class)
//        				.putExtra("tab", "goals")
//        				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
//        tabHost.addTab(tabHost.newTabSpec("tab_training")
//        		.setIndicator(getString(R.string.my_profile_tab_training), res.getDrawable(R.drawable.gd_action_bar_help))
//        		.setContent(new Intent(this, MyProfileTabActivity.class)
//        				.putExtra("tab", "training")
//        				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));

        tabHost.setCurrentTab(0);
	}

}
