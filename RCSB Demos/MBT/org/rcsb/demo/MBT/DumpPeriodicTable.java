package org.rcsb.demo.MBT;

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
		Output.incrementIndent();
		
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
		
		Output.decrementIndent();
	}
	
}
