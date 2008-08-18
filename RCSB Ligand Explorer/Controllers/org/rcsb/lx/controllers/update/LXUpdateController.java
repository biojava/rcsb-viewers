package org.rcsb.lx.controllers.update;

import org.rcsb.lx.controllers.app.LXVersionInformation;
import org.rcsb.lx.controllers.app.LigandExplorer;
import org.rcsb.lx.controllers.scene.LXSceneController;
import org.rcsb.lx.ui.FullSequencePanel;
import org.rcsb.lx.ui.SequenceTabbedPane;
import org.rcsb.mbt.controllers.update.UpdateController;
import org.rcsb.mbt.controllers.update.UpdateEvent;
import org.rcsb.mbt.glscene.jogl.AtomGeometry;
import org.rcsb.mbt.glscene.jogl.BondGeometry;
import org.rcsb.mbt.glscene.jogl.DisplayLists;
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
	private SequenceTabbedPane sequencePane = null;

	/**
	 * Cleans everything related to the current structure from memory.
	 * 
	 */
	public void removeStructure(boolean transitory)
	{
		LXUpdateEvent evt = new LXUpdateEvent(UpdateEvent.Action.CLEAR_ALL);
		evt.transitory = transitory;
		fireUpdateViewEvent(evt);

		if(!transitory) {
			LigandExplorer.sgetActiveFrame().setTitle("RCSB PDB Ligand Explorer "
													  + LXVersionInformation.version()
													  + " (powered by the MBT)");
		}
		
		// try to keep memory usage down.
		System.gc();
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
		fireUpdateViewEvent(evt);
		
		evt = null;
		
		if(resetView)
			LigandExplorer.sgetSceneController().resetView(true);
	}

	/**
	 * Set the sequence pane - I think I'd rather have the refresh get the pane from the mainframe.
	 * That way there's no synchonization issues when it changes.
	 * 14-May-08  rickb
	 * 
	 * @param pane
	 */
	public void setSequencePane(final SequenceTabbedPane pane) {
		sequencePane = pane;
	}
	
	/**
	 * Refresh the sequence panes.
	 */
	public void refreshSequencePanes() {
		if(sequencePane != null && sequencePane.fullSequences != null) {
			final FullSequencePanel[] panels = sequencePane.fullSequences.sequencePanels;
			for(int i = 0; i < panels.length; i++) {
				final FullSequencePanel panel = panels[i];
				if(panel != null) {
					panel.needsComponentReposition = true;
					panel.repaint();
				}
			}
			
		}
	}
	}
