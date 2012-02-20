package com.jonglen7.jugglinglab.ui;

import greendroid.app.GDActivity;
import android.os.Bundle;

import com.jonglen7.jugglinglab.R;
import com.jonglen7.jugglinglab.jugglinglab.core.PatternRecord;

public class PatternInfoActivity extends GDActivity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern_info);
    	
        PatternRecord pattern_record = (PatternRecord) getIntent().getExtras().getParcelable("pattern_record");

        setTitle(pattern_record.getDisplay());

        // TODO Romain (PatternInfo): Fill out information, add the possibility to add/delete from the DB the hand/body movements
    
    }

}
