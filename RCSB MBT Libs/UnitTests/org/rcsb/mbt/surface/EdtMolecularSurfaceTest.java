/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rcsb.mbt.surface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.vecmath.Point3f;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rcsb.mbt.surface.datastructure.Sphere;
import org.rcsb.mbt.surface.datastructure.TriangulatedSurface;

import static org.junit.Assert.*;

/**
 *
 * @author Peter
 */
public class EdtMolecularSurfaceTest {
    // maximum deviation for comparing surface areas calculated
    // analytically vs. EdtSurfaceCalculator
    private static float maxDeviation = 0.1f;

    float PROBE_RADIUS = 1.4f;
    
    private static float rasrad[ ]={1.90f,1.88f,1.63f,1.48f,1.78f,1.2f,1.87f,1.96f,1.63f,0.74f,1.8f, 1.48f, 1.2f};//liang
    //                                 ca   c    n    o    s    h   p   cb    ne   fe  other ox  hx
    private static Sphere s1 = null;
    private static Sphere s2 = null;
    private static Sphere s3 = null;


    public EdtMolecularSurfaceTest() {
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
     * Test for EdtMolecularSurface with one sphere.
     */
    @Test
    public void testMS1() throws IOException {
        List<Sphere>spheres = new ArrayList<Sphere>();
        spheres.add(s1);

        SurfaceCalculator instance = new EdtMolecularSurface(spheres, PROBE_RADIUS, 1.0f);
        TriangulatedSurface surface = instance.getSurface();

        // expected data from original EDTSurf.exe (without smoothing!)
        float expArea = 24.661f;
        assertEquals(expArea, surface.getSurfaceArea(), 0.001f);
        assertEquals(290, surface.getVertices().size());
        assertEquals(576, surface.getFaces().size());
    }

    /**
     * Tests if the analytically calculated surface area matches the calculated
     * surface areas within a tolerance.
     * Note: this test works only if the parameter "fixfs" in EdtSurfaceCalculator
     * is set to at least 8. The default is 4.
     */
    @Test
    public void testMS1a() {
        List<Sphere>spheres = new ArrayList<Sphere>();
        spheres.add(s1);

        float radius = s1.getRadius();
        float area = 4.0f * (float)Math.PI * radius * radius;

        SurfaceCalculator instance = new EdtMolecularSurface(spheres, PROBE_RADIUS, 5.0f);
        TriangulatedSurface surface = instance.getSurface();

        assertEquals(area, surface.getSurfaceArea(), area*maxDeviation);
    }

    /**
     * Test for EdtMolecularSurface with two overlapping spheres.
     */
    @Test
    public void testMS2() {
        List<Sphere>spheres = new ArrayList<Sphere>();
        spheres.add(s1);
        spheres.add(s2);

        SurfaceCalculator instance = new EdtMolecularSurface(spheres, PROBE_RADIUS, 1.0f);
        TriangulatedSurface surface = instance.getSurface();

        // expected data from original EDTSurf.exe (without smoothing!)
        float expArea = 44.120f;
        assertEquals(expArea, surface.getSurfaceArea(), 0.001f);
        assertEquals(515, surface.getVertices().size());
        assertEquals(1026, surface.getFaces().size());
    }

    /**
     * Test for EdtMolecularSurface with three overlapping spheres.
     */
    @Test
    public void testMS3() {
        List<Sphere>spheres = new ArrayList<Sphere>();
        spheres.add(s1);
        spheres.add(s2);
        spheres.add(s3);

        SurfaceCalculator instance = new EdtMolecularSurface(spheres, PROBE_RADIUS, 1.0f);
        TriangulatedSurface surface = instance.getSurface();
        surface.computenorm();

        // expected data from original EDTSurf.exe (without smoothing!)
        float expArea = 57.850f;
        assertEquals(expArea, surface.getSurfaceArea(), 0.001f);
        assertEquals(684, surface.getVertices().size());
        assertEquals(1364, surface.getFaces().size());

    }

}