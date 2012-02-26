package com.jonglen7.jugglinglab.ui;

import greendroid.widget.QuickActionGrid;
import greendroid.widget.QuickActionWidget;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;

import com.jonglen7.jugglinglab.R;
import com.jonglen7.jugglinglab.util.Collection;

public class QuickActionGridCollection extends QuickActionGrid {
	
	final static int EDIT = 0;
	final static int DELETE = 1;
	
	Context context;
	Collection collection;
	Intent intent;
	Activity activity;

	public QuickActionGridCollection(Context context) {
		super(context);
		this.context = context;
        this.addQuickAction(new MyQuickAction(context, R.drawable.gd_action_bar_edit, R.string.gd_edit));
        this.addQuickAction(new MyQuickAction(context, android.R.drawable.ic_delete, R.string.quickactions_delete));
        this.setOnQuickActionClickListener(mActionListener);
	}

    private OnQuickActionClickListener mActionListener = new OnQuickActionClickListener() {
        public void onQuickActionClicked(QuickActionWidget widget, int position) {
    		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
    		
        	switch (position) {
        	case EDIT:
        		final EditText input = new EditText(context);
        		builder.setView(input);
        		builder.setTitle(context.getString(R.string.gd_edit));
        		input.setText(collection.getCUSTOM_DISPLAY());
        		
        		builder.setPositiveButton(context.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
	        		public void onClick(DialogInterface dialog, int whichButton) {
		        		collection.edit(input.getText().toString());
		        		activity.finish();
		        		activity.startActivity(intent);
	        		}
        		});

        		builder.setNegativeButton(context.getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
	        		public void onClick(DialogInterface dialog, int whichButton) {
	        			dialog.cancel();
	        		}
        		});
        		builder.show();
        		break;
        		
        	case DELETE:
        		builder.setTitle(String.format(context.getString(R.string.alert_dialog_two_buttons_title, collection.getCUSTOM_DISPLAY())));
        		
        		builder.setPositiveButton(context.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
	        		public void onClick(DialogInterface dialog, int whichButton) {
	            		collection.delete();
		        		activity.finish();
		        		activity.startActivity(intent);
	        		}
        		});

        		builder.setNegativeButton(context.getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
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
    
    public void show(View view, Collection collection, Intent intent, Activity activity) {
    	this.collection = collection;
    	this.intent = intent;
    	this.activity = activity;
    	super.show(view);
    }
}
