package org.rcsb.pw.controllers.scene.mutators;

import java.awt.Color;
import java.util.Iterator;
import java.util.Vector;

import org.rcsb.mbt.controllers.app.AppBase;
import org.rcsb.mbt.controllers.scene.PickLevel;
import org.rcsb.mbt.controllers.scene.SceneController;
import org.rcsb.mbt.glscene.jogl.DisplayListRenderable;
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
import org.rcsb.mbt.model.StructureComponent;
import org.rcsb.mbt.model.StructureComponentRegistry;
import org.rcsb.mbt.model.StructureMap;
import org.rcsb.mbt.model.WaterChain;
import org.rcsb.mbt.model.attributes.AtomColorByRgb;
import org.rcsb.mbt.model.attributes.AtomStyle;
import org.rcsb.mbt.model.attributes.BondStyle;
import org.rcsb.mbt.model.attributes.ChainStyle;
import org.rcsb.mbt.model.attributes.IResidueColor;
import org.rcsb.mbt.model.attributes.ResidueColorByRgb;
import org.rcsb.mbt.model.attributes.StructureStyles;
import org.rcsb.pw.controllers.app.ProteinWorkshop;
import org.rcsb.pw.controllers.scene.mutators.options.ColorOptions;




public class ColorMutator extends Mutator
{
	private ColorOptions options = null; 
	
