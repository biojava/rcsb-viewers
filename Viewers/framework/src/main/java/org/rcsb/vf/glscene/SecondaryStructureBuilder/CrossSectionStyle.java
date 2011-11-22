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

import org.rcsb.vf.glscene.jogl.Color3f;

//***********************************************************************************
// Imports
//***********************************************************************************
//

/**
 * CrossSectionStyle class encapsulates descriptors of the cross section of the
 * ribon used for building SS.
 * <P>
 * 
 * @author Apostol Gramada
 */
public class CrossSectionStyle
{
	/*
	 * Style type
	 */
	private CrossSectionType styleType;

	/**
	 * Flag the polygon whether closed or not
	 */
	private boolean closedPolygon = true;

	/**
	 * Number of vertices in the polygon. Same as number of facets if a closed
	 * polygon
	 */
	private int vertexCount = 0;

	/**
	 * Diameters of the (closed) polygon. In general, we can have several
	 * distances defining the shape. Their meaning has to be interpreted case by
	 * case.
	 */
	private float[] diameters;

	/**
	 * Colors to be assigned to vertices. In general, we can have each vertex of
	 * the polygon colored in its own color.
	 */
	private Color3f[] vertexColors;

	public enum CrossSectionType{
	/**
	 * Standard - Styles: Rectangular CrossSection
	 */
	RECTANGULAR_RIBBON,

	/**
	 * Standard - Styles: Plane Single Face
	 */
	DOUBLE_FACE,

	/**
	 * Standard - Styles: Plane Double Face
	 */
	SINGLE_FACE,

	/**
	 * Standard - Styles: "Elliptical" (not elliptical yet) CrossSection
	 */
	OBLONG,

	/**
	 * Standard - Styles: POLYGONAL CrossSection
	 */
	REGULAR_POLYGON,

	/**
	 * Standard - Styles: POLYGONAL ROUNDED CrossSection
	 */
	ROUNDED_TUBE,

	/**
	 * Standard - Styles: Polyline
	 */
	POINT,
	
	/**
	 * Satisfies uninitialized complaints
	 */
	}

	/**
	 * Creates a CrossSectionStyle with nothing set initially
	 */
	public CrossSectionStyle() {
	}

	/**
	 * Creates a minimal-defined CrossSectionStyle from given polygon type
	 * (closed of open), number of vertices and diameter
	 */
	public CrossSectionStyle(final boolean closedFlag, final int vertices, final float[] diams) {
		this.closedPolygon = closedFlag;
		this.vertexCount = vertices;
		this.diameters = diams;
	}

	/**
	 * Creates a CrossSectionStyle from a predefined list and with a given
	 * diameter set.
	 */
	public CrossSectionStyle(final CrossSectionType type, final float[] diams) {
		this.styleType = type;

		switch (type) {
		case RECTANGULAR_RIBBON:
			this.closedPolygon = true;
			this.vertexCount = 5;
			break;
		case DOUBLE_FACE:
			this.closedPolygon = true;
			this.vertexCount = 3;
			break;
		case SINGLE_FACE:
			this.closedPolygon = false;
			this.vertexCount = 2;
			break;
		case OBLONG:
			this.closedPolygon = true;
			this.vertexCount = 15;
			break;
		case REGULAR_POLYGON:
		case ROUNDED_TUBE:
			this.closedPolygon = true;
			this.vertexCount = 8;
			break;
		case POINT:
			this.closedPolygon = false;
			break;
		default:
			break;
		}

		this.diameters = diams;
	}

	/**
	 * Set the vertex colors
	 */
	public void setVertexColors(final Color3f[] colors) {
		this.vertexColors = colors;
	}

	/**
	 * Set the closed/open Polygon flag
	 */
	public void setClosedFlag(final boolean closed) {
		this.closedPolygon = closed;
	}

	/**
	 * Set the number of vertices
	 */
	public void setVertexCount(final int vertices) {
		this.vertexCount = vertices;
	}

	/**
	 * Set the number of vertices
	 */
	public void setDiameters(final float[] diams) {
		this.diameters = diams;
	}

	/**
	 * Get the Style Name
	 */
	public String getStyleName() {
		return this.styleType.toString();
	}

	/**
	 * Get the Style Type
	 */
	public CrossSectionType getStyleType() {
		return this.styleType;
	}

	/**
	 * Set the vertex colors
	 */
	public Color3f[] getVertexColors() {
		return this.vertexColors;
	}

	/**
	 * Get the closed/open Polygon flag
	 */
	public boolean getClosedFlag() {
		return this.closedPolygon;
	}

	/**
	 * Get the number of vertices
	 */
	public int getVertexCount() {
		return this.vertexCount;
	}

	/**
	 * Get the number of vertices
	 */
	public float[] getDiameters() {
		return this.diameters;
	}

}
