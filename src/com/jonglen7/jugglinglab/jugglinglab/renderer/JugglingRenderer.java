package com.jonglen7.jugglinglab.jugglinglab.renderer;

import java.io.Serializable;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.SharedPreferences;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;

import com.jonglen7.jugglinglab.R;
import com.jonglen7.jugglinglab.jugglinglab.core.AnimatorPrefs;
import com.jonglen7.jugglinglab.jugglinglab.jml.HandLink;
import com.jonglen7.jugglinglab.jugglinglab.jml.JMLPattern;
import com.jonglen7.jugglinglab.jugglinglab.prop.Prop;
import com.jonglen7.jugglinglab.jugglinglab.util.Coordinate;
import com.jonglen7.jugglinglab.jugglinglab.util.JLMath;
import com.jonglen7.jugglinglab.jugglinglab.util.JuggleExceptionInternal;
import com.jonglen7.jugglinglab.jugglinglab.util.JuggleExceptionUser;
import com.jonglen7.jugglinglab.jugglinglab.util.Permutation;
import com.jonglen7.jugglinglab.util.ColorConverter;

public class JugglingRenderer implements Renderer, Serializable {
	
	// Attributes
	private static final long serialVersionUID = 1L;
	
	private Context context = null;
	private JMLPattern pattern = null;
	private Floor floor = null;
	private Juggler juggler = null;
	private SharedPreferences preferences = null;
	private AnimatorPrefs prefs = null;
	
	private Coordinate overallmax = null;
    private Coordinate overallmin = null;
    private Coordinate tempc = null;
	private Coordinate cameraCenter;
	
	private double sim_interval_secs = 0.0;
	private double time = 0.0;
	
	private int[]				animpropnum = null, temppropnum = null;
	private Permutation		invpathperm = null;
	private int				num_frames;
	private long				real_interval_millis;

	private static final double snapangle = JLMath.toRad(15.0);
    

	
	double boundingBoxeMaxSize;
	double roiHalfHeight;
	double depthValue;
	double FOVY = 45;
	double Z;
	float zNear;
	float zFar;
	float top;
	float bottom;
	float right;
	float left;
	
	
	// Romain: Added for rotation
    public float mAngleX;
    public float mAngleY;
    public float[] mAccumulatedRotation = new float[16]; /** Store the accumulated rotation. */
    private float[] mCurrentRotation = new float[16];    	/** Store the current rotation. */
    private float[] mTemporaryMatrix = new float[16];    	/** Store a temporary matrix. */
    private float[] mModelMatrix = new float[16];			/** Store the model matrix */
    
    boolean updateView = false;
//    private final float ANGLE_EPSILON = 2.5f;
//    private final float ANGLE_MAX = 360;
//    private final float[] ANGLE_STICKY = new float[] {0 * ANGLE_MAX / 4,
//                                                      1 * ANGLE_MAX / 4,
//                                                      2 * ANGLE_MAX / 4,
//                                                      3 * ANGLE_MAX / 4,
//                                                      4 * ANGLE_MAX / 4};
	
    // Romain: Added for zoom
    public float mZoom;
    public final float ZOOM_MIN = 0.0f;
    public final float ZOOM_STEP = 0.2f;
    // Quick hack to have a better initial zoom when only one juggler
    public final float ZOOM_INIT_1 = 1.6f;
    public final float ZOOM_INIT_2 = 0.8f;
	
	// Romain: Added for translation
    public float mTranslateX;
    public float mTranslateY;
    
    // Fred: Added for manipulation of scene when animation is paused
    public boolean freeze = false;
    public double freeze_time_begin = 0.0f;

