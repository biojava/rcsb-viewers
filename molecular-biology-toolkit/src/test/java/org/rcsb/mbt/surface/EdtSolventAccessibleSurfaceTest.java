/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rcsb.mbt.surface;

import java.util.ArrayList;
import java.util.List;
import javax.vecmath.Point3f;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rcsb.mbt.surface.core.TwoSphereOverlap;
import org.rcsb.mbt.surface.datastructure.Sphere;
import org.rcsb.mbt.surface.datastructure.TriangulatedSurface;

import static org.junit.Assert.*;

/**
 *
 * @author Peter
 */
public class EdtSolventAccessibleSurfaceTest {
    // maximum deviation for comparing surface areas calculated
    // analytically vs. EdtSurfaceCalculator
    private static float maxDeviation = 0.1f;

    float PROBE_RADIUS = 1.4f;

    private static float rasrad[ ]={1.90f,1.88f,1.63f,1.48f,1.78f,1.2f,1.87f,1.96f,1.63f,0.74f,1.8f, 1.48f, 1.2f};//liang
    //                                 ca   c    n    o    s    h   p   cb    ne   fe  other ox  hx
    private static Sphere s1 = null;
    private static Sphere s2 = null;
    private static Sphere s3 = null;


    public EdtSolventAccessibleSurfaceTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
//  ATOM      1  N   ALA A  13      22.637   5.768  11.762  1.00 44.60           N
//  ATOM      2  CA  ALA A  13      23.655   4.852  11.146  1.00 44.15           C
//  ATOM      3  C   ALA A  13      24.276   5.552   9.942  1.00 43.61           C
        s1 = new Sphere();
        s1.setRadius(rasrad[2]);
        s1.setLocation(new Point3f(22.637f, 5.768f, 11.762f));
        s2 = new Sphere();
        s2.setRadius(rasrad[0]);
        s2.setLocation(new Point3f(23.655f, 4.852f, 11.146f));
        s3 = new Sphere();
        s3.setRadius(rasrad[1]);
        s3.setLocation(new Point3f(24.276f, 5.552f, 9.942f));
    }

    /**
     * Test for EdtSolventAccessibleSurface with one sphere.
     */
    @Test
    public void testSAS1() {
        List<Sphere>spheres = new ArrayList<Sphere>();
        spheres.add(s1);

        SurfaceCalculator instance = new EdtSolventAccessibleSurface(spheres, PROBE_RADIUS, 1.0f);
        TriangulatedSurface surface = instance.getSurface();

        // expected data from original EDTSurf.exe (without smoothing!)
        float expArea = 126.124f;
        assertEquals(expArea, surface.getSurfaceArea(), 0.001f);
        assertEquals(1550, surface.getVertices().size());
        assertEquals(3096, surface.getFaces().size());
    }

    /**
     * Test for EdtMolecularSurface with one sphere.
     */
    @Test
    public void testSAS1a() {
        List<Sphere>spheres = new ArrayList<Sphere>();
        spheres.add(s1);

        float radius = s1.getRadius() + 1.4f;
        float area = 4.0f * (float)Math.PI * radius * radius;

        SurfaceCalculator instance = new EdtSolventAccessibleSurface(spheres, PROBE_RADIUS, 1.0f);
        TriangulatedSurface surface = instance.getSurface();

        assertEquals(area, surface.getSurfaceArea(), area*maxDeviation);
    }

    /**
     * Test for EdtSolventAccessibleSurface with two overlapping spheres.
     */
    @Test
    public void testSAS2() {
        List<Sphere>spheres = new ArrayList<Sphere>();
        spheres.add(s1);
        spheres.add(s2);

        SurfaceCalculator instance = new EdtSolventAccessibleSurface(spheres, PROBE_RADIUS, 1.0f);
        TriangulatedSurface surface = instance.getSurface();

        // expected data from original EDTSurf.exe (without smoothing!)
        float expArea = 169.742f;
        // Note: in this case the surface area is off by about 0.003. 
        // Is this due to float point arithmetic?
        assertEquals(expArea, surface.getSurfaceArea(), 0.003f);
        assertEquals(2083, surface.getVertices().size());
        assertEquals(4162, surface.getFaces().size());

    }

    /**
     * Test for EdtSolventAccessibleSurface with three overlapping spheres.
     */
    @Test
    public void testSAS3() {
        List<Sphere>spheres = new ArrayList<Sphere>();
        spheres.add(s1);
        spheres.add(s2);
        spheres.add(s3);

        SurfaceCalculator instance = new EdtSolventAccessibleSurface(spheres, PROBE_RADIUS, 1.0f);
        TriangulatedSurface surface = instance.getSurface();

        // expected data from original EDTSurf.exe (without smoothing!)
        float expArea = 198.276f;
        // Note: in this case the surface area is off by about 0.005. 
        // Is this due to float point arithmetic?
        assertEquals(expArea, surface.getSurfaceArea(), 0.005f);
        assertEquals(2420, surface.getVertices().size());
        assertEquals(4836, surface.getFaces().size());
    }

    /**
     * Tests if the surface area calculated for a sphere (patch) that is partially
     * covered by another sphere (context) gives the same result within a tolerance as
     * an exact solution calculated with the TwoSphereOverlap class.
     */
    @Test
    public void testSAS4() {
        Sphere sphere1 = new Sphere(new Point3f(0, 0, 0), 2.0f, null);
        List<Sphere>patch = new ArrayList<Sphere>();
        patch.add(sphere1);

        Sphere sphere2 = new Sphere(new Point3f(3.0f, 0, 0), 2.0f, null);
        List<Sphere>context = new ArrayList<Sphere>();
        context.add(sphere2);

        SurfaceCalculator instance = new EdtSolventAccessibleSurface(patch, context, PROBE_RADIUS, 6.0f, 5.0f);
        TriangulatedSurface surface = instance.getSurface();

        TwoSphereOverlap overlap = new TwoSphereOverlap(sphere1, sphere2, PROBE_RADIUS);
        System.out.println("exp: " + overlap.getAccessibleSurfaceArea1() + " calc: " + surface.getSurfaceArea());
        assertEquals(overlap.getAccessibleSurfaceArea1(), surface.getSurfaceArea(), overlap.getAccessibleSurfaceArea1()*maxDeviation);
    }
}