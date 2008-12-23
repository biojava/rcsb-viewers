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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.rcsb.lx.controllers.app.LigandExplorer;
import org.rcsb.lx.glscene.jogl.ResidueFontInfo;
import org.rcsb.lx.model.Interaction;
import org.rcsb.lx.model.InteractionConstants;
import org.rcsb.lx.model.InteractionMap;
import org.rcsb.lx.model.LXModel;
import org.rcsb.lx.ui.ContactMap_ContactingResiduesPane.ResidueRange;
import org.rcsb.lx.ui.ContactMap_LigandAtomsPane.AtomRange;
import org.rcsb.mbt.model.util.Status;


public class ContactMap_SquarePanel extends JPanel implements MouseListener, MouseMotionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9088606443931161382L;

	public BufferedImage image = null;
	
	public boolean isInitialized = false;	// has this class been initialized yet?
	
	private ContactMap parent;
	
	private int horizontalValue = 0;
	private int verticalValue = 0;
	
	public AdjustmentListener horzontalAdjustmentListener = new AdjustmentListener() {

		public void adjustmentValueChanged(AdjustmentEvent e) {
			ContactMap_SquarePanel.this.horizontalValue = e.getValue();
			ContactMap_SquarePanel.super.repaint();
		}
		
	};
	
	public AdjustmentListener verticalAdjustmentListener = new AdjustmentListener() {

		public void adjustmentValueChanged(AdjustmentEvent e) {
			ContactMap_SquarePanel.this.verticalValue = e.getValue();
			ContactMap_SquarePanel.super.repaint();
		}
		
	};

	public ContactMap_SquarePanel(final ContactMap parent) {
		super(null, false);
		this.parent = parent;
		super.setBorder(BorderFactory.createLineBorder(Color.white));
		
		super.setBackground(LXDocumentFrame.sidebarColor);
		
		super.addMouseListener(this);
		super.addMouseMotionListener(this);
	}
	
	@Override
	public void paintComponent(final Graphics g) {
		super.paintComponent(g);
		
		final Graphics2D g2 = (Graphics2D)g;
		final Insets insets = super.getInsets();
				
		// initialize, if necessary...
		if(!this.isInitialized) {
			if(this.parent.ligandAtomsPane.isInitialized && this.parent.contactingResiduesPane.isInitialized) {
				// the other panes are initialized, so set the center pane's layout and continue.
				this.isInitialized = true;
				
				// remove the panels and re-add them as scroll panes.
				this.parent.remove(this.parent.ligandAtomsPane);
				this.parent.remove(this.parent.contactingResiduesPane);
				this.parent.horizontalScrollPane.setViewportView(this.parent.contactingResiduesPane);
				this.parent.verticalScrollPane.setViewportView(this.parent.ligandAtomsPane);
				this.parent.add(this.parent.horizontalScrollPane);
				this.parent.add(this.parent.verticalScrollPane);
				
				this.parent.setLayout(this.parent.layoutManager);
				this.parent.revalidate();
			} else {	// can't do anything until the other panes are finished...
				return;
			}
		}
		
		// if no buffer exists (initial paint, or a repaint request)...
		if(this.image == null) {
			LXModel model = LigandExplorer.sgetModel();
			final InteractionMap im = model.getInteractionMap();
						
			this.image = new BufferedImage(this.parent.contactingResiduesPane.fullSize.width, this.parent.ligandAtomsPane.fullSize.height, BufferedImage.TYPE_INT_RGB);
			final Graphics2D buf = (Graphics2D)this.image.getGraphics();
//			buf.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			buf.setBackground(LXDocumentFrame.sidebarColor);
			buf.clearRect(0, 0, this.image.getWidth(), this.image.getHeight());
			
			buf.setFont(ResidueFontInfo.contactsResidueFont);
			
			final Vector residueRanges = this.parent.contactingResiduesPane.residueRanges;
			final int resCount = residueRanges.size();
			final Vector atomRanges = this.parent.ligandAtomsPane.atomRanges;
			final int atomCount = atomRanges.size();
			final int outerPadding = 0;
			final int innerPadding = 2;
			
			for(int i = 0; i < resCount; i++) {
				final ResidueRange resRange = (ResidueRange)residueRanges.get(i);
				
				final int startRectangleX = resRange.startX + outerPadding;
				final int endRectangleX = resRange.endX - outerPadding;
				final int rectWidth = endRectangleX - startRectangleX;
				final int startIndicatorsX = startRectangleX + innerPadding;
				final int endIndicatorsX = endRectangleX - innerPadding;
				final int indicatorsWidth = endIndicatorsX - startIndicatorsX;
				
				final Vector interactions = im.getInteractions(resRange.residue);
				final int interCount = interactions.size();
				
				for(int j = 0; j < atomCount; j++) {
					final AtomRange atomRange = (AtomRange)atomRanges.get(j);
					
					final int startRectangleY = atomRange.startY + outerPadding;
					final int endRectangleY = atomRange.endY - outerPadding;
					final int startIndicatorsY = startRectangleY + innerPadding;
					final int endIndicatorsY = endRectangleY - innerPadding;
					final int rectHeight = endRectangleY - startRectangleY;
					final int indicatorsHeight = endIndicatorsY - startIndicatorsY;
					final double interactionTypeIndicatorHeight = indicatorsHeight / 5d;
					final int interactionTypeIndicatorHeightInt = (int)interactionTypeIndicatorHeight;
					
					boolean hasHydrophilic = false, hasHydrophobic = false, hasInterLigand = false, hasBridgedH2O = false, hasOther = false;
					for(int k = 0; k < interCount; k++) {
						final Interaction inter = (Interaction)interactions.get(k);
						if(inter.getFirstAtom() == atomRange.atom || inter.getSecondAtom() == atomRange.atom) {
							if(inter.getInteractionType() == InteractionConstants.hydrophilicType) {
								hasHydrophilic = true;
							} else if(inter.getInteractionType() == InteractionConstants.hydrophobicType) {
								hasHydrophobic = true;
							} else if(inter.getInteractionType() == InteractionConstants.interLigandType) {
								hasInterLigand = true;
							} else if(inter.getInteractionType() == InteractionConstants.otherType) {
								hasOther = true;
							} else if(inter.getInteractionType() == InteractionConstants.waterMediatedType) {
								hasBridgedH2O = true;
							}
						}
					}
					
					buf.setColor(Color.black);
					final RoundRectangle2D.Float border = new RoundRectangle2D.Float( 
							startRectangleX, startRectangleY, 
							rectWidth, rectHeight, 
						    3f, 3f);
					buf.fill(border);
					
					int tmpY = startIndicatorsY;
					if(hasInterLigand) {
						buf.setColor(InteractionConstants.interLigandBondColorOb);
						buf.fillRect(startIndicatorsX, tmpY, indicatorsWidth, interactionTypeIndicatorHeightInt);
					}
					tmpY = (int)(startIndicatorsY + interactionTypeIndicatorHeight);
					if(hasHydrophilic) {
						buf.setColor(InteractionConstants.hydrophilicBondColorOb);
						buf.fillRect(startIndicatorsX, tmpY, indicatorsWidth, interactionTypeIndicatorHeightInt);
					}
					tmpY = (int)(startIndicatorsY + interactionTypeIndicatorHeight * 2);
					if(hasHydrophobic) {
						buf.setColor(InteractionConstants.hydrophobicBondColorOb);
						buf.fillRect(startIndicatorsX, tmpY, indicatorsWidth, interactionTypeIndicatorHeightInt);
					}
					tmpY = (int)(endIndicatorsY - interactionTypeIndicatorHeight * 2);
					if(hasBridgedH2O) {
						buf.setColor(InteractionConstants.waterBondColorOb);
						buf.fillRect(startIndicatorsX, tmpY, indicatorsWidth, interactionTypeIndicatorHeightInt);
					}
					tmpY = (int)(endIndicatorsY - interactionTypeIndicatorHeight);
					if(hasOther) {
						buf.setColor(InteractionConstants.otherColorOb);
						buf.fillRect(startIndicatorsX, tmpY, indicatorsWidth, endIndicatorsY - tmpY);
					}
				}
			}
		}
		
		// draw the buffer, observing the scroll bar positions...
		g2.drawImage(this.image, null, insets.left - this.horizontalValue, insets.top - this.verticalValue);
	}

	// ignored mouse events.
	public void mouseEntered(final MouseEvent e) {}
	public void mouseExited(final MouseEvent e) {}
	public void mousePressed(final MouseEvent e) {}
	public void mouseReleased(final MouseEvent e) {}
	public void mouseDragged(final MouseEvent e) {}

	private static final Comparator atomRangeComparator = new Comparator() {
		public int compare(Object o1, Object o2) {
			AtomRange range = (AtomRange)o1;
			int location = ((Integer)o2).intValue();
			
			if(location > range.endY) {
				return -1;
			}
			
			if(location < range.startY) {
				return 1;
			}
			
			return 0;
		}
	};
	
	private static final Comparator residueRangeComparator = new Comparator() {
		public int compare(Object o1, Object o2) {
			ResidueRange range = (ResidueRange)o1;
			int location = ((Integer)o2).intValue();
			
			if(location > range.endX) {
				return -1;
			}
			
			if(location < range.startX) {
				return 1;
			}
			
			return 0;
		}
	};
	
	private AtomRange currentLigandAtom = null;
	private ResidueRange currentContactingResidue = null;
	private int currentLigandAtomIndex = -1;
	private int currentContactingResidueIndex = -1;
	public void findLocation(final Point p) {
		final int xCoord = p.x + this.horizontalValue;
		final int yCoord = p.y + this.verticalValue;
		
		if(this.currentLigandAtom != null) {
			if(yCoord <= this.currentLigandAtom.endY  && yCoord >= this.currentLigandAtom.startY) {
			} else if(yCoord > this.currentLigandAtom.endY) {
				// if the user did not go outside the boundaries of the panel, this is likely the cell directly below (if any).
				if(this.currentLigandAtomIndex == this.parent.ligandAtomsPane.atomRanges.size() - 1) {
					// if the previous index was the last, there's nothing below the cell, and so nothing to report.
					this.currentLigandAtom = null;
					this.currentLigandAtomIndex = -1;
					return;
				}
				final int tmpIndex = this.currentLigandAtomIndex + 1;
				final AtomRange tmpRange = (AtomRange)this.parent.ligandAtomsPane.atomRanges.get(tmpIndex);
				
				if(yCoord > tmpRange.endY) {
					// this is not the next cell down. Do a full binary search.
					final int index = Collections.binarySearch(this.parent.ligandAtomsPane.atomRanges, new Integer(yCoord), ContactMap_SquarePanel.atomRangeComparator);
					if(index < 0 || index >= this.parent.ligandAtomsPane.atomRanges.size()) {
						// not found, so nothing to report.
						this.currentLigandAtom = null;
						this.currentLigandAtomIndex = -1;
						return;
					}
					
					this.currentLigandAtomIndex = index;
					this.currentLigandAtom = (AtomRange)this.parent.ligandAtomsPane.atomRanges.get(index);
				} else {
					// this is the next cell down.
					this.currentLigandAtomIndex = tmpIndex;
					this.currentLigandAtom = tmpRange;
				}
			} else if(yCoord < this.currentLigandAtom.startY) {
				// if the user did not go outside the boundaries of the panel, this is likely the cell directly above (if any).
				if(this.currentLigandAtomIndex == 0) {
					// if the previous index was the zero, there's nothing above the cell, and so nothing to report.
					this.currentLigandAtom = null;
					this.currentLigandAtomIndex = -1;
					return;
				}
				final int tmpIndex = this.currentLigandAtomIndex - 1;
				final AtomRange tmpRange = (AtomRange)this.parent.ligandAtomsPane.atomRanges.get(tmpIndex);
				
				if(yCoord < tmpRange.startY) {
					// this is not the next cell down. Do a full binary search.
					final int index = Collections.binarySearch(this.parent.ligandAtomsPane.atomRanges, new Integer(yCoord), ContactMap_SquarePanel.atomRangeComparator);
					if(index < 0 || index >= this.parent.ligandAtomsPane.atomRanges.size()) {
						// not found, so nothing to report.
						this.currentLigandAtom = null;
						this.currentLigandAtomIndex = -1;
						return;
					}
					
					this.currentLigandAtomIndex = index;
					this.currentLigandAtom = (AtomRange)this.parent.ligandAtomsPane.atomRanges.get(index);
				} else {
					// this is the next cell down.
					this.currentLigandAtomIndex = tmpIndex;
					this.currentLigandAtom = tmpRange;
				}
			}
		} else {
			// if there is nowhere to start, do a full binary search for it
			final int index = Collections.binarySearch(this.parent.ligandAtomsPane.atomRanges, new Integer(yCoord), ContactMap_SquarePanel.atomRangeComparator);
			if(index < 0 || index >= this.parent.ligandAtomsPane.atomRanges.size()) {
				// not found, so nothing to report.
				return;
			}
			
			// found.
			this.currentLigandAtom = (AtomRange)this.parent.ligandAtomsPane.atomRanges.get(index);
			this.currentLigandAtomIndex = index;
		}
		
		if(this.currentContactingResidue != null) {
			if(xCoord <= this.currentContactingResidue.endX  && xCoord >= this.currentContactingResidue.startX) {
			} else if(xCoord > this.currentContactingResidue.endX) {
				// if the user did not go outside the boundaries of the panel, this is likely the cell directly below (if any).
				if(this.currentContactingResidueIndex == this.parent.contactingResiduesPane.residueRanges.size() - 1) {
					// if the previous index was the last, there's nothing below the cell, and so nothing to report.
					this.currentContactingResidue = null;
					this.currentContactingResidueIndex = -1;
					return;
				}
				final int tmpIndex = this.currentContactingResidueIndex + 1;
				final ResidueRange tmpRange = (ResidueRange)this.parent.contactingResiduesPane.residueRanges.get(tmpIndex);
				
				if(xCoord > tmpRange.endX) {
					// this is not the next cell down. Do a full binary search.
					final int index = Collections.binarySearch(this.parent.contactingResiduesPane.residueRanges, new Integer(xCoord), ContactMap_SquarePanel.residueRangeComparator);
					if(index < 0 || index >= this.parent.contactingResiduesPane.residueRanges.size()) {
						// not found, so nothing to report.
						this.currentContactingResidue = null;
						this.currentContactingResidueIndex = -1;
						return;
					}
					
					this.currentContactingResidueIndex = index;
					this.currentContactingResidue = (ResidueRange)this.parent.contactingResiduesPane.residueRanges.get(index);
				} else {
					// this is the next cell down.
					this.currentContactingResidueIndex = tmpIndex;
					this.currentContactingResidue = tmpRange;
				}
			} else if(xCoord < this.currentContactingResidue.startX) {
				// if the user did not go outside the boundaries of the panel, this is likely the cell directly above (if any).
				if(this.currentContactingResidueIndex == 0) {
					// if the previous index was the zero, there's nothing above the cell, and so nothing to report.
					this.currentContactingResidue = null;
					this.currentContactingResidueIndex = -1;
					return;
				}
				final int tmpIndex = this.currentContactingResidueIndex - 1;
				final ResidueRange tmpRange = (ResidueRange)this.parent.contactingResiduesPane.residueRanges.get(tmpIndex);
				
				if(xCoord < tmpRange.startX) {
					// this is not the next cell down. Do a full binary search.
					final int index = Collections.binarySearch(this.parent.contactingResiduesPane.residueRanges, new Integer(xCoord), ContactMap_SquarePanel.residueRangeComparator);
					if(index < 0 || index >= this.parent.contactingResiduesPane.residueRanges.size()) {
						// not found, so nothing to report.
						this.currentContactingResidue = null;
						this.currentContactingResidueIndex = -1;
						return;
					}
					
					this.currentContactingResidueIndex = index;
					this.currentContactingResidue = (ResidueRange)this.parent.contactingResiduesPane.residueRanges.get(index);
				} else {
					// this is the next cell down.
					this.currentContactingResidueIndex = tmpIndex;
					this.currentContactingResidue = tmpRange;
				}
			}
		} else {
			// if there is nowhere to start from, do a full binary search.
			final int index = Collections.binarySearch(this.parent.contactingResiduesPane.residueRanges, new Integer(xCoord), ContactMap_SquarePanel.residueRangeComparator);
			if(index < 0 || index >= this.parent.contactingResiduesPane.residueRanges.size()) {
				// not found, so nothing to report.
				return;
			}
			
			this.currentContactingResidueIndex = index;
			this.currentContactingResidue = (ResidueRange)this.parent.contactingResiduesPane.residueRanges.get(index);
		}
		
		if(this.currentLigandAtom != null && this.currentContactingResidue != null) {
//			final PdbToNdbConverter conv = this.currentLigandAtom.atom.structure.getStructureMap().getPdbToNdbConverter();
//			Object[] atomPdbIds = conv.getPdbIds(this.currentLigandAtom.a, ndbResidueId)
			
			Status.output(Status.LEVEL_REMARK, "Click to see contacts for ligand atom " + this.currentLigandAtom.atomDescription + " and residue " + this.currentContactingResidue.residue.toString());
		}
	}
	
	public void mouseMoved(final MouseEvent e) {
		final Point p = e.getPoint();
		this.findLocation(p);
	}
	
	public void mouseClicked(final MouseEvent e) {
		// the mouseMoved() method found the current location.
		
		if(this.currentContactingResidue != null && this.currentLigandAtom != null) {
			this.parent.informationPane.reportInteractions(this.currentContactingResidue.residue, this.currentLigandAtom.atom);
		}
	}
}
