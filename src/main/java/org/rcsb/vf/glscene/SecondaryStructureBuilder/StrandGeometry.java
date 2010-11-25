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
 * Created on 2007/02/08
 *
 */ 
package org.rcsb.vf.glscene.SecondaryStructureBuilder;

import java.awt.Color;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;


import javax.vecmath.Vector3f;
import org.rcsb.mbt.model.Chain;
import org.rcsb.mbt.model.Fragment;
import org.rcsb.mbt.model.StructureComponent;
import org.rcsb.vf.glscene.SecondaryStructureBuilder.Extrusion;
import org.rcsb.vf.glscene.SecondaryStructureBuilder.FrenetTrihedron;
import org.rcsb.vf.glscene.SecondaryStructureBuilder.Hermite;
import org.rcsb.vf.glscene.SecondaryStructureBuilder.IntrinsicCrossSection;
import org.rcsb.vf.glscene.SecondaryStructureBuilder.CrossSectionStyle.CrossSectionType;
import org.rcsb.vf.glscene.SecondaryStructureBuilder.vec.Vec3f;
import org.rcsb.vf.glscene.jogl.Color3f;
import org.rcsb.vf.glscene.jogl.Constants;
import org.rcsb.vf.glscene.jogl.DisplayLists;

import com.sun.opengl.util.GLUT;


/**
 * StrandGeometry is an {@link SsGeometry} container which, primarily, manages
 * the geometry associated with a strand. It is a ribon-like shape that passes
 * through "smoothed" CA atoms, and contains interpolated points. As with all
 * {@link GeometryEntity} objects, it can have other
 * <code> GeometryEntity </code> children.
 * <P>
 * 
 * @author Apostol Gramada
 */
public class StrandGeometry extends SsGeometry {

	// *******************************************************************************
	// Class variables
	// *******************************************************************************
	//
	// Empirical geometry parameters for STRAND.
	private static final float STRAND_HERMITE_FACTOR = 0.5f;

	// Style - Polygon Mesh
	// private int polygonStyle;
	private boolean drawArrow = true;

	// Other parameters
	private Vector3f[] tangents = null;

	private Vector3f[] normals = null;

	// private Shape3D shape = null;

	/**
	 * Constructs a StrandGeometry objects with default values for the number of
	 * facets, segments, and cross section style.
	 */
	public StrandGeometry(final StructureComponent sc) {
		super(sc);
		
		this.csType = CrossSectionType.RECTANGULAR_RIBBON;
		this.uniformColor = true;
		this.segments = 6;
		this.userData = "StrandGeometry";
	}

	/**
	 * Returns a Java3D object representing a strand and eventually any other
	 * <code>GeometryEntity</code> that this object contains as a child.
	 */
	
