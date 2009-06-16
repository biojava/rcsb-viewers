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
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import java.util.regex.Pattern;

import org.rcsb.lx.controllers.app.LigandExplorer;
import org.rcsb.lx.glscene.jogl.LXGlGeometryViewer;
import org.rcsb.lx.glscene.jogl.ResidueFontInfo;
import org.rcsb.mbt.model.Chain;
import org.rcsb.mbt.model.ExternChain;
import org.rcsb.mbt.model.Residue;
import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.StructureComponent;
import org.rcsb.mbt.model.StructureMap;
import org.rcsb.mbt.model.StructureComponentRegistry.ComponentType;
import org.rcsb.mbt.model.attributes.AtomStyle;
import org.rcsb.mbt.model.attributes.BondStyle;
import org.rcsb.mbt.model.attributes.ChainStyle;
import org.rcsb.mbt.model.attributes.StructureStyles;
import org.rcsb.mbt.model.util.AminoAcidInfo;
import org.rcsb.mbt.model.util.Status;
import org.rcsb.vf.glscene.jogl.AtomGeometry;
import org.rcsb.vf.glscene.jogl.BondGeometry;
import org.rcsb.vf.glscene.jogl.GlGeometryViewer;
import org.rcsb.vf.glscene.jogl.SequencePanelBase;

/**
 * @author John Beaver
 */
@SuppressWarnings("serial")
public class FullSequencePanel extends SequencePanelBase {
	public String[] title = null;

	protected static int startSequenceX = Integer.MIN_VALUE;

	protected static Rectangle cellBounds = null;
	protected static Rectangle2D letterBounds = null;
	protected static int letterCenterX;

	protected static final int rulerWidth = 3;

	Pattern spaces = Pattern.compile("\\s++");

	public StructureComponent chain; // the index of the chain in ag and epi
										// which we're concerned about.
	public Chain firstNdbChain = null;

	public Residue selectedResidue = null; // the residue which is currently
											// selected; null if no residues are
											// selected.

	private int scrollbarWidth;

	/*
	 * data structure: HashMap rangesByRow { key: Integer row // 0-based value:
	 * Integer[] { startCell // the cell that this range starts in on this row
	 * startIndex // the index of epi or ag (depending on super.immunoOnly)
	 * length // the length of this range } }
	 */
	Map<Integer, Integer[]> rangesByRow = new TreeMap<Integer, Integer[]>();

	// useLight: display a light chain; otherwise, display a heavy chain
	// immunoOnly: only display the immunogenic residues (paratope).
	public FullSequencePanel(final String title,
			final StructureComponent chain, final int scrollbarWidth) {
		super();

		this.chain = chain;
		this.scrollbarWidth = scrollbarWidth;

		if (chain instanceof ExternChain)
		// so what do we do if it's *not* an ExternChain???
		{
			final ExternChain xc = (ExternChain) chain;
			final Residue firstRes = xc.getResidue(0);
			firstNdbChain = firstRes.structure.getStructureMap().getChain(
					firstRes.getChainId());
		}

		super.addMouseListener(new SequenceMouseListener(this));
		super.addMouseMotionListener(new SequenceMouseMotionListener(this));

		this.title = spaces.split(title);
	}

	@Override
	public void heightForWidth(int width) {
		generateRanges(width);
	}

	public void setStatics(final Graphics g) {
		if (g == null)
			return;

		final int horizontalCellPadding = 2;

		if (FullSequencePanel.cellBounds == null) {
			final FontMetrics fontMetrics = g
					.getFontMetrics(ResidueFontInfo.fullSequenceResidueFont);
			letterBounds = fontMetrics.getStringBounds("W", g);
			letterCenterX = (int) letterBounds.getCenterX();
			cellBounds = new Rectangle((int) letterBounds.getWidth()
					+ horizontalCellPadding, (int) letterBounds.getHeight() * 2
					+ rulerWidth);
		}

		final FontMetrics fontMetrics = g.getFontMetrics(descriptionFont);
		int descriptionWidth = -1;
		int descriptionHeight = 0;
		for (int i = 0; i < title.length; i++) {
			final Rectangle2D descriptionBounds = fontMetrics.getStringBounds(
					title[i], g);
			descriptionWidth = Math.max((int) descriptionBounds.getWidth(),
					descriptionWidth);
			descriptionHeight += descriptionBounds.getHeight();
		}

		startSequenceX = Math.max(startSequenceX, descriptionWidth
				+ descriptionBarPadding * 2);
	}

	// generate the residue ranges that the paintComponent() function needs.
	// sets the panelHeight, as a side product.
	// returns the Chain corresponding to the ranges.
	// width: the panel width to generate the ranges for.
	private int oldWidth = -1;

