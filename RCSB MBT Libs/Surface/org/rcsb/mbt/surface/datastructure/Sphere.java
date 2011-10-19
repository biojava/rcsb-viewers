/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rcsb.mbt.surface.datastructure;

import javax.vecmath.Point3f;

/**
 *
 * @author Peter
 */
public class Sphere {
    private Point3f location;
    private float radius;
    private Object reference;

    public Sphere() {
    }
    
    public Sphere(Point3f location, float radius, Object reference) {
        this.location = location;
        this.radius = radius;
        this.setReference(reference);
    }
    
    /**
     * @return the location
     */
    public Point3f getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(Point3f location) {
        this.location = location;
    }

    /**
     * @return the radius
     */
    public float getRadius() {
        return radius;
    }

    /**
     * @param radius the radius to set
     */
    public void setRadius(float radius) {
        this.radius = radius;
    }

    /**
     * @return the reference object related to this sphere
     */
    public Object getReference() {
		return reference;
	}
    
    /**
     * @param reference object for this sphere to set
     */
	private void setReference(Object reference) {
		this.reference = reference;
	}

	
}
