//  $Id: SsGeometry.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
//
//  Copyright (c) 2000-2002  San Diego Supercomputer Center (SDSC),
//  a facility operated jointly by the University of California,
//  San Diego (UCSD) and General Atomics, San Diego, California, USA.
//
//  Users and possessors of this source code are hereby granted a
//  nonexclusive, royalty-free copyright and design patent license to
//  use this code in individual software.  License is not granted for
//  commercial resale, in whole or in part, without prior written
//  permission from SDSC.  This source is provided "AS IS" without express
//  or implied warranty of any kind.
//
//  For further information, please see:  http://mbt.sdsc.edu
//
//  History:
//  $Log: SsGeometry.java,v $
//  Revision 1.1  2007/02/08 02:38:52  jbeaver
//  version 1.50
//
//  Revision 1.1  2006/09/20 16:50:43  jbeaver
//  first commit - branched from ProteinWorkshop
//
//  Revision 1.1  2006/08/24 17:39:03  jbeaver
//  *** empty log message ***
//
//  Revision 1.5  2006/07/18 21:06:38  jbeaver
//  *** empty log message ***
//
//  Revision 1.4  2006/05/16 17:57:02  jbeaver
//  *** empty log message ***
//
//  Revision 1.3  2006/04/14 23:37:34  jbeaver
//  Update with some (very broken) surface rendering stuff
//
//  Revision 1.2  2006/03/27 19:11:56  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2006/03/09 00:18:55  jbeaver
//  Initial commit
//
//  Revision 1.20  2005/11/08 20:58:34  moreland
//  Switched style code to new StructureStyles API.
//
//  Revision 1.19  2005/09/21 17:36:03  agramada
//  Reenabled a piece of logic that deals with special sequences of ss elements.
//  For whatever reasons it was commented out in a previous version.
//
//  Revision 1.18  2005/06/28 23:52:43  agramada
//  Added code to handle special cases related to gaps in structural data.
//  Also, some preliminary enhancements in anticipation of the new cylinder
//  style for helices.
//
//  Revision 1.17  2005/05/24 22:39:30  agramada
//  Added code to fix the problem of long coils appearing in the data gap regions.
//
//  Revision 1.15  2005/01/21 23:07:41  agramada
//  Added a couple of variants for the drawSs method which allow a finer customization
//  of the ribbon styles. Also, experimental code for using the new style ribbons is
//  present in this version without being effectively useful at this time though.
//
//  Revision 1.14  2004/07/02 16:38:58  agramada
//  Included logic to detect and remove long segments that were drawn when
//  gaps in data were encountered. This solution only works at this point with
//  secondary structures generated via the DerivedInformation class since in this
//  case a "NONE" conformation type is associated with gaps and can be easily
//  detected. A solution for the case when secondary structures come from the
//  file needs to be added.
//
//  Revision 1.13  2004/05/24 21:26:10  agramada
//  Added calls to the setQuality method in building Helix and Strands in the
//  drawSs method. Appearantly they were missing.
//
//  Revision 1.10  2004/01/21 22:03:06  agramada
//  Removed a lot of code no longer used. Improved color updating methods.
//  Added code to throw exceptions when too short fragments are encountered.
//
//  Revision 1.7  2003/12/08 22:39:12  agramada
//  Extensive changes especially in the drawSs method to conform with how
//  fragments are defined in the StructureMap (i.e. no residue sharing).
//  Also, moved to a more uniform approach to color updating/highlighting.
//
//  Revision 1.6  2003/11/24 17:34:10  moreland
//  Commented out debug print statements.
//
//  Revision 1.5  2003/10/01 23:30:20  agramada
//  Updated the import statements to reflect new location of the DerivedInformation class.
//
//  Revision 1.4  2003/08/01 16:26:25  agramada
//  Added a quality parameter and its set and get methods in the GeometryEntity
//  class. Also, added stuff to handle thie new parameter in the PsGeometry and
//  SsGeometry classes.
//
//  Revision 1.3  2003/07/17 17:58:50  agramada
//  Fix a boundary array index issue in the highlightClosestResidue method.
//  Also, as a temporar solution, added the drawBackboneTrace method, which
//  draw all Ss elements with a line style.
//
//  Revision 1.2  2003/06/25 21:22:59  agramada
//  Added methods to set coordinates from an array of float[] components,
//
//  Revision 1.1  2003/06/24 22:19:46  agramada
//  Reorganized the geometry package. Old classes removed, new classes added.
//
//

//***********************************************************************************
// Package
//***********************************************************************************
//
package org.rcsb.mbt.glscene.StructureViewerImpl;

//***********************************************************************************
// Imports
//***********************************************************************************
//
import java.util.*;


import java.awt.Color;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.rcsb.mbt.glscene.StructureViewerImpl.CrossSectionStyle.CrossSectionType;
import org.rcsb.mbt.glscene.StructureViewerImpl.vec.Vec3f;
import org.rcsb.mbt.glscene.jogl.DisplayLists;
import org.rcsb.mbt.model.*;
import org.rcsb.mbt.model.StructureComponentRegistry.ComponentType;
import org.rcsb.mbt.model.attributes.*;
import org.rcsb.mbt.model.util.*;

import com.sun.opengl.util.GLUT;

/**
 * SsGeometry is the base class for the geometry of all Secondary Structure
 * objects.
 * <P>
 * 
 * @author Apostol Gramada
 */
public class SsGeometry extends GeometryEntity {
	public enum ConformationShape { RIBBON, CYLINDER }

	private int priestleSteps = 2;

	protected Vec3f[] coords = null;

	protected Vec3f previousCaCoord;

	protected Vec3f nextCaCoord;

	protected Color ssColor;

	protected static float coilWidth = 0.8f;

	protected float[][] pathColorMap = null;

	protected float[][] colorMap;

	protected boolean uniformColor;

	protected Object userData = null;

	protected CrossSectionType csType;

	protected CrossSectionStyle csStyle = null;

	protected boolean highlighted = false;

	protected int segments;

	protected SsGeometry previousSs = null;

	protected SsGeometry nextSs = null;

	protected boolean leftExtended = false;

	protected boolean rightExtended = false;

	protected boolean rounded = true;

