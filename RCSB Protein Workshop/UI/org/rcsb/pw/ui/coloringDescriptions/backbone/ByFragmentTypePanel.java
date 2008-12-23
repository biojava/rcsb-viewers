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
import java.util.Hashtable;

import org.rcsb.mbt.model.StructureComponentRegistry;
import org.rcsb.mbt.model.StructureComponentRegistry.ComponentType;
import org.rcsb.mbt.model.attributes.ResidueColorByFragmentType;
import org.rcsb.mbt.model.interim.Conformation;
import org.rcsb.pw.ui.DescriptionPanel;
import org.rcsb.uiApp.controllers.app.AppBase;
import org.rcsb.uiApp.ui.dialogs.ColorChooserDialog;




public class ByFragmentTypePanel extends DescriptionPanel implements MouseListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -71635146298791416L;
	// parallele arrays
	private static final String[] labels = {"Turn", "Coil", "Helix", "Strand", "Unknown"};
	private static final ComponentType[] identifiers =
		{ComponentType.TURN, ComponentType.COIL, ComponentType.HELIX, ComponentType.STRAND, ComponentType.UNDEFINED_CONFORMATION}; 
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
