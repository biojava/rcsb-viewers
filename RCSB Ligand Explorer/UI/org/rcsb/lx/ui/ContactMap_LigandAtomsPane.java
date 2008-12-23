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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.font.LineMetrics;
import java.awt.image.BufferedImage;
import java.util.Vector;

import javax.swing.JPanel;

import org.rcsb.lx.controllers.app.LigandExplorer;
import org.rcsb.lx.glscene.jogl.ResidueFontInfo;
import org.rcsb.lx.model.LXModel;
import org.rcsb.mbt.model.Atom;
import org.rcsb.mbt.model.Residue;
import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.StructureMap;
import org.rcsb.mbt.model.attributes.StructureStyles;
import org.rcsb.mbt.model.util.PdbToNdbConverter;
import org.rcsb.mbt.model.Chain;


public class ContactMap_LigandAtomsPane extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1883207852680134190L;

	public BufferedImage image = null;
	
	public Vector atomRanges = new Vector();
	
	public boolean isInitialized = false;	// has this class been initialized yet?
	
	private ContactMap parent;
	
	private static LineMetrics letterMetrics = null;

	public ContactMap_LigandAtomsPane(final ContactMap parent) {
		super(null, false);
		this.parent = parent;

//		super.setBorder(BorderFactory.createLineBorder(Color.white));
		
		super.setBackground(LXDocumentFrame.sidebarColor);
	}
	
	public class AtomRange {
		public Atom atom = null;
		public String atomDescription = null;
		public int startY = -1;
		public int endY = -1; 
	}
	
	
	
	public Dimension fullSize = new Dimension(0,0);

	@Override
	public Dimension getPreferredSize() {
		return this.fullSize;
	}

	@Override
	public void paintComponent(final Graphics g) {
		super.paintComponent(g);

		final Graphics2D g2 = (Graphics2D)g;
		final Insets insets = super.getInsets();
		
		final LXModel model = LigandExplorer.sgetModel();
		
		// hack.
		if (!model.hasStructures())
			return;

		final Structure struc = model.getStructures().get(0);
		final StructureMap sm = struc.getStructureMap();
		final StructureStyles ss = sm.getStructureStyles();
		
		final int padding = 3;
		
		// initialize, if necessary...
		if(!this.isInitialized)
		{
			final Residue currentLigandResidues[] = LigandExplorer.sgetSceneController().getLigandResidues();
			
			if(ContactMap_LigandAtomsPane.letterMetrics == null) {
				ContactMap_LigandAtomsPane.letterMetrics = ResidueFontInfo.contactsResidueFont.getLineMetrics("M",g2.getFontRenderContext());
			}
			final int letterHeight = (int)ContactMap_LigandAtomsPane.letterMetrics.getAscent();
			
			// if no ligand has been specified, bail.
			if(currentLigandResidues == null) {
				return;
			}
			
			this.fullSize.width = -1;
			this.atomRanges.clear();
			
			int curY = insets.top;
			for (Residue residue : currentLigandResidues)
				for (Atom a : residue.getAtoms())
				{
					final int stringWidth = (int)ResidueFontInfo.contactsResidueFont.getStringBounds(a.name,g2.getFontRenderContext()).getWidth();
					
					final AtomRange range = new AtomRange();
					range.atomDescription = a.name;
					range.atom = a;
					range.startY = curY;
					curY += letterHeight;
					range.endY = curY;
					this.atomRanges.add(range);
					
					this.fullSize.width = Math.max(this.fullSize.width, stringWidth);
					
					curY += padding;
				}
			
			this.fullSize.width += insets.left + insets.right + padding * 2 + 2;	// hack. The scrollpane adds a border of its own. May not be same for all platforms. 
			this.fullSize.height = curY + insets.top + insets.bottom;
		}
		
		// if no buffer exists (initial paint, or a repaint request)...
		if(this.image == null) {
			this.image = new BufferedImage(this.fullSize.width, this.fullSize.height, BufferedImage.TYPE_INT_RGB);
			final Graphics2D buf = (Graphics2D)this.image.getGraphics();
			
			buf.setBackground(LXDocumentFrame.sidebarColor);
			buf.clearRect(0, 0, this.image.getWidth(), this.image.getHeight());
			
			buf.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			
			buf.setColor(Color.black);
			buf.setFont(ResidueFontInfo.contactsResidueFont);
			
			final int count = this.atomRanges.size();
			for(int i = 0; i < count; i++) {
				final AtomRange range = (AtomRange)this.atomRanges.get(i);
				
				// draw the atom description
				buf.drawString(range.atomDescription, insets.left + padding, range.endY);
			}
		}
		
		// draw the buffered image...
		g2.drawImage(this.image, null, insets.left, insets.top);
		
		if(!this.isInitialized) {
			this.isInitialized = true;
			this.parent.squarePane.repaint();	// repaint the grid, now that this pane has been initialized. It will call the layout manager when it's finished.
		}
	}
}
