package com.jonglen7.jugglinglab.util;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.jonglen7.jugglinglab.R;

public class Collection implements Parcelable {
	
	public static final int STARRED_XML_LINE_NUMBER = 23;
	private static final String table = "Collection";
	
	private int IS_TUTORIAL;
	private int ID_COLLECTION;
	private String CUSTOM_DISPLAY;
	private boolean isStarred;
	private Context context;

	public Collection(Cursor cursor, Context context) {
    	this.context = context;
		this.ID_COLLECTION = cursor.getInt(cursor.getColumnIndex("ID_COLLECTION"));
		this.IS_TUTORIAL = cursor.getInt(cursor.getColumnIndex("IS_TUTORIAL"));
		int XML_LINE_NUMBER = cursor.getInt(cursor.getColumnIndex("XML_LINE_NUMBER"));
		this.isStarred = XML_LINE_NUMBER == STARRED_XML_LINE_NUMBER;
    	this.CUSTOM_DISPLAY = (cursor.getString(cursor.getColumnIndex("CUSTOM_DISPLAY")) != null ? cursor.getString(cursor.getColumnIndex("CUSTOM_DISPLAY")) : context.getResources().getStringArray(R.array.collection)[XML_LINE_NUMBER]);
	}
	
	public Collection(int IS_TUTORIAL, Context context) {
    	this.context = context;
    	this.IS_TUTORIAL = IS_TUTORIAL;
		this.ID_COLLECTION = -1;
		this.isStarred = false;
		this.CUSTOM_DISPLAY = "";
	}

	public Collection(Parcel in) {
		readFromParcel(in);
	}

	private void readFromParcel(Parcel in) {
		IS_TUTORIAL = in.readInt();
		ID_COLLECTION = in.readInt();
		CUSTOM_DISPLAY = in.readString();
		isStarred = in.readInt() != 0;
	}

	@Override
	public int describeContents() {
		// Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(IS_TUTORIAL);
		dest.writeInt(ID_COLLECTION);
		dest.writeString(CUSTOM_DISPLAY);
		dest.writeInt(isStarred ? 1 : 0);
	}

	public int getIS_TUTORIAL() {
		return IS_TUTORIAL;
	}

	public int getID_COLLECTION() {
		return ID_COLLECTION;
	}

	public String getCUSTOM_DISPLAY() {
		return CUSTOM_DISPLAY;
	}
	
	public boolean isStarred() {
		return isStarred;
	}
	
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public Collection createFromParcel(Parcel in) {
           	return new Collection(in);
		}
 
		public Collection[] newArray(int size) {
			return new Collection[size];
		}
	};

	public int indexOf(ArrayList<Collection> collections) {
		int index = -1, i = 0;
		while (index < 0 && i < collections.size()) {
			if (this.ID_COLLECTION == collections.get(i).getID_COLLECTION()) index = i;
			i++;
		}
		return index;
	}
	
	private void insertInDB() {
		DataBaseHelper myDbHelper = DataBaseHelper.init(this.context);
		ContentValues cv = new ContentValues();
		cv.put("IS_TUTORIAL", this.IS_TUTORIAL);
		cv.put("CUSTOM_DISPLAY", this.CUSTOM_DISPLAY);
		this.ID_COLLECTION = (int) myDbHelper.getWritableDatabase().insert("Collection", null, cv);
		myDbHelper.close();
	}

	public void edit(String CUSTOM_DISPLAY) {
		this.CUSTOM_DISPLAY = CUSTOM_DISPLAY;
		if (this.ID_COLLECTION < 0) insertInDB();
		ContentValues cv = new ContentValues();
		cv.put("CUSTOM_DISPLAY", this.CUSTOM_DISPLAY);
		DataBaseHelper myDbHelper = DataBaseHelper.init(this.context);
		myDbHelper.getWritableDatabase().update(table, cv, "ID_COLLECTION=" + this.ID_COLLECTION, null);
        myDbHelper.close();
	}

	public void delete() {
		DataBaseHelper myDbHelper = DataBaseHelper.init(this.context);
		myDbHelper.getWritableDatabase().delete(table, "ID_COLLECTION=" + this.ID_COLLECTION, null);
		myDbHelper.getWritableDatabase().delete("TrickCollection", "ID_COLLECTION=" + this.ID_COLLECTION, null);
        myDbHelper.close();
	}

}
