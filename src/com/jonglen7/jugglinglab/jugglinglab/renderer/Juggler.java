// Juggler.java
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

package com.jonglen7.jugglinglab.jugglinglab.renderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import com.jonglen7.jugglinglab.jugglinglab.jml.HandLink;
import com.jonglen7.jugglinglab.jugglinglab.jml.JMLPattern;
import com.jonglen7.jugglinglab.jugglinglab.util.Coordinate;
import com.jonglen7.jugglinglab.jugglinglab.util.JLMath;
import com.jonglen7.jugglinglab.jugglinglab.util.JuggleExceptionInternal;
import com.jonglen7.jugglinglab.jugglinglab.util.MathVector;


//  This class calculates the coordinates of the juggler elbows, shoulders, etc.

public class Juggler {
	
	
	// Juggler dimensions, in cm
	public final static double shoulder_hw = 23.0;	// shoulder half-width (m)
	public final static double shoulder_h = 40.0;	// throw pos. to shoulder
	public final static double waist_hw = 17.0;		// waist half-width
	public final static double waist_h = -5.0;
	public final static double elbow_hw = 30.0;		// elbow "home"
	public final static double elbow_h = 6.0;
	public final static double elbow_slop = 12.0;
	public final static double hand_out = 5.0;		// outside width of hand
	public final static double hand_in = 5.0;
	public final static double head_hw = 10.0;		// head half-width
	public final static double head_h = 26.0;		// head height
	public final static double neck_h = 5.0;		// neck height
	public final static double shoulder_y = 0;
	public final static double pattern_y = 30;
	public final static double upper_length = 41;
	public final static double lower_length = 40;
	
	public final static double lower_gap_wrist = 1;
	public final static double lower_gap_elbow = 0;
	public final static double lower_hand_height = 0;
	public final static double upper_gap_elbow = 0;
	public final static double upper_gap_shoulder = 0;
	
	protected final static double lower_total = lower_length + lower_gap_wrist + lower_gap_elbow;
	protected final static double upper_total = upper_length + upper_gap_elbow + upper_gap_shoulder;

	// The remaining are used only for the 3d display 
	public final static double shoulder_radius = 6;
	public final static double elbow_radius = 4;
	public final static double wrist_radius = 2;
	
	
	// Number of juggler
	private int nbJuggler;
	// Structure that will contains the pixel positions of the juggler
	private MathVector[][] jugglerDescription;
	
	// Array for openGL
	private float vertices[];
    private byte indices[] = {
            0, 4,    1, 5,						// Left and Right Hand
            4, 2,    5, 3,						// Left and Right Arm
            2, 6,    6, 7,    7, 3,     3, 2,	// Body
            9, 8,    8, 10,   10, 11,   11, 9	// Head
   	};
	
	// Buffer from openGL
    private FloatBuffer   mVertexBuffer;
    private ByteBuffer  mIndexBuffer;
    private ByteBuffer vbb;
    
    
    
    
    // Constructors
    public Juggler(int nbJuggler)
    { 	
    	// Allocation
    	this.nbJuggler = nbJuggler;
    	
		this.jugglerDescription = new MathVector[nbJuggler][12];
		
		int size = nbJuggler*3*12;	// NbJuggler x 3 coordinates x 12 description points
		this.vertices = new float[size];
    	
		vbb = ByteBuffer.allocateDirect(size*4);  			
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        
    	mIndexBuffer = ByteBuffer.allocateDirect(indices.length);
        mIndexBuffer.put(indices);
        mIndexBuffer.position(0);
    	
    }
	
