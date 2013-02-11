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
package org.rcsb.pw.controllers.scene.mutators;

import org.rcsb.mbt.model.Atom;
import org.rcsb.mbt.model.Bond;
import org.rcsb.mbt.model.Chain;
import org.rcsb.mbt.model.ExternChain;
import org.rcsb.mbt.model.Fragment;
import org.rcsb.mbt.model.Residue;
import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.StructureMap;
import org.rcsb.mbt.model.Surface;
import org.rcsb.mbt.model.attributes.AtomStyle;
import org.rcsb.mbt.model.attributes.StructureStyles;
import org.rcsb.pw.controllers.scene.mutators.options.StylesOptions;
import org.rcsb.vf.controllers.scene.mutators.MutatorBase;
import org.rcsb.vf.glscene.jogl.AtomGeometry;
import org.rcsb.vf.glscene.jogl.BondGeometry;
import org.rcsb.vf.glscene.jogl.ChainGeometry;
import org.rcsb.vf.glscene.jogl.DisplayListRenderable;
import org.rcsb.vf.glscene.jogl.Geometry;
import org.rcsb.vf.glscene.jogl.JoglSceneNode;

public class StylesMutator extends MutatorBase {
	private StylesOptions options = null; 
	
	public StylesMutator() {
		super();
		this.options = new StylesOptions();
	}

	
	@Override
	public boolean supportsBatchMode() {
		return true;
	}
	
	
	@Override
	public void doMutationSingle(final Object mutee) {
		mutees.clear();
		mutees.add(mutee);
		this.doMutation();
		mutees.clear();
	}
	
	
	@Override
	public void doMutation() {
		for (Object next : mutees)
			if(next instanceof Atom) {
				this.changeStyle((Atom)next);
			} else if(next instanceof Bond) {
				this.changeStyle((Bond)next);
			} else if(next instanceof Residue) {
				this.changeStyle((Residue)next);
			} else if(next instanceof Chain) {
				this.changeStyle((Chain)next);
			} else if(next instanceof ExternChain) {
				this.changeStyle((ExternChain)next);
			} else if(next instanceof Fragment) {
				this.changeStyle((Fragment)next);
			} else if(next instanceof Structure) {
				this.changeStyle((Structure)next);
			} else if(next instanceof Surface) {
				this.changeStyle((Surface)next);
			}		
	}

	public StylesOptions getOptions() {
		return this.options;
	}	
	
	private void changeStyle(final Atom a) {
		final Structure s = a.getStructure();
		final StructureMap sm = s.getStructureMap();
		final StructureStyles ss = sm.getStructureStyles();
		
        switch(getActivationType()) {
        case ATOMS_AND_BONDS:
            final DisplayListRenderable renderable = ((JoglSceneNode)sm.getUData()).getRenderable(a);
        	if(renderable != null)
        	{
        	
        		final AtomStyle oldStyle = (AtomStyle)ss.getStyle(a);
        		System.out.println("StylesMutator: changeStyle(atom): "+ oldStyle.getAtomColor());
        		final AtomStyle newStyle = new AtomStyle();
        		if(oldStyle != null) {
	        		newStyle.setAtomColor(oldStyle.getAtomColor());
	        		newStyle.setAtomLabel(oldStyle.getAtomLabel());
        		}
        		newStyle.setAtomRadius(this.options.getCurrentAtomRadius());
        		ss.setStyle(a, newStyle);
        		renderable.style = newStyle;
        		
        		final AtomGeometry oldGeometry = (AtomGeometry)renderable.geometry; // if this is renderable, the geometry will not be null.
        		final AtomGeometry newGeometry = new AtomGeometry();
        		newGeometry.setForm(this.options.getCurrentAtomForm());
        		newGeometry.setQuality(oldGeometry.getQuality());
        		renderable.geometry = newGeometry;
        		
        		renderable.setDirty();
        		
        		for (Bond b : sm.getBonds(a))
        			this.changeBondStyleBasedOnAtoms(b);
        	}
        	
        	break;
        case RIBBONS:
        	// propogate up one level.
        	this.changeStyle(sm.getResidue(a));
        	break;
        	
        case SURFACE:
        	break;
        	
        default:
        	(new Exception(getActivationType() + " is an invalid style mode.")).printStackTrace();
        }
    }
	
	private void changeBondStyleBasedOnAtoms(final Bond b) {
		final JoglSceneNode sn = (JoglSceneNode)b.structure.getStructureMap().getUData();
		
		final DisplayListRenderable renderable = sn.getRenderable(b);
    	if(renderable != null) {
    		// derive bond form from atom form...
    		final DisplayListRenderable atom0Renderable = sn.getRenderable(b.getAtom(0));
    		final DisplayListRenderable atom1Renderable = sn.getRenderable(b.getAtom(1));
    		int bondForm = -1;
    		if((atom0Renderable != null && (atom0Renderable.geometry.getForm() == Geometry.FORM_LINES || atom0Renderable.geometry.getForm() == Geometry.FORM_POINTS)) ||
    				(atom1Renderable != null && (atom1Renderable.geometry.getForm() == Geometry.FORM_LINES || atom1Renderable.geometry.getForm() == Geometry.FORM_POINTS))) {
    			bondForm = Geometry.FORM_LINES;
    		} else {
    			bondForm = Geometry.FORM_THICK;
    		}
    		
    		final BondGeometry oldGeometry = (BondGeometry)renderable.geometry; // if this is renderable, the geometry will not be null.
    		final BondGeometry newGeometry = new BondGeometry();
    		if(bondForm != -1) {
    			newGeometry.setForm(bondForm);
    		}
    		newGeometry.setQuality(oldGeometry.getQuality());
    		newGeometry.setShowOrder(this.options.isBondOrderShown());
    		
    		if(oldGeometry.getForm() != newGeometry.getForm() || oldGeometry.getShowOrder() != newGeometry.getShowOrder()) {
    			renderable.geometry = newGeometry;
    			renderable.setDirty();
    		}
    	}
	}
    
