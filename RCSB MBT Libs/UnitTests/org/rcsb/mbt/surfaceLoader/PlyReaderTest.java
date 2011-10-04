package org.rcsb.mbt.surfaceLoader;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.vecmath.Color4f;
import javax.vecmath.Point3f;

import org.junit.Test;

public class PlyReaderTest {

	@Test
	public final void testGetVertices() throws FileNotFoundException, IOException {
		PlyReader reader = new PlyReader();
		reader.readPly("test-input/PlyReaderTest/2ptn.ply");
		Point3f[] vertices = reader.getVertices();
		assertEquals(154134, vertices.length);
	}

	@Test
	public final void testGetFaces() throws FileNotFoundException, IOException {
		PlyReader reader = new PlyReader();
		reader.readPly("test-input/PlyReaderTest/2ptn.ply");
		int[][] faces = reader.getFaces();
		assertEquals(308254, faces.length);
	}

	@Test
	public final void testGetColors() throws FileNotFoundException, IOException {
		PlyReader reader = new PlyReader();
		reader.readPly("test-input/PlyReaderTest/2ptn.ply");
		Color4f[] colors = reader.getVertexColors();
		assertEquals(154134, colors.length);
	}

}