	public DisplayLists generateJoglGeometry(final GL gl, final GLU glu, final GLUT glut) {
		this.processQualityInfo(this.quality);

		// Create an object for Hermite sampling
		final Hermite hermite = new Hermite();
		hermite.setKnotWeight(StrandGeometry.STRAND_HERMITE_FACTOR);

		if (this.csStyle == null) {
			final float[] diams = new float[2];
			diams[0] = 2.00f;
			diams[1] = 0.20f;

			// setStyle( CrossSectionStyle.OBLONG );
			this.csStyle = new CrossSectionStyle(this.csType, diams);
		}

		final int spinePointCount = (this.coords.length - 1) * this.segments + 1;
		
		// We need to enrich the set of points on the Strand path with
		// additional points
		// provided by an interpolation technique, in this case Hermite
		// interpolation.
		// Assume for the moment that our collection of atoms contains a single
		// chain.
		//
		// Need a vector containing both CA and interpolated points
		//
		final Vector3f[] pathCoords = new Vector3f[spinePointCount];

		// Local reference frames along the path. Needed to reconstruct the
		// cross section at each point.
		//
		final FrenetTrihedron[] pathTrihedron = new FrenetTrihedron[spinePointCount];

		this.tangents = new Vector3f[this.coords.length];
		this.normals = new Vector3f[this.coords.length];
		this.getAllTangentsAndNormals(this.coords, this.tangents, this.normals);

		// Holders for tangent vectors at the ends of a segment.
		//

		// Other working space
		//
		final Vec3f sampleCoord = new Vec3f();
		final Vector3f sampleOrigin = new Vector3f();
		FrenetTrihedron firstTrihedron = null;
		FrenetTrihedron secondTrihedron = null;
		Vector3f norm1 = new Vector3f();
		Vector3f tan1 = new Vector3f();
		Vector3f tan2 = new Vector3f();
		final Vector3f origin = new Vector3f();

		// Since we are here, normals and tangents are also already specified.
		//	
		tan1 = this.tangents[0];
		tan2 = this.tangents[1];
		norm1 = this.normals[0];

		tan1.normalize();
		tan2.normalize();

		hermite.setKnotWeight(2.0f);

		origin.set(this.coords[0].value[0], this.coords[0].value[1], this.coords[0].value[2]);
		secondTrihedron = new FrenetTrihedron(origin, tan1, norm1);

		float t = 0.0f;
		int k = -1;
		for (int i = 0; i < this.coords.length - 1; i++) {
			k++;
			pathCoords[k] = new Vector3f(this.coords[i].value[0],
					this.coords[i].value[1], this.coords[i].value[2]);
			pathTrihedron[k] = new FrenetTrihedron(secondTrihedron);
			firstTrihedron = pathTrihedron[k];

			hermite.set(this.coords[i], this.coords[i + 1], tan1,
					tan2);

			tan1 = this.tangents[i + 1];
			norm1 = this.normals[i + 1];
			if (i >= this.coords.length - 2) {
				tan2 = tan1;
			} else {
				tan2 = this.tangents[i + 2];
			}

			tan1.normalize();
			tan2.normalize();

			origin.set(this.coords[i + 1].value[0], this.coords[i + 1].value[1],
					this.coords[i + 1].value[2]);
			secondTrihedron = new FrenetTrihedron(origin, tan1, norm1);
			for (int j = 1; j < this.segments; j++) {
				k++;
				t = j * (1.0f / this.segments);
				pathTrihedron[k] = new FrenetTrihedron();
				pathTrihedron[k]
						.interpolate(t, firstTrihedron, secondTrihedron);

				hermite.sample(t, sampleCoord);
				sampleOrigin.set(sampleCoord.value[0], sampleCoord.value[1],
						sampleCoord.value[2]);
				pathTrihedron[k].setOriginOnly(sampleOrigin);
				pathCoords[k] = new Vector3f(sampleCoord.value[0],
						sampleCoord.value[1], sampleCoord.value[2]);
			}
		}
		k++;
		pathCoords[k] = new Vector3f(this.coords[this.coords.length - 1].value[0],
				this.coords[this.coords.length - 1].value[1],
				this.coords[this.coords.length - 1].value[2]);
		pathTrihedron[k] = new FrenetTrihedron(secondTrihedron);
		// 
		// Ended preparing the path coordinates and local refrence frames.

		// Prepare the scale vectors for the head of the arrow
		final int l = pathCoords.length - 1;
		final Vector3f[] arrowScale = new Vector3f[3];
		arrowScale[0] = new Vector3f(1.00f, 1.00f, 1.50f);
		arrowScale[2] = new Vector3f(1.00f, 1.00f, 0.00f);
		final Vector3f tmp0 = new Vector3f(pathCoords[l]);
		final Vector3f tmp1 = new Vector3f(pathCoords[l - 1]);
		final Vector3f tmp2 = new Vector3f(pathCoords[l - 2]);
		tmp2.sub(tmp0);
		final float slope = 1.50f / tmp2.length();
		tmp1.sub(tmp0);
		arrowScale[1] = new Vector3f(1.00f, 1.00f, slope * tmp1.length());

		if (this.uniformColor) {
			if (this.ssColor == null) {
				this.ssColor = new Color(1.0f, 0.0f, 0.0f);
			}
			final Color3f[] vColor = new Color3f[1];
			vColor[0] = new Color3f(this.ssColor);
			this.csStyle.setVertexColors(vColor);
			this.pathColorMap = new float[spinePointCount][3];
			for (int i = 0; i < spinePointCount; i++) {
				this.pathColorMap[i] = this.ssColor.getColorComponents(null);
			}
		} else {
			this.setPathColor(spinePointCount);
		}

		return this.drawFigure(this.csStyle, arrowScale, pathCoords,
				pathTrihedron, this.pathColorMap, spinePointCount, gl, glu, glut);
	}

	private Object[] ranges = null;
	
