//  $Id: ResidueColorRegistry.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: ResidueColorRegistry.java,v $
//  Revision 1.1  2007/02/08 02:38:52  jbeaver
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
//  Revision 1.7  2004/05/14 22:55:10  moreland
//  Added ResidueColorByFragmentType to registry.
//
//  Revision 1.6  2004/05/10 17:58:52  moreland
//  Added "Yellow", "Gray", "White", and "Black" RGB colors.
//
//  Revision 1.5  2004/04/09 00:12:54  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.4  2004/02/12 17:26:45  moreland
//  Added ResidueColorByRgb to registry.
//
//  Revision 1.3  2004/01/29 17:53:41  moreland
//  Updated copyright and class comment block.
//
//  Revision 1.2  2003/09/11 22:23:56  moreland
//  Added ResidueColorByResidueIndex class and made it the default for ResidueColor.
//
//  Revision 1.1  2003/04/23 23:01:40  moreland
//  First version.
//
//  Revision 1.1  2003/02/27 21:06:33  moreland
//  Began adding classes for viewable "Styles" (colors, sizes, forms, etc).
//
//  Revision 1.0  2003/06/10 23:38:39  moreland
//  First version.
//


package org.rcsb.mbt.model.attributes;


import java.util.*;


/**
 *  A class used to register ResidueColor implementation objects.
 *  The names of the ResidueColor objects can be retrieved and
 *  used in a GUI menu in order for a user to pick the desired
 *  ResidueColor algorithm. The retrieved ResidueColor object may then be
 *  handed to a StructureStyle instance in order to assocate one
 *  or more Residue representations to a given coloring scheme.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.attributes.IResidueColor
 *  @see	org.rcsb.mbt.model.attributes.StructureStyles
 */
public class ResidueColorRegistry
{
	// The registered ResidueColor objects
	private static final Hashtable residueColorObjects = new Hashtable( );

	// The default ResidueColor object name
	private static String defaultName = null;

	// Add the well-known ResidueColor implementation names.
	static
	{
		ResidueColorRegistry.add( ResidueColorByHydrophobicity.NAME, ResidueColorByHydrophobicity.create() );
		ResidueColorRegistry.add( ResidueColorByResidueIndex.NAME, ResidueColorByResidueIndex.create() );
		ResidueColorRegistry.add( ResidueColorByFragmentType.NAME, ResidueColorByFragmentType.create() );
		ResidueColorRegistry.add( "Red", new ResidueColorByRgb( 1.0f, 0.0f, 0.0f ) );
		ResidueColorRegistry.add( "Green", new ResidueColorByRgb( 0.0f, 1.0f, 0.0f ) );
		ResidueColorRegistry.add( "Blue", new ResidueColorByRgb( 0.0f, 0.0f, 1.0f ) );
		ResidueColorRegistry.add( "Cyan", new ResidueColorByRgb( 0.0f, 1.0f, 1.0f ) );
		ResidueColorRegistry.add( "Magenta", new ResidueColorByRgb( 1.0f, 0.0f, 1.0f ) );
		ResidueColorRegistry.add( "Yellow", new ResidueColorByRgb( 1.0f, 1.0f, 0.0f ) );
		ResidueColorRegistry.add( "Gray", new ResidueColorByRgb( 0.5f, 0.5f, 0.5f ) );
		ResidueColorRegistry.add( "White", new ResidueColorByRgb( 1.0f, 1.0f, 1.0f ) );
		ResidueColorRegistry.add( "Black", new ResidueColorByRgb( 0.0f, 0.0f, 0.0f ) );

		ResidueColorRegistry.defaultName = ResidueColorByResidueIndex.NAME;
	}

	//
	// Registration methods
	//

	/**
	 *  Return the number of registered ResidueColor impelementations.
	 */
	public static int count( )
	{
		return ResidueColorRegistry.residueColorObjects.size( );
	}

	/**
	 *  Return the name of the default ResidueColor impelementation.
	 */
	public static String getDefaultName( )
	{
		return ResidueColorRegistry.defaultName;
	}

	/**
	 *  Add a new ResidueColor implementation.
	 */
	public static void add( final String name, final IResidueColor residueColor )
	{
		ResidueColorRegistry.residueColorObjects.put( name, residueColor );
	}

	/**
	 *  Remove an existing ResidueColor implementation.
	 */
	public static void remove( final String name )
	{
		ResidueColorRegistry.residueColorObjects.remove( name );
	}

	/**
	 *  Get an ResidueColor implementation by name.
	 */
	public static IResidueColor get( final String name )
	{
		return (IResidueColor) ResidueColorRegistry.residueColorObjects.get( name );
	}

	/**
	 *  Get the default ResidueColor implementation.
	 */
	public static IResidueColor getDefault( )
	{
		return (IResidueColor) ResidueColorRegistry.residueColorObjects.get( ResidueColorRegistry.defaultName );
	}

	/**
	 *  Return an Enumeration of String values for all registered
	 *  ResidueColor objects.
	 */
	public static Enumeration names( )
	{
		return ResidueColorRegistry.residueColorObjects.keys( );
	}
}

