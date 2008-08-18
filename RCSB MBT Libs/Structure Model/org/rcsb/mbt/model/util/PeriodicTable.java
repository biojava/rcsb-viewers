//  $Id: PeriodicTable.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
//
//  Copyright 2000-2004 The Regents of the University of California.
//  All Rights Reserved.
//
//  Permission to use, copy, modify and distribute any part of this
//  Molecular Biology Toolkit (MBT)
//  for educational, research and non-profit purposes, without fee, and without
//  a written agreement is hereby granted, provided that the above copyright
//  notice, this paragraph and the following three paragraphs appear in all
//  copies.
//
//  Those desiring to incorporate this MBT into commercial products
//  or use for commercial purposes should contact the Technology Transfer &
//  Intellectual Property Services, University of California, San Diego, 9500
//  Gilman Drive, Mail Code 0910, La Jolla, CA 92093-0910, Ph: (858) 534-5815,
//  FAX: (858) 534-7345, E-MAIL:invent@ucsd.edu.
//
//  IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR
//  DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING
//  LOST PROFITS, ARISING OUT OF THE USE OF THIS MBT, EVEN IF THE
//  UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//
//  THE MBT PROVIDED HEREIN IS ON AN "AS IS" BASIS, AND THE
//  UNIVERSITY OF CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT,
//  UPDATES, ENHANCEMENTS, OR MODIFICATIONS. THE UNIVERSITY OF CALIFORNIA MAKES
//  NO REPRESENTATIONS AND EXTENDS NO WARRANTIES OF ANY KIND, EITHER IMPLIED OR
//  EXPRESS, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
//  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE, OR THAT THE USE OF THE
//  MBT WILL NOT INFRINGE ANY PATENT, TRADEMARK OR OTHER RIGHTS.
//
//  For further information, please see:  http://mbt.sdsc.edu
//
//  History:
//  $Log: PeriodicTable.java,v $
//  Revision 1.1  2007/02/08 02:38:52  jbeaver
//  version 1.50
//
//  Revision 1.1  2006/09/20 16:50:43  jbeaver
//  first commit - branched from ProteinWorkshop
//
//  Revision 1.1  2006/08/24 17:39:03  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2006/03/09 00:18:55  jbeaver
//  Initial commit
//
//  Revision 1.6  2004/04/09 00:15:21  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.5  2004/01/29 17:29:07  moreland
//  Updated copyright and class block comments.
//
//  Revision 1.4  2003/11/20 21:38:36  moreland
//  Completed the entry of all elements (1-118) of the known periodic table.
//
//  Revision 1.3  2003/03/19 22:22:57  moreland
//  Now supports mixed, upper, and lower case lookups of element symbols.
//
//  Revision 1.2  2003/02/27 21:03:21  moreland
//  Fixed bug in 0th place-holder Element (had nulls that broke hashtable).
//
//  Revision 1.1  2003/02/19 02:37:44  moreland
//  Added basic atomic element and periodic table information classes.
//
//  Revision 1.0  2003/02/18 18:06:54  moreland
//  First version.
//


package org.rcsb.mbt.model.util;


import java.util.Hashtable;


/**
 *  Provides a container object to hold chemical Element objects.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.util.Element
 */
public class PeriodicTable
{
	private static final String SBLOCK = "s-block";
	private static final String PBLOCK = "p-block";
	private static final String DBLOCK = "d-block";
	private static final String FBLOCK = "f-block";

