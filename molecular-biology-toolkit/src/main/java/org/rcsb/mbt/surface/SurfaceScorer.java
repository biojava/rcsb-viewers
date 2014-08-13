package org.rcsb.mbt.surface;

import java.util.List;

import org.rcsb.mbt.surface.datastructure.FaceInfo;
import org.rcsb.mbt.surface.datastructure.TriangulatedSurface;
import org.rcsb.mbt.surface.datastructure.VertInfo;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

/**
 * Provides several methods to determine how closely related two triangulated surfaces are.
 * @author Henry Truong
 */
public class SurfaceScorer {

	private List<FaceInfo> faces;
	private List<VertInfo> vertices;
	private Vector3f intersection = new Vector3f(); // point/vector of intersection
	private Vector3f negIntersection = new Vector3f();
	
	
	/**
	 * Constructor that takes a TriangulatedSurface. The surface passed in should be the original surface
	 * that you wish to compare other surfaces with.
	 */
	public SurfaceScorer(TriangulatedSurface ts) {
		faces = ts.getFaces();
		vertices = ts.getVertices();
	}
	
	/**
	 * Compares a surface to the original surface
	 * @param ts: The surface that you want to compare to the original
	 * @return The surface score
	 */
	public double scoringSurfaceA(TriangulatedSurface ts) {
		double totalDistance = 0.0;
		double currentDistance = 0.0;
		for(int i = 0; i < vertices.size(); i++) {
			Vector3f v = new Vector3f(vertices.get(i).normal);
			v.scale(500);
			currentDistance = intersectsLine(ts, vertices.get(i).p, v); 
			if(currentDistance >= 0) {
				totalDistance += currentDistance;
			}
		}
		return totalDistance/vertices.size();
	}
	
	/**
	 * Compares a surface to the original surface. Squares the distance to penalize
	 * larger distances.
	 * @param ts: The surface that you want to compare to the original
	 * @return The surface score
	 */
	public double scoringSurfaceASquared(TriangulatedSurface ts) {
		double totalDistance = 0.0;
		double currentDistance = 0.0;
		for(int i = 0; i < vertices.size(); i++) {
			Vector3f v = new Vector3f(vertices.get(i).normal);
			v.scale(1000);
			currentDistance = intersectsLine(ts, vertices.get(i).p, v); 
			if(currentDistance >= 0) {
				totalDistance += Math.pow(currentDistance,2);
			}
		}
		totalDistance = totalDistance/vertices.size();
		return Math.sqrt(totalDistance);
	}
	
	/**
	 * Calculates the distance from the intersection and vertice
	 * @param intersection: the point that the two surfaces intersect
	 * @param vertice: the point on the original surface
	 * @return the distance between the two points
	 */
	private double distance(Vector3f intersection, Point3f vertice) {
		return Math.sqrt(Math.pow((double)intersection.x - vertice.x, 2) + Math.pow((double)intersection.y - vertice.y, 2) + 
				Math.pow((double)intersection.z - vertice.z, 2));
	}
	
	/**
	 * Checks to see if the surfaces intersect
	 * @param surface: the surface that the original is being compared to
	 * @param vertix: the vertix of the original surface
	 * @param direction: the direction of the vertix
	 * @return
	 */
	private double intersectsLine(TriangulatedSurface surface, Point3f vertix, Vector3f direction) {
		List<VertInfo> verts = surface.getVertices();
		double lowestDistance = -1;
		double currentDistance = 0.0;
		double distance = 0.0;
		double negDistance = 0.0;
		for (FaceInfo f : surface.getFaces()) {
			Point3f t1 = verts.get(f.a).p;
			Point3f t2 = verts.get(f.b).p;
			Point3f t3 = verts.get(f.c).p;
			
			if(intersect(t1, t2, t3, vertix, direction) || negIntersect(t1, t2, t3, vertix, direction)){
				distance = distance(this.intersection, vertix);
				negDistance = distance(this.negIntersection, vertix);
				if(distance < negDistance) {
					currentDistance = distance;
				}
				else {
					currentDistance = negDistance;
				}
				if(currentDistance < lowestDistance || lowestDistance == -1) {
					lowestDistance = currentDistance;
				}
				intersection = new Vector3f();
				negIntersection = new Vector3f();
			}
		}
		
		return lowestDistance;
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
		
	//	System.out.println("dir: " + direction.x + " " + direction.y + " " + direction.z);
		//triangle vectors
	 	Vector3f n = new Vector3f();
		Vector3f u = new Vector3f();
		Vector3f v = new Vector3f();
		
		Vector3f zero = new Vector3f();			
		//ray vectors
		Vector3f w = new Vector3f();
		Vector3f w0 = new Vector3f();
		
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
		//System.out.println("dir: " + direction.x + " " + direction.y + " " + direction.z);
		//System.out.println("r: " + r);
		intersection.scaleAdd(r, direction, p);

		// is I inside T?
		float    uu, uv, vv, wu, wv, d;
		uu = u.dot(u);
		uv = u.dot(v);
		vv = v.dot(v);
		w.sub(intersection,t1);
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
	
	/**
	 * http://softsurfer.com/Archive/algorithm_0105/algorithm_0105.htm#intersect_RayTriangle%28%29
	 * Differs by changing the direction to negative, to account for cases in which the new surface
	 * is below the original surface
	 * @param t1
	 * @param t2
	 * @param t3
	 * @param p
	 * @param direction
	 * @return
	 */
	private boolean negIntersect(Point3f t1, Point3f t2, Point3f t3, Point3f p, Vector3f direction) {  
		//	float SMALL_NUM = 0.00000001f;
		//triangle vectors
	 	Vector3f n = new Vector3f();
		Vector3f u = new Vector3f();
		Vector3f v = new Vector3f();
		
		Vector3f zero = new Vector3f();			
		//ray vectors
		Vector3f w = new Vector3f();
		Vector3f w0 = new Vector3f();
		
		float SMALL_NUM = 0.000001f;
		// Find if the intersection point lies inside the triangle by testing it against all edges
		// get triangle edge vectors and plane normal
		u.sub(t2, t1);
		v.sub(t3, t1);	
		n.cross(u,v);
		if (n.equals(zero)) {
			return false;
		}
		Vector3f negDirect = new Vector3f();
		negDirect.x = -direction.x;
		negDirect.y = -direction.y;
		negDirect.z = -direction.z;
		
		w0.sub(p, t1); 

		float a = - n.dot(w0);	
		float b = n.dot(negDirect);

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

		negIntersection.set(negDirect);
		negIntersection.scaleAdd(r, p);

		// is I inside T?
		float    uu, uv, vv, wu, wv, d;
		uu = u.dot(u);
		uv = u.dot(v);
		vv = v.dot(v);
		w.sub(negIntersection,t1);
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
