//  $Id: Structure.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: Structure.java,v $
//  Revision 1.1  2007/02/08 02:38:52  jbeaver
//  version 1.50
//
//  Revision 1.1  2006/09/20 16:50:42  jbeaver
//  first commit - branched from ProteinWorkshop
//
//  Revision 1.1  2006/08/24 17:39:03  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2006/03/09 00:18:55  jbeaver
//  Initial commit
//
//  Revision 1.14  2004/10/27 20:03:30  moreland
//  Corrected javadoc SEE references.
//
//  Revision 1.13  2004/05/05 00:42:32  moreland
//  Added StructureInfo support.
//
//  Revision 1.12  2004/04/09 00:17:01  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.11  2004/01/30 22:47:56  moreland
//  Added more detail descriptions for the class block comment.
//
//  Revision 1.10  2004/01/30 21:23:56  moreland
//  Added new diagrams.
//
//  Revision 1.9  2004/01/29 17:08:16  moreland
//  Updated copyright and class block comments.
//
//  Revision 1.8  2003/12/09 21:18:17  moreland
//  Changed println to Status.output calls.
//
//  Revision 1.7  2003/04/23 17:33:12  moreland
//  Removed StructureComponentID/scid and replaced it with a "structure" field
//  in the StructureComponent base class.
//  Changed "getType" method to "getStructureComponentType" to return dynamic
//  SC type (ie: class name).
//  Changed getStructureComponentById method to return an object instance
//  rather than filling in a user-supplied object. The hashCode for a
//  StructureComponent subclass instance should be the the same for a
//  given StructureComponent type index.
//
//  Revision 1.6  2002/12/16 17:47:58  moreland
//  Added the getStructureMap factory method to produce a StructureMap object.
//
//  Revision 1.5  2002/12/16 06:27:53  moreland
//  Changed code to support differentiation of Conformation into Coil, Helix,
//  Strand, and Turn sub-class types (at Eliot Clingman's suggestion).
//  Also, Structure is no longer a StructureComponent sub-class (made no sense).
//
//  Revision 1.4  2002/11/14 18:14:13  moreland
//  Made minor correction to the comments.
//
//  Revision 1.3  2002/11/06 23:20:57  moreland
//  Removed references to the old selection event mechanism since raw data
//  is not selected (Viewable objects are).
//
//  Revision 1.2  2002/10/24 17:54:01  moreland
//  Provides implementation for the improved Structure API/implementation.
//
//  Revision 1.1.1.1  2002/07/16 18:00:18  moreland
//  Imported sources
//
//  Revision 1.0  2002/06/10 23:38:39  moreland
//


package org.rcsb.mbt.model;

import org.rcsb.mbt.model.StructureComponentRegistry.ComponentType;
import org.rcsb.mbt.model.filters.IStructureComponentFilter;
import org.rcsb.mbt.model.filters.RelationFilter;
import org.rcsb.mbt.model.util.Status;


/**
 *  This class provides the primary container for data in a molecular data set.
 *  It provides a purposfully simple "lists of records" data model.
 *  Each structure may contain zero or more records (StructureComponent
 *  objects) where each type is defined to be a subclass of StructureComponent.
 *  The StructureComponent known by the toolkit are registered in the
 *  StructureComponentRegistry class. While the toolkit itself automatically
 *  initializes the registry with several well-known StructureComponent types
 *  (eg: Atom, Bond, Residue, Fragment, Chain, Coil, Helix, Strand, Turn, etc)
 *  new types, can also be dynamically registered by a custom StructureLoader
 *  or by application code.
 *  <P>
 *  The simple "list of records" data model enables programs that just want
 *  to read and process raw data to remain simple while simultaneously
 *  providing a powerful and extensible model for adding new data types
 *  and more complex data model layers on top of this basic container design.
 *  <P>
 *  <center>
 *  <IMG SRC="doc-files/Structure.jpg">
 *  </center>
 *  <P>
 *  Note that since there are currently no "set" methods in this class,
 *  there are also currently no StructureComponentEvent handling methods.
 *  We hope to add set/modeling methods in future releases of the toolkit.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.StructureMap
 *  @see	org.rcsb.mbt.model.attributes.StructureStyles
 *  @see	org.rcsb.mbt.model.StructureModel
 *  @see	org.rcsb.mbt.model.StructureComponentEvent
 *  @see	org.rcsb.mbt.model.StructureComponentEventListener
 */
public abstract class Structure
{
	/**
	 * This method returns a string which represents the URL
	 * from which the structure instance was produced.
	 *
	 * Examples:
	 *
	 *    file://c:/myFiles/5ebx.pdb     // a local PDB file
	 *    file://c:/myFiles/5ebx.mmCIF   // a local mmCIF file
	 *    ftp://ftp.rcsb.org/2cpk.pdb    // a remote pdb file
	 *    ftp://ftp.rcsb.org/4hhb.mmCIF  // a remote mmCIF file
	 *    iiop://corba.sdsc.edu/2cpk     // an openMMS reference
	 *
	 * This URL can be handed back to the StructureFactory in order
	 * to re-load the structure.
	 */
	abstract public String getUrlString( );

