package com.jonglen7.jugglinglab.widget;

import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.jonglen7.jugglinglab.jugglinglab.renderer.JugglingRenderer;

/**
 * 
 * Implement a touch surface
 * Simple rotation around axes x and y
 * Pinch-to-zoom with regard to http://android-developers.blogspot.fr/2010/06/making-sense-of-multitouch.html
 * 
 */
public class TouchSurfaceView extends GLSurfaceView {

	/** Attributes **/
    private final float TOUCH_SCALE_FACTOR = 180.0f / 640;
    private final float TRACKBALL_SCALE_FACTOR = 36.0f;
   
    private JugglingRenderer mRenderer;
	private PointF mPrevious = new PointF();
	private boolean isOnPause = false;
    private ScaleGestureDetector mScaleDetector;
    boolean motion = false;
    boolean scale = false;

	
    /** Constructor **/
    public TouchSurfaceView(Context context) 
    {
        super(context);
        init(context);
    }

    /** Constructor **/
    public TouchSurfaceView(Context context, AttributeSet attrs) 
    {
        super(context, attrs);
        init(context);
    }
    
    /** Initialization method **/
    private void init(Context context) 
    {
    	// Set focus
    	setFocusable(true);
    	
    	// Create our ScaleGestureDetector
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }
    
    
    @Override
    public void setRenderer(Renderer renderer) 
    {
    	mRenderer = (JugglingRenderer) renderer;
    	super.setRenderer(mRenderer);
    }
    
    @Override 
    public boolean onTrackballEvent(MotionEvent e) 
    {
        mRenderer.mAngleX += e.getX() * TRACKBALL_SCALE_FACTOR;
        mRenderer.mAngleY += e.getY() * TRACKBALL_SCALE_FACTOR;
        requestRender();
        return true;
    }

    @Override 
    public boolean onTouchEvent(MotionEvent e) 
    {
    	// Let the ScaleGestureDetector inspect all events.
        mScaleDetector.onTouchEvent(e);

        // Get current touch coordinates
        float x = e.getX();
        float y = e.getY();
        
        // Switch detection action : either move (rotate, zoom) or pause/resume
        switch (e.getAction() & MotionEvent.ACTION_MASK) 
        {
        	// Move action (rotate or zoom)
	        case MotionEvent.ACTION_MOVE:
	        	
	        	// Only move if animation is not paused
	        	if (!isOnPause) 
	        	{
	        	    if (!scale) {
    	        		float dx;
    	        		float dy;
    	        		
    	        		// Only ROTATE if the ScaleGestureDetector isn't processing a gesture.
    	        		if (!mScaleDetector.isInProgress()) 
    	        		{
    	                    dx = x - mPrevious.x;
    	                    dy = y - mPrevious.y;
    	                    mRenderer.mAngleX += dx * TOUCH_SCALE_FACTOR;
    	                    mRenderer.mAngleY += dy * TOUCH_SCALE_FACTOR;
    	                    requestRender();
    	        		}
	        	    }
	        		
	        		motion = true;
	        	}
	            break;
	            
	        // Pause/Resume when first finger lifted
	        case MotionEvent.ACTION_UP:
        		// Only if no gesture.
        		if (!motion) 
        		{
	        		if (isOnPause)
	            		onResume();
	            	else
	            		onPause();
	        		isOnPause = !isOnPause;
        		}
        		motion = false;
        		scale = false;

	        	break;
        }
        
        mPrevious.set(x, y);
        return true;
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) 
    {
            if(keyCode==KeyEvent.KEYCODE_DPAD_UP)
            	zoomIn();
            if(keyCode==KeyEvent.KEYCODE_DPAD_DOWN)
            	zoomOut();
            invalidate();
            return true;
    }
    
    /** Zoom In **/
    public void zoomIn() {
    	mRenderer.SetZoomValue(mRenderer.mZoom + mRenderer.ZOOM_STEP);
    }
    
    /** Zoom Out **/
    public void zoomOut() {
        mRenderer.SetZoomValue(mRenderer.mZoom - mRenderer.ZOOM_STEP);
    }
    
    /** ScaleListener for Pinch To Zoom **/
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
        	
        	// Update scale factor
        	mRenderer.SetZoomValue(mRenderer.mZoom*detector.getScaleFactor());
        	requestRender();
            
            // Don't let the object get too small or too large.
            //mRenderer.mZoom = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));

        	scale = true;
        	
            return true;
        }
    }
}

