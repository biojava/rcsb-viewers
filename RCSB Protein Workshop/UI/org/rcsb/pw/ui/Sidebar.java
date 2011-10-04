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
package org.rcsb.pw.ui;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.rcsb.vf.controllers.app.VFAppBase;
import org.rcsb.pw.ui.mutatorPanels.MutatorBasePanel;




public class Sidebar extends JPanel implements ChangeListener
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 4223763741039492L;
	//private Model model = null;
    public JTabbedPane tabs = null;
//    public BatchApplyPanel batchPanel = null;
    
    public MutatorBasePanel mutators = null;
    public ColoringOptions coloring = null;
    public GlobalOptionsPanel tools = null;
    public CreditsPanel credits = null;
    public DebugTab debug = null;
    
    public Sidebar()
    {
    	super(null, false);
    	super.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    	
        this.tabs = new JTabbedPane();
//        this.batchPanel = new BatchApplyPanel();
        this.mutators = new MutatorBasePanel();
        

        this.tools = new GlobalOptionsPanel();
        this.coloring = new ColoringOptions();
        this.credits = new CreditsPanel();
        if(VFAppBase.sgetSceneController().isDebugEnabled()) {
        	this.debug = new DebugTab();
        }
        
        this.tabs.setDoubleBuffered(false);
        this.tabs.addTab("Tools", this.mutators);
        this.tabs.addTab("Shortcuts", this.coloring);
        this.tabs.addTab("Options", this.tools);
        this.tabs.addTab("Help and Credits", this.credits);
        if(VFAppBase.sgetSceneController().isDebugEnabled()) {
        	this.tabs.addTab("Debug", this.debug);
        }
        //this.tabs.addTab("Debug", Debug.getPanel());
        
        this.tabs.addChangeListener(this);
        
        super.add(this.tabs);
        
        System.out.println("Sidebar: mutatorBasePanel" + mutators.getPreferredSize().width);
        System.out.println("Sidebar: GlobalOptionsPanel" + tools.getPreferredSize().width);
        System.out.println("Sidebar: ColoringOptions" + coloring.getPreferredSize().width);
        System.out.println("Sidebar: CreditsPanel" + credits.getPreferredSize().width);
        System.out.println("Sidebar: width" + this.getPreferredSize().width);
    }


	public void stateChanged(final ChangeEvent e) {
        // user has implicitly disabled this mode if it was enabled.
		VFAppBase.sgetSceneController().setColorSelectorSampleModeEnabled(false);
	}
}
