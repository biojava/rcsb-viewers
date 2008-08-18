//  $Id: AtomRadiusRegistry.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: AtomRadiusRegistry.java,v $
//  Revision 1.1  2007/02/08 02:38:52  jbeaver
//  version 1.50
//
//  Revision 1.1  2006/09/20 16:50:42  jbeaver
//  first commit - branched from ProteinWorkshop
//
//  Revision 1.1  2006/08/24 17:39:02  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2006/03/09 00:18:55  jbeaver
//  Initial commit
//
//  Revision 1.6  2004/04/09 00:12:52  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.5  2004/01/29 17:53:39  moreland
//  Updated copyright and class comment block.
//
//  Revision 1.4  2003/12/16 22:31:55  moreland
//  Changed default to ScaledCpk in order to match default ball-and-stick style.
//
//  Revision 1.3  2003/12/16 21:42:42  moreland
//  Added new style implementation classes.
//
//  Revision 1.2  2003/09/16 21:09:05  moreland
//  Renamed "AtomRadiusByElement" to "AtomRadiusByCpk".
//  Added "AtomRadiusByScaledCpk" class.
//
//  Revision 1.1  2003/02/27 21:06:33  moreland
//  Began adding classes for viewable "Styles" (colors, sizes, forms, etc).
//
//  Revision 1.0  2003/06/10 23:38:39  moreland
//  First version.
//


package org.rcsb.mbt.model.attributes;


import java.util.Enumeration;
import java.util.Hashtable;


/**
 *  A class used to register AtomRadius implementation objects.
 *  The names of the AtomRadius objects can be retrieved and
 *  used in a GUI menu in order for a user to pick the desired
 *  AtomRadius algorithm. The retrieved AtomRadius object may then be
 *  handed to a StructureStyle instance in order to assocate one
 *  or more Atom representations to a given radius scheme.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.attributes.IAtomRadius
 *  @see	org.rcsb.mbt.model.attributes.StructureStyles
 */
public class AtomRadiusRegistry
{
	// The registered AtomRadius objects
	private static final Hashtable atomRadiusObjects = new Hashtable( );

	// The default AtomRadius object name
	private static String defaultName = null;

	// Add the well-known AtomRadius implementation names.
	static
	{
		AtomRadiusRegistry.add( AtomRadiusByCpk.NAME, AtomRadiusByCpk.create() );
		AtomRadiusRegistry.add( AtomRadiusByScaledCpk.NAME, AtomRadiusByScaledCpk.create() );
		AtomRadiusRegistry.add( AtomRadiusByConstant.NAME, AtomRadiusByConstant.create() );

		AtomRadiusRegistry.defaultName = AtomRadiusByScaledCpk.NAME;
	}

	//
	// Registration methods
	//

	/**
	 *  Return the number of registered AtomRadius impelementations.
	 */
	public static int count( )
	{
		return AtomRadiusRegistry.atomRadiusObjects.size( );
	}

	/**
	 *  Return the name of the default AtomRadius impelementation.
	 */
	public static String getDefaultName( )
	{
		return AtomRadiusRegistry.defaultName;
	}

	/**
	 *  Add a new AtomRadius implementation.
	 */
	public static void add( final String name, final IAtomRadius atomRadius )
	{
		AtomRadiusRegistry.atomRadiusObjects.put( name, atomRadius );
	}

	/**
	 *  Remove an existing AtomRadius implementation.
	 */
	public static void remove( final String name )
	{
		AtomRadiusRegistry.atomRadiusObjects.remove( name );
	}

	/**
	 *  Get an AtomRadius implementation by name.
	 */
	public static IAtomRadius get( final String name )
	{
		return (IAtomRadius) AtomRadiusRegistry.atomRadiusObjects.get( name );
	}

	/**
	 *  Get the default AtomRadius implementation.
	 */
	public static IAtomRadius getDefault( )
	{
		return (IAtomRadius) AtomRadiusRegistry.atomRadiusObjects.get( AtomRadiusRegistry.defaultName );
	}

	/**
	 *  Return an Enumeration of String values for all registered
	 *  AtomRadius objects.
	 */
	public static Enumeration names( )
	{
		return AtomRadiusRegistry.atomRadiusObjects.keys( );
	}
}

