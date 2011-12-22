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

import javax.vecmath.Point3f;

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
