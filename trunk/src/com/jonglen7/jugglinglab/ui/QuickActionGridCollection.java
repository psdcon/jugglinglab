package com.jonglen7.jugglinglab.ui;

import greendroid.widget.QuickActionGrid;
import greendroid.widget.QuickActionWidget;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.EditText;

import com.jonglen7.jugglinglab.R;
import com.jonglen7.jugglinglab.util.Collection;

public class QuickActionGridCollection extends QuickActionGrid {
	
	Context context;
	Collection collection;

	public QuickActionGridCollection(Context context) {
		super(context);
		this.context = context;
        this.addQuickAction(new MyQuickAction(context, R.drawable.gd_action_bar_edit, R.string.gd_edit));
        this.addQuickAction(new MyQuickAction(context, android.R.drawable.ic_delete, R.string.quickactions_delete));
        this.setOnQuickActionClickListener(mActionListener);
	}

    private OnQuickActionClickListener mActionListener = new OnQuickActionClickListener() {
        public void onQuickActionClicked(QuickActionWidget widget, int position) {
        	switch (position) {
        	case 0: // Edit
        		final EditText input = new EditText(context);
        		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        		builder.setView(input);
        		builder.setTitle(context.getString(R.string.gd_edit));
        		input.setText(collection.getCUSTOM_DISPLAY());
        		
        		builder.setPositiveButton(context.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
	        		public void onClick(DialogInterface dialog, int whichButton) {
		        		collection.edit(input.getText().toString());
		        		// TODO Romain (update ListView): Modify the name in the ListView
	        		}
        		});

        		builder.setNegativeButton(context.getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
	        		public void onClick(DialogInterface dialog, int whichButton) {
	        			dialog.cancel();
	        		}
        		});
        		builder.show();
        		break;
        		
        	case 1: // Delete
        		collection.delete();
        		break;
        		
        	default:
        		break;
        	}
        }
    };
    
    public void show(View view, Collection collection) {
    	this.collection = collection;
    	super.show(view);
    }
}
