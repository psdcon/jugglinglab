package com.jonglen7.jugglinglab.ui;

import java.io.IOException;
import java.io.StringReader;

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
import com.jonglen7.jugglinglab.jugglinglab.generator.GeneratorTarget;
import com.jonglen7.jugglinglab.jugglinglab.generator.siteswapGenerator;
import com.jonglen7.jugglinglab.jugglinglab.jml.JMLParser;
import com.jonglen7.jugglinglab.jugglinglab.jml.JMLPattern;
import com.jonglen7.jugglinglab.jugglinglab.util.JuggleExceptionUser;

public class GeneratorListActivity extends ListActivity {
    
    /** GeneratorTarget. */
    GeneratorTarget target;
    
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
			sg.runGenerator(target);
		} catch (JuggleExceptionUser e) {
			e.printStackTrace();
		}
		
        Log.v("GeneratorListActivity", target.getPattern_list().toString());
        
        setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, target.getPattern_list()));
        ListView lv = getListView();
        lv.setTextFilterEnabled(true);
        lv.setOnItemClickListener(itemClickListener);
    }
    
    private OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			// When clicked, show a toast with the TextView text
			Toast.makeText(getApplicationContext(), ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
			
			// TODO That doesn't work, wait for the reply of the Juggling Lab guy
			/*JMLPattern pat = null;
			JMLParser p = new JMLParser();
            try {
    	    	Log.v("GeneratorListActivity", "Avant p.parse(new StringReader(((TextView) view).getText().toString()));");
				p.parse(new StringReader(((TextView) view).getText().toString()));
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
            try {
    	    	Log.v("GeneratorListActivity", "Avant pat = new JMLPattern(p.getTree());");
				pat = new JMLPattern(p.getTree());
			} catch (JuggleExceptionUser e) {
				e.printStackTrace();
			}
	    	Log.v("GeneratorListActivity", pat.toString());*/
		}
    	
    };
    
}
