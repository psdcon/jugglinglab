// ballProp.java
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

import java.util.*;
import java.awt.*;
import java.lang.reflect.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Color;

import com.jonglen7.jugglinglab.jugglinglab.core.*;
import com.jonglen7.jugglinglab.jugglinglab.util.*;
import com.jonglen7.jugglinglab.jugglinglab.renderer.*;


public class ballProp extends Prop {
    static String[] colornames = {"black", "blue", "cyan", "gray",
        "green", "magenta", "red", "white", "yellow"};
    static int[] colorvals = {Color.BLACK, Color.BLUE, Color.CYAN, Color.GRAY, 
    	Color.GREEN, Color.MAGENTA, Color.RED, Color.WHITE, Color.YELLOW};

    protected static final int 		colornum_def 	= 8;		// red
    protected static final double 	diam_def 		= 10.0;		// in cm
    protected static final boolean 	highlight_def 	= false;

    protected double 	diam 		= diam_def;		// diameter, in cm
    protected int		color 		= colornum_def;
    protected boolean	highlight 	= highlight_def;
    
    protected int	ball_pixel_size = 1;

    protected double 	lastzoom = 0.0;
    
    protected Coordinate size = null;
    protected Coordinate center = null;
	protected Coordinate grip = null;

    // Constructor To be delete or adapted
    public ballProp(){
    	
        size = new Coordinate(ball_pixel_size, ball_pixel_size);
        center = new Coordinate();
        grip = new Coordinate();
        // Draw Initialization
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);

        mIndexBuffer = ByteBuffer.allocateDirect(indices.length);
        mIndexBuffer.put(indices);
        mIndexBuffer.position(0);
    }
    
    
    
    public String getName() {
        return "Ball";
    }

    public int getEditorColor() {
        return color;
    }

    public ParameterDescriptor[] getParameterDescriptors() {
        ParameterDescriptor[] result = new ParameterDescriptor[3];

        Vector<String> range = new Vector<String>();
        for (int i = 0; i < colornames.length; i++)
            range.add(colornames[i]);

        result[0] = new ParameterDescriptor("color", ParameterDescriptor.TYPE_CHOICE,
                                            range, colornames[colornum_def], colornames[color]);
        result[1] = new ParameterDescriptor("diam", ParameterDescriptor.TYPE_FLOAT,
                                            null, new Double(diam_def), new Double(diam));
        result[2] = new ParameterDescriptor("highlight", ParameterDescriptor.TYPE_BOOLEAN,
                                            null, new Boolean(highlight_def), new Boolean(highlight));

        return result;
    }

    protected void init(String st) throws JuggleExceptionUser {
        color = colornum_def;

        if (st == null) return;
        ParameterList pl = new ParameterList(st);

        String colorstr = pl.getParameter("color");
        if (colorstr != null) {
            int temp = -1;
            if (colorstr.indexOf((int)',') == -1) { // color name
                temp = Color.parseColor(colorstr);
            } else {								// RGB triplet
                // delete the '{' and '}' characters first
                String str = colorstr;
                int pos;
                while ((pos = str.indexOf('{')) >= 0) {
                    str = str.substring(0,pos) + str.substring(pos+1,str.length());
                }
                while ((pos = str.indexOf('}')) >= 0) {
                    str = str.substring(0,pos) + str.substring(pos+1,str.length());
                }
                int red = 0, green = 0, blue = 0;
                StringTokenizer st2 = new StringTokenizer(str, ",", false);
                if (st2.hasMoreTokens())
                    red = Integer.valueOf(st2.nextToken()).intValue();
                if (st2.hasMoreTokens())
                    green = Integer.valueOf(st2.nextToken()).intValue();
                if (st2.hasMoreTokens())
                    blue = Integer.valueOf(st2.nextToken()).intValue();
                temp = Color.rgb(red, green, blue);
            }

            if (temp != -1)
                color = temp;
            else
                throw new JuggleExceptionUser(errorstrings.getString("Error_prop_color")+": '"+colorstr+"'");
        }

        String diamstr = pl.getParameter("diam");
        if (diamstr != null) {
            try {
                Double ddiam = Double.valueOf(diamstr);
                double temp = ddiam.doubleValue();
                if (temp > 0.0)
                    diam = temp;
                else
                    throw new JuggleExceptionUser(errorstrings.getString("Error_prop_diameter"));
            } catch (NumberFormatException nfe) {
                throw new JuggleExceptionUser(errorstrings.getString("Error_number_format_prefix")+" 'diam' "+
                                              errorstrings.getString("Error_number_format_suffix"));
            }
        }

        String highlightstr = pl.getParameter("highlight");
        if (highlightstr != null) {
            Boolean bhighlight = Boolean.valueOf(highlightstr);
            highlight = bhighlight.booleanValue();
        }
        
    }

    public Coordinate getMax() {
        return new Coordinate(diam/2,0,diam);
    }

    public Coordinate getMin() {
        return new Coordinate(-diam/2,0,0);
    }

    public Coordinate getPropSize() {
        return size;
    }

	public Coordinate getPropCenter() {
		return center;
	}
	
    public void setPropCenter(Coordinate center) {
		this.center = center;
	}
	
    public Coordinate getPropGrip() {
        return grip;
    }

    protected void init(double zoom) {
    	
        ball_pixel_size = (int)(0.5 + zoom * diam);
        
        if (ball_pixel_size < 1)
            ball_pixel_size = 1;

        size = new Coordinate(ball_pixel_size, ball_pixel_size);
        //center = new Coordinate(ball_pixel_size/2, ball_pixel_size/2);
        //grip = new Coordinate(ball_pixel_size/2, ball_pixel_size);

        lastzoom = zoom;
    }
    

    float one = 3.0f;
    //float one = 3.0f*one_;
    private float cubeVertices[] = {
	            -one, -one, -one,
	            one, -one, -one,
	            one,  one, -one,
	            -one,  one, -one,
	            -one, -one,  one,
	            one, -one,  one,
	            one,  one,  one,
	            -one,  one,  one,
	    };
    private float vertices[] = {
            -one, -one, -one,
            one, -one, -one,
            one,  one, -one,
            -one,  one, -one,
            -one, -one,  one,
            one, -one,  one,
            one,  one,  one,
            -one,  one,  one,
    };

    byte indices[] = {
            0, 4, 5,    0, 5, 1,
            1, 5, 6,    1, 6, 2,
            2, 6, 7,    2, 7, 3,
            3, 7, 4,    3, 4, 0,
            4, 7, 6,    4, 6, 5,
            3, 0, 1,    3, 1, 2
    };
    
    private FloatBuffer   mVertexBuffer;
    private ByteBuffer  mIndexBuffer;
    
    public void centerProp() {
    	for (int i= 0; i<8; i++){
    		vertices[3*i]   = cubeVertices[3*i] + (float)center.x;
    		vertices[3*i+1] = cubeVertices[3*i+1] + (float)center.z;
    		vertices[3*i+2] = cubeVertices[3*i+2] + (float)center.y;
    	}
    }
    
    public void draw(GL10 gl)
    {

    	
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);
              
        gl.glFrontFace(gl.GL_CW);
        gl.glColor4f(0.0f, 1.0f, 0.0f, 0.5f);
        gl.glVertexPointer(3, gl.GL_FLOAT, 0, mVertexBuffer);
        gl.glEnableClientState(gl.GL_VERTEX_ARRAY);
        gl.glDrawElements(gl.GL_TRIANGLES, 36, gl.GL_UNSIGNED_BYTE, mIndexBuffer);

    }
}
