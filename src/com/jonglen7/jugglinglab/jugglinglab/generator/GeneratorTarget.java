// GeneratorTarget.java
//
// Copyright 2003 by Jack Boyce (jboyce@users.sourceforge.net) and others

/*
    This file is part of Juggling Lab.

    Juggling Lab is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    Juggling Lab is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Juggling Lab; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package com.jonglen7.jugglinglab.jugglinglab.generator;

import java.util.ArrayList;

import com.jonglen7.jugglinglab.jugglinglab.core.PatternRecord;


// This is used as an adapter to handle the generator output
public class GeneratorTarget {
	ArrayList<PatternRecord> pattern_list;

    public ArrayList<PatternRecord> getPattern_list() {
		return pattern_list;
	}

	public GeneratorTarget() {
    	pattern_list = new ArrayList<PatternRecord>();
    }

    public void writePattern(final String display, final String notation, final String anim) {
    	pattern_list.add(new PatternRecord(display, "", notation, anim));
    }
    
    public void clearPatternList(){
    	pattern_list.clear();
    }
}