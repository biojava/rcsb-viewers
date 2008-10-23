package org.rcsb.lx.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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
import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.swing.JPanel;

import org.rcsb.lx.controllers.app.LigandExplorer;
import org.rcsb.lx.glscene.jogl.LXGlGeometryViewer;
import org.rcsb.lx.glscene.jogl.ResidueFontInfo;
import org.rcsb.mbt.glscene.jogl.AtomGeometry;
import org.rcsb.mbt.glscene.jogl.BondGeometry;
import org.rcsb.mbt.glscene.jogl.GlGeometryViewer;
import org.rcsb.mbt.model.Chain;
import org.rcsb.mbt.model.ExternChain;
import org.rcsb.mbt.model.Residue;
import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.StructureComponent;
import org.rcsb.mbt.model.StructureComponentRegistry;
import org.rcsb.mbt.model.StructureMap;
import org.rcsb.mbt.model.attributes.AtomStyle;
import org.rcsb.mbt.model.attributes.BondStyle;
import org.rcsb.mbt.model.attributes.ChainStyle;
import org.rcsb.mbt.model.attributes.StructureStyles;
import org.rcsb.mbt.model.util.AminoAcidInfo;
import org.rcsb.mbt.model.util.Status;


/**
 * @author John Beaver
 */
public class FullSequencePanel extends JPanel
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 6692539761089101610L;

	public String[] title = null;
    
    protected static int startSequenceX = Integer.MIN_VALUE; 
    
    protected static int decriptionFontSize = 16;
    protected static Font descriptionFont = new Font( "SansSerif", Font.PLAIN, FullSequencePanel.decriptionFontSize );
    protected static Rectangle cellBounds = null;
    protected static final int descriptionBarBuffer = 10;
	
	Pattern spaces = Pattern.compile("\\s++");
    
    public boolean needsComponentReposition = false;
    
    public StructureComponent chain;     // the index of the chain in ag and epi which we're concerned about.
    public Chain firstNdbChain = null;
    
    public Residue selectedResidue = null; // the residue which is currently selected; null if no residues are selected.
    
    private int scrollbarWidth;
    
    /* data structure:
     *   HashMap rangesByRow {
     *      key: Integer row        // 0-based
     *      value: Integer[] {
     *         startCell    // the cell that this range starts in on this row
     *         startIndex   // the index of epi or ag (depending on super.immunoOnly)
     *         length       // the length of this range
     *      }
     *   }
     */
    HashMap<Integer, Integer[]> rangesByRow = new HashMap<Integer, Integer[]>();
	
    // useLight: display a light chain; otherwise, display a heavy chain
    // immunoOnly: only display the immunogenic residues (paratope).
    public FullSequencePanel(final String title, final StructureComponent chain, final int scrollbarWidth) {
        super(null, false);
        super.setDoubleBuffered(false);
        
        this.chain = chain;
        this.scrollbarWidth = scrollbarWidth;
        
        if(chain instanceof ExternChain)
        {
        	final ExternChain xc = (ExternChain)chain;
        	final Residue firstRes = xc.getResidue(0);
        	this.firstNdbChain = firstRes.structure.getStructureMap().getChain(firstRes.getChainId());
        }
        
        super.addMouseListener(new SequenceMouseListener(this));
        super.addMouseMotionListener(new SequenceMouseMotionListener(this));
        
        this.title = this.spaces.split(title);
    }
	
	
    public void setStatics(final Graphics g) {
        // quick fix.
        if(g == null) {
            return;
        }
        
        final int horizontalCellBuffer = 2;
        
        if(FullSequencePanel.cellBounds == null) {
            final FontMetrics fontMetrics = g.getFontMetrics(ResidueFontInfo.fullSequenceResidueFont);
            final Rectangle2D letterBounds = fontMetrics.getStringBounds("W",g);
            FullSequencePanel.cellBounds = new Rectangle((int)letterBounds.getWidth() + horizontalCellBuffer,(int)letterBounds.getHeight());
        }
        
        
        final FontMetrics fontMetrics = g.getFontMetrics(FullSequencePanel.descriptionFont);
		int descriptionWidth = -1;
		int descriptionHeight = 0;
		for(int i = 0; i < this.title.length; i++) {
			final Rectangle2D descriptionBounds = fontMetrics.getStringBounds(this.title[i], g);
			descriptionWidth = Math.max((int)descriptionBounds.getWidth(), descriptionWidth);
			descriptionHeight += descriptionBounds.getHeight();
		}
		
        FullSequencePanel.startSequenceX = Math.max(FullSequencePanel.startSequenceX,descriptionWidth + FullSequencePanel.descriptionBarBuffer * 2);
    }
    
    
    // generate the residue ranges that the paintComponent() function needs.
    // sets the panelHeight, as a side product.
    // returns the Chain corresponding to the ranges.
    // width: the panel width to generate the ranges for.
    public int preferredHeight = 0;
    private int oldWidth = -1;
    public StructureComponent generateRanges(final int width) {
//        Vector proteins = this.data.getCurrentProteins();
//        if(proteins == null) return null;
        
        this.setStatics(super.getGraphics());
        
        final Insets insets = super.getInsets();
        
        // quick fix.
        if(insets == null || FullSequencePanel.cellBounds == null) {
            return null;
        }
        
        // calculate the number of columns for this window size
        final int numCols = (width - insets.left - insets.right - FullSequencePanel.startSequenceX - this.scrollbarWidth - FullSequencePanel.descriptionBarBuffer) / (int)FullSequencePanel.cellBounds.getWidth();
        
        // set up the ranges
        int curRow = 0;
        
        // exit if no chain is found...
        if(this.chain == null) {
            return null;
        }
        
        // exit if the width is the same as last time this function was called...
        if(this.oldWidth == width || numCols <= 3) {
            return this.chain;
        }
        this.oldWidth = width;
        
        // remove all ranges in preparation for adding new ones.
        this.rangesByRow.clear();
        
        int numResidues = (this.chain instanceof ExternChain)? ((ExternChain)chain).getResidueCount() : 0;
        
        for(int startIndex = 0; startIndex < numResidues; curRow++, startIndex += numCols) {
            //int startIndex = i * numCols;
            final int size = Math.min(numCols, numResidues - startIndex);
            if(size >= 0) {
                this.rangesByRow.put(new Integer(curRow),
                				     new Integer[] {new Integer(0),new Integer(startIndex),new Integer(size)});
            }
        }
        
        this.preferredHeight = Math.max(curRow * FullSequencePanel.cellBounds.height + 10, (int)FullSequencePanel.cellBounds.getHeight() * (this.title.length + 1));  // 10 == visual buffer.
        
        return this.chain;
    }
    
    private Dimension oldSize = new Dimension(-1,-1);
    public BufferedImage oldImage = null;
    @Override
	public void paintComponent(final Graphics g) {
        final Graphics2D g2 = (Graphics2D)g;

        // only make a new image if something has changed.
        final Dimension newSize = super.getSize();
        if(newSize.equals(this.oldSize) && this.oldImage != null && !this.needsComponentReposition) {
           super.paintComponent(g);
           g2.drawImage(this.oldImage,null,0,0);
           return; 
        }
        this.oldSize = newSize;
        this.needsComponentReposition = false;
        
        this.oldImage = new BufferedImage(newSize.width,newSize.height,BufferedImage.TYPE_INT_RGB);
        final Graphics2D buf = (Graphics2D)this.oldImage.getGraphics();
        
        //buf.setColor(new Color( 0.7f, 0.7f, 0.7f ));
        //buf.fillRect(0,0,this.oldImage.getWidth(),this.oldImage.getHeight());
        
        final StructureMap sm = this.chain.structure.getStructureMap();
        final StructureStyles ss = sm.getStructureStyles();
        
        buf.setFont( ResidueFontInfo.fullSequenceResidueFont );
        
        final StructureComponent chain = this.generateRanges(newSize.width);
        
        // if there is no chain for this panel, paint the base panel, and quit.
        if(chain == null) {
            super.paintComponent(g);
            return;
        }
        
        Vector<Residue> residuesVec = null;
        if(chain instanceof ExternChain)
        	residuesVec = ((ExternChain)chain).getResiduesVec();
                
        final ChainStyle style = (ChainStyle)ss.getStyle(this.firstNdbChain);
        
        // draw the ranges
        int maxRow = 0;
        for (Integer row : rangesByRow.keySet())
        {
            if (row > maxRow)
				maxRow = row;
            
            final Integer[] values = rangesByRow.get(row);
            final int startCell = values[0].intValue();
            final int startIndex = values[1].intValue();
            final int length = values[2].intValue();
            
            final int tmp = startCell + length;
            final int curY = row * (int)FullSequencePanel.cellBounds.getHeight() + (int)FullSequencePanel.cellBounds.getHeight();
            int curX = FullSequencePanel.startSequenceX;
            for(int cell = startCell, index = startIndex; cell < tmp; cell++, index++) {
                final Residue r = residuesVec.get(index);
                
                // if this is visible, draw an indicator
                if(ss.isSelected(r)) {
                    buf.setColor(Color.gray);
                    buf.fillRect(curX, curY - (int)(FullSequencePanel.cellBounds.getHeight() / 1.5), (int)FullSequencePanel.cellBounds.getWidth(), (int)(FullSequencePanel.cellBounds.getHeight() / 1.25));
                }
                
                final float[] color = {0,0,0};
                
                style.getResidueColor(r,color);
                buf.setColor(new Color(color[0],color[1],color[2]));
                
                String symbol = null;
                if(r.getClassification() == Residue.Classification.AMINO_ACID) {
                    symbol = AminoAcidInfo.getLetterFromCode(r.getCompoundCode());
                } else if(r.getClassification() == Residue.Classification.NUCLEIC_ACID) {
                	symbol = r.getCompoundCode();
                } else {
                    symbol = "*";
                }
                
                buf.drawString(symbol,curX,curY);
                
                curX += (int)FullSequencePanel.cellBounds.getWidth();
            }
        }
        
        // draw the description bar
        buf.setColor(Color.white);
        buf.setFont(FullSequencePanel.descriptionFont);
		
		for(int i = 0; i < this.title.length; i++) {
			buf.drawString(this.title[i], FullSequencePanel.descriptionBarBuffer, (int)FullSequencePanel.cellBounds.getHeight() * (i + 1));
		}
        
        // draw the background, etc.
        super.paintComponent(g2);
        
        // draw the buffer
        g2.drawImage(this.oldImage,null,0,0);
    }
	
	
    
    public Residue getResidueAt(final Point p) {
        if(p.x < FullSequencePanel.startSequenceX) {
            return null;
        }
        
        //Vector receptors = this.data.getCurrentReceptors();
        
        // if we're only showing immunogenic residues (epitope), handle separately...
        Residue r = null;
        
        final int row = p.y / FullSequencePanel.cellBounds.height;
        
        final Integer[] tmp = this.rangesByRow.get(new Integer(row));
        // if this is not a valid row...
        if(tmp == null) {
            return null;
        }
        
        final int startCell = tmp[0].intValue();
        final int startIndex = tmp[1].intValue();
        final int length = tmp[2].intValue();
        
        final int column = (p.x - FullSequencePanel.startSequenceX) / FullSequencePanel.cellBounds.width;
        // if this is not a valid column...
        if(column >= startCell + length) {
            return null;
        }
        
        final int index = (column - startCell) + startIndex;
        if (this.chain instanceof ExternChain) 
        	r = ((ExternChain)this.chain).getResidue(index);
        
        return r;
    }
}

