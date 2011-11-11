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
package org.rcsb.pw.ui.mutatorPanels;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.rcsb.mbt.model.LineSegment;
import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.attributes.LineStyle;
import org.rcsb.pw.controllers.app.ProteinWorkshop;
import org.rcsb.pw.controllers.scene.mutators.MutatorEnum;
import org.rcsb.pw.ui.CommonDialogs;
import org.rcsb.pw.ui.FullWidthBoxLayout;
import org.rcsb.uiApp.controllers.update.IUpdateListener;
import org.rcsb.uiApp.controllers.update.UpdateEvent;
import org.rcsb.uiApp.ui.dialogs.ColorChooserDialog;
import org.rcsb.vf.controllers.app.VFAppBase;
import org.rcsb.vf.glscene.jogl.JoglSceneNode;




public class LinesOptionsPanel extends JPanel implements IUpdateListener, ActionListener
{
	private static final long serialVersionUID = -802675686903557055L;

	private JButton changeColorButton = null;

	private JCheckBox showDistancesCheck = null;
	
	private CustomRadioPanel styles = null;
	private ButtonGroup styleGroup = null;
	private JLabel lineStyles = null;
	private JRadioButton dashLineRadio = null;
	private JRadioButton dotLineRadio = null;
	private JRadioButton solidLineRadio = null;

	private CustomLabelPanel firstLabelPanel = null;
	private CustomLabelPanel secondLabelPanel = null;
	private JLabel firstPointLabel = null;
	private JLabel secondPointLabel = null;
	private JTextField firstPointText = null;
	private JTextField secondPointText = null;

	private CustomButtonPanel remove = null;
	private JButton clearLineButton = null;
	private JButton clearAllButton = null;
    
