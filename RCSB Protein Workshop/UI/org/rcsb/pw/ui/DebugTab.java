/*
 * BioJava development code
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence. This should
 * be distributed with the code. If you do not have a copy,
 * see:
 *
 * http://www.gnu.org/copyleft/lesser.html
 *
 * Copyright for this code is held jointly by the individual
 * authors. These should be listed in @author doc comments.
 *
 * For more information on the BioJava project and its aims,
 * or to join the biojava-l mailing list, visit the home page
 * at:
 *
 * http://www.biojava.org/
 *
 * This code was contributed from the Molecular Biology Toolkit
 * (MBT) project at the University of California San Diego.
 *
 * Please reference J.L. Moreland, A.Gramada, O.V. Buzko, Qing
 * Zhang and P.E. Bourne 2005 The Molecular Biology Toolkit (MBT):
 * A Modular Platform for Developing Molecular Visualization
 * Applications. BMC Bioinformatics, 6:21.
 *
 * The MBT project was funded as part of the National Institutes
 * of Health PPG grant number 1-P01-GM63208 and its National
 * Institute of General Medical Sciences (NIGMS) division. Ongoing
 * development for the MBT project is managed by the RCSB
 * Protein Data Bank(http://www.pdb.org) and supported by funds
 * from the National Science Foundation (NSF), the National
 * Institute of General Medical Sciences (NIGMS), the Office of
 * Science, Department of Energy (DOE), the National Library of
 * Medicine (NLM), the National Cancer Institute (NCI), the
 * National Center for Research Resources (NCRR), the National
 * Institute of Biomedical Imaging and Bioengineering (NIBIB),
 * the National Institute of Neurological Disorders and Stroke
 * (NINDS), and the National Institute of Diabetes and Digestive
 * and Kidney Diseases (NIDDK).
 *
 * Created on 2008/12/22
 *
 */ 
package org.rcsb.pw.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.rcsb.mbt.model.Structure;
import org.rcsb.uiApp.controllers.app.AppBase;
import org.rcsb.uiApp.controllers.update.IUpdateListener;
import org.rcsb.uiApp.controllers.update.UpdateEvent;
import org.rcsb.vf.controllers.app.VFAppBase;
import org.rcsb.vf.controllers.scene.SceneController;


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
		
		SceneController sceneController = VFAppBase.sgetSceneController();
		this.planeField = new JTextField(sceneController.getDebugSettings().blurPlane + "");
		this.factorField = new JTextField(sceneController.getDebugSettings().blurFactor + "");
		
		super.add(this.antialiasBox);
		this.antialiasBox.addActionListener(new ActionListener() {

		public void actionPerformed(final ActionEvent e)
		{
			final JCheckBox source = (JCheckBox)e.getSource();
			SceneController sceneController = VFAppBase.sgetSceneController();
			if(source.isSelected())
			{
				sceneController.getDebugSettings().isAntialiasingEnabled = true;
				sceneController.getDebugSettings().blurPlane = Float.parseFloat(DebugTab.this.planeField.getText());
				sceneController.getDebugSettings().blurFactor = Float.parseFloat(DebugTab.this.factorField.getText());
			}
			
			else
				sceneController.getDebugSettings().isAntialiasingEnabled = false;

			VFAppBase.sgetGlGeometryViewer().requestRepaint();
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
	public void handleUpdateEvent(UpdateEvent evt)
	{
		if (evt.action == UpdateEvent.Action.VIEW_RESET)
			reset();
	}
}
