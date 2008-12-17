package org.rcsb.pw.controllers.scene.mutators;

import java.util.Vector;

import org.rcsb.vf.controllers.app.VFAppBase;
import org.rcsb.mbt.model.Atom;
import org.rcsb.mbt.model.Bond;
import org.rcsb.mbt.model.Chain;
import org.rcsb.mbt.model.ExternChain;
import org.rcsb.mbt.model.Fragment;
import org.rcsb.mbt.model.Residue;
import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.StructureComponent;
import org.rcsb.mbt.model.StructureMap;
import org.rcsb.mbt.model.StructureComponentRegistry.ComponentType;
import org.rcsb.mbt.model.attributes.StructureStyles;
import org.rcsb.mbt.model.attributes.Style;
import org.rcsb.pw.controllers.app.ProteinWorkshop;
import org.rcsb.vf.controllers.scene.mutators.MutatorBase;
import org.rcsb.vf.glscene.jogl.DisplayListGeometry;
import org.rcsb.vf.glscene.jogl.DisplayListRenderable;
import org.rcsb.vf.glscene.jogl.JoglSceneNode;




public class StructureElement_VisibilityMutator extends MutatorBase {
	private StructureElement_VisibilityOptions options = null; 
	
	public StructureElement_VisibilityMutator() {
		super();
		this.options = new StructureElement_VisibilityOptions();
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
		boolean newVisibility = false;
		switch(options.getVisibility())
		{
		case INVISIBLE:
			newVisibility = false;
			break;
		case VISIBLE:
			newVisibility = true;
			break;
		case TOGGLE:
			// be consistant - toggle based on the first selection.
			final Object first = mutees.iterator().next();
			
			newVisibility = !this.isComponentVisible(first);
			break;
		default:
			(new Exception("Invalid option: " + this.options.getVisibility())).printStackTrace();
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
		ActivationType mutatorActivationType = MutatorBase.getActivationType();
        switch(mutatorActivationType)
        {
		case ATOMS_AND_BONDS:
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
		case RIBBONS:
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
            (new Exception("Invalid option: " + mutatorActivationType)).printStackTrace();  
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
		if(sc.getStructureComponentType() == ComponentType.CHAIN) {
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
			final ComponentType scType = sc.getStructureComponentType();
			final DisplayListGeometry geometry =
				VFAppBase.sgetSceneController().getDefaultGeometry().get( scType );
			
			
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
		ActivationType mutatorActivationType = MutatorBase.getActivationType();
        switch(mutatorActivationType)
        {
        case ATOMS_AND_BONDS:
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
        case RIBBONS:
            final Residue r = a.structure.getStructureMap().getResidue(a);
            if(r != null) {
            	this.setVisibility(r, newVisibility);
            }
            break;
        default:
            (new Exception("Invalid option: " + mutatorActivationType)).printStackTrace();          }
    }

    private void setVisibility(final Bond b, final boolean newVisibility) {
		ActivationType mutatorActivationType = MutatorBase.getActivationType();
        switch(mutatorActivationType)
        {
        case ATOMS_AND_BONDS:
        	// delegate to the atoms...
        	this.setVisibility(b.getAtom(0), newVisibility);
        	this.setVisibility(b.getAtom(1), newVisibility);
            break;
            
        case RIBBONS:
            final Residue r = b.structure.getStructureMap().getResidue(b.getAtom(0));
            if(r != null) {
            	this.setVisibility(r, newVisibility);
            }
            break;
        default:
            (new Exception("Invalid option: " + mutatorActivationType)).printStackTrace();          }
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
		ActivationType mutatorActivationType = MutatorBase.getActivationType();
        switch(mutatorActivationType)
        {
        case ATOMS_AND_BONDS:
            final Vector<Atom> atoms = r.getAtoms();
            
            for (Atom a : atoms)
                this.setComponentVisibilitySimple(a, newVisibility);
            
            for (Bond b : sm.getBonds(atoms))
            	this.setComponentVisibilitySimple(b, newVisibility);

            break;
            
        case RIBBONS:
            sm.getStructureStyles().setVisible(r, newVisibility);
            break;
            
        default:
            (new Exception("Invalid option: " + mutatorActivationType)).printStackTrace();
        }
    }
    
    private void setVisibility(final Chain c, final boolean newVisibility) {
		ActivationType mutatorActivationType = MutatorBase.getActivationType();
        switch(mutatorActivationType)
        {
        case ATOMS_AND_BONDS:
        case RIBBONS:
        	for (Fragment f : c.getFragments())
        		this.setVisibility(f, newVisibility);
            break;
        default:
            (new Exception("Invalid option: " + mutatorActivationType)).printStackTrace();
        }
    }
    
    private void setVisibility(final ExternChain c, final boolean newVisibility)
    {
		ActivationType mutatorActivationType = MutatorBase.getActivationType();
        switch(mutatorActivationType)
        {
        case ATOMS_AND_BONDS: break;
        case RIBBONS: if (!c.isBasicChain()) return; break;
        default:
            (new Exception("Invalid option: " + mutatorActivationType)).printStackTrace();            
        }
        
    	for (Chain mbtChain : c.getMbtChains())
    		this.setVisibility(mbtChain, newVisibility);
    }
    
    private void setVisibility(final Fragment f, final boolean newVisibility)
    {       
		ActivationType mutatorActivationType = MutatorBase.getActivationType();
        switch(mutatorActivationType)
        {
        case ATOMS_AND_BONDS:
        case RIBBONS:
        	for (Residue r : f.getResidues())
        		this.setVisibility(r, newVisibility);
            break;
        default:
            (new Exception("Invalid option: " + mutatorActivationType)).printStackTrace();            
        }
    }
    
    private void setVisibility(final Structure s, final boolean newVisibility) {
        final StructureMap sm = s.getStructureMap();
        
		ActivationType mutatorActivationType = MutatorBase.getActivationType();
        switch(mutatorActivationType)
        {
        case ATOMS_AND_BONDS:
        case RIBBONS:
            for (Chain c : sm.getChains())
                this.setVisibility(c, newVisibility);
            break;
        default:
            (new Exception("Invalid option: " + mutatorActivationType)).printStackTrace();            
        }
    }
}