	/**
	 * Returns the scene graph component associated with the Strand defined by
	 * the underlying data
	 * 
	 */
	public DisplayLists drawFigure(final CrossSectionStyle style,
			final Vector3f[] arrowScale, final Vector3f[] coordinates,
			final FrenetTrihedron[] triheds, final float[][] colorMap, final int pointCount, final GL gl, final GLU glu, final GLUT glut) {
		final DisplayLists arrayLists = new DisplayLists(this.structureComponent);
		
		// 
		// Set an Appearance for the backbone.
		//
		// Material material = new Material();
		// Color3f color = new Color3f( new Color( 1.0f, 1.0f, 0.0f ) );
//		Color3f specularColor = new Color3f(new Color(0.7f, 0.7f, 0.7f));
//		Color3f ambientColor = new Color3f(new Color(0.9f, 0.9f, 0.9f));
//		Color3f emissiveColor = new Color3f(new Color(0.05f, 0.05f, 0.05f));
		// material.setShininess( 128.0f );
		// material.setSpecularColor( specularColor );
		// material.setEmissiveColor( emissiveColor );
		// material.setAmbientColor( ambientColor );
		//arrayLists.ambientColor = ambientColor.color;
		//arrayLists.diffuseColor = ambientColor.color;
		arrayLists.specularColor = Constants.chainSpecularColor.color;
		arrayLists.emissiveColor = Constants.chainEmissiveColor.color;
		arrayLists.shininess = Constants.chainHighShininess;
		//gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, specularColor.color, 0);
		//gl.glMaterialfv(GL.GL_FRONT, GL.GL_EMISSION, emissiveColor.color, 0);
		//gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, ambientColor.color);
//		highShininess[0] = 128.0f;
		//gl.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, shininessTemp, 0);

		// Appearance backboneApp = new Appearance();
		// backboneApp.setMaterial( material );

		// ColoringAttributes coloring = new ColoringAttributes( color,
		// ColoringAttributes.NICEST );
		// PolygonAttributes polygonAtt = new PolygonAttributes();
		// polygonAtt.setCullFace( PolygonAttributes.CULL_BACK );
		//gl.glCullFace(GL.GL_BACK);
		// polygonAtt.setPolygonMode( polygonStyle );
		//gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
		// backboneApp.setPolygonAttributes( polygonAtt );

		// Shape3D backboneShape = new Shape3D();
		// Shape3D arrowShape = new Shape3D();

		// GeometryArray figure = null;
		// GeometryArray arrow = null;

		// BranchGroup branch = new BranchGroup();
		final int start = 0; // Keeps track of the starting point of each chain
						// (Several chains per structure in general).
		int[] stripVertexCounts = null; // Stores number of vertices in each
										// strip of the chain.
		final int verticesPerBase = style.getVertexCount();
		final int edges = verticesPerBase - 1;

		IntrinsicCrossSection sect = null;

		// drawArrow = false;
		AffineCrossSection[] cs = null;
		if (this.drawArrow) {
			cs = new AffineCrossSection[pointCount + 1];
			stripVertexCounts = new int[pointCount + 2];
		} else {
			cs = new AffineCrossSection[pointCount];
			stripVertexCounts = new int[pointCount + 1];
		}

		// Generate the array of vertex numbers for all strips in the geometry.
		//
		final int arrowStart = pointCount - 3;
		if (this.csType == CrossSectionType.RECTANGULAR_RIBBON) {
			// The two lids have a different # of vertices than a regular
			// segment of the extrusion
			//
			stripVertexCounts[0] = stripVertexCounts[stripVertexCounts.length - 1] = 2 * verticesPerBase;
			for (int j = 1; j < stripVertexCounts.length - 1; j++) {
				stripVertexCounts[j] = 4 * edges; // the same # of vertices in
													// each strip.
			}

			sect = CrossSectionBuilder.getIntrinsicCrossSection(style, null);
		} else if (this.csType == CrossSectionType.DOUBLE_FACE) {
			// The two lids have a different # of vertices than a regular
			// segment of the extrusion
			//
			stripVertexCounts[0] = stripVertexCounts[stripVertexCounts.length - 1] = 2 * verticesPerBase;
			for (int j = 1; j < stripVertexCounts.length - 1; j++) {
				stripVertexCounts[j] = 4 * edges; // the same # of vertices in
													// each strip.
			}

			sect = CrossSectionBuilder.getIntrinsicCrossSection(style,
					triheds[0].getTangent());
		} else if (this.csType == CrossSectionType.SINGLE_FACE) {
			// TODO this is a single-polygon implementation, where both faces
			// are seen. Ignoring it for now.

			// The two lids have a different # of vertices than a regular
			// segment of the extrusion
			//
			stripVertexCounts[0] = stripVertexCounts[stripVertexCounts.length - 1] = 2 * verticesPerBase;
			for (int j = 1; j < stripVertexCounts.length - 1; j++) {
				stripVertexCounts[j] = 4 * edges; // the same # of vertices in
													// each strip.
			}

			// polygonAtt.setBackFaceNormalFlip( true );
			// polygonAtt.setCullFace( PolygonAttributes.CULL_NONE );
			sect = CrossSectionBuilder.getIntrinsicCrossSection(style,
					triheds[0].getTangent());
			this.rounded = false;
		} else if (this.csType == CrossSectionType.OBLONG) {
			// The two lids have a different # of vertices than a regular
			// segment of the extrusion
			//
			stripVertexCounts[0] = stripVertexCounts[stripVertexCounts.length - 1] = 2 * verticesPerBase;
			for (int j = 1; j < stripVertexCounts.length - 1; j++) {
				stripVertexCounts[j] = 4 * edges; // the same # of vertices in
													// each strip.
			}

			sect = CrossSectionBuilder.getIntrinsicCrossSection(style,
					triheds[0].getTangent());
			sect.averageNormals();
			this.rounded = true;
		} else if (this.csType == CrossSectionType.POINT) {
			// should use boolean ribbon = false that several of the SSGeometry
			// functions have instead.
			stripVertexCounts = new int[1];
			stripVertexCounts[0] = pointCount;
			// backboneApp.setLineAttributes( new LineAttributes( 3.0f,
			// LineAttributes.PATTERN_SOLID, false ) );
		}
		
		if (this.csType != CrossSectionType.POINT) {
			sect.setColors(null);

			if (this.drawArrow) {
				for (int i = 0; i <= arrowStart; i++) {
					cs[i] = new AffineCrossSection(sect, triheds[i]);
				}
				for (int i = arrowStart + 1; i <= pointCount; i++) {
					cs[i] = new AffineCrossSection(sect, triheds[i - 1]);
				}
			} else {
				for (int i = 0; i < pointCount; i++) {
					cs[i] = new AffineCrossSection(sect, triheds[i]);
				}
			}

			if (this.drawArrow) {
				int j = 0;
				for (int i = arrowStart + 1; i < cs.length; i++) {
					cs[i].scale(arrowScale[j]);
					j++;
				}
			}
		}

		// record the vertex ranges
		final Fragment f = (Fragment)this.userData;
		final Chain c = f.getChain();
		final int resCount = f.getResidueCount();
		this.ranges = new Object[resCount];
//		arrayLists.setupRangeMap(resCount);
		
		if(c.getResidueCount() - 1 == f.getEndResidueIndex()) {
		}
		
		if(f.getStartResidueIndex() == 0) {
		}
		
//		Object existsObject = new Object();
//		Object doesntExistObject = new Object();
//		RangeMap rangeMap = new RangeMap(0, arrayLists.vertexSize - 1, doesntExistObject);
		
		int vertexSize = -1;
		if(this.csType != CrossSectionType.POINT) {
			vertexSize = 4 * ((cs.length - 1) * (cs[0].getVertexCount() - 1) + cs[0].getVertexCount());
		} else {
			vertexSize = pointCount;
		}
		
		for(int index = 0; index < resCount; index++) {
			if (this.csType != CrossSectionType.POINT) {
				int startIndex, endIndex;
				final int halfSpan = 2 * this.segments * (verticesPerBase - 1);
				final int center = 2 * verticesPerBase + 2 * halfSpan * index;

				if (index > 0) {
					startIndex = center - halfSpan;
				} else {
					startIndex = 0;
				}
				if (index < f.getResidueCount() - 1) {
					endIndex = center + halfSpan;
				} else {
					endIndex = vertexSize - 17;		//TODO Why 21? Causes strange extra triangles when vertexSize - 1.
					if (this.drawArrow) {
						endIndex += 4 * (verticesPerBase - 1);
					}
				}
				
				this.ranges[index] = new Object[] {f.getResidue(index), new int[] {startIndex, endIndex}};
			} else {
				int startIndex, endIndex;
				final int halfSpan = this.segments / 2;
				final int center = index * this.segments;

				if (index > 0) {
					startIndex = center - halfSpan;
				} else {
					startIndex = 0;
				}
				if (index < f.getResidueCount() - 1) {
					endIndex = center + halfSpan;
				} else {
					endIndex = vertexSize - 1;
				}

				this.ranges[index] = new Object[] {f.getResidue(index), new int[] {startIndex, endIndex}};
			}
		}
		
		if (this.csType != CrossSectionType.POINT) {

			final Extrusion figure = new Extrusion(cs.length, start,
					stripVertexCounts, cs, colorMap, start, this.rounded);
			figure.draw(arrayLists, gl, glu, glut, this.ranges);
		} else {
//			arrayLists.shininess = noShininess;
			arrayLists.disableLighting();
			
			final BackboneLine figure = new BackboneLine(pointCount,
					stripVertexCounts, coordinates, colorMap);
			figure.draw(arrayLists, gl, glu, glut, this.ranges);
		}
		
//		for(int i = 0; i < arrayLists.vertexSize; i++) {
////			if(rangeMap.getContiguousValue(i, i) == doesntExistObject) {
//				System.out.print("");
//			}
//		}
		
		return arrayLists;
	}

