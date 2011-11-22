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
 *  A container object to hold information about an element
 *  from the periodic table.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.util.PeriodicTable
 */
public class Element
{
	public String name = null;        // eg: "Helium"
	public String symbol = null;      // eg: "He"
	public int atomic_number = 0;     // eg: 2
	public double atomic_weight = 0;  // eg: 4.002602
	public int group_number = 0;      // eg: 18
	public String group_name = null;  // eg: "Noble gas"
	public int period_number = 0;     // eg: 1
	public String block = null;       // eg: "p-block"

	/**
	 *  Construct an Element object from the given attribute values.
	 */
	public Element(
		final String name,
		final String symbol,
		final int atomic_number,
		final double atomic_weight,
		final int group_number,
		final String group_name,
		final int period_number,
		final String block
	)
	{
		this.name = name;
		this.symbol = symbol;
		this.atomic_number = atomic_number;
		this.atomic_weight = atomic_weight;
		this.group_number = group_number;
		this.group_name = group_name;
		this.period_number = period_number;
		this.block = block;
	}

	/**
	 *  Return a String representation of this object.
	 */
	
	public String toString( )
	{
		return "{ " +
			"name=" + this.name + ", " +
			"symbol=" + this.symbol + ", " +
			"atomic_number=" + this.atomic_number + ", " +
			"atomic_weight=" + this.atomic_weight + ", " +
			"group_number=" + this.group_number + ", " +
			"group_name=" + this.group_name + ", " +
			"period_number=" + this.period_number + ", " +
			"block=" + this.block + " }";
	}
}

