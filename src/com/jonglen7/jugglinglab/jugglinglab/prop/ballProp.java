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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Color;

import com.jonglen7.jugglinglab.jugglinglab.util.Coordinate;
import com.jonglen7.jugglinglab.jugglinglab.util.JuggleExceptionUser;
import com.jonglen7.jugglinglab.jugglinglab.util.ParameterDescriptor;
import com.jonglen7.jugglinglab.jugglinglab.util.ParameterList;


public class ballProp extends Prop {
	
	
    // -------------------------------------
    // Attributes
    // -------------------------------------
	
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

	
	
	
    // -------------------------------------
    // Constructors
    // -------------------------------------
	
    public ballProp(){
    	
        size = new Coordinate(ball_pixel_size, ball_pixel_size);
        center = new Coordinate();
        grip = new Coordinate();
        
        // Draw Initialization
        this.mRaduis = 3.0;	//TODO Fred: 25 is a hardcoded value 
        this.mStep = 25.0;	//TODO Fred: 25 is a hardcoded value 
        
        ByteBuffer vbb = ByteBuffer.allocateDirect(40000); //TODO Fred: 40000 is a hardcoded value 
        vbb.order(ByteOrder.nativeOrder());
        sphereVertex = vbb.asFloatBuffer();       
        
        //mPoints = this.build();
    }

        
    

    // -------------------------------------
    // Initialization methods
    // -------------------------------------

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
    
	protected void init(double zoom) {
    	
        ball_pixel_size = (int)(0.5 + zoom * diam);
        
        if (ball_pixel_size < 1)
            ball_pixel_size = 1;

        size = new Coordinate(ball_pixel_size, ball_pixel_size);
        //center = new Coordinate(ball_pixel_size/2, ball_pixel_size/2);
        //grip = new Coordinate(ball_pixel_size/2, ball_pixel_size);

        lastzoom = zoom;
    }
    
	
	
    // -------------------------------------
    // Attributes Getters & Setters
    // -------------------------------------
    
    public double getDiam() {
		return diam;
	}

	public void setDiam(double diam) {
		this.diam = diam;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public boolean isHighlight() {
		return highlight;
	}

	public void setHighlight(boolean highlight) {
		this.highlight = highlight;
	}

	public Coordinate getSize() {
		return size;
	}

	public void setSize(Coordinate size) {
		this.size = size;
	}

	public Coordinate getCenter() {
		return center;
	}

	public void setCenter(Coordinate center) {
		this.center = center;
	}
    
    
	
    // -------------------------------------
    // Advanced Getters
    // -------------------------------------
    
    public String getName() {
        return "Ball";
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

    public Coordinate getMax() {
        return new Coordinate(diam/2,0,diam);
    }

    public Coordinate getMin() {
        return new Coordinate(-diam/2,0,0);
    }


    
    
    // -------------------------------------
    // Drawing Attributes and methods
    // -------------------------------------
    
    private FloatBuffer sphereVertex;
    private double mRaduis;
    private double mStep;
    private float mVertices[];
    private static double DEG = Math.PI/180;
    private int mPoints;
    
    private int build() {

        /**
         * x = p * sin(phi) * cos(theta)
         * y = p * sin(phi) * sin(theta)
         * z = p * cos(phi)
         */
        double dTheta = mStep * DEG;
        double dPhi = dTheta;
        int points = 0;

        for(double phi = -(Math.PI); phi <= Math.PI; phi+=dPhi) {
            //for each stage calculating the slices
            for(double theta = 0.0; theta <= (Math.PI * 2); theta+=dTheta) {
                sphereVertex.put((float) (mRaduis * Math.sin(phi) * Math.cos(theta)) + (float)center.x); // TODO Fred: Hack Fred
                sphereVertex.put((float) (mRaduis * Math.sin(phi) * Math.sin(theta)) + (float)center.z); // to simulate 
                sphereVertex.put((float) (mRaduis * Math.cos(phi)) + (float)center.y);					 // centerProp()
                points++;

            }
        }
        sphereVertex.position(0);
        return points;
    }
    public void centerProp() {
    	//TODO Fred: HACK, supposed to be in the constructor
    	//              simulate the centerProp()
    	
        mPoints = this.build();
        /*
    	for (int i= 0; i<8; i++){
    		sphereVertex.[3*i]   = sphereVertex[3*i] + (float)center.x;
    		sphereVertex[3*i+1] = sphereVertex[3*i+1] + (float)center.z;
    		sphereVertex[3*i+2] = sphereVertex[3*i+2] + (float)center.y;
    	}
    	*/
    	
    }
    
    public void draw(GL10 gl)
    {
       
        gl.glFrontFace(gl.GL_CW);
        gl.glColor4f(((float)Color.red(color))/255.0f, ((float)Color.green(color))/255.0f, ((float)Color.blue(color))/255.0f, 1.0f);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, sphereVertex);
        gl.glEnableClientState(gl.GL_VERTEX_ARRAY);
        gl.glDrawArrays(GL10.GL_POINTS, 0, mPoints);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

    }
}
