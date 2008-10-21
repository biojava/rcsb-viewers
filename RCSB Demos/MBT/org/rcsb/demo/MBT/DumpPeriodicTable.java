package org.rcsb.demo.MBT;

//Copyright 2000-2008 The Regents of the University of California.
//All Rights Reserved.
//
//Permission to use, copy, modify and distribute any part of this
//Molecular Biology Toolkit (MBT)
//for educational, research and non-profit purposes, without fee, and without
//a written agreement is hereby granted, provided that the above copyright
//notice, this paragraph and the following three paragraphs appear in all
//copies.
//
//Those desiring to incorporate this MBT into commercial products
//or use for commercial purposes should contact the Technology Transfer &
//Intellectual Property Services, University of California, San Diego, 9500
//Gilman Drive, Mail Code 0910, La Jolla, CA 92093-0910, Ph: (858) 534-5815,
//FAX: (858) 534-7345, E-MAIL:invent@ucsd.edu.
//
//IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR
//DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING
//LOST PROFITS, ARISING OUT OF THE USE OF THIS MBT, EVEN IF THE
//UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.//
//THE MBT PROVIDED HEREIN IS ON AN "AS IS" BASIS, AND THE
//UNIVERSITY OF CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT,
//UPDATES, ENHANCEMENTS, OR MODIFICATIONS. THE UNIVERSITY OF CALIFORNIA MAKES
//NO REPRESENTATIONS AND EXTENDS NO WARRANTIES OF ANY KIND, EITHER IMPLIED OR
//EXPRESS, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
//MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE, OR THAT THE USE OF THE
//MBT WILL NOT INFRINGE ANY PATENT, TRADEMARK OR OTHER RIGHTS.
//
//For further information, please see:  http://mbt.sdsc.edu

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
		Output.lineOut("Dumping Periodic Table:");
		Output.lineOut("");
		Output.indent();
		
		for (int atomicNumber = 1; atomicNumber < PeriodicTable.getElementCount(); atomicNumber++)
		{
			Element element = PeriodicTable.getElement(atomicNumber);
			Output.lineOut(atomicNumber + ": " + element.symbol + " (" + element.name + ')');
			Output.lineOut("  Atomic Weight: " + element.atomic_weight);
			Output.lineOut("  Group        : " + element.group_number + " (" + element.group_name + ")" );
			Output.lineOut("  Block        : " + element.block);
			float[] color = ElementStyles.getElementColor(atomicNumber);
			Output.lineOut("  Color        : R = " + color[0] + ",  G = " + color[1] + ",  B = " + color[2]);
			Output.lineOut("  VDW Radius   : " + ElementStyles.getElementRadius(atomicNumber));
			Output.lineOut("");
		}
		
		Output.outdent();
	}
	
}
