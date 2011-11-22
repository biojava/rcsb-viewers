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
import org.rcsb.mbt.model.attributes.StructureStyles;
import org.rcsb.uiApp.controllers.app.AppBase;
import org.rcsb.vf.controllers.scene.mutators.MutatorBase;


public class SelectionMutator extends MutatorBase {
//	SelectionOptions options = null;
	
	public SelectionMutator() {
		super();
//		this.options = new SelectionOptions(m);
	}
	
	
	public boolean supportsBatchMode() {
		return false;
	}
	
	private Object previouslySelectedItem = null;
	
	
	public void doMutationSingle(final Object mutee)
	{
		// if no modifier keys are down, clear all selections first
		if (this.shouldConsiderClickModifiers() && !this.isShiftDown() && !this.isCtrlDown())
			for(Structure structure : AppBase.sgetModel().getStructures())
				structure.getStructureMap().getStructureStyles().clearSelections();
		
		// This is a batch mode Mutator. use toggleMutee() and then doMutation().
		if(mutee instanceof Atom) {
			this.changeSelection((Atom)mutee);
		} else if(mutee instanceof Bond) {
			this.changeSelection((Bond)mutee);
		} else if(mutee instanceof Residue) {
			this.changeSelection((Residue)mutee);
		} else if(mutee instanceof Chain) {
			this.changeSelection((Chain)mutee);
		} else if(mutee instanceof ExternChain) {
			this.changeSelection((ExternChain)mutee);
		} else if(mutee instanceof Fragment) {
			this.changeSelection((Fragment)mutee);
		} else if(mutee instanceof Structure) {
			this.changeSelection((Structure)mutee);
		}
		
		this.previouslySelectedItem = mutee;
	}
	
	
	public void doMutation() {
		for (Object next : mutees)
			this.doMutationSingle(next);
	}

//	public LabelsOptions getOptions() {
//		return this.options;
//	}

	public void changeSelection(final Atom a) {
		final StructureMap sm = a.structure.getStructureMap();
		final StructureStyles ss = sm.getStructureStyles();
		if(this.shouldConsiderClickModifiers()) {
			if(this.isCtrlDown()) {
				ss.setSelected(a, !ss.isSelected(a));
			} else if(this.isShiftDown()) {
				if(this.previouslySelectedItem instanceof Atom) {
					final Atom previous = (Atom)this.previouslySelectedItem;
					final int curIndex = sm.getAtomIndex(a);
					final int prevIndex = sm.getAtomIndex(previous);
					int start = -1, end = -1;
					if(curIndex < prevIndex) {
						start = curIndex;
						end = prevIndex;
					} else {
						start = prevIndex;
						end = curIndex;
					}
					for(int i = start; i <= end; i++) {
						ss.setSelected(sm.getAtom(i), true);						
					}
				} else {	// act as if nothing is down...
					ss.setSelected(a, true);
				}
			} else {	// if nothing down...
				ss.setSelected(a, true);
			}
		} else if(this.shouldConsiderSelectedFlag()) {
			ss.setSelected(a, this.isSelected());
		} else {
			ss.setSelected(a, !ss.isSelected(a));
		}
	}
	
	public void changeSelection(final Bond b) {
		final StructureMap sm = b.structure.getStructureMap();
		final StructureStyles ss = sm.getStructureStyles();
		if(this.shouldConsiderClickModifiers()) {
			if(this.isCtrlDown()) {
				ss.setSelected(b, !ss.isSelected(b));
			} else if(this.isShiftDown()) {
				if(this.previouslySelectedItem instanceof Bond) {
					final Bond previous = (Bond)this.previouslySelectedItem;
					
					final Atom b1a1 = b.getAtom(0);
					final Atom b1a2 = b.getAtom(1);
					final Atom b2a1 = previous.getAtom(0);
					final Atom b2a2 = previous.getAtom(1);
					
					final int b1a1Index = sm.getAtomIndex(b1a1);
					final int b1a2Index = sm.getAtomIndex(b1a2);
					final int b2a1Index = sm.getAtomIndex(b2a1);
					final int b2a2Index = sm.getAtomIndex(b2a2);
					
					final int start = Math.min(b1a1Index, Math.min(b1a2Index, Math.min(b2a1Index, b2a2Index)));
					final int end = Math.max(b1a1Index, Math.max(b1a2Index, Math.max(b2a1Index, b2a2Index)));
					
					for(int i = start; i <= end; i++) {
						ss.setSelected(sm.getAtom(i), true);						
					}
				} else {	// act as if nothing is down...
					ss.setSelected(b, true);
				}
			} else {	// if nothing down...
				ss.setSelected(b, true);
			}
		} else if(this.shouldConsiderSelectedFlag()) {
			ss.setSelected(b, this.isSelected());
		} else {
			ss.setSelected(b, !ss.isSelected(b));
		}
	}
	
