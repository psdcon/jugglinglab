package com.jonglen7.jugglinglab.ui;

import greendroid.widget.QuickActionGrid;
import greendroid.widget.QuickActionWidget;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.EditText;

import com.jonglen7.jugglinglab.R;
import com.jonglen7.jugglinglab.util.Collection;

public class QuickActionGridCollection extends QuickActionGrid {
	
	final static int EDIT = 0;
	final static int DELETE = 1;
	
	Collection collection;
	Activity activity;

	public QuickActionGridCollection(Context context) {
        super(context);
        this.activity = (Activity) context;

        this.addQuickAction(new MyQuickAction(activity, R.drawable.gd_action_bar_edit, R.string.quickactions_rename));
        this.addQuickAction(new MyQuickAction(activity, android.R.drawable.ic_delete, R.string.quickactions_delete));
        this.setOnQuickActionClickListener(mActionListener);
	}

    private OnQuickActionClickListener mActionListener = new OnQuickActionClickListener() {
        public void onQuickActionClicked(QuickActionWidget widget, int position) {
    		final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
    		
        	switch (position) {
        	case EDIT:
        		final EditText input = new EditText(activity);
        		builder.setView(input);
        		builder.setTitle(activity.getString(R.string.quickactions_rename));
        		input.setText(collection.getCUSTOM_DISPLAY());
        		
        		builder.setPositiveButton(activity.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
	        		public void onClick(DialogInterface dialog, int whichButton) {
		        		collection.edit(input.getText().toString());
		        		activity.finish();
		        		activity.startActivity(activity.getIntent());
	        		}
        		});

        		builder.setNegativeButton(activity.getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
	        		public void onClick(DialogInterface dialog, int whichButton) {
	        			dialog.cancel();
	        		}
        		});
        		builder.show();
        		break;
        		
        	case DELETE:
        		builder.setTitle(activity.getString(R.string.quickactions_delete_confirmation, collection.getCUSTOM_DISPLAY()));
        		
        		builder.setPositiveButton(activity.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
	        		public void onClick(DialogInterface dialog, int whichButton) {
	            		collection.delete();
		        		activity.finish();
		        		activity.startActivity(activity.getIntent());
	        		}
        		});

        		builder.setNegativeButton(activity.getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
	        		public void onClick(DialogInterface dialog, int whichButton) {
	        			dialog.cancel();
	        		}
        		});
        		builder.show();
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
