/**
 * 
 */
package org.rcsb.mbt.surface.datastructure;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.vecmath.Point3f;

import org.junit.BeforeClass;
import org.junit.Test;
import org.rcsb.mbt.surface.EdtMolecularSurface;
import org.rcsb.mbt.surface.SurfaceCalculator;


/**
 * @author Peter Rose
 *
 */
public class TriangulatedSurfaceTest {
	private static TriangulatedSurface surface1 = null;
	private static TriangulatedSurface surface2 = null;
	private static TriangulatedSurface surface3 = null;
	
	@BeforeClass
    public static void setUpClass() {
		surface1 = new TriangulatedSurface();
		surface2 = new TriangulatedSurface();
		surface3 = new TriangulatedSurface();
		
		FaceInfo face1 = new FaceInfo();
    	face1.a = 0;
    	face1.b = 1;
    	face1.c = 2;
    	List<FaceInfo> faces1 = new ArrayList<FaceInfo>();
    	faces1.add(face1);
    	surface1.setFaces(faces1);
    	
    	surface2.setFaces(faces1);
    	
    	FaceInfo face3 = new FaceInfo();
    	face3.a = 3;
    	face3.b = 4;
    	face3.c = 5;
    	List<FaceInfo> faces3 = new ArrayList<FaceInfo>();
    	faces3.addAll(faces1);
    	faces3.addAll(faces3);
    	surface3.setFaces(faces3);
    	
    	// vertices for face 1
    	VertInfo vertex1 = new VertInfo();
    	vertex1.p = new Point3f(0.0f, 1.0f, 0.0f);  	
    	VertInfo vertex2 = new VertInfo();
    	vertex2.p = new Point3f(0.0f, -1.0f, 1.0f);	
    	VertInfo vertex3 = new VertInfo();
    	vertex3.p = new Point3f(0.0f, -1.0f, -1.0f);
    	List<VertInfo> vertices1 = new ArrayList<VertInfo>();
    	vertices1.add(vertex1);
    	vertices1.add(vertex2);
    	vertices1.add(vertex3);
    	surface1.setVertices(vertices1);
    	
    	// vertices for face 2, shifted by -2 along y-axis
    	VertInfo vertex4 = new VertInfo();
    	vertex4.p = new Point3f(0.0f, -1.0f, 0.0f);	
    	VertInfo vertex5 = new VertInfo();
    	vertex5.p = new Point3f(0.0f, -3.0f, 1.0f);
    	VertInfo vertex6 = new VertInfo();
    	vertex6.p = new Point3f(0.0f, -3.0f, -1.0f);
    	List<VertInfo> vertices2 = new ArrayList<VertInfo>();
    	vertices2.add(vertex4);
    	vertices2.add(vertex5);
    	vertices2.add(vertex6);
    	surface2.setVertices(vertices2);
    	
    	List<VertInfo> vertices3 = new ArrayList<VertInfo>();
    	vertices3.addAll(vertices1);
    	vertices3.addAll(vertices2);
    	surface3.setVertices(vertices3);
	}
	
}
