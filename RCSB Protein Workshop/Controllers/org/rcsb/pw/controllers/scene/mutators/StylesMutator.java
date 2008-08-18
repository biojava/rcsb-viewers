package org.rcsb.pw.controllers.scene.mutators;

import java.util.Iterator;
import java.util.Vector;

import org.rcsb.mbt.controllers.app.AppBase;
import org.rcsb.mbt.controllers.scene.PickLevel;
import org.rcsb.mbt.glscene.jogl.AtomGeometry;
import org.rcsb.mbt.glscene.jogl.BondGeometry;
import org.rcsb.mbt.glscene.jogl.ChainGeometry;
import org.rcsb.mbt.glscene.jogl.DisplayListRenderable;
import org.rcsb.mbt.glscene.jogl.Geometry;
import org.rcsb.mbt.glscene.jogl.GlGeometryViewer;
import org.rcsb.mbt.glscene.jogl.JoglSceneNode;
import org.rcsb.mbt.model.Atom;
import org.rcsb.mbt.model.Bond;
import org.rcsb.mbt.model.Chain;
import org.rcsb.mbt.model.Fragment;
import org.rcsb.mbt.model.MiscellaneousMoleculeChain;
import org.rcsb.mbt.model.PdbChain;
import org.rcsb.mbt.model.Residue;
import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.StructureMap;
import org.rcsb.mbt.model.WaterChain;
import org.rcsb.mbt.model.attributes.AtomStyle;
import org.rcsb.mbt.model.attributes.StructureStyles;
import org.rcsb.pw.controllers.scene.mutators.options.StylesOptions;




public class StylesMutator extends Mutator {
	private StylesOptions options = null; 
	
	public StylesMutator() {
		super();
		this.options = new StylesOptions();
	}

	
	public boolean supportsBatchMode() {
		return true;
	}
	
	
	public void doMutationSingle(final Object mutee) {
		Mutator.mutees.clear();
		Mutator.mutees.put(mutee, null);
		this.doMutation();
		Mutator.mutees.clear();
	}
	
	
	public void doMutation() {
		final Iterator it = super.mutees.keySet().iterator();
		while(it.hasNext()) {
			final Object next = it.next();
			if(next instanceof Atom) {
				this.changeStyle((Atom)next);
			} else if(next instanceof Bond) {
				this.changeStyle((Bond)next);
			} else if(next instanceof Residue) {
				this.changeStyle((Residue)next);
			} else if(next instanceof Chain) {
				this.changeStyle((Chain)next);
			} else if(next instanceof PdbChain) {
				this.changeStyle((PdbChain)next);
			} else if(next instanceof WaterChain) {
				this.changeStyle((WaterChain)next);
			} else if(next instanceof MiscellaneousMoleculeChain) {
				this.changeStyle((MiscellaneousMoleculeChain)next);
			} else if(next instanceof Fragment) {
				this.changeStyle((Fragment)next);
			} else if(next instanceof Structure) {
				this.changeStyle((Structure)next);
			}
		}
		
//		Model.getSingleton().getStructures()[0].getStructureMap().getSceneNode().regenerateGlobalList();
	}

	public StylesOptions getOptions() {
		return this.options;
	}	
	
	private void changeStyle(final Atom a) {
		final Structure s = a.getStructure();
		final StructureMap sm = s.getStructureMap();
		final StructureStyles ss = sm.getStructureStyles();
		
        switch(PickLevel.pickLevel) {
        case PickLevel.COMPONENTS_ATOMS_BONDS:
            final DisplayListRenderable renderable = sm.getSceneNode().getRenderable(a);
        	if(renderable != null)
        	{
        		final AtomStyle oldStyle = (AtomStyle)ss.getStyle(a);
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
        		
        		final Vector bonds = sm.getBonds(a);
        		final int bondSize = bonds.size();
        		for(int i = 0; i < bondSize; i++) {
        			this.changeBondStyleBasedOnAtoms((Bond)bonds.get(i));
        		}
        	}
        	
        	break;
        case PickLevel.COMPONENTS_RIBBONS:
        	// propogate up one level.
        	this.changeStyle(sm.getResidue(a));
        	break;
        default:
        	(new Exception(PickLevel.pickLevel + " is an invalid style mode.")).printStackTrace();
        }
    }
	
