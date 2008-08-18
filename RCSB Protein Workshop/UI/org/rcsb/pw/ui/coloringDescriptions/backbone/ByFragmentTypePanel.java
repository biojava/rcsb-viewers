package org.rcsb.pw.ui.coloringDescriptions.backbone;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.util.Hashtable;

import org.rcsb.mbt.controllers.app.AppBase;
import org.rcsb.mbt.model.Conformation;
import org.rcsb.mbt.model.StructureComponentRegistry;
import org.rcsb.mbt.model.attributes.ResidueColorByFragmentType;
import org.rcsb.mbt.ui.dialogs.ColorChooserDialog;
import org.rcsb.pw.ui.DescriptionPanel;




public class ByFragmentTypePanel extends DescriptionPanel implements MouseListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -71635146298791416L;
	// parallele arrays
	private static final String[] labels = {"Turn", "Coil", "Helix", "Strand", "Unknown"};
	private static final String[] identifiers = {StructureComponentRegistry.TYPE_TURN, StructureComponentRegistry.TYPE_COIL, StructureComponentRegistry.TYPE_HELIX, StructureComponentRegistry.TYPE_STRAND, Conformation.TYPE_UNDEFINED}; 
	private static final Point[] boxTopLeftPoints = {new Point(-1,-1),new Point(-1,-1),new Point(-1,-1),new Point(-1,-1),new Point(-1,-1)};
	
	private static final Dimension colorBoxDimension = new Dimension(-1,-1);
	
	public ByFragmentTypePanel() {
		super.addMouseListener(this);
	}
	
	
	public void paintComponent(final Graphics g) {
		//Graphics2D g_ = (Graphics2D)g;
		//g.clearRect(0, 0, super.getWidth(), super.getHeight());
		super.paintComponent(g);
		
		final Insets insets = super.getInsets();
		final int lineHeight = g.getFontMetrics().getHeight();
		ByFragmentTypePanel.colorBoxDimension.height = lineHeight;
		ByFragmentTypePanel.colorBoxDimension.width = lineHeight;
		
		final int xBuffer = 5;
		final int yBuffer = 2;
		
		// draw the labels
		g.setColor(Color.BLACK);
		int maxWidth = -1;
		int curY = 0;
		for(int i = 0; i < ByFragmentTypePanel.labels.length; i++) {
			final String label = ByFragmentTypePanel.labels[i];
			
			final Rectangle2D rect = g.getFontMetrics().getStringBounds(label, g);
			maxWidth = (int)Math.max(maxWidth, rect.getWidth());
			curY += rect.getHeight() + yBuffer;
			
			g.drawString(label, insets.left, curY);
		}
		
		final Hashtable hash = ResidueColorByFragmentType.create().fragmentHash;
		
		// draw the color boxes
		curY = 0;
		for(int i = 0; i < ByFragmentTypePanel.labels.length; i++) {
			final float[] color = (float[])hash.get(ByFragmentTypePanel.identifiers[i]);
			final Point point = ByFragmentTypePanel.boxTopLeftPoints[i];
			
			final Color colorOb = new Color(color[0], color[1], color[2]);
			g.setColor(colorOb);
			
			point.x = maxWidth + xBuffer;
			point.y = curY;
			g.fillRect(point.x, point.y, ByFragmentTypePanel.colorBoxDimension.width, ByFragmentTypePanel.colorBoxDimension.height);
			
			curY += ByFragmentTypePanel.colorBoxDimension.height + yBuffer;
		}
	}

	public void mouseClicked(final MouseEvent e) {
		final Point sourcePoint = e.getPoint();
		
		for(int i = 0; i < ByFragmentTypePanel.boxTopLeftPoints.length; i++) {
			final Point boxPoint = ByFragmentTypePanel.boxTopLeftPoints[i];
			if(sourcePoint.y >= boxPoint.y && sourcePoint.y <= boxPoint.y + ByFragmentTypePanel.colorBoxDimension.height 
					&& sourcePoint.x >= boxPoint.x && sourcePoint.x <= boxPoint.x + ByFragmentTypePanel.colorBoxDimension.width) {
				final float[] color = (float[])ResidueColorByFragmentType.create().fragmentHash.get(ByFragmentTypePanel.identifiers[i]);
				Color colorOb = new Color(color[0], color[1], color[2]);
				
				final ColorChooserDialog dialog = new ColorChooserDialog(AppBase.sgetActiveFrame());
				dialog.setColor(colorOb);
				if(dialog.showDialog()) {
					colorOb = dialog.getColor();
					colorOb.getColorComponents(color);
				}
				
				super.repaint();
			}
		}
	}

	public void mouseEntered(final MouseEvent e) {
	}

	public void mouseExited(final MouseEvent e) {
	}

	public void mousePressed(final MouseEvent e) {
	}

	public void mouseReleased(final MouseEvent e) {
	}
}
