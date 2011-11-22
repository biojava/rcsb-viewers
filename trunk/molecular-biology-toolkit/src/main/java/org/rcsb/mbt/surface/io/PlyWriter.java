/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rcsb.mbt.surface.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.rcsb.mbt.surface.core.EdtSurfaceCalculator;
import org.rcsb.mbt.surface.datastructure.FaceInfo;
import org.rcsb.mbt.surface.datastructure.TriangulatedSurface;
import org.rcsb.mbt.surface.datastructure.VertInfo;


/**
 *
 * @author Peter Rose
 */
public class PlyWriter {

    public void outputply(TriangulatedSurface surface, String fileName) // used, optional
    {
        List<VertInfo> vertices = surface.getVertices();
        List<FaceInfo> faces = surface.getFaces();
        PrintWriter writer = null;
        try {
            File f = new File(fileName);
            System.out.println("Filename:" + f.getAbsolutePath());
            writer = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
        } catch (IOException ex) {
            Logger.getLogger(EdtSurfaceCalculator.class.getName()).log(Level.SEVERE, null, ex);
        }
        writer.println("ply");
        writer.println("format ascii 1.0");
        writer.println("comment ball mesh");
        writer.println("element vertex " + vertices.size());
        writer.println("property float x");
        writer.println("property float y");
        writer.println("property float z");
        writer.println("property float nx");
        writer.println("property float ny");
        writer.println("property float nz");
        writer.println("element face " + faces.size());
        writer.println("property list uchar int vertex_indices");
        writer.println("end_header");

        for (VertInfo v: vertices) {
//		writer.printf("%.3f %.3f %.3f %.3f %.3f %.3f\n", v.p.x/scalefactor-ptran.x,v.p.y/scalefactor-ptran.y,
//			v.p.z/scalefactor-ptran.z, v.normal.x, v.normal.y, v.normal.z);
            writer.printf("%.3f %.3f %.3f %.3f %.3f %.3f\n", v.p.x, v.p.y, v.p.z, v.normal.x, v.normal.y, v.normal.z);
//                System.out.println("atomid: " + verts[i].atomid + ", inout: " + verts[i].inout + ", iscont" + verts[i].iscont);
        }
        for (FaceInfo face: faces) {
            if (!face.inout)//outer
            {
                writer.printf("3 %d %d %d\n", face.a, face.b, face.c);
            } else {
                writer.printf("3 %d %d %d\n", face.a, face.c, face.b);
            }
        }
        writer.flush();
        writer.close();
    }
}
