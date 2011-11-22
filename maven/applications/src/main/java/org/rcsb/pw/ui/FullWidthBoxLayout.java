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
package org.rcsb.pw.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.Vector;

import javax.swing.JSeparator;

public class FullWidthBoxLayout implements LayoutManager {
    private final Dimension preferredSize = new Dimension(-1,-1);
    private final Dimension minimumSize = new Dimension(-1,-1);
    
    public enum LayoutAxis { AXIS_X, AXIS_Y }
    
    public LayoutAxis axis = null;
    
    public FullWidthBoxLayout() 
    {
    	this(LayoutAxis.AXIS_Y);
    }
    
    public FullWidthBoxLayout(final LayoutAxis axis) {
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
        
        if(this.axis == LayoutAxis.AXIS_Y) {
	        int firstColumnMaxWidth = 0;
	        
	        final Component[] firstColumn = container.getComponents();
	        int curY = insets.top;
	        final Vector<Component> descriptionPanels = new Vector<Component>();
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

	        // TODO -pr panel too wide
//	        for (Component comp : descriptionPanels)
//	        	comp.setBounds(insets.left + firstColumnMaxWidth + xBuffer,
//	        				   insets.top, 170, curY - insets.top);
	        
	        for (Component comp : descriptionPanels)
	        	comp.setBounds(insets.left + firstColumnMaxWidth + xBuffer,
	        				   insets.top, 120, curY - insets.top);
	        
	        curY += insets.bottom;
	        
	        this.preferredSize.height = curY;
	        this.preferredSize.width = 0;//parent.getWidth();
	        this.minimumSize.height = curY;
	        this.minimumSize.width = 0;
        } else if(this.axis == LayoutAxis.AXIS_X) {
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
