// VelocityRef.java
//
// Copyright 2004 by Jack Boyce (jboyce@users.sourceforge.net) and others

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

package com.jonglen7.jugglinglab.jugglinglab.jml;

import com.jonglen7.jugglinglab.jugglinglab.path.Path;
import com.jonglen7.jugglinglab.jugglinglab.util.Coordinate;


public class VelocityRef {
    protected Path	pp;
    protected boolean	start;

    public VelocityRef(Path pp, boolean start) {
        this.pp = pp;
        this.start = start;
    }

    public Coordinate getVelocity() {
        if (start)
            return pp.getStartVelocity();
        else
            return pp.getEndVelocity();
    }
}
