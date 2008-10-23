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
import org.rcsb.mbt.model.Residue;
import org.rcsb.mbt.model.StructureModel;
import org.rcsb.mbt.model.ExternChain;
import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.StructureComponent;
import org.rcsb.mbt.model.StructureMap;


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
        super.setViewportView(contentPane);
        super.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        super.setDoubleBuffered(false);
    }

    public void setComponents()
    {
        createViewersForCurrentEpitope();
    }
    
    public void createViewersForCurrentEpitope()
    {
        contentPane.removeAll();
        StructureModel model = AppBase.sgetModel();
        if (model.getStructures() == null)
        	return;
        
        final Structure struc = model.getStructures().get(0);
        final StructureMap sm = struc.getStructureMap();
        
        final int scrollbarWidth = (int)super.getVerticalScrollBar().getPreferredSize().getWidth();
        
        final Vector<StructureComponent> topChains = sm.getPdbTopLevelElements();
        Vector<StructureComponent> chains = new Vector<StructureComponent>();
        
        for (StructureComponent comp : topChains)
          if (!(comp instanceof ExternChain && ((ExternChain)comp).getResidue(0).getClassification() ==
        	  Residue.Classification.LIGAND)) chains.add(comp);
        						// leave out the ligands...
        	  
        if(chains != null)
        {            
            sequencePanels = new FullSequencePanel[chains.size()];
            
            int i = 0;
            for (StructureComponent c : chains)
            {
            	String title = null;
            	
                if (c instanceof ExternChain)
                {
                	ExternChain xc = (ExternChain)c;
                	switch (xc.getExternChainType())
                	{
                	case BASIC: title = "Chain " + xc.getChainId() + ":"; break;
                	case MISCELLANEOUS: title = "Non Protein:"; break;
                	default:
                		continue;
                						// no waters...
                	}
                }
                sequencePanels[i++] = new FullSequencePanel(title, c, scrollbarWidth);
            }
            
            Arrays.sort(sequencePanels,
            	new Comparator<FullSequencePanel>()
            	{
					public int compare(final FullSequencePanel s1, final FullSequencePanel s2)
					{
						if (s1 == null)
							return 1;
						
						else if (s2 == null)
							return -1;
						
						if (AppBase.isDebug())
							assert(s1.chain instanceof ExternChain && s2.chain instanceof ExternChain);
											// curious...
							
						ExternChain xc1 = (ExternChain)s1.chain;
						ExternChain xc2 = (ExternChain)s2.chain;
						
						// non-protein chains always go to the bottom
						if (xc1.isMiscellaneousChain())
								return 1;
						
						// non-protein chains always go to the bottom
						if (xc2.isMiscellaneousChain())
							   return -1;
						
						return xc1.getChainId().compareTo(xc2.getChainId());
					}           	
	            });
            
            for(int iPanel = 0; iPanel < sequencePanels.length; iPanel++) {
            	if(sequencePanels[iPanel] != null) {
            		contentPane.add(sequencePanels[iPanel]);
            	}
            }
        }
        
        // force a resize of the components (see paintComponent())
        needsRepositioning = true;
        
        //super.revalidate();
        super.repaint();
        
//        Refresher.setSequencePanels(sequencePanels);
    }
    
    private final Dimension oldSize = new Dimension(-1,-1);
    @Override
	public void paintComponent(final Graphics g) {
        final Dimension newSize = contentPane.getSize();
        
        if(!newSize.equals(oldSize) || needsRepositioning) {
            final JScrollBar bar = super.getVerticalScrollBar();
            if(bar == null) {
                return;
            }
            
            final int scrollWidth = bar.getWidth();
            final Insets insets = super.getInsets();
            
            int curY = 0;
            if(sequencePanels != null) {
                for(int i = 0; i < sequencePanels.length; i++) {
                	if(sequencePanels[i] != null) {
	                    sequencePanels[i].generateRanges(newSize.width);
	                    sequencePanels[i].setBounds(0,curY,newSize.width,sequencePanels[i].preferredHeight);
	                    curY += sequencePanels[i].preferredHeight + 3;
                	}
                }
            }
            
			contentPane.setPreferredSize(new Dimension(newSize.width - scrollWidth - insets.left - insets.right,curY + insets.top + insets.bottom));
        }
    }
}
