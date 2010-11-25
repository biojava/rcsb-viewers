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


import org.rcsb.mbt.model.util.PeriodicTable;


/**
 *  Provides a static (but run-time modifiable) list of color and radius
 *  values for each element in the periodic table.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.attributes.AtomColorByElement
 *  @see	org.rcsb.mbt.model.util.Element
 *  @see	org.rcsb.mbt.model.util.PeriodicTable
 *  @see	org.rcsb.mbt.model.StructureModel
 */
public class ElementStyles
{
	private static float elementColors[][];
	private static float elementRadii[];
	private static int elementCount = 0;

	static
	{
		ElementStyles.elementCount = PeriodicTable.getElementCount( );
		ElementStyles.elementColors = new float[ElementStyles.elementCount][3];
		ElementStyles.elementRadii = new float[ElementStyles.elementCount];

		//
		// Element Colors
		//

		// Initially set element colors to a medium gray
		for ( int i=0; i<ElementStyles.elementCount; i++ ) {
			ElementStyles.setElementColor( i, 0.7f, 0.7f, 0.7f );
		}

		// Now, override the default color assignments
		ElementStyles.setElementColor(  1, 1.00f, 1.00f, 1.00f );  // Hydrogen
		ElementStyles.setElementColor(  2, 0.87f, 1.00f, 1.00f );  // Helium
		ElementStyles.setElementColor(  3, 0.81f, 0.48f, 1.00f );  // Lithium
		ElementStyles.setElementColor(  4, 0.78f, 1.00f, 0.00f );  // Beryllium
		ElementStyles.setElementColor(  5, 1.00f, 0.71f, 0.71f );  // Boron
		ElementStyles.setElementColor(  6, 0.00f, 1.00f, 0.00f );  // Carbon
		ElementStyles.setElementColor(  7, 0.00f, 0.00f, 1.00f );  // Nitrogen
		ElementStyles.setElementColor(  8, 1.00f, 0.00f, 0.00f );  // Oxygen
		ElementStyles.setElementColor(  9, 0.71f, 1.00f, 1.00f );  // Flourine
		ElementStyles.setElementColor( 10, 0.67f, 0.91f, 0.97f );  // Neon
		ElementStyles.setElementColor( 11, 0.67f, 0.35f, 0.97f );  // Sodium
		ElementStyles.setElementColor( 12, 0.55f, 1.00f, 0.00f );  // Magnesium
		ElementStyles.setElementColor( 13, 0.84f, 0.65f, 0.65f );  // Aluminum
		ElementStyles.setElementColor( 14, 0.52f, 0.61f, 0.61f );  // Silicon
		ElementStyles.setElementColor( 15, 1.00f, 0.52f, 0.00f );  // Phosphorus
		ElementStyles.setElementColor( 16, 1.00f, 0.81f, 0.19f );  // Sulfur
		ElementStyles.setElementColor( 17, 0.13f, 0.97f, 0.13f );  // Chlorine
		ElementStyles.setElementColor( 18, 0.51f, 0.84f, 0.91f );  // Argon
		ElementStyles.setElementColor( 19, 0.55f, 0.26f, 0.84f );  // Potassium
		ElementStyles.setElementColor( 20, 0.22f, 1.00f, 0.00f );  // Calcium
		ElementStyles.setElementColor( 21, 0.88f, 0.88f, 0.88f );  // Sc
		ElementStyles.setElementColor( 22, 0.75f, 0.75f, 0.75f );  // Ti
		ElementStyles.setElementColor( 23, 0.63f, 0.63f, 0.66f );  // V
		ElementStyles.setElementColor( 24, 0.53f, 0.60f, 0.75f );  // Cr
		ElementStyles.setElementColor( 25, 0.60f, 0.47f, 0.75f );  // Mn
		ElementStyles.setElementColor( 26, 0.52f, 0.48f, 0.78f );  // Iron
		ElementStyles.setElementColor( 27, 0.44f, 0.47f, 0.75f );  // Co
		ElementStyles.setElementColor( 28, 0.34f, 0.47f, 0.75f );  // Ni
		ElementStyles.setElementColor( 29, 0.97f, 0.47f, 0.37f );  // Cu
		ElementStyles.setElementColor( 30, 0.47f, 0.50f, 0.66f );  // Zn
		ElementStyles.setElementColor( 31, 0.75f, 0.56f, 0.56f );  // Ga
		ElementStyles.setElementColor( 32, 0.37f, 0.56f, 0.56f );  // Ge
		ElementStyles.setElementColor( 33, 0.72f, 0.50f, 0.88f );  // As
		ElementStyles.setElementColor( 34, 0.97f, 0.63f, 0.00f );  // Se
		ElementStyles.setElementColor( 35, 0.63f, 0.16f, 0.16f );  // Br
		ElementStyles.setElementColor( 36, 0.35f, 0.72f, 0.82f );  // Kr
		ElementStyles.setElementColor( 37, 0.44f, 0.16f, 0.69f );  // Rb
		ElementStyles.setElementColor( 38, 0.00f, 0.97f, 0.00f );  // Sr
		ElementStyles.setElementColor( 39, 0.56f, 0.97f, 0.97f );  // Y
		ElementStyles.setElementColor( 40, 0.56f, 0.88f, 0.88f );  // Zr
		ElementStyles.setElementColor( 41, 0.44f, 0.75f, 0.78f );  // Nb
		ElementStyles.setElementColor( 42, 0.31f, 0.69f, 0.69f );  // Mo
		ElementStyles.setElementColor( 43, 0.22f, 0.60f, 0.66f );  // Tc
		ElementStyles.setElementColor( 44, 0.12f, 0.53f, 0.56f );  // Ru
		ElementStyles.setElementColor( 45, 0.03f, 0.47f, 0.53f );  // Rh
		ElementStyles.setElementColor( 46, 0.00f, 0.40f, 0.50f );  // Pd
		ElementStyles.setElementColor( 47, 0.60f, 0.75f, 0.97f );  // Ag
		ElementStyles.setElementColor( 48, 0.97f, 0.85f, 0.56f );  // Cd
		ElementStyles.setElementColor( 49, 0.67f, 0.44f, 0.44f );  // In
		ElementStyles.setElementColor( 50, 0.38f, 0.50f, 0.50f );  // Sn
		ElementStyles.setElementColor( 51, 0.60f, 0.38f, 0.69f );  // Sb
		ElementStyles.setElementColor( 52, 0.82f, 0.47f, 0.00f );  // Te
		ElementStyles.setElementColor( 53, 0.58f, 0.00f, 0.58f );  // Iodine
		ElementStyles.setElementColor( 54, 0.25f, 0.60f, 0.69f );  // Xe
		ElementStyles.setElementColor( 55, 0.31f, 0.10f, 0.53f );  // Cs
		ElementStyles.setElementColor( 56, 0.00f, 0.78f, 0.00f );  // Ba
		ElementStyles.setElementColor( 57, 0.44f, 0.85f, 0.97f );  // La
		ElementStyles.setElementColor( 58, 0.97f, 0.97f, 0.78f );  // Ce
		ElementStyles.setElementColor( 59, 0.85f, 0.97f, 0.78f );  // Pr
		ElementStyles.setElementColor( 60, 0.75f, 0.97f, 0.78f );  // Nd
		ElementStyles.setElementColor( 61, 0.63f, 0.97f, 0.78f );  // Pm
		ElementStyles.setElementColor( 62, 0.56f, 0.97f, 0.78f );  // Sm
		ElementStyles.setElementColor( 63, 0.38f, 0.97f, 0.78f );  // Eu
		ElementStyles.setElementColor( 64, 0.25f, 0.97f, 0.78f );  // Gd
		ElementStyles.setElementColor( 65, 0.19f, 0.97f, 0.78f );  // Tb
		ElementStyles.setElementColor( 66, 0.09f, 0.97f, 0.69f );  // Dy
		ElementStyles.setElementColor( 67, 0.00f, 0.97f, 0.60f );  // Ho
		ElementStyles.setElementColor( 68, 0.00f, 0.88f, 0.44f );  // Er
		ElementStyles.setElementColor( 69, 0.00f, 0.82f, 0.31f );  // Tm
		ElementStyles.setElementColor( 70, 0.00f, 0.75f, 0.22f );  // Yb
		ElementStyles.setElementColor( 71, 0.00f, 0.66f, 0.13f );  // Lu
		ElementStyles.setElementColor( 72, 0.28f, 0.75f, 0.97f );  // Hf
		ElementStyles.setElementColor( 73, 0.28f, 0.63f, 0.97f );  // Ta
		ElementStyles.setElementColor( 74, 0.13f, 0.56f, 0.82f );  // W
		ElementStyles.setElementColor( 75, 0.13f, 0.47f, 0.66f );  // Re
		ElementStyles.setElementColor( 76, 0.13f, 0.41f, 0.56f );  // Os
		ElementStyles.setElementColor( 77, 0.09f, 0.31f, 0.50f );  // Ir
		ElementStyles.setElementColor( 78, 0.09f, 0.35f, 0.56f );  // Pt
		ElementStyles.setElementColor( 79, 0.97f, 0.82f, 0.13f );  // Au
		ElementStyles.setElementColor( 80, 0.69f, 0.69f, 0.76f );  // Hg
		ElementStyles.setElementColor( 81, 0.63f, 0.31f, 0.28f );  // Tl
		ElementStyles.setElementColor( 82, 0.31f, 0.35f, 0.38f );  // Pb
		ElementStyles.setElementColor( 83, 0.60f, 0.28f, 0.69f );  // Bi
		ElementStyles.setElementColor( 84, 0.66f, 0.35f, 0.00f );  // Po
		ElementStyles.setElementColor( 85, 0.44f, 0.28f, 0.25f );  // At
		ElementStyles.setElementColor( 86, 0.25f, 0.50f, 0.56f );  // Rn
		ElementStyles.setElementColor( 87, 0.25f, 0.00f, 0.38f );  // Fr
		ElementStyles.setElementColor( 88, 0.00f, 0.47f, 0.00f );  // Ra
		ElementStyles.setElementColor( 89, 0.44f, 0.66f, 0.97f );  // Ac
		ElementStyles.setElementColor( 90, 0.00f, 0.72f, 0.97f );  // Th
		ElementStyles.setElementColor( 91, 0.00f, 0.63f, 0.97f );  // Pa
		ElementStyles.setElementColor( 92, 0.00f, 0.56f, 0.97f );  // U
		ElementStyles.setElementColor( 93, 0.00f, 0.50f, 0.94f );  // Np
		ElementStyles.setElementColor( 94, 0.00f, 0.41f, 0.94f );  // Pu
		ElementStyles.setElementColor( 95, 0.31f, 0.34f, 0.94f );  // Am
		ElementStyles.setElementColor( 96, 0.47f, 0.35f, 0.88f );  // Cm
		ElementStyles.setElementColor( 97, 0.53f, 0.28f, 0.88f );  // Bk
		ElementStyles.setElementColor( 98, 0.63f, 0.19f, 0.82f );  // Cf
		ElementStyles.setElementColor( 99, 0.69f, 0.09f, 0.82f );  // Es
		ElementStyles.setElementColor( 100, 0.69f, 0.09f, 0.72f );  // Fm
		ElementStyles.setElementColor( 101, 0.69f, 0.03f, 0.63f );  // Md
		ElementStyles.setElementColor( 102, 0.72f, 0.03f, 0.50f );  // No
		ElementStyles.setElementColor( 103, 0.78f, 0.00f, 0.38f );  // Lr
	/*
		TODO: Need to complete entry of element colors.
		setElementColor( 104, 0.00f, 0.00f, 0.00f );  // Rf
		setElementColor( 105, 0.00f, 0.00f, 0.00f );  // Db
		setElementColor( 106, 0.00f, 0.00f, 0.00f );  // Sg
		setElementColor( 107, 0.00f, 0.00f, 0.00f );  // Bh
		setElementColor( 108, 0.00f, 0.00f, 0.00f );  // Hs
		setElementColor( 109, 0.00f, 0.00f, 0.00f );  // Mt
		setElementColor( 110, 0.00f, 0.00f, 0.00f );  // Ds
		setElementColor( 111, 0.00f, 0.00f, 0.00f );  // Uuu
		setElementColor( 112, 0.00f, 0.00f, 0.00f );  // Uub
		setElementColor( 113, 0.00f, 0.00f, 0.00f );  // Uut
		setElementColor( 114, 0.00f, 0.00f, 0.00f );  // Uuq
		setElementColor( 115, 0.00f, 0.00f, 0.00f );  // Uup
		setElementColor( 116, 0.00f, 0.00f, 0.00f );  // Uuh
		setElementColor( 117, 0.00f, 0.00f, 0.00f );  // Uus
		setElementColor( 118, 0.00f, 0.00f, 0.00f );  // Uuo
	*/

		//
		// Element Radii
		//

		// Initially set element radii to 1.7
		for ( int i=0; i<ElementStyles.elementCount; i++ ) {
			ElementStyles.elementRadii[i] = 1.7f;
		}

		// Common elements get set to Van Der Waals Radii
		// http://www.ccdc.cam.ac.uk/prods/mercury/docs/mercury/PortableHTML/mercurydocn122.html
		ElementStyles.elementRadii[1]   = 1.20f;  // Hydrogen
		ElementStyles.elementRadii[2]   = 1.40f;  // Helium
		ElementStyles.elementRadii[3]   = 1.82f;  // Lithium
		ElementStyles.elementRadii[4]   = 2.00f;  // Beryllium
		ElementStyles.elementRadii[5]   = 2.00f;  // Boron
		ElementStyles.elementRadii[6]   = 1.70f;  // Carbon
		ElementStyles.elementRadii[7]   = 1.55f;  // Nitrogen
		ElementStyles.elementRadii[8]   = 1.52f;  // Oxygen
		ElementStyles.elementRadii[9]   = 1.47f;  // Fluorine
		ElementStyles.elementRadii[10]  = 1.54f;  // Neon
		ElementStyles.elementRadii[11]  = 2.27f;  // Sodium
		ElementStyles.elementRadii[12]  = 1.73f;  // Magnesium
		ElementStyles.elementRadii[13]  = 2.00f;  // Aluminum
		ElementStyles.elementRadii[14]  = 2.10f;  // Silicon
		ElementStyles.elementRadii[15]  = 1.80f;  // Phosphorus
		ElementStyles.elementRadii[16]  = 1.80f;  // Sulfur
		ElementStyles.elementRadii[17]  = 1.75f;  // Chlorine
		ElementStyles.elementRadii[18]  = 1.88f;  // Argon
		ElementStyles.elementRadii[19]  = 2.75f;  // Potassium
		ElementStyles.elementRadii[20]  = 2.00f;  // Calcium
		// non-contiguous
		ElementStyles.elementRadii[28]  = 1.63f;  // Nickel
		ElementStyles.elementRadii[29]  = 1.40f;  // Copper
		ElementStyles.elementRadii[30]  = 1.39f;  // Zinc
		ElementStyles.elementRadii[31]  = 1.87f;  // Gallium
		ElementStyles.elementRadii[33]  = 1.85f;  // Arsenic
		ElementStyles.elementRadii[34]  = 1.90f;  // Selenium
		ElementStyles.elementRadii[35]  = 1.85f;  // Bromine
		ElementStyles.elementRadii[36]  = 2.02f;  // Krypton
		ElementStyles.elementRadii[46]  = 1.63f;  // Palladium
		ElementStyles.elementRadii[47]  = 1.72f;  // Silver
		ElementStyles.elementRadii[48]  = 1.58f;  // Cadmium
		ElementStyles.elementRadii[49]  = 1.93f;  // Indium
		ElementStyles.elementRadii[50]  = 2.17f;  // Tin
		ElementStyles.elementRadii[52]  = 2.06f;  // Tellurium
		ElementStyles.elementRadii[53]  = 1.98f;  // Iodine
		ElementStyles.elementRadii[54]  = 2.16f;  // Xenon
		ElementStyles.elementRadii[78]  = 1.72f;  // Platinum
		ElementStyles.elementRadii[79]  = 1.66f;  // Gold
	/*
		TODO: Need to complete entry of element radii.
		elementRadii[80]  = 1.55;  // Mercury
		elementRadii[81]  = 1.96;  // Thallium
		elementRadii[82]  = 2.02;  // Lead
		elementRadii[92]  = 1.86;  // Uranium
	*/
	};

