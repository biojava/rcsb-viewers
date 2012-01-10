package org.rcsb.mbt.surface.gamer;

import java.util.List;

import org.rcsb.mbt.surface.datastructure.FaceInfo;
import org.rcsb.mbt.surface.datastructure.TriangulatedSurface;
import org.rcsb.mbt.surface.datastructure.VertInfo;
import org.rcsb.mbt.surface.gamer.biom.FLTVECT;
import org.rcsb.mbt.surface.gamer.biom.INT3VECT;
import org.rcsb.mbt.surface.gamer.biom.SurfMesh;

public class ImproveMesh {
	private static biom biom = new biom();

	public static TriangulatedSurface Smooth(TriangulatedSurface surface, int iterations) {	
		long t1 = System.nanoTime();
		SurfMesh surfmesh = createSurfMesh(surface);
		long t2 = System.nanoTime();
		System.out.println("Create surfmesh: " + ((t2-t1)/1000000) + " ms");
		System.out.println("Histogram before: ");
		SurfSmooth.GenerateHistogram(surfmesh);
		int MAX_MIN_ANGLE = 15;
		int MIN_MAX_ANGLE = 150;
		boolean FLIP_EDGES = true;
		int MAX_ITER = 6;
		SurfSmooth.SurfaceMesh_smooth(surfmesh, MAX_MIN_ANGLE, MIN_MAX_ANGLE, MAX_ITER, FLIP_EDGES);
		for (int i = 0; i < iterations; i++) {
			SurfSmooth.SurfaceMesh_normalSmooth(surfmesh);
		}
		long t3 = System.nanoTime();
		System.out.println("Normal smooth per iteration: " + ((t3-t2)/(1000000*iterations)) + " ms");
        SurfSmooth.GenerateHistogram(surfmesh);
        return createTriangulatedSurface(surface, surfmesh);
	}
	
	public static TriangulatedSurface Coarse(TriangulatedSurface surface, int iterations) {	
		long t1 = System.nanoTime();
		SurfMesh surfmesh = createSurfMesh(surface);
		long t2 = System.nanoTime();
		System.out.println("Create surfmesh: " + ((t2-t1)/1000000) + " ms");
		System.out.println("Histogram before: ");
		SurfSmooth.GenerateHistogram(surfmesh);
		float FLAT_RATE = 0.016f;

		SurfSmooth.SurfaceMesh_coarse(surfmesh, FLAT_RATE, 1, 0, -1);
		for (int i = 0; i < iterations; i++) {
			SurfSmooth.SurfaceMesh_normalSmooth(surfmesh);
		}
		long t3 = System.nanoTime();
		System.out.println("Normal smooth per iteration: " + ((t3-t2)/(1000000*iterations)) + " ms");
        SurfSmooth.GenerateHistogram(surfmesh);
        return createTriangulatedSurface(surface, surfmesh);
	}
	
	public static TriangulatedSurface NormalSmooth(TriangulatedSurface surface, int iterations) {	
		long t1 = System.nanoTime();
		SurfMesh surfmesh = createSurfMesh(surface);
		long t2 = System.nanoTime();
		System.out.println("Create surfmesh: " + ((t2-t1)/1000000) + " ms");
		System.out.println("Histogram before: ");
		SurfSmooth.GenerateHistogram(surfmesh);
		for (int i = 0; i < iterations; i++) {
			SurfSmooth.SurfaceMesh_normalSmooth(surfmesh);
		}
		long t3 = System.nanoTime();
		System.out.println("Normal smooth per iteration: " + ((t3-t2)/(1000000*iterations)) + " ms");
        SurfSmooth.GenerateHistogram(surfmesh);
        return createTriangulatedSurface(surface, surfmesh);
	}

	private static SurfMesh createSurfMesh(TriangulatedSurface surface) {
		SurfMesh surfmesh = biom.new SurfMesh();

		// copy vertices
		List<VertInfo> vertices = surface.getVertices();
		int nv = vertices.size();
		surfmesh.nv = nv;	
		surfmesh.vertex = new FLTVECT[nv];
		for (int i = 0; i < nv; i++) {
			VertInfo vertInfo = vertices.get(i);
			FLTVECT vertex = biom.new FLTVECT();
			vertex.x = vertInfo.p.x;
			vertex.y = vertInfo.p.y;
			vertex.z = vertInfo.p.z;
			surfmesh.vertex[i] = vertex;
		}

		// copy faces
		List<FaceInfo> faces = surface.getFaces();
		int nf = faces.size();
		surfmesh.nf = nf;
		surfmesh.face = new INT3VECT[nf];
		for (int i = 0; i < nf; i++) {
			FaceInfo faceInfo = faces.get(i);
			INT3VECT face = biom.new INT3VECT();
			face.a = faceInfo.a;
			face.b = faceInfo.b;
			face.c = faceInfo.c;
			surfmesh.face[i] = face;
		}
		
		return surfmesh;
	}
	
	private static TriangulatedSurface createTriangulatedSurface(TriangulatedSurface surface, SurfMesh surfmesh) {
		// copy vertices
		List<VertInfo> vertices = surface.getVertices();
		if (vertices.size() != surfmesh.nv) {
			System.out.println("Reduced vertices: " + vertices.size() + " -> " + surfmesh.nv);
		}
		for (int i = 0; i < surfmesh.nv; i++) {
			VertInfo vertInfo = vertices.get(i);
			FLTVECT vertex = surfmesh.vertex[i];
			vertInfo.p.x = vertex.x;
			vertInfo.p.y = vertex.y;
			vertInfo.p.z = vertex.z;
		}

		// copy faces
		List<FaceInfo> faces = surface.getFaces();
		if (faces.size() != surfmesh.nf) {
			System.out.println("Reduced faces: " + faces.size() + " -> " + surfmesh.nf);
		}
		for (int i = 0; i < surfmesh.nf; i++) {
			FaceInfo faceInfo = faces.get(i);
			INT3VECT face = surfmesh.face[i];
			faceInfo.a = face.a;
			faceInfo.b = face.b;
			faceInfo.c = face.c;
			surfmesh.face[i] = face;
		}
		
		surface.computenorm();
		return surface;
	}
}
