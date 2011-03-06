/******
 * File from SuperdryColorPickerApp
 * Under the MIT License
 * Copyright (c) 2011 superdry
 */

package com.jonglen7.jugglinglab.ui;

import com.jonglen7.jugglinglab.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

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
		public BorderView(Context context) {
			super(context);
		}

		protected void onDraw(Canvas canvas) {
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setStyle(Paint.Style.FILL);
			paint.setColor(Color.WHITE);
			canvas.drawRoundRect(new RectF(0, 0, borderViewWidth,
					borderViewWidth), 3, 3, paint);
			paint.setStyle(Paint.Style.FILL);
			paint.setColor(Color.BLACK);
			canvas.drawRoundRect(new RectF(1, 1, borderViewWidth - 1,
					borderViewWidth - 1), 3, 3, paint);
			paint.setStyle(Paint.Style.FILL);
			paint.setColor(color);
			canvas.drawRoundRect(new RectF(2, 2, borderViewWidth - 2,
					borderViewWidth - 2), 3, 3, paint);
		}

	}

	public void setDpi(int dpi) {
		this.borderViewWidth = (int) (dpi * 75 / 240);
	}

	public void setColor(int color) {
		this.color = color;
	}
}