	private float aspect;
   	
    
	// Constructors
	// Parameter context is used to acces JMLPattern and SharedPreferences
	public JugglingRenderer(Context context, JMLPattern pattern) {
		
		// Initialize class attributes
		this.context = context;
		//this.pattern = ((JMLPatternActivity)context).getJMLPattern();
		this.pattern = pattern;
		this.preferences = context.getSharedPreferences("com.jonglen7.jugglinglab_preferences", 0);
		this.prefs = new AnimatorPrefs();
		this.cameraCenter = new Coordinate();
		this.overallmin = new Coordinate(-40.0f, 70.0f, -30.0f);
		this.overallmax= new Coordinate(40.0f, 175.0f, 35.0f);
		this.tempc = new Coordinate();
		
		// Initialize matrices
		Matrix.setIdentityM(mAccumulatedRotation, 0);
		Matrix.setIdentityM(mCurrentRotation, 0);
		Matrix.setIdentityM(mTemporaryMatrix, 0);
		Matrix.setIdentityM(mModelMatrix, 0);
		
		// Create juggler(s) and Floor
		this.juggler = new Juggler(context, this.pattern.getNumberOfJugglers()); 
		this.floor = new Floor(context);

		// Lay out the spatial paths in the pattern
		try {
			pattern.layoutPattern();
		} catch (JuggleExceptionInternal e) {
			e.printStackTrace();
		} catch (JuggleExceptionUser e) {
			e.printStackTrace();
		}
		
		// Update space-time attributes regarding pattern
        syncToPattern();
        setCameraCoordinate();

        // Set zoom
        this.mZoom = (this.pattern.getNumberOfJugglers() == 1)? ZOOM_INIT_1: ZOOM_INIT_2;
        
        // Compute max dimensions
        // this.overallmax.z to handle from floor to highest prop position
        boundingBoxeMaxSize = Math.max(this.overallmax.z,  Math.max(Math.abs(this.overallmax.x - this.overallmin.x), Math.abs(this.overallmax.y - this.overallmin.y)));
		this.depthValue = mZoom * (boundingBoxeMaxSize + 20);
        this.top = Math.abs((float)(this.overallmax.z - this.overallmin.z));
        this.bottom = -this.top;
        this.left = -this.top;
        this.right = this.top;
        this.zNear = 10.0f;
        this.zFar = 2.0f*(float)depthValue;
        
        /*
		Log.v("JugglingRenderer","OverallMin Coordinate X=" + this.overallmin.x + " Y=" + this.overallmin.y + " Z=" + this.overallmin.z);
    	Log.v("JugglingRenderer","OverallMax Coordinate X=" + this.overallmax.x + " Y=" + this.overallmax.y + " Z=" + this.overallmax.z);
    	Log.v("JugglingRenderer","BoundingBoxeMaxSize=" + boundingBoxeMaxSize);
    	Log.v("JugglingRenderer", "roiHalfHeight = " + this.roiHalfHeight + ", depth = " + this.depthValue);
    	Log.v("JugglingRenderer", "top = " + this.top + ", bottom = " + this.bottom + ", left = " + this.left + ", right = " + this.right + ", zNear = " + this.zNear + ", zfar = " + this.zFar);
    	*/

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
        int color = preferences.getInt("background_color", context.getResources().getInteger(R.color.background_default_color));
        float[] rgba = new ColorConverter().hex2rgba(color);
        gl.glClearColor(rgba[0], rgba[1], rgba[2], rgba[3]);
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
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
	    
		// Clears the screen and depth buffer.
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
			
 		// Save the current matrix.
		gl.glPushMatrix();
		
		// Sticky angles (to keep things easier, the angles are kept between 0 and 360)
//        mAngleX = (mAngleX + ANGLE_MAX) % ANGLE_MAX;
//        mAngleY = (mAngleY + ANGLE_MAX) % ANGLE_MAX;
//		for (float angle: ANGLE_STICKY) {
//		    if (angle - ANGLE_EPSILON <= mAngleX && mAngleX <= angle + ANGLE_EPSILON)
//		        mAngleX = angle;
//            if (angle - ANGLE_EPSILON <= mAngleY && mAngleY <= angle + ANGLE_EPSILON)
//                mAngleY = angle;
//		}
		
		// Set a matrix that contains the current rotation.
		Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.setIdentityM(mCurrentRotation, 0);
		Matrix.rotateM(mCurrentRotation, 0, mAngleX, 0.0f, 1.0f, 0.0f);
		Matrix.rotateM(mCurrentRotation, 0, mAngleY, 1.0f, 0.0f, 0.0f);
		//Log.v("JugglingRenderer", "mAngleX = " + mAngleX + " mAngleY = " + mAngleY);
		mAngleX = 0.0f;
		mAngleY = 0.0f;

		// Multiply the current rotation by the accumulated rotation, and then set the accumulated rotation to the result.
		Matrix.multiplyMM(mTemporaryMatrix, 0, mCurrentRotation, 0, mAccumulatedRotation, 0);
		System.arraycopy(mTemporaryMatrix, 0, mAccumulatedRotation, 0, 16);
		
		// Put the overall rotation into the model matrix.
		Matrix.multiplyMM(mTemporaryMatrix, 0, mModelMatrix, 0, mAccumulatedRotation, 0);
		System.arraycopy(mTemporaryMatrix, 0, mModelMatrix, 0, 16);

		//printMatrix(mModelMatrix);
		
		// Apply Rotatation, Scaling and Translation
		gl.glMultMatrixf(mModelMatrix, 0);
        gl.glScalef(mZoom, mZoom, mZoom);
        gl.glTranslatef(-(float)cameraCenter.x, -(float)cameraCenter.y, -(float)cameraCenter.z);
        
		// Draw the Frame
		drawEffectiveFrame(gl);
        
		// Restore the last matrix.
		gl.glPopMatrix();
		
		// Workaround to handle objects disappearance
		// It actually update the distance the camera has to move backward to see all objects even when the scene has been rotated
		if (updateView)		
		{		
			boundingBoxeMaxSize =  Math.max(this.overallmax.z,  Math.max(Math.abs(this.overallmax.x - this.overallmin.x), Math.abs(this.overallmax.y - this.overallmin.y)));		
			depthValue = mZoom* (boundingBoxeMaxSize + 20);		
			zFar = 2.0f*(float)depthValue;
			
			// Update camera viewport		
			gl.glMatrixMode(GL10.GL_PROJECTION);		
			gl.glLoadIdentity();		
			gl.glOrthof(this.aspect*this.left, this.aspect*this.right, this.bottom, this.top, this.zNear, this.zFar);		
			gl.glMatrixMode(GL10.GL_MODELVIEW);		
			gl.glLoadIdentity();
			
			updateView = false;
		}
		
		// Move camera
		gl.glLoadIdentity();
		gl.glTranslatef(-(float)cameraCenter.x, 0.0f, -(float)(depthValue));
	
		
		// Time for an animation
		if (!freeze)
			time = (time + sim_interval_secs) % pattern.getLoopEndTime() ;

	}


	
    /*
     * 
     * Set our projection matrix. This doesn't have to be done
     * each time we draw, but usually a new projection needs to
     * be set when the viewport is resized.
     * 
     */
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		
		// Sets the current view port to the new size.
		gl.glViewport(0, 0, width, height);
		this.aspect = (float)width / (float)height;
		
