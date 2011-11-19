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

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.rcsb.pw.controllers.app.ProteinWorkshop;
import org.rcsb.pw.ui.ColorPreviewerPanel;
import org.rcsb.pw.ui.FullWidthBoxLayout;
import org.rcsb.vf.controllers.scene.mutators.MutatorBase;


public class ColorOptionsPanel extends JPanel
{
	private static final long serialVersionUID = 4557607549738209715L;
	public JLabel activeColorLabel = null;
	public ColorPreviewerPanel activeColorPanel = null;
	    
    public ColorOptionsPanel() {
        super(null, false);
 //       super.setLayout(new FullWidthBoxLayout());
		super.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        super.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("3)  Change the tool's options, if necessary."),
                BorderFactory.createEmptyBorder(-1,1,1,1)));
        
        
        // create the interface objects...
        this.activeColorLabel = new JLabel("Select Color ");
        this.activeColorPanel = new ColorPreviewerPanel();
        
        super.add(this.activeColorLabel);
        super.add(this.activeColorPanel);
        
        ProteinWorkshop.sgetActiveFrame().setColorOptionsPanel(this);
    }
    
    public void updateMutatorActivation(final MutatorBase.ActivationType activationType) {
	
    	switch(activationType) {
    	case ATOMS_AND_BONDS:
    		break;
    	case RIBBONS:
    		break;
    	case SURFACE:
    		break;
    	default:
    		(new Exception(activationType + " is an invalid pick level")).printStackTrace();
    	}
    	
    	super.revalidate();
    	super.repaint();
    }
}