    public LinesOptionsPanel() {
        super(null, false);
        super.setLayout(new FullWidthBoxLayout());
        super.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("3)  Choose options, click on 2 atoms/residues."),
                BorderFactory.createEmptyBorder(-1,1,1,1)));

    	this.changeColorButton = new JButton("Change Color");

    	this.styleGroup = new ButtonGroup();
    	this.lineStyles = new JLabel("Styles: ");
    	this.dashLineRadio = new JRadioButton("------");
    	this.dotLineRadio = new JRadioButton("......");
    	this.solidLineRadio = new JRadioButton("_____");
    	this.styles = new CustomRadioPanel(this.lineStyles, this.styleGroup, this.solidLineRadio, this.dashLineRadio, this.dotLineRadio);
    	
    	this.showDistancesCheck = new JCheckBox("Display distances");

    	this.firstPointLabel = new JLabel( "1st Atom/Residue:  ");
    	this.firstPointText = new JTextField();
    	this.firstLabelPanel = new CustomLabelPanel(this.firstPointLabel, this.firstPointText);
    	this.secondPointLabel = new JLabel("2nd Atom/Residue: ");
    	this.secondPointText = new JTextField();
    	this.secondLabelPanel = new CustomLabelPanel(this.secondPointLabel, this.secondPointText);

    	this.clearLineButton = new JButton("Clear Line");
    	this.clearAllButton = new JButton("Clear All");
    	this.remove = new CustomButtonPanel(this.clearLineButton, this.clearAllButton);
    	
    	this.changeColorButton.addActionListener(this);
    	this.dashLineRadio.addActionListener(this);
    	this.solidLineRadio.addActionListener(this);
    	this.dotLineRadio.addActionListener(this);
    	this.clearAllButton.addActionListener(this);
    	this.showDistancesCheck.addActionListener(this);
    	
    	this.firstPointText.setEditable(false);
    	this.secondPointText.setEditable(false);
        
        super.add(this.changeColorButton);
        super.add(this.styles);
        super.add(this.showDistancesCheck);
        super.add(this.firstLabelPanel);
        super.add(this.secondLabelPanel);
        super.add(this.remove);
        
        this.reset();
        
        ProteinWorkshop.sgetUpdateController().registerListener(this);
        ProteinWorkshop.sgetActiveFrame().setLinesPanel(this);
    }
    
     
    
    public void updateObject1Text(final String text) {
    	this.firstPointText.setText(text);
    	this.secondPointText.setText("");
    }
    
    public void updateObject2Text(final String text) {
    	this.secondPointText.setText(text);
    }

    public void reset() {
    	this.dashLineRadio.doClick();
    	if(!this.showDistancesCheck.isSelected()) {
    		this.showDistancesCheck.doClick();
    	}
    	this.firstPointText.setText("");
    	this.secondPointText.setText("");
    	this.clearAllButton.doClick();
    }
    
	/* (non-Javadoc)
	 * @see edu.sdsc.mbt.views_controller.IViewUpdateListener#handleModelChangedEvent(edu.sdsc.mbt.views_controller.ViewUpdateEvent)
	 */
	public void handleUpdateEvent(UpdateEvent evt)
	{
		if (evt.action == UpdateEvent.Action.VIEW_RESET)
			reset();
	}

	private class CustomButtonPanel extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 284524094867350957L;

		public CustomButtonPanel(final JButton button1, final JButton button2) {
			super(false);
			final BoxLayout layout = new BoxLayout(this, BoxLayout.X_AXIS);
			super.setLayout(layout);
			
			super.add(button2);
		}
	}

	private class CustomRadioPanel extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 9192384863754007766L;

		public CustomRadioPanel(final JLabel label, final ButtonGroup group, final JRadioButton button1, final JRadioButton button2, final JRadioButton button3) {
			super(false);
			final BoxLayout layout = new BoxLayout(this, BoxLayout.X_AXIS);
			super.setLayout(layout);
			
			group.add(button1);
			group.add(button2);
			group.add(button3);
			
			super.add(label);
			super.add(button1);
			super.add(button2);
			super.add(button3);
		}
	}
	
	private class CustomLabelPanel extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = -8994902263334020836L;

		public CustomLabelPanel(final JLabel label, final JTextField field) {
			super(false);
			final BoxLayout layout = new BoxLayout(this, BoxLayout.X_AXIS);
			super.setLayout(layout);
			
			field.setPreferredSize(new Dimension(80, 20));
			
			super.add(label);
			super.add(field);
		}
	}

	public void actionPerformed(final ActionEvent arg0)
	{	
		MutatorEnum mutEnum = ProteinWorkshop.sgetSceneController().getMutatorEnum();

		if(arg0.getSource() == this.changeColorButton)
		{
			final ColorChooserDialog dialog = CommonDialogs.getColorDialog();
			dialog.show();
			if(dialog.wasOKPressed())
			{
				final float[] setColor = mutEnum.getLinesMutator().getOptions().getColor();
				dialog.getColor().getColorComponents(setColor);
			}
			//More Code is needed
		} else if(arg0.getSource() == this.solidLineRadio) {
			if(((JRadioButton)arg0.getSource()).isSelected()) {
				mutEnum.getLinesMutator().getOptions().setLineStyle(LineStyle.SOLID);
			}
		} else if(arg0.getSource() == this.dotLineRadio) {
			if(((JRadioButton)arg0.getSource()).isSelected()) {
				mutEnum.getLinesMutator().getOptions().setLineStyle(LineStyle.DOTTED);
			}	
		} else if(arg0.getSource() == this.dashLineRadio) {
			if(((JRadioButton)arg0.getSource()).isSelected()) {
				mutEnum.getLinesMutator().getOptions().setLineStyle(LineStyle.DASHED);
			}
		} else if(arg0.getSource() == this.clearLineButton) {
			// This button's doing nothing right now
		} else if(arg0.getSource() == this.clearAllButton) {
			final Iterator<LineSegment> it = mutEnum.getLinesMutator().lines.iterator();
			Structure struc = null;
			while (it.hasNext()) {
				final LineSegment line = (LineSegment)it.next();
				((JoglSceneNode)line.structure.getStructureMap().getUData()).removeRenderable(line);
				struc = line.structure;
			}
			if(struc != null) {
				((JoglSceneNode)struc.getStructureMap().getUData()).regenerateGlobalList();
			}
			VFAppBase.sgetGlGeometryViewer().requestRepaint();
		} else if(arg0.getSource() == this.showDistancesCheck) {
			mutEnum.getLinesMutator().getOptions().setDisplayDistance(this.showDistancesCheck.isSelected());
		}
	}
}
	
