package org.rcsb.pw.ui.coloringDescriptions.backbone;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;

import org.rcsb.mbt.model.attributes.InterpolatedColorMap;
import org.rcsb.mbt.model.attributes.ResidueColorByResidueIndex;
import org.rcsb.pw.ui.DescriptionPanel;
import org.rcsb.pw.ui.dialogs.ColorMapDialog;
import org.rcsb.uiApp.controllers.app.AppBase;




public class ByIndexPanel extends DescriptionPanel implements MouseListener
{
	private static final long serialVersionUID = 7514109359122970635L;

	public ByIndexOptions options = new ByIndexOptions();
	
	public String startLabel = "Start";
	public String endLabel = "End";
	
	public ByIndexPanel() {
		final InterpolatedColorMap map = (InterpolatedColorMap)ResidueColorByResidueIndex.create().getColorMap().clone();
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
