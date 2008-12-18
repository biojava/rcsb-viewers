package org.rcsb.mbt.model.util;

import org.rcsb.mbt.model.Residue;

/**
 * Anything that wants to provide an embellished residue name needs to implement this
 * and register itself with ExternReferences.
 * 
 * @author rickb
 *
 */
public interface IResidueNameModifier
{
	String getModifiedResidueName(Residue residue);
}
