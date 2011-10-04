/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rcsb.mbt.surface.core;

import org.rcsb.mbt.surface.datastructure.Sphere;

/**
 * TwoSphereOverlap calculates the solvent accessible and buried surface area of
 * two spheres that overlap using an analytical method.
 *
 * @author Peter Rose
 */
public class TwoSphereOverlap {
    private float radius1;
    private float radius2;
    private float probeRadius;
    private float distance;

    public TwoSphereOverlap(Sphere sphere1, Sphere sphere2, float probeRadius) {
       this.radius1 = sphere1.getRadius();
       this.radius2 = sphere2.getRadius();
       this.probeRadius = probeRadius;
       this.distance = sphere1.getLocation().distance(sphere2.getLocation());
    }

    public float getAccessibleSurfaceArea1() {
        return area(radius1, probeRadius) - getBuriedSurfaceArea1();
    }

    public float getBuriedSurfaceArea1() {
        return buriedArea(radius1, radius2, probeRadius, distance);
    }

    public float getAccessibleSurfaceArea2() {
        return area(radius2, probeRadius) - getBuriedSurfaceArea2();
    }

    public float getBuriedSurfaceArea2() {
        return buriedArea(radius2, radius1, probeRadius, distance);
    }

    private static float area(float radius, float probeRadius) {
        return (float) (4 * Math.PI * (radius + probeRadius)*(radius + probeRadius));
    }

    /*
     * Returns buried surface area for the first sphere based on the formula (3) in
     * S. Wodak, J Janin, Proc. Natl. Acad. Sci. USA (1980), 77, 1736-1740.
     */
    private static float buriedArea(float radius1, float radius2, float probeRadius, float distance) {
        float r2 = radius1 + radius2 + 2* probeRadius - distance;
        if (r2 < 0.0f) {
            return 0.0f;
        }
        if (distance == 0.0f) {
        // how should we deal with this case??
        }
        return (float)Math.PI * (radius2 + probeRadius) * r2 * (1 + (radius1 - radius2)/distance);
    }
}
