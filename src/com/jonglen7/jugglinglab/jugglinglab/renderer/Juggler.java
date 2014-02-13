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

import android.content.Context;
import android.content.SharedPreferences;

import com.jonglen7.jugglinglab.R;
import com.jonglen7.jugglinglab.jugglinglab.jml.HandLink;
import com.jonglen7.jugglinglab.jugglinglab.jml.JMLPattern;
import com.jonglen7.jugglinglab.jugglinglab.util.Coordinate;
import com.jonglen7.jugglinglab.jugglinglab.util.JLMath;
import com.jonglen7.jugglinglab.jugglinglab.util.JuggleExceptionInternal;
import com.jonglen7.jugglinglab.jugglinglab.util.MathVector;
import com.jonglen7.jugglinglab.util.ColorConverter;


//  This class calculates the coordinates of the juggler elbows, shoulders, etc.

public class Juggler {
	
	private float JUGGLER_WIDTH = 1.0f;
	private float JUGGLER_ARM_WIDTH = 1.0f;
	
	
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
	
    private Context context = null;
    private SharedPreferences preferences = null;

    // Number of juggler
	private int nbJuggler;
	
	// Structure that will contains the pixel positions of the juggler
	private MathVector[][] jugglerDescription;
	
	// Boolean for null elbow coordinate
	private boolean bNullLeftelbow = false;
	private boolean bNullRightelbow = false;
	
	// Array for openGL
	private float vertices[][];
	
	// Body
    private byte indicesOfBody[] = {
            2, 6,    6, 7,    7, 3,     3, 2,	// Body
   	};
    
    // Head
    private byte indicesOfHead[] = {
            9, 8,    8, 10,   10, 11,   11, 9	// Head
   	};
    
    // Inner Body
    private byte indicesOfInnerBody[] = { 2, 6, 3, 7 }; 		// Body for GL_TRIANGLE_STRIP
    
    // Inner Head
    private byte indicesOfInnerHead[] = { 8, 9, 10, 11 }; 		// Head for GL_TRIANGLE_STRIP
    
    // Left Arm
    private byte indicesOfLeftArm[] = {0, 4, 4, 2,};			// Left Arm and Hand
    private byte indicesOfLeftArmWithoutLeftElbow[] = {0, 2};	// Left Arm and Hand Without Elbow
    
    // Right Arm
    private byte indicesOfRightArm[] = {1, 5, 5, 3,};			// Right Arm and Hand
    private byte indicesOfRightArmWithoutRightElbow[] = {1, 3};	// Right Arm and Han Without Elbow
 
	// Buffer from openGL
    private FloatBuffer   mVertexBuffer;
    private ByteBuffer  mIndexBuffer;
    private ByteBuffer vbb;
    
    // Round Head attributes
    private float roundHeadVertices[][];
    private ByteBuffer roundHeadVbb;
    private FloatBuffer roundHeadVertexBuffer;
    private int roundHeadNbSegments = 36;
    
    
    
    
    // Constructors
    public Juggler(Context context, int nbJuggler)
    { 	
    	// Allocation
        this.context = context;
    	this.nbJuggler = nbJuggler;
        this.preferences = context.getSharedPreferences("com.jonglen7.jugglinglab_preferences", 0);
    	
		this.jugglerDescription = new MathVector[nbJuggler][12];
		
		int size = 3*12;	// 3 coordinates x 12 description points
		this.vertices = new float[nbJuggler][size];
    	
		vbb = ByteBuffer.allocateDirect(size*4); 			
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        
    	mIndexBuffer = ByteBuffer.allocateDirect(indicesOfHead.length);
        mIndexBuffer.put(indicesOfHead);
        mIndexBuffer.position(0);
        
        // Round Head
        this.roundHeadVertices = new float[nbJuggler][3*roundHeadNbSegments];
        this.roundHeadVbb = ByteBuffer.allocateDirect(3*roundHeadNbSegments*4);
        this.roundHeadVbb.order(ByteOrder.nativeOrder());
        this.roundHeadVertexBuffer = this.roundHeadVbb.asFloatBuffer();
        
        
        
    	
    }
	