	public SsGeometry(final StructureComponent sc) {
		super(sc);
	}
	
	/**
	 * Builds a scene of the secondary structures, the list of SS being the one
	 * produced by the {@link StructureMap} argument based on what is
	 * explicitely available in the data source file or from the Kabsch-Sander
	 * algorithm.
	 * 
	 * **JB converted to work on a single chain...
	 */
	public static DisplayLists[] createSs(final Chain c,
			final StructureMap structureMap, final StructureStyles styles, final boolean ribbon, final GL gl, final GLU glu, final GLUT glut) throws FragmentTooShortException {
		// Quality factors
		final float helixQuality = 0.8f;
		final float coilQuality = 0.8f;
		final float turnQuality = 0.8f;
		final float strandQuality = 0.8f;

		final int helixSmoothingSteps = 0;
		final int strandSmoothingSteps = 2;
		final int turnSmoothingSteps = 2;
		final int coilSmoothingSteps = 2;

		final CrossSectionType helixCsType = CrossSectionType.REGULAR_POLYGON;
		// int helixCsType = CrossSectionType.RECTANGULAR_RIBBON;
		final CrossSectionType strandCsType = CrossSectionType.RECTANGULAR_RIBBON;
		final CrossSectionType turnCsType = CrossSectionType.ROUNDED_TUBE;
		final CrossSectionType coilCsType = CrossSectionType.ROUNDED_TUBE;

		return SsGeometry.createSs(c, structureMap, styles, ribbon,
				helixQuality, strandQuality, turnQuality, coilQuality,
				helixSmoothingSteps, strandSmoothingSteps, turnSmoothingSteps,
				coilSmoothingSteps, helixCsType, strandCsType, turnCsType,
				coilCsType, ConformationShape.RIBBON, gl, glu, glut);
	}

	/**
	 * Builds a scene of the secondary structures, the list of SS being the one
	 * produced by the {@link StructureMap} argument based on what is
	 * explicitely available in the data source file or from the Kabsch-Sander
	 * algorithm.
	 * 
	 * **JB converted to work on a single chain...
	 */
	public static DisplayLists[] createSs(final Chain c,
			final StructureMap structureMap, final StructureStyles styles, final boolean ribbon,
			final int helixSmoothingSteps, final int strandSmoothingSteps,
			final int turnSmoothingSteps, final int coilSmoothingSteps, final GL gl, final GLU glu, final GLUT glut)
			throws FragmentTooShortException {
		// Quality factors
		final float helixQuality = 0.8f;
		final float coilQuality = 0.8f;
		final float turnQuality = 0.8f;
		final float strandQuality = 0.8f;

		final CrossSectionType helixCsType = CrossSectionType.REGULAR_POLYGON;
		// int helixCsType = CrossSectionType.ROUNDED_TUBE;
		final CrossSectionType strandCsType = CrossSectionType.RECTANGULAR_RIBBON;
		final CrossSectionType turnCsType = CrossSectionType.ROUNDED_TUBE;
		final CrossSectionType coilCsType = CrossSectionType.ROUNDED_TUBE;

		return SsGeometry.createSs(c, structureMap, styles, ribbon,
				helixQuality, strandQuality, turnQuality, coilQuality,
				helixSmoothingSteps, strandSmoothingSteps, turnSmoothingSteps,
				coilSmoothingSteps, helixCsType, strandCsType, turnCsType,
				coilCsType, ConformationShape.RIBBON, gl, glu, glut);
	}

	/**
	 * Builds a scene of the secondary structures, the list of SS being the one
	 * produced by the {@link StructureMap} argument based on what is
	 * explicitely available in the data source file or from the Kabsch-Sander
	 * algorithm.
	 * 
	 * **JB converted to work on a single chain...
	 * 
	 */
	public static DisplayLists[] createSs(final Chain c,
			final StructureMap structureMap, final StructureStyles styles, final boolean ribbon,
			final int helixSmoothingSteps, final int strandSmoothingSteps,
			final int turnSmoothingSteps, final int coilSmoothingSteps,
			final ConformationShape helixSsShape, final GL gl, final GLU glu, final GLUT glut)
			throws FragmentTooShortException {
		// Quality factors
		final float helixQuality = 0.8f;
		final float coilQuality = 0.8f;
		final float turnQuality = 0.8f;
		final float strandQuality = 0.8f;

		final CrossSectionType helixCsType = CrossSectionType.REGULAR_POLYGON;
		// int helixCsType = CrossSectionType.ROUNDED_TUBE;
		final CrossSectionType strandCsType = CrossSectionType.RECTANGULAR_RIBBON;
		final CrossSectionType turnCsType = CrossSectionType.ROUNDED_TUBE;
		final CrossSectionType coilCsType = CrossSectionType.ROUNDED_TUBE;

		return SsGeometry.createSs(c, structureMap, styles, ribbon, 
				helixQuality, strandQuality, turnQuality, coilQuality,
				helixSmoothingSteps, strandSmoothingSteps, turnSmoothingSteps,
				coilSmoothingSteps, helixCsType, strandCsType, turnCsType,
				coilCsType, helixSsShape, gl, glu, glut);
	}