class SequenceMouseListener extends MouseAdapter {
    private FullSequencePanel parent;
    
    public SequenceMouseListener(final FullSequencePanel parent) {
        this.parent = parent;
    }
    
    @Override
	public void mouseClicked(final MouseEvent e) {
        final Structure struc = this.parent.chain.structure;
        final StructureStyles ss = struc.getStructureMap().getStructureStyles();
        
        final LXGlGeometryViewer viewer = LigandExplorer.sgetGlGeometryViewer();
        
        final Point p = e.getPoint();
        final Residue r = this.parent.getResidueAt(p);
        if(r != null) {
        	final AtomGeometry ag = (AtomGeometry) GlGeometryViewer.defaultGeometry.get(StructureComponentRegistry.TYPE_ATOM);
    		final AtomStyle as = (AtomStyle) ss.getDefaultStyle(
    				StructureComponentRegistry.TYPE_ATOM);
    		final BondGeometry bg = (BondGeometry) GlGeometryViewer.defaultGeometry.get(StructureComponentRegistry.TYPE_BOND);
    		final BondStyle bs = (BondStyle) ss.getDefaultStyle(
    				StructureComponentRegistry.TYPE_BOND);
            // toggle between adding and removing
        	if(ss.isSelected(r)) {
        		viewer.hideResidue(r);
        	} else {
        		viewer.renderResidue(r, as, ag, bs, bg, false);
        	}
            
            this.parent.needsComponentReposition = true;
            this.parent.repaint();
        }
        viewer.requestRepaint();
    }
    