	private static final Element elements[] =
	{
		// Element object fields:
		//    name, symbol, atomic_number, atomic_weight, group_number,
		//    group_name, period_number, block

		// First entry is placeholder so the index matches the atomic number
		new Element( "xxx", "xxx", 0, 0.0, 0, "x", 0, "x" ),

		new Element( "Hydrogen", "H", 1, 1.00794, 1, "-", 1, PeriodicTable.SBLOCK ),
		new Element( "Helium", "He", 2, 4.002602, 18, "Noble gas", 1, PeriodicTable.PBLOCK ),
		new Element( "Lithium", "Li", 3, 6.941, 1, "Alkali metal", 2, PeriodicTable.SBLOCK ),
		new Element( "Beryllium", "Be", 4, 9.012182, 2, "Alkaline earth metal", 2, PeriodicTable.SBLOCK ),

		new Element( "Boron", "B", 5, 10.811, 13, "-", 2, PeriodicTable.PBLOCK ),
		new Element( "Carbon", "C", 6, 12.0107, 14, "-", 2, PeriodicTable.PBLOCK ),
		new Element( "Nitrogen", "N", 7, 14.0067, 15, "Pnictogen", 2, PeriodicTable.PBLOCK ),
		new Element( "Oxygen", "O", 8, 15.9994, 16, "Chalcogen", 2, PeriodicTable.PBLOCK ),
		new Element( "Fluorine", "F", 9, 18.9984032, 17, "Halogen", 2, PeriodicTable.PBLOCK ),
		new Element( "Neon", "Ne", 10, 20.1797, 18, "Noble gas", 2, PeriodicTable.PBLOCK ),
		new Element( "Sodium", "Na", 11, 22.989770, 1, "Alkali metal", 3, PeriodicTable.SBLOCK ),
		new Element( "Magnesium", "Mg", 12, 24.3050, 2, "Alkaline earth metal", 3, PeriodicTable.SBLOCK ),
		new Element( "Aluminum", "Al", 13, 26.981538, 13, "-", 3, PeriodicTable.PBLOCK ),
		new Element( "Silicon", "Si", 14, 28.0855, 14, "-", 3, PeriodicTable.PBLOCK ),
		new Element( "Phosphorus", "P", 15, 30.973761, 15, "Pnictogen", 3, PeriodicTable.PBLOCK ),
		new Element( "Sulfur", "S", 16, 32.065, 16, "Chalcogen", 3, PeriodicTable.PBLOCK ),
		new Element( "Chlorine", "Cl", 17, 35.453, 17, "Halogen", 3, PeriodicTable.PBLOCK ),
		new Element( "Argon", "Ar", 18, 39.948, 18, "Noble gas", 3, PeriodicTable.PBLOCK ),
		new Element( "Potassium", "K", 19, 39.0983, 1, "Alkali metal", 4, PeriodicTable.SBLOCK ),
		new Element( "Calcium", "Ca", 20, 40.078, 2, "Alkaline earth metal", 4, PeriodicTable.SBLOCK ),
		new Element( "Scandium", "Sc", 21, 44.955910, 3, "-", 4, PeriodicTable.DBLOCK ),
		new Element( "Titanium", "Ti", 22, 47.867, 4, "-", 4, PeriodicTable.DBLOCK ),
		new Element( "Vanadium", "V", 23, 50.9415, 5, "-", 4, PeriodicTable.DBLOCK ),
		new Element( "Chromium", "Cr", 24, 51.9961, 6, "-", 4, PeriodicTable.DBLOCK ),
		new Element( "Manganese", "Mn", 25, 54.938049, 7, "-", 4, PeriodicTable.DBLOCK ),
		new Element( "Iron", "Fe", 26, 55.845, 8, "-", 4, PeriodicTable.DBLOCK ),
		new Element( "Cobalt", "Co", 27, 58.9332, 9, "-", 4, PeriodicTable.DBLOCK ),
		new Element( "Nickel", "Ni", 28, 58.6934, 10, "-", 4, PeriodicTable.DBLOCK ),
		new Element( "Copper", "Cu", 29, 63.546, 11, "Coinage metal", 4, PeriodicTable.DBLOCK ),
		new Element( "Zinc", "Zn", 30, 65.409, 12, "-", 4, PeriodicTable.DBLOCK ),
		new Element( "Gallium", "Ga", 31, 69.723, 13, "-", 4, PeriodicTable.PBLOCK ),
		new Element( "Germanium", "Ge", 32, 72.64, 14, "-", 4, PeriodicTable.PBLOCK ),
		new Element( "Arsenic", "As", 33, 74.9216, 15, "Pnictogen", 4, PeriodicTable.PBLOCK ),
		new Element( "Selenium", "Se", 34, 78.96, 16, "Chalcogen", 4, PeriodicTable.PBLOCK ),
		new Element( "Bromine", "Br", 35, 79.904, 17, "Halogen", 4, PeriodicTable.PBLOCK ),
		new Element( "Krypton", "Kr", 36, 83.798, 18, "Noble gas", 4, PeriodicTable.PBLOCK ),
		new Element( "Rubidium", "Rb", 37, 85.4678, 1, "Alkali metal", 5, PeriodicTable.SBLOCK ),
		new Element( "Strontium", "Sr", 38, 87.62, 2, "Alkaline earth metal", 5, PeriodicTable.SBLOCK ),
		new Element( "Yttrium", "Y", 39, 88.90585, 3, "-", 5, PeriodicTable.DBLOCK ),
		new Element( "Zirconium", "Zr", 40, 91.224, 4, "-", 5, PeriodicTable.DBLOCK ),
		new Element( "Niobium", "Nb", 41, 92.90638, 5, "-", 5, PeriodicTable.DBLOCK ),
		new Element( "Molybdenum", "Mo", 42, 95.94, 6, "-", 5, PeriodicTable.DBLOCK ),
		new Element( "Technetium", "Tc", 43, 98.0, 7, "-", 5, PeriodicTable.DBLOCK ),
		new Element( "Ruthenium", "Ru", 44, 101.07, 8, "Precious metal", 5, PeriodicTable.DBLOCK ),
		new Element( "Rhodium", "Rh", 45, 102.9055, 9, "Precious metal", 5, PeriodicTable.DBLOCK ),
		new Element( "Palladium", "Pd", 46, 106.42, 10, "Precious metal", 5, PeriodicTable.DBLOCK ),
		new Element( "Silver", "Ag", 47, 107.8682, 11, "Coinage metal", 5, PeriodicTable.DBLOCK ),
		new Element( "Cadmium", "Cd", 48, 112.411, 12, "-", 5, PeriodicTable.DBLOCK ),
		new Element( "Indium", "In", 49, 114.818, 13, "-", 5, PeriodicTable.PBLOCK ),
		new Element( "Tin", "Sn", 50, 118.710, 14, "-", 5, PeriodicTable.PBLOCK ),
		new Element( "Antimony", "Sb", 51, 121.76, 15, "Pnictogen", 5, PeriodicTable.PBLOCK ),
		new Element( "Tellurium", "Te", 52, 127.6, 16, "Chalcogen", 5, PeriodicTable.PBLOCK ),
		new Element( "Iodine", "I", 53, 126.90447, 17, "Halogen", 5, PeriodicTable.PBLOCK ),
		new Element( "Xenon", "Xe", 54, 131.293, 18, "Noble gas", 5, PeriodicTable.PBLOCK ),
		new Element( "Cesium", "Cs", 55, 132.90545, 1, "Alkali metal", 6, PeriodicTable.SBLOCK ),
		new Element( "Barium", "Ba", 56, 137.327, 2, "Alkaline earth metal", 6, PeriodicTable.SBLOCK ),
		new Element( "Lanthanum", "La", 57, 138.9055, -1, "Lanthanoid", 6, PeriodicTable.FBLOCK ),
		new Element( "Cerium", "Ce", 58, 140.116, -1, "Lanthanoid", 6, PeriodicTable.FBLOCK ),
		new Element( "Praseodymium", "Pr", 59, 140.90765, -1, "Lanthanoid", 6, PeriodicTable.FBLOCK ),
		new Element( "Neodymium", "Nd", 60, 144.24, -1, "Lanthanoid", 6, PeriodicTable.FBLOCK ),
		new Element( "Promethium", "Pm", 61, 145.0, -1, "Lanthanoid", 6, PeriodicTable.FBLOCK ),
		new Element( "Samarium", "Sm", 62, 150.36, -1, "Lanthanoid", 6, PeriodicTable.FBLOCK ),
		new Element( "Europium", "Eu", 63, 151.964, -1, "Lanthanoid", 6, PeriodicTable.FBLOCK ),
		new Element( "Gadolinium", "Gd", 64, 157.25, -1, "Lanthanoid", 6, PeriodicTable.FBLOCK ),
		new Element( "Terbium", "Tb", 65, 158.92534, -1, "Lanthanoid", 6, PeriodicTable.FBLOCK ),
		new Element( "Dysprosium", "Dy", 66, 162.5, -1, "Lanthanoid", 6, PeriodicTable.FBLOCK ),
		new Element( "Holmium", "Ho", 67, 164.93032, -1, "Lanthanoid", 6, PeriodicTable.FBLOCK ),
		new Element( "Erbium", "Er", 68, 167.259, -1, "Lanthanoid", 6, PeriodicTable.FBLOCK ),
		new Element( "Thulium", "Tm", 69, 168.93421, -1, "Lanthanoid", 6, PeriodicTable.FBLOCK ),
		new Element( "Ytterbium", "Yb", 70, 173.04, -1, "Lanthanoid", 6, PeriodicTable.FBLOCK ),
		new Element( "Lutetium", "Lu", 71, 174.967, 3, "-", 6, PeriodicTable.DBLOCK ),
		new Element( "Hafnium", "Hf", 72, 178.49, 4, "-", 6, PeriodicTable.DBLOCK ),
		new Element( "Tantalum", "Ta", 73, 180.9479, 5, "-", 6, PeriodicTable.DBLOCK ),
		new Element( "Tungsten", "W", 74, 183.84, 6, "-", 6, PeriodicTable.DBLOCK ),
		new Element( "Rhenium", "Re", 75, 186.207, 7, "-", 6, PeriodicTable.DBLOCK ),
		new Element( "Osmium", "Os", 76, 190.23, 8, "Precious metal", 6, PeriodicTable.DBLOCK ),
		new Element( "Iridium", "Ir", 77, 192.217, 9, "Precious metal", 6, PeriodicTable.DBLOCK ),
		new Element( "Platinum", "Pt", 78, 195.078, 10, "Precious metal", 6, PeriodicTable.DBLOCK ),
		new Element( "Gold", "Au", 79, 196.96655, 11, "Coinage metal", 6, PeriodicTable.DBLOCK ),
		new Element( "Mercury", "Hg", 80, 200.59, 12, "-", 6, PeriodicTable.DBLOCK ),
		new Element( "Thallium", "Tl", 81, 204.3833, 13, "-", 6, PeriodicTable.PBLOCK ),
		new Element( "Lead", "Pb", 82, 207.2, 14, "-", 6, PeriodicTable.PBLOCK ),
		new Element( "Bismuth", "Bi", 83, 208.98038, 15, "Pnictogen", 6, PeriodicTable.PBLOCK ),
		new Element( "Polonium", "Po", 84, 209.0, 16, "Chalcogen", 6, PeriodicTable.PBLOCK ),
		new Element( "Astatine", "At", 85, 210.0, 17, "Halogen", 6, PeriodicTable.PBLOCK ),
		new Element( "Radon", "Rn", 86, 222.0, 18, "Noble Gas", 6, PeriodicTable.PBLOCK ),
		new Element( "Francium", "Fr", 87, 223.0, 1, "Alkali Metal", 7, PeriodicTable.SBLOCK ),
		new Element( "Radium", "Ra", 88, 226.0, 2, "Alkaline Earth Metal", 7, PeriodicTable.SBLOCK ),
		new Element( "Actinium", "Ac", 89, 227.0, -1, "Actinoid", 7, PeriodicTable.FBLOCK ),
		new Element( "Thorium", "Th", 90, 232.0381, -1, "Actinoid", 7, PeriodicTable.FBLOCK ),
		new Element( "Protactinium", "Pa", 91, 231.03588, -1, "Actinoid", 7, PeriodicTable.FBLOCK ),
		new Element( "Uranium", "U", 92, 238.02891, -1, "Actinoid", 7, PeriodicTable.FBLOCK ),
		new Element( "Neptunium", "Np", 93, 237.0, -1, "Actinoid", 7, PeriodicTable.FBLOCK ),
		new Element( "Plutonium", "Pu", 94, 244.0, -1, "Actdinoid", 7, PeriodicTable.FBLOCK ),
		new Element( "Americium", "Am", 95, 243.0, -1, "Actinoid", 7, PeriodicTable.FBLOCK ),
		new Element( "Curium", "Cm", 96, 247.0, -1, "Actinoid", 7, PeriodicTable.FBLOCK ),
		new Element( "Berkelium", "Bk", 97, 247.0, -1, "Actinoid", 7, PeriodicTable.FBLOCK ),
		new Element( "Californium", "Cf", 98, 251.0, -1, "Actinoid", 7, PeriodicTable.FBLOCK ),
		new Element( "Einsteinium", "Es", 99, 252.0, -1, "Actinoid", 7, PeriodicTable.FBLOCK ),
		new Element( "Fermium", "Fm", 100, 257.0, -1, "Actinoid", 7, PeriodicTable.FBLOCK ),
		new Element( "Mendelevium", "Md", 101, 258.0, -1, "Actinoid", 7, PeriodicTable.FBLOCK ),
		new Element( "Nobelium", "No", 102, 259.0, -1, "Actinoid", 7, PeriodicTable.FBLOCK ),
		new Element( "Lawrencium", "Lr", 103, 262.0, 3, "-", 7, PeriodicTable.DBLOCK ),
		new Element( "Rutherfordium", "Rf", 104, 261.0, 4, "-", 7, PeriodicTable.DBLOCK ),
		new Element( "Dubnium", "Db", 105, 262.0, 5, "-", 7, PeriodicTable.DBLOCK ),
		new Element( "Seaborgium", "Sg", 106, 266.0, 6, "-", 7, PeriodicTable.DBLOCK ),
		new Element( "Bohrium", "Bh", 107, 264.0, 7, "-", 7, PeriodicTable.DBLOCK ),
		new Element( "Hassium", "Hs", 108, 269.0, 8, "-", 7, PeriodicTable.DBLOCK ),
		new Element( "Meitnerium", "Mt", 109, 268.0, 9, "-", 7, PeriodicTable.DBLOCK ),
		new Element( "Darmstadtium", "Ds", 110, 271.0, 10, "-", 7, PeriodicTable.DBLOCK ),
		new Element( "Unununium", "Uuu", 111, 272.0, 11, "-", 7, PeriodicTable.DBLOCK ),
		new Element( "Ununbium", "Uub", 112, 285.0, 12, "-", 7, PeriodicTable.DBLOCK ),
		new Element( "Ununtrium", "Uut", 113, 0.0, 0, "-", -1, PeriodicTable.PBLOCK ),
		new Element( "Ununquadium", "Uuq", 114, 289.0, 14, "-", 7, PeriodicTable.PBLOCK ),
		new Element( "Ununpentium", "Uup", 115, 0.0, 0, "-", -1, PeriodicTable.PBLOCK ),
		new Element( "Ununhexium", "Uuh", 116, 292.0, 16, "Chalcogen", -1, PeriodicTable.PBLOCK ),
		new Element( "Ununseptium", "Uus", 117, 0.0, 0, "-", -1, PeriodicTable.PBLOCK ),
		new Element( "Ununoctium", "Uuo", 118, 0.0, 0, "-", -1, PeriodicTable.PBLOCK )
	};

