package com.jonglen7.jugglinglab.ui;

import greendroid.widget.QuickActionBar;
import greendroid.widget.QuickActionWidget;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.jonglen7.jugglinglab.R;
import com.jonglen7.jugglinglab.util.Collection;

public class QuickActionBarCollection extends QuickActionBar {
	
	Context context;
	Collection collection;

	public QuickActionBarCollection(Context context) {
		super(context);
		this.context = context;
        this.addQuickAction(new MyQuickAction(context, R.drawable.gd_action_bar_edit, R.string.gd_edit));
        this.setOnQuickActionClickListener(mActionListener);
	}

    private OnQuickActionClickListener mActionListener = new OnQuickActionClickListener() {
        public void onQuickActionClicked(QuickActionWidget widget, int position) {
        	switch (position) {
        	case 0: // TODO Romain: Edit
        		Toast.makeText(context, "Edit (popup)", Toast.LENGTH_LONG).show();
        		break;
        	default:
        		Toast.makeText(context, "Item " + position + " clicked: " + collection.getDisplay(), Toast.LENGTH_LONG).show();
        		break;
        	}
        }
    };
    
    public void show(View view, Collection collection) {
    	this.collection = collection;
        //TODO Romain: Uncomment when ready
//    	super.show(view);
    }
}