	// Find the pixeled position of the juggler description points regarding the pattern
    // Stock the result in MathVector[][] structure
    // TODO Fred: Stock directly in the vertices[] to be exploited by openGL methods
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
	
	
	// Convert from MatVector structure to vertices OpenGL structure
	public void MathVectorToVertices(){
		int i = 0;
		for (int juggler = 1; juggler <= this.nbJuggler; juggler++) {
			
			bNullLeftelbow = false;
			bNullRightelbow = false;
			i = 0;
			
			for (int j=0; j<4; j++){
				vertices[juggler-1][i] = (int)jugglerDescription[juggler-1][j].x;
				vertices[juggler-1][i+1] = (int)jugglerDescription[juggler-1][j].y;
				vertices[juggler-1][i+2] = (int)jugglerDescription[juggler-1][j].z;
				i+=3;
			}
			
			// Sometimes leftelbow is null
			// When this happen the line shoulder-hand is the whole arm
			if (jugglerDescription[juggler-1][4] != null) {			
				vertices[juggler-1][i] = (int)jugglerDescription[juggler-1][4].x;
				vertices[juggler-1][i+1] = (int)jugglerDescription[juggler-1][4].y;
				vertices[juggler-1][i+2] = (int)jugglerDescription[juggler-1][4].z;	
			} else {
				bNullLeftelbow = true;
			}
			i+=3;
			
			// Sometimes righttelbow is null
			// When this happen the line shoulder-hand is the whole arm
			if (jugglerDescription[juggler-1][5] != null) {
				vertices[juggler-1][i] = (int)jugglerDescription[juggler-1][5].x;
				vertices[juggler-1][i+1] = (int)jugglerDescription[juggler-1][5].y;
				vertices[juggler-1][i+2] = (int)jugglerDescription[juggler-1][5].z;
			} else {
				bNullRightelbow = true;
			}		
			i+=3;
			
			// Squared head vertices
			for (int j=6; j<12; j++){
				vertices[juggler-1][i] = (int)jugglerDescription[juggler-1][j].x;
				vertices[juggler-1][i+1] = (int)jugglerDescription[juggler-1][j].y;
				vertices[juggler-1][i+2] = (int)jugglerDescription[juggler-1][j].z;
				i+=3;
			}
			
			// Round Head vertices
			int count = 0;
			float tmpX = 0.0f, tmpY = 0.0f, tmpZ = 0.0f, theta = 0.0f;
			
			// Compute translation vector
			float xPos = (float)(jugglerDescription[juggler-1][9].x + jugglerDescription[juggler-1][10].x)/2.0f;
			float yPos = (float)(jugglerDescription[juggler-1][9].y + jugglerDescription[juggler-1][10].y)/2.0f;
			float zPos = (float)(jugglerDescription[juggler-1][9].z + jugglerDescription[juggler-1][10].z)/2.0f;
			
            // Compute angle of rotation
            theta = (float)(Math.atan2(- jugglerDescription[juggler-1][9].z + jugglerDescription[juggler-1][10].z  , jugglerDescription[juggler-1][9].x - jugglerDescription[juggler-1][10].x));
            
            // Compute vertices of the head
			for (float j = 0; j < 360.0f; j += (360.0f/this.roundHeadNbSegments)) 
			{
				// Get coordinates in a classic 3-axis
	            tmpX = (float)(Math.cos(Math.PI/180.0f *j)*Juggler.head_hw);
	            tmpY = (float)(Math.sin(Math.PI/180.0f *j)*Juggler.head_h/2);
	            tmpZ = 0.0f;
	            
	            // Apply a rotation around Y axis to have them relatively to the juggler orientation 
	            // and translate to align to the juggler body
	            this.roundHeadVertices[juggler-1][count++] = xPos + (float)(Math.cos(theta) * tmpX + Math.sin(theta) * tmpZ);
	            this.roundHeadVertices[juggler-1][count++] = yPos + tmpY;
	            this.roundHeadVertices[juggler-1][count++] = zPos + (float)(- Math.sin(theta) * tmpX + Math.cos(theta) * tmpZ);  
	        }
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

        // Get colors from the preferences
        int edges_color = preferences.getInt("juggler_edges_color", context.getResources().getInteger(R.color.juggler_edges_default_color));
        float[] edges_rgba = new ColorConverter().hex2rgba(edges_color);

        int inner_color = preferences.getInt("juggler_inner_color", context.getResources().getInteger(R.color.juggler_inner_default_color));
        float[] inner_rgba = new ColorConverter().hex2rgba(inner_color);
        
        // Edit Romain: To enable transparent textures, had to comment the next line
//    	gl.glDisable(GL10.GL_BLEND);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        
        for (int juggler = 1; juggler <= this.nbJuggler; juggler++) {
        	
        	// Set attributes to draw round head
        	roundHeadVertexBuffer.put(roundHeadVertices[juggler-1]);
        	roundHeadVertexBuffer.position(0);
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, roundHeadVertexBuffer);

        	// Draw round head inner part
            gl.glPolygonOffset(1.0f, 1.0f);
            gl.glEnable(GL10.GL_POLYGON_OFFSET_FILL);
        	gl.glColor4f(inner_rgba[0],inner_rgba[1],inner_rgba[2],inner_rgba[3]);
        	gl.glLineWidth(1.0f);
            gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, roundHeadNbSegments);
            gl.glDisable(GL10.GL_POLYGON_OFFSET_FILL);
            
            // Draw round head edges
        	gl.glColor4f(edges_rgba[0],edges_rgba[1],edges_rgba[2],edges_rgba[3]);
        	gl.glLineWidth(JUGGLER_WIDTH);
        	gl.glDrawArrays(GL10.GL_LINE_LOOP, 0, roundHeadNbSegments);
        	

        	// Set attributes to draw other part of the juggler
        	mVertexBuffer.put(vertices[juggler-1]);
            mVertexBuffer.position(0);
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);