	/**
	 * Builds a scene of the secondary structures, the list of SS being the one
	 * produced by the {@link StructureMap} argument based on what is
	 * explicitely available in the data source file or from the Kabsch-Sander
	 * algorithm.
	 * 
	 * **JB converted to work on a single chain...
	 */
	public static DisplayLists[] createSs(final Chain c,
			final StructureMap structureMap, final StructureStyles styles, boolean ribbon, final float helixQuality, final float strandQuality,
			final float turnQuality, final float coilQuality, final int helixSmoothingSteps,
			final int strandSmoothingSteps, final int turnSmoothingSteps,
			final int coilSmoothingSteps, final CrossSectionType helixCsType,
			final CrossSectionType strandCsType, final CrossSectionType turnCsType,
			final CrossSectionType coilCsType, final ConformationShape helixSsShape,
			final GL gl, final GLU glu, final GLUT glut)
			throws FragmentTooShortException {
		final DisplayLists[] arrayLists = new DisplayLists[c.getFragmentCount()];
		
		int fragmentCount = 0;
		int residueCount = 0;
		int startIndex;
		int allResidueCount;
		int endResidueIndex, startResidueIndex;
		int trailingResidueIndex, leadingResidueIndex;
		Atom caAtom = new Atom();
		Atom leadingCaAtom = new Atom();
		Atom trailingCaAtom = new Atom();
		Residue residue = null;
		final Chain chainItem = c;
		Vec3f[] coords = null;
		Vec3f previousCoord = null;
		Vec3f nextCoord = null;
		boolean hasLeading;
		boolean hasTrailing;
		ComponentType conformationType, previousConformationType, nextConformationType;
		float[][] colorMap = null;

		SsGeometry lastSS = null;
		CoilGeometry cGeom = null;
		CoilGeometry tGeom = null;
		StrandGeometry sGeom = null;
		HelixGeometry hGeom = null;

		//helixSsShape = 0;

		final float[] diams = new float[2];
		diams[0] = 1.0f;
		diams[0] = SsGeometry.coilWidth;
		diams[1] = 0.2f;
		final CrossSectionStyle helixCsStyle = new CrossSectionStyle(
				CrossSectionType.REGULAR_POLYGON, diams);
		final CrossSectionStyle uniformCsStyle = new CrossSectionStyle(
				CrossSectionType.RECTANGULAR_RIBBON, diams);
		helixCsStyle.setVertexCount(10);

		final Vector geometries = new Vector();

		Fragment fragment = null;
		int headResId = 0;
		int tailResId = 0;
		boolean gapFollows = false;
		boolean gapPrecedes = false;
		boolean manageable = true;
		fragmentCount = chainItem.getFragmentCount();
		previousCoord = null;
		nextCoord = null;
		lastSS = null;
		String userInformation = null;
		
		for (int fIndex = 0; fIndex < fragmentCount; fIndex++) {
			manageable = true;
			fragment = chainItem.getFragment(fIndex);
			conformationType = fragment.getConformationType();
			// System.err.println( conformationType + " in chain " +
			// chainItem.getChainId() + " from " +
			// fragment.getStartResidueIndex() + " to " +
			// fragment.getEndResidueIndex() );
			if (fIndex > 0) {
				previousConformationType = chainItem
						.getFragmentType(fIndex - 1);
			} else {
				previousConformationType = ComponentType.UNDEFINED_CONFORMATION;
			}
			if (fIndex < fragmentCount - 1) {
				nextConformationType = chainItem.getFragmentType(fIndex + 1);
			} else {
				nextConformationType = ComponentType.UNDEFINED_CONFORMATION;
			}
			leadingResidueIndex = startResidueIndex = fragment
					.getStartResidueIndex();
			trailingResidueIndex = endResidueIndex = fragment
					.getEndResidueIndex();
			if ((fIndex > 0)
					&& (chainItem.getFragmentType(fIndex - 1) != ComponentType.UNDEFINED_CONFORMATION)) {
				leadingResidueIndex = chainItem
						.getFragmentEndResidue(fIndex - 1);
				headResId = chainItem.getResidue(leadingResidueIndex)
						.getResidueId();
			}
			if ((fIndex < fragmentCount - 1)
					&& (chainItem.getFragmentType(fIndex + 1) != ComponentType.UNDEFINED_CONFORMATION)) {
				trailingResidueIndex = chainItem
						.getFragmentStartResidue(fIndex + 1);
				tailResId = chainItem.getResidue(trailingResidueIndex)
						.getResidueId();
			}
			gapPrecedes = (headResId < chainItem.getResidue(startResidueIndex)
					.getResidueId() - 1)
					|| (previousConformationType == ComponentType.NONE);
			gapFollows = (tailResId > chainItem.getResidue(endResidueIndex)
					.getResidueId() + 1)
					|| (nextConformationType == ComponentType.NONE);

			residueCount = endResidueIndex - startResidueIndex + 1;
			if (conformationType == ComponentType.UNDEFINED_CONFORMATION) {
				userInformation = new String("UNDEFINED: chain -> "
						+ chainItem.getChainId() + "  fragment " + fIndex
						+ " Residues: " + startResidueIndex + " - "
						+ endResidueIndex);
				// System.out.println( userInformation );
				//
				// Skip UNDEFINED types
				//
			} else if (conformationType == ComponentType.NONE) {
				// System.err.println( "NONE " + conformationType );
			} else if (conformationType == ComponentType.COIL) {
				startIndex = 0;
				hasLeading = false;
				hasTrailing = false;
				cGeom = new org.rcsb.mbt.glscene.StructureViewerImpl.CoilGeometry(fragment);
				cGeom.setPriestleSteps(coilSmoothingSteps);
				cGeom.setQuality(coilQuality);
				cGeom.setUserData(fragment);
				if (!ribbon) {
					cGeom.setCrossSectionType(CrossSectionType.POINT);
				} else {
					cGeom.setCrossSectionType(coilCsType);
				}
				allResidueCount = residueCount;

				if ((leadingResidueIndex < startResidueIndex)
						&& ((previousConformationType != ComponentType.NONE) && (!gapPrecedes))
						&& (previousConformationType != ComponentType.COIL)) {
					residue = chainItem.getResidue(leadingResidueIndex);
					leadingCaAtom = residue.getAlphaAtom();
					hasLeading = true;
					allResidueCount++;
					startIndex = 1;
				}
				if ((trailingResidueIndex > endResidueIndex)
						&& (nextConformationType != ComponentType.NONE && (!gapFollows))
				// && (nextConformationType !=
				// ComponentType.COIL)
				) {
					residue = chainItem.getResidue(trailingResidueIndex);
					trailingCaAtom = residue.getAlphaAtom();
					hasTrailing = true;
					allResidueCount++;
				}

				// System.err.println(" Coil from " + leadingResidueIndex + " to
				// " + trailingResidueIndex + " with a start at " +
				// startResidueIndex + " and an end at " + endResidueIndex + "
				// allResidueCount " + allResidueCount);
				// System.err.println(" Gap State " + gapPrecedes + " and " +
				// gapFollows + " headResId " + headResId + " tailResId " +
				// tailResId );

				if (allResidueCount < 2) {
					manageable = false;
					Status.output(Status.LEVEL_WARNING, "Coil in chain "
							+ chainItem.getChainId() + "  from "
							+ startResidueIndex + "  to  " + endResidueIndex
							+ "  too short, can not handle ");
					// throw new FragmentTooShortException( conformationType + "
					// " + startResidueIndex + " " + endResidueIndex + " too
					// short, can not handle" );
				}

				coords = new Vec3f[allResidueCount];
				colorMap = new float[allResidueCount][3];
				if (hasLeading) {
					coords[0] = new Vec3f((float) leadingCaAtom.coordinate[0],
							(float) leadingCaAtom.coordinate[1],
							(float) leadingCaAtom.coordinate[2]);
					residue = chainItem.getResidue(leadingResidueIndex);
					final ChainStyle chainStyle = (ChainStyle) styles
							.getStyle(chainItem);
					chainStyle.getResidueColor(residue, colorMap[0]);
					cGeom.setLeftExtended(true);
				}
				if (hasTrailing) {
					coords[allResidueCount - 1] = new Vec3f(
							(float) trailingCaAtom.coordinate[0],
							(float) trailingCaAtom.coordinate[1],
							(float) trailingCaAtom.coordinate[2]);
					residue = chainItem.getResidue(trailingResidueIndex);
					final ChainStyle chainStyle = (ChainStyle) styles
							.getStyle(chainItem);
					chainStyle.getResidueColor(residue,
							colorMap[allResidueCount - 1]);
					cGeom.setRightExtended(true);
				}

				for (int i = 0; i < residueCount; i++) {
					residue = chainItem.getResidue(startResidueIndex + i);
					caAtom = residue.getAlphaAtom();
					if(caAtom == null) {
						caAtom = residue.getAtom(0);
					}
					coords[i + startIndex] = new Vec3f(
							(float) caAtom.coordinate[0],
							(float) caAtom.coordinate[1],
							(float) caAtom.coordinate[2]);
					final ChainStyle chainStyle = (ChainStyle) styles
							.getStyle(chainItem);
					chainStyle.getResidueColor(residue, colorMap[i
							+ startIndex]);
				}

				for (int i = 0; i < coords.length; i++) {
					// System.err.println(" i = " + i + " Color " +
					// colorMap[i][0] + " " + colorMap[i][1] + " " +
					// colorMap[i][2] );
				}

				cGeom.setCoordinates(coords);
				cGeom.setColorMap(colorMap);

				if ((lastSS != null) && (!gapPrecedes)) {
					nextCoord = cGeom.getNextToFirstCa();
					if(nextCoord == null) {
						continue;
					}
					lastSS.setNextCaCoord(nextCoord);
					cGeom.setPreviousSs(lastSS);
					lastSS.setNextSs(cGeom);
				}
				if (previousCoord != null && (!gapPrecedes)) {
					cGeom.setPreviousCaCoord(previousCoord);
				}
				if (manageable) {
					previousCoord = cGeom.getPreviousToLastCa();
					lastSS = cGeom;
					geometries.add(cGeom);
				}
			} else if ((conformationType == ComponentType.TURN)
					| ((conformationType == ComponentType.HELIX) & (residueCount < 3))) {
				startIndex = 0;
				hasLeading = false;
				hasTrailing = false;
				tGeom = new org.rcsb.mbt.glscene.StructureViewerImpl.CoilGeometry(fragment);
				tGeom.setPriestleSteps(turnSmoothingSteps);
				tGeom.setQuality(turnQuality);
				tGeom.setUserData(fragment);
				if (!ribbon) {
					tGeom.setCrossSectionType(CrossSectionType.POINT);
				} else {
					tGeom.setCrossSectionType(turnCsType);
				}

				allResidueCount = residueCount;

				/*
				 * if( conformationType == ComponentType.TURN) {
				 * userInformation = new String( "Turn: chain -> " + chain + "
				 * fragment " + fIndex + " Residues: " + startResidueIndex + " - " +
				 * endResidueIndex ); } else { userInformation = new String(
				 * "Warning: Short (2 Res) Helix reinterpreted as turn: chain -> " +
				 * chain + " fragment " + fIndex + " Residues: " +
				 * startResidueIndex + " - " + endResidueIndex ); }
				 */

				// Extend a turn to include the segments connecting them to the
				// previous and next SS structures ONLY if the previous and/or
				// next SS are not a Coil fragment.
				//
				if (leadingResidueIndex < startResidueIndex && (!gapPrecedes)) {
					if ((previousConformationType != ComponentType.COIL)
							& (previousConformationType != ComponentType.TURN)) {
						residue = chainItem.getResidue(leadingResidueIndex);
						leadingCaAtom = residue.getAlphaAtom();
						hasLeading = true;
						allResidueCount++;
						startIndex = 1;
					}
				}
				if (trailingResidueIndex > endResidueIndex && (!gapFollows)) {
					if (chainItem.getFragmentType(fIndex + 1) != ComponentType.COIL) {
						residue = chainItem.getResidue(trailingResidueIndex);
						trailingCaAtom = residue.getAlphaAtom();
						hasTrailing = true;
						allResidueCount++;
					}
				}

				// System.err.println( " length " + allResidueCount );
				coords = new Vec3f[allResidueCount];
				colorMap = new float[allResidueCount][3];
				if (hasLeading) {
					coords[0] = new Vec3f((float) leadingCaAtom.coordinate[0],
							(float) leadingCaAtom.coordinate[1],
							(float) leadingCaAtom.coordinate[2]);
					residue = chainItem.getResidue(leadingResidueIndex);
					final ChainStyle chainStyle = (ChainStyle) styles
							.getStyle(chainItem);
					chainStyle.getResidueColor(residue, colorMap[0]);
					tGeom.setLeftExtended(true);
				}
				if (hasTrailing) {
					coords[allResidueCount - 1] = new Vec3f(
							(float) trailingCaAtom.coordinate[0],
							(float) trailingCaAtom.coordinate[1],
							(float) trailingCaAtom.coordinate[2]);
					residue = chainItem.getResidue(trailingResidueIndex);
					final ChainStyle chainStyle = (ChainStyle) styles
							.getStyle(chainItem);
					chainStyle.getResidueColor(residue,
							colorMap[allResidueCount - 1]);
					tGeom.setRightExtended(true);
				}

				for (int i = 0; i < residueCount; i++) {
					residue = chainItem.getResidue(startResidueIndex + i);
					final ChainStyle chainStyle = (ChainStyle) styles
							.getStyle(chainItem);
					caAtom = residue.getAlphaAtom();
					if(caAtom == null) {	// if no ca atom, use a random atom...
						caAtom = residue.getAtom(0);
					}
					coords[i + startIndex] = new Vec3f(
							(float) caAtom.coordinate[0],
							(float) caAtom.coordinate[1],
							(float) caAtom.coordinate[2]);
					chainStyle.getResidueColor(residue, colorMap[i
							+ startIndex]);
				}

				if (((conformationType == ComponentType.TURN) & (allResidueCount < 2))
						| ((conformationType == ComponentType.HELIX) & (allResidueCount < 3))) {
					manageable = false;
					Status.output(Status.LEVEL_WARNING, "Turn in chain "
							+ chainItem.getChainId() + "  from "
							+ startResidueIndex + "  to  " + endResidueIndex
							+ " too short, can not handle ");
					// throw new FragmentTooShortException( conformationType + "
					// too short, can not handle" );
				}

				tGeom.setColorMap(colorMap);
				tGeom.setCoordinates(coords);

				if (lastSS != null && (!gapPrecedes)) {
					nextCoord = tGeom.getNextToFirstCa();
					if(nextCoord == null) {
						continue;
					}
					lastSS.setNextCaCoord(nextCoord);
					tGeom.setPreviousSs(lastSS);
					lastSS.setNextSs(tGeom);
				}
				if (previousCoord != null && (!gapPrecedes)) {
					tGeom.setPreviousCaCoord(previousCoord);
				}
				if (manageable) {
					previousCoord = tGeom.getPreviousToLastCa();
					lastSS = tGeom;
					geometries.add(tGeom);
				}
			} else if (conformationType == ComponentType.STRAND) {
				//if(true)
				//continue;
				
				startIndex = 0;
				hasTrailing = false;
				manageable = true;
				sGeom = new org.rcsb.mbt.glscene.StructureViewerImpl.StrandGeometry(fragment);
				sGeom.setPriestleSteps(strandSmoothingSteps);
				sGeom.setQuality(strandQuality);
				sGeom.setUserData(fragment);
				// sGeom.setStyle(
				// org.rcsb.mbt.viewers.StructureViewerImpl.Strand.STYLE_SINGLE_FACE
				// );
				// sGeom.setCrossSectionStyle( uniformCsStyle );
				sGeom.setRoundedShape(false);
				// sGeom.setDrawArrow( false );
				if (!ribbon) {
					sGeom.setCrossSectionType(CrossSectionType.POINT);
				} else {
					sGeom.setCrossSectionType(strandCsType);
				}

				allResidueCount = residueCount;

				if ((nextConformationType == ComponentType.STRAND)
						|| (nextConformationType == ComponentType.HELIX)
						&& (!gapFollows)) {
					residue = chainItem.getResidue(trailingResidueIndex);
					trailingCaAtom = residue.getAlphaAtom();
					hasTrailing = true;
					allResidueCount++;
				}

				if (allResidueCount < 2) {
					manageable = false;
					Status.output(Status.LEVEL_WARNING, "Strand in chain "
							+ chainItem.getChainId() + "  from "
							+ startResidueIndex + "  to  " + endResidueIndex
							+ "  too short, can not handle ");
					// throw new FragmentTooShortException( conformationType + "
					// too short, can not handle" );
				}

				coords = new Vec3f[allResidueCount];
				colorMap = new float[allResidueCount][3];
				for (int i = 0; i < residueCount; i++) {
					residue = chainItem.getResidue(startResidueIndex + i);
					caAtom = residue.getAlphaAtom();
					coords[startIndex + i] = new Vec3f(
							(float) caAtom.coordinate[0],
							(float) caAtom.coordinate[1],
							(float) caAtom.coordinate[2]);
					final ChainStyle chainStyle = (ChainStyle) styles
							.getStyle(chainItem);
					chainStyle.getResidueColor(residue, colorMap[i]);
				}

				if (hasTrailing) {
					coords[allResidueCount - 1] = new Vec3f(
							(float) trailingCaAtom.coordinate[0],
							(float) trailingCaAtom.coordinate[1],
							(float) trailingCaAtom.coordinate[2]);
					residue = chainItem.getResidue(trailingResidueIndex);
					final ChainStyle chainStyle = (ChainStyle) styles
							.getStyle(chainItem);
					chainStyle.getResidueColor(residue,
							colorMap[allResidueCount - 1]);
					sGeom.setRightExtended(true);
				}

				sGeom.setColorMap(colorMap);
				sGeom.setCoordinates(coords);

				if (lastSS != null && (!gapPrecedes)) {
					try {
						nextCoord = sGeom.getNextToFirstCa();
						if(nextCoord == null) {
							continue;
						}
					} catch (final ArrayIndexOutOfBoundsException e) {
						// System.err.println( "Strand: Next to First can not be
						// handled " );
						manageable = false;
					}
					lastSS.setNextCaCoord(nextCoord);
					sGeom.setPreviousSs(lastSS);
					lastSS.setNextSs(sGeom);
				}
				if (previousCoord != null && (!gapPrecedes)) {
					sGeom.setPreviousCaCoord(previousCoord);
				}
				try {
					previousCoord = sGeom.getPreviousToLastCa();
				} catch (final ArrayIndexOutOfBoundsException e) {
					// System.err.println( "Strand: Previous to last can not be
					// handled " );
					manageable = false;
				}
				if (manageable) {
					lastSS = sGeom;
					geometries.add(sGeom);
				}
			} else if (conformationType == ComponentType.HELIX) {
				//if(true)
				//continue;
				
				hGeom = new org.rcsb.mbt.glscene.StructureViewerImpl.HelixGeometry(fragment);
				hGeom.setPriestleSteps(helixSmoothingSteps);
				startIndex = 0;
				hasTrailing = false;
				hGeom.setQuality(helixQuality);
				hGeom.setUserData(fragment);
				hGeom.setSsShape(helixSsShape);
				// hGeom.setCrossSectionStyle( uniformCsStyle );
				// hGeom.setCrossSectionStyle( helixCsStyle );
				if (!ribbon) {
					hGeom.setCrossSectionType(CrossSectionType.POINT);
				} else {
					hGeom.setCrossSectionType(helixCsType);
				}

				allResidueCount = residueCount;

				if ((nextConformationType == ComponentType.STRAND)
						|| (nextConformationType == ComponentType.HELIX)
						&& (!gapFollows)) {
					residue = chainItem.getResidue(trailingResidueIndex);
					trailingCaAtom = residue.getAlphaAtom();
					hasTrailing = true;
					allResidueCount++;
				}
				// System.err.println(" Helix from " + startResidueIndex + " to
				// " + trailingResidueIndex + " with a start at " +
				// startResidueIndex + " and an end at " + endResidueIndex + "
				// allResidueCount " + allResidueCount);

				if (allResidueCount < 3) {
					manageable = false;
					Status.output(Status.LEVEL_WARNING, "Helix in chain "
							+ chainItem.getChainId() + "  from "
							+ startResidueIndex + "  to  " + endResidueIndex
							+ "  too short, can not handle ");
					// throw new FragmentTooShortException( conformationType + "
					// too short, can not handle" );
				}

				coords = new Vec3f[allResidueCount];
				colorMap = new float[allResidueCount][3];
				for (int i = 0; i < residueCount; i++) {
					residue = chainItem.getResidue(startResidueIndex + i);
					// System.err.println( "Residue: " + i + " Residue index " +
					// startResidueIndex );
					caAtom = residue.getAlphaAtom();
					if (caAtom == null) {
						System.err.println("No Ca atom returned for residue "
								+ residue);
					}
					coords[i] = new Vec3f((float) caAtom.coordinate[0],
							(float) caAtom.coordinate[1],
							(float) caAtom.coordinate[2]);
					// styles.getResidueColor( chainItem.getResidue( startIndex
					// + i ), colorMap[i] );
					final ChainStyle chainStyle = (ChainStyle) styles
							.getStyle(chainItem);
					chainStyle.getResidueColor(residue, colorMap[i]);
				}

				if (hasTrailing) {
					coords[allResidueCount - 1] = new Vec3f(
							(float) trailingCaAtom.coordinate[0],
							(float) trailingCaAtom.coordinate[1],
							(float) trailingCaAtom.coordinate[2]);
					residue = chainItem.getResidue(trailingResidueIndex);
					final ChainStyle chainStyle = (ChainStyle) styles
							.getStyle(chainItem);
					chainStyle.getResidueColor(residue,
							colorMap[allResidueCount - 1]);
					hGeom.setRightExtended(true);
				}

				hGeom.setCoordinates(coords);
				hGeom.setColorMap(colorMap);

				if (lastSS != null && (!gapPrecedes)) {
					nextCoord = hGeom.getNextToFirstCa();
					if(nextCoord == null) {
						continue;
					}
					lastSS.setNextCaCoord(nextCoord);
					hGeom.setPreviousSs(lastSS);
					lastSS.setNextSs(hGeom);
				}
				if (previousCoord != null && (!gapPrecedes)) {
					hGeom.setPreviousCaCoord(previousCoord);
				}
				if (manageable) {
					previousCoord = hGeom.getPreviousToLastCa();
					lastSS = hGeom;
					geometries.add(hGeom);
				}
			} else {
				// Should we have anything else here ?
			}
		}

		final Iterator geomIterator = geometries.iterator();
		GeometryEntity entity = null;
		while (geomIterator.hasNext()) {
			entity = (GeometryEntity) geomIterator.next();
			
			// all these GeometryEntries have Fragments backing them.
			final Fragment f = (Fragment)entity.structureComponent;
			final Chain chain = f.getChain();
			final Iterator it = chain.getFragments().iterator();
			int fragmentIndex = -1;
			for(int i = 0; it.hasNext(); i++) {
				final Fragment f_ = (Fragment)it.next();
				if(f_ == f) {
					fragmentIndex = i;
					break;
				}
			}
			
			arrayLists[fragmentIndex] = entity.generateJoglGeometry(gl, glu, glut);
		}

		return arrayLists;
	}


