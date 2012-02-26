package com.jonglen7.jugglinglab.ui;

import greendroid.app.GDListActivity;

import java.util.ArrayList;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.jonglen7.jugglinglab.R;
import com.jonglen7.jugglinglab.jugglinglab.core.PatternRecord;
import com.jonglen7.jugglinglab.jugglinglab.generator.GeneratorTarget;
import com.jonglen7.jugglinglab.jugglinglab.generator.siteswapGenerator;
import com.jonglen7.jugglinglab.jugglinglab.util.JuggleExceptionUser;
import com.jonglen7.jugglinglab.util.DataBaseHelper;
import com.jonglen7.jugglinglab.util.ListAdapterTrick;
import com.jonglen7.jugglinglab.util.Trick;

public class GeneratorListActivity extends GDListActivity {
	
	/** Settings for the generator. */
	int max_patterns;
	int max_seconds;
    
    /** GeneratorTarget. */
    GeneratorTarget target;

	/** DataBase. */
	DataBaseHelper myDbHelper;
    
    /** Pattern list. */
    ArrayList<PatternRecord> pattern_list;

    /** ListView. */
    ListView listView;

    /** QuickAction. */
    QuickActionGridTrick quickActionGrid;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);

        myDbHelper = DataBaseHelper.init(this);
        
        /** The ArrayList that will populate the ListView. */
        pattern_list = createPatternList();
        
        setTitle(pattern_list.size() + " patterns found");

        listView = getListView();
        ListAdapterTrick mSchedule = new ListAdapterTrick(listView, getLayoutInflater(), pattern_list, this, getIntent(), GeneratorListActivity.this);
        
        listView.setOnItemClickListener(itemClickListener);
        listView.setOnItemLongClickListener(itemLongClickListener);
        listView.setAdapter(mSchedule);

        myDbHelper.close();
        
        /** QuickAction. */
        quickActionGrid = new QuickActionGridTrick(this);
    }

    private ArrayList<PatternRecord> createPatternList() {
        /** Settings for the generator. */
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        max_patterns = Integer.parseInt(preferences.getString("generator_max_patterns", "100"));
        max_seconds = Integer.parseInt(preferences.getString("generator_max_seconds", "3"));
        
        /** GeneratorTarget. */
        target = new GeneratorTarget();
        target.clearPatternList();
        
        /** Get pattern. */
        Bundle extras = getIntent().getExtras();
        if (extras== null){
        	Toast.makeText(getApplicationContext(), "ERROR",
                    Toast.LENGTH_SHORT).show();
        }
        String pattern = extras.getString("pattern");
        
        /** Generate siteswap. */
        siteswapGenerator sg = new siteswapGenerator();
        try {
			sg.initGenerator(pattern);
			sg.runGenerator(target, max_patterns, max_seconds);
		} catch (JuggleExceptionUser e) {
			e.printStackTrace();
		}

        ArrayList<PatternRecord> pattern_list = target.getPattern_list();
        
        /** Get the names of the patterns (if there is one) */
        for (PatternRecord pattern_record : pattern_list) {
        	String display = new Trick(pattern_record, this).getCUSTOM_DISPLAY();
        	if (display != "") pattern_record.setDisplay(display);
        	pattern_record.setDisplay(pattern_record.getDisplay().trim());
        }
        
        return pattern_list;
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
    
    private OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Intent i = new Intent(GeneratorListActivity.this, JMLPatternActivity.class);
	        i.putExtra("pattern_record", pattern_list.get(position));
	        startActivity(i);
		}
    	
    };

    /** QuickAction. */
    private OnItemLongClickListener itemLongClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			quickActionGrid.show(view, pattern_list.get(position), getIntent(), GeneratorListActivity.this);
			return true;
		}
	};
    
}
