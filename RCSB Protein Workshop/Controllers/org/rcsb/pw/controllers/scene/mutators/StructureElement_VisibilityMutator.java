package org.rcsb.pw.controllers.scene.mutators;

import java.util.Iterator;
import java.util.Vector;

import org.rcsb.mbt.controllers.app.AppBase;
import org.rcsb.mbt.controllers.scene.PickLevel;
import org.rcsb.mbt.glscene.jogl.DisplayListGeometry;
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
import org.rcsb.mbt.model.attributes.StructureStyles;
import org.rcsb.mbt.model.attributes.Style;
import org.rcsb.pw.controllers.app.ProteinWorkshop;
import org.rcsb.pw.controllers.scene.mutators.options.StructureElement_VisibilityOptions;




public class StructureElement_VisibilityMutator extends Mutator {
	private StructureElement_VisibilityOptions options = null; 
	
	public StructureElement_VisibilityMutator() {
		super();
		this.options = new StructureElement_VisibilityOptions();
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
		boolean newVisibility = false;
		switch(this.options.getVisibility()) {
		case StructureElement_VisibilityOptions.VISIBILITY_INVISIBLE:
			newVisibility = false;
			break;
		case StructureElement_VisibilityOptions.VISIBILITY_VISIBLE:
			newVisibility = true;
			break;
		case StructureElement_VisibilityOptions.VISIBILITY_TOGGLE:
			// be consistant - toggle based on the first selection.
			final Object first = Mutator.mutees.keySet().iterator().next();
			
			newVisibility = !this.isComponentVisible(first);
			break;
		default:
			(new Exception("Invalid option: " + PickLevel.pickLevel)).printStackTrace();
		}
		
		final Iterator it = Mutator.mutees.keySet().iterator();
		while(it.hasNext()) {
			final Object next = it.next();
			if(next instanceof Atom) {
				this.setVisibility((Atom)next, newVisibility);
			} else if(next instanceof Bond) {
				this.setVisibility((Bond)next, newVisibility);
			} else if(next instanceof Residue) {
				this.setVisibility((Residue)next, newVisibility);
			} else if(next instanceof Chain) {
				this.setVisibility((Chain)next, newVisibility);
			} else if(next instanceof PdbChain) {
				this.setVisibility((PdbChain)next, newVisibility);
			} else if(next instanceof WaterChain) {
				this.setVisibility((WaterChain)next, newVisibility);
			} else if(next instanceof MiscellaneousMoleculeChain) {
				this.setVisibility((MiscellaneousMoleculeChain)next, newVisibility);
			} else if(next instanceof Fragment) {
				this.setVisibility((Fragment)next, newVisibility);
			} else if(next instanceof Structure) {
				this.setVisibility((Structure)next, newVisibility);
			} else {
				(new Exception(next.getClass().toString())).printStackTrace();
			}
		}
		
		// may have affected the tree. Repaint.
		ProteinWorkshop.sgetActiveFrame().getTreeViewer().getTree().repaint();
	}
	
