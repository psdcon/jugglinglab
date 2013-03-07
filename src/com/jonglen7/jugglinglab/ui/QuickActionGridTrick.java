package com.jonglen7.jugglinglab.ui;

import greendroid.widget.QuickActionGrid;
import greendroid.widget.QuickActionWidget;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.widget.EditText;

import com.jonglen7.jugglinglab.R;
import com.jonglen7.jugglinglab.jugglinglab.core.PatternRecord;
import com.jonglen7.jugglinglab.util.Collection;
import com.jonglen7.jugglinglab.util.DataBaseHelper;
import com.jonglen7.jugglinglab.util.Trick;

public class QuickActionGridTrick extends QuickActionGrid {

	final static int STAR = 0;
	final static int EDIT = 1;
	final static int LIST = 2;
	final static int SHARE = 3;
//	final static int CATCHES = 4;
//	final static int STATS = 5;
	final static int INFO = 4;
	final static int DELETE = 5;

	PatternRecord pattern_record;
	Activity activity;
	MyQuickAction qa_star;
    MyQuickAction qa_edit;
    MyQuickAction qa_list;
    MyQuickAction qa_share;
    MyQuickAction qa_info;
    MyQuickAction qa_delete;

	public QuickActionGridTrick(Activity activity) {
		super(activity);
		this.activity = activity;

		qa_star = new MyQuickAction(activity, R.drawable.gd_action_bar_star, R.string.gd_star);
		qa_edit = new MyQuickAction(activity, R.drawable.gd_action_bar_edit, R.string.gd_edit);
		qa_list = new MyQuickAction(activity, R.drawable.gd_action_bar_list, R.string.quickactions_trick_collections);
		qa_share = new MyQuickAction(activity, R.drawable.gd_action_bar_share, R.string.gd_share);
        //TODO Romain (Stats): cf icons http://androiddrawableexplorer.appspot.com/
//        this.addQuickAction(new MyQuickAction(activity, R.drawable.gd_action_bar_add, R.string.quickactions_trick_catches));
//        this.addQuickAction(new MyQuickAction(activity, R.drawable.gd_action_bar_info, R.string.quickactions_trick_stats)); // TODO Romain (Stats): change the icon
        qa_info = new MyQuickAction(activity, R.drawable.gd_action_bar_info, R.string.gd_info);
        qa_delete = new MyQuickAction(activity, android.R.drawable.ic_delete, R.string.quickactions_delete);

        this.setOnQuickActionClickListener(mActionListener);
	}

