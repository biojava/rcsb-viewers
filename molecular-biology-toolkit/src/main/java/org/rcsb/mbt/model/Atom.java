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
 *  Implements a StructureComponent container for atom data.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.StructureComponent
 *  @see	org.rcsb.mbt.model.Structure
 */
public class Atom
	extends StructureComponent
	implements java.lang.Cloneable
{

	//
	// Constructor
	//

	/**
	 *  Creates a new empty Atom object.
	 */
	public Atom( )
	{
	}
	
	/**
	 * Copy Constructor
	 * @param src
	 */
	public Atom(Atom src)
	{
		copyAtomMembers(src);
	}

	//
	// StructureComponent methods
	//

	/**
	 *  Copy all of the field values from the parameter object into "this".
	 */
	public void copy( final StructureComponent structureComponent )
	{
		this.setStructure( structureComponent.getStructure() );
		copyAtomMembers((Atom) structureComponent);
	}
	
	protected void copyAtomMembers(final Atom atom)
	{
		this.element       = atom.element;
		this.name          = atom.name;
		this.number        = atom.number;
		this.altLoc        = atom.altLoc;
		this.compound      = atom.compound;
		this.chain_id      = atom.chain_id;
		this.entity_id     = atom.entity_id;
		this.authorChain_id = atom.authorChain_id;
		this.residue_id    = atom.residue_id;
		this.authorResidue_id    = atom.authorResidue_id;
		this.insertionCode = atom.insertionCode;
		this.coordinate[0] = atom.coordinate[0];
		this.coordinate[1] = atom.coordinate[1];
		this.coordinate[2] = atom.coordinate[2];
		this.occupancy     = atom.occupancy;
		this.bfactor       = atom.bfactor;
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
	 *  
	 *  Notes:
	 *  Interesting notion - I don't see where it's used to actually create
	 *  an instance from the classname.  Seems storing simpleName and renaming
	 *  this to just getName() would work just as well and relieve a lot of
	 *  complication in StructureStyles.
	 *  
	 *  Leaving it, though.  Revisit later.
	 *  
	 *  rickb - 07-Aug-08
	 */
	public static String getClassName()
	{
		return Atom.class.getName();
	}

	/**
	 *  This method returns the fully qualified name of this class.
	 */
	public ComponentType getStructureComponentType( )
	{
		return ComponentType.ATOM;
	}

	//
	// Atom fields
	//

	/**
	 *  The element symbol.
	 *  For example, "C", "O", "N", "H", etc.
	 */
	public String element = ""; // _atom_site.type_symbol

	/**
	 *  The macromolecular ID (element name).
	 *  For example, "CA", "CB", "OG1", "N", etc.
	 */
	public String name = ""; // _atom_site.label_atom_id

	/**
	 *  The atom serial number.
	 */
	public int number = -1; // _atom_site.id

	/**
	 *  Compound 3-letter code.
	 *  For example, "VAL", "THR", "ILE", etc.
	 */
	public String compound = ""; // _atom_site.label_comp_id


	/**
	 *  Chain/Asymm unit ID.
	 *  For example, "A", "B", "C", etc.
	 */
	public String chain_id = ""; // _atom_site.label_asym_id
	
	
	/**
	 * Entity ID
	 */
	
	public int entity_id = 0;
	
	/**
	 *  Chain/Author assigned unit ID.
	 *  For example, "A", "B", "C", etc.
	 */
	public String authorChain_id = ""; // _atom_site.author_asym_id
	
	/**
	 *  Residue/Sequence ID.
	 *  Must be a positive integer.
	 *  For example, "10", "11", "13", etc.
	 */
	public int residue_id = -1; // _atom_site.label_seq_id
	
	/**
	 *  Residue/Author assigned Sequence ID.
	 *  Must be a positive integer.
	 *  For example, "10", "11", "13", etc.
	 */
	public int authorResidue_id = -1; // _atom_site.author_seq_id

	/**
	 *  Insertion Code.
	 *  For example, "A", "B", "C", etc.
	 */
	public String insertionCode = ""; // _atom_site.pdbx_PDB_ins_code

	/**
	 *  The x,y,z coordinate in angstroms.
	 *  For example, (21.023, 30.128, 12.340).
	 */
	public double coordinate[] = { 0.0f, 0.0f, 0.0f }; // _atom_site.Cartn_x,y,z

	/**
	 *  The fraction (0.0-1.0) of a given atom name that occurs in a residue
	 *  having alternate atom locations.
	 *  For example, "1.0", "0.5", "0.33", etc.
	 *  <P>
	 *  @see #altLoc
	 *  <P>
	 */
	public float occupancy = 0.0f; // _atom_site.occupancy

	/**
	 *  Alternate Location Identifier (single character)
	 *  should be specified when occupancy < 1.
	 *  For example, "", "A", "B", "C", etc.
	 *  <P>
	 *  @see #occupancy
	 *  <P>
	 */
	public String altLoc = ""; // _atom_site.label_alt_id

	/**
	 *  Temperature B-Factor (0.0 - 100.0).
	 *  For example, "17.12", "26.28", etc.
	 */
	public float bfactor = 0.0f; // _atom_site.B_iso_or_equiv

	/**
	 *  Partial Charge (-1.0 - 3.0).
	 *  For example, "0.3", "1.1", etc.
	 */
	public float partialCharge = 0.0f; // _atom_site.???
	
	/**
	 *  Non-polymer indicator. Atom is part of a residue that
	 *  is not in a polymer, i.e. a ligand atom
	 */
	public Boolean nonpolymer = null;
	
	/**
	 * Return true if author chain id, author residue id and atom name match. Note, this is only
	 * a partial match of atom attributes, for example it does not check for insertion code!
	 * @param authorChainId
	 * @param authorResidueId
	 * @param name
	 * @return
	 */
	public boolean partialEquals(String authorChainId, int authorResidueId, String name, String compId, String insertionCode, String altId) {
		return this.authorChain_id.equals(authorChainId) && this.authorResidue_id == authorResidueId &&
				this.name.equals(name) && this.compound.equals(compId) && this.insertionCode.equals(insertionCode) &&
				this.altLoc.equals(altId);
	}
}

