package com.jonglen7.jugglinglab.ui;

import greendroid.app.GDApplication;
import android.content.Intent;
import android.net.Uri;

import com.jonglen7.jugglinglab.R;

public class HomeApplication extends GDApplication {

    @Override
    public Class<?> getHomeActivityClass() {
        return HomeActivity.class;
    }
    
    @Override
    public Intent getMainApplicationIntent() {
        return new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.app_url)));
    }

}
