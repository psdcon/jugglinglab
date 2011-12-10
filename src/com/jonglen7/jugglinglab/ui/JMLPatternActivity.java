package com.jonglen7.jugglinglab.ui;

import greendroid.app.GDActivity;

import java.io.IOException;
import java.io.StringReader;

import org.xml.sax.SAXException;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import com.jonglen7.jugglinglab.R;
import com.jonglen7.jugglinglab.jugglinglab.core.AnimatorPrefs;
import com.jonglen7.jugglinglab.jugglinglab.core.PatternRecord;
import com.jonglen7.jugglinglab.jugglinglab.jml.JMLParser;
import com.jonglen7.jugglinglab.jugglinglab.jml.JMLPattern;
import com.jonglen7.jugglinglab.jugglinglab.notation.Notation;
import com.jonglen7.jugglinglab.jugglinglab.renderer.JugglingRenderer;
import com.jonglen7.jugglinglab.jugglinglab.util.JuggleExceptionInternal;
import com.jonglen7.jugglinglab.jugglinglab.util.JuggleExceptionUser;

/**
 * Generate a JMLPattern using a PatternRecord
 * @author Richard Romain
 *
 */

public class JMLPatternActivity extends GDActivity {
	
	JugglingRenderer renderer = null;
	PatternRecord pattern_record = null;
	boolean isOnPause = false;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);
        
        // TODO Fred: See http://android.cyrilmottier.com/?p=381 and
        // http://android.cyrilmottier.com/?p=450
        // That might be necessary because computation takes some time
        
        // Get the PatternRecord.
        Bundle extras = getIntent().getExtras();
        if (extras== null){
        	Toast.makeText(getApplicationContext(), "ERROR",
                    Toast.LENGTH_SHORT).show();
        }
    	
        pattern_record = (PatternRecord) extras.getParcelable("pattern_record");

        setTitle(pattern_record.getDisplay());

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
        
        // Assign Speed SeekBar Listener
        SeekBar speedSeekbar = (SeekBar) findViewById(R.id.animation_speed_seekbar);
        speedSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
 
            public void onProgressChanged(SeekBar seekBar, int progress,
            boolean fromTouch) {

            	AnimatorPrefs prefs = renderer.getPrefs();
            	prefs.slowdown = 20 - (double)progress;
            	renderer.setPrefs(prefs);
            	renderer.syncToPattern();
           
            }
            public void onStartTrackingTouch(SeekBar seekBar) {
                //ignorer
            }
            public void onStopTrackingTouch(SeekBar seekBar) {
                //ignorer
            }
        });
        
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
    
    // TODO Fred: J'ai fais ça vite fait pour pauser/reprendre l'anim, il y a peut-être (surement) mieux
    public void pause(View view) {
    	if (isOnPause) {
    		((GLSurfaceView) view).onResume();
    	} else {
    		((GLSurfaceView) view).onPause();
    		
    	}
    	isOnPause = !isOnPause;
    }
    
	/** Called when the activity is paused. */
    @Override
    public void onPause() {
    	super.onPause();
    }
    
	/** Called when the activity is resumed. */
    @Override
    public void onResume() {
    	super.onResume();
    }
    
    /** Menu button. */
    // TODO Fred: Uncomment when ready
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.menu_settings:
//            	startActivity(new Intent(this, SettingsAnimationActivity.class));
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }
   
}