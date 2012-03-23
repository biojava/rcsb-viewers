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
import org.rcsb.mbt.model.attributes.ElementStyles;
import org.rcsb.mbt.model.attributes.IAtomColor;
import org.rcsb.mbt.model.util.Element;
import org.rcsb.mbt.model.util.PeriodicTable;


/**
 *  This class implements the AtomColor interface by applying a color
 *  to the given atomIndex+Atom by using the ElementColor class.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.attributes.IAtomColor
 *  @see	org.rcsb.mbt.model.Atom
 *  @see	org.rcsb.mbt.model.util.PeriodicTable
 *  @see	org.rcsb.mbt.model.util.Element
 *  @see	org.rcsb.mbt.model.attributes.ElementStyles
 */
public class AtomColorByElementCarbonGray
	implements IAtomColor
{
	public static final String NAME = "By Element Carbon Gray";

	// Holds a singleton instance of this class.
	private static AtomColorByElementCarbonGray singleton = null;

	/**
	 *  The constructor is PRIVATE so that the "create" method
	 *  is used to produce a singleton instance of this class.
	 */
	private AtomColorByElementCarbonGray( )
	{
	}

	/**
	 *  Return the singleton instance of this class.
	 */
	public static AtomColorByElementCarbonGray create( )
	{
		if ( AtomColorByElementCarbonGray.singleton == null ) {
			AtomColorByElementCarbonGray.singleton = new AtomColorByElementCarbonGray( );
		}
		return AtomColorByElementCarbonGray.singleton;
	}

	/**
	 *  Produce a color based upon the atom element type.
	 */
	public void getAtomColor( final Atom atom, final float[] color )
	{
		final Element element = PeriodicTable.getElement( atom.element );
		if ( element == null )
		{
			// Unknown element - make it gray.
			color[0] = color[1] = color[2] = 0.5f;
			return;
		}
		final float rgb[] = ElementStyles.getElementColor( element.atomic_number );
		color[0] = rgb[0];
		color[1] = rgb[1];
		color[2] = rgb[2];
		
		if (element.atomic_number == 6) {
			color[0] = 0.8f;
			color[1] = 0.8f;
			color[2] = 0.8f;
		}
	}
}