	/**
	 * Sets the cross section type
	 */
	public void setCrossSectionType(final CrossSectionType type) {
		this.csType = type;
	}

	/**
	 * Sets the cross section style
	 */
	public void setCrossSectionStyle(final CrossSectionStyle csStyle) {
		this.csStyle = csStyle;
	}

	/**
	 * Whether the shape should be rounded or not
	 */
	public void setRoundedShape(final boolean round) {
		this.rounded = round;
	}

	/**
	 * Set number of segments to be drawn between any two consecutive CA
	 * positions.
	 */
	public void setSegmentCount(final int seg) {
		if (seg <= 0) {
			this.segments = 1;
		} else {
			this.segments = seg;
		}
	}

	/**
	 * Sets the coordinates along which to draw the spine. Deprecated, use
	 * setCoordinates with an array of float[] as an argument instead.
	 */
	public void setCoordinates(final Vec3f[] coords) {
		this.coords = coords;
		// Apply Priestle smoothing
		Priestle.smooth(coords, this.getPriestleSteps());
	}

	/**
	 * Sets the coordinates along which to draw the spine.
	 */
	public void setCoordinates(final float[][] coord) {
		this.coords = new Vec3f[coord.length];
		for (int i = 0; i < this.coords.length; i++) {
			this.coords[i] = new Vec3f(coord[i][0],
					coord[i][1], coord[i][2]);
		}
		// Apply Priestle smoothing
		Priestle.smooth(this.coords, this.getPriestleSteps());
		// Priestle.smooth( this.coords, 2 );
		// CaPreprocess.smooth( this.coords, 5 );
	}

