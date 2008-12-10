package org.rcsb.mbt.glscene.jogl;

//  $Id: BondGeometry.java,v 1.2 2007/02/09 13:18:37 jbeaver Exp $
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
//  UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.//
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
//  $Log: BondGeometry.java,v $
//  Revision 1.2  2007/02/09 13:18:37  jbeaver
//  fixed black protein bug and miscolored protein bug
//
//  Revision 1.1  2007/02/08 02:38:52  jbeaver
//  version 1.50
//
//  Revision 1.1  2006/09/20 16:50:42  jbeaver
//  first commit - branched from ProteinWorkshop
//
//  Revision 1.2  2006/09/02 18:52:28  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2006/08/24 17:39:03  jbeaver
//  *** empty log message ***
//
//  Revision 1.2  2006/05/30 09:43:44  jbeaver
//  Added lines and fog
//
//  Revision 1.1  2006/03/09 00:18:55  jbeaver
//  Initial commit
//
//  Revision 1.0 2005/04/04 00:12:54  moreland
//


// package org.rcsb.mbt.viewers.GlStructureViewer;


import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix3d;
import javax.vecmath.Quat4d;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3d;
import org.rcsb.mbt.model.*;
import org.rcsb.mbt.model.attributes.*;
import org.rcsb.mbt.model.geometry.ArrayLinearAlgebra;

import com.sun.opengl.util.GLUT;

import java.util.*;



/**
 *  Construct a new object for generating bond geometry using default
 *  <P>
 *  @author	John L. Moreland
 *  @copyright	UCSD
 *  @see
 */
