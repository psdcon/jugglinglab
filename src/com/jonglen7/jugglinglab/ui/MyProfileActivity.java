package com.jonglen7.jugglinglab.ui;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

import com.jonglen7.jugglinglab.R;
import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.IntentAction;

public class MyProfileActivity extends TabActivity {
	
	private TabHost tabHost;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        
        /** ActionBar. */
        final ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
        actionBar.setHomeAction(new IntentAction(this, createIntent(this), R.drawable.ic_title_home_default));
        
        buildTabs();
    }

    /** ActionBar. */
    public static Intent createIntent(Context context) {
        Intent i = new Intent(context, HomeActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return i;
    }
    
	private void buildTabs(){
        tabHost = getTabHost();

        tabHost.addTab(tabHost.newTabSpec("tab_test1").setIndicator(getString(R.string.my_profile_tab_favs)).setContent(R.id.my_profile_content));
        tabHost.addTab(tabHost.newTabSpec("tab_test2").setIndicator(getString(R.string.my_profile_tab_practicing)).setContent(R.id.my_profile_content));
        tabHost.addTab(tabHost.newTabSpec("tab_test3").setIndicator(getString(R.string.my_profile_tab_records)).setContent(R.id.my_profile_content));

        tabHost.setCurrentTab(0);
	}

}
