package com.jonglen7.jugglinglab.ui;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

public class GeneratorListActivity extends BaseListActivity implements Runnable {
    
    /** Pattern list. */
	ProgressDialog dialog;
    ArrayList<PatternRecord> pattern_list;

    /** ListView. */
    ListAdapterTrick mSchedule;

    /** QuickAction. */
    QuickActionGridTrick quickActionGrid;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        dialog = ProgressDialog.show(this,
        		getResources().getString(R.string.please_wait),
        		getResources().getString(R.string.generator_list_generating_siteswaps),
        		true);
        Thread thread = new Thread(this);
        thread.start();
        
        /** QuickAction. */
        quickActionGrid = new QuickActionGridTrick(this);
    }

    public void run() {
    	pattern_list = new ArrayList<PatternRecord>();
    	
        /** Settings for the generator. */
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int max_patterns = Integer.parseInt(preferences.getString("generator_max_patterns", "100"));
        int max_seconds = Integer.parseInt(preferences.getString("generator_max_seconds", "3"));
        
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
            	String display = new Trick(pattern_record, this).getCUSTOM_DISPLAY();
            	if (display != "") pattern_record.setDisplay(display);
            	pattern_record.setDisplay(pattern_record.getDisplay().trim());
            }
        }
        
        handler.sendEmptyMessage(0);
    }
    
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            setTitle(getResources().getString(R.string.generator_list_title, pattern_list.size()));

            listView = getListView();
            mSchedule = new ListAdapterTrick(listView, pattern_list, GeneratorListActivity.this);
            
            listView.setOnItemClickListener(itemClickListener);
            listView.setOnItemLongClickListener(itemLongClickListener);
            listView.setAdapter(mSchedule);

        	dialog.dismiss();
        }
    };
    
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
			quickActionGrid.show(view, pattern_list.get(position));
			return true;
		}
	};
    
}
