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
 * The code for laplacian smoothing was adopted from the EDTSurf
 * C++ code (http://zhanglab.ccmb.med.umich.edu/EDTSurf) contributed 
 * by Dong Xu and Yang Zhang at the University of Michigan, Ann Arbor. 
 * 
 * Please reference D. Xu, Y. Zhang (2009) 
 * Generating Triangulated Macromolecular Surfaces by Euclidean 
 * Distance Transform. PLoS ONE 4(12): e8140.
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
 */

package org.rcsb.mbt.surface.datastructure;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

/**
 * Represents a triangulated surface defined by a list of vertices
 * and faces.
 * 
 * @author Peter Rose
 */
public class TriangulatedSurface {

    private List<VertInfo> vertices = new ArrayList<VertInfo>(0);
    private List<FaceInfo> faces = new ArrayList<FaceInfo>(0);

    public void setVertices(List<VertInfo> vertices) {
        this.vertices = vertices;
    }

    public void setFaces(List<FaceInfo> faces) {
        this.faces = faces;
    }

    public List<VertInfo> getVertices() {
        return vertices;
    }

    public List<FaceInfo> getFaces() {
        return faces;
    }
    
    public List<LineInfo> getLines() {
    	Set<LineInfo> lines = new HashSet<LineInfo>();
    	for (FaceInfo f: faces) {
    		// make sure to add each line only once
    		LineInfo lab = new LineInfo(f.a,f.b);
    		if (! lines.contains(lab)) {
    			lines.add(lab);
    		} else {
    			lab = null;
    		}
    		LineInfo lac = new LineInfo(f.a,f.c);
    		if (! lines.contains(lac)) {
    			lines.add(lac);
    		} else {
    			lac = null;
    		}
    		LineInfo lbc = new LineInfo(f.b,f.c);
    		if (! lines.contains(lbc)) {
    			lines.add(lbc);
    		} else {
    			lbc = null;
    		}
    	}
    	return new ArrayList<LineInfo>(lines);
    }

    /**
     * Calculates the area of the face (triangle) using Heron's formula.
     * @param faceIndex
     * @return area of the face (triangle)
     */
    public float getFaceArea(int faceIndex) {
        int fa = faces.get(faceIndex).a;
        int fb = faces.get(faceIndex).b;
        int fc = faces.get(faceIndex).c;
        float d1 = vertices.get(fa).p.distance(vertices.get(fb).p);
        float d2 = vertices.get(fb).p.distance(vertices.get(fc).p);
        float d3 = vertices.get(fc).p.distance(vertices.get(fa).p);
        float s = 0.5f * (d1 + d2 + d3); // half of the perimeter
        return (float) Math.sqrt(s * (s-d1) * (s-d2) * (s-d3));
    }

    /**
     * Calculates the surface area.
     * @return surface area
     */
    public float getSurfaceArea() {
        float area = 0.0f;
        for (int i = 0, n = faces.size(); i < n; i++) {
            area += getFaceArea(i);
        }
        return area;
    }

    public Point3f getCentroid() {
    	Point3f centroid = new Point3f();
    	for (VertInfo v: vertices) {
    		centroid.add(v.p);
    	}
    	System.out.println("Centroid sum: " + centroid);
    	if (vertices.size() > 0) {
    		centroid.scale(1.0f/vertices.size());
    	}
    	System.out.println("Centroid scaled: " + centroid);
    	return centroid;
    }
    
    public Vector3f getCompositeNormal() {
    	Vector3f normal = new Vector3f();
    	for (VertInfo v: vertices) {
    		normal.add(v.normal);
    	}
    	normal.normalize();
    	return normal;
    }
    
    public void computenorm() {
        Vector3f ab = new Vector3f();
        Vector3f ac = new Vector3f();
        Vector3f faceNormal = new Vector3f();

        for (VertInfo v : vertices) {
            v.normal.set(0.0f, 0.0f, 0.0f);
        }

        for (FaceInfo f : faces) {
            VertInfo va = vertices.get(f.a);
            VertInfo vb = vertices.get(f.b);
            VertInfo vc = vertices.get(f.c);
            ab.sub(vb.p, va.p);
            ac.sub(vc.p, va.p);
            faceNormal.cross(ab, ac);
            faceNormal.normalize();
            f.pn = faceNormal;
            va.normal.add(faceNormal);
            vb.normal.add(faceNormal);
            vc.normal.add(faceNormal);
        }

        for (int i = 0; i < vertices.size(); i++) {
            Vector3f v = vertices.get(i).normal;
            v.normalize();
        }
    }
    
    public void laplaciansmooth(int numiter) {

    	// TODO
 //       float scalefactor = 4.0f; // note, this is the original scalefactor from EDTSurf, How should it be set???
        // coords used here are the actual coordinates, not the scaled coordinates
 
    	// typically, the scalefactor is around 2. or lower
    	float scalefactor = 2.0f;
    	int vertnumber = vertices.size();
        Point3f[] tps = new Point3f[vertnumber];
        for (int i = 0; i < tps.length; i++) {
            tps[i] = new Point3f();
        }
        int[][] vertdeg = new int[20][vertnumber];
        int i, j;
        boolean flagvert;
//	for(i=0;i<20;i++)
//	{
//		vertdeg[i]=new int[vertnumber];
//	}
//	for(i=0;i<vertnumber;i++)
//	{
//		vertdeg[0][i]=0;
//	}
//        FaceInfo[] faces = this.faces.toArray(new FaceInfo[0]);
//        int facenumber = faces.length;
        for (FaceInfo face : faces) {
            //a
            flagvert = true;
            for (j = 0; j < vertdeg[0][face.a]; j++) {
                if (face.b == vertdeg[j + 1][face.a]) {
                    flagvert = false;
                    break;
                }
            }
            if (flagvert) {
                vertdeg[0][face.a]++;
                vertdeg[vertdeg[0][face.a]][face.a] = face.b;

            }
            flagvert = true;
            for (j = 0; j < vertdeg[0][face.a]; j++) {
                if (face.c == vertdeg[j + 1][face.a]) {
                    flagvert = false;
                    break;
                }
            }
            if (flagvert) {
                vertdeg[0][face.a]++;
                vertdeg[vertdeg[0][face.a]][face.a] = face.c;

            }
            //b
            flagvert = true;
            for (j = 0; j < vertdeg[0][face.b]; j++) {
                if (face.a == vertdeg[j + 1][face.b]) {
                    flagvert = false;
                    break;
                }
            }
            if (flagvert) {
                vertdeg[0][face.b]++;
                vertdeg[vertdeg[0][face.b]][face.b] = face.a;

            }
            flagvert = true;
            for (j = 0; j < vertdeg[0][face.b]; j++) {
                if (face.c == vertdeg[j + 1][face.b]) {
                    flagvert = false;
                    break;
                }
            }
            if (flagvert) {
                vertdeg[0][face.b]++;
                vertdeg[vertdeg[0][face.b]][face.b] = face.c;

            }
            //c
            flagvert = true;
            for (j = 0; j < vertdeg[0][face.c]; j++) {
                if (face.a == vertdeg[j + 1][face.c]) {
                    flagvert = false;
                    break;
                }
            }
            if (flagvert) {
                vertdeg[0][face.c]++;
                vertdeg[vertdeg[0][face.c]][face.c] = face.a;

            }
            flagvert = true;
            for (j = 0; j < vertdeg[0][face.c]; j++) {
                if (face.b == vertdeg[j + 1][face.c]) {
                    flagvert = false;
                    break;
                }
            }
            if (flagvert) {
                vertdeg[0][face.c]++;
                vertdeg[vertdeg[0][face.c]][face.c] = face.b;
            }
        }

        double wt = 1.00;
        double wt2 = 0.50;
//        int ssign;
        int k;
        double outwt = 0.75 / (scalefactor + 3.5);//area-preserving
        // usually, scalefactor = 4 -> 0.75/(4 + 3.5) = 0.1
        for (k = 0; k < numiter; k++) {
            for (i = 0; i < vertnumber; i++) {
      //          System.out.println("vertdeg " + vertdeg[0][i]);
//                System.out.println(vertices.get(i).p);
                if (vertdeg[0][i] < 3) {
//			tps[i].x=verts[i].p.x;
//			tps[i].y=verts[i].p.y;
//			tps[i].z=verts[i].p.z;
                    VertInfo vertex = vertices.get(i);
                    tps[i].set(vertex.p);
                } else if (vertdeg[0][i] == 3 || vertdeg[0][i] == 4) {
                    tps[i].x = 0;
                    tps[i].y = 0;
                    tps[i].z = 0;
                    for (j = 0; j < vertdeg[0][i]; j++) {
//				tps[i].x+=verts[vertdeg[j+1][i]].p.x;
//				tps[i].y+=verts[vertdeg[j+1][i]].p.y;
//				tps[i].z+=verts[vertdeg[j+1][i]].p.z;
                       tps[i].add(vertices.get(vertdeg[j + 1][i]).p);

                    }
 //                   System.out.println("tpsi: " + tps[i]);
//			tps[i].x+=wt2*verts[i].p.x;
//			tps[i].y+=wt2*verts[i].p.y;
//			tps[i].z+=wt2*verts[i].p.z;
                    VertInfo vertex = vertices.get(i);
                    tps[i].x+=wt2*vertex.p.x;
                    tps[i].y+=wt2*vertex.p.y;
	            tps[i].z+=wt2*vertex.p.z;

                    tps[i].x /= (float) (wt2 + vertdeg[0][i]);
                    tps[i].y /= (float) (wt2 + vertdeg[0][i]);
                    tps[i].z /= (float) (wt2 + vertdeg[0][i]);
       //             System.out.println(i + ": " + tps[i]);
                } else {
                    tps[i].x = 0;
                    tps[i].y = 0;
                    tps[i].z = 0;
                    for (j = 0; j < vertdeg[0][i]; j++) {
//				tps[i].x+=verts[vertdeg[j+1][i]].p.x;
//				tps[i].y+=verts[vertdeg[j+1][i]].p.y;
//				tps[i].z+=verts[vertdeg[j+1][i]].p.z;
                        tps[i].add(vertices.get(vertdeg[j + 1][i]).p);
                    }
//			tps[i].x+=wt*verts[i].p.x;
//			tps[i].y+=wt*verts[i].p.y;
//			tps[i].z+=wt*verts[i].p.z;
                    VertInfo vertex = vertices.get(i);
                    tps[i].x+=wt*vertex.p.x;
	     	    tps[i].y+=wt*vertex.p.y;
		    tps[i].z+=wt*vertex.p.z;

                    tps[i].x /= (float) (wt + vertdeg[0][i]);
                    tps[i].y /= (float) (wt + vertdeg[0][i]);
                    tps[i].z /= (float) (wt + vertdeg[0][i]);
             //                           System.out.println(i + ": " + tps[i]);
                }
            }
            for (i = 0; i < vertnumber; i++) {
//		verts[i].p.x=tps[i].x;
//		verts[i].p.y=tps[i].y;
//		verts[i].p.z=tps[i].z;
                VertInfo vertex = vertices.get(i);
                vertex.p.set(tps[i]);
 //               System.out.println("vert: " + i + " " + vertex.p);
            }
            computenorm();
            for (i = 0; i < vertnumber; i++) {
                VertInfo vertex = vertices.get(i);
                // vertex.inout is never set (obsolete variable?)
//                if (vertex.inout) {
//                    ssign = 1;
//                } else {
//                    ssign = -1;
//
//                }
//		verts[i].p.x+=ssign*outwt*verts[i].pn.x;
//		verts[i].p.y+=ssign*outwt*verts[i].pn.y;
//		verts[i].p.z+=ssign*outwt*verts[i].pn.z;
                vertex.p.x+= outwt*vertex.normal.x;
 		vertex.p.y+= outwt*vertex.normal.y;
		vertex.p.z+= outwt*vertex.normal.z;
//                System.out.println("vert: " + i + " " + ssign*outwt);
            }
        }
//	delete[]tps;
//	for(i=0;i<20;i++)
//		delete[]vertdeg[i];

    }
}
