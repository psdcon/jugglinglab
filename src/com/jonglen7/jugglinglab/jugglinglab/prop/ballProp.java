package com.jonglen7.jugglinglab.jugglinglab.prop;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Vector;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Color;

import com.jonglen7.jugglinglab.jugglinglab.util.Coordinate;
import com.jonglen7.jugglinglab.jugglinglab.util.JuggleExceptionUser;
import com.jonglen7.jugglinglab.jugglinglab.util.ParameterDescriptor;

public class ballProp extends Prop {
	
    // -------------------------------------
    // Attributes
    // -------------------------------------
    private FloatBuffer strip, fan_top, fan_bottom;
    private FloatBuffer tex_strip, tex_fan_top, tex_fan_bottom;
    private float radius;
    private int stacks,  slices;
    private int tex;
    
    private float PROP_COLOR[] = {1.0f, 0.0f, 0.0f, 1.0f}; 
    
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
    public ballProp() 
    {
            this.tex = 1;
            this.stacks = 12;
            this.slices = 12;
            this.radius = 4;
            
    }
    
    
    // -------------------------------------
    // Initialization method
    // -------------------------------------
    protected void init(String st) throws JuggleExceptionUser 
    {
    	
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
    // Drawing method
    // -------------------------------------
    public void draw(GL10 gl) {
            //gl.glBindTexture(GL10.GL_TEXTURE_2D, tex);
    		gl.glColor4f(PROP_COLOR[0],PROP_COLOR[1],PROP_COLOR[2],PROP_COLOR[3]);
           
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, fan_top);
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
   
            gl.glNormalPointer(GL10.GL_FLOAT, 0, fan_top);
            gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
           
            gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, tex_fan_top);
            gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
            gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, slices + 2);
           
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, strip);
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
   
            gl.glNormalPointer(GL10.GL_FLOAT, 0, strip);
            gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
            gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, (slices + 1) * 2 * stacks);
           
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, fan_bottom);
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
   
            gl.glNormalPointer(GL10.GL_FLOAT, 0, fan_bottom);
            gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
           

            gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, tex_fan_bottom);
            gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
           
            gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, slices + 2);
    }
   
    protected FloatBuffer[] makeEndCap(int stacks, int slices, boolean top) {
            // Calculate the Triangle Fan for the endcaps
            int triangleFanVertexCount = slices + 2;
            float dtheta = (float)(2.0 * Math.PI / slices);
            float drho =  (float)(Math.PI / stacks);
            float[] fanVertices = new float[triangleFanVertexCount * 3];
            float[] fanTextures = new float[triangleFanVertexCount * 2];
            float theta = 0;
            float sin_drho = (float)Math.sin(drho);
            //float cos_drho = (float)Math.cos(Math.PI / stacks);
            int tex_index = 0;
            fanTextures[tex_index++] = (top ? 0 : 1.0f);
            fanTextures[tex_index++] = 0.5f;
           
            int index = 0;
            fanVertices[index++] = 0.0f;
            fanVertices[index++] = 0.0f;
            fanVertices[index++] = (top ? 1 : -1);
           
           
            for (int j = 0; j <= slices; j++)
            {
                    theta = (j == slices) ? 0.0f : j * (top ? 1 : -1) * dtheta;
                    float x = (float)-Math.sin(theta) * sin_drho;
                    float y = (float)Math.cos(theta) * sin_drho;
                    float z = (top ? 1 : -1) * (float)Math.cos(drho);
                   
                    fanTextures[tex_index++] = x;
                    fanTextures[tex_index++] = y;
                   
                    fanVertices[index++] = x;
                    fanVertices[index++] = y;
                    fanVertices[index++] = z;
                   
            }

            FloatBuffer[] result = new FloatBuffer[2];
            result[0] = makeFloatBuffer(fanVertices);
            result[1] = makeFloatBuffer(fanTextures);
            return result;
    }
   
    protected void unitSphere(int stacks, int slices) {
            float drho =  (float)(Math.PI / stacks);
            float dtheta = (float)(2.0 * Math.PI / slices);

            FloatBuffer[] buffs = makeEndCap(stacks, slices, true);
            fan_top = buffs[0];
            tex_fan_top = buffs[1];
            buffs = makeEndCap(stacks, slices, false);
            fan_bottom = buffs[0];
            tex_fan_bottom = buffs[1];
           
            // Calculate the triangle strip for the sphere body
            int triangleStripVertexCount = (slices + 1) * 2 * stacks;
            float[] stripVertices = new float[triangleStripVertexCount * 3];
           
            int index = 0;
            for (int i = 0; i < stacks; i++) {
                    float rho = i * drho;
                   
                    for (int j = 0; j <= slices; j++)
                    {
                            float theta = (j == slices) ? 0.0f : j * dtheta;
                            float x = radius * (float)(-Math.sin(theta) * Math.sin(rho)) + (float)center.x;
                            float y = radius * (float)(Math.cos(theta) * Math.sin(rho)) + (float)center.z;
                            float z = radius * (float)Math.cos(rho) + (float)center.y;
                            // TODO: Implement texture mapping if texture used
                            //                TXTR_COORD(s, t);
                            stripVertices[index++] = x;
                            stripVertices[index++] = y;
                            stripVertices[index++] = z;
                           
                            x = radius * (float)(-Math.sin(theta) * Math.sin(rho + drho)) + (float)center.x;
                            y = radius * (float)(Math.cos(theta) * Math.sin(rho + drho)) + (float)center.z;
                            z = radius * (float)Math.cos(rho + drho) + (float)center.y;
                            // TODO: Implement texture mapping if texture used
                            //                TXTR_COORD(s, t);
                            stripVertices[index++] = x;
                            stripVertices[index++] = y;
                            stripVertices[index++] = z;
                    }
            }
            strip = makeFloatBuffer(stripVertices);
    }
    
    /**
     * Make a direct NIO FloatBuffer from an array of floats
     * @param arr The array
     * @return The newly created FloatBuffer
     */
    protected static FloatBuffer makeFloatBuffer(float[] arr) {
            ByteBuffer bb = ByteBuffer.allocateDirect(arr.length*4);
            bb.order(ByteOrder.nativeOrder());
            FloatBuffer fb = bb.asFloatBuffer();
            makeFloatBuffer(fb, arr);
            return fb;
    }
    
    protected static void makeFloatBuffer(FloatBuffer fb, float[] arr) {
        fb.put(arr);
        fb.position(0);
}


	@Override
	public void centerProp() {
		unitSphere(stacks, slices);
    	
		
	}


}

