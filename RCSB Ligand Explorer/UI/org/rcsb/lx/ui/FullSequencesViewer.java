package org.rcsb.lx.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.rcsb.mbt.controllers.app.AppBase;
import org.rcsb.mbt.controllers.scene.PdbToNdbConverter;
import org.rcsb.mbt.model.StructureModel;
import org.rcsb.mbt.model.MiscellaneousMoleculeChain;
import org.rcsb.mbt.model.PdbChain;
import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.StructureComponent;
import org.rcsb.mbt.model.StructureMap;
import org.rcsb.mbt.model.WaterChain;


/**
 * @author John Beaver
 */
public class FullSequencesViewer extends JScrollPane {
    /**
	 * 
	 */
	private static final long serialVersionUID = 9161617478848865490L;

	JPanel contentPane = new JPanel(null,false);
    
    // definitions for painted component sizes
    
    public FullSequencePanel[] sequencePanels = null;
    
    public boolean needsRepositioning = false;
    
    public FullSequencesViewer() {
        super();
        super.setViewportView(this.contentPane);
        super.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        super.setDoubleBuffered(false);
    }

    public void setComponents() {
//        this.data = data;
        
        this.createViewersForCurrentEpitope();
    }
    
    public void createViewersForCurrentEpitope() {
        this.contentPane.removeAll();
        StructureModel model = AppBase.sgetModel();
        if(model.getStructures() == null) {
        	return;
        }
        
        final Structure struc = model.getStructures().get(0);
        final StructureMap sm = struc.getStructureMap();
        
        final int scrollbarWidth = (int)super.getVerticalScrollBar().getPreferredSize().getWidth();
        
        final PdbToNdbConverter converter = sm.getPdbToNdbConverter();
        
        final Vector chains = sm.getPdbTopLevelElements();
        if(chains != null) {            
            this.sequencePanels = new FullSequencePanel[chains.size()];
            
            for(int i = 0; i < chains.size(); i++) {
            	String title = null;
            	
            	final StructureComponent c = (StructureComponent)chains.get(i);
                if(c instanceof PdbChain) {
                	title = "Chain " + ((PdbChain)c).pdbChainId + ":";
                } else if(c instanceof WaterChain) {
                	continue;	// don't bother reporting water residues
                } else if(c instanceof MiscellaneousMoleculeChain) {
                	// MiscellaneousMoleculeChains are those without pdb chain ids.
                	title = "Various Molecules:";
                }
                
                this.sequencePanels[i] = new FullSequencePanel(title, c, scrollbarWidth);
            }
            
            Arrays.sort(this.sequencePanels, new Comparator() {

				public int compare(final Object o1, final Object o2) {
					final FullSequencePanel s1 = (FullSequencePanel)o1;
					final FullSequencePanel s2 = (FullSequencePanel)o2;
					
					String pdbChainId1 = null;
					if(s1 == null) {
						return 1;
					} else if(s1.chain instanceof PdbChain) {
						pdbChainId1 = ((PdbChain)s1.chain).pdbChainId;
					} else if(s1.chain instanceof MiscellaneousMoleculeChain) {
						return -1;
					}
					if(pdbChainId1 == null) {
						return 1;
					}
					
					String pdbChainId2 = null;
					if(s2 == null) {
						return -1;
					} if(s2.chain instanceof PdbChain) {
						pdbChainId2 = ((PdbChain)s2.chain).pdbChainId;
					} else if(s2.chain instanceof MiscellaneousMoleculeChain) {
						return 1;
					}
					if(pdbChainId2 == null) {
						return -1;
					}
					
					return pdbChainId1.compareTo(pdbChainId2);
				}
            	
            });
            
            for(int i = 0; i < this.sequencePanels.length; i++) {
            	if(this.sequencePanels[i] != null) {
            		this.contentPane.add(this.sequencePanels[i]);
            	}
            }
        }
        
        // force a resize of the components (see paintComponent())
        this.needsRepositioning = true;
        
        //super.revalidate();
        super.repaint();
        
//        Refresher.setSequencePanels(this.sequencePanels);
    }
    
    private final Dimension oldSize = new Dimension(-1,-1);
    public void paintComponent(final Graphics g) {
        final Dimension newSize = this.contentPane.getSize();
        
        if(!newSize.equals(this.oldSize) || this.needsRepositioning) {
            final JScrollBar bar = super.getVerticalScrollBar();
            if(bar == null) {
                return;
            }
            
            final int scrollWidth = bar.getWidth();
            final Insets insets = super.getInsets();
            
            int curY = 0;
            if(this.sequencePanels != null) {
                for(int i = 0; i < this.sequencePanels.length; i++) {
                	if(this.sequencePanels[i] != null) {
	                    this.sequencePanels[i].generateRanges(newSize.width);
	                    this.sequencePanels[i].setBounds(0,curY,newSize.width,this.sequencePanels[i].preferredHeight);
	                    curY += this.sequencePanels[i].preferredHeight + 3;
                	}
                }
            }
            
			this.contentPane.setPreferredSize(new Dimension(newSize.width - scrollWidth - insets.left - insets.right,curY + insets.top + insets.bottom));
        }
    }

	public void clearStructure(final boolean transatory) {
		// TODO Auto-generated method stub
		
	}

	public void newStructureAdded(final Structure struc, final boolean transatory) {
		// TODO Auto-generated method stub
		
	}

	public void reset() {
		// TODO Auto-generated method stub
		
	}
}