	/**
	 * Sets the previous Secondary Structure geometry (in a chain for instance)
	 */
	public void setPreviousSs(final SsGeometry ss) {
		this.previousSs = ss;
	}

	/**
	 * Sets the next Secondary Structure geometry (in a chain for instance)
	 */
	public void setNextSs(final SsGeometry ss) {
		this.nextSs = ss;
	}

	/**
	 * Set a flag that the fragment has been extended to the left to fill a gap
	 */
	public void setLeftExtended(final boolean extend) {
		this.leftExtended = extend;
	}

	/**
	 * Set a flag that the fragment has been extended to the right to fill a gap
	 */
	public void setRightExtended(final boolean extend) {
		this.rightExtended = extend;
	}

	/**
	 * Set Previous Ca atom coordinate. Deprecated.
	 */
	public void setPreviousCaCoord(final Vec3f coord) {
		this.previousCaCoord = new Vec3f();
		this.previousCaCoord.value[0] = coord.value[0];
		this.previousCaCoord.value[1] = coord.value[1];
		this.previousCaCoord.value[2] = coord.value[2];
	}

	/**
	 * Set Previous Ca atom coordinate.
	 */
	public void setPreviousCaCoord(final float[] coord) {
		this.previousCaCoord = new Vec3f(coord[0], coord[1],
				coord[2]);
	}