	/**
	 * Sets the normals along the spine.
	 */
	public void setNormals(final Vector3f[] normals) {
		this.normals = normals;
	}

	// Smooth a set of normals
	//
	private void smoothNormals(final Vector3f[] norm, final Vector3f[] tangent, final int times) {
		final Vector3f tmp = new Vector3f();
		final Vector3f tmp1 = new Vector3f();
		final Vector3f tmp2 = new Vector3f();
		Vector3f previousNorm = new Vector3f();
		for (int j = 0; j < times; j++) {
			previousNorm = norm[0];
			for (int i = 1; i < norm.length - 1; i++) {
				tmp2.set(norm[i]);
				norm[i].add(previousNorm);
				norm[i].add(norm[i + 1]);
				tmp1.set(tangent[i]);
				tmp1.normalize();
				tmp.set(tmp1);
				tmp.scale(norm[i].dot(tmp1));
				norm[i].sub(tmp);
				norm[i].normalize();
				previousNorm = new Vector3f(tmp2);
				// previousNorm = norm[i];
			}
		}
	}

	/*
	 * // Smooth a set of normals // private void smoothNormals( Vector3d[]
	 * norm, Vector3d[] tangent, int times ) { // This is a version based on the
	 * same approach as in Priestle smoothing // int size = norm.length; float
	 * scale = 0.0f; Vector3d vx0, vx1, vx2, vx1p, vx, vn, dvx, tmp; vn = new
	 * Vector3d(); dvx = new Vector3d(); vx = new Vector3d(); vx1p = new
	 * Vector3d(); vx0 = new Vector3d( ); vx1 = new Vector3d( ); vx2 = new
	 * Vector3d( ); tmp = new Vector3d(); for( int i = 0; i < times; i++ ) { //
	 * Adopt a sliding window approach // vx0.set( norm[0].coordinates[0], norm[0].coordinates[1],
	 * norm[0].coordinates[2] ); vx1.set( norm[1].coordinates[0], norm[1].coordinates[1], norm[1].coordinates[2] ); vx2.set(
	 * norm[2].coordinates[0], norm[2].coordinates[1], norm[2].coordinates[2] ); for( int j = 0; j < size-2; j++) {
	 * vn.sub( vx2, vx0 ); vn.normalize(); dvx.sub( vx1, vx0 ); scale = dvx.dot(
	 * vn ); vx.scaleAdd( scale, vn, vx0 ); vx1p.add( vx, vx1 ); vx1p.scale(
	 * 0.5f ); tmp.set( tangent[i+1] ); tmp.normalize(); tmp.scale( vx1p.dot(
	 * tmp ) ); vx1p.sub( tmp ); vx1p.normalize();
	 *  // Now we can (re)set the coordinate at j+1 norm[j+1].coordinates[0] = vx1p.coordinates[0];
	 * norm[j+1].coordinates[1] = vx1p.coordinates[1]; norm[j+1].coordinates[2] = vx1p.coordinates[2];
	 *  // And reset the vectors we need if( j != (size-3) ) { vx0 = vx1; vx1 =
	 * vx2; vx2 = new Vector3d( norm[j+3].coordinates[0], norm[j+3].coordinates[1], norm[j+3].coordinates[2] ); } } } }
	 */

