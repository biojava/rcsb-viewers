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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import org.rcsb.mbt.model.Structure;
import org.rcsb.vf.glscene.jogl.SequencePanelBase;


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