    private void changeStyle(final Bond b) {
    	final Structure s = b.getStructure();
		final StructureMap sm = s.getStructureMap();
		
		ActivationType pickLevel = getActivationType();
        switch(pickLevel) {
        case ATOMS_AND_BONDS:
        	/*GlGeometryViewer viewer = Model.getSingleton().getViewer();
            DisplayListRenderable renderable = viewer.getRenderable(b);
        	if(renderable != null) {
        		BondGeometry oldGeometry = (BondGeometry)renderable.geometry; // if this is renderable, the geometry will not be null.
        		BondGeometry newGeometry = new BondGeometry();
        		newGeometry.setForm(this.options.getCurrentBondForm());
        		newGeometry.setQuality(oldGeometry.getQuality());
        		newGeometry.setShowOrder(this.options.isBondOrderShown());
        		renderable.geometry = newGeometry;
        		
        		renderable.setDirty();
        	}*/
        	
        	// delegate to atoms...
        	System.out.println("StylesMutator: changeStyle(bond): " + b.getAtom(0).compound);
        	this.changeStyle(b.getAtom(0));
        	this.changeStyle(b.getAtom(1));
        	break;
        case RIBBONS:
        	// propogate up one level.
        	this.changeStyle(sm.getResidue(b.getAtom(0)));
        	break;
        	
        case SURFACE:
        	break;
        	
        default:
        	(new Exception(pickLevel + " is an invalid style mode.")).printStackTrace();
        }     
    }
    
    private void changeStyle(final Residue r)
    {
		ActivationType pickLevel = getActivationType();
        switch(pickLevel)
        {
        case ATOMS_AND_BONDS:       	
        	for (Bond b : r.getStructure().getStructureMap().getBonds(r.getAtoms()))
        		this.changeStyle(b);
        	break;
        	
        case RIBBONS:
        	// propogate up one level.
        	this.changeStyle(r.getFragment());
        	break;
        	
        case SURFACE:
        	break;
        	
        default:
        	(new Exception(pickLevel + " is an invalid style mode.")).printStackTrace();
        }
    }
    
    private void changeStyle(final Fragment f) {
    	final Structure s = f.getStructure();
		final StructureMap sm = s.getStructureMap();
		
		ActivationType pickLevel = getActivationType();
        switch(pickLevel) {
        case ATOMS_AND_BONDS:
        	for (Residue r : f.getResidues())
            	for (Bond b : sm.getBonds(r.getAtoms()))
            		this.changeStyle(b);
        	break;
        	
        case RIBBONS:
        	// propogate up one level.
        	this.changeStyle(f.getChain());
        	break;
        	
        case SURFACE:
        	break;
        	
        default:
        	(new Exception(pickLevel + " is an invalid style mode.")).printStackTrace();
        }
    }
    
    private void changeStyle(final ExternChain c)
    {
    	final Structure s = c.structure;
		final StructureMap sm = s.getStructureMap();
		
		ActivationType pickLevel = getActivationType();
        switch(pickLevel)
        {
        case ATOMS_AND_BONDS:
        	for (Residue r : c.getResiduesVec())
            	for (Bond b : sm.getBonds(r.getAtoms()))
            		this.changeStyle(b);
        	break;
        	
        case RIBBONS:
        	// propogate to the chains
        	for (Chain mbtChain : c.getMbtChains())
        		this.changeStyle(mbtChain);
        	break;
        	
        case SURFACE:
        	break;
        	
        default:
        	(new Exception(pickLevel + " is an invalid style mode.")).printStackTrace();
        }
    }
    
    private void changeStyle(final Chain c) {
    	final Structure s = c.getStructure();
		final StructureMap sm = s.getStructureMap();
		
		ActivationType pickLevel = getActivationType();
        switch(pickLevel)
        {
        case ATOMS_AND_BONDS:
        	for (Fragment f : c.getFragments())
        		this.changeStyle(f);
        	break;
        	
        case RIBBONS:
        	// propogate up one level.
            final DisplayListRenderable renderable = ((JoglSceneNode)sm.getUData()).getRenderable(c);
        	if(renderable != null) {
        		final ChainGeometry oldGeometry = (ChainGeometry)renderable.geometry; // if this is renderable, the geometry will not be null.
        		final ChainGeometry newGeometry = new ChainGeometry();
        		newGeometry.setRibbonForm(this.options.getCurrentRibbonForm());
        		newGeometry.setRibbonsAreSmoothed(this.options.areRibbonsSmoothed());
        		newGeometry.setQuality(oldGeometry.getQuality());
        		renderable.geometry = newGeometry;
        		
        		renderable.setDirty();
        	}
        	break;
        case SURFACE:
        	break;
        default:
        	(new Exception(pickLevel + " is an invalid style mode.")).printStackTrace();
        }        
    }
    
    private void changeStyle(final Structure s)
    {
    	for (Chain c : s.getStructureMap().getChains())
    		this.changeStyle(c);
    }
    
    private void changeStyle(final Surface s)
    {
		// TODO
    }
}
