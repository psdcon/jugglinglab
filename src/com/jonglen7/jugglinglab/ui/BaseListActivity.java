package com.jonglen7.jugglinglab.ui;

import greendroid.app.GDListActivity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.jonglen7.jugglinglab.R;

// TODO Romain (BaseListActivity): Possibility to factorize with BaseActivity ?
public class BaseListActivity extends GDListActivity {

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
            	startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.menu_help:
            	startActivity(new Intent(this, HelpActivity.class));
            	break;
            case R.id.menu_about:
            	startActivity(new Intent(this, AboutActivity.class));
            	break;
        }
        return super.onOptionsItemSelected(item);
    }

}
