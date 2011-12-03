// Descibe a mathematical 3D vector 
// Inspired by Peter Walser's idx3d API

package com.jonglen7.jugglinglab.jugglinglab.util;


public class MathVector {

	// Attributes
	public double x=0;      //Cartesian (default)
	public double y=0;      //Cartesian (default)
	public double z=0;      //Cartesian (default) & Cylindric
	public double r=0;      //Cylindric
	public double theta=0;  //Cylindric


	// Contructors

	public MathVector ()
	{
	}

	public MathVector (double xpos, double ypos, double zpos)
	{
		x=xpos;
		y=ypos;
		z=zpos;
	}

	// Public Methods

	public MathVector normalize()
	// Normalizes the vector
	{
		double dist=length();
		if (dist==0) return this;
		double invdist=1/dist;
		x*=invdist;
		y*=invdist;
		z*=invdist;
		return this;
	}
	
	public MathVector reverse()
	// Reverses the vector
	{	
		x=-x;
		y=-y;
		z=-z;
		return this;
	}
	
	public double length()
	// Lenght of this vector
	{	
		return (double)Math.sqrt(x*x+y*y+z*z);
	}

	/*
	public MathVector transform(idx3d_Matrix m)
	// Modifies the vector by matrix m 
	{
		double newx = x*m.m00 + y*m.m01 + z*m.m02+ m.m03;
		double newy = x*m.m10 + y*m.m11 + z*m.m12+ m.m13;
		double newz = x*m.m20 + y*m.m21 + z*m.m22+ m.m23;
		return new MathVector(newx,newy,newz);
	}
	*/

	public void buildCylindric()
	// Builds the cylindric coordinates out of the given cartesian coordinates
	{
		r=(double)Math.sqrt(x*x+y*y);
		theta=(double)Math.atan2(x,y);
	}

	public void buildCartesian()
	// Builds the cartesian coordinates out of the given cylindric coordinates
	{
		x=r*java.lang.Math.cos(theta);
		y=r*java.lang.Math.sin(theta);
	}

	public static MathVector getNormal(MathVector a, MathVector b)
	// returns the normal vector of the plane defined by the two vectors
	{
		return vectorProduct(a,b).normalize();
	}
	
	public static MathVector getNormal(MathVector a, MathVector b, MathVector c)
	// returns the normal vector of the plane defined by the two vectors
	{
		return vectorProduct(a,b,c).normalize();
	}
	
	public static MathVector vectorProduct(MathVector a, MathVector b)
	// returns a x b
	{
		return new MathVector(a.y*b.z-b.y*a.z,a.z*b.x-b.z*a.x,a.x*b.y-b.x*a.y);
	}
	
	public static MathVector vectorProduct(MathVector a, MathVector b, MathVector c)
	// returns (b-a) x (c-a)
	{
		return vectorProduct(sub(b,a),sub(c,a));
	}

	public static double angle(MathVector a, MathVector b)
	// returns the angle between 2 vectors
	{
		a.normalize();
		b.normalize();
		return (a.x*b.x+a.y*b.y+a.z*b.z);
	}
	
	public static MathVector add(MathVector a, MathVector b)
	// adds 2 vectors
	{
		return new MathVector(a.x+b.x,a.y+b.y,a.z+b.z);
	}
	
	public static MathVector sub(MathVector a, MathVector b)
	// substracts 2 vectors
	{
		return new MathVector(a.x-b.x,a.y-b.y,a.z-b.z);
	}
	
	public static MathVector scale(double f, MathVector a)
	// substracts 2 vectors
	{
		return new MathVector(f*a.x,f*a.y,f*a.z);
	}
	
	public static double len(MathVector a)
	// length of vector
	{
		return (double)Math.sqrt(a.x*a.x+a.y*a.y+a.z*a.z);
	}
	
	public static MathVector random(double fact)
	// returns a random vector
	{
		return new MathVector(fact*java.lang.Math.random(),fact*java.lang.Math.random(),fact*java.lang.Math.random());
	}
	
	public String toString()
	{
		return new String ("<vector x="+x+" y="+y+" z="+z+">\r\n");
	}

	public MathVector getClone()
	{
		return new MathVector(x,y,z);
	}

}
