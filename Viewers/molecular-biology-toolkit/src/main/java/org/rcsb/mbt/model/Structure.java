/*
 * BioJava development code
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence. This should
 * be distributed with the code. If you do not have a copy,
 * see:
 *
 * http://www.gnu.org/copyleft/lesser.html
 *
 * Copyright for this code is held jointly by the individual
 * authors. These should be listed in @author doc comments.
 *
 * For more information on the BioJava project and its aims,
 * or to join the biojava-l mailing list, visit the home page
 * at:
 *
 * http://www.biojava.org/
 *
 * This code was contributed from the Molecular Biology Toolkit
 * (MBT) project at the University of California San Diego.
 *
 * Please reference J.L. Moreland, A.Gramada, O.V. Buzko, Qing
 * Zhang and P.E. Bourne 2005 The Molecular Biology Toolkit (MBT):
 * A Modular Platform for Developing Molecular Visualization
 * Applications. BMC Bioinformatics, 6:21.
 *
 * The MBT project was funded as part of the National Institutes
 * of Health PPG grant number 1-P01-GM63208 and its National
 * Institute of General Medical Sciences (NIGMS) division. Ongoing
 * development for the MBT project is managed by the RCSB
 * Protein Data Bank(http://www.pdb.org) and supported by funds
 * from the National Science Foundation (NSF), the National
 * Institute of General Medical Sciences (NIGMS), the Office of
 * Science, Department of Energy (DOE), the National Library of
 * Medicine (NLM), the National Cancer Institute (NCI), the
 * National Center for Research Resources (NCRR), the National
 * Institute of Biomedical Imaging and Bioengineering (NIBIB),
 * the National Institute of Neurological Disorders and Stroke
 * (NINDS), and the National Institute of Diabetes and Digestive
 * and Kidney Diseases (NIDDK).
 *
 * Created on 2007/02/08
 *
 */ 
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

