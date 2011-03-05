package com.jonglen7.jugglinglab.ui;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.jonglen7.jugglinglab.R;
import com.jonglen7.jugglinglab.jugglinglab.core.PatternRecord;
import com.jonglen7.jugglinglab.jugglinglab.generator.GeneratorTarget;
import com.jonglen7.jugglinglab.jugglinglab.generator.siteswapGenerator;
import com.jonglen7.jugglinglab.jugglinglab.util.JuggleExceptionUser;
import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.IntentAction;

public class GeneratorListActivity extends ListActivity {
	
	/** Settings for the generator. */
	int max_patterns;
	int max_seconds;
    
    /** GeneratorTarget. */
    GeneratorTarget target;
    
    /** Pattern list. */
    ArrayList<PatternRecord> pattern_list;
    //ArrayList<String> pattern_list_display;
    
    /** ListView */
    ListView listView;
    
    /** The ArrayList that will populate the ListView. */
    ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generator_list);
        
        createPatternList();
        
        /*setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, pattern_list_display));
        ListView lv = getListView();
        lv.setTextFilterEnabled(true);
        lv.setOnItemClickListener(itemClickListener);
        lv.setOnItemLongClickListener(new QuickActionClickListener(pattern_list));*/
        
        listView = getListView();
        listView.setOnItemClickListener(itemClickListener);
        listView.setOnItemLongClickListener(new QuickActionClickListener(pattern_list));
        
        SimpleAdapter mSchedule = new SimpleAdapter (this.getBaseContext(), listItem, R.layout.list_item,
               new String[] {"list_item_text", "list_item_fav"}, new int[] {R.id.list_item_text, R.id.list_item_fav});

        listView.setAdapter(mSchedule);
        
        /** ActionBar. */
        final ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
        actionBar.setHomeAction(new IntentAction(this, createIntent(this), R.drawable.ic_title_home_default));
        actionBar.setTitle(pattern_list.size() + " patterns found");
    }
    
    private void createPatternList() {
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
        //pattern_list_display = new ArrayList<String>();
		HashMap<String, String> map;
        for (int i=0; i<pattern_list.size(); i++) {
        	//pattern_list_display.add(pattern_list.get(i).getDisplay());
        	map = new HashMap<String, String>();
        	map.put("list_item_text", pattern_list.get(i).getDisplay());
        	map.put("list_item_fav", String.valueOf(R.drawable.fav));
        	listItem.add(map);
        }
    }

    /** ActionBar. */
    public static Intent createIntent(Context context) {
        Intent i = new Intent(context, HomeActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return i;
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
            case R.id.menu_about:
            	startActivity(new Intent(this, AboutActivity.class));
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
    
}