	private void changeBondStyleBasedOnAtoms(final Bond b) {
		final JoglSceneNode sn = b.structure.getStructureMap().getSceneNode();
		
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
		final StructureStyles ss = sm.getStructureStyles();
		
        switch(PickLevel.pickLevel) {
        case PickLevel.COMPONENTS_ATOMS_BONDS:
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
        	this.changeStyle(b.getAtom(0));
        	this.changeStyle(b.getAtom(1));
        	break;
        case PickLevel.COMPONENTS_RIBBONS:
        	// propogate up one level.
        	this.changeStyle(sm.getResidue(b.getAtom(0)));
        	break;
        default:
        	(new Exception(PickLevel.pickLevel + " is an invalid style mode.")).printStackTrace();
        }     
    }
    
    private void changeStyle(final Residue r) {
    	final Structure s = r.getStructure();
		final StructureMap sm = s.getStructureMap();
		final StructureStyles ss = sm.getStructureStyles();
		
        switch(PickLevel.pickLevel) {
        case PickLevel.COMPONENTS_ATOMS_BONDS:
        	/*Iterator atomsIt = r.getAtoms().iterator();
        	while(atomsIt.hasNext()) {
        		Atom a = (Atom)atomsIt.next();
        		this.changeStyle(a);
        	}*/
        	
        	final Iterator bondsIt = r.getStructure().getStructureMap().getBonds(r.getAtoms()).iterator();
        	while(bondsIt.hasNext()) {
        		final Bond b = (Bond)bondsIt.next();
        		this.changeStyle(b);
        	}
        	break;
        case PickLevel.COMPONENTS_RIBBONS:
        	// propogate up one level.
        	this.changeStyle(r.getFragment());
        	break;
        default:
        	(new Exception(PickLevel.pickLevel + " is an invalid style mode.")).printStackTrace();
        }
    }
    
    private void changeStyle(final Fragment f) {
    	final Structure s = f.getStructure();
		final StructureMap sm = s.getStructureMap();
		final StructureStyles ss = sm.getStructureStyles();
		
        switch(PickLevel.pickLevel) {
        case PickLevel.COMPONENTS_ATOMS_BONDS:
        	final Iterator residueIt = f.getResidues().iterator();
        	while(residueIt.hasNext()) {
        		final Residue r = (Residue)residueIt.next();
        		
        		/*Iterator atomsIt = r.getAtoms().iterator();
            	while(atomsIt.hasNext()) {
            		Atom a = (Atom)atomsIt.next();
            		this.changeStyle(a);
            	}*/
        		
        		final Iterator bondsIt = sm.getBonds(r.getAtoms()).iterator();
            	while(bondsIt.hasNext()) {
            		final Bond b = (Bond)bondsIt.next();
            		this.changeStyle(b);
            	}
        	}
        	break;
        case PickLevel.COMPONENTS_RIBBONS:
        	// propogate up one level.
        	this.changeStyle(f.getChain());
        	break;
        default:
        	(new Exception(PickLevel.pickLevel + " is an invalid style mode.")).printStackTrace();
        }
    }
    
    private void changeStyle(final PdbChain c) {
    	final Structure s = c.structure;
		final StructureMap sm = s.getStructureMap();
		final StructureStyles ss = sm.getStructureStyles();
		
        switch(PickLevel.pickLevel) {
        case PickLevel.COMPONENTS_ATOMS_BONDS:
        	final Iterator residueIt = c.getResidueIterator();
        	while(residueIt.hasNext()) {
        		final Residue r = (Residue)residueIt.next();
        		
        		/*Iterator atomsIt = r.getAtoms().iterator();
            	while(atomsIt.hasNext()) {
            		Atom a = (Atom)atomsIt.next();
            		this.changeStyle(a);
            	}*/
        		
        		final Iterator bondsIt = sm.getBonds(r.getAtoms()).iterator();
            	while(bondsIt.hasNext()) {
            		final Bond b = (Bond)bondsIt.next();
            		this.changeStyle(b);
            	}
        	}
        	break;
        case PickLevel.COMPONENTS_RIBBONS:
        	// propogate to the chains
        	final Iterator chainIt = c.getMbtChainIterator();
        	while(chainIt.hasNext()) {
        		final Chain mbtChain = (Chain)chainIt.next();
        		this.changeStyle(mbtChain);
        	}
        	break;
        default:
        	(new Exception(PickLevel.pickLevel + " is an invalid style mode.")).printStackTrace();
        }
    }
    
