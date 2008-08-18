package org.rcsb.pw.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.rcsb.mbt.controllers.app.AppBase;
import org.rcsb.mbt.controllers.update.IUpdateListener;
import org.rcsb.mbt.controllers.update.UpdateEvent;


public class MiscellaneousConvenienceOptionsPanel extends JPanel implements IUpdateListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -456939384877516677L;
	private JCheckBox autoRotateBox = null;
	
	public MiscellaneousConvenienceOptionsPanel() {
		super(null, false);
        super.setLayout(new FullWidthBoxLayout());
        
        super.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Rotation..."),
                BorderFactory.createEmptyBorder(-1,1,1,1)));
        
        this.autoRotateBox = new JCheckBox("Auto Rotate");
        super.add(this.autoRotateBox);
        this.autoRotateBox.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				AppBase.sgetSceneController().setAutoRotateEnabled(((JCheckBox)e.getSource()).isSelected());
			}
        	
        });
        
        AppBase.sgetUpdateController().registerListener(this);
	}
	
	public void reset() {
		if(this.autoRotateBox.isSelected()) {
			this.autoRotateBox.doClick();
		}
	}

	/* (non-Javadoc)
	 * @see edu.sdsc.mbt.views_controller.IViewUpdateListener#handleModelChangedEvent(edu.sdsc.mbt.views_controller.ViewUpdateEvent)
	 */
	public void handleModelChangedEvent(UpdateEvent evt)
	{
		if (evt.action == UpdateEvent.Action.VIEW_RESET)
			reset();
		
	}
}
