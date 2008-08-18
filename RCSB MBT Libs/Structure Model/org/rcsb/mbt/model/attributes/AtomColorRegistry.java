//  $Id: AtomColorRegistry.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: AtomColorRegistry.java,v $
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
//  Revision 1.5  2004/11/08 17:28:33  moreland
//  Added AtomColorByBFactor class.
//
//  Revision 1.4  2004/04/09 00:12:52  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.3  2004/01/29 17:53:38  moreland
//  Updated copyright and class comment block.
//
//  Revision 1.2  2003/12/20 01:13:44  moreland
//  Registered new AtomColorByResidueColor implementation class.
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
 *  A class used to register AtomColor implementation objects.
 *  The names of the AtomColor objects can be retrieved and
 *  used in a GUI menu in order for a user to pick the desired
 *  AtomColor algorithm. The retrieved AtomColor object may then be
 *  handed to a StructureStyle instance in order to assocate one
 *  or more Atom representations to a given coloring scheme.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.attributes.IAtomColor
 *  @see	org.rcsb.mbt.model.attributes.StructureStyles
 */
public class AtomColorRegistry
{
	// The registered AtomColor objects
	private static final Hashtable atomColorObjects = new Hashtable( );

	// The default AtomColor object name
	private static String defaultName = null;

	// Add the well-known AtomColor implementation names.
	static
	{
		AtomColorRegistry.add( AtomColorByElement.NAME, AtomColorByElement.create() );
		AtomColorRegistry.add( AtomColorByResidueColor.NAME, AtomColorByResidueColor.create() );
		AtomColorRegistry.add( AtomColorByBFactor.NAME, AtomColorByBFactor.create() );

		AtomColorRegistry.defaultName = AtomColorByElement.NAME;
	}

	//
	// Registration methods
	//

	/**
	 *  Return the number of registered AtomColor impelementations.
	 */
	public static int count( )
	{
		return AtomColorRegistry.atomColorObjects.size( );
	}

	/**
	 *  Return the name of the default AtomColor impelementation.
	 */
	public static String getDefaultName( )
	{
		return AtomColorRegistry.defaultName;
	}

	/**
	 *  Add a new AtomColor implementation.
	 */
	public static void add( final String name, final IAtomColor atomColor )
	{
		AtomColorRegistry.atomColorObjects.put( name, atomColor );
	}

	/**
	 *  Remove an existing AtomColor implementation.
	 */
	public static void remove( final String name )
	{
		AtomColorRegistry.atomColorObjects.remove( name );
	}

	/**
	 *  Get an AtomColor implementation by name.
	 */
	public static IAtomColor get( final String name )
	{
		return (IAtomColor) AtomColorRegistry.atomColorObjects.get( name );
	}

	/**
	 *  Get the default AtomColor implementation.
	 */
	public static IAtomColor getDefault( )
	{
		return (IAtomColor) AtomColorRegistry.atomColorObjects.get( AtomColorRegistry.defaultName );
	}

	/**
	 *  Return an Enumeration of String values for all registered
	 *  AtomColor objects.
	 */
	public static Enumeration names( )
	{
		return AtomColorRegistry.atomColorObjects.keys( );
	}
}

