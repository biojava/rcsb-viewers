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
import org.rcsb.vf.glscene.SecondaryStructureBuilder.Priestle;
import org.rcsb.vf.glscene.SecondaryStructureBuilder.CrossSectionStyle.CrossSectionType;
import org.rcsb.vf.glscene.SecondaryStructureBuilder.vec.Vec3f;
import org.rcsb.vf.glscene.jogl.Color3f;
import org.rcsb.vf.glscene.jogl.Constants;
import org.rcsb.vf.glscene.jogl.DisplayLists;

import com.jogamp.opengl.util.gl2.GLUT;


/**
 * HelixGeometry is an {@link SsGeometry} container which, primarily, manages
 * the geometry associated with an alpha-helix. No smoothing is done, the ribon
 * is interpolated between original CA coordinates. As with all
 * {@link GeometryEntity} objects, it can have other <code> Geometry </code>
 * children.
 * <P>
 * The algorithm for the generation of the normals and binormals along the helix
 * are borrowed from Molscript
 * <P>
 * 
 * @author Apostol Gramada
 */
public class HelixGeometry extends SsGeometry {

	// *******************************************************************************
	// Class variables
	// *******************************************************************************
	//
	// Empirical geometry parameters for STRAND.
	private static final float HELIX_HERMITE_FACTOR = 4.7f;

	private static final float HELIX_ALPHA = 0.558f; // The equivalent in rad of a
												// 32.0^o angle

	private static final float HELIX_BETA = -0.191f; // The equivalent in rad of a
												// -11.0^o angle

	// Styles - cross section
	private float width = 2.6f;
	private float height = 0.80f;
	private int facets = 14;

	private ConformationShape ssShape = ConformationShape.RIBBON;

