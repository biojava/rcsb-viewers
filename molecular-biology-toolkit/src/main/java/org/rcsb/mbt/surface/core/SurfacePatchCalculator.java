/*
 * BioJava development code
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence. This should
 * be distributed with the code. If you do not have a copy,
 * see:
 *
 * http://www.gnu.org/copyleft/lesser.html
 *
 * Copyright for this code is held jointly by the individual
 * authors. These should be listed in @author doc comments.
 *
 * For more information on the BioJava project and its aims,
 * or to join the biojava-l mailing list, visit the home page
 * at:
 *
 * http://www.biojava.org/
 *
 * This code was contributed from the Molecular Biology Toolkit
 * (MBT) project at the University of California San Diego.
 *
 * Please reference J.L. Moreland, A.Gramada, O.V. Buzko, Qing
 * Zhang and P.E. Bourne 2005 The Molecular Biology Toolkit (MBT):
 * A Modular Platform for Developing Molecular Visualization
 * Applications. BMC Bioinformatics, 6:21.
 *
 * The MBT project was funded as part of the National Institutes
 * of Health PPG grant number 1-P01-GM63208 and its National
 * Institute of General Medical Sciences (NIGMS) division. Ongoing
 * development for the MBT project is managed by the RCSB
 * Protein Data Bank(http://www.pdb.org) and supported by funds
 * from the National Science Foundation (NSF), the National
 * Institute of General Medical Sciences (NIGMS), the Office of
 * Science, Department of Energy (DOE), the National Library of
 * Medicine (NLM), the National Cancer Institute (NCI), the
 * National Center for Research Resources (NCRR), the National
 * Institute of Biomedical Imaging and Bioengineering (NIBIB),
 * the National Institute of Neurological Disorders and Stroke
 * (NINDS), and the National Institute of Diabetes and Digestive
 * and Kidney Diseases (NIDDK).
 *
 * Created on 2011/11/08
 *
 */

package org.rcsb.mbt.surface.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.rcsb.mbt.surface.datastructure.FaceInfo;
import org.rcsb.mbt.surface.datastructure.Sphere;
import org.rcsb.mbt.surface.datastructure.TriangulatedSurface;
import org.rcsb.mbt.surface.datastructure.VertInfo;

/**
 *
 * @author Peter Rose
 */
public class SurfacePatchCalculator {
    TriangulatedSurface surface;
    Map<Integer, Integer> vertexMap = new HashMap<Integer, Integer>(); // maps original vertex indices to the truncated vertex list

    public SurfacePatchCalculator(TriangulatedSurface surface, List<Sphere> context, float distanceThreshold) {
        this.surface = surface;
        // truncate vertices and faces to the patch site region
        System.out.println("before vertices: " + surface.getVertices().size());
        truncateByDistance(context, distanceThreshold);
        System.out.println("truncated vertices: " + surface.getVertices().size());
        truncateFaces();
        flipper();
        smoothEdges();      
        System.out.println("truncated vertices after smoothing: " + surface.getVertices().size());
    }

    public TriangulatedSurface getSurfacePatch() {
        return surface;
    }

    private void truncateByDistance(List<Sphere> context, float distanceThreshold) {
    	 List<VertInfo> truncatedVertices = new ArrayList<VertInfo>();
         List<VertInfo> vertices = surface.getVertices();
  
         float thresholdSq = distanceThreshold * distanceThreshold;
         
         int vertCount = 0;
         for (int i = 0; i < vertices.size(); i++ ) {
        	 VertInfo v = vertices.get(i);
             for (Sphere s : context) {
                 Point3f ps = s.getLocation();       
                 if (ps.distanceSquared(v.p) < thresholdSq) {
                     vertexMap.put(i,vertCount);
                     vertCount++;
                     truncatedVertices.add(v);
                     break;
                 }
             }
         }
         
         surface.setVertices(truncatedVertices);
    }

    private void truncateFaces() {
        List<FaceInfo> truncatedFaces = new ArrayList<FaceInfo>();
        List<FaceInfo> faces = surface.getFaces();

        for (FaceInfo f: faces) {
            if (vertexMap.containsKey(f.a) &&
                vertexMap.containsKey(f.b) &&
                vertexMap.containsKey(f.c))  {
                // re-index faces with vertex indices based on truncated vertices
                f.a = vertexMap.get(f.a);
                f.b = vertexMap.get(f.b);
                f.c = vertexMap.get(f.c);
                truncatedFaces.add(f);
            }
        }
        vertexMap.clear();
        surface.setFaces(truncatedFaces);
    }
    
    private void smoothEdges() {
    	int vertexCount = 0;
    	do {
    		vertexCount = surface.getVertices().size();
    		System.out.println("smoothing: " + vertexCount);
    		removeJaggedEdge();
    		truncateFaces();
    	} while (surface.getVertices().size() < vertexCount);
    }
    
    private void removeJaggedEdge() {
    	int[] edgeCount = new int[surface.getVertices().size()];
    	List<FaceInfo> faces = surface.getFaces();
    	for (FaceInfo f: faces) {
    		edgeCount[f.a]++;
    		edgeCount[f.b]++;
    		edgeCount[f.c]++;
    	}
    	
    	List<VertInfo> vertices = surface.getVertices();
    	List<VertInfo> truncatedVertices = new ArrayList<VertInfo>();
    	
    	int vertCount = 0;
    	for (int i = 0; i < edgeCount.length; i++) {
    		if (edgeCount[i] > 1) {
    			vertexMap.put(i,vertCount);
                vertCount++;
                truncatedVertices.add(vertices.get(i));
    		}
    	}
        surface.setVertices(truncatedVertices);
    }
    
