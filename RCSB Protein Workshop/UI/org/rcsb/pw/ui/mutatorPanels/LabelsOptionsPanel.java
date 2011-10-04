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

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.rcsb.pw.controllers.app.ProteinWorkshop;
import org.rcsb.pw.controllers.scene.mutators.LabelsMutator;
import org.rcsb.pw.controllers.scene.mutators.MutatorEnum;
import org.rcsb.pw.controllers.scene.mutators.options.LabelsOptions;
import org.rcsb.pw.ui.FullWidthBoxLayout;
import org.rcsb.uiApp.controllers.app.AppBase;
import org.rcsb.uiApp.controllers.update.IUpdateListener;
import org.rcsb.uiApp.controllers.update.UpdateEvent;



public class LabelsOptionsPanel extends JPanel implements IUpdateListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1718513394427117019L;
	private JRadioButton byNothingButton = null;
	private JRadioButton byAtomButton = null;
	private JRadioButton byResidueButton = null;
	
	private CustomLabelPanel customLabelPanel = null;
	private JRadioButton customLabelButton = null;
	private JTextField customLabelField = null;
	
	private ButtonGroup modeGroup = null;
	
	private class CustomLabelPanel extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = -6394182903551182695L;

		public CustomLabelPanel(final JRadioButton button, final JTextField field) {
			super(false);
			final BoxLayout layout = new BoxLayout(this, BoxLayout.X_AXIS);
			super.setLayout(layout);
			
			// TODO -pr is this panel too wide??
//			field.setPreferredSize(new Dimension(170,0));
			field.setPreferredSize(new Dimension(80,0));
			
			super.add(button);
			super.add(field);
		}
	}
	
	private class CustomLabelListener implements DocumentListener {
		private JRadioButton customLabelButton = null;
		
		public CustomLabelListener(final JRadioButton customLabelButton) {
			this.customLabelButton = customLabelButton;
		}
		
		public void changedUpdate(final DocumentEvent e) {
			final Document source = e.getDocument();
			
			// auto-select this option
			if(!this.customLabelButton.isSelected()) {
				this.customLabelButton.doClick();
			}
			
			try {
				ProteinWorkshop.sgetSceneController().getMutatorEnum().getLabelsMutator().getOptions().setCustomLabel(source.getText(0,source.getLength()));
			} catch (final BadLocationException e1) {
				e1.printStackTrace();
			}
		}

		public void insertUpdate(final DocumentEvent e) {
			this.changedUpdate(e);
		}

		public void removeUpdate(final DocumentEvent e) {
			this.changedUpdate(e);
		}
		
	}
	
    public LabelsOptionsPanel() {
        super(new FullWidthBoxLayout(), false);
        super.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("3)  Change the tool's options, if necessary."),
                BorderFactory.createEmptyBorder(-1,1,1,1)));
        
        // create the interface objects...
        this.byNothingButton = new JRadioButton("No labels.");
        this.byAtomButton = new JRadioButton("Label by atoms.");
        this.byResidueButton = new JRadioButton("Label by residues.");
        this.customLabelButton = new JRadioButton("Custom label: ");
        
        this.byNothingButton.addActionListener(new LabelsButtonListener(LabelsOptions.LABELS_OFF));
        this.byAtomButton.addActionListener(new LabelsButtonListener(LabelsOptions.LABEL_BY_ATOM));
        this.byResidueButton.addActionListener(new LabelsButtonListener(LabelsOptions.LABEL_BY_RESIDUE));
        this.customLabelButton.addActionListener(new LabelsButtonListener(LabelsOptions.LABEL_CUSTOM));
        
        this.customLabelField = new JTextField(ProteinWorkshop.sgetSceneController().getMutatorEnum().getLabelsMutator().getOptions().getCustomLabel());
        this.customLabelPanel = new CustomLabelPanel(this.customLabelButton,this.customLabelField);
        
        this.customLabelField.getDocument().addDocumentListener(new CustomLabelListener(this.customLabelButton));
        
        this.modeGroup = new ButtonGroup();
        this.modeGroup.add(this.byNothingButton);
        this.modeGroup.add(this.byAtomButton);
        this.modeGroup.add(this.byResidueButton);
        this.modeGroup.add(this.customLabelButton);
        
        super.add(this.byNothingButton);
        super.add(this.byAtomButton);
        super.add(this.byResidueButton);
        super.add(this.customLabelPanel);
        
        this.reset();
        
        AppBase.sgetUpdateController().registerListener(this);
    }

    public void reset() {
        this.byNothingButton.doClick();
        ProteinWorkshop.sgetSceneController().getMutatorEnum().getLabelsMutator().getOptions().setCustomLabel("");
        this.customLabelField.setText("");
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

class LabelsButtonListener implements ActionListener {
    private int style;
    
    public LabelsButtonListener(final int modeType) {
        this.style = modeType;
    }
    
    public void actionPerformed(final ActionEvent e)
    {
        final JRadioButton source = (JRadioButton) e.getSource();
        
        if(source.isSelected())
        {
            final MutatorEnum mutatorEnum = ProteinWorkshop.sgetSceneController().getMutatorEnum();
            
            final LabelsMutator mutator = mutatorEnum.getLabelsMutator();
            final LabelsOptions options = mutator.getOptions();
            options.setCurrentLabellingStyle(this.style);
        }
    }
}