public class BondGeometry
	extends DisplayListGeometry
{
	// Shared display lists: key="form:quality" value=Integer(displayList)
	public static HashMap<String, DisplayLists> sharedDisplayLists = new HashMap<String, DisplayLists>( );

	// Attributes used to bound the quality setting.
	private static final int minSlices = 6;
	private static final int maxSlices = 20;
	private static final int minSegments = 2;
	private static final int maxSegments = 2;

	// Bond-specific geometry attributes.
	private boolean showOrder = false; // Hide/show bond order?

	/**
	 *  Construct a new BondGeometry object.
	 *  <P>
	 */
	public BondGeometry( )
	{
	}


	/**
	 *  Set the state of showing/hiding a representation of bond order.
	 *  <P>
	 *  <table>
	 *  <tr>
	 *      <td><b>Order</b></td>
	 *      <td><b>Segments</b></td>
	 *      <td><b>Order/2.0</b></td>
	 *      <td><b>Copies</b></td>
	 *  </tr>
	 *  <tr> <td>0.0</td> <td>-----</td> <td>0.0</td> <td>1</td> </tr>
	 *  <tr> <td>0.5</td> <td>- - -</td> <td>1.0</td> <td>1</td> </tr>
	 *  <tr> <td>1.0</td> <td>-----</td> <td>2.0</td> <td>1</td> </tr>
	 *  <tr> <td>1.5</td> <td>-----<BR>- - -</td> <td>3.0</td> <td>2</td> </tr>
	 *  <tr> <td>2.0</td> <td>-----<BR>-----</td> <td>4.0</td> <td>2</td> </tr>
	 *  <tr> <td>2.5</td> <td>-----<BR>-----<BR>- - -</td> <td>5.0</td> <td>3</td> </tr>
	 *  <tr> <td>3.0</td> <td>-----<BR>-----<BR>-----</td> <td>6.0</td> <td>3</td> </tr>
	 *  <tr> <td colspan=4>Etc...</td> </tr>
	 *  </table>
	 *  <P>
	 *  @param
	 *  @return
	 *  @throws
	 */
	public final void setShowOrder( final boolean state )
	{
		this.showOrder = state;
	}


	/**
	 *  Get the state of showing/hiding a representation of bond order.
	 *  <P>
	 *  @param
	 *  @return
	 *  @throws
	 */
	public final boolean getShowOrder( )
	{
		return this.showOrder;
	}


	/**
	 *  Please complete the missing tags for main
	 *  <P>
	 *  @param
	 *  @return
	 *  @throws
	 */
	public final int getDisplayList( final int displayList, final StructureComponent structureComponent, final Style style, final GL gl, final GLU glu, final GLUT glut)
	{
		return this.getDisplayList( displayList, structureComponent, style, gl, glu, glut);
	}


	/**
	 *  Please complete the missing tags for main
	 *  <P>
	 *  @param
	 *  @return
	 *  @throws
	 */
	
	public DisplayLists[] getDisplayLists(final StructureComponent structureComponent, final Style style, final GL gl, final GLU glu, final GLUT glut) {
		//
		// Handle quality, form, and shared display lists.
		//

		final Bond bond = (Bond)structureComponent;
		final BondStyle bondStyle = (BondStyle)style;
		
		// Shared display lists: key="form:quality" value=Integer(displayList)
		final float quality = this.getQuality( );
		final int form = this.getForm( );
		final String dlKey = form + ":" + quality;
		DisplayLists cylinderList = (DisplayLists) BondGeometry.sharedDisplayLists.get( dlKey );

		final ArrayList<DisplayLists> allLists = new ArrayList<DisplayLists>();
		
		if ( cylinderList == null )
		{
			final float topPt[] = { 0.0f, +0.5f, 0.0f };
			final float botPt[] = { 0.0f, -0.5f, 0.0f };
			final int slices =
				BondGeometry.minSlices + (int) ((BondGeometry.maxSlices - BondGeometry.minSlices) * quality);
			final int segments =
				BondGeometry.minSegments + (int) ((BondGeometry.maxSegments - BondGeometry.minSegments) * quality);

			cylinderList = new DisplayLists(bond);
			cylinderList.setupLists(1);
			cylinderList.startDefine(0, gl, glu, glut);
			
			if ( (form == Geometry.FORM_POINTS) ||
				(form == Geometry.FORM_LINES) )
			{
				gl.glLineWidth( 3.0f );
				gl.glBegin( GL.GL_LINES );
				gl.glVertex3fv( topPt, 0 );
				gl.glVertex3fv( botPt, 0 );
				gl.glEnd( );
				gl.glLineWidth( 1.0f );
			}
			else if ( form == Geometry.FORM_FLAT ) {
				this.drawCylinder( gl, glu, glut, 1.0f, false, 1.0f, false, 1.0f, BondGeometry.minSlices, BondGeometry.minSegments );
			} else if ( form == Geometry.FORM_THICK ) {
				this.drawCylinder( gl, glu, glut, 1.0f, false, 1.0f, false, 1.0f, slices, segments );
			} else {
				throw new IllegalArgumentException( "unknown form " + form );
			}

//			gl.glEndList( );
			cylinderList.endDefine(gl, glu, glut);

//			cylinderDLInteger = new Integer( cylinderDl );
			BondGeometry.sharedDisplayLists.put( dlKey, cylinderList );
		}
		cylinderList = cylinderList.copy();
		cylinderList.structureComponent = bond;

//		cylinderDl = cylinderDLInteger.intValue( );

		//
		// Gather state
		//


		final Structure structure = bond.getStructure( );
		final StructureMap structureMap = structure.getStructureMap( );
		final StructureStyles structureStyles = structureMap.getStructureStyles( );


		// Get split bond locations
		final Atom atom0 = bond.getAtom(0);
		final Atom atom1 = bond.getAtom(1);

		// Compute split bond locations using the two atom locations.

		final double center[] = {
			(atom0.coordinate[0] + atom1.coordinate[0]) / 2.0f,
			(atom0.coordinate[1] + atom1.coordinate[1]) / 2.0f,
			(atom0.coordinate[2] + atom1.coordinate[2]) / 2.0f
		};
		final double locations[][] = {
			{
				(atom0.coordinate[0] + center[0]) / 2.0f,
				(atom0.coordinate[1] + center[1]) / 2.0f,
				(atom0.coordinate[2] + center[2]) / 2.0f
			},
			{
				(atom1.coordinate[0] + center[0]) / 2.0f,
				(atom1.coordinate[1] + center[1]) / 2.0f,
				(atom1.coordinate[2] + center[2]) / 2.0f
			}
		};

		final double bondDistance = bond.getDistance( );


		// Compute the bond direction using the relative atom positions.

		final double direction[] = {
			atom1.coordinate[0] - atom0.coordinate[0],
			atom1.coordinate[1] - atom0.coordinate[1],
			atom1.coordinate[2] - atom0.coordinate[2]
		};
		
		// normalize the vector.
		final float directionLength = (float)Math.sqrt(
			direction[0] * direction[0] +
			direction[1] * direction[1] +
			direction[2] * direction[2] );
		direction[0] /= directionLength;
		direction[1] /= directionLength;
		direction[2] /= directionLength;

		// The rotation vector is just the cross-product of the two vectors:
		// (0.0, 1.0, 0.0) for the normalized Y-Up geometry/vector, and,
		// (direction[0], direction[1], direction[2]) for the bond direction
		// which, when multiplied out, simplifies to just:
		final double rotation[] = { direction[2], 0.0f, -direction[0] };

		// The rotation angle is just the angle between the two vectors
		// which is "acos( v1 DOT v2 / |v1| * |v2| )" which simplifies to:
		final double pi = Math.PI;//3.1415926535f;
		final double r2d = 180.0f / pi;  // radians to degrees conversion factor
		final double rotAngleRadians = Math.acos( direction[1] );
		final double rotAngleDegrees = r2d * rotAngleRadians;

		// When we draw the bond order representation, we need
		// to compute different segment locations to get the
		// "dashed line" effect for partial orders (0.5).
		//
		//   -----         -----
		//  /     \=======/     \
		//  |  *  |       |  *  |
		//  \     / == == \     /
		//   -----         -----
		//
		// Only used when we do bond order representation.
		final float bondSegmentFraction = 0.40f; // 90% JLM DEBUG: Make user settable?
		double segmentDistance0 = 0.0f;
		double segmentDistance1 = 0.0f;
		double atomRadius1 = 0.0;
		
		double preRotYcos = 1.0, preRotYsin = 0.0;
					// sin/cos vectors to pre-rotate double bonds around the Y axis prior to pointing,
					// to align with third plane-defining atom.
		
		boolean doPreRot = false;
		
		if ( this.showOrder )
		{
			final AtomStyle atomStyle0 =
				(AtomStyle) structureStyles.getStyle( atom0 );
			final AtomStyle atomStyle1 =
				(AtomStyle) structureStyles.getStyle( atom1 );
			final float atomRadius0 = atomStyle0.getAtomRadius( atom0 );
			atomRadius1 = atomStyle1.getAtomRadius( atom1 );

			final double interAtomDistance =
				bondDistance - atomRadius0 - atomRadius1;
			final double bondSegmentLength =
				interAtomDistance * bondSegmentFraction;
			final double halfBondSegmentLength = bondSegmentLength * 0.5f;
			final double interSegmentGap =
				interAtomDistance * (1.0f - bondSegmentFraction * 2.0f) * 0.333f;

			segmentDistance0 =
				atomRadius0 + interSegmentGap + halfBondSegmentLength;
			segmentDistance1 =
				atomRadius0 + interSegmentGap + bondSegmentLength +
				interSegmentGap + halfBondSegmentLength;
		}

		// Get bond length and radius
        double bondRadius = atomRadius1 / 2.0; // XXX bondStyle.getBondRadius( bond );
		double bondScale = bondRadius / 2.0f;
		final double bondSplitLen = bondDistance / 2.0f;

		// Figure out what to do with the bond order
		int nBondParts = 1; // By default, we only draw one bond copy.
		float bondOrder = bond.getOrder( );
		if ( showOrder )
		{
			nBondParts = Math.round( bondOrder );
			if ( nBondParts <= 0 )
				nBondParts = 1;
		}

		// Draw the bond

		cylinderList.specularColor = Constants.mat_specular;
		cylinderList.shininess = Constants.atomHighShininess;
		cylinderList.emissiveColor = Constants.black;


		// If order == 1 OR !showOrder then
		// nBondParts == 1 and we'll only go through the outer loop once.
		int partialIX = (showOrder && bondOrder - (float)nBondParts + 0.1 > 0.5)? nBondParts++ : -1;
		
		float stance[] = new float[nBondParts];
		if (nBondParts > 1)
		{
			bondRadius /= (nBondParts);
			bondScale /= (nBondParts);
			
			double offset = bondRadius;
			Atom participantAtom0 = null, centerAtom = null, participantAtom1 = null,
			     candidateAtom = null;;
			
			//
			// beg get pre-rotation angle
			// find outlier atom
			//
			for (int ixAtom = 0; ixAtom < 2 && participantAtom1 == null; ixAtom++)
			{
				participantAtom0 = ixAtom == 0? atom0 : atom1;
				centerAtom = ixAtom == 0? atom1 : atom0;
				
				Vector<Bond> checkBonds = structureMap.getBonds(centerAtom);
				
				if (checkBonds.size() > 1)		// (if 1, it's this bond and we don't care.)
					for (Bond checkBond : checkBonds)
					{
						for (int nxAtom = 0; nxAtom < 2; nxAtom++)
						{
							Atom checkAtom = checkBond.getAtom(nxAtom);
							if (checkAtom != participantAtom0 && checkAtom != centerAtom)
							{				
								if (checkAtom.element.charAt(0) == 'C')
								{
									participantAtom1 = checkAtom;
									break;
								}
								
								else
									candidateAtom = checkAtom;
							}
						}
					}
			}
			
			if (participantAtom1 == null)
				participantAtom1 = candidateAtom;
							// ( which may be null )
			
			//
			// end get other bond
			// beg get angle
			//
			
			assert(participantAtom1 != null);
						// this shouldn't happen, but it seems to...
			
			if (participantAtom1 != null)
			{			
				double[] xlatedThirdAtomPoint =
					new double[] { participantAtom1.coordinate[0] - participantAtom0.coordinate[0], 
								   participantAtom1.coordinate[1] - participantAtom0.coordinate[1],
								   participantAtom1.coordinate[2] - participantAtom0.coordinate[2] };
			
				
				double rotator[] = new double[] { -rotAngleRadians, rotation[0], rotation[1], rotation[2]};			
				ArrayLinearAlgebra.angleAxisRotate(rotator, xlatedThirdAtomPoint);
							// apply the reverse rotation to the third atom point, so that it is in reference
							// to the y-aligned initial bond vector
				
				double xzLen = Math.sqrt(xlatedThirdAtomPoint[0] * xlatedThirdAtomPoint[0] +
										 xlatedThirdAtomPoint[2] * xlatedThirdAtomPoint[2]);
							// the pre-rotation is only about the y axis, so we need to get the values for the xz plane, only
				
				preRotYcos = xlatedThirdAtomPoint[0] / xzLen;
				preRotYsin = xlatedThirdAtomPoint[2] / xzLen;
							// sin-cos fully define the rotation angle (see below)
				
				doPreRot = true;
			}
			
			stance[0] = (float)-offset;
			
			if (nBondParts == 3)
				stance[1] = 0.0f;
			
			stance[nBondParts - 1] = (float)offset;
				// stance array determines the offset where each bond part gets drawn
		}
		
		else
			stance[0] = 0.0f;
		
		for ( int bondPartIX = 0; bondPartIX < nBondParts; bondPartIX++ )
				// Draw the bond parts (1 - 3 [ +1 if fractional bond ])
		{
			for ( int s = 0; s < 2; s++ )
				// Draw the two split bond segments (top and bottom).
			{
				final DisplayLists currentList = cylinderList.copy();
				currentList.isLeftSideOfBond = s == 0;;

				if (  form == Geometry.FORM_POINTS || form == Geometry.FORM_LINES )
				{
					currentList.mutableColorType = GL.GL_EMISSION;
					currentList.emissiveColor = null;
					currentList.disableLigting = true;
				}
				else
				{
					currentList.mutableColorType = GL.GL_AMBIENT_AND_DIFFUSE;
				}
				
				if ( this.showOrder && bondPartIX == partialIX )
				{
					locations[0][0] =
						atom0.coordinate[0] + direction[0] * segmentDistance0;
					locations[0][1] =
						atom0.coordinate[1] + direction[1] * segmentDistance0;
					locations[0][2] =
						atom0.coordinate[2] + direction[2] * segmentDistance0;
					locations[1][0] =
						atom0.coordinate[0] + direction[0] * segmentDistance1;
					locations[1][1] =
						atom0.coordinate[1] + direction[1] * segmentDistance1;
					locations[1][2] =
						atom0.coordinate[2] + direction[2] * segmentDistance1;
				}
				currentList.translation = new float[] {(float)locations[s][0], (float)locations[s][1], (float)locations[s][2]};
				currentList.rotation = new float[] {(float)rotAngleDegrees, (float)rotation[0], (float)rotation[1], (float)rotation[2]};

				if ( nBondParts > 1 )
				{
					if (doPreRot)
					{
						currentList.doPreRot = doPreRot;
						currentList.preRotYcos = preRotYcos;
						currentList.preRotYsin = preRotYsin;
						currentList.preTranslateX = stance[bondPartIX];
					}

					// If last copy and odd bond order (ie: 0.5 partial order)
					// make the geometry "broken" by shrinking the two pieces
					// of the split geometry vertically to leave gaps.
					if ( bondPartIX == partialIX )
						currentList.scale = new float[] {1.f, bondSegmentFraction, 1.f};
				}

				if(currentList.scale == null)
					currentList.scale = new float[] {(float)bondScale, (float)bondSplitLen, (float)bondScale};
				
				else
				{
					currentList.scale[0] *= bondScale;
					currentList.scale[1] *= bondSplitLen;
					currentList.scale[2] *= bondScale;
				}

				allLists.add(currentList);
			}
		}
		
		DisplayLists displayLists[] = new DisplayLists[allLists.size()];
		for (int ix = 0; ix < allLists.size(); ix++)
			displayLists[ix] = allLists.get(ix);
							// Interesting - 'toArray()' doesn't.  We get a bad cast exception...
		
		return displayLists;
	}


	/**
	 *  Please complete the missing tags for main
	 *  <P>
	 *  @param
	 *  @return
	 *  @throws
	 */
	public void drawCylinder( final GL gl, final GLU glu, final GLUT glut, double height, final boolean drawBottom, final double bottomRadius, final boolean drawTop, final double topRadius, final int slices, final int segments )
	{
		double halfHeight = height / 2.0f;

		// Transform the GLU Z-Out cylinder orientation to a Y-Up orientation,
		// and change it's center from the base to its mid-point!
		gl.glTranslated( 0.0, halfHeight, 0.0 );
		gl.glRotated( 90.0, 1.0, 0.0, 0.0 );

		//
		// Draw the cylinder body.
		//

		final GLUquadric cyl = glu.gluNewQuadric( );
		glu.gluCylinder( cyl, bottomRadius, topRadius, height, slices, segments );
		glu.gluDeleteQuadric( cyl );

		//
		// Draw the cylinder top and/or bottom.
		//

		GLUquadric disk = null;

		if ( drawTop )
		{
			gl.glTranslated( 0.0, halfHeight, 0.0 );
			disk = glu.gluNewQuadric( );
			glu.gluDisk( disk, 0.0, topRadius, slices, 1 );
			glu.gluDeleteQuadric( disk );
		}

		if ( drawBottom )
		{
			if ( drawTop ) {
				gl.glTranslated( 0.0, -height, 0.0 );
			} else {
				gl.glTranslated( 0.0, -halfHeight, 0.0 );
			}
			disk = glu.gluNewQuadric( );
			glu.gluDisk( disk, 0.0, bottomRadius, slices, 1 );
			glu.gluDeleteQuadric( disk );
		}
	}
}

