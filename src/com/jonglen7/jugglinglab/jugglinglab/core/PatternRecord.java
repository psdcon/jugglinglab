package com.jonglen7.jugglinglab.jugglinglab.core;

import java.util.HashMap;
import java.util.Iterator;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

public class PatternRecord implements Parcelable {

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
	    // TODO Romain (PatternRecord): Clean that part (quick and dirty)
		HashMap<String, String> values = new HashMap<String, String>();
        values.put("pattern", "");
        values.put("hands", "");
        values.put("body", "");
        values.put("prop", "");
        values.put("dwell", "");
        values.put("bps", "");
        String[] anim_splitted = anim.split(";");
        if (anim_splitted.length == 1 && anim_splitted[0].split("=").length == 1)
            values.put("pattern", anim_splitted[0]);
        else {
    		for (String attr: anim_splitted) {
    		    String[] val = attr.split("=");
    		    if (val.length == 2 && val[1].trim().length() > 0)
    		        values.put(val[0], val[1].trim());
    		}
        }
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
		// Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(display);
		dest.writeString(animprefs);
		dest.writeString(notation);
		dest.writeString(anim);
	}
	
	public static final Parcelable.Creator<PatternRecord> CREATOR = new Parcelable.Creator<PatternRecord>() {
		public PatternRecord createFromParcel(Parcel in) {
           	return new PatternRecord(in);
		}
 
		public PatternRecord[] newArray(int size) {
			return new PatternRecord[size];
		}
	};
}
