package org.rcsb.pw.controllers.scene.mutators;

import java.util.Vector;

import org.rcsb.mbt.controllers.app.AppBase;
import org.rcsb.mbt.controllers.scene.PickLevel;
import org.rcsb.mbt.glscene.jogl.DisplayListGeometry;
import org.rcsb.mbt.glscene.jogl.DisplayListRenderable;
import org.rcsb.mbt.glscene.jogl.JoglSceneNode;
import org.rcsb.mbt.model.Atom;
import org.rcsb.mbt.model.Bond;
import org.rcsb.mbt.model.Chain;
import org.rcsb.mbt.model.Fragment;
import org.rcsb.mbt.model.ExternChain;
import org.rcsb.mbt.model.Residue;
import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.StructureComponent;
import org.rcsb.mbt.model.StructureComponentRegistry;
import org.rcsb.mbt.model.StructureMap;
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
		mutees.clear();
		mutees.add(mutee);
		this.doMutation();
		mutees.clear();
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
			final Object first = mutees.iterator().next();
			
			newVisibility = !this.isComponentVisible(first);
			break;
		default:
			(new Exception("Invalid option: " + PickLevel.pickLevel)).printStackTrace();
		}
		
		for (Object next : mutees)
			if(next instanceof Atom) {
				this.setVisibility((Atom)next, newVisibility);
			} else if(next instanceof Bond) {
				this.setVisibility((Bond)next, newVisibility);
			} else if(next instanceof Residue) {
				this.setVisibility((Residue)next, newVisibility);
			} else if(next instanceof Chain) {
				this.setVisibility((Chain)next, newVisibility);
			} else if(next instanceof ExternChain) {
				this.setVisibility((ExternChain)next, newVisibility);
			} else if(next instanceof Fragment) {
				this.setVisibility((Fragment)next, newVisibility);
			} else if(next instanceof Structure) {
				this.setVisibility((Structure)next, newVisibility);
			} else {
				(new Exception(next.getClass().toString())).printStackTrace();
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
			} else if(component instanceof ExternChain) {
				final ExternChain c = (ExternChain)component;
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
			}
			
			else if (component instanceof ExternChain)
			{
				final ExternChain xc = (ExternChain)component;
				final StructureStyles ss = xc.structure.getStructureMap().getStructureStyles();
				
				if (xc.isBasicChain())
				{
				// special case, since a PDB chain can consist of several MBT chains of various chemical types.
				// iterate until the first protein chain is found.
					for (Chain mbtChain : xc.getMbtChains())
						if(mbtChain.getClassification() == Residue.Classification.AMINO_ACID)
							return ss.isVisible(mbtChain.getResidue(0)) ||
								   (mbtChain.getResidueCount() > 1 && ss.isVisible(mbtChain.getResidue(1)));
				}
				
				else return ss.isVisible(xc.getResidue(0));
			}
			
			else if(component instanceof Structure) {
				final Structure s = (Structure)component;
				final StructureMap sm = s.getStructureMap();
				final StructureStyles ss = sm.getStructureStyles();
				
				for (Chain mbtChain : sm.getChains())
					if (mbtChain.getClassification() == Residue.Classification.AMINO_ACID) {
						return ss.isVisible(mbtChain.getResidue(0)) ||
							   (mbtChain.getResidueCount() > 1 && ss.isVisible(mbtChain.getResidue(1)));
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
        switch(PickLevel.pickLevel)
        {
        case PickLevel.COMPONENTS_ATOMS_BONDS:
        	this.setComponentVisibilitySimple(a, newVisibility);
        	
        	final StructureMap sm = a.structure.getStructureMap();

        	for (Bond b : sm.getBonds(a))
        	{
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
            final Vector<Atom> atoms = r.getAtoms();
            
            for (Atom a : atoms)
                this.setComponentVisibilitySimple(a, newVisibility);
            
            for (Bond b : sm.getBonds(atoms))
            	this.setComponentVisibilitySimple(b, newVisibility);

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
        	for (Fragment f : c.getFragments())
        		this.setVisibility(f, newVisibility);
            break;
        default:
            (new Exception("Invalid option: " + PickLevel.pickLevel)).printStackTrace();
        }
    }
    
    private void setVisibility(final ExternChain c, final boolean newVisibility)
    {
        switch(PickLevel.pickLevel)
        {
        case PickLevel.COMPONENTS_ATOMS_BONDS: break;
        case PickLevel.COMPONENTS_RIBBONS: if (!c.isBasicChain()) return;
        default:
            (new Exception("Invalid option: " + PickLevel.pickLevel)).printStackTrace();            
        }
        
    	for (Chain mbtChain : c.getMbtChains())
    		this.setVisibility(mbtChain, newVisibility);
    }
    
    private void setVisibility(final Fragment f, final boolean newVisibility)
    {       
        switch(PickLevel.pickLevel) {
        case PickLevel.COMPONENTS_ATOMS_BONDS:
        case PickLevel.COMPONENTS_RIBBONS:
        	for (Residue r : f.getResidues())
        		this.setVisibility(r, newVisibility);
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
            for (Chain c : sm.getChains())
                this.setVisibility(c, newVisibility);
            break;
        default:
            (new Exception("Invalid option: " + PickLevel.pickLevel)).printStackTrace();            
        }
    }
}