	public StructureComponent generateRanges(final int width) {
		setStatics(super.getGraphics());

		final Insets insets = super.getInsets();

		// quick fix.
		if (insets == null || FullSequencePanel.cellBounds == null
				|| chain == null)
			return null;

		// calculate the number of columns for this window size
		final int drawWidth = width - insets.left - insets.right
				- FullSequencePanel.startSequenceX - scrollbarWidth
				- descriptionBarPadding;

		final int numCols = drawWidth
				/ (int) FullSequencePanel.cellBounds.getWidth();

		// set up the ranges
		int curRow = 0;

		// exit if the width is the same as last time this function was
		// called...
		if (oldWidth == width || numCols <= 3)
			return chain;

		oldWidth = width;

		// remove all ranges in preparation for adding new ones.
		rangesByRow.clear();

		int numResidues = (chain instanceof ExternChain) ? ((ExternChain) chain)
				.getResidueCount()
				: 0;

		int extraRow = 0;
		for (int startIndex = 0; startIndex < numResidues; curRow++, startIndex += numCols) {
			final int size = Math.min(numCols, numResidues - startIndex);
			if (numCols != size)
				extraRow = 1;
			if (size >= 0)
				rangesByRow.put(new Integer(curRow), new Integer[] {
						new Integer(0), new Integer(startIndex),
						new Integer(size) });
		}

		preferredHeight = Math.max((curRow + extraRow)
				* FullSequencePanel.cellBounds.height + 10,
				(int) FullSequencePanel.cellBounds.getHeight()
						* (title.length + 1));

		return chain;
	}

	@Override
	public void paintComponent(final Graphics g) {
		final Graphics2D g2 = (Graphics2D) g;

		// only make a new image if something has changed.
		final Dimension newSize = super.getSize();
		if (newSize.equals(oldSize) && oldImage != null && !isDirty) {
			super.paintComponent(g);
			g2.drawImage(oldImage, null, 0, 0);
			return;
		}

		oldSize = newSize;
		isDirty = false;

		oldImage = new BufferedImage(newSize.width, newSize.height,
				BufferedImage.TYPE_INT_RGB);
		final Graphics2D buf = (Graphics2D) oldImage.getGraphics();

		final StructureMap sm = chain.structure.getStructureMap();
		final StructureStyles ss = sm.getStructureStyles();

		buf.setFont(ResidueFontInfo.fullSequenceResidueFont);

		final StructureComponent chain = generateRanges(newSize.width);

		// if there is no chain for this panel, paint the base panel, and quit.
		if (chain == null) {
			super.paintComponent(g);
			return;
		}

		Vector<Residue> residuesVec = null;
		if (chain instanceof ExternChain)
			residuesVec = ((ExternChain) chain).getResiduesVec();

		final ChainStyle style = (ChainStyle) ss.getStyle(firstNdbChain);

		// draw the ranges
		int maxRow = 0;
		for (Integer row : rangesByRow.keySet()) {
			if (row > maxRow)
				maxRow = row;

			final Integer[] values = rangesByRow.get(row);
			final int startCell = values[0].intValue();
			final int startIndex = values[1].intValue();
			final int length = values[2].intValue();

			final int tmp = startCell + length;
			final int curY = row
					* (int) FullSequencePanel.cellBounds.getHeight()
					+ (int) FullSequencePanel.cellBounds.getHeight();
			int curX = FullSequencePanel.startSequenceX;
			boolean first = true;
			int lastCharEndX = curX - 1;
			for (int cell = startCell, index = startIndex; cell < tmp; cell++, index++) {
				final Residue r = residuesVec.get(index);

				// if this is visible, draw an indicator
				if (ss.isSelected(r)) {
					buf.setColor(Color.gray);
					buf
							.fillRect(curX, curY
									- (int) (FullSequencePanel.cellBounds
											.getHeight() / 1.5),
									(int) FullSequencePanel.cellBounds
											.getWidth(),
									(int) (FullSequencePanel.cellBounds
											.getHeight() / 1.25));
				}

				final float[] color = { 0, 0, 0 };

				style.getResidueColor(r, color);
				buf.setColor(new Color(color[0], color[1], color[2]));

				String symbol = null;
			
				switch (r.getClassification()) {
				case AMINO_ACID:
					symbol = AminoAcidInfo.getLetterFromCode(r.getCompoundCode());
					break;
				case NUCLEIC_ACID:
					symbol = r.getCompoundCode();
					// shorten two letter DNA code, i.e. DT -> T
					if (symbol.length() == 2 && symbol.startsWith("D")) {
						symbol = symbol.substring(1);
					} else if (symbol.length() > 2) {
						symbol = "X";
					}
					break;
				}
				if (symbol == null) {
					symbol = "*";
				}

				buf.drawString(symbol, curX, curY);

				buf.setColor(Color.white);
				int lineY = curY + 1;

				if (first || r.getAuthorResidueId() % 5 == 0) {
					if (first
							|| (r.getAuthorResidueId() % 10 == 0 && curX > lastCharEndX)) {
						String resId = String.valueOf(r.getAuthorResidueId());
						buf.drawString(resId, curX, curY
								+ (int) letterBounds.getHeight() + rulerWidth);
						lastCharEndX = curX
								+ (resId.length()
										* (int) letterBounds.getWidth() + 1);
					}

					int tickX = curX + letterCenterX;
					buf.drawLine(tickX, lineY, tickX, lineY + rulerWidth);
					first = false;
				}
				buf.drawLine(curX, lineY, curX + (int) letterBounds.getWidth(),
						lineY);
				curX += (int) FullSequencePanel.cellBounds.getWidth();
			}
		}

		// draw the description bar
		buf.setColor(Color.white);
		buf.setFont(descriptionFont);

		for (int i = 0; i < title.length; i++) {
			buf.drawString(title[i], descriptionBarPadding, (int) cellBounds
					.getHeight()
					* (i + 1));
		}

		// draw the background, etc.
		super.paintComponent(g2);

		// draw the buffer
		g2.drawImage(oldImage, null, 0, 0);
	}

