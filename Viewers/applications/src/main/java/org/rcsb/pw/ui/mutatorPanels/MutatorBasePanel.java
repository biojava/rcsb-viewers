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
package org.rcsb.pw.ui.mutatorPanels;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;

import javax.swing.JPanel;

import org.rcsb.pw.controllers.app.ProteinWorkshop;
import org.rcsb.pw.controllers.scene.mutators.MutatorEnum;
import org.rcsb.pw.ui.tree.TreeViewer;

public class MutatorBasePanel extends JPanel
{
	private static final long serialVersionUID = 3893750857058349620L;
	private VisibilityOptionsPanel visibilityPanel = null;
    private ColorOptionsPanel colorPanel = null; 
    private StylesOptionsPanel stylesPanel = null;
    private LabelsOptionsPanel labelsPanel = null;
    private ReCenterOptionsPanel reCenterPanel = null;
    private LinesOptionsPanel linesPanel = null;
    private SurfacePanel surfacePanel = null;
//    private SelectionOptionsPanel selectionPanel = null;
    
    private MutatorPanel mutatorPanel = null;
    public TreeViewer tree = null;
    
    private MutatorActivationPanel pickLevelPanel = null;
    
    private JPanel currentPanel = null;
    
    private class CustomLayout implements LayoutManager2 {
    	private Dimension maxSize = new Dimension(Integer.MAX_VALUE,Integer.MAX_VALUE);
    	private Dimension minSize = new Dimension(0,0);
    	
		public void addLayoutComponent(String name, Component comp) {}

		public void layoutContainer(Container parent) {
			final int buffer = 3;
			final Insets insets = parent.getInsets();
			int curY = insets.top + buffer;
			int curX = insets.left + buffer;
			
			final int fullWidth = parent.getWidth() - insets.left - insets.right - buffer * 2;
			final int fullHeight = parent.getHeight() - insets.top - insets.bottom - buffer * 2;
			
			final Dimension mutatorSize = mutatorPanel.getPreferredSize();
			final Dimension pickLevelSize = pickLevelPanel.getPreferredSize();
			final Dimension surfaceSize = surfacePanel.getPreferredSize();
			
			mutatorPanel.setBounds(curX, curY, fullWidth, mutatorSize.height);
			curY += mutatorSize.height + buffer;
			pickLevelPanel.setBounds(curX, curY, fullWidth, pickLevelSize.height);
			curY += pickLevelSize.height + buffer;
			
			if(currentPanel != null) {
				final Dimension currentSize = currentPanel.getPreferredSize();
				currentPanel.setBounds(curX, curY, fullWidth, currentSize.height);
				curY += currentSize.height + buffer;
			}
			
			final int treeHeight = fullHeight - curY - surfaceSize.height;
			tree.setBounds(curX, curY, fullWidth, treeHeight);
			
			curY = fullHeight - surfaceSize.height;
			surfacePanel.setBounds(curX, curY, fullWidth, surfaceSize.height);
		}

		public Dimension minimumLayoutSize(Container parent) {
			return this.minSize;
		}

		public Dimension preferredLayoutSize(Container parent) {
			return this.minSize;
		}

		public void removeLayoutComponent(Component comp) {}

		public void addLayoutComponent(Component comp, Object constraints) {}

		public float getLayoutAlignmentX(Container target) {
			return 0;
		}

		public float getLayoutAlignmentY(Container target) {
			return 0;
		}

		public void invalidateLayout(Container target) {}

		public Dimension maximumLayoutSize(Container target) {
			return this.maxSize;
		}
    }
    
    public MutatorBasePanel()
    {
        super(null, false);
        super.setLayout(new CustomLayout());
        
        ProteinWorkshop.sgetActiveFrame().setMutatorBasePanel(this);
        
        this.tree = new TreeViewer();
        this.mutatorPanel = new MutatorPanel();
        this.visibilityPanel = new VisibilityOptionsPanel();
        this.colorPanel = new ColorOptionsPanel();
        this.stylesPanel = new StylesOptionsPanel();
        this.labelsPanel = new LabelsOptionsPanel();
        this.reCenterPanel = new ReCenterOptionsPanel();
        this.linesPanel = new LinesOptionsPanel();
//        this.selectionPanel = new SelectionOptionsPanel();
        this.pickLevelPanel = new MutatorActivationPanel();
        this.surfacePanel = new SurfacePanel();
        
      
        super.add(this.mutatorPanel);
        super.add(this.pickLevelPanel);
        super.add(this.surfacePanel);
  
        this.updateOptionsPanel();
        
        super.add(this.tree);
     
    }
    
    public void updateOptionsPanel()
    {
    	// first, remove the current panel.
    	if(this.currentPanel != null) {
    		super.remove(this.currentPanel);
    	}
    	
    	final MutatorEnum mutEnum = ProteinWorkshop.sgetSceneController().getMutatorEnum();
    	switch(mutEnum.getCurrentMutatorId())
    	{
    	case VISIBILITY_MUTATOR:
    		this.currentPanel = this.visibilityPanel;
    		break;
    	case COLORING_MUTATOR:
    		this.currentPanel = this.colorPanel;
    		break;
    	case LABELING_MUTATOR:
    		this.currentPanel = this.labelsPanel;
    		break;
    	case LINES_MUTATOR:
    		this.currentPanel = this.linesPanel;
     		break;
    	case STYLES_MUTATOR:
    		this.currentPanel = this.stylesPanel;
     		break;
    	case RECENTER_MUTATOR:
    		this.currentPanel = this.reCenterPanel;
    		break;
    	default:
    		assert(false);
    	}
    	
		ProteinWorkshop.sgetActiveFrame().getTreeViewer().setSelectionModel(TreeViewer.SELECTION_MODEL_NONSELECTOR);

		if(this.currentPanel != null) {
    		super.add(this.currentPanel, 2);
    	}
    	
    	super.revalidate();
    	super.repaint();
    }
}
