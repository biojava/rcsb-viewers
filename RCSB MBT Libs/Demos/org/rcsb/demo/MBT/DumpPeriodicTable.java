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
 * Created on 2008/12/22
 *
 */ 
package org.rcsb.demo.MBT;

import java.io.PrintStream;

import org.rcsb.demo.utils.Output;
import org.rcsb.mbt.model.attributes.ElementStyles;
import org.rcsb.mbt.model.util.Element;
import org.rcsb.mbt.model.util.PeriodicTable;

/**
 * Dump the internal periodic table.
 * 
 * These are the base definitions, as the MBT carries them.
 * 
 * Note that for a given atom, you should use the AtomStyle
 * mechanism, as the color may have been reset (by a mutator, for instance), 
 * and the radius will be that necessary for rendering in the current style
 * ('CPK' vs. 'Ball and Stick', for instance.)
 * 
 * {@see org.rcsb.mbt.model.attributes} for further information
 * 
 * @author rickb
 *
 */
public class DumpPeriodicTable
{
	public static void main(String args[])
	{
		doDump(System.err);
	}
	
	public static void doDump(PrintStream _out)
	{
		Output output = new Output(_out);
		
		output.lineOut("Dumping Periodic Table:");
		output.lineOut("");
		output.indent();
		
		for (int atomicNumber = 1; atomicNumber < PeriodicTable.getElementCount(); atomicNumber++)
		{
			Element element = PeriodicTable.getElement(atomicNumber);
			output.lineOut(atomicNumber + ": " + element.symbol + " (" + element.name + ')');
			output.lineOut("  Atomic Weight: " + element.atomic_weight);
			output.lineOut("  Group        : " + element.group_number + " (" + element.group_name + ")" );
			output.lineOut("  Block        : " + element.block);
			float[] color = ElementStyles.getElementColor(atomicNumber);
			output.lineOut("  Color        : R = " + color[0] + ",  G = " + color[1] + ",  B = " + color[2]);
			output.lineOut("  VDW Radius   : " + ElementStyles.getElementRadius(atomicNumber));
			output.lineOut("");
		}
		
		output.outdent();
	}
	
}
