package org.rcsb.pw.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JSeparator;

public class FullWidthBoxLayout implements LayoutManager {
    private final Dimension preferredSize = new Dimension(-1,-1);
    private final Dimension minimumSize = new Dimension(-1,-1);
    public static int AXIS_X = 0;
    public static int AXIS_Y = 1;
    
    public int axis = -1;
    
    public FullWidthBoxLayout() {
    	this(FullWidthBoxLayout.AXIS_Y);
    }
    
    public FullWidthBoxLayout(final int axis) {
    	this.axis = axis;
    }
    
    public void removeLayoutComponent(final Component comp) { }

    public void layoutContainer(final Container parent) {
        this.setSize(parent);
    }
    
    private void setSize(final Container container) {
        // if the component has not been resized, this is not necessary...
//        Dimension newSize = container.getSize();
//        if(this.lastSize != null && this.lastSize.equals(newSize)) {
//            return;
//        }
//        this.lastSize = newSize;
        
        final Insets insets = container.getInsets();
        
        if(this.axis == FullWidthBoxLayout.AXIS_Y) {
	        int firstColumnMaxWidth = 0;
	        
	        final Component[] firstColumn = container.getComponents();
	        int curY = insets.top;
	        final Vector descriptionPanels = new Vector();
	        for(int i = 0; i < firstColumn.length; i++) {
	        	if(firstColumn[i].isVisible()) {
		        	if(firstColumn[i] instanceof JSeparator) {
		        		final int parentWidth = container.getWidth() - insets.left - insets.right;
		        		final int twentyPercentHalfWidth = (int)((parentWidth / 2) * .2);
		        		final int startX = insets.left + twentyPercentHalfWidth;
		        		final int width = parentWidth - twentyPercentHalfWidth * 2;
		        		final int height = firstColumn[i].getPreferredSize().height;
		        		firstColumn[i].setBounds(startX, curY, width, height);
		        		curY += height;
		        	} else if(firstColumn[i] instanceof DescriptionPanel){	// handle the description panels separately below
		        		descriptionPanels.add(firstColumn[i]);
		        	} else {
		        		final Dimension size = firstColumn[i].getPreferredSize();
		        		firstColumn[i].setBounds(insets.left, curY, size.width, size.height);
		        		curY += size.height;
		        		firstColumnMaxWidth = Math.max(firstColumnMaxWidth, size.width);
		        	}
	        	}
	        }
	        
	        final int xBuffer = 5;
	        
	        final Iterator panelIt = descriptionPanels.iterator();
	        while(panelIt.hasNext()) {
	        	final JPanel panel = (JPanel)panelIt.next();
	        	panel.setBounds(insets.left + firstColumnMaxWidth + xBuffer, insets.top, 170, curY - insets.top);
	        }
	        
	        curY += insets.bottom;
	        
	        this.preferredSize.height = curY;
	        this.preferredSize.width = 0;//parent.getWidth();
	        this.minimumSize.height = curY;
	        this.minimumSize.width = 0;
        } else if(this.axis == FullWidthBoxLayout.AXIS_X) {
        	final Component[] comps = container.getComponents();
        	int curX = insets.left;
        	final int visualBuffer = 3;
        	
        	for(int i = 0; i < comps.length; i++) {
        		final Dimension dim = comps[i].getPreferredSize();
        		comps[i].setBounds(curX, insets.top, dim.width, dim.height);
        		
        		curX += visualBuffer + dim.width;
        		
        		this.preferredSize.height = dim.height;
        	}
        	
        	this.preferredSize.height += insets.top + insets.bottom; 
        	this.preferredSize.width = 0;
        	this.minimumSize.height = this.preferredSize.height;
        	this.minimumSize.width = this.preferredSize.width;
        }
    }

    public void addLayoutComponent(final String name, final Component comp) {}

    public Dimension minimumLayoutSize(final Container parent) {
        this.setSize(parent);
        
        return this.minimumSize;
    }

    public Dimension preferredLayoutSize(final Container parent) {
        this.setSize(parent);
        
        return this.preferredSize;
    }
    
    public void setDescriptionPanel(final DescriptionPanel descriptionPanel) {
    }
}
