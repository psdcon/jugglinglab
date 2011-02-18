package com.jonglen7.jugglinglab.jugglinglab.renderer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;

import com.jonglen7.jugglinglab.jugglinglab.core.AnimatorPrefs;
import com.jonglen7.jugglinglab.jugglinglab.jml.JMLPattern;
import com.jonglen7.jugglinglab.jugglinglab.util.Coordinate;
import com.jonglen7.jugglinglab.jugglinglab.util.JuggleExceptionInternal;

public class JugglingRenderer implements Renderer {
	
	// Attributes
	private Juggler juggler;
	private JMLPattern pattern;
	private AnimatorPrefs pref;
	
	double sim_interval_secs = 0.0;
	double time = 0.0;
	
	// Constructors
	public JugglingRenderer() {
		pattern = null;
		pref = new AnimatorPrefs();
		juggler = new Juggler(1);  // TODO: Implements for multiple jugglers
		//juggler = new FakeJuggler();
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
		gl.glTranslatef(0, -100.0f, -100.0f); 
		// Reduce
		//gl.glScalef(0.2f, 0.2f, 0.2f);
		// Draw the Juggler	
		// TODO: WTF is this time?
		try {
			juggler.findJugglerCoordinates(pattern, time);
			juggler.MathVectorToVertices();
			juggler.draw(gl);
		} catch (JuggleExceptionInternal e){
			e.printStackTrace();
		}
		// Restore the last matrix.
		gl.glPopMatrix();
		
		// The 2 following lines comes from Animator.syncToPattern()
		// Try to simulate time for an animation
        time = (time + sim_interval_secs) % pattern.getLoopEndTime() ;
        
        
        
		/*
		// FakeJuggler
		
		// Clears the screen and depth buffer.
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		// Replace the current matrix with the identity matrix
		gl.glLoadIdentity();
		// Save the current matrix.
		gl.glPushMatrix();
		// Translates 10 units into the screen.
		gl.glTranslatef(0, -100.0f, -100.0f); 
		// Reduce
		//gl.glScalef(0.2f, 0.2f, 0.2f);
		// Draw	
		juggler.draw(gl);
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


	
	public void setPattern(JMLPattern pattern) {
		this.pattern = pattern;
		int num_frames = (int)(0.5 + (pattern.getLoopEndTime() - pattern.getLoopStartTime()) * pref.slowdown * pref.fps);
		sim_interval_secs = (pattern.getLoopEndTime()-pattern.getLoopStartTime()) / num_frames;
	
		
	}
	
/*
    public com.jonglen7.jugglinglab.jugglinglab.util.Coordinate getHandWindowMax() {
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
*/
}