	// Calculate All tangent vectors along the spine and also the (unit) normal
	// vectors.
	// Should be revised such that it can take into consideration neighbouring
	// residues if available.
	//
	private void getAllTangentsAndNormals(final Vec3f[] coords, final Vector3f[] vec,
			final Vector3f[] norm) {
		Vector3f vvec1 = null;
		Vector3f vvec2 = null;
		Vector3f norm1 = null;
		Vector3f norm2 = null;
		Vector3f vM1 = null;
		Vector3f vP1 = null;
		Vector3f v1 = null;
		Vector3f tmp = null;
		Vector3f tmp1 = null;
		Vector3f v0 = null;

		if (coords.length == 2) {
			// In an ideal world this would not happen.
			// How should we treat this rather special case?
			// Here information about neighbouring residue would be especially
			// helpfull
			//
			final int i = 0;
			float tmpScal = 1;
			norm1 = new Vector3f();
			norm2 = new Vector3f();
			v1 = new Vector3f(coords[i + 1].value[0], coords[i + 1].value[1],
					coords[i + 1].value[2]);
			v0 = new Vector3f(coords[i].value[0], coords[i].value[1],
					coords[i].value[2]);

			if (this.previousCaCoord != null) {
				vM1 = new Vector3f(this.previousCaCoord.value[0],
						this.previousCaCoord.value[1], this.previousCaCoord.value[2]);
			}
			if (this.nextCaCoord != null) {
				vP1 = new Vector3f(this.nextCaCoord.value[0], this.nextCaCoord.value[1],
						this.nextCaCoord.value[2]);
			}

			if (this.previousCaCoord == null) {
				// Tangents
				vvec1 = new Vector3f();
				vvec1.sub(v1, v0);

				vec[0] = new Vector3f(vvec1);
				if (this.nextCaCoord == null) {
					vec[1] = vec[0];

					// Normals: No good choice unless information about previous
					// residue is available
					// Just get something normal to the tangent
					// Assume vvec1 not zero, otherwise there is something wrong
					// with the data
					norm1.set(vvec1);
					vvec1.normalize();
					if (norm1.x != 0.0d) {
						norm1.x *= 2.0d;
						vvec1.scale(vvec1.dot(norm1));
						tmp = new Vector3f();
						tmp.sub(norm1, vvec1);
						norm1.set(tmp);

						// If we were unlucky and the vvec1 was mainly in the x
						// direction
						if (norm1.length() < 1.0e-6d) {
							norm1.y += 1.0d;
						}
						norm1.normalize();
						norm[0] = new Vector3f(norm1);
						norm[1] = norm[0];
					}
				} else {
					vvec2 = new Vector3f();
					vvec2.sub(vP1, v0);
					vec[1] = new Vector3f(vvec2);
					vP1.sub(v1);
					v1.sub(v0);
					vP1.normalize();
					v1.normalize();
					norm2.sub(vP1, v1);
					vvec2.normalize();
					vvec2.scale(vvec2.dot(norm2));
					norm2.sub(vvec2);
					norm2.normalize();
					norm[1] = new Vector3f(norm2);
					vvec1.normalize();
					vvec1.scale(vvec1.dot(norm2));
					norm2.sub(vvec1);
					norm2.normalize();
					norm[0] = new Vector3f(norm2);
				}
			} else { // We DO have information about previous CA atom

				// Tangents
				vvec1 = new Vector3f();
				vvec1.sub(v1, vM1);

				vec[0] = new Vector3f(vvec1);

				if (this.nextCaCoord == null) {
					// vec[1] = vec[0];

					v1.sub(v0);

					vec[1] = new Vector3f(v1);

					v0.sub(vM1);
					v1.normalize();
					v0.normalize();
					norm1.sub(v1, v0);

					// Subtract the component parallel to the tangent
					vvec1.normalize();
					vvec1.scale(vvec1.dot(norm1));
					norm1.sub(vvec1);

					norm1.normalize();
					norm[0] = new Vector3f(norm1);
					vvec1.set(vec[1]);
					vvec1.normalize();
					vvec1.scale(vvec1.dot(norm1));
					norm1.sub(vvec1);

					norm1.normalize();
					norm[1] = new Vector3f(norm1);
					tmpScal = norm[1].dot(norm[0]);
					if (tmpScal <= 0) {
						norm[1].negate();
					}
				} else { // We also have a next coordinate, use it
					vP1 = new Vector3f(this.nextCaCoord.value[0],
							this.nextCaCoord.value[1], this.nextCaCoord.value[2]);
					vvec2 = new Vector3f();
					vvec2.sub(vP1, v0);
					vec[1] = new Vector3f(vvec2);

					vP1.sub(v1);
					v1.sub(v0);
					v0.sub(vM1);
					vP1.normalize();
					v1.normalize();
					v0.normalize();
					// Normals, review this, not actually perpendicular to the
					// tangent as derived
					norm2.sub(vP1, v1);
					norm1.sub(v1, v0);

					vvec1.normalize();
					vvec2.normalize();
					vvec1.scale(vvec1.dot(norm1));
					vvec2.scale(vvec2.dot(norm2));
					norm1.sub(vvec1);
					norm2.sub(vvec2);

					norm2.normalize();
					norm1.normalize();

					norm[0] = new Vector3f(norm1);
					norm[1] = new Vector3f(norm2);
					tmpScal = norm[1].dot(norm[0]);
					if (tmpScal <= 0) {
						norm[1].negate();
					}
				}
			}

			return;
		}

		// First calculate the tangents at non-boundary sites. This is
		// non-ambiguous
		// 
		for (int i = 1; i < coords.length - 1; i++) {
			vP1 = new Vector3f(coords[i + 1].value[0], coords[i + 1].value[1],
					coords[i + 1].value[2]);
			vM1 = new Vector3f(coords[i - 1].value[0], coords[i - 1].value[1],
					coords[i - 1].value[2]);
			vec[i] = new Vector3f();
			vec[i].sub(vP1, vM1);
			// vec[i].normalize();
		}

		// Tangent and Normal for i = 0
		// 
		vP1 = new Vector3f(coords[1].value[0], coords[1].value[1],
				coords[1].value[2]);
		v0 = new Vector3f(coords[0].value[0], coords[0].value[1],
				coords[0].value[2]);
		if (this.previousCaCoord != null) {
			vM1 = new Vector3f(this.previousCaCoord.value[0],
					this.previousCaCoord.value[1], this.previousCaCoord.value[2]);
			vvec1 = new Vector3f(vP1);
			vvec1.sub(vM1);
			vec[0] = new Vector3f(vvec1);
			// vec[0].normalize();

			vP1.sub(v0);
			v0.sub(vM1);
			vP1.normalize();
			v0.normalize();
			norm[0] = new Vector3f();
			norm[0].sub(vP1, v0);
			tmp1 = new Vector3f();
			tmp1.set(vvec1);
			tmp1.normalize();
			tmp = new Vector3f();
			tmp.set(tmp1);
			tmp.scale(norm[0].dot(tmp1));
			norm[0].sub(tmp);

			norm[0].normalize();
		} else {
			vP1.sub(v0);
			vec[0] = new Vector3f();
			vec[0].set(vP1);
			// vec[0].normalize();
		}

		// Tangent and Normal for i = coords.length - 1
		//
		vM1 = new Vector3f(coords[coords.length - 2].value[0],
				coords[coords.length - 2].value[1],
				coords[coords.length - 2].value[2]);
		v0 = new Vector3f(coords[coords.length - 1].value[0],
				coords[coords.length - 1].value[1],
				coords[coords.length - 1].value[2]);
		if (this.nextCaCoord != null) {
			vP1 = new Vector3f(this.nextCaCoord.value[0], this.nextCaCoord.value[1],
					this.nextCaCoord.value[2]);
			vvec2 = new Vector3f(vP1);
			vvec2.sub(vM1);
			vec[coords.length - 1] = new Vector3f(vvec2);
			// vec[coords.length-1].normalize();

			vP1.sub(v0);
			v0.sub(vM1);
			vP1.normalize();
			v0.normalize();
			norm[coords.length - 1] = new Vector3f();
			norm[coords.length - 1].sub(vP1, v0);
			vvec2.normalize();
			vvec2.scale(norm[coords.length - 1].dot(vvec2));
			norm[coords.length - 1].sub(vvec2);
			norm[coords.length - 1].normalize();
		} else {
			vec[coords.length - 1] = new Vector3f(v0);
			vec[coords.length - 1].sub(vM1);
			// vec[coords.length-1].normalize();
		}

		// Calculate the normals
		//
		tmp = new Vector3f();
		tmp1 = new Vector3f();
		for (int i = 1; i < coords.length - 1; i++) {
			vP1.set(coords[i + 1].value[0], coords[i + 1].value[1],
					coords[i + 1].value[2]);
			v0.set(coords[i].value[0], coords[i].value[1], coords[i].value[2]);
			vM1.set(coords[i - 1].value[0], coords[i - 1].value[1],
					coords[i - 1].value[2]);
			vP1.sub(v0);
			v0.sub(vM1);
			vP1.normalize();
			v0.normalize();
			norm[i] = new Vector3f();
			norm[i].sub(vP1, v0);

			tmp1.set(vec[i]);
			tmp1.normalize();
			tmp.set(tmp1);
			tmp.scale(norm[i].dot(tmp1));
			norm[i].sub(tmp);
			norm[i].normalize();
		}

		if (norm[0] == null) {
			norm[0] = new Vector3f(norm[1]);
			tmp1.set(vec[0]);
			tmp1.normalize();
			tmp.set(tmp1);
			tmp.scale(norm[0].dot(tmp1));
			norm[0].sub(tmp);
			norm[0].normalize();
		}

		if (norm[coords.length - 1] == null) {
			norm[coords.length - 1] = new Vector3f(norm[coords.length - 2]);
			tmp1.set(vec[coords.length - 1]);
			tmp1.normalize();
			tmp.set(tmp1);
			tmp.scale(norm[coords.length - 1].dot(tmp1));
			norm[coords.length - 1].sub(tmp);
			norm[coords.length - 1].normalize();
		}

		Vector3f previousNorm = new Vector3f();
		previousNorm = norm[0];
		float tmpScalar = 0;

		previousNorm = norm[0];
		for (int i = 1; i < coords.length; i++) {
			tmpScalar = norm[i].dot(previousNorm);
			// System.out.println( "Dot product of consequtive normals: " +
			// tmpScalar );
			if (tmpScalar < 0.0) {
				// System.out.println( "Site at: " + i + " switched direction "
				// + tmpScalar + " Reversing" );
				norm[i].negate();
			}
			previousNorm = norm[i];
		}

		this.smoothNormals(norm, vec, 2);

		return;
	}

