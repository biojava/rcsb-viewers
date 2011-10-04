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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import org.rcsb.pw.controllers.app.ProteinWorkshop;
import org.rcsb.pw.controllers.app.ProteinWorkshop.PWAppModuleFactory;
import org.rcsb.pw.controllers.scene.mutators.MutatorEnum;
import org.rcsb.uiApp.controllers.app.AppBase;
import org.rcsb.uiApp.controllers.update.IUpdateListener;
import org.rcsb.uiApp.controllers.update.UpdateEvent;


public class MutatorPanel extends JPanel implements IUpdateListener
{
	private static final long serialVersionUID = -7205000642717901366L;
	// tools
	private JToggleButton visibilityButton = null;
	private JToggleButton colorChangeButton = null;
	private JToggleButton labelingButton = null;
	private JToggleButton stylesButton = null;
	private JToggleButton linesButton = null;
	private JToggleButton reCenterButton = null;
//	private JToggleButton selectionButton = null;
	private ButtonGroup mutatorGroup = null;
	
	private class CustomLayout implements LayoutManager2 {
    	private Dimension size = new Dimension(0,0);
    	
		public void addLayoutComponent(String name, Component comp) {}

		public void layoutContainer(Container parent) {
			final int buffer = 3;
			final Insets insets = parent.getInsets();
			int curY = insets.top + buffer;
			int curX = insets.left + buffer;
			
			final Dimension visSize = visibilityButton.getPreferredSize();
			final Dimension colorSize = colorChangeButton.getPreferredSize();
			final Dimension labelingSize = labelingButton.getPreferredSize();
			final Dimension stylesSize = stylesButton.getPreferredSize();
			final Dimension linesSize = linesButton.getPreferredSize();
			final Dimension reCenterSize = reCenterButton.getPreferredSize();
			final int maxButtonWidth = Math.max(visSize.width, Math.max(colorSize.width, Math.max(labelingSize.width, Math.max(stylesSize.width, Math.max(linesSize.width, reCenterSize.width )))));
			final int buttonHeight = visSize.height;
			
			System.out.println("MutatorPanel: visSize: " + visSize);
			System.out.println("MutatorPanel: colorSize: " + colorSize);
			System.out.println("MutatorPanel: labelingSize: " + labelingSize);
			System.out.println("MutatorPanel: stylesSize: " + stylesSize);
			System.out.println("MutatorPanel: linesSize: " + linesSize);
			System.out.println("MutatorPanel: reCenterSize: " + reCenterSize);
			System.out.println("MutatorPanel: maxButtonWidth: " + maxButtonWidth);
			
			
			visibilityButton.setBounds(curX, curY, maxButtonWidth, buttonHeight);
			curY += buttonHeight + buffer;
			colorChangeButton.setBounds(curX, curY, maxButtonWidth, buttonHeight);
			curY += buttonHeight + buffer;
			labelingButton.setBounds(curX, curY, maxButtonWidth, buttonHeight);
			curY = insets.top + buffer;
			curX += maxButtonWidth + buffer;
			stylesButton.setBounds(curX, curY, maxButtonWidth, buttonHeight);
			curY += buttonHeight + buffer;
			linesButton.setBounds(curX, curY, maxButtonWidth, buttonHeight);
			curY += buttonHeight + buffer;
			reCenterButton.setBounds(curX, curY, maxButtonWidth, buttonHeight);
			curY += buttonHeight + buffer;
			
			Container parentParent = parent.getParent();
			Insets parentParentInsets = parentParent.getInsets();
			
			System.out.println("MutatorPanel: parentParentWidth: " + parentParent.getWidth());
			this.size.width = parentParent.getWidth() - parentParentInsets.left - parentParentInsets.right;
			this.size.height = curY + insets.bottom;
		}

		public Dimension minimumLayoutSize(Container parent) {
			return this.size;
		}

		public Dimension preferredLayoutSize(Container parent) {
			return this.size;
		}

		public void removeLayoutComponent(Component comp) {}

		public void addLayoutComponent(Component comp, Object constraints) {}

