/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rcsb.mbt.surface.datastructure;

import java.util.List;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;



/**
 *
 * @author Peter Rose
 */
public class TriangulatedSurface {

    private List<VertInfo> vertices;
    private List<FaceInfo> faces;

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
            va.normal.add(faceNormal);
            vb.normal.add(faceNormal);
            vc.normal.add(faceNormal);
        }

//        for (VertInfo v : vertices) {
//            v.normal.normalize();
//        }
        for (int i = 0; i < vertices.size(); i++) {
            Vector3f v = vertices.get(i).normal;
            v.normalize();
        }
    }

    public void laplaciansmooth(int numiter) {

        float scalefactor = 4.0f; // note, this is the original scalefactor from EDTSurf, How should it be set???
        // coords used here are the actual coordinates, not the scaled coordinates
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
        System.out.println("outwt: " + outwt);
        System.out.println("vertnumber: " + vertnumber);
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
