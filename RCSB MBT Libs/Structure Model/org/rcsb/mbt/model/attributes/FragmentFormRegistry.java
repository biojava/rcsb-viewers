//  $Id: FragmentFormRegistry.java,v 1.1 2007/02/08 02:38:51 jbeaver Exp $
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
//  $Log: FragmentFormRegistry.java,v $
//  Revision 1.1  2007/02/08 02:38:51  jbeaver
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
//  Revision 1.2  2004/04/09 00:12:54  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.1  2004/01/29 17:46:21  moreland
//  Changed name from "Style" to "Form" for consistency with other viewables styles.
//
//  Revision 1.3  2004/01/05 22:03:48  moreland
//  Added FragmentFormRibbon class.
//
//  Revision 1.2  2004/01/05 21:59:44  moreland
//  Added FragmentFormCylinder class.
//
//  Revision 1.1  2003/12/20 01:37:06  moreland
//  First implementation.
//
//  Revision 1.0  2003/12/19 23:38:39  moreland
//  First version.
//


package org.rcsb.mbt.model.attributes;


import java.util.*;


/**
 *  A class used to register FragmentForm implementation objects.
 *  The names of the FragmentForm objects can be retrieved and
 *  used in a GUI menu in order for a user to pick the desired
 *  FragmentForm algorithm. The retrieved FragmentForm object may then be
 *  handed to a StructureStyle instance in order to assocate one
 *  or more Fragment representations to a given style scheme.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.attributes.FragmentForm
 *  @see	org.rcsb.mbt.model.attributes.StructureStyles
 */
public class FragmentFormRegistry
{
	// The registered FragmentForm objects
	private static final Hashtable fragmentFormObjects = new Hashtable( );

	// The default FragmentForm object name
	private static String defaultName = null;

	// Add the well-known FragmentForm implementation names.
	static
	{
		FragmentFormRegistry.add( FragmentFormWireframe.NAME, FragmentFormWireframe.create() );
		FragmentFormRegistry.add( FragmentFormTube.NAME, FragmentFormTube.create() );
		FragmentFormRegistry.add( FragmentFormCartoon.NAME, FragmentFormCartoon.create() );
		FragmentFormRegistry.add( FragmentFormLadder.NAME, FragmentFormLadder.create() );
		FragmentFormRegistry.add( FragmentFormCylinder.NAME, FragmentFormCylinder.create() );
		FragmentFormRegistry.add( FragmentFormRibbon.NAME, FragmentFormRibbon.create() );

		FragmentFormRegistry.defaultName = FragmentFormCartoon.NAME;
	}

	//
	// Registration methods
	//

	/**
	 *  Return the number of registered FragmentForm impelementations.
	 */
	public static int count( )
	{
		return FragmentFormRegistry.fragmentFormObjects.size( );
	}

	/**
	 *  Return the name of the default FragmentForm impelementation.
	 */
	public static String getDefaultName( )
	{
		return FragmentFormRegistry.defaultName;
	}

	/**
	 *  Add a new FragmentForm implementation.
	 */
	public static void add( final String name, final FragmentForm fragmentForm )
	{
		FragmentFormRegistry.fragmentFormObjects.put( name, fragmentForm );
	}

	/**
	 *  Remove an existing FragmentForm implementation.
	 */
	public static void remove( final String name )
	{
		FragmentFormRegistry.fragmentFormObjects.remove( name );
	}

	/**
	 *  Get an FragmentForm implementation by name.
	 */
	public static FragmentForm get( final String name )
	{
		return (FragmentForm) FragmentFormRegistry.fragmentFormObjects.get( name );
	}

	/**
	 *  Get the default FragmentForm implementation.
	 */
	public static FragmentForm getDefault( )
	{
		return (FragmentForm) FragmentFormRegistry.fragmentFormObjects.get( FragmentFormRegistry.defaultName );
	}

	/**
	 *  Return an Enumeration of String values for all registered
	 *  FragmentForm objects.
	 */
	public static Enumeration names( )
	{
		return FragmentFormRegistry.fragmentFormObjects.keys( );
	}
}

