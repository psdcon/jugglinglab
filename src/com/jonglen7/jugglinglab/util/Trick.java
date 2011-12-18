package com.jonglen7.jugglinglab.util;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.jonglen7.jugglinglab.R;


public class Trick {

	private HashMap<String, String> pattern_record_values;
	private Context context;
	private int ID_TRICK;
	private int STARRED;
	private String CUSTOM_DISPLAY;
	
	public Trick(HashMap<String, String> pattern_record_values, Context context) {
		this.pattern_record_values = pattern_record_values;
		this.context = context;
		DataBaseHelper myDbHelper = DataBaseHelper.init(context);

		Cursor cursor = null;
		String query = "SELECT T.ID_TRICK, T.STARRED, T.CUSTOM_DISPLAY " +
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
			this.ID_TRICK = cursor.getInt(cursor.getColumnIndex("ID_TRICK"));
			this.STARRED = cursor.getInt(cursor.getColumnIndex("STARRED"));
			this.CUSTOM_DISPLAY = cursor.getString(cursor.getColumnIndex("CUSTOM_DISPLAY"));
		} else {
			this.ID_TRICK = -1;
			this.STARRED = 0;
			this.CUSTOM_DISPLAY = "";
		}

    	if (cursor != null) cursor.close();

        myDbHelper.close();
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

		String display = pattern_record_values.get("pattern");
		if (HANDS_CUSTOM_DISPLAY != null) {
			display += " " + HANDS_CUSTOM_DISPLAY;
		} else {
			String[] hand_movements = this.context.getResources().getStringArray(R.array.hand_movement);
			if (HANDS_XML_LINE_NUMBER > 0 && HANDS_XML_LINE_NUMBER < hand_movements.length) {
				display += " " + this.context.getResources().getStringArray(R.array.hand_movement)[HANDS_XML_LINE_NUMBER];
			}
		}
		cv.put("CUSTOM_DISPLAY", display);
		
		
		this.ID_TRICK = (int) myDbHelper.getWritableDatabase().insert("Trick", null, cv);
		this.STARRED = 0;
		this.CUSTOM_DISPLAY = display;

    	if (cursor != null) cursor.close();

        myDbHelper.close();
	}
	
	public void star() {
		if (this.ID_TRICK < 0) insertInDB();
		ContentValues cv = new ContentValues();
		cv.put("STARRED", 1 - this.STARRED);
		DataBaseHelper myDbHelper = DataBaseHelper.init(this.context);
		myDbHelper.getWritableDatabase().update("Trick", cv, "ID_TRICK=" + ID_TRICK, null);
        myDbHelper.close();
	}

	public int getSTARRED() {
		return STARRED;
	}
	
}
