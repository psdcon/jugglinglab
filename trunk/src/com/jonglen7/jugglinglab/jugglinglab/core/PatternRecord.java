package com.jonglen7.jugglinglab.jugglinglab.core;

public class PatternRecord {
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
}
