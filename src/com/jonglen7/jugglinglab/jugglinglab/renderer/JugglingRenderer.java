package com.jonglen7.jugglinglab.jugglinglab.renderer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLU;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

import com.jonglen7.jugglinglab.jugglinglab.core.AnimatorPrefs;
import com.jonglen7.jugglinglab.jugglinglab.jml.HandLink;
import com.jonglen7.jugglinglab.jugglinglab.jml.JMLPattern;
import com.jonglen7.jugglinglab.jugglinglab.prop.Prop;
import com.jonglen7.jugglinglab.jugglinglab.util.Coordinate;
import com.jonglen7.jugglinglab.jugglinglab.util.JLMath;
import com.jonglen7.jugglinglab.jugglinglab.util.JuggleExceptionInternal;
import com.jonglen7.jugglinglab.jugglinglab.util.JuggleExceptionUser;
import com.jonglen7.jugglinglab.jugglinglab.util.Permutation;

public class JugglingRenderer implements Renderer {
	
	
	// Attributes
	private JMLPattern pattern;
	private Juggler juggler;
	private AnimatorPrefs prefs;
	
	private Coordinate		overallmax = null;
    private Coordinate		overallmin = null;
    
    private Coordinate tempc = null;
	
	private double sim_interval_secs = 0.0;
	private double time = 0.0;
	
	private int[]				animpropnum = null, temppropnum = null;
	private Permutation		invpathperm = null;
	private int				num_frames;
	private double			sim_time;
	private long				real_interval_millis;

	private static final double snapangle = JLMath.toRad(15.0);
    