	/**
	 * Hightlights the portion of SS sorounding residue at index.
	 */
	/*public void highlightResidueRegion(int index, float[] color) {

		if (csType != 6) {
			int verticesPerBase = csStyle.getVertexCount();
			int startIndex, endIndex;
			int halfSpan = 2 * segments * (verticesPerBase - 1);
			int center = 2 * verticesPerBase + 2 * halfSpan * index;

			if (index > 0) {
				startIndex = center - halfSpan;
			} else {
				startIndex = center;
			}
			if (index < coords.length - 1) {
				endIndex = center + halfSpan;
			} else {
				endIndex = center;
				if (drawArrow) {
					endIndex += 4 * (verticesPerBase - 1);
				}
			}

			Extrusion geom = (Extrusion) shape.getGeometry();
			if (!highlighted) {
				// Needs highlighted
				//
				geom.highlight(startIndex, endIndex, color);
				highlighted = true;
			} else {
				// Already highlighted, probably needs to be returned to the
				// initial color
				//
				geom.updateColors(startIndex, endIndex, index * segments);
				highlighted = false;
			}
		} else {
			int startIndex, endIndex;
			int halfSpan = segments / 2;
			int center = index * segments;

			if (index > 0) {
				startIndex = center - halfSpan;
			} else {
				startIndex = center;
			}
			if (index < coords.length - 1) {
				endIndex = center + halfSpan;
			} else {
				endIndex = center;
			}

			BackboneLine geom = (BackboneLine) shape.getGeometry();
			if (!highlighted) {
				geom.highlight(startIndex, endIndex, color);
				highlighted = true;
			} else {
				geom.updateColors(startIndex, endIndex);
				highlighted = false;
			}
		}
	}*/

