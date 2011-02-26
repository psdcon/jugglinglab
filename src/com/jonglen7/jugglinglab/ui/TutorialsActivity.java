package com.jonglen7.jugglinglab.ui;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jonglen7.jugglinglab.R;
import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.IntentAction;

public class TutorialsActivity extends ListActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorials);
        
        /** ActionBar. */
        final ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
        actionBar.setHomeAction(new IntentAction(this, createIntent(this), R.drawable.ic_title_home_default));
        
        setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, tutorials));
        ListView lv = getListView();
        lv.setTextFilterEnabled(true);
    }

    /** ActionBar. */
    public static Intent createIntent(Context context) {
        Intent i = new Intent(context, HomeActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return i;
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
