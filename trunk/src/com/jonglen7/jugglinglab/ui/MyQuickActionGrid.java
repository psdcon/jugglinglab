package com.jonglen7.jugglinglab.ui;

import com.jonglen7.jugglinglab.R;
import com.jonglen7.jugglinglab.jugglinglab.core.PatternRecord;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Toast;
import greendroid.widget.QuickAction;
import greendroid.widget.QuickActionGrid;
import greendroid.widget.QuickActionWidget;

public class MyQuickActionGrid extends QuickActionGrid {
	
	// TODO: Change the images showing in the QuickActionGrid and implement the onQuickActionClicked method
	
	Context context;
	PatternRecord pattern_record;

	public MyQuickActionGrid(Context context) {
		super(context);
		this.context = context;
        this.addQuickAction(new MyQuickAction(context, R.drawable.gd_action_bar_star, R.string.gd_star));
        this.addQuickAction(new MyQuickAction(context, R.drawable.gd_action_bar_export, R.string.gd_export));
        this.addQuickAction(new MyQuickAction(context, R.drawable.gd_action_bar_add, R.string.gd_add));
        this.addQuickAction(new MyQuickAction(context, R.drawable.gd_action_bar_share, R.string.gd_share));
        this.addQuickAction(new MyQuickAction(context, R.drawable.gd_action_bar_info, R.string.gd_info));

        this.setOnQuickActionClickListener(mActionListener);
	}

    private OnQuickActionClickListener mActionListener = new OnQuickActionClickListener() {
        public void onQuickActionClicked(QuickActionWidget widget, int position) {
            Toast.makeText(context, "Item " + position + " clicked: " + pattern_record.getAnim(), Toast.LENGTH_LONG).show();
            // TODO Romain: Share: "http://jugglinglab.sourceforge.net/siteswap.php?" + pattern_record.getAnim()
        }
    };
    
    public void show(View view, PatternRecord pattern_record) {
    	this.pattern_record = pattern_record;
    	super.show(view);
    }
    
    private static class MyQuickAction extends QuickAction {
        
        private static final ColorFilter BLACK_CF = new LightingColorFilter(Color.BLACK, Color.BLACK);

        public MyQuickAction(Context ctx, int drawableId, int titleId) {
            super(ctx, buildDrawable(ctx, drawableId), titleId);
        }
        
        private static Drawable buildDrawable(Context ctx, int drawableId) {
            Drawable d = ctx.getResources().getDrawable(drawableId);
            d.setColorFilter(BLACK_CF);
            return d;
        }
        
    }

}
