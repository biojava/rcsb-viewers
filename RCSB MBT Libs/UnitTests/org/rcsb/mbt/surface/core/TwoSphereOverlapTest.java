/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rcsb.mbt.surface.core;

import javax.vecmath.Point3f;
import org.junit.Before;
import org.junit.Test;
import org.rcsb.mbt.surface.core.TwoSphereOverlap;
import org.rcsb.mbt.surface.datastructure.Sphere;

import static org.junit.Assert.*;

/**
 *
 * @author Peter Rose
 */
public class TwoSphereOverlapTest {
    private Sphere sphere1;
    private Sphere sphere2;
    private float probeRadius;

    public TwoSphereOverlapTest() {
    }

    @Before
    public void setUp() {
        sphere1 = new Sphere(new Point3f(0, 0, 0), 2.0f, null);
        sphere2 = new Sphere(new Point3f(0, 0, 0), 1.5f, null);
        probeRadius = 1.5f;
    }

    /**
     * Test that the accessible and buried area add up to the total surface
     * area if the two spheres overlap by 0.1.
     */
    @Test
    public void testGetSurfaceArea1() {
        System.out.println("getAccesibleSurfaceArea1");
        float distance = sphere1.getRadius() + sphere2.getRadius() + 2* probeRadius -0.1f;
        sphere1.setLocation(new Point3f(distance, 0, 0));
        TwoSphereOverlap instance = new TwoSphereOverlap(sphere1, sphere2, probeRadius);
        float expResult = (float) ((4 * Math.PI * Math.pow(sphere1.getRadius()+probeRadius, 2)));
        float a1 = instance.getAccessibleSurfaceArea1();
        float b1 = instance.getBuriedSurfaceArea1();
        float result = a1 + b1;
        System.out.println("a1: " + a1 + " b1:" + b1);
        assertTrue(b1 > 0);
        assertEquals(expResult, result, Math.ulp(1.0f));
    }

    /**
     * Test that the accessible area is equal to the total area and that
     * the buried area is zero if the two spheres are 2*probeRadius apart.
     */
    @Test
    public void testGetSurfaceArea2() {
        System.out.println("getAccesibleSurfaceArea2");
        float distance = sphere1.getRadius() + sphere2.getRadius() + 2* probeRadius;
        sphere1.setLocation(new Point3f(distance, 0, 0));
        TwoSphereOverlap instance = new TwoSphereOverlap(sphere1, sphere2, probeRadius);
        float expResult = (float) ((4 * Math.PI * Math.pow(sphere1.getRadius()+probeRadius, 2)));
        float a1 = instance.getAccessibleSurfaceArea1();
        float b1 = instance.getBuriedSurfaceArea1();
        float result = a1 + b1;
        System.out.println("a1: " + a1 + " b1:" + b1);
        assertEquals(expResult, a1, Math.ulp(1.0f));
        assertEquals(0.0f, b1, Math.ulp(1.0f));
        assertEquals(expResult, result, Math.ulp(1.0f));
    }

    /**
     * Test that the accessible area is equal to the total area and that
     * the buried area is zero if the two spheres are more than 2*probeRadius apart.
     */
    @Test
    public void testGetSurfaceArea3() {
        System.out.println("getAccesibleSurfaceArea3");
        float distance = sphere1.getRadius() + sphere2.getRadius() + 2* probeRadius + 0.1f;
        sphere1.setLocation(new Point3f(distance, 0, 0));
        TwoSphereOverlap instance = new TwoSphereOverlap(sphere1, sphere2, probeRadius);
        float expResult = (float) ((4 * Math.PI * Math.pow(sphere1.getRadius()+probeRadius, 2)));
        float a1 = instance.getAccessibleSurfaceArea1();
        float b1 = instance.getBuriedSurfaceArea1();
        float result = a1 + b1;
        System.out.println("a1: " + a1 + " b1:" + b1);
        assertEquals(expResult, a1, Math.ulp(1.0f));
        assertEquals(0.0f, b1, Math.ulp(1.0f));
        assertEquals(expResult, result, Math.ulp(1.0f));
    }

}