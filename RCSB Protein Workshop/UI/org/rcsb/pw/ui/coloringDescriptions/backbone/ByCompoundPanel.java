package org.rcsb.pw.ui.coloringDescriptions.backbone;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.TreeMap;

import org.rcsb.mbt.model.attributes.ResidueColorByResidueCompound;
import org.rcsb.pw.ui.DescriptionPanel;
import org.rcsb.uiApp.controllers.app.AppBase;
import org.rcsb.uiApp.ui.dialogs.ColorChooserDialog;




public class ByCompoundPanel extends DescriptionPanel implements MouseListener
{
	private static final long serialVersionUID = -4107738767627452238L;

	// parallel to ResidueColorByResidueCompound.colorByCompound iterator.
	private static Point[] boxTopLeftPoints = null;
	
	private static final Dimension colorBoxDimension = new Dimension(-1,-1);
	
	private static final int columnCount = 4;
	
	public ByCompoundPanel() {
		
		super.addMouseListener(this);
	}
	
	
	public void paintComponent(final Graphics g) {
		//Graphics2D g_ = (Graphics2D)g;
		//g.clearRect(0, 0, super.getWidth(), super.getHeight());
		super.paintComponent(g);
		
		final TreeMap map = ResidueColorByResidueCompound.colorByCompound;	// use an instance to make sure the static constructor is used.
		final int mapSize = map.size();
		final int columnLength = mapSize / ByCompoundPanel.columnCount + 1;
		
		if(ByCompoundPanel.boxTopLeftPoints == null || ByCompoundPanel.boxTopLeftPoints.length != mapSize) {
			ByCompoundPanel.boxTopLeftPoints = new Point[mapSize];
			for(int i = 0; i < mapSize; i++) {
				ByCompoundPanel.boxTopLeftPoints[i] = new Point(-1,-1);
			}
		}
		
		final Insets insets = super.getInsets();
		final int lineHeight = g.getFontMetrics().getHeight();
		ByCompoundPanel.colorBoxDimension.height = lineHeight;
		ByCompoundPanel.colorBoxDimension.width = lineHeight;
		
		final int xBuffer = 5;
		final int yBuffer = 2;
		
		final Iterator keyIt = map.keySet().iterator();
		final Iterator valIt = map.values().iterator();
		
		// for each column...
		int columnStartX = insets.left;
		for(int i = 0; i < ByCompoundPanel.columnCount; i++) {
			// draw the labels
			g.setColor(Color.BLACK);
			int maxWidth = -1;
			int curY = 0;
			for(int j = 0; j < columnLength && keyIt.hasNext(); j++) {
				final String compound = (String)keyIt.next();
				
				final Rectangle2D rect = g.getFontMetrics().getStringBounds(compound, g);
				maxWidth = (int)Math.max(maxWidth, rect.getWidth());
				curY += rect.getHeight() + yBuffer;
				
				g.drawString(compound, columnStartX, curY);
			}
			
			// draw the color boxes
			curY = 0;
			for(int j = 0; j < columnLength && valIt.hasNext(); j++) {
				final Color color = (Color)valIt.next();
				final Point point = ByCompoundPanel.boxTopLeftPoints[i * columnLength + j];
				
				g.setColor(color);
				
				point.x = columnStartX + maxWidth + xBuffer;
				point.y = curY;
				g.fillRect(point.x, point.y, ByCompoundPanel.colorBoxDimension.width, ByCompoundPanel.colorBoxDimension.height);
				
				curY += ByCompoundPanel.colorBoxDimension.height + yBuffer;
			}
			
			columnStartX += maxWidth + xBuffer * 2 + ByCompoundPanel.colorBoxDimension.width;
		}
	}

	public void mouseClicked(final MouseEvent e) {
		final Point sourcePoint = e.getPoint();
		
		for(int i = 0; i < ByCompoundPanel.boxTopLeftPoints.length; i++) {
			final Point boxPoint = ByCompoundPanel.boxTopLeftPoints[i];
			if(sourcePoint.y >= boxPoint.y && sourcePoint.y <= boxPoint.y + ByCompoundPanel.colorBoxDimension.height 
					&& sourcePoint.x >= boxPoint.x && sourcePoint.x <= boxPoint.x + ByCompoundPanel.colorBoxDimension.width) {
				final String compound = (String)ResidueColorByResidueCompound.colorByCompound.keySet().toArray()[i];
				Color color = (Color)ResidueColorByResidueCompound.colorByCompound.values().toArray()[i];
				
				final ColorChooserDialog dialog = new ColorChooserDialog(AppBase.sgetActiveFrame());
				dialog.setColor(color);
				if(dialog.showDialog()) {
					color = dialog.getColor();
					ResidueColorByResidueCompound.colorByCompound.put(compound, color);
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
