package com.jonglen7.jugglinglab.jugglinglab.core;

import android.os.Parcel;
import android.os.Parcelable;

public class PatternRecord implements Parcelable {
	// TODO Fred: à lire http://www.tutos-android.com/parcelable-android
	private String display;
	
	// TODO Fred : "il faudra le buter si on l'implemente pas"
    private String animprefs;

	private String notation;
    private String anim;

    public PatternRecord(String dis, String ap, String not, String ani) {
        this.display = dis;
        this.animprefs = ap;
        this.notation = not;
        this.anim = ani;
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
