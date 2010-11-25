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