	private static Hashtable symbolToElementHash = null;
	static
	{
		PeriodicTable.symbolToElementHash = new Hashtable( );
		for ( int i=0; i<PeriodicTable.elements.length; i++ )
		{
			// Mixed case
			PeriodicTable.symbolToElementHash.put( PeriodicTable.elements[i].symbol, PeriodicTable.elements[i] );
			// Upper case
			PeriodicTable.symbolToElementHash.put( PeriodicTable.elements[i].symbol.toUpperCase(), PeriodicTable.elements[i] );
			// Lower case
			PeriodicTable.symbolToElementHash.put( PeriodicTable.elements[i].symbol.toLowerCase(), PeriodicTable.elements[i] );
		}
	}

	//
	// Primary Methods
	//

	/**
	 *  Return the number of Elements stored in this table.
	 */
	public static int getElementCount( )
	{
		return PeriodicTable.elements.length;
	}

	/**
	 *  Get an Element by its atomic number.
	 */
	public static Element getElement( final int atomic_number )
	{
		if ( atomic_number < 1 ) {
			return null;
		}
		if ( atomic_number >= PeriodicTable.elements.length ) {
			return null;
		}
		return PeriodicTable.elements[atomic_number];
	}

	/**
	 *  Get an Element by its symbol.
	 */
	public static Element getElement( final String symbol )
	{
		return (Element) PeriodicTable.symbolToElementHash.get( symbol );
	}

