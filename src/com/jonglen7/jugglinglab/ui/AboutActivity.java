package com.jonglen7.jugglinglab.ui;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.widget.TextView;

import com.jonglen7.jugglinglab.R;

/** The About Activity that displays information about the application. */
public class AboutActivity extends BaseActivity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarContentView(R.layout.activity_about);
        
        /// Version      
        TextView txt_version = (TextView) findViewById(R.id.about_version_number);
        PackageManager manager = this.getPackageManager();
        PackageInfo info;
		try {
			info = manager.getPackageInfo(this.getPackageName(), 0);
			txt_version.setText(info.versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
       
    }
}
