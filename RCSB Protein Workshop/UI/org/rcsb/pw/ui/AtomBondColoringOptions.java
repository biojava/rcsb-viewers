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

import org.rcsb.mbt.controllers.app.AppBase;
import org.rcsb.mbt.controllers.update.IUpdateListener;
import org.rcsb.mbt.controllers.update.UpdateEvent;
import org.rcsb.mbt.glscene.jogl.DisplayListRenderable;
import org.rcsb.mbt.glscene.jogl.JoglSceneNode;
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
		        final JoglSceneNode sn = sm.getSceneNode();
		        
		        final Vector atoms = sm.getAtoms();
		        final Iterator atomIt = atoms.iterator();
		        while(atomIt.hasNext()) {
		        	final Atom a = (Atom)atomIt.next();
		        	final DisplayListRenderable renderable = sn.getRenderable(a);
		        	if(renderable != null) {
		        		final AtomStyle newAtomStyle = new AtomStyle();
		        		final AtomStyle oldAtomStyle = (AtomStyle)renderable.style;
		                newAtomStyle.setAtomColor(atomColor);
		                if(oldAtomStyle != null) {
			                newAtomStyle.setAtomLabel(oldAtomStyle.getAtomLabel());
			                newAtomStyle.setAtomRadius(oldAtomStyle.getAtomRadius());
		                }
		        		
		        		renderable.style = newAtomStyle;
		        		ss.setStyle(a, newAtomStyle);
		//        		renderable.setDirty();
		        	}
		        }
		        
		        final Iterator bondsIt = sm.getBonds(atoms).iterator();
		        while(bondsIt.hasNext()) {
		        	final Bond b = (Bond)bondsIt.next();
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
	public void handleModelChangedEvent(UpdateEvent evt)
	{
		if (evt.action == UpdateEvent.Action.VIEW_RESET)
			reset();
	}
}