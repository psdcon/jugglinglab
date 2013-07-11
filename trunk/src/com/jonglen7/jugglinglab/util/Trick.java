package com.jonglen7.jugglinglab.util;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.jonglen7.jugglinglab.R;
import com.jonglen7.jugglinglab.jugglinglab.core.PatternRecord;


public class Trick {

	private HashMap<String, String> pattern_record_values;
	private Context context;
	private int ID_TRICK;
    private ArrayList<Collection> collections;
	private String CUSTOM_DISPLAY;
	private Collection starredCollection;

	public Trick(PatternRecord pattern_record, Context context) {
		this.pattern_record_values = PatternRecord.animToValues(pattern_record.getAnim());
		this.context = context;
		this.collections = new ArrayList<Collection>();
		DataBaseHelper myDbHelper = DataBaseHelper.init(context);

		Cursor cursor = null;
		String query = "SELECT T.ID_TRICK, T.XML_LINE_NUMBER, T.CUSTOM_DISPLAY " +
					"FROM Trick T, Hands H, Body B, Prop P " +
					"WHERE T.ID_HANDS = H.ID_HANDS " +
					"AND T.ID_BODY = B.ID_BODY " +
					"AND T.ID_PROP = P.ID_PROP " +
					"AND T.PATTERN = '" + pattern_record_values.get("pattern") + "'";
		query += (pattern_record_values.get("hands").length() > 0) ? (" AND H.CODE = '" + pattern_record_values.get("hands") + "'") : " AND H.XML_LINE_NUMBER=0";
		query += (pattern_record_values.get("body").length() > 0) ? (" AND B.CODE = '" + pattern_record_values.get("body") + "'") : " AND B.XML_LINE_NUMBER=0";
		query += (pattern_record_values.get("prop").length() > 0) ? (" AND P.CODE = '" + pattern_record_values.get("prop") + "'") : " AND P.XML_LINE_NUMBER=0";
		cursor = myDbHelper.execQuery(query);
		cursor.moveToFirst();

		if (!cursor.isAfterLast()) {
	    	String[] trick = context.getResources().getStringArray(R.array.trick);
			this.ID_TRICK = cursor.getInt(cursor.getColumnIndex("ID_TRICK"));
			this.CUSTOM_DISPLAY = (cursor.getString(cursor.getColumnIndex("CUSTOM_DISPLAY")) != null ? cursor.getString(cursor.getColumnIndex("CUSTOM_DISPLAY")) : trick[cursor.getInt(cursor.getColumnIndex("XML_LINE_NUMBER"))]);

	    	cursor.close();
			query = "SELECT C.ID_COLLECTION AS ID_COLLECTION, IS_TUTORIAL, XML_LINE_NUMBER, CUSTOM_DISPLAY " +
					"FROM TrickCollection TC, Collection C " +
					"WHERE TC.ID_TRICK=" + this.ID_TRICK + " " +
					"AND TC.ID_COLLECTION = C.ID_COLLECTION";
			cursor = myDbHelper.execQuery(query);
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				this.collections.add(new Collection(cursor, context));
	            cursor.moveToNext();
			}
		} else {
			this.ID_TRICK = -1;
			this.CUSTOM_DISPLAY = pattern_record.getDisplay();
		}

    	if (cursor != null) cursor.close();
		
		query = "SELECT ID_COLLECTION, IS_TUTORIAL, XML_LINE_NUMBER, CUSTOM_DISPLAY " +
				"FROM Collection " +
				"WHERE XML_LINE_NUMBER=" + Collection.STARRED_XML_LINE_NUMBER;
		cursor = myDbHelper.execQuery(query);
		cursor.moveToFirst();
		this.starredCollection = new Collection(cursor, context);
		cursor.close();