		// Set the Projection
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrthof(this.aspect*this.left, this.aspect*this.right, this.bottom, this.top, this.zNear, this.zFar);
		
		// Select the modelview matrix
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		
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
			
			// Draw the Floor
			floor.draw(gl);
			
			// Draw the Jugglers 
			for (int j = 1; j <= pattern.getNumberOfJugglers(); j++) {
				juggler.findJugglerCoordinates(this.pattern, time);
				juggler.MathVectorToVertices();
				juggler.draw(gl);
			}

			// Draw the props
			int color;
			float x=0.0f, y=0.0f, z=0.0f;
			for (int i = 1; i <= pattern.getNumberOfPaths(); i++) {
	            pattern.getPathCoordinate(i, time, tempc);
	            if (!tempc.isValid())
	                tempc.setCoordinate(0.0,0.0,0.0);
	            x = (float)(0.5f + tempc.x);
	            y = (float)(0.5f + tempc.y);
	            z = (float)(0.5f + tempc.z);
	            Prop pr = pattern.getProp(animpropnum[i-1]);
	            // TODO: The color shouldn't be set here (I, Romain, think),
	            // because the color of the Prop doesn't depend on the value of
	            // i since one Prop won't always be in the same place, so we
	            // can't identify one Prop using i.
//	            color = preferences.getInt("SelectedColor_for_item_" + (i-1), context.getResources().getInteger(R.color.prop_default_color));
	            color = preferences.getInt("prop_color", context.getResources().getInteger(R.color.prop_default_color));
	            pr.setColor(color);
	            pr.setCenter(tempc);
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
        this.overallmax = Coordinate.max(this.overallmax, patternmax);

        this.overallmin = Coordinate.min(handmin, getJugglerWindowMin());
        this.overallmin = Coordinate.min(this.overallmin, patternmin);

        if (com.jonglen7.jugglinglab.jugglinglab.core.Constants.DEBUG_LAYOUT) {
            System.out.println("Hand max = " + handmax);
            System.out.println("Hand min = " + handmin);
            System.out.println("Prop max = " + propmax);
            System.out.println("Prop min = " + propmin);
            System.out.println("Pattern max = " + patternmax);
            System.out.println("Pattern min = " + patternmin);
            System.out.println("Overall max = " + this.overallmax);
            System.out.println("Overall min = " + this.overallmin);

            //this.overallmax = new Coordinate(100.0,0.0,500.0);
            //this.overallmin = new Coordinate(-100.0,0.0,-100.0);
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
    
    public double getTime() { return this.time; };

    public void setTime(double time) {
        /*		while (time < pat.getLoopStartTime())
        time += (pat.getLoopEndTime() - pat.getLoopStartTime());
        while (time > pat.getLoopEndTime())
        time -= (pat.getLoopEndTime() - pat.getLoopStartTime());
        */
        this.time = time;
    }
    
    private void setCameraCoordinate()
    {
    	double x = (double)(0.5*(overallmax.x+overallmin.x));
    	double y = (double)(0.5*(overallmax.z+overallmin.z));
    	double z = (double)(0.5*(overallmax.y+overallmin.y));
    	cameraCenter.setCoordinate(x, y, z);

//    	Log.v("JugglingRenderer","Camera Center\tX=" + this.cameraCenter.x + "\tY=" + this.cameraCenter.y + "\tZ=" + this.cameraCenter.z);
    }
    
	public AnimatorPrefs getPrefs() {
		return prefs;
	}

	public void setPrefs(AnimatorPrefs prefs) {
		this.prefs = prefs;
	}

	public void SetZoomValue(float newZoomValue) {
	    // The zoom can't go lower than ZOOM_MIN
		mZoom = Math.max(ZOOM_MIN, newZoomValue);
		
		updateView = true;
	}
    
    /** Reset Anim **/
    public void resetAnim() {
    	
        mAngleX = 0;
        mAngleY = 0;
        mZoom = (this.pattern.getNumberOfJugglers() == 1)? ZOOM_INIT_1: ZOOM_INIT_2;
        
		// Initialize matrices
		Matrix.setIdentityM(mAccumulatedRotation, 0);
		Matrix.setIdentityM(mCurrentRotation, 0);
		Matrix.setIdentityM(mTemporaryMatrix, 0);
		Matrix.setIdentityM(mModelMatrix, 0);
    }
    

}