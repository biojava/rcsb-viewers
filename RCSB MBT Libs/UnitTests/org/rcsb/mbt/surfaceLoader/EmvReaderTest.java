package org.rcsb.mbt.surfaceLoader;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.vecmath.Color4f;
import javax.vecmath.Point3f;

import org.junit.Test;


public class EmvReaderTest {

	@Test
	public final void testGetVertices() throws FileNotFoundException, IOException {
		EmvReader reader = new EmvReader();
		reader.readEmv("test-input/EmvReaderTest/emd_5127-i2.8-t50000.emv");
		Point3f[] vertices = reader.getVertices();
		assertEquals(7540, vertices.length);
	}

	@Test
	public final void testGetFaces() throws FileNotFoundException, IOException {
		EmvReader reader = new EmvReader();
		reader.readEmv("test-input/EmvReaderTest/emd_5127-i2.8-t50000.emv");
		int[][] faces = reader.getFaces();
		assertEquals(15124, faces.length);
	}

	@Test
	public final void testGetColors() throws FileNotFoundException, IOException {
		EmvReader reader = new EmvReader();
		reader.readEmv("test-input/EmvReaderTest/emd_5127-i2.8-t50000.emv");
		Color4f[] colors = reader.getVertexColors();
		assertEquals(7540, colors.length);
	}
}
