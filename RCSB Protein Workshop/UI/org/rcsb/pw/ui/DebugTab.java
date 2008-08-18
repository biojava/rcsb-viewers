package org.rcsb.pw.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.rcsb.mbt.controllers.app.AppBase;
import org.rcsb.mbt.controllers.update.IUpdateListener;
import org.rcsb.mbt.controllers.update.UpdateEvent;
import org.rcsb.mbt.glscene.controller.SceneController;
import org.rcsb.mbt.model.Structure;


public class DebugTab extends JPanel implements IUpdateListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -214394329679769463L;

	public JCheckBox antialiasBox = new JCheckBox("Antialiasing");
	
	public JTextField planeField = null;
	public JTextField factorField = null;
	
	public DebugTab() {
		super();
		super.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		SceneController sceneController = AppBase.sgetSceneController();
		this.planeField = new JTextField(sceneController.getDebugSettings().blurPlane + "");
		this.factorField = new JTextField(sceneController.getDebugSettings().blurFactor + "");
		
		super.add(this.antialiasBox);
		this.antialiasBox.addActionListener(new ActionListener() {

		public void actionPerformed(final ActionEvent e)
		{
			final JCheckBox source = (JCheckBox)e.getSource();
			SceneController sceneController = AppBase.sgetSceneController();
			if(source.isSelected())
			{
				sceneController.getDebugSettings().isAntialiasingEnabled = true;
				sceneController.getDebugSettings().blurPlane = Float.parseFloat(DebugTab.this.planeField.getText());
				sceneController.getDebugSettings().blurFactor = Float.parseFloat(DebugTab.this.factorField.getText());
			}
			
			else
				sceneController.getDebugSettings().isAntialiasingEnabled = false;

			AppBase.sgetGlGeometryViewer().requestRepaint();
		}
			
		});
		
		super.add(new JLabel("Plane: "));
		super.add(this.planeField);
		super.add(new JLabel("Factor: "));
		super.add(this.factorField);
		
		this.reset();

		AppBase.sgetUpdateController().registerListener(this);
	}

	public void reset() {
		if(this.antialiasBox.isSelected()) {
			this.antialiasBox.setEnabled(false);
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
