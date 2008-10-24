package org.rcsb.lx.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import org.rcsb.mbt.glscene.jogl.SequencePanelBase;
import org.rcsb.mbt.model.Structure;


@SuppressWarnings("serial")
public class SequenceStructureTitlePanel extends SequencePanelBase
{
	private String title = null;
	private static Rectangle2D titleBounds = null;
	
	public SequenceStructureTitlePanel(Structure struc)
	{

		title = "Structure: " + struc.getStructureMap().getPdbId();
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D)g;

		// only make a new image if something has changed.
        final Dimension newSize = super.getSize();
        
        if (newSize.equals(oldSize) && oldImage != null)
        {
           super.paintComponent(g);
           g2.drawImage(oldImage,null,0,0);
           return; 
        }
        
        oldSize = newSize;
        
        oldImage = new BufferedImage(newSize.width, newSize.height, BufferedImage.TYPE_INT_RGB);
        final Graphics2D buf = (Graphics2D)oldImage.getGraphics();
        
        heightForWidth(newSize.width);

        buf.setFont(descriptionFont);
        buf.setColor(Color.cyan);
        buf.drawString(title, descriptionBarPadding, (int)titleBounds.getHeight() + 5);
        
        // draw the background, etc.
        super.paintComponent(g2);
        
        // draw the buffer
        g2.drawImage(oldImage,null,0,0);
	}
	
	@Override
	public void heightForWidth(int width)
	{
		if (isDirty)
		{
			Graphics g = getGraphics();
	        final FontMetrics fontMetrics = g.getFontMetrics(descriptionFont);
			titleBounds = fontMetrics.getStringBounds(title, g);
	        preferredHeight = (int)titleBounds.getHeight() + 10;
	        isDirty = false;
		}
	}
}