	public boolean isComponentVisible(final Object component) {
		switch(PickLevel.pickLevel) {
		case PickLevel.COMPONENTS_ATOMS_BONDS:
			if(component instanceof Atom || component instanceof Bond) {
				final StructureComponent sc = (StructureComponent)component;
				final StructureStyles ss = sc.structure.getStructureMap().getStructureStyles();
				return ss.isVisible(sc);
			} else if(component instanceof Residue) {
				final Residue r = (Residue)component;
				final StructureStyles ss = r.structure.getStructureMap().getStructureStyles();
				return ss.isVisible(r.getAtom(0));
			} else if(component instanceof Chain) {
				final Chain c = (Chain)component;
				final StructureStyles ss = c.structure.getStructureMap().getStructureStyles();
				return ss.isVisible(c.getResidue(0).getAtom(0));
			} else if(component instanceof Fragment) {
				final Fragment f = (Fragment)component;
				final StructureStyles ss = f.structure.getStructureMap().getStructureStyles();
				return ss.isVisible(f.getResidue(0).getAtom(0));
			} else if(component instanceof PdbChain) {
				final PdbChain c = (PdbChain)component;
				final StructureStyles ss = c.structure.getStructureMap().getStructureStyles();
				return ss.isVisible(c.getResidue(0).getAtom(0));
			} else if(component instanceof WaterChain) {
				final WaterChain c = (WaterChain)component;
				final StructureStyles ss = c.structure.getStructureMap().getStructureStyles();
				return ss.isVisible(c.getResidue(0).getAtom(0));
			} else if(component instanceof MiscellaneousMoleculeChain) {
				final MiscellaneousMoleculeChain c = (MiscellaneousMoleculeChain)component;
				final StructureStyles ss = c.structure.getStructureMap().getStructureStyles();
				return ss.isVisible(c.getResidue(0).getAtom(0));
			} else if(component instanceof Structure) {
				final Structure s = (Structure)component;
				final StructureStyles ss = s.getStructureMap().getStructureStyles();
				return ss.isVisible(s.getStructureMap().getAtom(0));
			}
			//break;
		case PickLevel.COMPONENTS_RIBBONS:
			if(component instanceof Atom) {
				final Atom a = (Atom)component;
				final StructureMap sm = a.structure.getStructureMap();
				final StructureStyles ss = sm.getStructureStyles();
				return ss.isVisible(sm.getResidue(a));
			} else if(component instanceof Bond) {
				final Atom a = ((Bond)component).getAtom(0);
				final StructureMap sm = a.structure.getStructureMap();
				final StructureStyles ss = sm.getStructureStyles();
				return ss.isVisible(sm.getResidue(a));
			} else if(component instanceof Residue) {
				final Residue r = (Residue)component;
				final StructureStyles ss = r.structure.getStructureMap().getStructureStyles();
				return ss.isVisible(r);
			} else if(component instanceof Chain) {
				final Chain c = (Chain)component;
				final StructureStyles ss = c.structure.getStructureMap().getStructureStyles();
				return ss.isVisible(c.getResidue(0));
			} else if(component instanceof Fragment) {
				final Fragment f = (Fragment)component;
				final StructureStyles ss = f.structure.getStructureMap().getStructureStyles();
				return ss.isVisible(f.getResidue(0));
			} else if(component instanceof PdbChain) {
				final PdbChain pdbChain = (PdbChain)component;
				final StructureStyles ss = pdbChain.structure.getStructureMap().getStructureStyles();
				
				// special case, since a PDB chain can consist of several MBT chains of various chemical types.
				// iterate until the first protein chain is found.
				final Iterator chainIt = pdbChain.getMbtChainIterator();
				while(chainIt.hasNext()) {
					final Chain mbtChain = (Chain)chainIt.next();
					
					if(mbtChain.getClassification() == Residue.Classification.AMINO_ACID) {
						return ss.isVisible(mbtChain.getResidue(0)) || (mbtChain.getResidueCount() > 1 && ss.isVisible(mbtChain.getResidue(1)));
					}
				}
			} else if(component instanceof WaterChain) {
				final WaterChain c = (WaterChain)component;
				final StructureStyles ss = c.structure.getStructureMap().getStructureStyles();
				return ss.isVisible(c.getResidue(0));
			} else if(component instanceof MiscellaneousMoleculeChain) {
				final MiscellaneousMoleculeChain c = (MiscellaneousMoleculeChain)component;
				final StructureStyles ss = c.structure.getStructureMap().getStructureStyles();
				return ss.isVisible(c.getResidue(0));
			} else if(component instanceof Structure) {
				final Structure s = (Structure)component;
				final StructureMap sm = s.getStructureMap();
				final StructureStyles ss = sm.getStructureStyles();
				
				// special case, since a PDB chain can consist of several MBT chains of various chemical types.
				// iterate until the first protein chain is found.
				final Iterator chainIt = sm.getChains().iterator();
				while(chainIt.hasNext()) {
					final Chain mbtChain = (Chain)chainIt.next();
					
					if(mbtChain.getClassification() == Residue.Classification.AMINO_ACID) {
						return ss.isVisible(mbtChain.getResidue(0)) || (mbtChain.getResidueCount() > 1 && ss.isVisible(mbtChain.getResidue(1)));
					}
				}
			}
			//break;
		default:
			(new Exception(component.getClass().toString())).printStackTrace();
		}
		
		return false;
	}

	public StructureElement_VisibilityOptions getOptions() {
		return this.options;
	}	
	
	// use this only with atoms and bonds
	private void setComponentVisibilitySimple(final StructureComponent sc, final boolean newVisibility) {
		final Structure s = sc.structure;
		final StructureMap sm = s.getStructureMap();
		final StructureStyles ss = sm.getStructureStyles();
		
		if(sc == null) {
			return;
		}
		
		// for backbones, make sure this is an amino acid chain...
		if(sc.getStructureComponentType() == StructureComponentRegistry.TYPE_CHAIN) {
			final Chain c = (Chain)sc;
			if(c.getClassification() != Residue.Classification.AMINO_ACID) {
				return;
			}
		}
		
		// make sure we're not trying to do something that's already been done.
		if(ss.isVisible(sc) == newVisibility) {
			return;
		}
		
		ss.setVisible(sc, newVisibility);
		
		if(newVisibility) {
			final String scType = sc.getStructureComponentType();
			final DisplayListGeometry geometry =
				(DisplayListGeometry) AppBase.sgetSceneController().getDefaultGeometry().get( scType );
			
			
			Style style = ss.getStyle( sc );
			if(style == null) {
				style = ss.getDefaultStyle(sc.getStructureComponentType());
			}
			
			((JoglSceneNode)sm.getUData()).addRenderable(new DisplayListRenderable( sc, style, geometry ));
		}
		
		else
			((JoglSceneNode)sm.getUData()).removeRenderable(sc);
	}
	
