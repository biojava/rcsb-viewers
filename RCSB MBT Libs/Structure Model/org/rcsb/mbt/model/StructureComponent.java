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


/**
 *  Defines the abstract base class for a StructureComponent data container.
 *  Each subclass instance represents a raw "record" from its source Structure.
 *  Using the Structure class directly is useful if an application simply
 *  wishes to walk the raw records of a data set. For a more structured "map"
 *  (hierarchical view) of derived data, see the StructureMap class.
 *  <P>
 *  Here is an example of the Atom sub-class which demonstrates a typical
 *  use case for all StructureComponent sub-classes:
 *  <P>
 *  <PRE>
 *      int atomCount = structure.getStructureComponentCount(
 *         StructureComponentRegistry.TYPE_ATOM );
 *      for ( int i=0; i<atomCount; i++ )
 *      {   
 *         Atom atom = (Atom) getStructureComponentByIndex(
 *            StructureComponentRegistry.TYPE_ATOM, i );
 *         // do something useful here with the current Atom record
 *      }
 *  </PRE>
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.Structure
 *  @see	org.rcsb.mbt.model.StructureMap
 */
public abstract class StructureComponent
	implements java.lang.Cloneable
{
	/**
	 *  The Structure to which a StructureComponent instance belongs.
	 */
	public Structure structure = null;

	/**
	 *  Set the Structure to which a StructureComponent instance belongs.
	 */
	public final void setStructure( final Structure structure )
	{
		if ( structure == null ) {
			throw new NullPointerException( "Null structure" );
		}
		this.structure = structure;
	}

	/**
	 *  Get the Structure to which a StructureComponent instance belongs.
	 */
	public final Structure getStructure( )
	{
		return this.structure;
	}

	/**
	 *  Clone this object.
	 */
	
	public Object clone( )
		throws CloneNotSupportedException
	{
		return super.clone( );
	}

	/**
	 *  Copy all of the field values from the parameter object into "this".
	 */
	abstract public void copy( StructureComponent structureComponent );

	/**
	 *  This method returns the fully qualified name of this class.
	 *  Sub-classes must override this method (This method should really
	 *  be declared abstract, but unfortunately Java does not allow a
	 *  method to be both static AND abstract). Instead, this base class 
	 *  implementation will through an UnsupportedOperationException.
	 *  <P>
	 *  This name is used by the StructureComponentRegistry class to enable
	 *  dynamic registration and discovery of new StructureComponent
	 *  sub-classes/types. The name is also used to create instances
	 *  of any sublcass dynamically from this name.
	 */
	public static String getClassName( )
		throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException(
			"getClassName method not implemented" );
	}

	/**
	 *  This method returns the fully qualified name of this class.
	 *  Sub-classes must override this method. Unfortunately Java does not
	 *  allow a method to be both static AND abstract). Instead, this base
	 *  class implementation will through an UnsupportedOperationException.
	 *  <P>
	 *  This name is used by the StructureComponentRegistry class to enable
	 *  dynamic registration and discovery of new StructureComponent
	 *  sub-classes/types. The name is also used to create instances
	 *  of any sublcass dynamically from this name.
	 */
	public abstract ComponentType getStructureComponentType( );
}

