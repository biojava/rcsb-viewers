package org.rcsb.pw.controllers.scene.mutators;

import org.rcsb.vf.controllers.app.VFAppBase;
import org.rcsb.mbt.model.Atom;
import org.rcsb.mbt.model.Bond;
import org.rcsb.mbt.model.Chain;
import org.rcsb.mbt.model.ExternChain;
import org.rcsb.mbt.model.Fragment;
import org.rcsb.mbt.model.Residue;
import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.StructureMap;
import org.rcsb.pw.controllers.scene.mutators.options.ReCenterOptions;
import org.rcsb.vf.controllers.scene.mutators.MutatorBase;



public class ReCenterMutator extends MutatorBase
{
	private ReCenterOptions options = null; 
	
	public ReCenterMutator() {
		super();
		this.options = new ReCenterOptions();
	}

	
	public boolean supportsBatchMode() {
		return false;
	}
	
	
	public void doMutationSingle(final Object mutee) {
		if(mutee instanceof Atom) {
			this.changeCenter((Atom)mutee);
		} else if(mutee instanceof Bond) {
			this.changeCenter((Bond)mutee);
		} else if(mutee instanceof Residue) {
			this.changeCenter((Residue)mutee);
		} else if(mutee instanceof Chain) {
			this.changeCenter((Chain)mutee);
		} else if(mutee instanceof ExternChain) {
			this.changeCenter((ExternChain)mutee);
		} else if(mutee instanceof Fragment) {
			this.changeCenter((Fragment)mutee);
		} else if(mutee instanceof Structure) {
			final Structure s = (Structure)mutee;
			this.changeCenter(s);
		}
	}
	
	
	public void doMutation() {
		for (Object next : mutees)
			this.doMutationSingle(next);
	}
	
	public ReCenterOptions getOptions() {
		return this.options;
	}	
	
	private void changeCenter(final Atom a) {
    	VFAppBase.sgetGlGeometryViewer().lookAt(a.coordinate);
    }
    
    private void changeCenter(final Bond b) {
    	final Atom atom = b.getAtom(0);
        this.changeCenter(atom);
    }
    
    private void changeCenter(final Residue r) {
        Atom a = r.getAlphaAtom();
        if(a == null) {
        	a = r.getAtom(r.getAtomCount() / 2);
        }
        this.changeCenter(a);
    }
    
    private void changeCenter(final Fragment f) {
    	this.changeCenter(f.getResidue(f.getResidueCount() / 2));
    }
    
    private void changeCenter(final Chain c) {
    	this.changeCenter(c.getFragment(c.getFragmentCount() / 2));
    }
    
    private void changeCenter(final ExternChain c) {
    	this.changeCenter(c.getResidue(c.getResidueCount() / 2));
    }
    
    private void changeCenter(final Structure s) {
    	final StructureMap sm = s.getStructureMap();
    	this.changeCenter(sm.getChain(sm.getChainCount() / 2));
    }
}