            // To avoid issues with the depth buffer (cf. http://profs.sci.univr.it/~colombar/html_openGL_tutorial/en/06depth_014.html)
            gl.glPolygonOffset(1.0f, 1.0f);
            gl.glEnable(GL10.GL_POLYGON_OFFSET_FILL);

            // Set inner parts color
            gl.glColor4f(inner_rgba[0],inner_rgba[1],inner_rgba[2],inner_rgba[3]);
            gl.glLineWidth(1.0f);
            
            // Draw the inner body
	        mIndexBuffer.put(indicesOfInnerBody);
            mIndexBuffer.position(0);
            gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, indicesOfInnerBody.length, GL10.GL_UNSIGNED_BYTE, mIndexBuffer);
        
            // Draw the inner head
            //mIndexBuffer.put(indicesOfInnerHead);
            //mIndexBuffer.position(0);
            //gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, indicesOfInnerHead.length, GL10.GL_UNSIGNED_BYTE, mIndexBuffer);

            // Disable fill flag
            gl.glDisable(GL10.GL_POLYGON_OFFSET_FILL);
            
        	// Set juggler width
        	gl.glLineWidth(JUGGLER_WIDTH);

            // Set juggler edges color
            gl.glColor4f(edges_rgba[0],edges_rgba[1],edges_rgba[2],edges_rgba[3]);
            
	        // Draw the head edges
            //mIndexBuffer.put(indicesOfHead);
            //mIndexBuffer.position(0);
        	//gl.glDrawElements(GL10.GL_LINES, indicesOfHead.length, GL10.GL_UNSIGNED_BYTE, mIndexBuffer);
        	
        	// Draw the body edges
        	mIndexBuffer.put(indicesOfBody);
            mIndexBuffer.position(0);
        	gl.glDrawElements(GL10.GL_LINES, indicesOfBody.length, GL10.GL_UNSIGNED_BYTE, mIndexBuffer);
            
        	// Set arms width
        	gl.glLineWidth(JUGGLER_ARM_WIDTH);
        	
            // Set left arm color
        	gl.glColor4f(edges_rgba[0],edges_rgba[1],edges_rgba[2],edges_rgba[3]);
        	
        	// Draw left arm and hand
        	if (bNullLeftelbow) {
	            mIndexBuffer.put(indicesOfLeftArmWithoutLeftElbow);
	            mIndexBuffer.position(0);
	        	gl.glDrawElements(GL10.GL_LINES, indicesOfLeftArmWithoutLeftElbow.length, GL10.GL_UNSIGNED_BYTE, mIndexBuffer);
	        }
        	else
        	{
        		mIndexBuffer.put(indicesOfLeftArm);
	            mIndexBuffer.position(0);
	        	gl.glDrawElements(GL10.GL_LINES, indicesOfLeftArm.length, GL10.GL_UNSIGNED_BYTE, mIndexBuffer);
        	}
        	
        	// Set right arm color
        	gl.glColor4f(edges_rgba[0],edges_rgba[1],edges_rgba[2],edges_rgba[3]);
        	
        	// Draw right arm and hand
        	if (bNullRightelbow) {
	            mIndexBuffer.put(indicesOfRightArmWithoutRightElbow);
	            mIndexBuffer.position(0);
	        	gl.glDrawElements(GL10.GL_LINES, indicesOfRightArmWithoutRightElbow.length, GL10.GL_UNSIGNED_BYTE, mIndexBuffer);
	        }
        	else
        	{
        		mIndexBuffer.put(indicesOfRightArm);
	            mIndexBuffer.position(0);
	        	gl.glDrawElements(GL10.GL_LINES, indicesOfRightArm.length, GL10.GL_UNSIGNED_BYTE, mIndexBuffer);
        	}
        	
        }
    }
	
}	

