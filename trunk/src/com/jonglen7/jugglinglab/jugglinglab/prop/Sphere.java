package com.jonglen7.jugglinglab.jugglinglab.prop;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Found here http://stackoverflow.com/questions/6072308/problem-drawing-a-sphere-in-opengl-es
 * 
 * TODO Fred: Comm de Romain: j'ai essayé de l'utiliser pour avoir une "vraie"
 * balle et pas un cube, j'ai fais quelques modifs par rapport au code trouvé
 * sur le site mais rien de bien violent (supprimé le fait que des attributs
 * soient static, ajouté un Getter), mais pas réussi à supprimer l'erreur:
 * 
 * Application com.jonglen7.jugglinglab (SDK target 7) called a GL11 Pointer method with an indirect Buffer.
 * E/AndroidRuntime(384): Uncaught handler: thread GLThread 8 exiting due to uncaught exception
 * E/AndroidRuntime(384): java.lang.IllegalArgumentException: Must use a native order direct Buffer
 * 
 * Donc si tu as la motiv' pour le faire (de toute façon c'est dans la roadmap :p)
 * et que ça te semble utile d'utiliser cette classe: fonce! Sinon bah supprime ;)
 */
public class Sphere {

    private FloatBuffer sphereVertex;
    static float sphere_parms[]=new float[3];

    double mRaduis;
    double mStep;
    float mVertices[];
    private static double DEG = Math.PI/180;
    int mPoints;

    /**
     * The value of step will define the size of each facet as well as the number of facets
     *  
     * @param radius
     * @param step
     */

    public Sphere( float radius, double step) {
        this.mRaduis = radius;
        this.mStep = step;
        sphereVertex = FloatBuffer.allocate(40000);
        mPoints = build();
    }

    public void draw(GL10 gl) {
        gl.glFrontFace(GL10.GL_CW);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, sphereVertex);

        gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
        gl.glDrawArrays(GL10.GL_POINTS, 0, mPoints);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }

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
                sphereVertex.put((float) (mRaduis * Math.sin(phi) * Math.cos(theta)) );
                sphereVertex.put((float) (mRaduis * Math.sin(phi) * Math.sin(theta)) );
                sphereVertex.put((float) (mRaduis * Math.cos(phi)) );
                points++;

            }
        }
        sphereVertex.position(0);
        return points;
    }
    
    public FloatBuffer getSphereVertex() {
		return sphereVertex;
	}

}