	public Residue getResidueAt(final Point p) {
		if (p.x < FullSequencePanel.startSequenceX)
			return null;

		// if we're only showing immunogenic residues (epitope), handle
		// separately...
		Residue r = null;

		final int row = p.y / FullSequencePanel.cellBounds.height;

		final Integer[] tmp = rangesByRow.get(new Integer(row));
		// if this is not a valid row...
		if (tmp == null)
			return null;

		final int startCell = tmp[0].intValue();
		final int startIndex = tmp[1].intValue();
		final int length = tmp[2].intValue();

		final int column = (p.x - FullSequencePanel.startSequenceX)
				/ FullSequencePanel.cellBounds.width;
		// if this is not a valid column...
		if (column >= startCell + length)
			return null;

		final int index = (column - startCell) + startIndex;
		if (chain instanceof ExternChain)
			r = ((ExternChain) chain).getResidue(index);

		return r;
	}
}

class SequenceMouseListener extends MouseAdapter {
	private FullSequencePanel owner;

	public SequenceMouseListener(final FullSequencePanel parent) {
		this.owner = parent;
	}

	@Override
	public void mouseClicked(final MouseEvent e) {
		final Structure struc = owner.chain.structure;
		final StructureStyles ss = struc.getStructureMap().getStructureStyles();

		final LXGlGeometryViewer viewer = LigandExplorer.sgetGlGeometryViewer();

		final Point p = e.getPoint();
		final Residue r = owner.getResidueAt(p);
		if (r != null) {
			final AtomGeometry ag = (AtomGeometry) GlGeometryViewer.defaultGeometry
					.get(ComponentType.ATOM);
			final AtomStyle as = (AtomStyle) ss
					.getDefaultStyle(ComponentType.ATOM);
			final BondGeometry bg = (BondGeometry) GlGeometryViewer.defaultGeometry
					.get(ComponentType.BOND);
			final BondStyle bs = (BondStyle) ss
					.getDefaultStyle(ComponentType.BOND);
			// toggle between adding and removing

			if (ss.isSelected(r))
				viewer.hideResidue(r);

			else
				viewer.renderResidue(r, as, ag, bs, bg, false);

			owner.isDirty = true;
			owner.repaint();
		}
		viewer.requestRepaint();
	}

	@Override
	public void mouseExited(final MouseEvent e) {
		owner.selectedResidue = null;
	}
}

class SequenceMouseMotionListener extends MouseMotionAdapter {
	private FullSequencePanel owner;

	public SequenceMouseMotionListener(final FullSequencePanel parent) {
		this.owner = parent;
	}

	@Override
	public void mouseMoved(final MouseEvent e) {
		final Point p = e.getPoint();
		final Residue r = owner.getResidueAt(p);
		if (r == null || r == owner.selectedResidue)
			return;

		final Chain c = r.getFragment().getChain();
		final String chainId = c.getAuthorChainId();
		final String residueId = String.valueOf(r.getAuthorResidueId());
		Status.output(Status.LEVEL_REMARK, "Mouse at residue " + residueId
				+ ", on chain " + chainId + "; a " + r.getCompoundCode()
				+ " compound");
	}
}