    @Override
	public void mouseExited(final MouseEvent e) {
        this.parent.selectedResidue = null;
    }
}

class SequenceMouseMotionListener extends MouseMotionAdapter {
    private FullSequencePanel parent;
    
    public SequenceMouseMotionListener(final FullSequencePanel parent) {
        this.parent = parent;
    }
    
    //private static final ResidueColorByRgb yellow = new ResidueColorByRgb(Color.yellow.getRed() / 255,Color.yellow.getGreen() / 255,Color.yellow.getBlue() / 255);
    @Override
	public void mouseMoved(final MouseEvent e) {
        final Point p = e.getPoint();
        final Residue r = this.parent.getResidueAt(p);
        if(r == null || r == this.parent.selectedResidue) {
            return;
        }
        
        final Structure struc = r.structure;
        final StructureMap sm = struc.getStructureMap();
        
        final Chain c = r.getFragment().getChain();
		final Object[] val = sm.getPdbToNdbConverter().getPdbIds(c.getChainId(), new Integer(r.getResidueId()));
		final String chainId = (String)val[0];
		final String residueId = (String)val[1];
        Status.output(Status.LEVEL_REMARK,  "Mouse at residue " + residueId + ", on chain " + chainId + "; a " + r.getCompoundCode() + " compound");
    }
}