	// Find the pixeled position of the juggler description points regarding the pattern
    // Stock the result in MathVector[][] structure
    // TODO: Stock directly in the vertices[] to be exploited by openGL methods
	public void findJugglerCoordinates(JMLPattern pat, double time) throws JuggleExceptionInternal {
		
		for (int juggler = 1; juggler <= pat.getNumberOfJugglers(); juggler++) {
			MathVector lefthand, righthand;
			MathVector leftshoulder, rightshoulder;
			MathVector leftelbow, rightelbow;
			MathVector leftwaist, rightwaist;
			MathVector leftheadbottom, leftheadtop;
			MathVector rightheadbottom, rightheadtop;
			
			Coordinate coord0 = new Coordinate();
			Coordinate coord1 = new Coordinate();
			Coordinate coord2 = new Coordinate();
			pat.getHandCoordinate(juggler, HandLink.LEFT_HAND, time, coord0);
			pat.getHandCoordinate(juggler, HandLink.RIGHT_HAND, time, coord1);
			lefthand = new MathVector((float)coord0.x,
						(float)(coord0.z + lower_hand_height), (float)coord0.y);
			righthand = new MathVector((float)coord1.x,
						(float)(coord1.z + lower_hand_height), (float)coord1.y);
			
			pat.getJugglerPosition(juggler, time, coord2);
			double angle = JLMath.toRad(pat.getJugglerAngle(juggler, time));
			double s = Math.sin(angle);
			double c = Math.cos(angle);
			
			leftshoulder = new MathVector(
				(float)(coord2.x - shoulder_hw * c - shoulder_y * s),
				(float)(coord2.z + shoulder_h),
				(float)(coord2.y - shoulder_hw * s + shoulder_y * c));
			rightshoulder = new MathVector(
				(float)(coord2.x + shoulder_hw * c - shoulder_y * s),
				(float)(coord2.z + shoulder_h),
				(float)(coord2.y + shoulder_hw * s + shoulder_y * c));
			leftwaist = new MathVector(
				(float)(coord2.x - waist_hw * c - shoulder_y * s),
				(float)(coord2.z + waist_h),
				(float)(coord2.y - waist_hw * s + shoulder_y * c));
			rightwaist = new MathVector(
				(float)(coord2.x + waist_hw * c - shoulder_y * s),
				(float)(coord2.z + waist_h),
				(float)(coord2.y + waist_hw * s + shoulder_y * c));
			leftheadbottom = new MathVector(
				(float)(coord2.x - head_hw * c - shoulder_y * s),
				(float)(coord2.z + shoulder_h + neck_h),
				(float)(coord2.y - head_hw * s + shoulder_y * c));
			leftheadtop = new MathVector(
				(float)(coord2.x - head_hw * c - shoulder_y * s),
				(float)(coord2.z + shoulder_h + neck_h + head_h),
				(float)(coord2.y - head_hw * s + shoulder_y * c));
			rightheadbottom = new MathVector(
				(float)(coord2.x + head_hw * c - shoulder_y * s),
				(float)(coord2.z + shoulder_h + neck_h),
				(float)(coord2.y + head_hw * s + shoulder_y * c));
			rightheadtop = new MathVector(
				(float)(coord2.x + head_hw * c - shoulder_y * s),
				(float)(coord2.z + shoulder_h + neck_h + head_h),
				(float)(coord2.y + head_hw * s + shoulder_y * c));
			
			double L = lower_total;
			double U = upper_total;
			MathVector deltaL = MathVector.sub(lefthand, leftshoulder);
			double D = (double)(deltaL.length());
			if (D <= (L+U)) {
				// Calculate the coordinates of the elbows
				double Lr = Math.sqrt((4.0*U*U*L*L-(U*U+L*L-D*D)*(U*U+L*L-D*D))/(4.0*D*D));
				if (Double.isNaN(Lr))
					throw new JuggleExceptionInternal("NaN in renderer 1");
				
				double factor = Math.sqrt(U*U-Lr*Lr)/D;
				if (Double.isNaN(factor))
					throw new JuggleExceptionInternal("NaN in renderer 2");
				MathVector Lxsc = MathVector.scale((float)factor, deltaL);
				double Lalpha = Math.asin(deltaL.y / D);
				if (Double.isNaN(Lalpha))
					throw new JuggleExceptionInternal("NaN in renderer 3");
				factor = 1.0 + Lr*Math.tan(Lalpha)/(factor*D);
				leftelbow = new MathVector(
						leftshoulder.x + Lxsc.x * (float)factor,
						leftshoulder.y + Lxsc.y - (float)(Lr*Math.cos(Lalpha)),
						leftshoulder.z + Lxsc.z * (float)factor);
			} else {
				leftelbow = null;
			}
			
			MathVector deltaR = MathVector.sub(righthand, rightshoulder);
			D = (double)(deltaR.length());
			if (D <= (L+U)) {
				// Calculate the coordinates of the elbows
				double Rr = Math.sqrt((4.0*U*U*L*L-(U*U+L*L-D*D)*(U*U+L*L-D*D))/(4.0*D*D));
				if (Double.isNaN(Rr))
					throw new JuggleExceptionInternal("NaN in renderer 4");
				
				double factor = Math.sqrt(U*U-Rr*Rr)/D;
				if (Double.isNaN(factor))
					throw new JuggleExceptionInternal("NaN in renderer 5");
				MathVector Rxsc = MathVector.scale((float)factor, deltaR);
				double Ralpha = Math.asin(deltaR.y / D);
				if (Double.isNaN(Ralpha))
					throw new JuggleExceptionInternal("NaN in renderer 6");
				factor = 1.0 + Rr*Math.tan(Ralpha)/(factor*D);
				rightelbow = new MathVector(
						rightshoulder.x + Rxsc.x * (float)factor,
						rightshoulder.y + Rxsc.y - (float)(Rr*Math.cos(Ralpha)),
						rightshoulder.z + Rxsc.z * (float)factor);
			} else {
				rightelbow = null;
			}

			jugglerDescription[juggler-1][0] = lefthand;
			jugglerDescription[juggler-1][1] = righthand;
			jugglerDescription[juggler-1][2] = leftshoulder;
			jugglerDescription[juggler-1][3] = rightshoulder;
			jugglerDescription[juggler-1][4] = leftelbow;
			jugglerDescription[juggler-1][5] = rightelbow;
			jugglerDescription[juggler-1][6] = leftwaist;
			jugglerDescription[juggler-1][7] = rightwaist;
			jugglerDescription[juggler-1][8] = leftheadbottom;
			jugglerDescription[juggler-1][9] = leftheadtop;
			jugglerDescription[juggler-1][10] = rightheadbottom;
			jugglerDescription[juggler-1][11] = rightheadtop;
		}
	}
	
	
	//
	// TODO : Here, for just one juggler, Implement for numerous jugglers
	public void MathVectorToVertices(){
		
		int juggler = this.nbJuggler;
		int i = 0;
		for (int j=0; j<12; j++){
			vertices[i] = (int)jugglerDescription[juggler-1][j].x;
			vertices[i+1] = (int)jugglerDescription[juggler-1][j].y;
			vertices[i+2] = (int)jugglerDescription[juggler-1][j].z;
			i+=3;
		}
	}
	
	
	
	// Draw the juggler regarding the pixeled position stocked in jugglerDescription
    public void draw(GL10 gl) throws JuggleExceptionInternal {

        // Buffers to be passed to gl*Pointer() functions
        // must be direct, i.e., they must be placed on the
        // native heap where the garbage collector cannot
        // move them.
        //
        // Buffers with multi-byte datatypes (e.g., short, int, float)
        // must have their byte order set to native order

        
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);
        
    	gl.glDisable(gl.GL_BLEND);
    	gl.glColor4f(0.0f,0.0f,1.0f,1.0f);
    	gl.glLineWidth(3.0f);
        gl.glVertexPointer(3, gl.GL_FLOAT, 0, mVertexBuffer);
        gl.glEnableClientState(gl.GL_VERTEX_ARRAY);
        gl.glDrawElements(gl.GL_LINES, indices.length, gl.GL_UNSIGNED_BYTE, mIndexBuffer);
    }
	
}	

