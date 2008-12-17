package org.rcsb.vf.glscene.jogl;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public abstract class SequencePanelBase extends JPanel
{
    protected Dimension oldSize = new Dimension(-1,-1);
    public BufferedImage oldImage = null;
    public int preferredHeight = 0;
    
    public boolean isDirty = true;
    
	protected static final int descriptionBarPadding = 10;
	protected static int decriptionFontSize = 16;
	protected static final Font descriptionFont = new Font( "SansSerif", Font.PLAIN, decriptionFontSize );
	
	/**
	 * Constructor
	 */
	protected SequencePanelBase()
	{
		super(null, false);
		super.setDoubleBuffered(false);
	}
	
	/**
	 * Calculate and set the preferred height value for a given width
	 * 
	 * @param width
	 */
	public abstract void heightForWidth(int width);
}
