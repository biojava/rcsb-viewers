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
public class FaceInfo {
	public int a,b,c;
	public Point3f pn = new Point3f();
	public float area;
	public boolean inout;//interior true
}
