package com.jonglen7.jugglinglab.ui;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Toast;

import com.jonglen7.jugglinglab.R;
import com.jonglen7.jugglinglab.jugglinglab.core.PatternRecord;
import com.jonglen7.jugglinglab.jugglinglab.generator.GeneratorTarget;
import com.jonglen7.jugglinglab.jugglinglab.generator.siteswapGenerator;
import com.jonglen7.jugglinglab.jugglinglab.util.JuggleExceptionUser;
import com.jonglen7.jugglinglab.util.ListAdapterTrick;
import com.jonglen7.jugglinglab.util.Trick;

/**
 * The Generator List Activity that displays the results of the Generator Activity.
 */
public class GeneratorListActivity extends BaseListActivity {
    
    /** Pattern list. */
	ProgressDialog dialog;
    ArrayList<PatternRecord> pattern_list;

    /** ListView. */
    ListAdapterTrick mSchedule;

    /** QuickAction. */
    QuickActionGridTrick quickActionGrid;
    boolean show_delete = false;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        GenerateSiteswaps gs = new GenerateSiteswaps();
		gs.execute();
        
        /** QuickAction. */
        quickActionGrid = new QuickActionGridTrick(this);
    }
    
    /**
     * AsyncTask implementation based on the following tutorial:
     * http://www.tutos-android.com/asynctask-android-traitement-asynchrone-background
     */
	private class GenerateSiteswaps extends AsyncTask<Void, Void, Void>
	{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
	        dialog = ProgressDialog.show(GeneratorListActivity.this,
	        		getResources().getString(R.string.please_wait),
	        		getResources().getString(R.string.generator_list_generating_siteswaps),
	        		true);
		}
		
		@Override
		protected Void doInBackground(Void... arg0) {
	    	pattern_list = new ArrayList<PatternRecord>();
	    	
	        /** Settings for the generator. */
            int max_patterns = 100;
            int max_seconds = 3;

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(GeneratorListActivity.this);
	        String generator_max_patterns = preferences.getString("generator_max_patterns", "");
	        String generator_max_seconds = preferences.getString("generator_max_seconds", "");
	        if (!generator_max_patterns.isEmpty()) {
	            max_patterns = Integer.parseInt(generator_max_patterns);
	        }
            if (!generator_max_seconds.isEmpty()) {
                max_seconds = Integer.parseInt(generator_max_seconds);
            }
	        
	        /** GeneratorTarget. */
	        GeneratorTarget target = new GeneratorTarget();
	        target.clearPatternList();
	        
	        /** Get pattern. */
	        String pattern = null;
	        Bundle extras = getIntent().getExtras();
	        try {
	        	pattern = extras.getString("pattern");
	        } catch (Exception e) {
	        	Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_pattern), Toast.LENGTH_SHORT).show();
	        }
	            
	        if (pattern != null) {
	            /** Generate siteswap. */
	            siteswapGenerator sg = new siteswapGenerator();
	            try {
	    			sg.initGenerator(pattern);
	    			sg.runGenerator(target, max_patterns, max_seconds);
	    		} catch (JuggleExceptionUser e) {
	    			e.printStackTrace();
	    		}

	            pattern_list = target.getPattern_list();
	            
	            /** Get the names of the patterns (if there is one) */
	            for (PatternRecord pattern_record : pattern_list) {
	            	String display = new Trick(pattern_record, GeneratorListActivity.this).getCUSTOM_DISPLAY();
	            	if (display != "") pattern_record.setDisplay(display);
	            	pattern_record.setDisplay(pattern_record.getDisplay().trim());
	            }
	        }
	        
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			setTitle(getResources().getString(R.string.generator_list_title, pattern_list.size()));

            listView = getListView();
            mSchedule = new ListAdapterTrick(listView, pattern_list, GeneratorListActivity.this, show_delete);
            
            listView.setOnItemClickListener(itemClickListener);
            listView.setOnItemLongClickListener(itemLongClickListener);
            listView.setAdapter(mSchedule);

			dialog.dismiss();
		}
		
	}
    
    private OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Intent i = new Intent(GeneratorListActivity.this, AnimationActivity.class);
	        i.putExtra("pattern_record", pattern_list.get(position));
	        startActivity(i);
		}
    	
    };

    /** QuickAction. */
    private OnItemLongClickListener itemLongClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			quickActionGrid.show(view, pattern_list.get(position), show_delete);
			return true;
		}
	};
	
}
