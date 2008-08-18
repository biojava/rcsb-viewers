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