        myDbHelper.close();
	}

    public int getID_TRICK() {
        return ID_TRICK;
    }

	public String getCUSTOM_DISPLAY() {
		return CUSTOM_DISPLAY;
	}
	
	public ArrayList<Collection> getCollections() {
		return collections;
	}
	
	private void insertInDB() {
    	String query;
    	Cursor cursor = null;
    	ContentValues cv;
		DataBaseHelper myDbHelper = DataBaseHelper.init(this.context);
		
		// Hands
		int ID_HANDS;
		String HANDS_CUSTOM_DISPLAY = null;
		int HANDS_XML_LINE_NUMBER = -1;
		query = "SELECT ID_HANDS, XML_LINE_NUMBER, CUSTOM_DISPLAY " +
				"FROM Hands " +
				"WHERE ";
		query += (pattern_record_values.get("hands").length() > 0) ? ("CODE = '" + pattern_record_values.get("hands") + "'") : "XML_LINE_NUMBER=0";
		cursor = myDbHelper.execQuery(query);
		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			ID_HANDS = cursor.getInt(cursor.getColumnIndex("ID_HANDS"));
			HANDS_CUSTOM_DISPLAY = cursor.getString(cursor.getColumnIndex("CUSTOM_DISPLAY"));
			HANDS_XML_LINE_NUMBER = cursor.getInt(cursor.getColumnIndex("XML_LINE_NUMBER"));
		} else {
			cv = new ContentValues();
			cv.put("CODE", pattern_record_values.get("hands"));
			ID_HANDS = (int) myDbHelper.getWritableDatabase().insert("Hands", null, cv);
		}
    	if (cursor != null) cursor.close();

		// Body
		int ID_BODY;
		query = "SELECT ID_BODY " +
				"FROM Body " +
				"WHERE ";
		query += (pattern_record_values.get("body").length() > 0) ? ("CODE = '" + pattern_record_values.get("body") + "'") : "XML_LINE_NUMBER=0";
		cursor = myDbHelper.execQuery(query);
		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			ID_BODY = cursor.getInt(cursor.getColumnIndex("ID_BODY"));
		} else {
			cv = new ContentValues();
			cv.put("CODE", pattern_record_values.get("body"));
			ID_BODY = (int) myDbHelper.getWritableDatabase().insert("Body", null, cv);
		}
    	if (cursor != null) cursor.close();

		// Prop
		int ID_PROP;
		query = "SELECT ID_PROP " +
				"FROM Prop " +
				"WHERE ";
		query += (pattern_record_values.get("prop").length() > 0) ? ("CODE = '" + pattern_record_values.get("prop") + "'") : "XML_LINE_NUMBER=0";
		cursor = myDbHelper.execQuery(query);
		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			ID_PROP = cursor.getInt(cursor.getColumnIndex("ID_PROP"));
		} else {
			cv = new ContentValues();
			cv.put("CODE", pattern_record_values.get("prop"));
			ID_PROP = (int) myDbHelper.getWritableDatabase().insert("Prop", null, cv);
		}

		// Trick
		cv = new ContentValues();
		cv.put("PATTERN", pattern_record_values.get("pattern"));
		cv.put("ID_HANDS", ID_HANDS);
		cv.put("ID_BODY", ID_BODY);
		cv.put("ID_PROP", ID_PROP);

		String display;
		String pattern = pattern_record_values.get("pattern");
		// If the user gave a name to the trick, use that name
		// TODO Romain (Trick): Maybe find a cleaner way to do that check
		if (this.CUSTOM_DISPLAY != pattern) {
		    display = this.CUSTOM_DISPLAY;
		} else {
    		display = pattern;
    		if (HANDS_CUSTOM_DISPLAY != null) {
    			display += " " + HANDS_CUSTOM_DISPLAY;
    		} else {
    			String[] hand_movements = this.context.getResources().getStringArray(R.array.hand_movement);
    			if (HANDS_XML_LINE_NUMBER > 0 && HANDS_XML_LINE_NUMBER < hand_movements.length) {
    				display += " " + this.context.getResources().getStringArray(R.array.hand_movement)[HANDS_XML_LINE_NUMBER];
    			}
    		}
		}
		cv.put("CUSTOM_DISPLAY", display);
		
		
		this.ID_TRICK = (int) myDbHelper.getWritableDatabase().insert("Trick", null, cv);
		this.CUSTOM_DISPLAY = display;

    	if (cursor != null) cursor.close();

        myDbHelper.close();
	}
	
	public void star() {
		updateCollection(starredCollection);
	}

	public void edit(String CUSTOM_DISPLAY) {
		this.CUSTOM_DISPLAY = CUSTOM_DISPLAY;
		if (this.ID_TRICK < 0) insertInDB();
		ContentValues cv = new ContentValues();
		cv.put("CUSTOM_DISPLAY", this.CUSTOM_DISPLAY);
		DataBaseHelper myDbHelper = DataBaseHelper.init(this.context);
		myDbHelper.getWritableDatabase().update("Trick", cv, "ID_TRICK=" + ID_TRICK, null);
        myDbHelper.close();
	}
	
	public void updateCollection(Collection collection) {
		if (this.ID_TRICK < 0) insertInDB();
		DataBaseHelper myDbHelper = DataBaseHelper.init(this.context);

		String query = "SELECT * " +
						"FROM TrickCollection " +
						"WHERE ID_COLLECTION=" + collection.getID_COLLECTION();
		Cursor cursor = myDbHelper.execQuery(query);
	 	cursor.moveToFirst();
        cursor.close();
        
        int index = collection.indexOf(collections);
		if (index >= 0) {
			collections.remove(index);
			myDbHelper.getWritableDatabase().delete("TrickCollection", "ID_TRICK=" + this.ID_TRICK + " AND ID_COLLECTION=" + collection.getID_COLLECTION(), null);
		} else{
			collections.add(collection);
			ContentValues cv = new ContentValues();
			cv.put("ID_TRICK", this.ID_TRICK);
			cv.put("ID_COLLECTION", collection.getID_COLLECTION());
			myDbHelper.getWritableDatabase().insert("TrickCollection", null, cv);
		}
		
        myDbHelper.close();
	}

	public void delete() {
		DataBaseHelper myDbHelper = DataBaseHelper.init(this.context);
		myDbHelper.getWritableDatabase().delete("Trick", "ID_TRICK=" + this.ID_TRICK, null);
		// TODO Romain (Trick): Update the step of the tricks that are "below" that trick in each collection the trick is in
		myDbHelper.getWritableDatabase().delete("TrickCollection", "ID_TRICK=" + this.ID_TRICK, null);
        myDbHelper.close();
	}
	
}
