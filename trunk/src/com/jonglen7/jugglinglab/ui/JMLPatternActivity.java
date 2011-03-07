package com.jonglen7.jugglinglab.ui;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.xml.sax.SAXException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.jonglen7.jugglinglab.R;
import com.jonglen7.jugglinglab.jugglinglab.core.PatternRecord;
import com.jonglen7.jugglinglab.jugglinglab.jml.JMLParser;
import com.jonglen7.jugglinglab.jugglinglab.jml.JMLPattern;
import com.jonglen7.jugglinglab.jugglinglab.notation.Notation;
import com.jonglen7.jugglinglab.jugglinglab.renderer.JugglingRenderer;
import com.jonglen7.jugglinglab.jugglinglab.util.JuggleExceptionInternal;
import com.jonglen7.jugglinglab.jugglinglab.util.JuggleExceptionUser;
import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.IntentAction;

/**
 * Generate a JMLPattern using a PatternRecord
 * @author Richard Romain
 *
 */

public class JMLPatternActivity extends Activity {
	
	JugglingRenderer renderer = null;
	PatternRecord pattern_record = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);
        
        // Get the PatternRecord.
        Bundle extras = getIntent().getExtras();
        if (extras== null){
        	Toast.makeText(getApplicationContext(), "ERROR",
                    Toast.LENGTH_SHORT).show();
        }
    	
        pattern_record = (PatternRecord) extras.getParcelable("pattern_record");
        
        // ActionBar
        final ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
        actionBar.setHomeAction(new IntentAction(this, createIntent(this), R.drawable.ic_title_home_default));
        actionBar.setTitle(pattern_record.getDisplay());
        ArrayList<PatternRecord> pattern_list = new ArrayList<PatternRecord>();
        pattern_list.add(pattern_record);
        actionBar.setOnClickListener(new QuickActionClickListener(pattern_list));

       
        // 2D Square
        /*
        GLSurfaceView view = new GLSurfaceView(this);
   		view.setRenderer(new OpenGLRenderer());
   		setContentView(view);
   		*/
        
   		// 3D Cube
        /*
        GLSurfaceView mGLSurfaceView;
        mGLSurfaceView = new TouchSurfaceView(this);
        setContentView(mGLSurfaceView);
        mGLSurfaceView.requestFocus();
        mGLSurfaceView.setFocusableInTouchMode(true);
        */
        
        // Fake Juggler and Fake Ball
   		/*
        GLSurfaceView view = new GLSurfaceView(this);
   		view.setRenderer(new JugglingRenderer());
   		setContentView(view);
   		*/
        
        // Initialize Renderer and View
    	renderer = new JugglingRenderer(this);
    	GLSurfaceView view = (GLSurfaceView) findViewById(R.id.surface);
        view.setRenderer(renderer);
        //setContentView(view);

    }
    
    

    /** ActionBar. */
    public static Intent createIntent(Context context) {
        Intent i = new Intent(context, HomeActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return i;
    }
    

    
    /** Compute JMLPattern from the PatternRecord **/
    public JMLPattern getJMLPattern() {
    	
    	JMLPattern pattern = null;
    	
		if (pattern_record.getNotation().compareTo("siteswap") == 0) {
			try {
				Notation ssn = Notation.getNotation("siteswap");
				pattern = ssn.getJMLPattern(pattern_record.getAnim());
			} catch (JuggleExceptionUser e) {
				e.printStackTrace();
			} catch (JuggleExceptionInternal e) {
				e.printStackTrace();
			}
		} else if (pattern_record.getNotation().compareTo("jml") == 0) {
			JMLParser p = new JMLParser();
			
			try {
				p.parse(new StringReader(pattern_record.getAnim()));
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

            try {
				pattern = new JMLPattern(p.getTree());
			} catch (JuggleExceptionUser e) {
				e.printStackTrace();
			}
		} else {
			Log.v("JMLPatternActivity", "WTF!? Neither siteswap or jml !");
		}
		
    	Log.v("JMLPatternActivity", pattern.toString());
    	
    	return pattern;
    }
    
    
    
	/** Called when the activity is resumed. */
    @Override
    public void onResume() {
    	super.onResume();
    	this.getPreferences();
    	
    }
    
    protected void getPreferences(){
    	SharedPreferences preferences = getSharedPreferences("com.jonglen7.jugglinglab_preferences", 0);
        
    	int newColor = preferences.getInt("SelectedColor", this.getResources().getInteger(R.color.prop_default_color));     
		
    	
    	String message = String.format("#%02x%02x%02x", Color.red(newColor),
				Color.green(newColor), Color.blue(newColor));
		
        Toast.makeText(getApplicationContext(), "newColor = " + message,
                Toast.LENGTH_SHORT).show();
        
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
            	startActivity(new Intent(this, SettingsAnimationActivity.class));
                break;
            case R.id.menu_about:
            	startActivity(new Intent(this, AboutActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    
}
