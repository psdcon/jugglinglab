package com.jonglen7.jugglinglab.ui;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.xml.sax.SAXException;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import com.jonglen7.jugglinglab.jugglinglab.jml.JMLPattern;
import com.jonglen7.jugglinglab.jugglinglab.renderer.JugglingRenderer;
import com.jonglen7.jugglinglab.jugglinglab.renderer.OpenGLRenderer;
import com.jonglen7.jugglinglab.jugglinglab.renderer.TouchSurfaceView;
import com.jonglen7.jugglinglab.jugglinglab.util.JuggleExceptionInternal;
import com.jonglen7.jugglinglab.jugglinglab.util.JuggleExceptionUser;

public class VideoActivity  extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
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
        
        // Fake Juggler  
   		/*
        GLSurfaceView view = new GLSurfaceView(this);
   		view.setRenderer(new JugglingRenderer());
   		setContentView(view);
   		*/
        
        
        
        
        
        // **********************************************************
        // Juggler
        // **********************************************************
        
        // Read the JML file
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
		
		// pattern.layoutPattern()
		try {
			pattern.layoutPattern();
		} catch (JuggleExceptionInternal e) {
			e.printStackTrace();
		} catch (JuggleExceptionUser e) {
			e.printStackTrace();
		}
		
		
		
		
		JugglingRenderer jd = new JugglingRenderer();
		jd.setPattern(pattern);

   
        GLSurfaceView view = new GLSurfaceView(this);
        view.setRenderer(jd);
        setContentView(view);
        
    }
    
    

}
