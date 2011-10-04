/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rcsb.mbt.surface.datastructure;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

/**
 *
 * @author Peter
 */
public class VertInfo {
    public Point3f p = new Point3f();
	public Vector3f normal = new Vector3f();
	public int atomid;
	public boolean inout; // doesn't seem to be set, could be removed??
    public boolean iscont;//is concave surface
}
