package org.rcsb.lx.ui;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.rcsb.lx.controllers.app.LigandExplorer;
import org.rcsb.lx.controllers.update.LXUpdateEvent;
import org.rcsb.lx.model.LXModel;
import org.rcsb.mbt.controllers.update.IUpdateListener;
import org.rcsb.mbt.controllers.update.UpdateEvent;
import org.rcsb.mbt.model.Structure;



public class ContactMap extends JPanel implements IUpdateListener
{

	/* (non-Javadoc)
	 * @see edu.sdsc.mbt.views_controller.IViewUpdateListener#handleModelChangedEvent(edu.sdsc.mbt.views_controller.ViewUpdateEvent)
	 */
	private static final long serialVersionUID = 2902801421108589221L;

	public ContactMap_ContactingResiduesPane contactingResiduesPane = null;

	public ContactMap_LigandAtomsPane ligandAtomsPane = null;

	public ContactMap_InformationPane informationPane = null;
	
	public ContactMap_SquarePanel squarePane = null;

	public JScrollPane horizontalScrollPane = null;

	public JScrollPane verticalScrollPane = null;

	public static final int GRID_WIDTH = 20;
	public static final int GRID_SEPARATOR_WIDTH = 20;
	public static final int GRID_HEIGHT = 20;
	
	public static final int INFORMATION_PANE_WIDTH = 350;
	
	public BufferedImage createImage() {
		final int visualBuffer = 0;
		
		int totalWidth = this.contactingResiduesPane.fullSize.width + this.ligandAtomsPane.fullSize.width;
		int totalHeight = this.contactingResiduesPane.fullSize.height + this.ligandAtomsPane.fullSize.height;
		BufferedImage image = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_RGB);
		
		Graphics2D g = (Graphics2D)image.getGraphics();
		g.setBackground(LXDocumentFrame.sidebarColor);
		g.clearRect(0, 0, totalWidth, totalHeight);
		
		int startLigandX = 0;
		int startLigandY = 0;
		g.drawImage(this.ligandAtomsPane.image, null, startLigandX, startLigandY);
		
		int startContactingX = this.ligandAtomsPane.fullSize.width;
		int startContactingY = this.ligandAtomsPane.fullSize.height;
		g.drawImage(this.contactingResiduesPane.image, null, startContactingX, startContactingY);
		
		int startSquareX = this.ligandAtomsPane.fullSize.width;
		int startSquareY = 0;
		g.drawImage(this.squarePane.image, null, startSquareX, startSquareY);
		