	/**
	 * Constructs a HelixGeometry objects with default values for the number of
	 * facets, segments, and cross section style.
	 */
	public HelixGeometry(final StructureComponent sc) {
		super(sc);
		this.csType = CrossSectionType.REGULAR_POLYGON;
		this.ssShape = ConformationShape.RIBBON;
		this.uniformColor = false;
		this.userData = "Helix";
		this.segments = 8;
		this.facets = 12;
	}

	
	public DisplayLists generateJoglGeometry(final GL gl, final GLU glu, final GLUT glut) {
		this.processQualityInfo(this.quality);
		if (this.ssShape == ConformationShape.CYLINDER) {
			this.segments = 2;
		}

		// Create an object for Hermite sampling
		final Hermite hermite = new Hermite();
		hermite.setKnotWeight(HELIX_HERMITE_FACTOR);

		final int spinePointCount = (this.coords.length - 1) * this.segments + 1;

		// We need to enrich the set of points on the Coil path with additional
		// points
		// provided by an interpolation technique, in this case Hermite
		// interpolation.
		// Assume for the moment that our collection of atoms contains a single
		// chain.
		//
		final Vector3f[] pathCoords = new Vector3f[spinePointCount];
		Vector3f[] scale = new Vector3f[spinePointCount];
		final FrenetTrihedron[] pathTrihedron = new FrenetTrihedron[spinePointCount];

		final Vec3f vec1 = new Vec3f();
		final Vec3f vec2 = new Vec3f();
		final Vector3f tan1 = new Vector3f();
		final Vector3f tan2 = new Vector3f();
		Vector3f hermiteTan = null;
		final Vector3f bin1 = new Vector3f();
		final Vector3f bin2 = new Vector3f();
		final Vector3f origin = new Vector3f();
		final Vector3f tmpv = new Vector3f();

		final Vec3f sampleCoord = new Vec3f();
		final Vector3f sampleOrigin = new Vector3f();
		FrenetTrihedron firstTrihedron = null;
		FrenetTrihedron secondTrihedron = null;

		if (this.ssShape == ConformationShape.RIBBON) {
			if (this.csStyle == null) {
				final float[] diams = new float[2];
				diams[0] = this.height;
				diams[1] = this.width;
				this.csStyle = new CrossSectionStyle(this.csType, diams);
				if (this.csStyle.getVertexCount() == 0) {
					this.csStyle.setVertexCount(this.facets + 1);
				}
				if ((this.facets != this.csStyle.getVertexCount())
						&& ((this.csType == CrossSectionType.ROUNDED_TUBE) || (this.csType == CrossSectionType.REGULAR_POLYGON))) {
					this.csStyle.setVertexCount(this.facets + 1);
				}
			}

			this.csType = this.csStyle.getStyleType();

			this.getTangentsAndBinormals(0, this.coords, vec1, vec2, bin1, bin2);
			tan1.set(vec1.value[0], vec1.value[1], vec1.value[2]);

			origin.set(this.coords[0].value[0], this.coords[0].value[1],
					this.coords[0].value[2]);
			secondTrihedron = new FrenetTrihedron(origin, tan1, bin1, 1);

			float t = 0.0f;
			// Parameters used to scale the first and last segment
			//
			int k = -1;
			for (int i = 0; i < this.coords.length - 1; i++) {
				k++;
				this.getTangentsAndBinormals(i, this.coords, vec1, vec2, bin1, bin2);
				pathCoords[k] = new Vector3f(this.coords[i].value[0],
						this.coords[i].value[1], this.coords[i].value[2]);

				pathTrihedron[k] = new FrenetTrihedron(secondTrihedron);
				firstTrihedron = pathTrihedron[k];

				hermite.set(this.coords[i], this.coords[i + 1], vec1,
						vec2);

				tan2.set(vec2.value[0], vec2.value[1], vec2.value[2]);
				origin.set(this.coords[i + 1].value[0], this.coords[i + 1].value[1],
						this.coords[i + 1].value[2]);
				secondTrihedron = new FrenetTrihedron(origin, tan2, bin2, 1);

				// Add additional interpolated points
				//
				for (int j = 1; j < this.segments; j++) {
					k++;
					t = j * (1.0f / this.segments);
					pathTrihedron[k] = new FrenetTrihedron();
					hermite.sample(t, sampleCoord);
					hermiteTan = hermite.getTangent(t);
					hermiteTan.normalize();
					pathTrihedron[k].setTangent(hermiteTan);
					pathTrihedron[k].interpolateByBinormal(t, firstTrihedron,
							secondTrihedron);

					sampleOrigin.set(sampleCoord.value[0],
							sampleCoord.value[1], sampleCoord.value[2]);
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

			scale = this.getTrigScale(spinePointCount, this.segments, SsGeometry.coilWidth, this.width);
		} else if (this.ssShape == ConformationShape.CYLINDER) {

			final Vector3f[] tmpCoord = new Vector3f[this.coords.length];
			final Vector3f[] tmpTan = new Vector3f[this.coords.length];
			final Vector3f[] tmpNorm = new Vector3f[this.coords.length];

			hermite.setKnotWeight(1.5f);

			this.getCylinderPath(this.coords, tmpCoord, tmpTan, tmpNorm, 6);

			tmpv
					.set(this.coords[0].value[0], this.coords[0].value[1],
							this.coords[0].value[2]);
			tmpv.sub(tmpCoord[0]);
			this.height = this.width = (float)(2.0 * Math.sqrt(tmpv.dot(tmpv)) + SsGeometry.coilWidth);
			if (this.csStyle == null) {
				final float[] diams = new float[2];
				diams[0] = this.height;
				diams[1] = this.width;
				this.csStyle = new CrossSectionStyle(this.csType, diams);
				if (this.csStyle.getVertexCount() == 0) {
					this.csStyle.setVertexCount(this.facets + 1);
				}
				if ((this.facets != this.csStyle.getVertexCount())
						&& ((this.csType == CrossSectionType.ROUNDED_TUBE) || (this.csType == CrossSectionType.REGULAR_POLYGON))) {
					this.csStyle.setVertexCount(this.facets + 1);
				}
			}

			this.csType = this.csStyle.getStyleType();

			tan1.set(tmpTan[0]);

			origin.set(tmpCoord[0]);
			secondTrihedron = new FrenetTrihedron(origin, tan1, tmpNorm[0]);

			float t = 0.0f;
			//
			int k = -1;
			for (int i = 0; i < this.coords.length - 1; i++) {
				k++;
				pathCoords[k] = tmpCoord[i];

				pathTrihedron[k] = new FrenetTrihedron(secondTrihedron);
				firstTrihedron = pathTrihedron[k];

				hermite.set(tmpCoord[i], tmpCoord[i + 1], tmpTan[i],
						tmpTan[i + 1]);
				secondTrihedron = new FrenetTrihedron(tmpCoord[i + 1],
						tmpTan[i + 1], tmpNorm[i + 1]);

				// Add additional interpolated points
				//
				for (int j = 1; j < this.segments; j++) {
					k++;
					t = j * (1.0f / this.segments);
					pathTrihedron[k] = new FrenetTrihedron();
					hermite.sample(t, sampleCoord);
					hermiteTan = hermite.getTangent(t);
					hermiteTan.normalize();
					pathTrihedron[k].setTangent(hermiteTan);
					pathTrihedron[k].interpolateByBinormal(t, firstTrihedron,
							secondTrihedron);
					sampleOrigin.set(sampleCoord.value[0],
							sampleCoord.value[1], sampleCoord.value[2]);
					pathTrihedron[k].setOriginOnly(sampleOrigin);

					pathCoords[k] = new Vector3f(sampleCoord.value[0],
							sampleCoord.value[1], sampleCoord.value[2]);
				}
			}
			k++;
			pathCoords[k] = new Vector3f(tmpCoord[tmpCoord.length - 1]);
			pathTrihedron[k] = new FrenetTrihedron(secondTrihedron);

			scale = this.getUniformScale(spinePointCount);
		}

		if (this.uniformColor) {
			if (this.ssColor == null) {
				this.ssColor = new Color(1.0f, 0.0f, 0.0f);
			}
			final Color3f[] vColor = new Color3f[1];
			vColor[0] = new Color3f(this.ssColor);
			this.csStyle.setVertexColors(vColor);
			this.pathColorMap = new float[spinePointCount + 1][3];
			for (int i = 0; i < spinePointCount + 1; i++) {
				this.pathColorMap[i] = this.ssColor.getColorComponents(null);
			}
		} else {
			this.setPathColor(spinePointCount);
		}

		if (this.ssShape == ConformationShape.RIBBON) {
			return this.drawFigure(this.csStyle, pathCoords, scale, pathTrihedron,
					this.pathColorMap, spinePointCount, gl, glu, glut);
		} else if (this.ssShape == ConformationShape.CYLINDER) {
			return this.drawFigure(this.csStyle, pathCoords, scale, pathTrihedron,
					this.pathColorMap, spinePointCount, gl, glu, glut);
		}
		
		return null;
	}
	
	private Object[] ranges = null;
	
	public static final float[] highShininess = { 128f };
	public static final float[] noShininess = { 0 };
	private DisplayLists drawFigure(final CrossSectionStyle style, final Vector3f[] coordinates,
			final Vector3f[] scale, final FrenetTrihedron[] triheds, final float[][] colorMap,
			final int pointCount, final GL gl, final GLU glu, final GLUT glut) {
		final DisplayLists arrayLists = new DisplayLists(this.structureComponent);
		
		arrayLists.specularColor = Constants.chainSpecularColor.color;
		arrayLists.emissiveColor = Constants.chainEmissiveColor.color;
		arrayLists.shininess = Constants.chainHighShininess;

		final int start = 0; // Keeps track of the starting point of each chain
						// (Several chains per structure in general).
		int[] stripVertexCounts = null; // Stores number of vertices in each
										// strip of the chain.
		final int verticesPerBase = style.getVertexCount();

		IntrinsicCrossSection sect = null;
		stripVertexCounts = new int[pointCount + 1];
		stripVertexCounts[0] = stripVertexCounts[pointCount] = 2 * verticesPerBase;
		// The two lids have a different # of vertices than a regular segment of
		// the extrusion
		//
		for (int j = 1; j < pointCount; j++) {
			stripVertexCounts[j] = 4 * (style.getVertexCount() - 1); // the
																		// same
																		// # of
																		// vertices
																		// in
																		// each
																		// strip.
		}
		sect = CrossSectionBuilder.getIntrinsicCrossSection(style, triheds[0]
				.getTangent());
		sect.setColors(null);

		switch (this.csType) {
		case RECTANGULAR_RIBBON:
			this.rounded = false;
			break;
		case DOUBLE_FACE:
			this.rounded = false;
			break;
		case SINGLE_FACE:
			//TODO ignore this for now. This is for single faced polygons, where the back of the polygon must be lighted appropriately as well as the front.
			//polygonAtt.setBackFaceNormalFlip(true);
			//polygonAtt.setCullFace(PolygonAttributes.CULL_NONE);
			this.rounded = false;
			break;
		case OBLONG:
		case REGULAR_POLYGON:
								// XXX - ROUNDED_TUBE?
			sect.averageNormals();
			this.rounded = true;
			break;
		case POINT:
			//TODO the ribbon = false value in various functions of SSGeometry should be used instead of this for now.
			stripVertexCounts = new int[1];
			stripVertexCounts[0] = pointCount;
			break;
		default:
			break;
		}

		final AffineCrossSection[] cs = new AffineCrossSection[pointCount];
		if (scale != null) {
			for (int i = 0; i < pointCount; i++) {
				cs[i] = new AffineCrossSection(sect, triheds[i]);
				cs[i].scale(scale[i]);
			}
		} else {
			for (int i = 0; i < pointCount; i++) {
				cs[i] = new AffineCrossSection(sect, triheds[i]);
			}
		}

		// record the vertex ranges
		final Fragment f = (Fragment)this.userData;
		final Chain c = f.getChain();
		final int resCount = f.getResidueCount();
		this.ranges = new Object[resCount];
		
		if(c.getResidueCount() - 1 == f.getEndResidueIndex()) {
		}
		
		if(f.getStartResidueIndex() == 0) {
		}
		
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
					endIndex = vertexSize - 1;
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
			final Extrusion figure = new Extrusion(cs.length, start, stripVertexCounts, cs,
					colorMap, start, this.rounded);
			figure.draw(arrayLists, gl, glu, glut, this.ranges);
		} else {
			arrayLists.disableLighting();
			final BackboneLine figure = new BackboneLine(pointCount, stripVertexCounts,
					coordinates, colorMap);
			figure.draw(arrayLists, gl, glu, glut, this.ranges);
		}
		
		return arrayLists;
	}

	// Calculates the normals along a path.
	// The path and tans vectors should be non null.
	//
	private void setNormals(final Vector3f[] norms, final Vector3f[] tans, final int l, final int r) {
		final Vector3f tmp = new Vector3f();
		if ((r - l) <= 1) {
			return;
		}

		final int k = (r + l) / 2;;
		norms[k].add(norms[l], norms[r]);
		tmp.set(tans[k]);
		tmp.scale(norms[k].dot(tans[k]));
		norms[k].sub(tmp);
		norms[k].normalize();

		this.setNormals(norms, tans, l, k);
		this.setNormals(norms, tans, k, r);

		return;
	}

	// Calculates the path of the cylinder in that representation as well as the
	// tangents along the path.
	// The path and tans vectors should be non null.
	//
	private void getCylinderPath(final Vec3f[] coords, final Vector3f[] path,
			final Vector3f[] tans, final Vector3f[] norms, final int steps) {
		final Vector3f v1 = new Vector3f();
		final Vector3f v2 = new Vector3f();
		final Vector3f v = new Vector3f();
		final Vector3f dv1 = new Vector3f();
		final Vector3f tmpv = new Vector3f();

		final int len = coords.length - 1;

		for (int i = 0; i <= len; i++) {
			path[i] = new Vector3f(coords[i].value[0], coords[i].value[1],
					coords[i].value[2]);
		}

		// Set the end points
		if ((len >= 4)) {
			v2.set(path[4]);
			tmpv.set(path[2]);
			v2.sub(tmpv);
			v1.set(path[2]);
			tmpv.set(path[0]);
			v1.sub(tmpv);
			dv1.sub(v1, v2);
			dv1.scale(0.25f);
			path[0].add(tmpv, dv1);

			v2.set(path[len - 4]);
			tmpv.set(path[len - 2]);
			v2.sub(tmpv);
			v1.set(path[len - 2]);
			tmpv.set(path[len]);
			v1.sub(tmpv);
			dv1.sub(v1, v2);
			dv1.scale(0.25f);
			path[len].add(tmpv, dv1);
		} else if ((len == 3)) {
			tmpv.set(path[1]);
			v1.add(path[2], path[0]);
			v1.scale(0.5f);
			path[1].set(v1);
			v1.add(tmpv, path[3]);
			v1.scale(0.5f);
			path[2].set(v1);

			dv1.sub(path[2], path[1]);
			dv1.normalize();
			v1.sub(path[0], path[1]);
			dv1.scale(v1.dot(dv1));
			path[0].add(path[1], dv1);
			v1.sub(path[3], path[2]);
			dv1.normalize();
			dv1.scale(v1.dot(dv1));

			path[3].add(path[2], dv1);
		} else {
			System.err.println("Segment too short to handle ");
		}

		Priestle.smooth(path, steps);;

		// Calculate tangents
		//
		for (int i = 1; i < len; i++) {
			tans[i] = new Vector3f();
			tans[i].sub(path[i + 1], path[i - 1]);
			tans[i].normalize();
		}
		tans[0] = new Vector3f();
		tans[0].sub(path[1], path[0]);
		tans[0].normalize();
		tans[len] = new Vector3f();
		tans[len].sub(path[len], path[len - 1]);
		tans[len].normalize();

		// Calculate normals
		//

		for (int i = 1; i < len; i++) {
			tmpv
					.set(coords[i].value[0], coords[i].value[1],
							coords[i].value[2]);
			norms[i] = new Vector3f();
			norms[i].sub(tmpv, path[i]);
			v.set(tans[i]);
			v.scale(norms[i].dot(tans[i]));
			norms[i].sub(v);
			norms[i].normalize();
		}

		tmpv.set(coords[0].value[0], coords[0].value[1], coords[0].value[2]);
		norms[0] = new Vector3f();
		norms[0].sub(tmpv, path[0]);
		norms[0].normalize();
		v.set(norms[0]);
		v.scale(tans[0].dot(norms[0]));
		tans[0].sub(v);
		tans[0].normalize();

		tmpv.set(coords[len].value[0], coords[len].value[1],
				coords[len].value[2]);
		norms[len] = new Vector3f();
		norms[len].sub(tmpv, path[len]);
		norms[len].normalize();
		v.set(norms[len]);
		v.scale(tans[len].dot(norms[len]));
		tans[len].sub(v);
		tans[len].normalize();

		for (int i = 1; i < len; i++) {
			norms[i] = new Vector3f();
		}
		this.setNormals(norms, tans, 0, len);

	}

	// Calculate the tangent vectors at the ends of the interval of
	// interpolation
	//
	private void getTangentsAndBinormals(final int i, final Vec3f[] coords, final Vec3f vec1,
			final Vec3f vec2, final Vector3f bin1, final Vector3f bin2) {
		// based on the approach in Molscript.
		// NOTE: It is important to observe that this implementation requires
		// the method to be called with
		// NOTE: rigurously consecutive integer i.
		//
		final Vector3f vvec1 = new Vector3f();
		final Vector3f vvec2 = new Vector3f();
		final Vector3f v1 = new Vector3f();
		final Vector3f v2 = new Vector3f();
		final Vector3f v = new Vector3f();
		final Vector3f dv1 = new Vector3f();
		final Vector3f dv2 = new Vector3f();

		Vector3f cvec = new Vector3f();
		Vector3f rvec = new Vector3f();

		final Vector3f ncvec = new Vector3f();
		final Vector3f nrvec = new Vector3f();
		Vector3f tmp = null;

		if (i == 0) {
			final int j = i + 1;
			v1.set(coords[j + 1].value[0], coords[j + 1].value[1],
					coords[j + 1].value[2]);
			v2.set(coords[j - 1].value[0], coords[j - 1].value[1],
					coords[j - 1].value[2]);
			v.set(coords[j].value[0], coords[j].value[1], coords[j].value[2]);

			cvec.sub(v1, v2);
			cvec.normalize();

			dv1.set(v1);
			dv1.sub(v);
			dv2.set(v);
			dv2.sub(v2);
			rvec.cross(dv2, dv1);
			rvec.normalize();

			ncvec.set(cvec);
			nrvec.set(rvec);

			ncvec.scale((float)Math.cos(HELIX_BETA));
			nrvec.scale((float)Math.sin(HELIX_BETA));

			vvec2.add(ncvec, nrvec);

			if (this.previousCaCoord == null) {
				vvec1.sub(v, v2);
				vvec1.normalize();
			} else {
				final Vector3f vM1 = new Vector3f(this.previousCaCoord.value[0],
						this.previousCaCoord.value[1], this.previousCaCoord.value[2]);
				vvec1.sub(v, vM1);

				// In this case the binormal should be consistent with the one
				// of the previous structure too.
				//
				v.sub(v2);
				v.normalize();
				v2.sub(vM1);
				v2.normalize();
				v.sub(v2);

				vvec1.normalize();
				tmp = new Vector3f(vvec1);
				tmp.scale(v.dot(tmp));
				v.sub(tmp); // This should be perpendicular to vvec1
				v.normalize(); // And this is a unit normal
				bin1.cross(vvec1, v);
			}

			vec1.value[0] = vvec1.x;
			vec1.value[1] = vvec1.y;
			vec1.value[2] = vvec1.z;
			vec2.value[0] = vvec2.x;
			vec2.value[1] = vvec2.y;
			vec2.value[2] = vvec2.z;

			cvec.scale((float)Math.sin(HELIX_ALPHA));
			rvec.scale((float)Math.cos(HELIX_ALPHA));
			bin2.add(cvec, rvec);
			if (this.previousCaCoord == null) {
				bin1.set(bin2);
				vvec1.scale(bin1.dot(vvec1));
				bin1.sub(vvec1);
			}
			return;
		} else if (i == coords.length - 2) {
			vec1.value[0] = vec2.value[0];
			vec1.value[1] = vec2.value[1];
			vec1.value[2] = vec2.value[2];
			bin1.set(bin2);

			v.set(coords[i].value[0], coords[i].value[1], coords[i].value[2]);
			if (this.nextCaCoord == null) {
				v1.set(coords[i + 1].value[0], coords[i + 1].value[1],
						coords[i + 1].value[2]);
				vvec2.sub(v1, v);
				vvec2.normalize();
				tmp = new Vector3f(vvec2);
				tmp.scale(tmp.dot(bin2));
				bin2.sub(tmp);
			} else {
				final Vector3f vP1 = new Vector3f(this.nextCaCoord.value[0],
						this.nextCaCoord.value[1], this.nextCaCoord.value[2]);
				vvec2.sub(vP1, v);
				vvec2.normalize();
				v1.set(coords[i + 1].value[0], coords[i + 1].value[1],
						coords[i + 1].value[2]);
				vP1.sub(v1);
				vP1.normalize();
				v1.sub(v);
				v1.normalize();
				vP1.sub(v1);
				tmp = new Vector3f(vvec2);
				tmp.scale(vP1.dot(tmp));
				vP1.sub(tmp);
				vP1.normalize();
				bin2.cross(vvec2, vP1);
			}
			vec2.value[0] = vvec2.x;
			vec2.value[1] = vvec2.y;
			vec2.value[2] = vvec2.z;

			return;
		} else {
			vec1.value[0] = vec2.value[0];
			vec1.value[1] = vec2.value[1];
			vec1.value[2] = vec2.value[2];
			bin1.set(bin2);

			v1.set(coords[i + 2].value[0], coords[i + 2].value[1],
					coords[i + 2].value[2]);
			v2.set(coords[i].value[0], coords[i].value[1], coords[i].value[2]);
			v.set(coords[i + 1].value[0], coords[i + 1].value[1],
					coords[i + 1].value[2]);

			cvec = new Vector3f();
			cvec.sub(v1, v2);
			cvec.normalize();

			dv1.set(v1);
			dv1.sub(v);
			dv2.set(v);
			dv2.sub(v2);
			rvec = new Vector3f();
			rvec.cross(dv2, dv1);
			rvec.normalize();

			ncvec.set(cvec);
			nrvec.set(rvec);

			ncvec.scale((float)Math.cos(HELIX_BETA));
			nrvec.scale((float)Math.sin(HELIX_BETA));

			vvec2.add(ncvec, nrvec);

			vec2.value[0] = vvec2.x;
			vec2.value[1] = vvec2.y;
			vec2.value[2] = vvec2.z;

			cvec.scale((float)Math.sin(HELIX_ALPHA));
			rvec.scale((float)Math.cos(HELIX_ALPHA));
			bin2.add(cvec, rvec);

			return;
		}
	}

	/**
	 * Return an array of linear scale vectors
	 */
	private Vector3f[] getUniformScale(final int length) {
		final Vector3f[] scale = new Vector3f[length];
		final Vector3f one = new Vector3f(1.0f, 1.0f, 1.0f);

		for (int i = 0; i < length; i++) {
			scale[i] = one;
		}

		return scale;
	}

	/**
	 * Return an array of trigonometrical scale vectors
	 */
	private Vector3f[] getTrigScale(final int length, final int segments, final float min,
			final float max) {
		final Vector3f[] scale = new Vector3f[length];
		final Vector3f one = new Vector3f(1.0f, 1.0f, 1.0f);
		final Vector3f maxSizeScale = new Vector3f(1.0f, 1.0f, max / min);
		float t;
		float s;
		final float d = (max - min) / min;
		scale[length - 1] = scale[0] = one;
		for (int i = 1; i < segments; i++) {
			t = i * (1.0f / segments);
			s = 1 + (float)Math.sin(Math.PI * t / 2) * d;
			scale[length - 1 - i] = scale[i] = new Vector3f(1.0f, 1.0f, s);
		}

		for (int i = segments; i < length - segments; i++) {
			scale[i] = maxSizeScale;
		}

		return scale;
	}

	/**
	 * Sets the normals along the spine.
	 */
	public void setNormals(final Vector3f[] normals) {
	}

	/**
	 * Sets the general appearance of the helix shape, i.e. cylinder of ribbon.
	 */
	public void setSsShape(final ConformationShape ssshape) {
		this.ssShape = ssshape;
	}

	/**
	 * Returns the general appearance of the helix shape, i.e. cylinder of
	 * ribbon.
	 */
	public ConformationShape getSsShape(final int ssshape) {
		return this.ssShape;
	}

	/**
	 * Sets the tangents along the spine.
	 */
	public void setTangents(final Vector3f[] tangents) {
	}

	/**
	 * Sets the rendering mode for the polygonal mesh.
	 * 
	 */

	private void processQualityInfo(final float quality) {
		final int maxSegments = 10;
		final int minSegments = 2;
		final int maxFacets = 24;
		final int minFacets = 4;

		if (quality < ((float) minSegments) / ((float) maxSegments)) {
			this.segments = minSegments;
			this.segments += this.segments % 2;
		} else {
			this.segments = (int) (quality * maxSegments);
			this.segments += this.segments % 2; // Keep it even for correct highlighting
		}

		if ((this.csType == CrossSectionType.REGULAR_POLYGON)
				|| (this.csType == CrossSectionType.ROUNDED_TUBE)) {
			if (quality < ((float) minFacets) / ((float) maxFacets)) {
				this.facets = minFacets;
			} else {
				this.facets = (int) (quality * maxFacets);
			}
		}
	}

}