	/**
	 * Set Next Ca atom coordinate Deprecated
	 */
	public void setNextCaCoord(final Vec3f coord) {
		this.nextCaCoord = new Vec3f();
		this.nextCaCoord.value[0] = coord.value[0];
		this.nextCaCoord.value[1] = coord.value[1];
		this.nextCaCoord.value[2] = coord.value[2];
	}

	/**
	 * Set Next Ca atom coordinate
	 */
	public void setNextCaCoord(final float[] coord) {
		this.nextCaCoord = new Vec3f(coord[0], coord[1],
				coord[2]);
	}

	/**
	 * Sets the color map from a given set of colors Deprecated
	 */
	public void setColorMap(final Color[] color) {
		this.colorMap = new float[color.length][3];
		for (int i = 0; i < color.length; i++) {
			color[i].getColorComponents(this.colorMap[i]);
		}
		this.uniformColor = false;
	}

	/**
	 * Sets information (annotation) provided by the user
	 */
	public void setUserData(final Object info) {
		this.userData = info;
	}

	/**
	 * Sets the color map from a given set of float arrays.
	 */
	public void setColorMap(final float[][] color) {
		if (this.colorMap == null) {
			this.colorMap = color;
			/*
			 * colorMap = new float[color.length][3]; for( int i = 0; i <
			 * color.length; i++ ) { this.colorMap[i] = color[i]; }
			 */
		} else {
			final int len = color.length;
			if (this.colorMap.length != color.length) {
				for (int i = 0; i < this.colorMap.length; i++) {
					this.colorMap[i] = color[i % len];
				}
			} else {
				this.colorMap = color;
			}
		}
		this.uniformColor = false;
	}

