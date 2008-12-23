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
