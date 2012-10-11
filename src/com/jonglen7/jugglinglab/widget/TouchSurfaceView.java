package com.jonglen7.jugglinglab.widget;

import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.jonglen7.jugglinglab.jugglinglab.renderer.JugglingRenderer;

/**
 * Implement a simple rotation control in the X-Y plan
 * Pinch-to-zoom found here: http://media.pragprog.com/titles/eband3/code/Touchv1/src/org/example/touch/Touch.java
 */
public class TouchSurfaceView extends GLSurfaceView {

//    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;  // TODO Fred|Romain: Too fast on actual devices ?
    private final float TOUCH_SCALE_FACTOR = 180.0f / 640;
    private final float TRACKBALL_SCALE_FACTOR = 36.0f;
    private JugglingRenderer mRenderer;
	private PointF mPrevious = new PointF();
    private PointF mPreviousMid = new PointF();
    private PointF mNewMid = new PointF();
	
	boolean isOnPause = false;
	float move = 0;
	float oldDist = 1f;
	static final float distEpsilon = 0.1f;

    // We can be in one of these 3 states
    static final int NONE = 0;
    static final int ROTATE = 1;
    static final int ZOOM = 2;
    int mode = NONE;
	
    public TouchSurfaceView(Context context) {
        super(context);
        init();
    }

    public TouchSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    private void init() {
    	setFocusable(true);
    }
    
    @Override
    public void setRenderer(Renderer renderer) {
    	mRenderer = (JugglingRenderer) renderer;
    	mRenderer.mZoom = 1;
    	super.setRenderer(mRenderer);
    }
    
    @Override public boolean onTrackballEvent(MotionEvent e) {
        mRenderer.mAngleX += e.getX() * TRACKBALL_SCALE_FACTOR;
        mRenderer.mAngleY += e.getY() * TRACKBALL_SCALE_FACTOR;
        requestRender();
        return true;
    }

    @Override public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
        switch (e.getAction() & MotionEvent.ACTION_MASK) {
        case MotionEvent.ACTION_DOWN:
            mode = ROTATE;
            break;
         case MotionEvent.ACTION_POINTER_DOWN:
            oldDist = spacing(e);
            if (oldDist > 10f) {
               mode = ZOOM;
               midPoint(mPreviousMid, e);
            }
            break;
        case MotionEvent.ACTION_MOVE:
        	if (!isOnPause) {
        		float dx;
        		float dy;
        		if (mode == ROTATE) {
                    dx = x - mPrevious.x;
                    dy = y - mPrevious.y;
                    mRenderer.mAngleX += dx * TOUCH_SCALE_FACTOR;
                    mRenderer.mAngleY += dy * TOUCH_SCALE_FACTOR;
                    requestRender();
                    move += Math.abs(dx) + Math.abs(dy);
        		} else if (mode == ZOOM) {											
        			float newDist = spacing(e);
        			if (Math.abs((newDist - oldDist)/oldDist) < distEpsilon) {
        				midPoint(mNewMid, e);
        				dx = mNewMid.x - mPreviousMid.x;
        				dy = mNewMid.y - mPreviousMid.y;
        				mRenderer.mTranslateX += dx * TOUCH_SCALE_FACTOR;
        				mRenderer.mTranslateY -= dy * TOUCH_SCALE_FACTOR;
        				requestRender();
        				mPreviousMid.set(mNewMid);
        			} else if (newDist > 10f) {
        				zoom(newDist / oldDist);							// TODO FRED: Correct TouchZoom (cf usbkey.JLD2.txt)
        				mRenderer.mZoom = newDist / oldDist;				//           Issue here : double and inconsistent modification of mRenderer.mZoom ?!
        				requestRender();
        			}
        		}
        	}
            break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_POINTER_UP:
        	if (mode == ROTATE && move == 0) {
        		if (isOnPause)
            		onResume();
            	else
            		onPause();
        		isOnPause = !isOnPause;
        	}
        	move = 0;
        	mode = NONE;
        	break;
        }
        mPrevious.set(x, y);
        return true;
    }
    
    private float spacing(MotionEvent event) {
	   float x = event.getX(0) - event.getX(1);
	   float y = event.getY(0) - event.getY(1);
	   return FloatMath.sqrt(x * x + y * y);
	}

    /** Calculate the mid point of the first two fingers */
    private void midPoint(PointF point, MotionEvent event) {
       float x = event.getX(0) + event.getX(1);
       float y = event.getY(0) + event.getY(1);
       point.set(x / 2, y / 2);
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
            if(keyCode==KeyEvent.KEYCODE_DPAD_UP)
            	zoomIn();
            if(keyCode==KeyEvent.KEYCODE_DPAD_DOWN)
            	zoomOut();
            invalidate();
            return true;
    }
    
    public void zoom(float value) {
    	mRenderer.mZoom += value;
    }
    
    public void zoomIn() {
    	zoom(.1f);
    }
    
    public void zoomOut() {
    	zoom(-.1f);
    }
}

