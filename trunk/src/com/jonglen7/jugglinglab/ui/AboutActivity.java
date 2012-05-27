package com.jonglen7.jugglinglab.ui;

import android.os.Bundle;

import com.jonglen7.jugglinglab.R;

/** The About Activity that displays information about the application. */
public class AboutActivity extends BaseActivity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarContentView(R.layout.activity_about);
    }
}
