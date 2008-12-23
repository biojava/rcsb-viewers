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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.rcsb.mbt.model.Atom;
import org.rcsb.mbt.model.Bond;
import org.rcsb.mbt.model.StructureModel;
import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.StructureMap;
import org.rcsb.mbt.model.attributes.AtomColorByBFactor;
import org.rcsb.mbt.model.attributes.AtomColorByElement;
import org.rcsb.mbt.model.attributes.AtomColorByResidueColor;
import org.rcsb.mbt.model.attributes.AtomStyle;
import org.rcsb.mbt.model.attributes.BondColorByAtomColor;
import org.rcsb.mbt.model.attributes.BondStyle;
import org.rcsb.mbt.model.attributes.IAtomColor;
import org.rcsb.mbt.model.attributes.IBondColor;
import org.rcsb.mbt.model.attributes.StructureStyles;
import org.rcsb.pw.controllers.app.ProteinWorkshop;
import org.rcsb.uiApp.controllers.app.AppBase;
import org.rcsb.uiApp.controllers.update.IUpdateListener;
import org.rcsb.uiApp.controllers.update.UpdateEvent;
import org.rcsb.vf.glscene.jogl.DisplayListRenderable;
import org.rcsb.vf.glscene.jogl.JoglSceneNode;


public class AtomBondColoringOptions extends JPanel implements IUpdateListener, ActionListener
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 2595691224047801844L;
	JRadioButton byElementButton = new JRadioButton("Element");
    JRadioButton byBFactorButton = new JRadioButton("B Factor");
    JRadioButton byBackboneColorButton = new JRadioButton("Corresponding Backbone Color");
    //JRadioButton byRandomAtomButton = new JRadioButton("Random Colors");
    
    private final ButtonGroup buttonGroup = new ButtonGroup();
	private final JButton button = new JButton("Enact");
    
	private final FullWidthBoxLayout layout = new FullWidthBoxLayout();
    
    public AtomBondColoringOptions() {
        super(null, false);
        
        super.setLayout(this.layout);
        
        super.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Recolor the atoms/bonds by..."),
                BorderFactory.createEmptyBorder(-1,1,1,1)));
        
        super.add(this.byElementButton);
        super.add(this.byBFactorButton);
        super.add(this.byBackboneColorButton);
        super.add(this.button);
        
        this.buttonGroup.add(this.byElementButton);
        this.buttonGroup.add(this.byBFactorButton);
        this.buttonGroup.add(this.byBackboneColorButton);
        
        this.button.addActionListener(this);
        
        AppBase.sgetUpdateController().registerListener(this);
        
        this.byElementButton.doClick();
    }

    public void reset() {
        this.byElementButton.doClick();
        this.button.doClick();
    }

	public void actionPerformed(final ActionEvent e) {
		IAtomColor atomColor = null;
		if(this.byElementButton.isSelected()) {
			atomColor = AtomColorByElement.create();
		} else if(this.byBFactorButton.isSelected()) {
			atomColor = AtomColorByBFactor.create();
		} else if(this.byBackboneColorButton.isSelected()) {
			atomColor = AtomColorByResidueColor.create();
		} else {
			(new Exception()).printStackTrace();
		}
		final IBondColor bondColor = BondColorByAtomColor.create();
		
		StructureModel model = AppBase.sgetModel();
		if(model.hasStructures())
		{
			for(Structure s : model.getStructures())
			{
		        final StructureMap sm = s.getStructureMap();
		        final StructureStyles ss = sm.getStructureStyles();
		        final JoglSceneNode sn = (JoglSceneNode)sm.getUData();
		        
		        for (Atom a : sm.getAtoms())
		        {
		        	final DisplayListRenderable renderable = sn.getRenderable(a);
		        	if(renderable != null)
		        	{
		        		final AtomStyle newAtomStyle = new AtomStyle();
		        		final AtomStyle oldAtomStyle = (AtomStyle)renderable.style;
		                newAtomStyle.setAtomColor(atomColor);
		                if (oldAtomStyle != null)
		                {
			                newAtomStyle.setAtomLabel(oldAtomStyle.getAtomLabel());
			                newAtomStyle.setAtomRadius(oldAtomStyle.getAtomRadius());
		                }
		        		
		        		renderable.style = newAtomStyle;
		        		ss.setStyle(a, newAtomStyle);
		//        		renderable.setDirty();
		        	}
		        }
		        
		        for (Bond b : sm.getBonds())
		        {
		        	final DisplayListRenderable renderable = sn.getRenderable(b);
		        	if(renderable != null) {
		        		final BondStyle oldBondStyle = (BondStyle)renderable.style;
		        		final BondStyle newBondStyle = new BondStyle();
		                newBondStyle.setBondColor(bondColor);
		        		if(oldBondStyle != null) {
		        			newBondStyle.setBondForm(oldBondStyle.getBondForm());
		        			newBondStyle.setBondLabel(oldBondStyle.getBondLabel());
		        			newBondStyle.setBondRadius(oldBondStyle.getBondRadius());
		        		}
		                
		        		renderable.style = newBondStyle;
		        		ss.setStyle(b, newBondStyle);
		//        		renderable.setDirty();
		        	}
		        }
		        
		        sn.regenerateGlobalList();
			}
		}
        
        ProteinWorkshop.sgetGlGeometryViewer().requestRepaint();
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