	public ColorMutator() {
		super();
		this.options = new ColorOptions();
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

	
	public void doMutation()
	{
		SceneController sceneController = AppBase.sgetSceneController();
		if(sceneController.isColorSelectorSampleModeEnabled() && super.mutees.size() > 0) {
			final Object mutee = super.mutees.keySet().iterator().next();
			
			this.performColorSample(mutee);
			
			sceneController.setColorSelectorSampleModeEnabled(false);
		}
		
		else
		{
			final Iterator it = super.mutees.keySet().iterator();
			while(it.hasNext()) {
				final Object next = it.next();
				if(next instanceof Atom) {
					this.changeColor((Atom)next);
				} else if(next instanceof Bond) {
					this.changeColor((Bond)next);
				} else if(next instanceof Residue) {
					this.changeColor((Residue)next);
				} else if(next instanceof Chain) {
					this.changeColor((Chain)next);
				} else if(next instanceof PdbChain) {
					this.changeColor((PdbChain)next);
				} else if(next instanceof WaterChain) {
					this.changeColor((WaterChain)next);
				} else if(next instanceof MiscellaneousMoleculeChain) {
					this.changeColor((MiscellaneousMoleculeChain)next);
				} else if(next instanceof Fragment) {
					this.changeColor((Fragment)next);
				} else if(next instanceof Structure) {
					final Structure s = (Structure)next;
					this.changeColor(s);
				}
			}
		}
	}
	
	private void performColorSample(final Object mutee) {
		final float[] colorFl = {-1,-1,-1};
		
		if(mutee instanceof StructureComponent) {
			final StructureComponent mutee_ = (StructureComponent)mutee;
			final String type = mutee_.getStructureComponentType();
			
			if(type == StructureComponentRegistry.TYPE_ATOM) {
				final Atom a = (Atom)mutee;
				final AtomStyle style = (AtomStyle)a.structure.getStructureMap().getStructureStyles().getStyle(a);
				
				style.getAtomColor(a, colorFl);
			} else if(type == StructureComponentRegistry.TYPE_BOND) {
				final Bond b = (Bond)mutee;
				final BondStyle style = (BondStyle)b.structure.getStructureMap().getStructureStyles().getStyle(b);
				
				style.getBondColor(b, colorFl);
			} else if(type == StructureComponentRegistry.TYPE_FRAGMENT) {
				final Fragment f = (Fragment)mutee;
				final Chain c = f.getChain();
				final ChainStyle style = (ChainStyle)c.structure.getStructureMap().getStructureStyles().getStyle(c);
				
				style.getResidueColor(f.getResidue(f.getResidueCount() / 2), colorFl);
			} else if(type == StructureComponentRegistry.TYPE_RESIDUE) {
				final Residue r = (Residue)mutee;
				final StructureMap sm = r.structure.getStructureMap();
				
				final Chain c = sm.getChain(r.getChainId());
				final ChainStyle style = (ChainStyle)c.structure.getStructureMap().getStructureStyles().getStyle(c);
				
				style.getResidueColor(r, colorFl);
			} else if(type == StructureComponentRegistry.TYPE_CHAIN) {
				final Chain c = (Chain)mutee;
				
				final ChainStyle style = (ChainStyle)c.structure.getStructureMap().getStructureStyles().getStyle(c);
				style.getResidueColor(c.getResidue(c.getResidueCount() / 2), colorFl);
			} else if(mutee_ instanceof PdbChain) {
				final PdbChain c = (PdbChain)mutee;
				
				final ChainStyle style = (ChainStyle)c.structure.getStructureMap().getStructureStyles().getStyle((Chain)c.getMbtChainIterator().next());
				style.getResidueColor(c.getResidue(c.getResidueCount() / 2), colorFl);
			} else if(mutee_ instanceof WaterChain) {
				final WaterChain c = (WaterChain)mutee;
				
				final ChainStyle style = (ChainStyle)c.structure.getStructureMap().getStructureStyles().getStyle((Chain)c.getMbtChainIterator().next());
				style.getResidueColor(c.getResidue(c.getResidueCount() / 2), colorFl);
			} else if(mutee_ instanceof MiscellaneousMoleculeChain) {
				final MiscellaneousMoleculeChain c = (MiscellaneousMoleculeChain)mutee;
				
				final ChainStyle style = (ChainStyle)c.structure.getStructureMap().getStructureStyles().getStyle((Chain)c.getMbtChainIterator().next());
				style.getResidueColor(c.getResidue(c.getResidueCount() / 2), colorFl);
			}
		} else if(mutee instanceof Structure) {
			final Structure s = (Structure)mutee;
			final StructureMap sm = s.getStructureMap();
			
			final Chain firstChain = sm.getChain(0);
			
			final ChainStyle style = (ChainStyle)sm.getStructureStyles().getStyle(firstChain);
			style.getResidueColor(firstChain.getResidue(firstChain.getResidueCount() / 2), colorFl);
		}
		
		if(colorFl[0] == -1 && colorFl[1] == -1 && colorFl[2] == -1) {
			(new Exception(mutee.getClass().getName() + " not handled.")).printStackTrace();
		} else {
			final Color color = new Color(colorFl[0], colorFl[1], colorFl[2]);
			ProteinWorkshop.sgetActiveFrame().getColorPreviewerPanel().getColorPane().setColor(color);
		}
	}

	public ColorOptions getOptions() {
		return this.options;
	}	
	
	private static final float[] colorFl = new float[] {0,0,0};
	
    private void changeColor(final Atom a) {
    	final Structure struc = a.structure;
        final StructureMap sm = struc.getStructureMap();
        final StructureStyles ss = sm.getStructureStyles();
        
        switch(PickLevel.pickLevel) {
        case PickLevel.COMPONENTS_ATOMS_BONDS:
        	this.options.getCurrentColor().getColorComponents(ColorMutator.colorFl);
        	
        	final GlGeometryViewer viewer = AppBase.sgetGlGeometryViewer();
            final DisplayListRenderable renderable = ((JoglSceneNode)sm.getUData()).getRenderable(a);
            if(renderable != null) {
            	final AtomStyle oldStyle = (AtomStyle)renderable.style;
            	final AtomStyle style = new AtomStyle();
                style.setAtomColor(new AtomColorByRgb(ColorMutator.colorFl));
                if(oldStyle != null) {
                	style.setAtomLabel(oldStyle.getAtomLabel());
                	style.setAtomRadius(oldStyle.getAtomRadius());
                }
                ss.setStyle(a, style);
                
            	renderable.style = style;
//            	renderable.setDirty();
            }
            break;
        case PickLevel.COMPONENTS_RIBBONS:
            final Residue r = sm.getResidue(a);
            this.changeColor(r.getFragment());
            break;
        default:
            (new Exception()).printStackTrace();            
        }
    }
    
    private void changeColor(final Bond b) {
        final Structure struc = b.structure;
        final StructureMap sm = struc.getStructureMap();
        final StructureStyles ss = sm.getStructureStyles();
        
        switch(PickLevel.pickLevel) {
        case PickLevel.COMPONENTS_ATOMS_BONDS:
        	/*this.options.getCurrentColor().getColorComponents(colorFl);
            BondColorByRgb bondColor = new BondColorByRgb(colorFl);
            
            GlGeometryViewer viewer = Model.getSingleton().getViewer();
            DisplayListRenderable renderable = viewer.getRenderable(b);
            if(renderable != null) {
            	BondStyle oldStyle = (BondStyle)renderable.style;
            	BondStyle style = new BondStyle();
                style.setBondColor(bondColor);
                if(oldStyle != null) {
	                style.setBondForm(oldStyle.getBondForm());
	                style.setBondLabel(oldStyle.getBondLabel());
	                style.setBondRadius(oldStyle.getBondRadius());
                }
                ss.setStyle(b, style);
            	
            	renderable.style = style;
//            	renderable.setDirty();
            }*/
        	
        	// delegate to atoms...
        	this.changeColor(b.getAtom(0));
        	this.changeColor(b.getAtom(1));
            break;
        case PickLevel.COMPONENTS_RIBBONS:
            // even if this is between fragments, just change one of them arbitrarily.
            final Residue r = sm.getResidue(b.getAtom(0));
            this.changeColor(r.getFragment());
            break;
        default:
            (new Exception()).printStackTrace();
        }
    }
    
    private void changeColor(final Residue r) {
        final Structure struc = r.structure;
        final StructureMap sm = struc.getStructureMap();
        final StructureStyles ss = sm.getStructureStyles();
        
        switch(PickLevel.pickLevel) {
        case PickLevel.COMPONENTS_ATOMS_BONDS:
            final Vector atoms = r.getAtoms();
            
            final Iterator atomIt = atoms.iterator();
            while(atomIt.hasNext()) {
                final Atom a = (Atom)atomIt.next();  
                this.changeColor(a);
            }
            
            final Vector bonds = sm.getBonds(atoms);
            final Iterator bondsIt = bonds.iterator();
            while(bondsIt.hasNext()) {
            	final Bond b = (Bond)bondsIt.next();
            	this.changeColor(b);
            }
            break;
        case PickLevel.COMPONENTS_RIBBONS:
        	final Chain c = sm.getChain(r.getChainId());
            
        	final GlGeometryViewer viewer = AppBase.sgetGlGeometryViewer();
            final DisplayListRenderable renderable = ((JoglSceneNode)sm.getUData()).getRenderable(c);
            if(renderable != null) {
            	this.options.getCurrentColor().getColorComponents(ColorMutator.colorFl);
            	
            	ChainStyle style = (ChainStyle)ss.getStyle(c);
                final IResidueColor residueColor = style.getResidueColor();
                ResidueColorByRgb residueColorByRgb = null;
                if(residueColor != null && residueColor instanceof ResidueColorByRgb) {
                	residueColorByRgb = (ResidueColorByRgb)residueColor;
                } else {
                	residueColorByRgb = new ResidueColorByRgb(residueColor);
                	style = new ChainStyle();
                	style.setResidueColor(residueColorByRgb);
                	ss.setStyle(c, style);
                }
                residueColorByRgb.setColor(r, ColorMutator.colorFl);
            	
//                for(int i = 0; i < renderable.arrayLists.length; i++) {
//                	if(renderable.arrayLists[i] != null) {
//                		renderable.arrayLists[i].setEntityColor(r, colorFl);
//                	}
//                }
                
            	renderable.style = style;
//            	renderable.setDirty();
            }
            break;
        default:
            (new Exception()).printStackTrace();
        }
    }
    
    private void changeColor(final Fragment f) {
    	for(int i = 0; i < f.getResidueCount(); i++) {
            final Residue r = f.getResidue(i);
            this.changeColor(r);
        }
    }
    
    private void changeColor(final Chain c) {
    	final int resCount = c.getResidueCount();
    	for(int i = 0; i < resCount; i++) {
    		final Residue r = c.getResidue(i);
    		this.changeColor(r);
    	}
    }
    
    private void changeColor(final PdbChain c) {
    	final Iterator it = c.getResidueIterator();
    	while(it.hasNext()) {
    		final Residue r = (Residue)it.next();
    		this.changeColor(r);
    	}
    }
    
    private void changeColor(final MiscellaneousMoleculeChain c) {
    	final Iterator it = c.getIterator();
    	while(it.hasNext()) {
    		final Residue r = (Residue)it.next();
    		this.changeColor(r);
    	}
    }
    
    private void changeColor(final WaterChain c) {
    	final Iterator it = c.getIterator();
    	while(it.hasNext()) {
    		final Residue r = (Residue)it.next();
    		this.changeColor(r);
    	}
    }
    
    private void changeColor(final Structure s) {
    	final Iterator chainIt = s.getStructureMap().getChains().iterator();
		while(chainIt.hasNext()) {
			final Chain c = (Chain)chainIt.next();
			this.changeColor(c);
		}
    }
}
