package com.jonglen7.jugglinglab.jugglinglab.core;

import java.util.HashMap;
import java.util.Iterator;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

public class PatternRecord implements Parcelable {
	// TODO Fred: à lire http://www.tutos-android.com/parcelable-android

	private String display;
    private String animprefs;
	private String notation;
    private String anim;
    
    public PatternRecord(String dis, String ap, String not, String ani) {
        this.animprefs = ap;
        this.notation = not;
        this.anim = minimizeAnim(ani);
        this.display = dis.replace(animToValues(ani).get("pattern"), animToValues(this.anim).get("pattern"));
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
	
	private static String minimizeAnim(String anim) {
		HashMap<String, String> values = animToValues(anim);
		String pattern = values.get("pattern");
		String pattern_minimized;
		boolean minimized = false;
		int i = 1, j;
		while (!minimized && i <= pattern.length() / 2) {
			if (pattern.length() % i == 0) {
				pattern_minimized = pattern.substring(0, i);
				j = 0;
				while (j + i <= pattern.length() && pattern.substring(j, j + i).compareTo(pattern_minimized) == 0) j += i;
				if (j + i > pattern.length()) {
					minimized = true;
					values.put("pattern", pattern_minimized);
				}
			}
			i += 1;
		}
		return ValuesToAnim(values);
	}
	
	public static HashMap<String, String> animToValues(String anim) {
		HashMap<String, String> values = new HashMap<String, String>();
		String pattern = anim, hands = "", body = "", prop = "", dwell = "", bps = "";
		int patternIndex, handsIndex, bodyIndex, propIndex, dwellIndex, bpsIndex;
		String patternRegExp = "pattern=", handsRegExp = "hands=", bodyRegExp = "body=", propRegExp = "prop=", dwellRegExp = "dwell=", bpsRegExp = "bps=";
		int end;
		
		patternIndex = anim.indexOf(patternRegExp);
		if (patternIndex >= 0) {
			end = anim.indexOf(";", patternIndex + patternRegExp.length());
			if (end < 0) end = anim.length();
			pattern = anim.substring(patternIndex + patternRegExp.length(), end).trim();
		}
		
		handsIndex = anim.indexOf(handsRegExp);
		if (handsIndex >= 0) {
			end = anim.indexOf(";", handsIndex + handsRegExp.length());
			if (end < 0) end = anim.length();
			hands = anim.substring(handsIndex + handsRegExp.length(), end).trim();
		}
		
		bodyIndex = anim.indexOf(bodyRegExp);
		if (bodyIndex >= 0) {
			end = anim.indexOf(";", bodyIndex + bodyRegExp.length());
			if (end < 0) end = anim.length();
			body = anim.substring(bodyIndex + bodyRegExp.length(), end).trim();
		}
		
		propIndex = anim.indexOf(propRegExp);
		if (propIndex >= 0) {
			end = anim.indexOf(";", propIndex + propRegExp.length());
			if (end < 0) end = anim.length();
			prop = anim.substring(propIndex + propRegExp.length(), end).trim();
		}
		
		dwellIndex = anim.indexOf(dwellRegExp);
		if (dwellIndex >= 0) {
			end = anim.indexOf(";", dwellIndex + dwellRegExp.length());
			if (end < 0) end = anim.length();
			dwell = anim.substring(dwellIndex + dwellRegExp.length(), end).trim();
		}
		
		bpsIndex = anim.indexOf(bpsRegExp);
		if (bpsIndex >= 0) {
			end = anim.indexOf(";", bpsIndex + bpsRegExp.length());
			if (end < 0) end = anim.length();
			bps = anim.substring(bpsIndex + bpsRegExp.length(), end).trim();
		}
		
		values.put("pattern", pattern);
		values.put("hands", hands);
		values.put("body", body);
		values.put("prop", prop);
		values.put("dwell", dwell);
		values.put("bps", bps);
		return values;
	}
    
	public static String ValuesToAnim(HashMap<String, String> values) {
		String anim = "";
		Iterator<String> it = values.keySet().iterator();
		while (it.hasNext()){
		   String key = it.next();
		   String value = values.get(key);
		   anim += key + "=" + value + ";";
		}
		return anim;
	}
	
    public String getDisplay() {
		return display;
	}

    public void setDisplay(String display) {
		this.display = display;
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
