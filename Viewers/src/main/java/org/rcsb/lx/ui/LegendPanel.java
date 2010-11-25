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
package org.rcsb.lx.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

import javax.swing.JPanel;

/**
 * 
 * a panel that draws a dashed line of color 'c' on a black box
 * (representing the viewer's background at the right of the panel. The
 * panel's background color is also passed.

 * **J added a background color parameter to functionally separate this from
 * the sidebar.
 * 
 * @author rickb
 *
 */
class LegendPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3714854117661569915L;
	Color c;

	LegendPanel(final Color c, final Color background) {
		super();
		this.c = c;
		this.setBackground(background);
	}

	public void paintComponent(final Graphics g) {
		super.paintComponent(g);
		// move the context so that it draws to the right of the text and in
		// relation to the center of the box vertically
		final int panelHeight = this.getHeight();
		g.translate(0, panelHeight / 2);
		// this class has more flexibility
		final Graphics2D g2d = (Graphics2D) g;

		// represents the viewer's black background
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, -5, 50, 10);

		// draw the representative dashed line
		g2d.setColor(this.c);
		final Stroke dashed = new BasicStroke(2.0f, // line width
				/* cap style */
				BasicStroke.CAP_BUTT,
				/* join style, miter limit */
				BasicStroke.JOIN_BEVEL, 1.0f,
				/* the dash pattern */
				new float[] { 8.0f, 8.0f },
				/* the dash phase */
				0.0f); /* on 8, off 8 */
		g2d.setStroke(dashed);
		g2d.drawLine(5, 0, 45, 0);
	}
}

