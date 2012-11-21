package org.rcsb.mbt.surface;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Matrix4d;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.rcsb.mbt.surface.datastructure.FaceInfo;
import org.rcsb.mbt.surface.datastructure.IcosahedralSampler;
import org.rcsb.mbt.surface.datastructure.TriangulatedSurface;
import org.rcsb.mbt.surface.datastructure.VertInfo;

public class BindingSiteSurfaceOrienter {
	private List<Point3f> samplePoints = new ArrayList<Point3f>(0);
	private List<TriangulatedSurface> surfaces;
	private Vector3f optimalOrientation = null;
	private Vector3f horizonalAlignment = null;

	private Vector3f zero = new Vector3f();	
	private Vector3f i = new Vector3f();
	private Vector3f n = new Vector3f();
	private Vector3f u = new Vector3f();
	private Vector3f v = new Vector3f();
	private Vector3f w = new Vector3f();
	private Vector3f w0 = new Vector3f();

	public BindingSiteSurfaceOrienter(List<Point3f> samplePoints, List<TriangulatedSurface> surfaces) {
		this.samplePoints = samplePoints;
		this.surfaces = surfaces;
	}

	public Vector3f getOptimalEyeOrientation() {
		if (optimalOrientation == null) {
			optimalOrientation = calcOptimalOrientation();
		}
		return optimalOrientation;
	}

	public Vector3f getHorizontalAlignment() {
		getOptimalEyeOrientation();
		if (horizonalAlignment == null) {
			horizonalAlignment = calcHorizontalAlignment();
		}
		return horizonalAlignment;
	}

	private Vector3f calcOptimalOrientation() {
		long t1 = System.nanoTime();
		Vector3f surfaceNormal = surfaceCompositeNormal();	
		long t2 = System.nanoTime();
//		System.out.println("SurfaceComposite: " + ((t2-t1)/1000000) + " ms");
		Vector3f optimalOrientation = new Vector3f();
		float dotProduct = 0;

		// find ligand orientation that is closest
		// to the composite surface normal. That way
		// both the ligand and the surface have
		// maximum visibility
		for (int direction: bestLigandOrientations()) {
			Matrix4f matrix = new Matrix4f();
			matrix.set(IcosahedralSampler.getQuat4d(direction));
			Vector3f orientation = new Vector3f(1.0f, 0.0f, 0.0f);
			matrix.transform(orientation);
			orientation.normalize();
			float dot = orientation.dot(surfaceNormal);
			if (dot > dotProduct) {
				dotProduct = dot;
				optimalOrientation = orientation;
			}
			// test if first direction is good enough
		}

		if (dotProduct == 0.0f) {
			optimalOrientation = surfaceNormal;
		}

//		System.out.println("optimalOrientation: dot product: " + dotProduct);
		return optimalOrientation;
	}


	private Vector3f calcHorizontalAlignment() {	
		Point3f centroid = sampleCentroid();		
		Point3f basePoint = new Point3f();
		Point3f maxPoint = new Point3f();

		float maxDistanceSq = 0.0f;

		for (TriangulatedSurface s: surfaces){
			List<VertInfo> vertices = s.getVertices();

			// to speed up calculation, sample every 20-th vertex
			for (int i = 0, n = vertices.size(); i < n; i+=20) {
				VertInfo v = vertices.get(i);
				Point3f base = projectPointToLine(v.p, centroid, optimalOrientation);
				float distanceSq = v.p.distanceSquared(base);
				if (distanceSq > maxDistanceSq) {
					maxDistanceSq = distanceSq;
					maxPoint = v.p;
					basePoint = base;
				}
			}
		}

		Vector3f alignment = new Vector3f();
		alignment.sub(maxPoint, basePoint);		
		alignment.normalize();

		return alignment;
	}

	private Vector3f surfaceCompositeNormal() {
		Vector3f compositeNormal = new Vector3f();
		for (TriangulatedSurface s: surfaces){
			List<VertInfo> vertices = s.getVertices();
			for (int i = 0, n = vertices.size(); i < n; i+=20) {
				VertInfo v = vertices.get(i);
				Vector3f tip = new Vector3f(v.normal);
				tip.scale(100f);
				if (! intersectsSurface(v.p, tip)) {
					compositeNormal.add(v.normal);
				}
			}
		}

		compositeNormal.normalize();
		return compositeNormal;
	}

