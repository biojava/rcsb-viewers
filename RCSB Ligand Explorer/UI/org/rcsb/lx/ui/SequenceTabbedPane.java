package org.rcsb.lx.ui;

import javax.swing.JTabbedPane;

import org.rcsb.lx.controllers.app.LigandExplorer;
import org.rcsb.lx.controllers.update.LXUpdateController;
import org.rcsb.lx.controllers.update.LXUpdateEvent;
import org.rcsb.lx.ui.ContactMap;
import org.rcsb.lx.ui.FullSequencesViewer;
import org.rcsb.mbt.controllers.update.IUpdateListener;
import org.rcsb.mbt.controllers.update.UpdateEvent;
import org.rcsb.mbt.model.Structure;


/**
 * @author John Beaver
 */
public class SequenceTabbedPane extends JTabbedPane implements IUpdateListener
{
	private static final long serialVersionUID = -8603574191355613187L;
	public FullSequencesViewer fullSequences = null;
    public ContactMap contacts = null;
    
    public SequenceTabbedPane()
    {
        super();
        
        LXUpdateController update = LigandExplorer.sgetActiveFrame().getUpdateController();
        update.registerListener(this);
    }

    public void setComponents()
    {
    	if (fullSequences == null)
    	{
    		fullSequences = new FullSequencesViewer();
    		contacts = new ContactMap();
            
            super.addTab("Full Sequences", fullSequences);
            super.addTab("Contact Map", contacts);
    	}

        contacts.setComponents(); 
        fullSequences.setComponents();
    }

    /**
     * Simply satisfy the base interface.
     */
    public void clearStructure() {}
    public void newStructureAdded(Structure structure) {}
    
	public void clearStructure(boolean transitory) {
		if(!transitory) {
			this.removeAll();
			this.fullSequences = null;
			this.contacts = null;
		}
	}

	public void newStructureAdded(final Structure struc, boolean transitory) {
		if(!transitory) {
			this.setComponents();
		}
	}

	public void reset()
	{
		fullSequences.updateSequences();
	}

	/* (non-Javadoc)
	 * @see edu.sdsc.mbt.views_controller.IViewUpdateListener#handleModelChangedEvent(edu.sdsc.mbt.views_controller.ViewUpdateEvent)
	 */
	public void handleUpdateEvent(UpdateEvent evt)
	{
		boolean transitory = false;
		
		if (evt instanceof LXUpdateEvent)
		  transitory = ((LXUpdateEvent)evt).transitory;
		
		switch(evt.action)
		{
		case STRUCTURE_ADDED:
			newStructureAdded(evt.structure, transitory);
			break;
			
		case VIEW_RESET:
			reset();
			break;
		}
	}
}