		public float getLayoutAlignmentX(Container target) {
			return 0;
		}

		public float getLayoutAlignmentY(Container target) {
			return 0;
		}

		public void invalidateLayout(Container target) {}

		public Dimension maximumLayoutSize(Container target) {
			return this.size;
		}
    }
	
    public MutatorPanel() {
        super(null, false);
        super.setLayout(new CustomLayout());
        super.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("1)  Select your tool."),
                BorderFactory.createEmptyBorder(-1,1,1,1)));
        
        this.visibilityButton = new JToggleButton("Visibility", false);
        this.colorChangeButton = new JToggleButton("Colors", false);
        this.labelingButton = new JToggleButton("Labels", false);
        this.stylesButton = new JToggleButton("Styles", false);
        this.linesButton = new JToggleButton("Lines", false);
        this.reCenterButton = new JToggleButton("Re-centering", false);
//        this.selectionButton = new JToggleButton("Selection", false);
        
        this.mutatorGroup = new ButtonGroup();
        this.mutatorGroup.add(this.visibilityButton);
        this.mutatorGroup.add(this.colorChangeButton);
        this.mutatorGroup.add(this.labelingButton);
        this.mutatorGroup.add(this.stylesButton);
        this.mutatorGroup.add(this.reCenterButton);
//        this.mutatorGroup.add(this.selectionButton);
        this.mutatorGroup.add(this.linesButton);
        
        super.add(this.visibilityButton);
        super.add(this.colorChangeButton);
        super.add(this.labelingButton);
        super.add(this.stylesButton);
        super.add(this.reCenterButton);
//        super.add(this.selectionButton);
 
        // Show lines button only for asymmetic units. For biological unit
        // we can't perform distance measurements since we don't have the atomic
        // coordinates of the symmetry related parts.
		if (ProteinWorkshop.sgetSceneController().showAsymmetricUnitOnly()) {
           super.add(this.linesButton);
        }
        
        this.visibilityButton.addActionListener(new MutatorButtonListener(MutatorEnum.Id.VISIBILITY_MUTATOR));
        this.colorChangeButton.addActionListener(new MutatorButtonListener(MutatorEnum.Id.COLORING_MUTATOR));
        this.labelingButton.addActionListener(new MutatorButtonListener(MutatorEnum.Id.LABELING_MUTATOR));
        this.stylesButton.addActionListener(new MutatorButtonListener(MutatorEnum.Id.STYLES_MUTATOR));
        this.reCenterButton.addActionListener(new MutatorButtonListener(MutatorEnum.Id.RECENTER_MUTATOR));
        this.linesButton.addActionListener(new MutatorButtonListener(MutatorEnum.Id.LINES_MUTATOR));
//        this.selectionButton.addActionListener(new MutatorButtonListener(MutatorModel.SELECTION_MUTATOR));
        
        this.visibilityButton.setToolTipText("Show/hide items in the 3d viewer.");
        this.colorChangeButton.setToolTipText("Change the color of items.");
        this.labelingButton.setToolTipText("Change the labeling of items.");
        this.stylesButton.setToolTipText("Change the styles of items.");
        this.reCenterButton.setToolTipText("Cause the 3d scene to re-center on the chosen object.");
//        this.selectionButton.setToolTipText("Choose more than one atom/bond/residue to cause batch operations upon.");
        this.linesButton.setToolTipText("Add or remove indicator lines between items");
        
        this.reset();
        
        AppBase.sgetUpdateController().registerListener(this);
    }

    public void reset() {
        this.visibilityButton.doClick();
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

class MutatorButtonListener implements ActionListener
{
    private MutatorEnum.Id actionType;
    
    public MutatorButtonListener(final MutatorEnum.Id actionType)
    {
        this.actionType = actionType;
    }
    
    public void actionPerformed(final ActionEvent e)
    {
    	final JToggleButton source = (JToggleButton)e.getSource();
    	if(source.isSelected())
    		ProteinWorkshop.sgetSceneController().getMutatorEnum().setCurrentMutator(this.actionType); 
    }
}
