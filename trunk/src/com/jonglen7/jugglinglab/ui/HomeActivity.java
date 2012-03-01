package com.jonglen7.jugglinglab.ui;

import greendroid.widget.ActionBarItem;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.jonglen7.jugglinglab.R;

public class HomeActivity extends BaseActivity {

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /** ActionBar. */
        setActionBarContentView(R.layout.activity_home);

        addActionBarItem(ActionBarItem.Type.Info, R.id.action_bar_view_info);
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