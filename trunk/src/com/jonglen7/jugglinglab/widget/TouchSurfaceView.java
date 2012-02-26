package com.jonglen7.jugglinglab.widget;

import com.jonglen7.jugglinglab.jugglinglab.renderer.JugglingRenderer;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Implement a simple rotation control in the X-Y plan
 *
 */
public class TouchSurfaceView extends GLSurfaceView {

	boolean isOnPause = false;
	float move = 0;
	
    public TouchSurfaceView(Context context) {
        super(context);
    }

    public TouchSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    @Override
    public void setRenderer(Renderer renderer) {
    	mRenderer = (JugglingRenderer) renderer;
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
        switch (e.getAction()) {
        case MotionEvent.ACTION_MOVE:
        	if (!isOnPause) {
                float dx = x - mPreviousX;
                float dy = y - mPreviousY;
                mRenderer.mAngleX += dx * TOUCH_SCALE_FACTOR;
                mRenderer.mAngleY += dy * TOUCH_SCALE_FACTOR;
                requestRender();
                move += Math.abs(dx) + Math.abs(dy);
        	}
            break;
        case MotionEvent.ACTION_UP:
        	if (move == 0) {
        		if (isOnPause) {
            		onResume();
            	} else {
            		onPause();
            	}
        		isOnPause = !isOnPause;
        	}
        	move = 0;
        	break;
        }
        mPreviousX = x;
        mPreviousY = y;
        return true;
    }
    
    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private final float TRACKBALL_SCALE_FACTOR = 36.0f;
    private JugglingRenderer mRenderer;
    private float mPreviousX;
    private float mPreviousY;
    
}