	public static float getMaxRadius() {
		float max = -1;
		for(int i = 0; i < ElementStyles.elementRadii.length; i++) {
			if(ElementStyles.elementRadii[i] > max) {
				max = ElementStyles.elementRadii[i];
			}
		}
		
		return max;
	}
	
	/**
	 * Get the number of elements for which style information is
	 * represented by this class.
	 */
	public static int getElementCount( )
	{
		return ElementStyles.elementCount;
	}

	//
	// Color methods
	//

	/**
	 * Set the RGB color of the given element number.
	 * Color components are clamped between 0.0 and 1.0.
	 */
	public static void setElementColor( final int number, final float r_, final float g_, final float b_ )
	{
		float r = r_;
		float b = b_;
		float g = g_;
		if ( r < 0.0f ) {
			r = 0.0f;
		}
		if ( r > 1.0f ) {
			r = 1.0f;
		}
		if ( g < 0.0f ) {
			g = 0.0f;
		}
		if ( g > 1.0f ) {
			g = 1.0f;
		}
		if ( b < 0.0f ) {
			b = 0.0f;
		}
		if ( b > 1.0f ) {
			b = 1.0f;
		}
		ElementStyles.elementColors[number][0] = r;
		ElementStyles.elementColors[number][1] = g;
		ElementStyles.elementColors[number][2] = b;
	}