    private void setVisibility(final Atom a, final boolean newVisibility)
    {
        final GlGeometryViewer glViewer = AppBase.sgetGlGeometryViewer();
        
        switch(PickLevel.pickLevel)
        {
        case PickLevel.COMPONENTS_ATOMS_BONDS:
        	this.setComponentVisibilitySimple(a, newVisibility);
        	
        	final StructureMap sm = a.structure.getStructureMap();
        	final Vector bonds = sm.getBonds(a);
        	final int bondSize = bonds.size();
        	for(int i = 0; i < bondSize; i++) {
        		final Bond b = (Bond)bonds.get(i);
        		
        		// change the bond visibility based on the new atom visibility
        		boolean atom0IsVisible = ((JoglSceneNode)sm.getUData()).getRenderable(b.getAtom(0)) != null;
        		boolean atom1IsVisible = ((JoglSceneNode)sm.getUData()).getRenderable(b.getAtom(1)) != null;
        		if(atom0IsVisible && atom1IsVisible) {
        			this.setComponentVisibilitySimple(b, true);
        		} else if(!atom0IsVisible || !atom1IsVisible) {
        			this.setComponentVisibilitySimple(b, false);
        		}
        	}
        	
            break;
        case PickLevel.COMPONENTS_RIBBONS:
            final Residue r = a.structure.getStructureMap().getResidue(a);
            if(r != null) {
            	this.setVisibility(r, newVisibility);
            }
            break;
        default:
            (new Exception()).printStackTrace();
        }
    }

    private void setVisibility(final Bond b, final boolean newVisibility) {
        final StructureMap sm = b.structure.getStructureMap();
    	
        switch(PickLevel.pickLevel) {
        case PickLevel.COMPONENTS_ATOMS_BONDS:
        	// delegate to the atoms...
        	this.setVisibility(b.getAtom(0), newVisibility);
        	this.setVisibility(b.getAtom(1), newVisibility);
        	
        	/*this.setComponentVisibilitySimple(b, newVisibility);
            
            // set atom visibility based on the surrounding bonds.
            if(newVisibility) {
            	this.setComponentVisibilitySimple(b.getAtom(0), newVisibility);
            	this.setComponentVisibilitySimple(b.getAtom(1), newVisibility);
            } else {
	            Vector bonds = sm.getBonds(b.getAtom(0));
	            int bondsSize = bonds.size();
	            boolean aBondIsVisible = false;
	            for(int i = 0; i < bondsSize; i++) {
	            	Bond b_ = (Bond)bonds.get(i);
	            	if(viewer.getRenderable(b_) != null) {
	            		aBondIsVisible = true;
	            		break;
	            	}
	            }
	            if(!aBondIsVisible) {
	            	this.setComponentVisibilitySimple(b.getAtom(0), newVisibility);
	            }
	            
	            bonds = sm.getBonds(b.getAtom(1));
	            bondsSize = bonds.size();
	            aBondIsVisible = false;
	            for(int i = 0; i < bondsSize; i++) {
	            	Bond b_ = (Bond)bonds.get(i);
	            	if(viewer.getRenderable(b_) != null) {
	            		aBondIsVisible = true;
	            		break;
	            	}
	            }
	            if(!aBondIsVisible) {
	            	this.setComponentVisibilitySimple(b.getAtom(1), newVisibility);
	            }
            }*/
            break;
        case PickLevel.COMPONENTS_RIBBONS:
            final Residue r = b.structure.getStructureMap().getResidue(b.getAtom(0));
            if(r != null) {
            	this.setVisibility(r, newVisibility);
            }
            break;
        default:
            (new Exception()).printStackTrace();
        }
    }
    
