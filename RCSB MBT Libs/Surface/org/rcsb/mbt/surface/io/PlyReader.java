package org.rcsb.mbt.surface.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.vecmath.Color3b;
import javax.vecmath.Color4f;
import javax.vecmath.Point3f;

import org.rcsb.mbt.surface.datastructure.FaceInfo;
import org.rcsb.mbt.surface.datastructure.VertInfo;


/**
 *
 * @author Peter
 */
public class PlyReader {
    private Point3f[] vertices = null;
    private int[][] faces = null;
    private Color4f[] colors = null;
    private VertInfo[] vertInfo = null;
    private FaceInfo[] faceInfo = null;

// http://www.okino.com/conv/imp_ply.htm
// The following table describes the element types that are parsed and imported.
//
//Element Type	Properties
//
//Face		Index list
//Vertex	"x", "y" and "z" vertex coordinates
//Vertex	"nx", "ny" and "nz" vertex normals
//Vertex	"u" and "v" vertex texture coordinates
//Vertex	"red", "green" and "blue" vertex colors (0..255 integers or 0..1 normalized)

    public void readPly(String fileName) throws FileNotFoundException, IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        FastScanner scanner = new FastScanner();

        String line = "";
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("element")) {
                if (line.contains("vertex")) {
                    int index = line.indexOf("vertex") + 6;
                    int vertexCount = Integer.parseInt(line.substring(index).trim());
                    System.out.println("# of vertices: " + vertexCount);
                    vertices = new Point3f[vertexCount];
                    colors = new Color4f[vertexCount];
                    vertInfo = new VertInfo[vertexCount];
                } else if (line.contains("face")) {
                    int index = line.lastIndexOf("face") + 4;
                    int faceCount = Integer.parseInt(line.substring(index).trim());
                    System.out.println("# of faces: " + faceCount);
                    faces = new int[faceCount][3];
                    faceInfo = new FaceInfo[faceCount];
                }
            }
            if (line.startsWith("end_header")) {
                System.out.println("Parsing vertices");
                for (int i = 0; i < vertices.length; i++) {
                    line = reader.readLine();
                    if (line == null) {
                        System.err.println("PlyReader: End of file reached while reading vertex: " + i);
                    }
                    scanner.setString(line);
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
 //[i] = new Point3f(x, y, -z);
//                    vertices[i] = new Point3f(x, y, z);
                    vertInfo[i] = new VertInfo();
                    vertInfo[i].p = new Point3f(x, y, z);
                    byte r = (byte)0;
                    if (scanner.hasNextInt()) {
                        r = (byte)scanner.nextInt();
                    }
                    byte g = (byte)0;
                    if (scanner.hasNextInt()) {
                        g = (byte)scanner.nextInt();
                    }
                    byte b = (byte)0;
                    if (scanner.hasNextInt()) {
                        b = (byte)scanner.nextInt();
                    }

                    Color3b c = new Color3b(r, g, b);
                    colors[i] = new Color4f(c.get());
                }

                System.out.println("Parsing faces");
                for (int i = 0; i < faces.length; i++) {
                    line = reader.readLine();
                    if (line == null) {
                        System.err.println("PlyReader: End of file reached while reading face: " + i);
                    }
                    scanner.setString(line);
                    int n = 0;
                    if (scanner.hasNextInt()) {
                        n = scanner.nextInt();
                    }
                    if (n != 3) {
                        System.err.println("PlyReader: Polygon is not a triangle: " + n);
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
 //                   faces[i] = new int[]{a, b, c};
                    faceInfo[i] = new FaceInfo();
                    faceInfo[i].a = a;
                    faceInfo[i].b = b;
                    faceInfo[i].c = c;
                }
            }
        }
    }


    /**
     * @return the vertices
     */
    public VertInfo[] getVertices() {
        return vertInfo;
    }
    /**
     * @return the faces
     */
    public FaceInfo[] getFaces() {
        return faceInfo;
    }
    /**
     * @return the vertices
     */
//    public Point3f[] getVertices() {
//        return vertices;
//    }

    /**
     * @return the faces
     */
//    public int[][] getFaces() {
//        return faces;
//    }

//    public Color4f[] getVertexColors() {
//        return colors;
//    }

}
