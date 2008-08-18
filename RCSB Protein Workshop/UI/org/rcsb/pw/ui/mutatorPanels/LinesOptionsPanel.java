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

import org.rcsb.mbt.controllers.app.AppBase;
import org.rcsb.mbt.controllers.update.IUpdateListener;
import org.rcsb.mbt.controllers.update.UpdateEvent;
import org.rcsb.mbt.model.LineSegment;
import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.attributes.LineStyle;
import org.rcsb.mbt.ui.dialogs.ColorChooserDialog;
import org.rcsb.pw.controllers.app.ProteinWorkshop;
import org.rcsb.pw.controllers.scene.mutators.MutatorEnum;
import org.rcsb.pw.ui.CommonDialogs;
import org.rcsb.pw.ui.FullWidthBoxLayout;




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
                BorderFactory.createTitledBorder("3)  Change the tool's options, if necessary."),
                BorderFactory.createEmptyBorder(-1,1,1,1)));

    	this.changeColorButton = new JButton("Change Color");

    	this.styleGroup = new ButtonGroup();
    	this.lineStyles = new JLabel("Styles: ");
    	this.dashLineRadio = new JRadioButton("------");
    	this.dotLineRadio = new JRadioButton("......");
    	this.solidLineRadio = new JRadioButton("_____");
    	this.styles = new CustomRadioPanel(this.lineStyles, this.styleGroup, this.solidLineRadio, this.dashLineRadio, this.dotLineRadio);
    	
    	this.showDistancesCheck = new JCheckBox("Display distances");

    	this.firstPointLabel = new JLabel( "1st Point:  ");
    	this.firstPointText = new JTextField();
    	this.firstLabelPanel = new CustomLabelPanel(this.firstPointLabel, this.firstPointText);
    	this.secondPointLabel = new JLabel("2nd Point: ");
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
    	this.solidLineRadio.doClick();
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
	public void handleModelChangedEvent(UpdateEvent evt)
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
			
//			super.add(button1);
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
			
			field.setPreferredSize(new Dimension(200, 20));
			
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
			final Iterator it = mutEnum.getLinesMutator().lines.iterator();
			Structure struc = null;
			while (it.hasNext()) {
				final LineSegment line = (LineSegment)it.next();
				line.structure.getStructureMap().getSceneNode().removeRenderable(line);
				struc = line.structure;
			}
			if(struc != null) {
				struc.getStructureMap().getSceneNode().regenerateGlobalList();
			}
			AppBase.sgetGlGeometryViewer().requestRepaint();
		} else if(arg0.getSource() == this.showDistancesCheck) {
			mutEnum.getLinesMutator().getOptions().setDisplayDistance(this.showDistancesCheck.isSelected());
		}
	}
}
	