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
package org.rcsb.mbt.model.filters;


import org.rcsb.mbt.model.StructureComponent;
import org.rcsb.mbt.model.StructureComponentRegistry.ComponentType;


/**
 *  Defines a standard interface to describe a filter implementation class.
 *  A filter is repsonsible for implementing two methods which should return
 *  values according to what subset of StructureComponent objects it will
 *  accept or reject. The "type" method should return the StructureComponentRegistry
 *  TYPE that it will process (this will also automatically limit the type of
 *  StructureComponent objects passed to its "accept" method). The "accept"
 *  method should return TRUE if the given StructureComponent object should
 *  be accepted or FALSE if it should be rejected.
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
 *         public boolean accept( StructureComponent structureComponent )
 *         {
 *             return ((Atom)structureComponent).name.startsWith( "CA" );
 *         }
 *     };
 *     StructureComponentIterator iterator =
 *        structure.getStructureComponentIterator( filter );
 *  </PRE>
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.Structure
 *  @see	org.rcsb.mbt.model.StructureComponent
 *  @see	org.rcsb.mbt.model.StructureComponentIterator
 */
public interface IStructureComponentFilter
{
	/**
	 *  Return the StructureComponent type that this filter will match
	 *  and return. See the StructureComponentRegistry class for valid TYPE
	 *  values.
	 */
	public ComponentType type( );

	/**
	 *  Return the StructureComponent type that this filter will match
	 *  and return. See the StructureComponentRegistry class for valid
	 *  TYPE values.
	 *  <P>
	 *  Note: because the "type" method will ensure that only
	 *  objects of the specified type will ever be passed to the "accept"
	 *  method, the StructureComponent object can always be safely
	 *  cast that type. For example, if the "type" method returns
	 *  TYPE_ATOM, an implementation of the "accept" method can always
	 *  safely cast the StructureComponent object to an Atom object.
	 *  <P>
	 */
	public boolean accept( StructureComponent structureComponent );
}

