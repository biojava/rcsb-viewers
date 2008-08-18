package org.rcsb.pw.ui;

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
import javax.swing.JRadioButton;

import org.rcsb.mbt.controllers.app.AppBase;
import org.rcsb.mbt.controllers.scene.PickLevel;
import org.rcsb.mbt.controllers.update.IUpdateListener;
import org.rcsb.mbt.controllers.update.UpdateEvent;
import org.rcsb.pw.controllers.app.ProteinWorkshop;


public class PickLevelPanel extends JPanel implements ActionListener, IUpdateListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6241838181581691219L;
	private final JRadioButton atomsBondsButton = new JRadioButton("Atoms and Bonds");
	private final JRadioButton ribbonsButton = new JRadioButton("Ribbons");
	private final ButtonGroup group = new ButtonGroup();
	
	private class CustomLayout implements LayoutManager2 {
    	private Dimension size = new Dimension(0,0);
    	
		public void addLayoutComponent(String name, Component comp) {}

		public void layoutContainer(Container parent) {
			final int buffer = 3;
			final Insets insets = parent.getInsets();
			int curY = insets.top + buffer;
			int curX = insets.left + buffer;
			
			final Dimension atomSize = atomsBondsButton.getPreferredSize();
			final Dimension ribbonsSize = ribbonsButton.getPreferredSize();
			
			atomsBondsButton.setBounds(curX, curY, atomSize.width, atomSize.height);
			curX += atomSize.width + buffer;
			ribbonsButton.setBounds(curX, curY, ribbonsSize.width, ribbonsSize.height);
			curX += ribbonsSize.width + buffer;
			
			Container parentParent = parent.getParent();
			Insets parentParentInsets = parentParent.getInsets();
			this.size.width = parentParent.getWidth() - parentParentInsets.left - parentParentInsets.right;
			this.size.height = curY + ribbonsSize.height + buffer + insets.bottom;
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
	
	public PickLevelPanel() {
		super(null, false);
		this.setLayout(new CustomLayout());
		super.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("2)  Choose what you want the tool to affect."),
                BorderFactory.createEmptyBorder(-1,1,1,1)));

		this.group.add(this.atomsBondsButton);
		this.group.add(this.ribbonsButton);
		
		super.add(this.atomsBondsButton);
		super.add(this.ribbonsButton);
		
		this.atomsBondsButton.addActionListener(this);
		this.ribbonsButton.addActionListener(this);
		
		AppBase.sgetUpdateController().registerListener(this);
		this.reset();
	}

	public void actionPerformed(final ActionEvent e) {
		if(this.atomsBondsButton.isSelected()) {
			PickLevel.pickLevel = PickLevel.COMPONENTS_ATOMS_BONDS;
		} else if(this.ribbonsButton.isSelected()) {
			PickLevel.pickLevel = PickLevel.COMPONENTS_RIBBONS;
		}
		
		ProteinWorkshop.sgetActiveFrame().getTreeViewer().tree.repaint();
		ProteinWorkshop.sgetActiveFrame().getStylesOptionsPanel().updatePickLevel(PickLevel.pickLevel);
	}

	public void reset()
	{
		this.atomsBondsButton.doClick();
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
