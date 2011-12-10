package com.jonglen7.jugglinglab.ui;

import greendroid.widget.QuickAction;
import greendroid.widget.QuickActionBar;
import greendroid.widget.QuickActionWidget;

import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Toast;

import com.jonglen7.jugglinglab.R;
import com.jonglen7.jugglinglab.jugglinglab.core.PatternRecord;
import com.jonglen7.jugglinglab.util.DataBaseHelper;

public class MyQuickActionBar extends QuickActionBar {

	/** DataBase. */
	DataBaseHelper myDbHelper;
	
	Context context;
	PatternRecord pattern_record;

	public MyQuickActionBar(Context context) {
		super(context);
		this.context = context;
        this.addQuickAction(new MyQuickAction(context, R.drawable.gd_action_bar_star, R.string.gd_star));
        //TODO Romain: Uncomment when ready
//        this.addQuickAction(new MyQuickAction(context, R.drawable.gd_action_bar_edit, R.string.gd_edit));
//        this.addQuickAction(new MyQuickAction(context, R.drawable.gd_action_bar_share, R.string.gd_share));
//        this.addQuickAction(new MyQuickAction(context, R.drawable.gd_action_bar_add, R.string.quickactions_catches));
//        this.addQuickAction(new MyQuickAction(context, R.drawable.gd_action_bar_info, R.string.quickactions_stats));

        this.setOnQuickActionClickListener(mActionListener);
	}

    private OnQuickActionClickListener mActionListener = new OnQuickActionClickListener() {
        public void onQuickActionClicked(QuickActionWidget widget, int position) {
        	myDbHelper = DataBaseHelper.init(context);
        	String query;
        	Cursor cursor = null;
        	ContentValues cv;
            
        	switch (position) {
        	case 0: // Star
        		HashMap<String, String> values = pattern_record.getValuesForDB();
        		
        		// Search the DB
    			query = "SELECT T.ID_TRICK, T.STARRED " +
	 					"FROM Trick T, Hands H, Body B, Prop P " +
	 					"WHERE T.ID_HANDS = H.ID_HANDS " +
	 					"AND T.ID_BODY = B.ID_BODY " +
	 					"AND T.ID_PROP = P.ID_PROP " +
	 					"AND T.PATTERN = '" + values.get("pattern") + "'";
    			query += (values.get("hands").length() > 0) ? (" AND H.CODE = '" + values.get("hands") + "'") : " AND H.XML_LINE_NUMBER=0";
    			query += (values.get("body").length() > 0) ? (" AND B.CODE = '" + values.get("body") + "'") : " AND B.XML_LINE_NUMBER=0";
    			query += (values.get("prop").length() > 0) ? (" AND P.CODE = '" + values.get("prop") + "'") : " AND P.XML_LINE_NUMBER=0";
    			cursor = myDbHelper.execQuery(query);
    			cursor.moveToFirst();
    			
    			if (!cursor.isAfterLast()) {
        			// Update
        			int ID_TRICK = cursor.getInt(cursor.getColumnIndex("ID_TRICK"));
        			int STARRED = cursor.getInt(cursor.getColumnIndex("STARRED"));
        			cv = new ContentValues();
        			cv.put("STARRED", 1 - STARRED);
        			myDbHelper.getWritableDatabase().update("Trick", cv, "ID_TRICK=" + ID_TRICK, null);
    			} else {
    				// Insert
    				
    				// Hands
    				int ID_HANDS;
    				String HANDS_CUSTOM_DISPLAY = null;
    				int HANDS_XML_LINE_NUMBER = -1;
    				query = "SELECT ID_HANDS, XML_LINE_NUMBER, CUSTOM_DISPLAY " +
    						"FROM Hands " +
    						"WHERE ";
        			query += (values.get("hands").length() > 0) ? ("CODE = '" + values.get("hands") + "'") : "XML_LINE_NUMBER=0";
        			cursor = myDbHelper.execQuery(query);
        			cursor.moveToFirst();
        			if (!cursor.isAfterLast()) {
            			ID_HANDS = cursor.getInt(cursor.getColumnIndex("ID_HANDS"));
            			HANDS_CUSTOM_DISPLAY = cursor.getString(cursor.getColumnIndex("CUSTOM_DISPLAY"));
            			HANDS_XML_LINE_NUMBER = cursor.getInt(cursor.getColumnIndex("XML_LINE_NUMBER"));
        			} else {
            			cv = new ContentValues();
            			cv.put("CODE", values.get("hands"));
            			ID_HANDS = (int) myDbHelper.getWritableDatabase().insert("Hands", null, cv);
        			}

    				// Body
    				int ID_BODY;
    				query = "SELECT ID_BODY " +
    						"FROM Body " +
    						"WHERE ";
        			query += (values.get("body").length() > 0) ? ("CODE = '" + values.get("body") + "'") : "XML_LINE_NUMBER=0";
        			cursor = myDbHelper.execQuery(query);
        			cursor.moveToFirst();
        			if (!cursor.isAfterLast()) {
        				ID_BODY = cursor.getInt(cursor.getColumnIndex("ID_BODY"));
        			} else {
            			cv = new ContentValues();
            			cv.put("CODE", values.get("body"));
            			ID_BODY = (int) myDbHelper.getWritableDatabase().insert("Body", null, cv);
        			}

    				// Prop
    				int ID_PROP;
    				query = "SELECT ID_PROP " +
    						"FROM Prop " +
    						"WHERE ";
        			query += (values.get("prop").length() > 0) ? ("CODE = '" + values.get("prop") + "'") : "XML_LINE_NUMBER=0";
        			cursor = myDbHelper.execQuery(query);
        			cursor.moveToFirst();
        			if (!cursor.isAfterLast()) {
        				ID_PROP = cursor.getInt(cursor.getColumnIndex("ID_PROP"));
        			} else {
            			cv = new ContentValues();
            			cv.put("CODE", values.get("prop"));
            			ID_PROP = (int) myDbHelper.getWritableDatabase().insert("Prop", null, cv);
        			}

    				// Trick
        			cv = new ContentValues();
        			cv.put("PATTERN", values.get("pattern"));
        			cv.put("ID_HANDS", ID_HANDS);
        			cv.put("ID_BODY", ID_BODY);
        			cv.put("ID_PROP", ID_PROP);
        			cv.put("STARRED", 1);

        			String display = values.get("pattern");
        			if (HANDS_CUSTOM_DISPLAY != null) {
        				display += " " + HANDS_CUSTOM_DISPLAY;
        			} else {
        				String[] hand_movements = context.getResources().getStringArray(R.array.hand_movement);
        				if (HANDS_XML_LINE_NUMBER > 0 && HANDS_XML_LINE_NUMBER < hand_movements.length) {
            				display += " " + context.getResources().getStringArray(R.array.hand_movement)[HANDS_XML_LINE_NUMBER];
        				}
        			}
        			cv.put("CUSTOM_DISPLAY", display);
        			
        			myDbHelper.getWritableDatabase().insert("Trick", null, cv);
    			}

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

        	if (cursor != null) cursor.close();

            myDbHelper.close();
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