	private boolean intersectsSurface(Point3f base, Vector3f direction) {	
		for (TriangulatedSurface s: surfaces) {
			if (intersectsLineWithCheck(s, base, direction)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return
	 */
	private List<Integer> bestLigandOrientations() {
		long t1 = System.nanoTime();
		Vector3f[] dirs = getRays();
		int[] unobstructedRays = new int[dirs.length];

		for (Point3f sample: samplePoints) {
			for (int i = 0; i < dirs.length; i++) {
				boolean intersect = false;
				for (TriangulatedSurface s: surfaces) {
					if (intersectsLine(s, sample, dirs[i])) {
						intersect = true;
						break;
					}
				}
				if (! intersect) {
					//				System.out.println("Adding to unobstructed rays");
					unobstructedRays[i]++;
				}
			}
		}

		// find all orientations that have the 
		// maximum number of unobstructed rays
		int maxRays = 0;
		List<Integer> directions = new ArrayList<Integer>();

		for (int i = 0; i < unobstructedRays.length; i++) {
			if (unobstructedRays[i] >= maxRays) {
				if (unobstructedRays[i] > maxRays) {
					directions.clear();
					maxRays = unobstructedRays[i];
				}
				directions.add(i);
			}
		}
//		System.out.println("Ligand orientations: " + directions.size() + " maxRays: " + maxRays);
		long t2 = System.nanoTime();
//		System.out.println("Best ligand orientation: " + ((t2-t1)/1000000) + " ms");
		return directions;
	}

	private Vector3f[] getRays() {
		Matrix4d matrix = new Matrix4d();
		// setting x to 1.0f gives different results, why??
		Point3f x = new Point3f(1000.0f, 0.0f, 0.0f);
		int n = IcosahedralSampler.getSphereCount();
		Vector3f[] rays = new Vector3f[n];

		for (int i = 0; i < n; i++) {
			matrix.set(IcosahedralSampler.getQuat4d(i));
			Vector3f ray = new Vector3f(x);
			matrix.transform(ray);
			rays[i] = ray;
		}
		return rays;
	}

	/**
	 * Line is defined by point base and direction. Direction must be a unit vector!
	 * http://softsurfer.com/Archive/algorithm_0102/algorithm_0102.htm
	 * @param point
	 * @param base 
	 * @param direction
	 * @return
	 */
	private Point3f projectPointToLine(Point3f point, Point3f base, Vector3f direction) {  	
		w.sub(point, base);		
		Point3f basePoint = new Point3f(direction);
		basePoint.scale(w.dot(direction));
		basePoint.add(base);
		return basePoint;
	}

	private Point3f sampleCentroid() {
		Point3f centroid = new Point3f();

		for (Point3f s: samplePoints) {;
		centroid.add(s);
		}
		if (samplePoints.size() > 0) {
			centroid.scale(1.0f/samplePoints.size());
		}
		return centroid;
	}

	public Point3f surfaceCentroid() {
		Point3f centroid = new Point3f();
		int count = 0;
		for (TriangulatedSurface s: surfaces) {
			List<VertInfo> vertices = s.getVertices();
			for (VertInfo v: vertices) {
				centroid.add(v.p);
				count++;
			}
		}
		if (count > 0) {
			centroid.scale(1.0f/count);
		}
		return centroid;
	}


	private boolean intersectsLineWithCheck(TriangulatedSurface surface, Point3f line1, Vector3f direction) {	
		float SMALL_NUM = 0.00001f;
		List<VertInfo> verts = surface.getVertices();
		for (FaceInfo f : surface.getFaces()) {
			Point3f t1 = verts.get(f.a).p;
			Point3f t2 = verts.get(f.b).p;
			Point3f t3 = verts.get(f.c).p;
			if (t1.epsilonEquals(line1, SMALL_NUM) ||
					t2.epsilonEquals(line1, SMALL_NUM) ||
					t3.epsilonEquals(line1, SMALL_NUM)) {
				continue;
			}
			if  (intersect(t1, t2, t3, line1, direction)) {
				return true;
			}
		}
		return false;
	}

	private boolean intersectsLine(TriangulatedSurface surface, Point3f line1, Vector3f direction) {	
		List<VertInfo> verts = surface.getVertices();
		for (FaceInfo f : surface.getFaces()) {
			Point3f t1 = verts.get(f.a).p;
			Point3f t2 = verts.get(f.b).p;
			Point3f t3 = verts.get(f.c).p;
			if  (intersect(t1, t2, t3, line1, direction)) {
				return true;
			}
		}
		return false;
	}


	/**
	 * http://softsurfer.com/Archive/algorithm_0105/algorithm_0105.htm#intersect_RayTriangle%28%29
	 * @param t1
	 * @param t2
	 * @param t3
	 * @param p
	 * @param l2
	 * @return
	 */
	private boolean intersect(Point3f t1, Point3f t2, Point3f t3, Point3f p, Vector3f direction) {  
		//	float SMALL_NUM = 0.00000001f;
		float SMALL_NUM = 0.000001f;
		// Find if the intersection point lies inside the triangle by testing it against all edges
		// get triangle edge vectors and plane normal
		u.sub(t2, t1);
		v.sub(t3, t1);	
		n.cross(u,v);
		if (n.equals(zero)) {
			return false;
		}

		w0.sub(p, t1);

		float a = - n.dot(w0);	
		float b = n.dot(direction);

		if (Math.abs(b) < SMALL_NUM) {
			return false;
		}

		// get intersect point of ray with triangle plane
		float r = a / b;
		if (r < 0.0f || r > 1.0f) {                  // ray goes away from triangle
			return false;  
		}   // => no intersect
		// for a segment, also test if (r > 1.0) => no intersect

		//*I = R.P0 + r * dir;           // intersect point of ray and plane

		i.set(direction);
		i.scaleAdd(r, p);

		// is I inside T?
		float    uu, uv, vv, wu, wv, d;
		uu = u.dot(u);
		uv = u.dot(v);
		vv = v.dot(v);
		w.sub(i,t1);
		wu = w.dot(u);
		wv = w.dot(v);
		d = uv * uv - uu * vv;

		// get and test parametric coords
		float s, t;
		s = (uv * wv - vv * wu) / d;
		if (s < 0.0f || s > 1.0f)        // I is outside T
			return false;
		t = (uv * wu - uu * wv) / d;
		if (t < 0.0f || (s + t) > 1.0f)  // I is outside T
			return false;

		// I is in T
		return true;
	}
}
