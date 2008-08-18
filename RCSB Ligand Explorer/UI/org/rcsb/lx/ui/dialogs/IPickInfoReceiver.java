package org.rcsb.lx.ui.dialogs;

/**
 * Atoms or various other types of molecular components can be picked.
 * Implement this interface to receive information from those picks.
 * 
 * (See the various measurement dialogs for examples.)
 * 
 * @author rickb
 *
 */
public interface IPickInfoReceiver
{
	public void processPick(double[] point, String description);

}
