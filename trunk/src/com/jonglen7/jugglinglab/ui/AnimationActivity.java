package com.jonglen7.jugglinglab.ui;

import greendroid.widget.ActionBarItem;

import java.io.IOException;
import java.io.StringReader;

import org.xml.sax.SAXException;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;
import android.widget.ZoomButton;

import com.jonglen7.jugglinglab.R;
import com.jonglen7.jugglinglab.jugglinglab.core.AnimatorPrefs;
import com.jonglen7.jugglinglab.jugglinglab.core.PatternRecord;
import com.jonglen7.jugglinglab.jugglinglab.jml.JMLParser;
import com.jonglen7.jugglinglab.jugglinglab.jml.JMLPattern;
import com.jonglen7.jugglinglab.jugglinglab.notation.Notation;
import com.jonglen7.jugglinglab.jugglinglab.renderer.JugglingRenderer;
import com.jonglen7.jugglinglab.jugglinglab.util.JuggleExceptionInternal;
import com.jonglen7.jugglinglab.jugglinglab.util.JuggleExceptionUser;
import com.jonglen7.jugglinglab.util.Trick;
import com.jonglen7.jugglinglab.widget.StarActionBarItem;
import com.jonglen7.jugglinglab.widget.TouchSurfaceView;

/** The Animation Activity that display the juggling animation. */
public class AnimationActivity extends BaseActivity {
	
	JugglingRenderer renderer = null;
	PatternRecord pattern_record = null;
	TouchSurfaceView mGLSurfaceView;
	
	/** ZoomButtons. */
    private ZoomButton mZoomIn;
    private ZoomButton mZoomOut;
	
    /** QuickAction. */
    QuickActionGridTrick quickActionGrid;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setActionBarContentView(R.layout.activity_animation);
        
        // Get the PatternRecord.
        Bundle extras = getIntent().getExtras();
        if (extras== null){
        	Toast.makeText(getApplicationContext(), "ERROR",
                    Toast.LENGTH_SHORT).show();
        }
    	
        pattern_record = (PatternRecord) extras.getParcelable("pattern_record");

        setTitle(pattern_record.getDisplay());
        
        getActionBar().setOnClickListener(clickListener);

        StarActionBarItem starItem = (StarActionBarItem) getActionBar()
        		.newActionBarItem(StarActionBarItem.class)
        		.setContentDescription(R.string.gd_star);
        starItem.setTrick(new Trick(pattern_record, this));
        
        addActionBarItem(starItem);
        addActionBarItem(ActionBarItem.Type.Compose, R.id.action_bar_compose);
        addActionBarItem(ActionBarItem.Type.Info, R.id.action_bar_view_info);
        
        /** QuickAction. */
        quickActionGrid = new QuickActionGridTrick(this);
        
        // TODO Fred: See http://android.cyrilmottier.com/?p=381 and
        // http://android.cyrilmottier.com/?p=450
        // That might be necessary because computation takes some time

        // 2D Square
        /*
        GLSurfaceView view = new GLSurfaceView(this);
   		view.setRenderer(new OpenGLRenderer());
   		setContentView(view);
   		*/
        
   		// 3D Cube
        /*
        TouchSurfaceView mGLSurfaceView;
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
//    	renderer = new JugglingRenderer(this);
        // TODO Fred: Do something if there is an exception to prevent a crash of JL
        try {
        	renderer = new JugglingRenderer(this, getJMLPattern(pattern_record));
        } catch (NullPointerException e) {
        	// Note de Romain: J'ai juste testé ça vite fait, ça a l'air d'empêcher
        	// le crash de l'appli donc c'est cool, mais bon c'est pas super propre car:
        	// 1/ finish(); return; => je ne sais même pas pourquoi on a besoin des 2
        	//    En cherchant sur le net seul finish(); devrait suffir, mais pas là
        	//    i.e. ça marche mais je ne sais pas pourquoi :s
        	// 2/ On carche des exceptions dans getJMLPattern(), c'est là que l'on
        	//    devrait gérer les erreurs en toute logique, mais pas réussi :(
        	// C'est plus un problème lié à Android qu'à l'animation, mais vu que
        	// tu vas surement reprendre à coder en étudiant ce problème, ça t'aidera
        	// à te remettre dans le bain :p
        	Toast.makeText(this, getString(R.string.invalid_pattern), Toast.LENGTH_LONG).show();
        	finish();
        	return;
        }
    	mGLSurfaceView = (TouchSurfaceView) findViewById(R.id.surface);
    	mGLSurfaceView.setRenderer(renderer);
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

    	/** ZoomButtons. */
        mZoomIn = (ZoomButton) findViewById(R.id.animation_btn_zoom_in);
        mZoomOut = (ZoomButton) findViewById(R.id.animation_btn_zoom_out);
        
        mZoomIn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mGLSurfaceView.zoomIn();
			}
		});
        
        mZoomOut.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mGLSurfaceView.zoomOut();
			}
		});
    }
    
//    /** Compute JMLPattern from the PatternRecord **/
//    public JMLPattern getJMLPattern() {
//    	
//    	JMLPattern pattern = null;
//    	
//		if (pattern_record.getNotation().compareTo("siteswap") == 0) {
//			try {
//				Notation ssn = Notation.getNotation("siteswap");
//				pattern = ssn.getJMLPattern(pattern_record.getAnim());
//			} catch (JuggleExceptionUser e) {
//				e.printStackTrace();
//			} catch (JuggleExceptionInternal e) {
//				e.printStackTrace();
//			}
//		} else if (pattern_record.getNotation().compareTo("jml") == 0) {
//			JMLParser p = new JMLParser();
//			
//			try {
//				p.parse(new StringReader(pattern_record.getAnim()));
//			} catch (SAXException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//
//            try {
//				pattern = new JMLPattern(p.getTree());
//			} catch (JuggleExceptionUser e) {
//				e.printStackTrace();
//			}
//		} else {
//			Log.v("JMLPatternActivity", "WTF!? Neither siteswap or jml !");
//		}
//    	
//    	return pattern;
//    }
    
    /** Compute JMLPattern from the PatternRecord **/
    private JMLPattern getJMLPattern(PatternRecord pattern_record) {
    	
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
    	
    	return pattern;
    }
    
	/** Called when the activity is paused. */
    @Override
    public void onPause() {
    	super.onPause();
        mGLSurfaceView.onPause();
    }
    
	/** Called when the activity is resumed. */
    @Override
    public void onResume() {
    	super.onResume();
        mGLSurfaceView.onResume();
    }

    /** ActionBar. */
    @Override
    public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
    	Intent intent;
        switch (item.getItemId()) {
            case R.id.action_bar_compose:
            	intent = new Intent(this, PatternEntryActivity.class);
            	intent.putExtra("pattern_record", pattern_record);
                startActivity(intent);
                return true;

            case R.id.action_bar_view_info:
            	intent = new Intent(this, PatternInfoActivity.class);
            	intent.putExtra("pattern_record", pattern_record);
                startActivity(intent);
                return true;

            default:
                return super.onHandleActionBarItemClick(item, position);
        }
    }

	/** QuickAction. */
	private OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			quickActionGrid.show(view, pattern_record);
		}
	};
    
}
