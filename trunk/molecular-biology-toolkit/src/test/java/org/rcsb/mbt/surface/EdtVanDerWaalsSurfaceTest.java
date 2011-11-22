/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rcsb.mbt.surface;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.vecmath.Point3f;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rcsb.mbt.surface.core.TwoSphereOverlap;
import org.rcsb.mbt.surface.datastructure.FaceInfo;
import org.rcsb.mbt.surface.datastructure.Sphere;
import org.rcsb.mbt.surface.datastructure.TriangulatedSurface;
import org.rcsb.mbt.surface.datastructure.VertInfo;
import org.rcsb.mbt.surface.io.PlyReader;
import org.rcsb.mbt.surface.io.PlyWriter;

import static org.junit.Assert.*;

/**
 *
 * @author Peter
 */
public class EdtVanDerWaalsSurfaceTest {
    // maximum deviation for comparing surface areas calculated
    // analytically vs. EdtSurfaceCalculator
    private static float maxDeviation = 0.1f;
    private static float rasrad[ ]={1.90f,1.88f,1.63f,1.48f,1.78f,1.2f,1.87f,1.96f,1.63f,0.74f,1.8f, 1.48f, 1.2f};//liang
    //                                 ca   c    n    o    s    h   p   cb    ne   fe  other ox  hx
    private static Sphere s1 = null;
    private static Sphere s2 = null;
    private static Sphere s3 = null;


    public EdtVanDerWaalsSurfaceTest() {
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
     * Test for EdtVanDerWaalsSurface with one sphere.
     */
    @Test
    public void testVDS1() throws FileNotFoundException, IOException {
        List<Sphere>spheres = new ArrayList<Sphere>();
        spheres.add(s1);

        SurfaceCalculator instance = new EdtVanDerWaalsSurface(spheres, 1.0f);
        TriangulatedSurface surface = instance.getSurface();

        // expected data from original EDTSurf.exe (without smoothing!)
        assertEquals(35.870f, surface.getSurfaceArea(), 3.0f);
        assertEquals(446, surface.getVertices().size());
        assertEquals(888, surface.getFaces().size());
        // areas match EDTSurf if no surface smoothing is applied
        // read reference result file from original EDTSurf application
        PlyReader reader = new PlyReader();
  //      reader.readPly("test/org/rcsb/mbt/surface/EDTSurf Testruns/1atom_vdw.ply");
 //       reader.readPly("test/org/rcsb/mbt/surface/EDTSurf Testruns/1atom_vdw_nosmooth.ply");
        reader.readPly("src/test/resources/test-input/SurfaceTest/1atom_vdw_nosmooth.ply");
        VertInfo[] vertices = reader.getVertices();
        FaceInfo[] faces = reader.getFaces();

        assertEquals(446, vertices.length);
        assertEquals(888, faces.length);

 //       surface.laplaciansmooth(1);
 //       surface.computenorm();
        for (int i = 0; i < faces.length; i++) {
            assertEquals(faces[i].a, surface.getFaces().get(i).a);
            assertEquals(faces[i].b, surface.getFaces().get(i).b);
            assertEquals(faces[i].c, surface.getFaces().get(i).c);
        }
        for (int i = 0; i < vertices.length; i++) {//
            assertEquals(vertices[i].p.x, surface.getVertices().get(i).p.x, 0.05f);
            assertEquals(vertices[i].p.y, surface.getVertices().get(i).p.y, 0.05f);
            assertEquals(vertices[i].p.z, surface.getVertices().get(i).p.z, 0.05f);
        }
        PlyWriter writer = new PlyWriter();
   //     writer.outputply(surface, "test/org/rcsb/mbt/surface/Testoutput/1atom_vdw.ply");
       // writer.outputply(surface, "target/test-output/SurfaceTest/1atom_vdw_nosmooth.ply");
    }

    /**
     * Test for EdtVanDerWaalsSurface with one sphere.
     */
    @Test
    public void testVS1a() {
        List<Sphere>spheres = new ArrayList<Sphere>();
        spheres.add(s1);

        float radius = s1.getRadius();
        float area = 4.0f * (float)Math.PI * radius * radius;

        SurfaceCalculator instance = new EdtVanDerWaalsSurface(spheres, 1.0f);
        TriangulatedSurface surface = instance.getSurface();
//        surface.laplaciansmooth(1);
//        surface.computenorm();

        assertEquals(area, surface.getSurfaceArea(), area*maxDeviation);
    }

    /**
     * Test for EdtVanDerWaalsSurface with two overlapping spheres.
     */
    @Test
    public void testVS2() {
        List<Sphere>spheres = new ArrayList<Sphere>();
        spheres.add(s1);
        spheres.add(s2);

        SurfaceCalculator instance = new EdtVanDerWaalsSurface(spheres, 1.0f);
        TriangulatedSurface surface = instance.getSurface();

        // expected data from original EDTSurf.exe (without smoothing!)
        float expArea = 62.482f;
        assertEquals(expArea, surface.getSurfaceArea(), 100*Math.ulp(expArea));
        assertEquals(762, surface.getVertices().size());
        assertEquals(1520, surface.getFaces().size());
    }

    /**
     * Test for EdtVanDerWaalsSurface with three overlapping spheres.
     */
    @Test
    public void testVS3() {
        List<Sphere>spheres = new ArrayList<Sphere>();
        spheres.add(s1);
        spheres.add(s2);
        spheres.add(s3);

        SurfaceCalculator instance = new EdtVanDerWaalsSurface(spheres, 1.0f);
        TriangulatedSurface surface = instance.getSurface();

        // expected data from original EDTSurf.exe (without smoothing!)
        float expArea = 81.644f;
        assertEquals(expArea, surface.getSurfaceArea(), 100*Math.ulp(expArea));
        assertEquals(999, surface.getVertices().size());
        assertEquals(1994, surface.getFaces().size());
    }

    /**
     * Tests if the surface area calculated for a sphere (patch) that is partially
     * covered by another sphere (context) gives the same result within a tolerance as
     * an exact solution calculated with the TwoSphereOverlap class.
     */
    @Test
    public void testVS4() {
        Sphere sphere1 = new Sphere(new Point3f(0, 0, 0), 2.0f, null);
        List<Sphere>patch = new ArrayList<Sphere>();
        patch.add(sphere1);

        Sphere sphere2 = new Sphere(new Point3f(0.5f, 0, 0), 2.0f, null);
        List<Sphere>context = new ArrayList<Sphere>();
        context.add(sphere2);

        SurfaceCalculator instance = new EdtVanDerWaalsSurface(patch, context, 5.0f);
        TriangulatedSurface surface = instance.getSurface();

        float probeRadius = 0.0f;
        TwoSphereOverlap overlap = new TwoSphereOverlap(sphere1, sphere2, probeRadius);
        System.out.println("exp: " + overlap.getAccessibleSurfaceArea1() + " calc: " + surface.getSurfaceArea());
        assertEquals(overlap.getAccessibleSurfaceArea1(), surface.getSurfaceArea(), overlap.getAccessibleSurfaceArea1()*maxDeviation);
    }
}