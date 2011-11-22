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
package org.rcsb.mbt.model.util;


/**
 *  A container object to hold general information about a single amino acid.
 *  It does contain information about a specific instance (see Residue).
 *  The well known amino acids as well as non-standard amino acids are
 *  generated and stored by the AminoAcidInfo class.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.Residue
 */
/*
 *  Hmm.  This is a growing list and we don't use all of them at the same time.
 *  Some sort of lazy creation scheme would save resources.
 *
 *  30-Oct-08 - rickb
 */
public class AminoAcid
{
	public String letter = null;           // eg: "A"
	public String code = null;             // eg: "ASX"
	public String name = null;             // eg: "Asparagine"
	public float hydrophobicity = 0.5f;    // eg: 0.0 - 1.0
	public float molecularWeight = 0.0f;   // eg: g

	/**
	 *  Construct an AminoAcid object from the given attribute values.
	 */
	public AminoAcid(
		final String letter,
		final String code,
		final String name,
		final float hydrophobicity,
		final float molecularWeight
	)
	{
		this.letter = letter;
		this.code = code;
		this.name = name;
		this.hydrophobicity = hydrophobicity;
		this.molecularWeight = molecularWeight;
	}

	/**
	 *  Return a String representation of this object.
	 */
	
	public String toString( )
	{
		return "{ " +
			"letter=" + this.letter + ", " +
			"code=" + this.code + ", " +
			"name=" + this.name + ", " +
			"hydrophobicity=" + this.hydrophobicity + ", " +
			"molecularWeight=" + this.molecularWeight + " }";
	}
}