	public void changeSelection(final Residue r) {
		final StructureMap sm = r.structure.getStructureMap();
		final StructureStyles ss = sm.getStructureStyles();
		if(this.shouldConsiderClickModifiers()) {
			if(this.isCtrlDown()) {
				ss.setSelected(r, !ss.isSelected(r));
			} else if(this.isShiftDown()) {
				if(this.previouslySelectedItem instanceof Residue) {
					final Residue previous = (Residue)this.previouslySelectedItem;
					final int curIndex = sm.getResidueIndex(r);
					final int prevIndex = sm.getResidueIndex(previous);
					int start = -1, end = -1;
					if(curIndex < prevIndex) {
						start = curIndex;
						end = prevIndex;
					} else {
						start = prevIndex;
						end = curIndex;
					}
					for(int i = start; i <= end; i++) {
						ss.setSelected(sm.getResidue(i), true);						
					}
				} else {	// act as if nothing is down...
					ss.setSelected(r, true);
				}
			} else {	// if nothing down...
				ss.setSelected(r, true);
			}
		} else if(this.shouldConsiderSelectedFlag()) {
			ss.setSelected(r, this.isSelected());
		} else {
			ss.setSelected(r, !ss.isSelected(r));
		}
	}
	
	public void changeSelection(final Chain c) {
		final StructureMap sm = c.structure.getStructureMap();
		final StructureStyles ss = sm.getStructureStyles();
		if(this.shouldConsiderClickModifiers()) {
			if(this.isCtrlDown()) {
				ss.setSelected(c, !ss.isSelected(c));
			} else if(this.isShiftDown()) {
				if(this.previouslySelectedItem instanceof Chain) {
					final Chain previous = (Chain)this.previouslySelectedItem;
					final int curIndex = sm.getChainIndex(c);
					final int prevIndex = sm.getChainIndex(previous);
					int start = -1, end = -1;
					if(curIndex < prevIndex) {
						start = curIndex;
						end = prevIndex;
					} else {
						start = prevIndex;
						end = curIndex;
					}
					for(int i = start; i <= end; i++) {
						ss.setSelected(sm.getChain(i), true);						
					}
				} else {	// act as if nothing is down...
					ss.setSelected(c, true);
				}
			} else {	// if nothing down...
				ss.setSelected(c, true);
			}
		} else if(this.shouldConsiderSelectedFlag()) {
			ss.setSelected(c, this.isSelected());
		} else {
			ss.setSelected(c, !ss.isSelected(c));
		}
	}
	
	public void changeSelection(final ExternChain c) {
		for (Chain mbtChain : c.getMbtChains())
			this.changeSelection(mbtChain);
	}
	
	public void changeSelection(final Fragment f) {
		final StructureMap sm = f.structure.getStructureMap();
		final StructureStyles ss = sm.getStructureStyles();
		if(this.shouldConsiderClickModifiers()) {
			if(this.isCtrlDown()) {
				ss.setSelected(f, !ss.isSelected(f));
			} else if(this.isShiftDown()) {
				if(this.previouslySelectedItem instanceof Fragment) {
					final Fragment previous = (Fragment)this.previouslySelectedItem;
					final int curIndex = sm.getFragmentIndex(f);
					final int prevIndex = sm.getFragmentIndex(previous);
					int start = -1, end = -1;
					if(curIndex < prevIndex) {
						start = curIndex;
						end = prevIndex;
					} else {
						start = prevIndex;
						end = curIndex;
					}
					for(int i = start; i <= end; i++) {
						ss.setSelected(sm.getFragment(i), true);						
					}
				} else {	// act as if nothing is down...
					ss.setSelected(f, true);
				}
			} else {	// if nothing down...
				ss.setSelected(f, true);
			}
		} else if(this.shouldConsiderSelectedFlag()) {
			ss.setSelected(f, this.isSelected());
		} else {
			ss.setSelected(f, !ss.isSelected(f));
		}
	}
	
	public void changeSelection(final Structure s) {
		for (Chain c : s.getStructureMap().getChains())
			this.changeSelection(c);
	}
}
