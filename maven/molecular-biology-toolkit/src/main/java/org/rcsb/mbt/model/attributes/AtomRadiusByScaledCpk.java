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
package org.rcsb.mbt.model.attributes;


import org.rcsb.mbt.model.Atom;
import org.rcsb.mbt.model.Residue;
import org.rcsb.mbt.model.attributes.ElementStyles;
import org.rcsb.mbt.model.attributes.IAtomRadius;
import org.rcsb.mbt.model.util.Element;
import org.rcsb.mbt.model.util.ExternReferences;
import org.rcsb.mbt.model.util.PeriodicTable;




/**
 *  This class implements the AtomRadius interface by applying a radius
 *  to the given an Atom by using the ElementRadius class.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.attributes.IAtomRadius
 *  @see	org.rcsb.mbt.model.Atom
 *  @see	org.rcsb.mbt.model.util.PeriodicTable
 *  @see	org.rcsb.mbt.model.util.Element
 *  @see	org.rcsb.mbt.model.attributes.ElementStyles
 */
public class AtomRadiusByScaledCpk
	implements IAtomRadius
{
	public static final String NAME = "By Scaled CPK (Ball-and-Stick)";

	// The scale factor applied to the CPK radius.
	private float scale = 0.2f;

	// Holds a singleton instance of this class.
	private static AtomRadiusByScaledCpk singleton = null;

	/**
	 *  The constructor is PRIVATE so that the "create" method
	 *  is used to produce a singleton instance of this class.
	 */
	private AtomRadiusByScaledCpk( )
	{
	}

	/**
	 *  Return the singleton instance of this class.
	 */
	public static AtomRadiusByScaledCpk create( )
	{
		if ( AtomRadiusByScaledCpk.singleton == null ) {
			AtomRadiusByScaledCpk.singleton = new AtomRadiusByScaledCpk( );
		}
		return AtomRadiusByScaledCpk.singleton;
	}

	/**
	 *  Produce a radius based upon the atom element type.
	 */
	public float getAtomRadius( final Atom atom )
	{
		final Element element = PeriodicTable.getElement( atom.element );
		if ( element == null ) {
			return this.scale * 1.0f; // Unknown element
		}
		float radius = this.scale * ElementStyles.getElementRadius( element.atomic_number );
		
		// display metal larger
		if (PeriodicTable.isMetal(element.atomic_number)) {
			radius = 0.5f * ElementStyles.getElementRadius( element.atomic_number );
		}
		
		/*
		 * TODO: Seems like the better way to do this is to construct
		 * a different style for amino acids with a different radius...
		 * 
		 * rickb - 23-May-08
		 */
		if (ExternReferences.isLigandExplorer())
		{
			final boolean isAminoAcid =
				atom.getStructure().getStructureMap().getResidue(atom).getClassification() == Residue.Classification.AMINO_ACID;
			return isAminoAcid ? radius / 2 : radius;
		}
		
		return radius;
	}

	/**
	 *  Set the CPK radius scale factor.
	 */
	public void setScale( final float scale )
	{
		this.scale = scale;
	}

	/**
	 *  Get the CPK radius scale factor.
	 */
	public float getScale( )
	{
		return this.scale;
	}
}

