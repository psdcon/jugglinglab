package com.jonglen7.jugglinglab.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.jonglen7.jugglinglab.R;

public class HomeActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }
    
    /** Handle "Pattern Entry" action. */
    public void onPatternEntryClick(View v) {
        startActivity(new Intent(this, PatternEntryActivity.class));
    }
    
    /** Handle "Generator" action. */
    public void onGeneratorClick(View v) {
        startActivity(new Intent(HomeActivity.this, GeneratorActivity.class));
    }
    
    /** Handle "Tutorials" action. */
    public void onTutorialsClick(View v) {
        startActivity(new Intent(this, TutorialsActivity.class));
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