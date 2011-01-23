package com.jonglen7.jugglinglab.ui;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jonglen7.jugglinglab.R;

public class TutorialsActivity extends ListActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorials);
        
        setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, tutorials));
        ListView lv = getListView();
        lv.setTextFilterEnabled(true);
    }
    
    private String[] tutorials = new String[] {
    		"Cascade 5",
    		"2 balls",
    		"3 balls",
    		"Chase",
    		"3 balls tricks",
    		"441",
    		"Arches"
    };

}
