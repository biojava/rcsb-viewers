//  $Id: CrossSectionStyle.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
//
//  Copyright 2000-2004 The Regents of the University of California.
//  All Rights Reserved.
//
//  Permission to use, copy, modify and distribute any part of this
//  Molecular Biology Toolkit (MBT)
//  for educational, research and non-profit purposes, without fee, and without
//  a written agreement is hereby granted, provided that the above copyright
//  notice, this paragraph and the following three paragraphs appear in all
//  copies.
//
//  Those desiring to incorporate this MBT into commercial products
//  or use for commercial purposes should contact the Technology Transfer &
//  Intellectual Property Services, University of California, San Diego, 9500
//  Gilman Drive, Mail Code 0910, La Jolla, CA 92093-0910, Ph: (858) 534-5815,
//  FAX: (858) 534-7345, E-MAIL:invent@ucsd.edu.
//
//  IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR
//  DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING
//  LOST PROFITS, ARISING OUT OF THE USE OF THIS MBT, EVEN IF THE
//  UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//
//  THE MBT PROVIDED HEREIN IS ON AN "AS IS" BASIS, AND THE
//  UNIVERSITY OF CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT,
//  UPDATES, ENHANCEMENTS, OR MODIFICATIONS. THE UNIVERSITY OF CALIFORNIA MAKES
//  NO REPRESENTATIONS AND EXTENDS NO WARRANTIES OF ANY KIND, EITHER IMPLIED OR
//  EXPRESS, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
//  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE, OR THAT THE USE OF THE
//  MBT WILL NOT INFRINGE ANY PATENT, TRADEMARK OR OTHER RIGHTS.
//
//  For further information, please see:  http://mbt.sdsc.edu
//
//  History:
//  $Log: CrossSectionStyle.java,v $
//  Revision 1.1  2007/02/08 02:38:52  jbeaver
//  version 1.50
//
//  Revision 1.1  2006/09/20 16:50:43  jbeaver
//  first commit - branched from ProteinWorkshop
//
//  Revision 1.1  2006/08/24 17:39:03  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2006/03/09 00:18:55  jbeaver
//  Initial commit
//
//  Revision 1.5  2004/05/24 21:38:40  agramada
//  Corrected a spelling error in a style name. Added a getStyleName method.
//
//  Revision 1.4  2004/04/09 00:04:28  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.3  2004/01/29 18:05:22  agramada
//  Removed General Atomics from copyright
//
//  Revision 1.2  2003/07/14 18:42:03  agramada
//  Change the LINE style into POINT style.
//
//  Revision 1.1  2003/06/24 22:26:57  agramada
//  Reorganized geometry package. Old classes removed, new classes added.
//
//

//***********************************************************************************
// Package
//***********************************************************************************
//
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
