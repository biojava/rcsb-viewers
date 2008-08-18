//  $Id: BondFormRegistry.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: BondFormRegistry.java,v $
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
//  Revision 1.3  2004/04/09 00:12:53  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.2  2004/01/29 17:53:39  moreland
//  Updated copyright and class comment block.
//
//  Revision 1.1  2003/04/23 23:00:51  moreland
//  First version.
//
//  Revision 1.0  2003/06/10 23:38:39  moreland
//  First version.
//


package org.rcsb.mbt.model.attributes;


import java.util.*;


/**
 *  A class used to register BondForm implementation objects.
 *  The names of the BondForm objects can be retrieved and
 *  used in a GUI menu in order for a user to pick the desired
 *  BondForm algorithm. The retrieved BondForm object may then be
 *  handed to a StructureStyle instance in order to assocate one
 *  or more Bond representations to a given bond form scheme.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.attributes.BondForm
 *  @see	org.rcsb.mbt.model.attributes.StructureStyles
 */
public class BondFormRegistry
{
	// The registered BondForm objects
	private static final Hashtable bondFormObjects = new Hashtable( );

	// The default BondForm object name
	private static String defaultName = null;

	// Add the well-known BondForm implementation names.
	static
	{
		BondFormRegistry.add( BondFormSimple.NAME, BondFormSimple.create() );
		BondFormRegistry.add( BondFormSplit.NAME, BondFormSplit.create() );
		BondFormRegistry.add( BondFormOrder.NAME, BondFormOrder.create() );

		BondFormRegistry.defaultName = BondFormSimple.NAME;
	}

	//
	// Registration methods
	//

	/**
	 *  Return the number of registered BondForm impelementations.
	 */
	public static int count( )
	{
		return BondFormRegistry.bondFormObjects.size( );
	}

	/**
	 *  Return the name of the default BondForm impelementation.
	 */
	public static String getDefaultName( )
	{
		return BondFormRegistry.defaultName;
	}

	/**
	 *  Add a new BondForm implementation.
	 */
	public static void add( final String name, final BondForm bondForm )
	{
		BondFormRegistry.bondFormObjects.put( name, bondForm );
	}

	/**
	 *  Remove an existing BondForm implementation.
	 */
	public static void remove( final String name )
	{
		BondFormRegistry.bondFormObjects.remove( name );
	}

	/**
	 *  Get an BondForm implementation by name.
	 */
	public static BondForm get( final String name )
	{
		return (BondForm) BondFormRegistry.bondFormObjects.get( name );
	}

	/**
	 *  Get the default BondForm implementation.
	 */
	public static BondForm getDefault( )
	{
		return (BondForm) BondFormRegistry.bondFormObjects.get( BondFormRegistry.defaultName );
	}

	/**
	 *  Return an Enumeration of String values for all registered
	 *  BondForm objects.
	 */
	public static Enumeration names( )
	{
		return BondFormRegistry.bondFormObjects.keys( );
	}
}