    /**
     * Flip triangles and remove triangle with vertex v0
     *         v0
     *        / |\
     *       /  | \
     * -----v1  | v2---  change to ----v1---v2----
     *       \  |/                     \   /
     *        \v3                       \v3
     *        
     * @param v0
     * @param v1
     * @param v2
     * @param v3
     */
 
    private List<List<FaceInfo>> getVertex2FaceMap() {
    	List<FaceInfo> faces = surface.getFaces();
    	List<VertInfo> vertices = surface.getVertices();
    	List<List<FaceInfo>> map = new ArrayList<List<FaceInfo>>(vertices.size());
    	for (int i = 0, n = vertices.size(); i < n; i++) {
    		map.add(new ArrayList<FaceInfo>());
    	}
    	for (FaceInfo f: faces) {
    		map.get(f.a).add(f);
    		map.get(f.b).add(f);
    		map.get(f.c).add(f);
    	}
    	return map;
    }
    
    /**
     * Flip triangles
     *         v0
     *        / |\
     *       /  | \
     * -----v1  | v2---  change to ----v1---v2----
     *       \  |/                     \   /
     *        \v3                       \v3
     */
    private void flipper() {
    	float ANGLE_THRESHOLD = (float) Math.toRadians(150.0f);
    	List<Integer> s1 = new ArrayList<Integer>(3);
       	List<Integer> s2 = new ArrayList<Integer>(3);
       	List<Integer> s3 = new ArrayList<Integer>(4);
       	Vector3f v1 = new Vector3f();
       	Vector3f v2 = new Vector3f();
       	
    	List<List<FaceInfo>> map = getVertex2FaceMap();
    	List<VertInfo> vertices = surface.getVertices();
    	for (int i = 0, n = vertices.size(); i < n; i++) {
    		List<FaceInfo> neighbors = map.get(i);
    		if (neighbors.size() == 2) {
    			s1.clear();
    			s2.clear();
    			s3.clear();
    			FaceInfo f1 = neighbors.get(0);
    			s1.add(f1.a);
    			s1.add(f1.b);
    			s1.add(f1.c);
    			FaceInfo f2 = neighbors.get(1);
    			s2.add(f2.a);
    			s2.add(f2.b);
    			s2.add(f2.c);
    			s3.addAll(s1);
    			s3.addAll(s2);
    		//	System.out.println("i : " + i);
    		//	System.out.println("s1: " + s1);
    		//	System.out.println("s2: " + s2);
    			// there should be two vertices in common
    			s1.retainAll(s2);
    			if (s1.size() !=2) 
    				continue;
    			// and there should be two vertices not in common
    			s3.removeAll(s1);
    			if (s3.size() !=2) 
    				continue;
    			s1.remove(new Integer(i));
    		//	System.out.println("Common: "  + s1);
    		//	System.out.println("Disjoint:" + s3);
    			Point3f p0 = vertices.get(i).p;
    			int i1 = s3.get(0);
    			int i2 = s3.get(1);
    			int i3 = s1.get(0);
    			Point3f p1 = vertices.get(i1).p;
    			Point3f p2 = vertices.get(i2).p;
    			v1.sub(p1,p0);
    			v2.sub(p2,p0);
    			float angle = v1.angle(v2);
    			if (Float.isNaN(angle)) {
    				System.err.println("SurfacePatchCalculator: angle is NaN");
    				System.err.println("v1: " + v1);
    				System.err.println("v2: " + v2);
    				continue;
    			}
    			if (angle < ANGLE_THRESHOLD) {
    	//			System.out.println("flip angle: " + Math.toDegrees(angle) + " dot: " + v1.dot(v2));
//    				System.out.println("f1 orig: " + f1.a + " " + f1.b + " " + f1.c);
//			    	System.out.println("f2 orig: " + f2.a + " " + f2.b + " " + f2.c);
    				boolean flipped = false;
    				if (flipFace(f1, i3, i2)) {
    					flipped = flipFace(f2, i, i1);
    				} else if (flipFace(f1, i3, i1)) {
    					flipped = flipFace(f2, i, i2);
    				}
    			    if (!flipped) {
    			    	System.err.println("SurfacePatchCalculator: problem with edge flipping");
//    			    	System.out.println("f1 flip: " + f1.a + " " + f1.b + " " + f1.c);
//    			    	System.out.println("f2 flip: " + f2.a + " " + f2.b + " " + f2.c);
    			    }
    			}
    			
    		}
    	}
    }
    
    /**
     * Changes vertex v1 to v2 in a face. Returns true if exchange
     * was successful.
     * @param f
     * @param v1
     * @param v2
     * @return
     */
    private boolean flipFace(FaceInfo f, int v1, int v2) {
    	if (f.a == v1) {
    		f.a = v2;
    		return true;
    	}
    	if (f.b == v1) {
    		f.b = v2;
    		return true;
    	}
    	if (f.c == v1) {
    		f.c = v2;
    		return true;
    	}
    	return false;
    }
    
    private void removeFragments() {
    	// only keep largest surface
    	// TODO implement
    }

    public static List<Sphere> calcSurroundings(List<Sphere> patch, List<Sphere> context, float distanceThreshold) {
        List<Sphere> surroundings = new ArrayList<Sphere>();
        float thresholdSq = distanceThreshold * distanceThreshold;
        for (Sphere s : patch) {
            Point3f ps = s.getLocation();
            for (Sphere p: context) {
                Point3f pp = p.getLocation();
                if (ps.distanceSquared(pp) < thresholdSq) {
                    surroundings.add(s);
                    break;
                }
            }
        }

        return surroundings;
    }
}