    private void changeStyle(final Chain c) {
    	final Structure s = c.getStructure();
		final StructureMap sm = s.getStructureMap();
		final StructureStyles ss = sm.getStructureStyles();
		
        switch(PickLevel.pickLevel) {
        case PickLevel.COMPONENTS_ATOMS_BONDS:
        	final Iterator fragmentIt = c.getFragments().iterator();
        	while(fragmentIt.hasNext()) {
        		final Fragment f = (Fragment)fragmentIt.next();
        		
        		this.changeStyle(f);
        	}
        	break;
        case PickLevel.COMPONENTS_RIBBONS:
        	// propogate up one level.
            final DisplayListRenderable renderable = sm.getSceneNode().getRenderable(c);
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
        default:
        	(new Exception(PickLevel.pickLevel + " is an invalid style mode.")).printStackTrace();
        }        
    }
    
    private void changeStyle(final WaterChain c) {
    	final Structure s = c.structure;
		final StructureMap sm = s.getStructureMap();
		final StructureStyles ss = sm.getStructureStyles();
		
        switch(PickLevel.pickLevel) {
        case PickLevel.COMPONENTS_ATOMS_BONDS:
        	final Iterator residueIt = c.getResidueIterator();
        	while(residueIt.hasNext()) {
        		final Residue r = (Residue)residueIt.next();
        		
        		/*Iterator atomsIt = r.getAtoms().iterator();
            	while(atomsIt.hasNext()) {
            		Atom a = (Atom)atomsIt.next();
            		this.changeStyle(a);
            	}*/
        		
        		final Iterator bondsIt = sm.getBonds(r.getAtoms()).iterator();
            	while(bondsIt.hasNext()) {
            		final Bond b = (Bond)bondsIt.next();
            		this.changeStyle(b);
            	}
        	}
        	break;
        case PickLevel.COMPONENTS_RIBBONS:
        	// propogate to the chains
        	final Iterator chainIt = c.getMbtChainIterator();
        	while(chainIt.hasNext()) {
        		final Chain mbtChain = (Chain)chainIt.next();
        		this.changeStyle(mbtChain);
        	}
        	break;
        default:
        	(new Exception(PickLevel.pickLevel + " is an invalid style mode.")).printStackTrace();
        }
    }
    
    private void changeStyle(final MiscellaneousMoleculeChain c) {
    	final Structure s = c.structure;
		final StructureMap sm = s.getStructureMap();
		final StructureStyles ss = sm.getStructureStyles();
		
        switch(PickLevel.pickLevel) {
        case PickLevel.COMPONENTS_ATOMS_BONDS:
        	final Iterator residueIt = c.getResidueIterator();
        	while(residueIt.hasNext()) {
        		final Residue r = (Residue)residueIt.next();
        		
        		/*Iterator atomsIt = r.getAtoms().iterator();
            	while(atomsIt.hasNext()) {
            		Atom a = (Atom)atomsIt.next();
            		this.changeStyle(a);
            	}*/
        		
        		final Iterator bondsIt = sm.getBonds(r.getAtoms()).iterator();
            	while(bondsIt.hasNext()) {
            		final Bond b = (Bond)bondsIt.next();
            		this.changeStyle(b);
            	}
        	}
        	break;
        case PickLevel.COMPONENTS_RIBBONS:
        	// propogate to the chains
        	final Iterator chainIt = c.getMbtChainIterator();
        	while(chainIt.hasNext()) {
        		final Chain mbtChain = (Chain)chainIt.next();
        		this.changeStyle(mbtChain);
        	}
        	break;
        default:
        	(new Exception(PickLevel.pickLevel + " is an invalid style mode.")).printStackTrace();
        }        
    }
    
    private void changeStyle(final Structure s) {
    	// propogate everything down to the chains.
    	final StructureMap sm = s.getStructureMap();
    	final Iterator chainIt = sm.getChains().iterator();
    	while(chainIt.hasNext()) {
    		final Chain c = (Chain)chainIt.next();
    		this.changeStyle(c);
    	}
    }
}
