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
package org.rcsb.pw.ui.coloringDescriptions.backbone;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.rcsb.mbt.model.Chain;
import org.rcsb.mbt.model.StructureModel;
import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.StructureMap;
import org.rcsb.mbt.model.attributes.ChainStyle;
import org.rcsb.mbt.model.attributes.IResidueColor;
import org.rcsb.mbt.model.attributes.ResidueColorByFragmentType;
import org.rcsb.mbt.model.attributes.ResidueColorByHydrophobicity;
import org.rcsb.mbt.model.attributes.ResidueColorByResidueCompound;
import org.rcsb.mbt.model.attributes.ResidueColorByResidueIndex;
import org.rcsb.mbt.model.attributes.StructureStyles;
import org.rcsb.pw.ui.DescriptionPanel;
import org.rcsb.pw.ui.FullWidthBoxLayout;
import org.rcsb.uiApp.controllers.app.AppBase;
import org.rcsb.uiApp.controllers.update.IUpdateListener;
import org.rcsb.uiApp.controllers.update.UpdateEvent;
import org.rcsb.vf.controllers.app.VFAppBase;
import org.rcsb.vf.glscene.jogl.DisplayListRenderable;
import org.rcsb.vf.glscene.jogl.GlGeometryViewer;
import org.rcsb.vf.glscene.jogl.JoglSceneNode;




public class BackboneColoringOptions extends JPanel implements IUpdateListener, ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 9020245685845161328L;
	private final JRadioButton byIndexButton = new JRadioButton("Chain Color Ramp");
	private final JRadioButton byFragmentTypeButton = new JRadioButton("Conformation Type");
    //JButton byRandomFragmentButton = new JButton("Random fragment colors");
    //JButton byRandomResidueButton = new JButton("Random residue colors");
	private final JRadioButton byHydrophobicityButton = new JRadioButton("Hydrophobicity");
	private final JRadioButton byCompoundButton = new JRadioButton("By Compound");
//	private final JRadioButton byResidueBFactorButton = new JRadioButton("By Average Residue B Factor");
//	private final JRadioButton bySideChainBFactorButton = new JRadioButton("By Average Side Chain B Factor");
	private final ButtonGroup buttonGroup = new ButtonGroup();
	
	private final JButton button = new JButton("Enact");
	
	private final FullWidthBoxLayout layout = new FullWidthBoxLayout();

    private ByIndexPanel byIndexPanel = null;
    private ByFragmentTypePanel byFragmentTypePanel = null;
    private ByHydrophobicityPanel byHydrophobicityPanel = null;
    private ByCompoundPanel byCompoundPanel = null;
    
    public BackboneColoringOptions() {
        super(null, false);
        super.setLayout(this.layout);
        
        this.byIndexPanel = new ByIndexPanel();
        this.byFragmentTypePanel = new ByFragmentTypePanel();
        this.byHydrophobicityPanel = new ByHydrophobicityPanel();
        this.byCompoundPanel = new ByCompoundPanel();
        
        super.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Recolor the backbone by..."),
                BorderFactory.createEmptyBorder(-1,1,1,1)));
        
        super.add(this.byIndexButton);
        super.add(this.byFragmentTypeButton);
        //super.add(this.byRandomFragmentButton);
        //super.add(this.byRandomResidueButton);
        super.add(this.byHydrophobicityButton);
        super.add(this.byCompoundButton);
//        super.add(this.byResidueBFactorButton);
//        super.add(this.bySideChainBFactorButton);
        super.add(this.button);
        
        this.buttonGroup.add(this.byIndexButton);
        this.buttonGroup.add(this.byFragmentTypeButton);
        this.buttonGroup.add(this.byHydrophobicityButton);
        this.buttonGroup.add(this.byCompoundButton);
//        this.buttonGroup.add(this.byResidueBFactorButton);
//        this.buttonGroup.add(this.bySideChainBFactorButton);
        
        this.button.addActionListener(this);
        this.byCompoundButton.addActionListener(this);
        this.byFragmentTypeButton.addActionListener(this);
        this.byHydrophobicityButton.addActionListener(this);
        this.byIndexButton.addActionListener(this);
