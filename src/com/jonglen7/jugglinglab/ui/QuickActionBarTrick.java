package com.jonglen7.jugglinglab.ui;

import greendroid.widget.QuickActionBar;
import greendroid.widget.QuickActionWidget;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.jonglen7.jugglinglab.R;
import com.jonglen7.jugglinglab.jugglinglab.core.PatternRecord;
import com.jonglen7.jugglinglab.util.Trick;

public class QuickActionBarTrick extends QuickActionBar {
	
	Context context;
	PatternRecord pattern_record;

	public QuickActionBarTrick(Context context) {
		super(context);
		this.context = context;
        this.addQuickAction(new MyQuickAction(context, R.drawable.gd_action_bar_star, R.string.gd_star));
        //TODO Romain: Uncomment when ready, cf icons http://androiddrawableexplorer.appspot.com/
//        this.addQuickAction(new MyQuickAction(context, R.drawable.gd_action_bar_edit, R.string.gd_edit));
//        this.addQuickAction(new MyQuickAction(context, R.drawable.gd_action_bar_share, R.string.gd_share));
//        this.addQuickAction(new MyQuickAction(context, R.drawable.gd_action_bar_add, R.string.quickactions_catches));
//        this.addQuickAction(new MyQuickAction(context, R.drawable.gd_action_bar_info, R.string.quickactions_stats));

        this.setOnQuickActionClickListener(mActionListener);
	}

    private OnQuickActionClickListener mActionListener = new OnQuickActionClickListener() {
        public void onQuickActionClicked(QuickActionWidget widget, int position) {
        	switch (position) {
        	case 0: // Star
        		Trick trick = new Trick(pattern_record.getValuesFromAnim(), context);
        		trick.star();
        		// TODO Romain: Change the value of the star next to the pattern
        		// Pb: the callback will be called, so maybe we should ONLY
        		// change the value of the star (i.e. delete the call to trick.star())
        		
        		break;
        	case 1: // TODO Romain: Edit
        		// What is editable? (display, hands movement display, ...)
        		Toast.makeText(context, "Edit (popup)", Toast.LENGTH_LONG).show();
        		break;
        	case 2: // Share
                // TODO Romain: Share: "http://jugglinglab.sourceforge.net/siteswap.php?" + pattern_record.getAnim()
        		Toast.makeText(context, "Share (popup)", Toast.LENGTH_LONG).show();
        		break;
        	case 3: // TODO Romain: Catches
        		Toast.makeText(context, "Add catches (popup)", Toast.LENGTH_LONG).show();
        		break;
        	case 4: // TODO Romain: Stats
        		Toast.makeText(context, "Stats (activity)", Toast.LENGTH_LONG).show();
        		break;
        	default:
        		Toast.makeText(context, "Item " + position + " clicked: " + pattern_record.getAnim(), Toast.LENGTH_LONG).show();
        		break;
        	}
        }
    };
    
    public void show(View view, PatternRecord pattern_record) {
    	this.pattern_record = pattern_record;
    	super.show(view);
    }

}
