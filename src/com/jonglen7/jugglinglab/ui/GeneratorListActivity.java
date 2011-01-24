package com.jonglen7.jugglinglab.ui;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.xml.sax.SAXException;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.jonglen7.jugglinglab.R;
import com.jonglen7.jugglinglab.jugglinglab.core.PatternRecord;
import com.jonglen7.jugglinglab.jugglinglab.generator.GeneratorTarget;
import com.jonglen7.jugglinglab.jugglinglab.generator.siteswapGenerator;
import com.jonglen7.jugglinglab.jugglinglab.jml.JMLParser;
import com.jonglen7.jugglinglab.jugglinglab.jml.JMLPattern;
import com.jonglen7.jugglinglab.jugglinglab.notation.Notation;
import com.jonglen7.jugglinglab.jugglinglab.util.JuggleExceptionInternal;
import com.jonglen7.jugglinglab.jugglinglab.util.JuggleExceptionUser;

public class GeneratorListActivity extends ListActivity {
    
    /** GeneratorTarget. */
    GeneratorTarget target;
    
    /** Pattern list. */
    ArrayList<PatternRecord> pattern_list;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generator_list);
        
        /** GeneratorTarget. */
        target = new GeneratorTarget();
        target.clearPatternList();
        
        /** Get pattern/ */
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
			// No limits
			// TODO This might take too long, find a way to prevent that
			//sg.runGenerator(target);
			sg.runGenerator(target, 100, 5.0);
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
			
			PatternRecord pattern = pattern_list.get(position);
			
			JMLPattern pat = null;
			
			if (pattern.getNotation().compareTo("siteswap") == 0) {
				try {
					Notation ssn = Notation.getNotation("siteswap");
					pat = ssn.getJMLPattern(pattern.getAnim());
				} catch (JuggleExceptionUser e) {
					e.printStackTrace();
				} catch (JuggleExceptionInternal e) {
					e.printStackTrace();
				}
			} if (pattern.getNotation().compareTo("jml") == 0) {
				JMLParser p = new JMLParser();
				
				try {
					p.parse(new StringReader(pattern.getAnim()));
				} catch (SAXException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

	            try {
					pat = new JMLPattern(p.getTree());
				} catch (JuggleExceptionUser e) {
					e.printStackTrace();
				}
			} else {
				Log.v("GeneratorListActivity", "WTF!? Neither siteswap or jml !");
			}
			
	    	Log.v("GeneratorListActivity", pat.toString());
		}
    	
    };
    
}
