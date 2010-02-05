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
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.JPanel;

import org.rcsb.lx.controllers.app.LigandExplorer;
import org.rcsb.lx.glscene.jogl.ResidueFontInfo;
import org.rcsb.lx.model.ComparablePdbResidueId;
import org.rcsb.lx.model.InteractionMap;
import org.rcsb.lx.model.LXModel;
import org.rcsb.mbt.model.Residue;
import org.rcsb.mbt.model.util.ChemicalComponentInfo;


public class ContactMap_ContactingResiduesPane extends JPanel
{
	private static final long serialVersionUID = -1645531273397364464L;

	public BufferedImage image = null;
	
	public Vector residueRanges = new Vector();
	public Vector dividers = new Vector();
	public Vector chainLabels = new Vector();
	
	public boolean isInitialized = false;	// has this class been initialized yet?
	
	private ContactMap parent;
	public static final String BREAKPOINT_SYMBOL = "…";
	
	public ContactMap_ContactingResiduesPane(final ContactMap parent) {
		super(null, false);
		this.parent = parent;
//		super.setBorder(BorderFactory.createLineBorder(Color.white));
		
		super.setBackground(LigandExplorer.sgetActiveFrame().sidebarColor);
	}
	
	public class ResidueRange {
		public Residue residue = null;
		public int startX = -1;
		public int endX = -1;
	}
	
	public class Divider {
		public int startX = -1;
	}
	
	public class ChainLabel {
		public String pdbChainId = null;
		public int startX = -1;
		public int endX = -1;
	}
	
	public Dimension getPreferredSize() {
		return this.fullSize;
	}
	
	private static Rectangle2D letterBounds = null;
	private static LineMetrics letterMetrics = null;
	private static Rectangle2D residueIdBounds = null;
	private static LineMetrics residueIdMetrics = null;
//	private static int breakpointWidth = -1;
	
	public Dimension fullSize = new Dimension(0,0);
	