	/**
	 * Updates the color map from a given set of float arrays.
	 */
	public void updateColorMap(final float[][] color) {
		this.setColorMap(color);
		this.setPathColor(this.pathColorMap.length);
		this.resetGeometryColor();
	}

	/**
	 * Updates the color map from a given float array color, at a given index i.
	 */
	public void updateColorMap(final float[] color, final int i) {
		this.updateColorMap(color, i, true);
	}

	/**
	 * Updates the color map from a given float array color, at a given index i.
	 */
	public void updateColorMap(final float[] color, final int i, final boolean recurse) {
		this.colorMap[i] = color;
		this.setPathColor(this.pathColorMap.length, i);
		this.resetResidueColor(i);
		if (recurse) {
			if (i == this.colorMap.length - 1) {
				final SsGeometry nextSs = this.getNextSs();
				if (nextSs == null) {
					return;
				}
				if (nextSs.isLeftExtended()) {
					nextSs.updateColorMap(color, 0, false);
				}
			}
			if (i == 0) {
				final SsGeometry previousSs = this.getPreviousSs();
				if (previousSs == null) {
					return;
				}
				if (previousSs.isRightExtended()) {
					final int len = previousSs.getColorMap().length;
					previousSs.updateColorMap(color, len - 1, false);
				}
			}
		}
	}

	/**
	 * Sets the color for the extended path.
	 */
	protected void setPathColor(final int spinePointCount) {
		if (this.pathColorMap == null) {
			this.pathColorMap = new float[spinePointCount][3];
		}
		final int shift = this.segments / 2;

		int i = 0;
		for (int j = 0; j <= shift; j++) {
			this.pathColorMap[j] = this.colorMap[0];
		}

		i = this.colorMap.length - 1;
		for (int j = i * this.segments - shift + 1; j <= i * this.segments; j++) {
			this.pathColorMap[j] = this.colorMap[i];
		}

		for (i = 1; i < this.colorMap.length - 1; i++) {
			for (int j = i * this.segments - shift + 1; j <= i * this.segments + shift; j++) {
				this.pathColorMap[j] = this.colorMap[i];
			}
		}
	}

	/**
	 * Sets the color for the extended path, in the region definede by index.
	 */
	protected void setPathColor(final int spinePointCount, final int index) {
		if (this.pathColorMap == null) {
			this.pathColorMap = new float[spinePointCount][3];
		}

		final int shift = this.segments / 2;
		if (index == this.colorMap.length - 1) {
			for (int i = this.segments * index - shift + 1; i <= this.segments * index; i++) {
				this.pathColorMap[i] = this.colorMap[index];
			}
			return;
		}

		if ((index == 0)) {
			for (int i = 0; i <= shift; i++) {
				this.pathColorMap[i] = this.colorMap[index];
			}
			return;
		}

		for (int i = this.segments * index - shift + 1; i <= this.segments * index
				+ shift; i++) {
			this.pathColorMap[i] = this.colorMap[index];
		}
	}