    private OnQuickActionClickListener mActionListener = new OnQuickActionClickListener() {
        public void onQuickActionClicked(QuickActionWidget widget, int position) {
    		final Trick trick = new Trick(pattern_record, activity);
    		final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
    		
        	switch (position) {
	    	case STAR:
	    		trick.star();
        		activity.finish();
        		activity.startActivity(activity.getIntent());
	    		break;
	    		
        	case EDIT:
        		final EditText input = new EditText(activity);
        		builder.setView(input);
        		builder.setTitle(activity.getString(R.string.gd_edit));
        		input.setText(trick.getCUSTOM_DISPLAY());
        		
        		builder.setPositiveButton(activity.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
	        		public void onClick(DialogInterface dialog, int whichButton) {
		        		trick.edit(input.getText().toString());
		        		// For the animation, since it gets the display from the pattern_record,
		        		// that is put in the Intent, we need to update it too
		        		pattern_record.setDisplay(input.getText().toString());
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
        		
        	case LIST:
        		final ArrayList<Collection> collections = createCollectionList();
        		ArrayList<String> collections_displays = new ArrayList<String>();
        		// TODO Romain (QuickActionGridTrick): Is there a better way to distinguish Tutorials and Pattern List ? https://groups.google.com/group/android-beginners/browse_thread/thread/4a757ecf893e42a1/dbf32ce04f1cc7af?#dbf32ce04f1cc7af
        		for (Collection c: collections) collections_displays.add("[" + (c.getIS_TUTORIAL() != 0? activity.getString(R.string.tutorials_title) : activity.getString(R.string.pattern_list_title)) + "] " + c.getCUSTOM_DISPLAY());
        		String[] collections_display = (String[])collections_displays.toArray(new String[0]);
        		
        		boolean[] checkedItems = new boolean[collections.size()];
        		ArrayList<Collection> trick_collections = trick.getCollections();
        		for (int i = 0; i < collections.size(); i++) {
        			if (collections.get(i).indexOf(trick_collections) >= 0) checkedItems[i] = true;
        			else checkedItems[i] = false;
        		}
        		
        		// TODO Romain (QuickActionGridTrick): Should reload the activity when done
        		// Don't do it in the onClick function, otherwise it will reload the activity every time the user checks a box
        		// Possible solution: Add a button to validate the change (same as case EDIT)
        		builder.setMultiChoiceItems(collections_display, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        		        trick.updateCollection(collections.get(which));
					}
        		});
        		
        		builder.setTitle(activity.getString(R.string.quickactions_trick_collections));
        		builder.show();
        		break;
        		
        	case SHARE:
        		Intent shareIntent = new Intent(Intent.ACTION_SEND);
        		shareIntent.setType("text/plain");
        		String subject = activity.getString(R.string.quickactions_trick_share_training, pattern_record.getDisplay());
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        		shareIntent.putExtra(Intent.EXTRA_TEXT, subject + "\n" +
        				"http://jugglinglab.sourceforge.net/siteswap.php?" + pattern_record.getAnim());
        		activity.startActivity(Intent.createChooser(shareIntent, activity.getString(R.string.quickactions_trick_share)));
        		break;
        		
//        	case CATCHES: // TODO Romain (Stats): Catches (use builder to display an EditText to enter how many catches, maybe add something to set the date too)
//        		Toast.makeText(activity, "Catches", Toast.LENGTH_SHORT).show();
//        		break;
//        		
//        	case STATS: // TODO Romain (Stats): Stats
////        		Intent intent = (new StatsActivity()).execute(context);
//        		Toast.makeText(activity, "Stats", Toast.LENGTH_SHORT).show();
////        		Intent intent = new Intent(activity, StatsActivity.class);
////                intent.putExtra("pattern_record", pattern_record);
////                activity.startActivity(i);
//        		break;
        		
        	case INFO:
        		Intent intent = new Intent(activity, PatternInfoActivity.class);
            	intent.putExtra("pattern_record", pattern_record);
            	activity.startActivity(intent);
        		break;
        		
        	case DELETE:
        		builder.setTitle(activity.getString(R.string.quickactions_delete_confirmation, trick.getCUSTOM_DISPLAY()));
        		
        		builder.setPositiveButton(activity.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
	        		public void onClick(DialogInterface dialog, int whichButton) {
	            		trick.delete();
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
    
    private ArrayList<Collection> createCollectionList() {
    	DataBaseHelper myDbHelper = DataBaseHelper.init(activity);
        
    	ArrayList<Collection> collections = new ArrayList<Collection>();
		
	 	String query = "SELECT DISTINCT ID_COLLECTION, IS_TUTORIAL, XML_LINE_NUMBER, CUSTOM_DISPLAY " +
	 					"FROM Collection " +
	 					"ORDER BY ID_COLLECTION";
    	Cursor cursor = myDbHelper.execQuery(query);
    	
	 	cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
        	Collection c = new Collection(cursor, activity);
        	if (!c.isStarred()) collections.add(c);
            cursor.moveToNext();
        }

	 	cursor.close();

        myDbHelper.close();
    	
		return collections;
	}
    
    public void show(View view, PatternRecord pattern_record) {
    	this.pattern_record = pattern_record;
    	this.clearAllQuickActions();
        this.addQuickAction(qa_star);
        this.addQuickAction(qa_edit);
        this.addQuickAction(qa_list);
        this.addQuickAction(qa_share);
        this.addQuickAction(qa_info);
    	if ((new Trick(pattern_record, activity)).getID_TRICK() >= 0)
    	    this.addQuickAction(qa_delete);
    	super.show(view);
    }

}
