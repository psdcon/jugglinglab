package com.jonglen7.jugglinglab.ui;

import greendroid.app.GDActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.jonglen7.jugglinglab.R;

public class VideoActivity  extends GDActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_video);
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

}
