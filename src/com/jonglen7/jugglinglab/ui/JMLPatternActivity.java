package com.jonglen7.jugglinglab.ui;

import java.io.IOException;
import java.io.StringReader;

import org.xml.sax.SAXException;

import android.app.Activity;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
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

/**
 * Generate a JMLPattern using a PatternRecord
 * @author Richard Romain
 *
 */

public class JMLPatternActivity extends Activity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        /** Get the PatternRecord. */
        Bundle extras = getIntent().getExtras();
        if (extras== null){
        	Toast.makeText(getApplicationContext(), "ERROR",
                    Toast.LENGTH_SHORT).show();
        }
    	
        PatternRecord pattern_record = (PatternRecord) extras.getParcelable("pattern_record");
        
        /** Generate the JMLPattern */
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
       
        // Black Screen
        //setContentView(R.layout.activity_video);
        
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
        

        // Juggler
        // Read JMLPAttern from JML file
        /*
        BufferedReader reader = null;
        try {
        	reader = new BufferedReader(new InputStreamReader(this.getAssets().open("3.jml")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Build the JMLPattern from it
		JMLPattern pattern = null;
		try {
			pattern = new JMLPattern(reader);
		} catch (JuggleExceptionUser e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/

    	// Juggler
    	// Pattern get from the PatternEntryActivity
    	JugglingRenderer renderer = new JugglingRenderer(pattern);
    	
        GLSurfaceView view = new GLSurfaceView(this);
        view.setRenderer(renderer);
        setContentView(view);
        
        

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