	/**
	 * Counts components of the specified type that exist in the structure.
	 * TYPE values are defined in the StructureComponentRegistry class.
	 */
	abstract public int getStructureComponentCount( ComponentType scType );

	/**
	 * Given a StructureComponentFilter, create and return an iterator
	 * which will selectively return a subset of structure components.
	 */
	public StructureComponentIterator createStructureComponentIterator(
		final IStructureComponentFilter filter )
	{
		try
		{
			return new StructureComponentIterator( this, filter );
		}
		catch( final Exception e )
		{
	 		Status.output( Status.LEVEL_ERROR, "Structure.createStructureComponentIterator: exception = " + e + " (perhaps a bad filter?)" );
			return null;
		}
	}


	/**
	 * Given a StructureComponentRelation name and a StructureComponent,
	 * create and return a StructureComponentIterator which will selectively
	 * return the related subset of StructureComponent objects. This is
	 * acheaved by creating a RelationFilter with relation name and
	 * StructureComponent parameters then simply calling the
	 * createStructureComponentIterator method with that filter.
	 * <P>
	 */
	public StructureComponentIterator getStructureComponentRelations(
		final String relation_name, final StructureComponent structureComponent )
			throws IllegalArgumentException
	{
		final StructureComponentRelation relation =
			StructureComponentRegistry.getRelation( relation_name );
		final RelationFilter relationFilter =
			new RelationFilter( relation, structureComponent );
		return this.createStructureComponentIterator( relationFilter );
	}

	/**
	 * Get the values for any StructureComponent subclass by its type-specific
	 * index. For example: Atom, Residue, Conformation, etc.
	 * <P>
	 * Note that it is the caller's responsibility to cast the returned
	 * object to the corresponding StructureComponent subclass type.
	 * For example, an application might do something like this:
	 * <P>
	 * <UL>
	 * <PRE>
	 * // First load a structure using the StructureFactory, and then...
	 * int atom_count =
	 *   structure.getStructureComponentCount( ComponentType.ATOM );
	 * for ( int i = 0; i < atom_count; i++ )
	 * {
	 *    Atom atom = (Atom) structure.getStructureComponentByIndex(ComponentType.ATOM, i );
	 *
	 *    System.err.println( "coord = " + atom.coordinate[0] + ", " +
     *      atom.coordinate[1] + ", " + atom.coordinate[2] );
     *      System.err.println( "name = " + atom.name );
     *    System.err.println( "occupancy = " + atom.occupancy );
	 * }
	 * </PRE>
	 * </UL>
	 */
	abstract public StructureComponent getStructureComponentByIndex(
		ComponentType structureComponentType, int index )
		throws IndexOutOfBoundsException, IllegalArgumentException;

	/**
	 *  Stores a reference to a StructureMap object for this Structure.
	 *  Only one StructureMap needs to be created for a given Structure,
	 *  because no inter-method-call state is kept by the object.
	 *  Once a single StructureMap is created, subsequent calls to
	 *  the getStructureMap method return the same StructureMap object.
	 */
	private StructureMap structureMap = null;

	/**
	 * Return the StructureMap object that describes the derived (implied)
	 * hierarchy of primary and secondary structure data for the Structure.
	 * This mapping provides a means to iteratate over components of the
	 * structure in an ordered and efficient manner.
	 * <P>
	 */
	public StructureMap getStructureMap( )
	{
		return this.structureMap;
	}
	
	/**
	 * Test if we have a structureMap
	 * 
	 * @return - true if we have one.
	 */
	public boolean hasStructureMap() { return this.structureMap != null; }

	/**
	 * Application may need a derived version of the structure map - 
	 * Can get set here - note that this should be called and the
	 * structure map set *before* getStructureMap is called.
	 * 
	 * An alternative would be some sort of factory method, but I
	 * don't want to get that specific until I know I need it.
	 * 
	 * This could all collapse back into a single structureMap class.
	 * 
	 * 14-May-08	rickb
	 * 
	 * @param map
	 */
	public void setStructureMap(StructureMap structureMap)
	{
		assert(this.structureMap == null);
						// map was set somewhere else.  Fix it.
		
		this.structureMap = structureMap;
	}
	
	// Holds an instance of a StructureInfo object.
	private StructureInfo structureInfo = null;

	/**
	 * Set the StructureInfo record for this structure.
	 * This is generally set by a StructureLoader, though it may be also
	 * set or modified for authoring purposes.
	 * <P>
	 * @see #getStructureInfo
	 * <P>
	 */
	public void setStructureInfo( final StructureInfo info )
	{
		this.structureInfo = info;
	}

	/**
	 * Get the StructureInfo record for this structure.
	 * <P>
	 * @see #setStructureInfo
	 * <P>
	 */
	public StructureInfo getStructureInfo( )
	{
		return this.structureInfo;
	}
}

