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


import org.rcsb.mbt.model.*;


/**
 *  This class implements the AtomColor interface using B-Factor.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.attributes.IAtomColor
 *  @see	org.rcsb.mbt.model.Atom
 */
public class AtomColorByBFactor
	implements IAtomColor
{
	public static final String NAME = "By B-Factor";

	// Holds a singleton instance of this class.
	private static AtomColorByBFactor singleton = null;

	private final IColorMap colorMap =
		new InterpolatedColorMap( InterpolatedColorMap.COLD_TO_HOT );


	/**
	 *  The constructor is PRIVATE so that the "create" method
	 *  is used to produce a singleton instance of this class.
	 */
	private AtomColorByBFactor( )
	{
	}


	/**
	 *  Return the singleton instance of this class.
	 */
	public static AtomColorByBFactor create( )
	{
		if ( AtomColorByBFactor.singleton == null ) {
			AtomColorByBFactor.singleton = new AtomColorByBFactor( );
		}
		return AtomColorByBFactor.singleton;
	}


	/**
	 *  Produce a fixed color for the Atom.
	 */
	public void getAtomColor( final Atom atom, final float[] color )
	{
		if ( atom == null ) {
			throw new NullPointerException( "null atom" );
		}
		if ( color == null ) {
			throw new NullPointerException( "null color" );
		}

		final float bfactor = atom.bfactor / 100.0f;  // Normalize range
		this.colorMap.getColor( bfactor, color );
	}
}