	public void paintComponent(final Graphics g) {
		super.paintComponent(g);
		
		final Graphics2D g2 = (Graphics2D)g;
		final Insets insets = super.getInsets();
		
		if(ContactMap_ContactingResiduesPane.letterBounds == null) {
			ContactMap_ContactingResiduesPane.letterBounds = ResidueFontInfo.contactsResidueFont.getStringBounds("M",g2.getFontRenderContext());
			ContactMap_ContactingResiduesPane.letterMetrics = ResidueFontInfo.contactsResidueFont.getLineMetrics("M",g2.getFontRenderContext());
			ContactMap_ContactingResiduesPane.residueIdBounds = ResidueFontInfo.contactsResidueIdFont.getStringBounds("1",g2.getFontRenderContext());
			ContactMap_ContactingResiduesPane.residueIdMetrics = ResidueFontInfo.contactsResidueIdFont.getLineMetrics("1",g2.getFontRenderContext());
//			ContactMap_ContactingResiduesPane.breakpointWidth = (int)ResidueFontInfo.contactsResidueFont.getStringBounds(ContactMap_ContactingResiduesPane.BREAKPOINT_SYMBOL,g2.getFontRenderContext()).getWidth();
		}
		
		final LXModel model = LigandExplorer.sgetModel();
		
		// hack.
		if (!model.hasStructures())
			return;
		
		// initialize, if necessary...
		if(!this.isInitialized) {
			final InteractionMap im = model.getInteractionMap();
			final Iterator residues = im.getResidues().iterator();
			final Residue currentLigandResidues[] = LigandExplorer.sgetSceneController().getLigandResidues();
			
			// if no ligand has been specified, bail.
			if(currentLigandResidues == null) {
				return;
			}
			
			// sift into chain arrays, sorted by residue number
			@SuppressWarnings("serial")
			class ByResidueId extends TreeMap<Comparable, Residue>{}
			final HashMap<String, ByResidueId> byChain = new HashMap<String, ByResidueId>(); // key: String chainId; value: TreeMap byResidueId <= Integer residueId; value: Residue
			while(residues.hasNext())
			{
				final Residue r = (Residue)residues.next();
				
				// don't report the current ligand's interactions with itself.
				if(r.getChainId().equals(currentLigandResidues[0].getChainId())) {
					continue;
				}
				
				String pdbChainId = r.getAuthorChainId();
				String pdbResId = String.valueOf(r.getAuthorResidueId());
				
				ByResidueId byResidueId = byChain.get(pdbChainId);
				if(byResidueId == null) {
					byResidueId = new ByResidueId();
					byChain.put(pdbChainId, byResidueId);
				}
				
				byResidueId.put(new ComparablePdbResidueId(pdbResId), r);	
			}
			
			int curX = insets.left + 3;	// visual buffer
			dividers.clear();
			residueRanges.clear();
			chainLabels.clear();
			
			for (String chainId : byChain.keySet())
			{
				final ByResidueId byResidue = byChain.get(chainId);
				
				final ChainLabel chainLabel = new ChainLabel();
				chainLabel.pdbChainId = chainId;
				chainLabel.startX = curX;
				
				for (Residue r : byResidue.values())
				{					
					// draw the residue symbol...
					final ResidueRange range = new ResidueRange();
					range.startX = curX;
					curX += ContactMap_ContactingResiduesPane.letterBounds.getWidth() + 5;	// add padding.
					range.endX = curX++;	// add an extra pixel so regions don't overlap.
					range.residue = r;
					residueRanges.add(range);
				}
				
				chainLabel.endX = curX;
				chainLabels.add(chainLabel);
				
				// add visual buffer between chains
				curX += 10;
			}
			
			this.fullSize.width = curX + insets.left + insets.right;
		}
		
		// if no buffer exists (initial paint, or a repaint request)...
		if(this.image == null) {
			final int letterHeight = (int)ContactMap_ContactingResiduesPane.letterMetrics.getAscent();
			final int residueIdHeight = (int)ContactMap_ContactingResiduesPane.residueIdMetrics.getAscent();
			
			int curY = insets.top + letterHeight;
			
			final int bracketHeight = 3;
			final int padding = 3;
			
			this.fullSize.height = (letterHeight + residueIdHeight * 2 + bracketHeight + padding * 4 + insets.top + insets.bottom);
			
			this.image = new BufferedImage(this.fullSize.width, this.fullSize.height, BufferedImage.TYPE_INT_RGB);
			final Graphics2D buf = (Graphics2D)this.image.getGraphics();
			
			buf.setBackground(LXDocumentFrame.sidebarColor);
			buf.clearRect(0, 0, this.image.getWidth(), this.image.getHeight());
			
//			buf.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			buf.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//			buf.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
			
			buf.setColor(Color.black);
			
			final int startResidueIdHeight = curY + padding + residueIdHeight;
			int count = this.residueRanges.size();
			for(int i = 0; i < count; i++) {
				final ResidueRange range = (ResidueRange)this.residueRanges.get(i);
				
				// draw the compound code
				buf.setFont(ResidueFontInfo.contactsResidueFont);
//				String residueLetter = AminoAcidInfo.getLetterFromCode(range.residue.getCompoundCode());
				String residueLetter = ChemicalComponentInfo.getLetterFromCode(range.residue.getCompoundCode());
//				if(residueLetter == null) {
//					residueLetter = "*";
//				}
				buf.drawString(residueLetter, range.startX, curY);
				
				String pdbResId = String.valueOf(range.residue.getAuthorResidueId());
				
				// draw the residue id
				buf.setFont(ResidueFontInfo.contactsResidueIdFont);
				buf.drawString(pdbResId, range.startX, startResidueIdHeight);
			}
			
			buf.setFont(ResidueFontInfo.contactsResidueFont);
			count = this.dividers.size();
			for(int i = 0; i < count; i++) {
				final Divider div = (Divider)this.dividers.get(i);
				
				buf.drawString(ContactMap_ContactingResiduesPane.BREAKPOINT_SYMBOL, div.startX, curY);
			}

			curY = startResidueIdHeight + padding;
			final int curYPlusBracketHeight = curY + bracketHeight;
			final int startChainLabelHeight = curYPlusBracketHeight + padding + residueIdHeight;
			
			buf.setFont(ResidueFontInfo.contactsResidueIdFont);
			count = this.chainLabels.size();
			for(int i = 0; i < count; i++) {
				final ChainLabel label = (ChainLabel)this.chainLabels.get(i);
				
				// draw the bracket...
				buf.drawLine(label.startX, curY, label.startX, curYPlusBracketHeight);
				buf.drawLine(label.startX, curYPlusBracketHeight, label.endX, curYPlusBracketHeight);
				buf.drawLine(label.endX, curYPlusBracketHeight, label.endX, curY);
				
				// draw the chain label...
				// note: actually, letterBound is not the chain id's letter bounds.
				final int chainLabelStartX = (label.startX + label.endX) / 2 - (int)ContactMap_ContactingResiduesPane.residueIdBounds.getWidth() / 2;
				buf.drawString(label.pdbChainId == null? "*" : label.pdbChainId, chainLabelStartX, startChainLabelHeight);
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
