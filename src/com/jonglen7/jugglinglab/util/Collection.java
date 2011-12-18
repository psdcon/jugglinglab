package com.jonglen7.jugglinglab.util;

import android.os.Parcel;
import android.os.Parcelable;

public class Collection implements Parcelable {
	
	private String table;
	private int ID_COLLECTION;
	private String display;
	
	public Collection(String table, int ID_COLLECTION, String display) {
		this.table = table;
		this.ID_COLLECTION = ID_COLLECTION;
		this.display = display;
	}

	public Collection(Parcel in) {
		readFromParcel(in);
	}

	private void readFromParcel(Parcel in) {
		table = in.readString();
		ID_COLLECTION = in.readInt();
		display = in.readString();
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(table);
		dest.writeInt(ID_COLLECTION);
		dest.writeString(display);
	}

	public String getTable() {
		return table;
	}

	public int getID_COLLECTION() {
		return ID_COLLECTION;
	}

	public String getDisplay() {
		return display;
	}
	
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public Collection createFromParcel(Parcel in) {
           	return new Collection(in);
		}
 
		public Collection[] newArray(int size) {
			return new Collection[size];
		}
	};

}