//        this.byResidueBFactorButton.addActionListener(this);
//        this.bySideChainBFactorButton.addActionListener(this);
        
        super.add(this.byCompoundPanel);
        super.add(this.byFragmentTypePanel);
        super.add(this.byHydrophobicityPanel);
        super.add(this.byIndexPanel);
        
        AppBase.sgetUpdateController().registerListener(this);
        
        this.byIndexButton.doClick();
        this.setDescriptionPanel(this.byIndexPanel);
    }
    
    private void setDescriptionPanel(final DescriptionPanel panel) {
    	this.byCompoundPanel.setVisible(false);
    	this.byFragmentTypePanel.setVisible(false);
    	this.byHydrophobicityPanel.setVisible(false);
    	this.byIndexPanel.setVisible(false);
    	
    	this.layout.setDescriptionPanel(panel);
    	
    	panel.setVisible(true);
    }

    public void reset() {
        this.byIndexButton.doClick();
        this.button.doClick();
    }

	public void actionPerformed(final ActionEvent e)
	{
		final Object source = e.getSource();
		
		if(source == this.button) {
			IResidueColor residueColor = null;
			if(this.byIndexButton.isSelected()) {
				residueColor = ResidueColorByResidueIndex.create();
			} else if(this.byCompoundButton.isSelected()) {
				residueColor = ResidueColorByResidueCompound.create();
			} else if(this.byFragmentTypeButton.isSelected()) {
				residueColor = ResidueColorByFragmentType.create();
			} else if(this.byHydrophobicityButton.isSelected()) {
				residueColor = ResidueColorByHydrophobicity.create();
//			} else if(this.byResidueBFactorButton.isSelected()) {
//				residueColor = ResidueColorByAverageBFactor.create();
//			} else if(this.bySideChainBFactorButton.isSelected()) {
//				residueColor = ResidueColorByAverageSideChainBFactor.create();
			} else {
				(new Exception()).printStackTrace();
			}
			
			final StructureModel model = VFAppBase.sgetModel();
			GlGeometryViewer glViewer = VFAppBase.sgetGlGeometryViewer();
			if(model.hasStructures())
			{
				for(Structure s : model.getStructures())
				{
			        final StructureMap sm = s.getStructureMap();
			        final StructureStyles ss = sm.getStructureStyles();
			        final JoglSceneNode sn = (JoglSceneNode)sm.getUData();
			        
			        final ChainStyle newStyle = new ChainStyle();
			        newStyle.setResidueColor(residueColor);
			        
			        final int chainCount = sm.getChainCount();
			        for(int i = 0; i < chainCount; i++) {
			        	final Chain c = sm.getChain(i);
			            final DisplayListRenderable renderable = sn.getRenderable(c);
			        	if(renderable != null) {
			        		renderable.style = newStyle;
			        		ss.setStyle(renderable.structureComponent, newStyle);
		//	        		renderable.setDirty();
			        	}
			        }
			        
			        sn.regenerateGlobalList();
				}
			}
			
			glViewer.requestRepaint();
			}
		
			else if(source == this.byCompoundButton)
			{
				if(this.byCompoundButton.isSelected())
					this.setDescriptionPanel(this.byCompoundPanel);

			}
			
			else if(source == this.byFragmentTypeButton)
			{
				if(this.byFragmentTypeButton.isSelected())
					this.setDescriptionPanel(this.byFragmentTypePanel);

			}
			
			else if(source == this.byHydrophobicityButton)
			{
				if(this.byHydrophobicityButton.isSelected())
					this.setDescriptionPanel(this.byHydrophobicityPanel);
			}
			
			else if(source == this.byIndexButton)
			{
				if(this.byIndexButton.isSelected())
					this.setDescriptionPanel(this.byIndexPanel); 
			}
	}

	/* (non-Javadoc)
	 * @see edu.sdsc.mbt.views_controller.IViewUpdateListener#handleModelChangedEvent(edu.sdsc.mbt.views_controller.ViewUpdateEvent)
	 */
	public void handleUpdateEvent(UpdateEvent evt)
	{
		if (evt.action == UpdateEvent.Action.VIEW_RESET)
			reset();
	}
}
