package org.rcsb.lx.controllers.update;

import org.rcsb.lx.controllers.app.LXVersionInformation;
import org.rcsb.lx.controllers.app.LigandExplorer;
import org.rcsb.mbt.controllers.update.UpdateController;
import org.rcsb.mbt.controllers.update.UpdateEvent;
import org.rcsb.mbt.model.Structure;


/**
 * Extended version of the ViewUpdateController.  This is used to send
 * notification that the model has changed.
 * 
 * @author rickb
 *
 */
public class LXUpdateController extends UpdateController
{
	/**
	 * Cleans everything related to the current structure from memory.
	 * 
	 */
	public void removeStructure(boolean transitory)
	{
		LXUpdateEvent evt = new LXUpdateEvent(UpdateEvent.Action.CLEAR_ALL);
		evt.transitory = transitory;
		fireUpdateEvent(evt);

		if(!transitory) {
			LigandExplorer.sgetActiveFrame().setTitle("RCSB PDB Ligand Explorer "
													  + LXVersionInformation.version()
													  + " (powered by the MBT)");
		}
		
		// try to keep memory usage down.
		System.gc();
	}
	
	public void fireInteractionChanged()
	{
		LXUpdateEvent evt = new LXUpdateEvent(UpdateEvent.Action.EXTENDED);
		evt.lxAction = LXUpdateEvent.LXAction.INTERACTIONS_CHANGED;
		fireUpdateEvent(evt);
	}

	/**
	 * Notify all the panels that a structure has been added.
	 * 
	 * @param resetView - whether or not to reset the view
	 * @param transitory - (?)
	 */
	public void fireStructureAdded(final Structure struc, final boolean resetView, final boolean transitory)
	{
		LXUpdateEvent evt = new LXUpdateEvent(UpdateEvent.Action.STRUCTURE_ADDED);
		evt.transitory = transitory;
		evt.structure = struc;
		fireUpdateEvent(evt);
		
		evt = null;
		
		if(resetView)
			LigandExplorer.sgetSceneController().resetView(true);
	}
}