	/**
	 * Set the RGB color of the given element name.
	 * Color components are clamped between 0.0 and 1.0.
	 */
	public static void setElementColor( final String name, final float r, final float g, final float b )
	{
		ElementStyles.setElementColor( PeriodicTable.getElementNumber(name), r, g, b );
	}

	/**
	 * Get the RGB color of the given element number.
	 * Color components are clamped between 0.0 and 1.0.
	 */
	public static float[] getElementColor( final int number )
	{
		if ( number <= 0 ) {
			return null;
		}
		if ( number >= ElementStyles.elementColors.length ) {
			return null;
		}
		return ElementStyles.elementColors[number];
	}

	/**
	 * Get the RGB color of the given element name.
	 * Color components are clamped between 0.0 and 1.0.
	 */
	public static float[] getElementColor( final String name )
	{
		final int number = PeriodicTable.getElementNumber( name );
		if ( number <= 0 ) {
			return null;
		}
		if ( number >= ElementStyles.elementColors.length ) {
			return null;
		}
		return ElementStyles.elementColors[ number ];
	}

	//
	// Radius methods
	//

	/**
	 * Set the radius of the given element number.
	 * Radius values are constrained to be non-negative.
	 */
	public static void setElementRadius( final int number, final float radius_ )
	{
		float radius = radius_;
		if ( radius < 0.0f ) {
			radius = 0.0f;
		}
		ElementStyles.elementRadii[number] = radius;
	}

	/**
	 * Set the radius of the given element name.
	 * Radius values are constrained to be non-negative.
	 */
	public static void setElementRadius( final String name, final float radius )
	{
		ElementStyles.setElementRadius( PeriodicTable.getElementNumber(name), radius );
	}

	/**
	 * Get the radius of the given element number.
	 * Radius values are constrained to be non-negative.
	 */
	public static float getElementRadius( final int number )
	{
		return ElementStyles.elementRadii[number];
	}

	/**
	 * Get the radius of the given element name.
	 * Radius values are constrained to be non-negative.
	 */
	public static float getElementRadius( final String name )
	{
		return ElementStyles.elementRadii[ PeriodicTable.getElementNumber(name) ];
	}
}

