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
import org.rcsb.mbt.model.interim.Conformation;

/**
 *  This class represents a relationship between the
 *  Conformation and the Atom StructureComponent class.
 *  It links the Conformation's start_residue and end_residue
 *  fields with the Atom's residue_id field.
 *  <P>
 *  This class is used by the StructureComponentRegistry
 *  to define the relationship model for StructureComponent objects.
 *  <P>
 *  @author John L. Moreland
 */
public class Relation_Conformation_Atom
	extends StructureComponentRelation
{
	/**
	 * Constructs a relation between Conformation and Atom objects.
	 * <P>
	 */
	public Relation_Conformation_Atom( )
	{
		super( "Relation_Conformation_Atom",
			ComponentType.RELATION_CONFORMATION_ATOMS, ComponentType.ATOM );
	}

	/**
	 * Defines how a Conformation object is linked to an Atom object.
	 * <P>
	 */
	
	protected boolean isLinked( final StructureComponent subject,
		final StructureComponent object )
	{
		// Cast the StructureComponent objects to the correct types.
		final Conformation conformation = (Conformation) subject;
		final Atom atom = (Atom) object;
	
		// Does the Atom's chain_id fall between the
		// Conformation's start_chain and end_chain values?
		if ( atom.chain_id.compareTo( conformation.start_chain) < 0 ) {
			return false;
		}
		if ( atom.chain_id.compareTo( conformation.end_chain ) > 0 ) {
			return false;
		}
		 
		// Does the Atom's residue_id fall between the
		// Conformation's start_residue and end_residue values?
		if ( atom.residue_id < conformation.start_residue ) {
			return false;
		}
		if ( atom.residue_id > conformation.end_residue ) {
			return false;
		}

		return true;
	}
}