    private void setVisibility(final Residue r, final boolean newVisibility) {

        final StructureMap sm = r.structure.getStructureMap();
        
        // connecting bonds will be seen twice. Bonds to non-visible residues will only be seen once. Add only connecting bonds.
        /*
         *  Data structure:
         *      HashMap bonds {
         *          key: Bond bond
         *          value: Integer countOccurances
         *      }
         */
        switch(PickLevel.pickLevel) {
        case PickLevel.COMPONENTS_ATOMS_BONDS:
            final Vector atoms = r.getAtoms();
        	final Iterator atomIt = atoms.iterator();
            while(atomIt.hasNext()) {
                final Atom a = (Atom)atomIt.next();
                this.setComponentVisibilitySimple(a, newVisibility);
            }
            
            final Iterator bondsIt = sm.getBonds(atoms).iterator();
            while(bondsIt.hasNext()) {
            	final Bond b = (Bond)bondsIt.next();
            	this.setComponentVisibilitySimple(b, newVisibility);
            }
            break;
        case PickLevel.COMPONENTS_RIBBONS:
            sm.getStructureStyles().setVisible(r, newVisibility);
            break;
        default:
            (new Exception("Invalid option: " + PickLevel.pickLevel)).printStackTrace();
        }
    }
    
    private void setVisibility(final Chain c, final boolean newVisibility) {
		switch(PickLevel.pickLevel) {
        case PickLevel.COMPONENTS_ATOMS_BONDS:
        case PickLevel.COMPONENTS_RIBBONS:
        	final Iterator fragments = c.getFragments().iterator();
        	while(fragments.hasNext()) {
        		final Fragment f = (Fragment)fragments.next();
        		this.setVisibility(f, newVisibility);
        	}
            break;
        default:
            (new Exception("Invalid option: " + PickLevel.pickLevel)).printStackTrace();
        }
    }
    
    private void setVisibility(final PdbChain c, final boolean newVisibility) {
        switch(PickLevel.pickLevel) {
        case PickLevel.COMPONENTS_ATOMS_BONDS:
        case PickLevel.COMPONENTS_RIBBONS:
        	final Iterator it = c.getMbtChainIterator();
        	while(it.hasNext()) {
        		final Chain mbtChain = (Chain)it.next();
        		this.setVisibility(mbtChain, newVisibility);
        	}
            break;
        default:
            (new Exception("Invalid option: " + PickLevel.pickLevel)).printStackTrace();            
        }
    }
    
    private void setVisibility(final WaterChain c, final boolean newVisibility) {
        switch(PickLevel.pickLevel) {
        case PickLevel.COMPONENTS_ATOMS_BONDS:
        	final Iterator chainIt = c.getMbtChainIterator();
            while(chainIt.hasNext()) {
                final Chain mbtChain = (Chain)chainIt.next();
                this.setVisibility(mbtChain, newVisibility);
            }
            break;
        case PickLevel.COMPONENTS_RIBBONS:
        	// no backbone to show...
        	break;
        default:
            (new Exception("Invalid option: " + PickLevel.pickLevel)).printStackTrace();            
        }
    }
    
    private void setVisibility(final MiscellaneousMoleculeChain c, final boolean newVisibility) {
         switch(PickLevel.pickLevel) {
        case PickLevel.COMPONENTS_ATOMS_BONDS:
        	final Iterator chainIt = c.getMbtChainIterator();
            while(chainIt.hasNext()) {
                final Chain mbtChain = (Chain)chainIt.next();
                this.setVisibility(mbtChain, newVisibility);
            }
            break;
        case PickLevel.COMPONENTS_RIBBONS:
        	// no backbone to show...
        	break;
        default:
            (new Exception("Invalid option: " + PickLevel.pickLevel)).printStackTrace();            
        }
    }
    
    private void setVisibility(final Fragment f, final boolean newVisibility) {
        final StructureMap sm = f.structure.getStructureMap();
        final StructureStyles ss = sm.getStructureStyles();
        
        switch(PickLevel.pickLevel) {
        case PickLevel.COMPONENTS_ATOMS_BONDS:
        case PickLevel.COMPONENTS_RIBBONS:
        	final Iterator it = f.getResidues().iterator();
        	while(it.hasNext()) {
        		final Residue r = (Residue)it.next();
        		this.setVisibility(r, newVisibility);
        	}
            break;
        default:
            (new Exception("Invalid option: " + PickLevel.pickLevel)).printStackTrace();            
        }
    }
    
    private void setVisibility(final Structure s, final boolean newVisibility) {
        final StructureMap sm = s.getStructureMap();
        
        switch(PickLevel.pickLevel) {
        case PickLevel.COMPONENTS_ATOMS_BONDS:
        case PickLevel.COMPONENTS_RIBBONS:
        	final Iterator chainIt = sm.getChains().iterator();
        	while(chainIt.hasNext()) {
                final Chain c = (Chain)chainIt.next();
                this.setVisibility(c, newVisibility);
            }
            break;
        default:
            (new Exception("Invalid option: " + PickLevel.pickLevel)).printStackTrace();            
        }
    }
}
