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


// MBT

// Core
import java.util.NoSuchElementException;

import org.rcsb.mbt.model.StructureComponentRegistry.ComponentType;
import org.rcsb.mbt.model.filters.*;


/**
 *  Defines a standard interface to describe a filter implementation class.
 *  A filter is repsonsible for implementing two methods which should return
 *  values according to what subset of StructureComponent objects it will
 *  accept or reject. The "type" method should return the
 *  StructureComponentRegistry TYPE that it will process (this will also
 *  automatically limit the type of StructureComponent objects passed to
 *  its "accept" method). The "accept" method should return TRUE if the given
 *  StructureComponent object should be accepted or FALSE if it should be
 *  rejected.
 *  <P>
 *  For example, to define a filter which filters Atom components
 *  which only accepts C-Alpha atoms, one could declare the following
 *  class (in this example, its declared as an internal class):
 *  <P>
 *  <PRE>
 *     StructureComponentFilter filter = new StructureComponentFilter( )
 *     {
 *         public String type( )
 *         {
 *             return StructureComponentRegistry.TYPE_ATOM;
 *         }
 *
 *         public boolean accept( StructureComponent sc )
 *         {
 *             return ((Atom)sc).name.startsWith( "CA" );
 *         }
 *     };
 *     StructureComponentIterator iterator =
 *        structure.getStructureComponentIterator( filter );
 *  </PRE>
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.filters.IStructureComponentFilter
 *  @see	org.rcsb.mbt.model.Structure
 *  @see	org.rcsb.mbt.model.StructureComponent
 */
public class StructureComponentIterator
{
	private Structure structure;
	private IStructureComponentFilter filter;
	private ComponentType type;
	private int count;
	private int index;
	private boolean foundNext;
	private StructureComponent tmpStructureComponent;

	/**
	 *  Construct a StructureComponentIterator for the given structure
	 *  using the given filter to describe the desired subset.
	 *  <P>
	 */
	public StructureComponentIterator( final Structure structure, final IStructureComponentFilter filter )
		throws ClassNotFoundException, InstantiationException,
			IllegalAccessException
	{
		this.structure = structure;
		this.filter = filter;
		this.type = filter.type( );

		this.count = structure.getStructureComponentCount( this.type );
		this.index = 0;
		this.foundNext = false;
	}

	/**
	 *  Return true if the Iterator has more matches or false otherwise.
	 *  <P>
	 */
	public boolean hasMore( )
	{
		if ( this.foundNext ) {
			return true;
		}
		while ( this.index<this.count )
		{
			this.tmpStructureComponent =
				this.structure.getStructureComponentByIndex( this.type, this.index );
			if ( this.filter.accept( this.tmpStructureComponent ) )
			{
				this.foundNext = true;
				return true;
			}
			this.index++;
		}
		this.foundNext = false;
		return false;
	}

	/**
	 *  Returns the index of the next match or -1 if there are no more matches.
	 *  <P>
	 */
	public int nextIndex( )
	{
		if ( this.hasMore() ) {
			return this.index;
		} else {
			return -1;
		}
	}

	/**
	 *  Iterates through a set of StructureComponent objects.
	 *  <P>
	 */
	public void next( final StructureComponent structureComponent )
		throws NoSuchElementException
	{
		if ( this.type != structureComponent.getStructureComponentType() ) {
			throw new NoSuchElementException( "StructureComponentIterator.next: type mismatch" );
		}

		if ( this.hasMore( ) )
		{
			structureComponent.copy( this.tmpStructureComponent );
			this.index++;
			this.foundNext = false;
			return;
		}
	}
}

