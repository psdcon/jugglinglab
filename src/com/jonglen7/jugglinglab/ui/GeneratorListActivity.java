package com.jonglen7.jugglinglab.ui;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.jonglen7.jugglinglab.R;
import com.jonglen7.jugglinglab.jugglinglab.core.PatternRecord;
import com.jonglen7.jugglinglab.jugglinglab.generator.GeneratorTarget;
import com.jonglen7.jugglinglab.jugglinglab.generator.siteswapGenerator;
import com.jonglen7.jugglinglab.jugglinglab.util.JuggleExceptionUser;

public class GeneratorListActivity extends ListActivity {
	
	/** Settings for the generator. */
	int max_patterns;
	int max_seconds;
    
    /** GeneratorTarget. */
    GeneratorTarget target;
    
    /** Pattern list. */
    ArrayList<PatternRecord> pattern_list;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generator_list);
        
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
		
		/** Pattern list */
		pattern_list = target.getPattern_list();
        ArrayList<String> pattern_list_display = new ArrayList<String>();
        for (int i=0; i<pattern_list.size(); i++) pattern_list_display.add(pattern_list.get(i).getDisplay());
        
        // TODO What to display if there are no results ?
        setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, pattern_list_display));
        ListView lv = getListView();
        lv.setTextFilterEnabled(true);
        lv.setOnItemClickListener(itemClickListener);
    }
    
    private OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Intent i = new Intent(GeneratorListActivity.this, JMLPatternActivity.class);
	        i.putExtra("pattern_record", pattern_list.get(position));
	        startActivity(i);
		}
    	
    };
    
}