	private Coordinate cameraCenter;
	
	
	
	
	
	
	// Constructors
	public JugglingRenderer(JMLPattern pattern) {
		
		// Initialize class attributes
		this.pattern = pattern;
		this.prefs = new AnimatorPrefs();
		this.cameraCenter = new Coordinate();
		this.tempc = new Coordinate();
		this.juggler = new Juggler(1);  // TODO: Implements for multiple jugglers
		//juggler = new FakeJuggler();
		
		try {
			pattern.layoutPattern();
		} catch (JuggleExceptionInternal e) {
			e.printStackTrace();
		} catch (JuggleExceptionUser e) {
			e.printStackTrace();
		}
		
        syncToPattern();
        setCameraCoordinate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.opengl.GLSurfaceView.Renderer#onSurfaceCreated(javax.microedition
	 * .khronos.opengles.GL10, javax.microedition.khronos.egl.EGLConfig)
	 */
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// Set the background color ( rgba ).
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
		// Enable Smooth Shading, default not really needed.
		gl.glShadeModel(GL10.GL_SMOOTH);
		// Depth buffer setup.
		gl.glClearDepthf(1.0f);
		// Enables depth testing.
		gl.glEnable(GL10.GL_DEPTH_TEST);
		// The type of depth testing to do.
		gl.glDepthFunc(GL10.GL_LEQUAL);
		// Really nice perspective calculations.
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.opengl.GLSurfaceView.Renderer#onDrawFrame(javax.microedition.
	 * khronos.opengles.GL10)
	 */
	public void onDrawFrame(GL10 gl) {

		
		// Clears the screen and depth buffer.
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		// Replace the current matrix with the identity matrix
		gl.glLoadIdentity();
		// Save the current matrix.
		gl.glPushMatrix();
		// Translates 10 units into the screen.
		gl.glTranslatef(0, -150.0f, -100.0f); 
		//gl.glTranslatef((float)-this.cameraCenter.z, (float)-this.cameraCenter.y, -100.0f); 
		// Reduce
		//gl.glScalef(0.2f, 0.2f, 0.2f);
		// Draw the Frame
		drawEffectiveFrame(gl);
		
		// Restore the last matrix.
		gl.glPopMatrix();
		
		// Time for an animation
        time = (time + sim_interval_secs) % pattern.getLoopEndTime() ;
        
        
        
		/*
		// Fake Juggler + Fake Ball
		
		// Clears the screen and depth buffer.
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		// Replace the current matrix with the identity matrix
		gl.glLoadIdentity();
		// Save the current matrix.
		gl.glPushMatrix();
		// Translates 10 units into the screen.
		gl.glTranslatef(0.0f, -100.0f, -100.0f); 
		//int x=11, y=30, z=139;
		juggler.draw(gl);
		int x=11, y=30, z=139;
		Prop pr = new ballProp();
		pr.setPropCenter(new Coordinate(x, y, z));
        pr.centerProp();
        pr.draw(gl);
		// Restore the last matrix.
		gl.glPopMatrix();
        */
        
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.opengl.GLSurfaceView.Renderer#onSurfaceChanged(javax.microedition
	 * .khronos.opengles.GL10, int, int)
	 */
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// Sets the current view port to the new size.
		gl.glViewport(0, 0, width, height);
		// Select the projection matrix
		gl.glMatrixMode(GL10.GL_PROJECTION);
		// Reset the projection matrix
		gl.glLoadIdentity();
		// Calculate the aspect ratio of the window
		GLU.gluPerspective(gl, 90.0f, (float) width / (float) height, 0.1f,
				100.0f);
		// Select the modelview matrix
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		// Reset the modelview matrix
		gl.glLoadIdentity();
	}


	
	private void setPattern(JMLPattern pattern) {
		this.pattern = pattern;
		int num_frames = (int)(0.5 + (pattern.getLoopEndTime() - pattern.getLoopStartTime()) * prefs.slowdown * prefs.fps);
		sim_interval_secs = (pattern.getLoopEndTime()-pattern.getLoopStartTime()) / num_frames;

	}
	
	/*
	 *  This function draw the effective frame (jugglers + props)
	 */
	private void drawEffectiveFrame(GL10 gl) {
		try {
			
			// Draw the Jugglers 
			for (int j = 1; j <= pattern.getNumberOfJugglers(); j++) {
				juggler.findJugglerCoordinates(this.pattern, time);
				juggler.MathVectorToVertices();
				juggler.draw(gl);
			}

			// Draw the props
			float x=0.0f, y=0.0f, z=0.0f;
			for (int i = 1; i <= pattern.getNumberOfPaths(); i++) {
	            pattern.getPathCoordinate(i, time, tempc);
	            if (!tempc.isValid())
	                tempc.setCoordinate(0.0,0.0,0.0);
	            x = (float)(0.5f + tempc.x);
	            y = (float)(0.5f + tempc.y);
	            z = (float)(0.5f + tempc.z);
	            Prop pr = pattern.getProp(animpropnum[i-1]);
	            pr.setPropCenter(tempc);
	            pr.centerProp();
	            pr.draw(gl); 
	       }	
			
		} catch (JuggleExceptionInternal e){
			e.printStackTrace();
		}
	}
	
	

    protected void advanceProps(int[] pnum) {
        for (int i = 0; i < pattern.getNumberOfPaths(); i++)
            temppropnum[invpathperm.getMapping(i+1)-1] = pnum[i];
        for (int i = 0; i < pattern.getNumberOfPaths(); i++)
            pnum[i] = temppropnum[i];
    }

    public void syncToPattern() {
        findMaxMin();
        syncRenderer();

        // figure out timing constants; adjust fps to get integer number of frames in loop
        num_frames = (int)(0.5 + (pattern.getLoopEndTime() - pattern.getLoopStartTime()) * prefs.slowdown * prefs.fps);
        sim_interval_secs = (pattern.getLoopEndTime()-pattern.getLoopStartTime()) / num_frames;
        real_interval_millis = (long)(1000.0 * sim_interval_secs * prefs.slowdown);

        //  time = (time + sim_interval_secs) % pattern.getLoopEndTime() ;
        
        animpropnum = new int[pattern.getNumberOfPaths()];
        for (int i = 1; i <= pattern.getNumberOfPaths(); i++)
            animpropnum[i-1] = pattern.getPropAssignment(i);
        temppropnum = new int[pattern.getNumberOfPaths()];
        invpathperm = pattern.getPathPermutation().getInverse();
    }

    protected void findMaxMin() {
        // the algorithm here could be improved to take into account which props are
        // on which paths.  We may also want to leave room for the rest of the juggler.
        int i;
        Coordinate patternmax = null, patternmin = null;
        Coordinate handmax = null, handmin = null;
        Coordinate propmax = null, propmin = null;

        for (i = 1; i <= pattern.getNumberOfPaths(); i++) {
            patternmax = Coordinate.max(patternmax, pattern.getPathMax(i));
            patternmin = Coordinate.min(patternmin, pattern.getPathMin(i));
        }

        // make sure all hands are visible
        for (i = 1; i <= pattern.getNumberOfJugglers(); i++) {
            handmax = Coordinate.max(handmax, pattern.getHandMax(i, HandLink.LEFT_HAND));
            handmin = Coordinate.min(handmin, pattern.getHandMin(i, HandLink.LEFT_HAND));
            handmax = Coordinate.max(handmax, pattern.getHandMax(i, HandLink.RIGHT_HAND));
            handmin = Coordinate.min(handmin, pattern.getHandMin(i, HandLink.RIGHT_HAND));
        }

        for (i = 1; i <= pattern.getNumberOfProps(); i++) {
            propmax = Coordinate.max(propmax, pattern.getProp(i).getMax());
            propmin = Coordinate.min(propmin, pattern.getProp(i).getMin());
        }

        // make sure props are entirely visible along all paths
        patternmax = Coordinate.add(patternmax, propmax);
        patternmin = Coordinate.add(patternmin, propmin);

        // make sure hands are entirely visible
        handmax = Coordinate.add(handmax, getHandWindowMax());
        handmin = Coordinate.add(handmin, getHandWindowMin());

        // make sure jugglers' bodies are visible
        this.overallmax = Coordinate.max(handmax, getJugglerWindowMax());
        this.overallmax = Coordinate.max(overallmax, patternmax);

        this.overallmin = Coordinate.min(handmin, getJugglerWindowMin());
        this.overallmin = Coordinate.min(overallmin, patternmin);

        if (com.jonglen7.jugglinglab.jugglinglab.core.Constants.DEBUG_LAYOUT) {
            System.out.println("Hand max = " + handmax);
            System.out.println("Hand min = " + handmin);
            System.out.println("Prop max = " + propmax);
            System.out.println("Prop min = " + propmin);
            System.out.println("Pattern max = " + patternmax);
            System.out.println("Pattern min = " + patternmin);
            System.out.println("Overall max = " + this.overallmax);
            System.out.println("Overall min = " + this.overallmin);

            this.overallmax = new Coordinate(100.0,0.0,500.0);
            this.overallmin = new Coordinate(-100.0,0.0,-100.0);
        }
    }

    
    protected void syncRenderer() {
    /*
        Dimension d = this.getSize();
        this.renderer.initDisplay(d, prefs.border, this.overallmax, this.overallmin);
    */
    }


    public Coordinate getHandWindowMax() {
        return new Coordinate(Juggler.hand_out, 0, 1);
    }

    public Coordinate getHandWindowMin() {
        return new Coordinate(-Juggler.hand_in, 0, -1);
    }

    public Coordinate getJugglerWindowMax() {
        Coordinate max = pattern.getJugglerMax(1);
        for (int i = 2; i <= pattern.getNumberOfJugglers(); i++)
            max = Coordinate.max(max, pattern.getJugglerMax(i));

        max = Coordinate.add(max, new Coordinate(Juggler.shoulder_hw, Juggler.shoulder_hw,  // Juggler.head_hw,
                                                 Juggler.shoulder_h + Juggler.neck_h + Juggler.head_h));
        return max;
        // return new Coordinate(Math.max(max.x, max.y), Math.max(max.x, max.y), max.z);
    }
    
    public Coordinate getJugglerWindowMin() {
        Coordinate min = pattern.getJugglerMin(1);
        for (int i = 2; i <= pattern.getNumberOfJugglers(); i++)
            min = Coordinate.min(min, pattern.getJugglerMin(i));

        min = Coordinate.add(min, new Coordinate(-Juggler.shoulder_hw, -Juggler.shoulder_hw, // -Juggler.head_hw,
                                                 Juggler.shoulder_h));
        return min;
        // return new Coordinate(Math.min(min.x, min.y), Math.min(min.x, min.y), min.z);
    }
    
    public double getTime() { return sim_time; };

    public void setTime(double time) {
        /*		while (time < pat.getLoopStartTime())
        time += (pat.getLoopEndTime() - pat.getLoopStartTime());
        while (time > pat.getLoopEndTime())
        time -= (pat.getLoopEndTime() - pat.getLoopStartTime());
        */
        sim_time = time;
    }
    
    private void setCameraCoordinate()
    {
    	double x = (double)(0.5*(overallmax.x+overallmin.x));
    	double y = (double)(0.5*(overallmax.z+overallmin.z));
    	double z = (double)(0.5*(overallmax.y+overallmin.y));
    	cameraCenter.setCoordinate(x, y, z);

    	
    	Log.v("JugglingRenderer","Camera Center\tX=" + this.cameraCenter.x + "\tY=" + this.cameraCenter.y + "\tZ=" + this.cameraCenter.z);
    }
    

}