	/**
	 * Hightlights the portion of SS sorounding residue at index.
	 */
	/*public void highlightResidue(int index, float[] color) {

		if (csType != 6) {
			int verticesPerBase = csStyle.getVertexCount();
			int startIndex, endIndex;
			int halfSpan = 2 * segments * (verticesPerBase - 1);
			int center = 2 * verticesPerBase + 2 * halfSpan * index;

			if (index > 0) {
				startIndex = center - halfSpan;
			} else {
				startIndex = center;
			}
			if (index < coords.length - 1) {
				endIndex = center + halfSpan;
			} else {
				endIndex = center;
				if (drawArrow) {
					endIndex += 4 * (verticesPerBase - 1);
				}
			}

			Extrusion geom = (Extrusion) shape.getGeometry();
			geom.highlight(startIndex, endIndex, color);
			highlighted = true;
		} else {
			int startIndex, endIndex;
			int halfSpan = segments / 2;
			int center = index * segments;

			if (index > 0) {
				startIndex = center - halfSpan;
			} else {
				startIndex = center;
			}
			if (index < coords.length - 1) {
				endIndex = center + halfSpan;
			} else {
				endIndex = center;
			}

			BackboneLine geom = (BackboneLine) shape.getGeometry();
			geom.highlight(startIndex, endIndex, color);
		}
	}*/