	/**
	 * Sets the color for the extended path.
	 */
	protected void setPathColorDebug(final int spinePointCount) {
		if (this.pathColorMap == null) {
			this.pathColorMap = new float[spinePointCount][3];
		}
		final float[] nodeColor = new float[3];
		nodeColor[0] = 1.0f;
		nodeColor[1] = 0.0f;
		nodeColor[2] = 0.0f;
		final float[] intColor = new float[3];
		intColor[0] = 1.0f;
		intColor[1] = 1.0f;
		intColor[2] = 1.0f;
		int l = -1;
		for (int i = 0; i < this.coords.length - 1; i++) {
			l++;
			this.pathColorMap[l] = nodeColor;
			// pathColorMap[l] = {1.0f, 0.0f, 0.0f};
			for (int j = 1; j < this.segments; j++) {
				l++;
				// pathColorMap[l] = colorMap[i];
				this.pathColorMap[l] = intColor;
			}
		}
		l++;
		this.pathColorMap[l] = nodeColor;
		// pathColorMap[l] = {1.0f, 0.0f, 0.0f};
	}

	/**
	 * Resets the vertex color in the geometry shape of this SsGeometry object.
	 */
	public void resetGeometryColor() {
		for (int i = 0; i < this.coords.length - 2; i++) {
			this.resetResidueColor(i);
		}
	}

	/**
	 * Returns the index of the closest residue to the given point, with the
	 * given color.
	 */
	public int getClosestResidueIndex(final float[] point) {
		// Detect the closest residue (i.e. CA atom)
		//
		final Vec3f vec = new Vec3f(point[0], point[1],
				point[2]);
		float dist = 1.0E10f;
		float a;
		int index = 0;
		for (int i = 0; i < this.coords.length; i++) {
			if ((a = this.distance(vec, this.coords[i])) <= dist) {
				dist = a;
				index = i;
			}
		}

		// Now update the color of the portion of SS associated with that
		// residue index
		//
		return index;
	}

	/**
	 * Highlights the closest residue to the given point, with the given color.
	 */
	public void highlightClosestResidue(final float[] point, final float[] color) {
		// Detect the closest residue (i.e. CA atom)
		//
		final Vec3f vec = new Vec3f(point[0], point[1],
				point[2]);
		float dist = 1.0E10f;
		float a;
		int index = 0;
		for (int i = 0; i < this.coords.length; i++) {
			if ((a = this.distance(vec, this.coords[i])) <= dist) {
				dist = a;
				index = i;
			}
		}

		// Now update the color of the portion of SS associated with that
		// residue index
		//
		this.highlightResidueRegion(index, color);
	}

	/**
	 * Highlights the whole fragment with the given color.
	 */
	public void highlight(final float[] color) {
		for (int i = 0; i < this.coords.length; i++) {
			this.highlightResidue(i, color);
		}
	}

	/**
	 * Resets the whole fragment to the original color map.
	 */
	public void resetColor() {
		for (int i = 0; i < this.coords.length; i++) {
			this.resetResidueColor(i);
		}
	}

	/**
	 * Returns the color map from a given set of float arrays.
	 */
	public float[][] getColorMap() {
		return this.colorMap;
	}

	/**
	 * Returns the previous Secondary Structure geometry (in a chain for
	 * instance)
	 */
	public SsGeometry getPreviousSs() {
		return this.previousSs;
	}

	/**
	 * Returns the next Secondary Structure geometry (in a chain for instance)
	 */
	public SsGeometry getNextSs() {
		return this.nextSs;
	}

	/**
	 * 
	 */
	public boolean getHighlightState() {
		return this.highlighted;
	}

	/**
	 * Hightlights the portion of SS sorounding residue at index.
	 */
	public void highlightResidueRegion(final int index, final float[] color) {
		// Needs to be overridden in each SS subclass
	}

	/**
	 * Hightlights the portion of SS sorounding residue at index.
	 */
	public void highlightResidue(final int index, final float[] color) {
		// Needs to be overridden in each SS subclass
	}

	/**
	 * Resets the color of the portion of SS sorounding residue at index.
	 */
	public void resetResidueColor(final int index) {
		// Needs to be overridden in each SS subclass
	}

	/**
	 * Returns information (annotation) provided by the user
	 */
	public Object getUserData() {
		return this.userData;
	}

	/**
	 * Set a flag that the fragment has been extended to the left to fill a gap
	 */
	public boolean isLeftExtended() {
		return this.leftExtended;
	}

	/**
	 * Set a flag that the fragment has been extended to the right to fill a gap
	 */
	public boolean isRightExtended() {
		return this.rightExtended;
	}

	/**
	 * First CA coord (after smoothing) may be needed outside for imposing
	 * continuity Deprecated
	 */
	public Vec3f getNextToFirstCa() {
		if(this.coords.length <= 1) {
			return null;
		}
		return this.coords[1];
	}

	/**
	 * First CA coord (after smoothing) may be needed outside for imposing
	 * continuity
	 */
	public float[] getNextToFirstCaCoord() {
		final float[] toReturn = new float[3];
		toReturn[0] = this.coords[1].value[0];
		toReturn[1] = this.coords[1].value[1];
		toReturn[2] = this.coords[1].value[2];

		return toReturn;
	}

	/**
	 * Get Previous to last CA coord (after smoothing) may be needed outside for
	 * imposing continuity Deprecated
	 */
	public Vec3f getPreviousToLastCa() {
		return this.coords[this.coords.length - 2];
	}

	/**
	 * Previous to last CA coord (after smoothing) may be needed outside for
	 * imposing continuity
	 */
	public float[] getPreviousToLastCaCoord() {
		final float[] toReturn = new float[3];
		toReturn[0] = this.coords[this.coords.length - 2].value[0];
		toReturn[1] = this.coords[this.coords.length - 2].value[1];
		toReturn[2] = this.coords[this.coords.length - 2].value[2];

		return toReturn;
	}

	/**
	 * Set the number of steps done in smoothing coordinates
	 */
	public void setPriestleSteps(final int i) {
		this.priestleSteps = i;
	}

	public int getPriestleSteps() {
		return this.priestleSteps;
	}

	/**
	 * Compute the distance between coordinates.
	 */
	private float distance(final Vec3f v1, final Vec3f v2) {
		float dist = 0.0f;
		dist += (v2.value[0] - v1.value[0]) * (v2.value[0] - v1.value[0]);
		dist += (v2.value[1] - v1.value[1]) * (v2.value[1] - v1.value[1]);
		dist += (v2.value[2] - v1.value[2]) * (v2.value[2] - v1.value[2]);

		dist = (float) Math.sqrt(dist);
		return dist;
	}

}
