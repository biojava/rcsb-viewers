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
package org.rcsb.mbt.model.interim;

import org.rcsb.mbt.model.StructureComponent;


/**
 *  Implements a an abstract StructureComponent container for conformation
 *  (secondary structure) data.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.Structure
 *  @see	org.rcsb.mbt.model.StructureComponent
 */
public abstract class Conformation
	extends StructureComponent
	implements java.lang.Cloneable
{
	//
	// StructureComponent methods
	//

	/**
	 *  Copy all of the field values from the parameter object into "this".
	 */
	
	public void copy( final StructureComponent structureComponent )
	{
		final Conformation conformation = (Conformation) structureComponent;
		this.name           = conformation.name;
		this.start_compound = conformation.start_compound;
		this.start_chain    = conformation.start_chain;
		this.start_residue  = conformation.start_residue;
		this.start_insertionCode = conformation.start_insertionCode;
		this.end_compound   = conformation.end_compound;
		this.end_chain      = conformation.end_chain;
		this.end_residue    = conformation.end_residue;
		this.end_insertionCode = conformation.end_insertionCode;
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
	 *  This method returns the fully qualified name of this class.
	 *  <P>
	 *  This name is used by the StructureComponentRegistry class to enable
	 *  dynamic registration and discovery of new StructureComponent
	 *  sub-classes/types. The name is also used to create a unique integer
	 *  indentifier for each type in order to make run-time type comparisons
	 *  fast.
	 */
	private static String className = null;
	public static String getClassName()
	{
		if ( Conformation.className == null ) {
			Conformation.className = ((new Throwable()).getStackTrace())[0].getClassName();
		}
		return Conformation.className;
	}

	//
	// Conformation fields
	//

	/**
	 *  For example, "helix_BA", "turn_1A"
	 */
	public String name = null;

	/**
	 *  For example, "ALA", "GLU", "LYS", etc.
	 */
	public String start_compound = null;

	/**
	 *  This value corresponds to the start Atom.chain_id value.
	 *  For example, "A", "B", "D", etc.
	 */
	public String start_chain = null;

	/**
	 *  This value corresponds to the Atom.residue_id value.
	 *  For example, 22, 27, etc.
	 */
	public int start_residue = -1;
	
	/**
	 *  This value corresponds to the Atom.insertionCode value.
	 *  For example, A, B, etc.
	 */
	public String start_insertionCode = "";

	/**
	 *  For example, "ALA", "GLU", "LYS", etc.
	 */
	public String end_compound = null;

	/**
	 *  This value corresponds to the end Atom.chain_id value.
	 *  For example, "A", "B", "D", etc.
	 */
	public String end_chain = null;

	/**
	 *  This value corresponds to the end Atom.residue_id value.
	 *  For example, 27, 137, etc.
	 */
	public int end_residue = -1;
	
	/**
	 *  This value corresponds to the end Atom.insertionCode value.
	 *  For example, A, B, etc.
	 */
	public String end_insertionCode = "";
}