	/**
	 * Resets the vertex color in the geometry shape of this SsGeometry object.
	 */
	/*public void resetGeometryColor() {
		if (csType != 6) {
			Extrusion geom = (Extrusion) shape.getGeometry();
			geom.setColors(pathColorMap);
		} else {
			BackboneLine geom = (BackboneLine) shape.getGeometry();
			geom.setColors(pathColorMap);
		}

		for (int i = 0; i < coords.length; i++) {
			resetResidueColor(i);
		}
	}*/

	/**
	 * Resets the color of the sorounding residue area at index.
	 */
	/*public void resetResidueColor(int index) {

		if (csType != 6) {
			int verticesPerBase = csStyle.getVertexCount();
			int startIndex, endIndex;
			int halfSpan = 2 * segments * (verticesPerBase - 1);
			int center = 2 * verticesPerBase + 2 * halfSpan * index;

			if (index > 0) {
				startIndex = center - halfSpan;
			} else {
				startIndex = center - 2 * verticesPerBase;
			}
			if (index < coords.length - 1) {
				endIndex = center + halfSpan;
			} else {
				endIndex = center + 2 * verticesPerBase;
				if (drawArrow) {
					endIndex += 4 * (verticesPerBase - 1);
				}
			}

			Extrusion geom = (Extrusion) shape.getGeometry();
			geom.updateColors(startIndex, endIndex, index * segments);
		} else {
			int startIndex, endIndex;
			int halfSpan = segments / 2;
			int center = index * segments;

			if (index > 0) {
				startIndex = center - halfSpan;
			} else {
				startIndex = center;
			}
			if (index < coords.length - 1) {
				endIndex = center + halfSpan;
			} else {
				endIndex = center;
			}

			BackboneLine geom = (BackboneLine) shape.getGeometry();
			geom.updateColors(startIndex, endIndex);
		}
	}*/

	/**
	 * Sets the tangents along the spine.
	 */
	public void setTangents(final Vector3f[] tangents) {
		this.tangents = tangents;
	}

	/**
	 * Sets the tangents along the spine.
	 */
	public void setDrawArrow(final boolean drawArrow) {
		this.drawArrow = drawArrow;
	}

	/**
	 * Sets the rendering mode for the polygonal mesh.
	 */
	/*public void setPolygonStyle(int style) {
		polygonStyle = style;
	}*/

	private void processQualityInfo(final float quality) {
		final int maxSegments = 10;
		final int minSegments = 1;

		if (quality < ((float) minSegments) / ((float) maxSegments)) {
			this.segments = minSegments;
			this.segments += this.segments % 2;
		} else {
			this.segments = (int) (quality * maxSegments);
			this.segments += this.segments % 2; // Keep it even for correct highlighting
		}
	}

}
