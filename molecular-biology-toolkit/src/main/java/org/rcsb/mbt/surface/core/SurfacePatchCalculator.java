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
