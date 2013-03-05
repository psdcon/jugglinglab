// Prop.java
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

package com.jonglen7.jugglinglab.jugglinglab.prop;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.microedition.khronos.opengles.GL10;

import com.jonglen7.jugglinglab.jugglinglab.util.Coordinate;
import com.jonglen7.jugglinglab.jugglinglab.util.JuggleExceptionUser;
import com.jonglen7.jugglinglab.jugglinglab.util.ParameterDescriptor;


public abstract class Prop {
    // static ResourceBundle guistrings;
    static ResourceBundle errorstrings;
    static {
        // guistrings = ResourceBundle.getBundle("GUIStrings");
        errorstrings = ResourceBundle.getBundle("com/jonglen7/jugglinglab/resources/ErrorStrings");
    }
    
    protected String initString;
    public static final String[] builtinProps = { "Ball", "Image", "Ring", "Cube", "Club" };

    public static Prop getProp(String name) throws JuggleExceptionUser {
        try {
            Object obj = Class.forName("com.jonglen7.jugglinglab.jugglinglab.prop."+name.toLowerCase(Locale.US)+"Prop").newInstance();
            if (!(obj instanceof Prop))
                throw new JuggleExceptionUser("Prop type '"+name+"' doesn't work");
            return (Prop)obj;
        }
        catch (ClassNotFoundException cnfe) {
            throw new JuggleExceptionUser("Prop type '"+name+"' not found");
        }
        catch (IllegalAccessException iae) {
            throw new JuggleExceptionUser("Cannot access '"+name+"' prop file (security)");
        }
        catch (InstantiationException ie) {
            throw new JuggleExceptionUser("Couldn't create '"+name+"' prop");
        }
    }
    
    public void initProp(String st) throws JuggleExceptionUser {
        initString = st;
        this.init(st);
    }

    protected abstract void init(String st) throws JuggleExceptionUser;
    
    public abstract String getName();
    public abstract ParameterDescriptor[] getParameterDescriptors();
    public abstract Coordinate getMax();
    public abstract Coordinate getMin();
    
    public abstract Coordinate getSize();
    public abstract void setSize(Coordinate size);
    public abstract Coordinate getCenter();
    public abstract void setCenter(Coordinate center);
    public abstract int getColor();
    public abstract void setColor(int color);
    public abstract double getDiam();
	public abstract void setDiam(double diam);
    
    
    public abstract void centerProp();  
    public abstract void draw(GL10 gl);
}