	//
	// Utility Methods
	//

	/**
	 *  Get an Element name by its atomic number.
	 */
	public static String getElementName( final int atomic_number )
	{
		if ( atomic_number < 1 ) {
			return null;
		}
		if ( atomic_number >= PeriodicTable.elements.length ) {
			return null;
		}
		return PeriodicTable.elements[atomic_number].name;
	}

	/**
	 *  Get an Element name by its symbol.
	 */
	public static String getElementName( final String symbol )
	{
		final Element element = (Element) PeriodicTable.symbolToElementHash.get( symbol );
		if ( element == null ) {
			return null;
		}
		return element.name;
	}

	/**
	 *  Get an Element atomic number by its symbol.
	 */
	public static int getElementNumber( final String symbol )
	{
		final Element element = (Element) PeriodicTable.symbolToElementHash.get( symbol );
		if ( element == null ) {
			return -1;
		}
		return element.atomic_number;
	}

	/**
	 *  Get an Element atomic weight by its atomic number.
	 */
	public static double getElementWeight( final int atomic_number )
	{
		if ( atomic_number < 1 ) {
			return 0.0;
		}
		if ( atomic_number >= PeriodicTable.elements.length ) {
			return 0.0;
		}
		return PeriodicTable.elements[atomic_number].atomic_weight;
	}

	/**
	 *  Get an Element atomic weight by its symbol.
	 */
	public static double getElementWeight( final String symbol )
	{
		final Element element = (Element) PeriodicTable.symbolToElementHash.get( symbol );
		if ( element == null ) {
			return 0.0;
		}
		return element.atomic_weight;
	}
}

