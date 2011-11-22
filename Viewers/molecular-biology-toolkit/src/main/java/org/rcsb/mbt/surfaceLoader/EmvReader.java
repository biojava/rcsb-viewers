package org.rcsb.mbt.surfaceLoader;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.vecmath.Color3b;
import javax.vecmath.Color4f;
import javax.vecmath.Point3f;

import org.rcsb.mbt.model.util.FastScanner;

/**
 *
 * @author Peter
 */
public class EmvReader {
    private Point3f[] vertices = null;
    private int[][] faces = null;
    private Color4f[] colors = null;

// http://www.okino.com/conv/imp_ply.htm
// The following table describes the element types that are parsed and imported.
//
    public void readEmv(String fileName) throws FileNotFoundException, IOException {
    	BufferedReader reader = new BufferedReader(new FileReader(fileName));
    	FastScanner scanner = new FastScanner();

    	List<Point3f> v = new ArrayList<Point3f>();
    	List<Integer> f1 = new ArrayList<Integer>();
    	List<Integer> f2 = new ArrayList<Integer>();
    	List<Integer> f3 = new ArrayList<Integer>();

    	float xmin = Float.MAX_VALUE;
    	float ymin = Float.MAX_VALUE;
    	float zmin = Float.MAX_VALUE;
    	float xmax = Float.MIN_VALUE;
    	float ymax = Float.MIN_VALUE;
    	float zmax = Float.MIN_VALUE;
    	
    	String line = "";
    	while ((line = reader.readLine()) != null) {
    		if (line.startsWith("v")) {
    			scanner.setString(line);
    			if (scanner.hasNext()) {
    				scanner.next();
    			}
    			float x = 0.0f;
    			if (scanner.hasNextFloat()) {
    				x = scanner.nextFloat();
    			}
    			float y = 0.0f;
    			if (scanner.hasNextFloat()) {
    				y = scanner.nextFloat();
    			}
    			float z = 0.0f;
    			if (scanner.hasNextFloat()) {
    				z = scanner.nextFloat();
    			}
    			
    			// NOTE: the z-coordinate must be flipped. Why ????
    			xmin = Math.min(xmin, x);
    			ymin = Math.min(xmin, y);
    			zmin = Math.min(xmin, z);
    			
    			xmax = Math.max(xmax, x);
    			ymax = Math.max(xmax, y);
    			zmax = Math.max(xmax, z);
    			v.add(new Point3f(x, y, -z));

    		} else if (line.startsWith("f")) {{
    			scanner.setString(line);
    			if (scanner.hasNext()) {
    				scanner.next();
    			}
    			int a = 0;
    			if (scanner.hasNextInt()) {
    				a = scanner.nextInt();
    			}
    			int b = 0;
    			if (scanner.hasNextInt()) {
    				b = scanner.nextInt();
    			}
    			int c = 0;
    			if (scanner.hasNextInt()) {
    				c = scanner.nextInt();
    			}
    			f1.add(a);
    			f2.add(b);
    			f3.add(c);
    		}
    	}
    	}
    	vertices = v.toArray(new Point3f[v.size()]);
    	colors = new Color4f[v.size()]; 
    	for (int i = 0; i < v.size(); i++) {
    		colors[i] = new Color4f(Color.CYAN);
    	}
    	faces = new int[f1.size()][3];
  
    	for (int i = 0; i < f1.size(); i++) {
    		faces[i][0] = f1.get(i);
    		faces[i][1] = f2.get(i);
    		faces[i][2] = f3.get(i);
    	
    	}
    	
    	System.out.println("min: " + xmin + " " + ymin + " " + zmin);
    	System.out.println("max: " + xmax + " " + ymax + " " + zmax);
    	System.out.println("rng: " + (xmax-xmin) + " " + (ymax-ymin) + " " + (zmax-zmin));
    	float xmid = (xmin + xmax) *0.5f;
    	float ymid = (ymin + ymax) *0.5f;
    	float zmid = (zmin + zmax) *0.5f;
    	xmid = 40;
    	ymid = 40;
    	zmid = 40;
    	Point3f offset = new Point3f(xmin + xmid, ymin + ymid, zmin + zmid);
    	for (int i = 0; i < vertices.length; i++) {
    		vertices[i].sub(offset);
    		vertices[i].scale(4.64f);
    	}
    }
    /**
     * @return the vertices
     */
    public Point3f[] getVertices() {
    	return vertices;
    }

    /**
     * @return the faces
     */
    public int[][] getFaces() {
    	return faces;
    }

    public Color4f[] getVertexColors() {
    	return colors;
    }

}
