/******
 * File from SuperdryColorPickerApp
 * Under the MIT License
 * Copyright (c) 2011 superdry
 */

package com.jonglen7.jugglinglab.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.jonglen7.jugglinglab.R;

public class ColorPickerPreference extends Preference {

	private int color = 0xff000000;
	private int borderViewWidth;

	public ColorPickerPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (this.getWidgetLayoutResource() != R.layout.prefcolorview) {
			setWidgetLayoutResource(R.layout.prefcolorview);
		}

	}

	public ColorPickerPreference(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		if (this.getWidgetLayoutResource() != R.layout.prefcolorview) {
			setWidgetLayoutResource(R.layout.prefcolorview);
		}
	}

	protected void onBindView(View view) {
		super.onBindView(view);
		LinearLayout linearlayout = (LinearLayout) view
				.findViewById(R.id.prefcolorlayout);
		if (linearlayout.getChildCount() == 0) {
			linearlayout.addView(new BorderView(getContext()), borderViewWidth,
					borderViewWidth);
		}
	}

	public class BorderView extends View {
	    private Paint paint = new Paint();
	    private RectF rect1 = new RectF(0, 0, borderViewWidth, borderViewWidth);
        private RectF rect2 = new RectF(1, 1, borderViewWidth - 1, borderViewWidth - 1);
        private RectF rect3 = new RectF(2, 2, borderViewWidth - 2, borderViewWidth - 2);
	    
		public BorderView(Context context) {
			super(context);
		}

		protected void onDraw(Canvas canvas) {
			paint.setAntiAlias(true);
			paint.setStyle(Paint.Style.FILL);
			paint.setColor(Color.WHITE);
			canvas.drawRoundRect(rect1, 3, 3, paint);
			paint.setStyle(Paint.Style.FILL);
			paint.setColor(Color.BLACK);
			canvas.drawRoundRect(rect2, 3, 3, paint);
			paint.setStyle(Paint.Style.FILL);
			paint.setColor(color);
			//Log.v("ColorPickerPreference", "onDraw | " + String.format("#%02x%02x%02x", Color.red(color),
			//		Color.green(color), Color.blue(color)));
			canvas.drawRoundRect(rect3, 3, 3, paint);
		}

	}

	public void setDpi(int dpi) {
		this.borderViewWidth = (int) (dpi * 75 / 240);
	}

	public void setColor(int color) {
		this.color = color;
		//Log.v("ColorPickerPreference", "setColor | " + String.format("#%02x%02x%02x", Color.red(color),
		//		Color.green(color), Color.blue(color)));
	}
}