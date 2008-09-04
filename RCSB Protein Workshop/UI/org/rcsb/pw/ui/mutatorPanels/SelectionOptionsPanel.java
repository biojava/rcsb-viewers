package org.rcsb.pw.ui.mutatorPanels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.rcsb.mbt.controllers.app.AppBase;
import org.rcsb.mbt.controllers.update.IUpdateListener;
import org.rcsb.mbt.controllers.update.UpdateEvent;
import org.rcsb.pw.ui.FullWidthBoxLayout;


public class SelectionOptionsPanel extends JPanel implements IUpdateListener, ActionListener
{
	private static final long serialVersionUID = -8444753487717915763L;

	public JButton clearButton = null;
	
    public SelectionOptionsPanel() {
        super(null, false);
        super.setLayout(new FullWidthBoxLayout());
        super.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("3)  Change the tool's options, if necessary."),
                BorderFactory.createEmptyBorder(-1,1,1,1)));
        
        this.clearButton = new JButton("Clear All Selections");
        this.clearButton.addActionListener(this);
        
        super.add(this.clearButton);
        
//        this.reset();
        
        AppBase.sgetUpdateController().registerListener(this);    }

    public void reset() {
        this.clearButton.doClick();
    }

	/* (non-Javadoc)
	 * @see edu.sdsc.mbt.views_controller.IViewUpdateListener#handleModelChangedEvent(edu.sdsc.mbt.views_controller.ViewUpdateEvent)
	 */
	public void handleUpdateEvent(UpdateEvent evt)
	{
		if (evt.action == UpdateEvent.Action.VIEW_RESET)
			reset();
	}

	public void actionPerformed(final ActionEvent e) {
		AppBase.sgetModel().getStructures().get(0).getStructureMap().getStructureStyles().clearSelections();
	}
}