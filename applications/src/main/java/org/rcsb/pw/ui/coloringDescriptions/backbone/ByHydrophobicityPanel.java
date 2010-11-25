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
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import org.rcsb.mbt.model.attributes.InterpolatedColorMap;
import org.rcsb.mbt.model.attributes.ResidueColorByHydrophobicity;
import org.rcsb.pw.ui.DescriptionPanel;
import org.rcsb.pw.ui.dialogs.ColorMapDialog;
import org.rcsb.uiApp.controllers.app.AppBase;




public class ByHydrophobicityPanel extends DescriptionPanel
{
	private static final long serialVersionUID = -8856342835983531455L;

	public ByHydrophobicityOptions options = new ByHydrophobicityOptions();
	
	public String startLabel = "least";
	public String endLabel = "most";
	
	public ByHydrophobicityPanel() {	
		final InterpolatedColorMap map = (InterpolatedColorMap)ResidueColorByHydrophobicity.create().getColorMap().clone();
		this.options.setCurrentColorMap(map);
		
		//super.addMouseListener(this);
	}
	
	
	public void paintComponent(final Graphics g) {
		//Graphics2D g_ = (Graphics2D)g;
		//g.clearRect(0, 0, super.getWidth(), super.getHeight());
		super.paintComponent(g);
		
		//Font oldFont = g.getFont();
		final Rectangle2D startBounds = g.getFontMetrics().getStringBounds(this.startLabel, g);
		final Rectangle2D endBounds = g.getFontMetrics().getStringBounds(this.endLabel, g);
		
		final InterpolatedColorMap colorMap = this.options.getCurrentColorMap();
		
		final float[] color = {0,0,0};		
		
		final Insets insets = super.getInsets();
		
		// draw the color ramp
		final int rampStartX = 15;
		final int rampWidth = (int)Math.max(startBounds.getWidth(), endBounds.getWidth());
		final int rampStartY = (int)(startBounds.getHeight() + 3 + insets.top);
		final int rampEndY = (int)(super.getHeight() - endBounds.getHeight() - insets.bottom);
		final int rampHeight = rampEndY - rampStartY;
		final float sampleStep = 1.0f / rampHeight;
		for ( int i=0; i < rampHeight; i++ )
		{
			final float f = sampleStep * i;
			colorMap.getColor( f, color );
			final Color colorObject = new Color( color[0], color[1], color[2] );
			g.setColor( colorObject );
			final int curY = i + rampStartY;
			g.drawLine( rampStartX, curY, rampStartX + rampWidth, curY );
		}
		
		// draw the labels
		g.setColor(Color.BLACK);
		g.drawString(this.startLabel, rampStartX, (int)startBounds.getHeight());
		g.drawString(this.endLabel, rampStartX, super.getHeight());
	}

	public void mouseClicked(final MouseEvent e) {
		final ColorMapDialog dialog = new ColorMapDialog(AppBase.sgetActiveFrame(), this.options.getCurrentColorMap());
		dialog.show();
		super.repaint();
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
