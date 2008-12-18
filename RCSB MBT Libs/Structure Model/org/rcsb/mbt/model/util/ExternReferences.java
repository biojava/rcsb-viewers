package org.rcsb.mbt.model.util;

import org.rcsb.mbt.model.Residue;

/**
 * These are abstractions of some residual stuff that came out of the original app/controller
 * framework.  The UIApp/subsequent apps override this and supply the required information/functions.
 * 
 * @author rickb
 *
 */
public class ExternReferences
{
	private static boolean flagIsLigandExplorer = false;
	public static void setIsLigandExplorer() { flagIsLigandExplorer = true; }
	/**
	 * This need to be removed, note where it's used
	 * (AtomRadiusByScaledCPK).  It's the last viewer-specific flag I couldn't
	 * eliminate in the given timeframe.
	 * 
	 * 17-Dec-08 - rickb
	 * @return
	 */
	public static boolean isLigandExplorer() { return flagIsLigandExplorer; }
	
	/////

	private static IResidueNameModifier residueNameModifier = null;
	public static void registerResidueNameModifier(IResidueNameModifier modifier) { residueNameModifier = modifier; }
	/**
	 * Application can embellish/add information to the residue name returned.
	 * 
	 * @param residue
	 * @return
	 */
	public static String getModifiedResidueName(Residue residue)
	{
		if (residueNameModifier != null)
			return residueNameModifier.getModifiedResidueName(residue);
		
		else
			return ((Integer)residue.getResidueId()).toString();
	}
}
