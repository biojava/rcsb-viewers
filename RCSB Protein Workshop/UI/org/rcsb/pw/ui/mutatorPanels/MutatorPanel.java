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

import org.rcsb.mbt.controllers.app.AppBase;
import org.rcsb.mbt.controllers.update.IUpdateListener;
import org.rcsb.mbt.controllers.update.UpdateEvent;
import org.rcsb.pw.controllers.app.ProteinWorkshop;
import org.rcsb.pw.controllers.scene.mutators.MutatorEnum;


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
        super.add(this.linesButton);
        
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
	public void handleModelChangedEvent(UpdateEvent evt)
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