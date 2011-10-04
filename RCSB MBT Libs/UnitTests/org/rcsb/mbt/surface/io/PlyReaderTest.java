/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rcsb.mbt.surface.io;

import javax.vecmath.Point3f;
import org.junit.Test;
import org.rcsb.mbt.surface.io.PlyReader;

import static org.junit.Assert.*;

/**
 *
 * @author Peter Rose
 */
public class PlyReaderTest {

    public PlyReaderTest() {
    }

    /**
     * Test of readPly method, of class PlyReader.
     */
    @Test
    public void testReadPly() throws Exception {
        System.out.println("readPly");
        String fileName = "test-input/SurfaceTest/3atom_ms.ply";
        PlyReader instance = new PlyReader();
        instance.readPly(fileName);

        int vertexCount = instance.getVertices().length;
        int faceCount = instance.getFaces().length;
        assertEquals(684, vertexCount);
        assertEquals(1364, faceCount);

        // check first and last vertex
        Point3f fv = new Point3f(21.573f, 5.010f, 11.792f);
        assertEquals(fv, instance.getVertices()[0].p);
        Point3f lv = new Point3f(25.761f, 5.917f, 9.999f);
        assertEquals(lv, instance.getVertices()[vertexCount-1].p);

        // check first and last faces
        assertEquals(0, instance.getFaces()[0].a);
        assertEquals(1, instance.getFaces()[0].b);
        assertEquals(2, instance.getFaces()[0].c);
        assertEquals(669, instance.getFaces()[faceCount-1].a);
        assertEquals(683, instance.getFaces()[faceCount-1].b);
        assertEquals(667, instance.getFaces()[faceCount-1].c);
    }
}