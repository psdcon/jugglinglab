package com.jonglen7.jugglinglab.jugglinglab.core;

import java.util.HashMap;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

public class PatternRecord implements Parcelable {
	// TODO Fred: à lire http://www.tutos-android.com/parcelable-android
	
	// TODO Fred : "il faudra le buter si on l'implemente pas"
    private String animprefs;

	private String display;
	private String notation;
    private String anim;
    
    public PatternRecord(String dis, String ap, String not, String ani) {
        this.display = dis;
        this.animprefs = ap;
        this.notation = not;
        this.anim = ani;
    }

    public PatternRecord(String dis, String ap, String not, Cursor cursor) {
        this(dis, ap, not, createAnimFromCursor(cursor));
    }

	private static String createAnimFromCursor(Cursor cursor) {
		String anim = "";
		anim += "pattern=" + cursor.getString(cursor.getColumnIndex("PATTERN"));
		anim += ";hands=" + cursor.getString(cursor.getColumnIndex("HANDS"));
		anim += ";body=" + cursor.getString(cursor.getColumnIndex("BODY"));
		anim += ";prop=" + cursor.getString(cursor.getColumnIndex("PROP"));
		return anim;
	}
	
	public HashMap<String, String> getValuesFromAnim() {
		HashMap<String, String> values = new HashMap<String, String>();
		String pattern = this.anim, hands = "", body = "", prop = "", dwell = "", bps = "";
		int patternIndex, handsIndex, bodyIndex, propIndex, dwellIndex, bpsIndex;
		String patternRegExp = "pattern=", handsRegExp = "hands=", bodyRegExp = "body=", propRegExp = "prop=", dwellRegExp = "dwell=", bpsRegExp = "bps=";
		int end;
		
		patternIndex = this.anim.indexOf(patternRegExp);
		if (patternIndex >= 0) {
			end = this.anim.indexOf(";", patternIndex + patternRegExp.length());
			if (end < 0) end = this.anim.length();
			pattern = this.anim.substring(patternIndex + patternRegExp.length(), end).trim();
		}
		
		handsIndex = this.anim.indexOf(handsRegExp);
		if (handsIndex >= 0) {
			end = this.anim.indexOf(";", handsIndex + handsRegExp.length());
			if (end < 0) end = this.anim.length();
			hands = this.anim.substring(handsIndex + handsRegExp.length(), end).trim();
		}
		
		bodyIndex = this.anim.indexOf(bodyRegExp);
		if (bodyIndex >= 0) {
			end = this.anim.indexOf(";", bodyIndex + bodyRegExp.length());
			if (end < 0) end = this.anim.length();
			body = this.anim.substring(bodyIndex + bodyRegExp.length(), end).trim();
		}
		
		propIndex = this.anim.indexOf(propRegExp);
		if (propIndex >= 0) {
			end = this.anim.indexOf(";", propIndex + propRegExp.length());
			if (end < 0) end = this.anim.length();
			prop = this.anim.substring(propIndex + propRegExp.length(), end).trim();
		}
		
		dwellIndex = this.anim.indexOf(dwellRegExp);
		if (dwellIndex >= 0) {
			end = this.anim.indexOf(";", dwellIndex + dwellRegExp.length());
			if (end < 0) end = this.anim.length();
			dwell = this.anim.substring(dwellIndex + dwellRegExp.length(), end).trim();
		}
		
		bpsIndex = this.anim.indexOf(bpsRegExp);
		if (bpsIndex >= 0) {
			end = this.anim.indexOf(";", bpsIndex + bpsRegExp.length());
			if (end < 0) end = this.anim.length();
			bps = this.anim.substring(bpsIndex + bpsRegExp.length(), end).trim();
		}
		
		values.put("pattern", pattern);
		values.put("hands", hands);
		values.put("body", body);
		values.put("prop", prop);
		values.put("dwell", dwell);
		values.put("bps", bps);
		return values;
	}
    
    public String getDisplay() {
		return display;
	}

	public String getNotation() {
		return notation;
	}

	public String getAnim() {
		return anim;
	}
	
	public PatternRecord(Parcel in) {
		readFromParcel(in);
	}

	private void readFromParcel(Parcel in) {
		display = in.readString();
		animprefs = in.readString();
		notation = in.readString();
		anim = in.readString();
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(display);
		dest.writeString(animprefs);
		dest.writeString(notation);
		dest.writeString(anim);
	}
	
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public PatternRecord createFromParcel(Parcel in) {
           	return new PatternRecord(in);
		}
 
		public PatternRecord[] newArray(int size) {
			return new PatternRecord[size];
		}
	};
}