		return image;
	}

	// placed only when all components have been properly set up.
	public LayoutManager layoutManager = new LayoutManager() {
		public void addLayoutComponent(String name, Component comp) {
		}

		public void layoutContainer(Container parent) {
			final int padding = 0;
			final Insets insets = ContactMap.super.getInsets();
			final Dimension contactingDim = ContactMap.this.contactingResiduesPane.fullSize;
			final Dimension ligandDim = ContactMap.this.ligandAtomsPane.fullSize;
			final int parentHeight = ContactMap.this.getHeight();
			final int parentWidth = ContactMap.this.getWidth();
			final int horizontalScrollBarHeight = ContactMap.this.horizontalScrollPane
					.getHorizontalScrollBar().getPreferredSize().height;
			final int verticalScrollBarWidth = ContactMap.this.verticalScrollPane
					.getVerticalScrollBar().getPreferredSize().width;
			final int verticalBarStartY = insets.top;
			final int verticalBarEndY = parentHeight - insets.bottom
					- horizontalScrollBarHeight - contactingDim.height
					- padding;
			final int verticalBarHeight = verticalBarEndY - verticalBarStartY
					+ 1; // 1: minor visual detail. Causes borders to
							// overlap.
			final int horizontalBarStartX = insets.left + ligandDim.width
					+ verticalScrollBarWidth + padding;
			final int horizontalBarEndX = parentWidth - ContactMap.INFORMATION_PANE_WIDTH - padding - insets.right;
			final int horizontalBarWidth = horizontalBarEndX
					- horizontalBarStartX;
			final int informationPaneStartX = horizontalBarEndX + padding;

			// ContactMap.this.horizontalScrollBar.setBounds(horizontalBarStartX,
			// parentHeight - horizontalScrollBarHeight - insets.bottom,
			// horizontalBarWidth, horizontalScrollBarHeight);
			// ContactMap.this.verticalScrollBar.setBounds(insets.left,
			// insets.top, verticalScrollBarWidth, verticalBarHeight);

			ContactMap.this.horizontalScrollPane.setBounds(horizontalBarStartX,
					parentHeight - insets.bottom - contactingDim.height - horizontalScrollBarHeight,
					horizontalBarWidth, contactingDim.height
							+ horizontalScrollBarHeight);
			ContactMap.this.verticalScrollPane.setBounds(insets.left,
					insets.top, ligandDim.width + verticalScrollBarWidth,
					verticalBarHeight);

			final int startSquareX = insets.left + verticalScrollBarWidth
					+ ligandDim.width + padding;
			final int squareWidth = parentWidth - startSquareX - insets.right - ContactMap.INFORMATION_PANE_WIDTH - padding;
			final int startSquareY = insets.top;
			final int squareHeight = parentHeight - insets.bottom
					- horizontalScrollBarHeight - contactingDim.height
					- startSquareY - padding;

			ContactMap.this.squarePane.setBounds(startSquareX, startSquareY,
					squareWidth, squareHeight);
			
			ContactMap.this.informationPane.setBounds(informationPaneStartX, insets.top, parentWidth - insets.right - informationPaneStartX, parentHeight - insets.top - insets.bottom);
		}

		public Dimension minimumLayoutSize(Container parent) {
			return null;
		}

		public Dimension preferredLayoutSize(Container parent) {
			return null;
		}

		public void removeLayoutComponent(Component comp) {
		}

	};

	public ContactMap()
	{
		super(null, false);

		super.setBackground(LXDocumentFrame.sidebarColor);
		this.setup();
		LXModel model = LigandExplorer.sgetModel();
		model.setContactMap(this);
		LigandExplorer.sgetActiveFrame().getUpdateController().registerListener(this);
	}
	
	private void setup() {
		this.contactingResiduesPane = new ContactMap_ContactingResiduesPane(
				this);
		this.ligandAtomsPane = new ContactMap_LigandAtomsPane(
				this);
		this.informationPane = new ContactMap_InformationPane();
		this.squarePane = new ContactMap_SquarePanel(this);
		this.verticalScrollPane = new JScrollPane(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		this.horizontalScrollPane = new JScrollPane(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		this.verticalScrollPane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		
		this.verticalScrollPane.getVerticalScrollBar().addAdjustmentListener(this.squarePane.verticalAdjustmentListener);
		this.horizontalScrollPane.getHorizontalScrollBar().addAdjustmentListener(this.squarePane.horzontalAdjustmentListener);
	}

	public void setComponents() {
		super.removeAll();

		// make sure everything calls its paintComponent() method. The layout
		// manager will eventually be set by this.squarePane's paintComponent() method.
		// this.horizontalScrollPane.setBounds(0, 0, 1, 1);
		// this.verticalScrollPane.setBounds(1, 1, 1, 1);
		this.contactingResiduesPane.setBounds(2, 2, 1, 1);
		this.ligandAtomsPane.setBounds(3, 3, 1, 1);
		this.squarePane.setBounds(4, 4, 1, 1);

		// super.add(this.horizontalScrollPane);
		// super.add(this.verticalScrollPane);

		// these are only added so that their paintComponent() functions will be
		// called. They will be replaced by their scrollpanes later.
		super.add(this.contactingResiduesPane);
		super.add(this.ligandAtomsPane);

		super.add(this.squarePane);
		super.add(this.informationPane);
	}
	
	public void clearStructure(boolean transitory) {
		super.setLayout(null);
		super.removeAll();
		if(!transitory) {
			super.revalidate();
			super.repaint();
		}
	}

	public void newStructureAdded(final Structure struc) {
		super.removeAll();
		this.setup();
		this.setComponents();
		super.repaint();
		super.revalidate();
	}

	public void reset()
	{
		this.clearStructure(false);
		this.newStructureAdded(null);
	}
	
	public void handleModelChangedEvent(UpdateEvent evt)
	{
		boolean transitory = false;
		
		if (evt instanceof LXUpdateEvent)
			transitory = ((LXUpdateEvent)evt).transitory;
		
		switch(evt.action)
		{
		case VIEW_RESET:
			reset();
			break;
			
		case CLEAR_ALL:
			clearStructure(transitory);
			break;
			
		case STRUCTURE_ADDED:
			newStructureAdded(evt.structure);
			break;
		}
		
	}


}
