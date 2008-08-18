//  $Id: BondRadiusByScaledAtomRadius.java,v 1.1 2007/02/08 02:38:51 jbeaver Exp $
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
//  $Log: BondRadiusByScaledAtomRadius.java,v $
//  Revision 1.1  2007/02/08 02:38:51  jbeaver
//  version 1.50
//
//  Revision 1.1  2006/09/20 16:50:42  jbeaver
//  first commit - branched from ProteinWorkshop
//
//  Revision 1.1  2006/08/24 17:39:02  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2006/03/09 00:18:54  jbeaver
//  Initial commit
//
//  Revision 1.4  2005/11/08 20:58:33  moreland
//  Switched style code to new StructureStyles API.
//
//  Revision 1.3  2004/04/09 00:12:53  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.2  2004/01/29 17:53:40  moreland
//  Updated copyright and class comment block.
//
//  Revision 1.1  2003/12/20 01:37:06  moreland
//  First implementation.
//
//  Revision 1.1  2003/12/16 21:42:42  moreland
//  Added new style implementation classes.
//
//  Revision 1.0  2003/12/15 18:33:19  moreland
//  First implementation.
//


package org.rcsb.mbt.model.attributes;


import org.rcsb.mbt.model.*;


/**
 *  This class implements the BondRadius interface by applying a radius
 *  to the given Bond by using the AtomRadius class.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.attributes.BondRadius
 *  @see	org.rcsb.mbt.model.Bond
 */
public class BondRadiusByScaledAtomRadius
	implements BondRadius
{
	public static final String NAME = "By Scaled Atom Radius (Ball-And-Stick)";

	// Scale factor applied to atom radius used to calcualte bond radius
	private final float scale = 0.3f;

	// Holds a singleton instance of this class.
	private static BondRadiusByScaledAtomRadius singleton = null;

	/**
	 *  The constructor is PRIVATE so that the "create" method
	 *  is used to produce a singleton instance of this class.
	 */
	private BondRadiusByScaledAtomRadius( )
	{
	}

	/**
	 *  Return the singleton instance of this class.
	 */
	public static BondRadiusByScaledAtomRadius create( )
	{
		if ( BondRadiusByScaledAtomRadius.singleton == null ) {
			BondRadiusByScaledAtomRadius.singleton = new BondRadiusByScaledAtomRadius( );
		}
		return BondRadiusByScaledAtomRadius.singleton;
	}

	/**
	 *  Produce the primary radius based upon the first atom radius.
	 */
	public float getBondRadius( final Bond bond )
	{
		final Atom atom = bond.getAtom( 0 );
		final StructureMap structureMap = atom.getStructure().getStructureMap( );
		final StructureStyles structureStyles = structureMap.getStructureStyles( );
		final AtomStyle atomStyle = (AtomStyle) structureStyles.getStyle( atom );
		return this.scale * atomStyle.getAtomRadius( atom );
	}

	/**
	 *  Produce the secondary radius based upon the second atom radius.
	 */
	public float getSplitBondRadius( final Bond bond )
	{
		final Atom atom = bond.getAtom( 1 );
		final StructureMap structureMap = atom.getStructure().getStructureMap( );
		final StructureStyles structureStyles = structureMap.getStructureStyles( );
		final AtomStyle atomStyle = (AtomStyle) structureStyles.getStyle( atom );
		return this.scale * atomStyle.getAtomRadius( atom );
	}
}

