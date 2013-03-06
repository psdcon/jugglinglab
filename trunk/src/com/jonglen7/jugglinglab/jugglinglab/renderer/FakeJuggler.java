package com.jonglen7.jugglinglab.jugglinglab.renderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class FakeJuggler {

	// Constants
    float vertices[] = {-34.0f , 85.0f   , 30.0f  , // 0
 						12.0f  , 100.0f  , 30.0f  , // 1
 						-23.0f , 140.0f  , 0.0f   , // 2
 						23.0f  , 140.0f  , 0.0f   , // 3
 						-20.0f , 99.0f   , -6.0f  , // 4
 						26.0f  , 100.0f  , -8.0f  , // 5
 						-17.0f , 95.0f   , 0.0f   , // 6
 						17.0f  , 95.0f   , 0.0f   , // 7
 						-10.0f , 145.0f  ,  0.0f  , // 8
 						-10.0f , 171.0f  , 0.0f   , // 9
 						10.0f  , 145.0f  , 0.0f   , // 10
 						10.0f  , 171.0f  , 0.0f     // 11
    };
    private byte indices[] = {
            0, 4,    1, 5,						// Left and Right Hand
            4, 2,    5, 3,						// Left and Right Arm
            2, 6,    6, 7,    7, 3,     3, 2,	// Body
            9, 8,    8, 10,   10, 11,   11, 9	// Head
   	};

    
	/*
	private float vertices[] = {
		      -10.0f, 171.0f,  0.0f,  // 0, Top Left
		      -10.0f, 145.0f, 0.0f,  // 1, Bottom Left
		      10.0f,  145.0f, 0.0f,  // 2, Bottom Right
		      10.0f,  171.0f,  0.0f,  // 3, Top Right
		};

    private byte indices[] = { 0, 1,    1, 2,   2, 3,   3, 0 }; // Head
    */
    
    
    /*
			Y				xmin = -34
			|				xmax = 26
			|_____X			ymin = 85
		   /				ymax = 171
		  /					zmin = -8
		 Z					zmax = 30
    */
    
    
    // Attributes
    private FloatBuffer   mVertexBuffer;
    private ByteBuffer  mIndexBuffer;

    // Constructor
    public FakeJuggler()
    {
        // Buffers to be passed to gl*Pointer() functions
        // must be direct, i.e., they must be placed on the
        // native heap where the garbage collector cannot
        // move them.
        //
        // Buffers with multi-byte datatypes (e.g., short, int, float)
        // must have their byte order set to native order

        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);

        mIndexBuffer = ByteBuffer.allocateDirect(indices.length);
        mIndexBuffer.put(indices);
        mIndexBuffer.position(0);
    }
    
    // Drawing function
    public void draw(GL10 gl)
    {
    	gl.glDisable(GL10.GL_BLEND);
    	gl.glColor4f(1.0f,0.0f,0.0f,1.0f);
    	gl.glLineWidth(3.0f);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDrawElements(GL10.GL_LINES, indices.length, GL10.GL_UNSIGNED_BYTE, mIndexBuffer);

    }
}
