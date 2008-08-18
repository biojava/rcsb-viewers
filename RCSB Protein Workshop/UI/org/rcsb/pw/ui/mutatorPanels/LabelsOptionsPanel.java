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

import org.rcsb.mbt.controllers.app.AppBase;
import org.rcsb.mbt.controllers.update.IUpdateListener;
import org.rcsb.mbt.controllers.update.UpdateEvent;
import org.rcsb.pw.controllers.app.ProteinWorkshop;
import org.rcsb.pw.controllers.scene.mutators.LabelsMutator;
import org.rcsb.pw.controllers.scene.mutators.MutatorEnum;
import org.rcsb.pw.controllers.scene.mutators.options.LabelsOptions;
import org.rcsb.pw.ui.FullWidthBoxLayout;



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
			
			field.setPreferredSize(new Dimension(170,0));
			
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
	public void handleModelChangedEvent(UpdateEvent evt)
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