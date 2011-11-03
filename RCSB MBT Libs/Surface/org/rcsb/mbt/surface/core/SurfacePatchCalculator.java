/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rcsb.mbt.surface.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
    private static float thresholdSq = 10.0f * 10.0f;
    TriangulatedSurface surface;
    Map<Integer, Integer> vertexMap = new HashMap<Integer, Integer>(); // maps original vertex indices to the truncated vertex list

    public SurfacePatchCalculator(TriangulatedSurface surface, int patchSize, List<Sphere> patch) {
        this.surface = surface;
        // truncate vertices and faces to the patch site region
        System.out.println("before vertices: " + surface.getVertices().size());
        truncateVertices(patchSize, patch);
        System.out.println("truncated vertices: " + surface.getVertices().size());
        truncateFaces();
    }

    public TriangulatedSurface getSurfacePatch() {
        return surface;
    }

    private void truncateVertices(int patchSize, List<Sphere> patch) {
        // truncate vertices array to just the patch spheres
        List<VertInfo> truncatedVertices = new ArrayList<VertInfo>();
        List<VertInfo> vertices = surface.getVertices();
        int vertCount = 0;

        Set<Object> patchObject = new HashSet<Object>(patch.size());
        for (Sphere s: patch) {
        	patchObject.add(s.getReference());
        }
        for (int i = 0; i < vertices.size(); i++) {
            VertInfo v = vertices.get(i);
       //     if (patchObject.contains(v.reference)) {
           if (v.atomid < patchSize) {
   //     	   System.out.println("adding vertex");
                truncatedVertices.add(v);
                vertexMap.put(i, vertCount);
                vertCount++;
            }
        }
        System.out.println("vertex count: "  + vertCount);

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

        surface.setFaces(truncatedFaces);
    }

    public static List<Sphere> calcSurroundings(List<Sphere> spheres, List<Sphere> patch) {
        List<Sphere> surroundings = new ArrayList<Sphere>();

        for (Sphere s : spheres) {
            Point3f ps = s.getLocation();
            for (Sphere p: patch) {
                Point3f pp = p.getLocation();
                if (ps.distanceSquared(pp) < thresholdSq) {
                    surroundings.add(p);
                    break;
                }
            }
        }

        return surroundings;
    }
}
