package org.rcsb.pw.controllers.scene.mutators;

import java.util.Iterator;

import org.rcsb.mbt.controllers.app.AppBase;
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
import org.rcsb.mbt.model.attributes.StructureStyles;


public class SelectionMutator extends Mutator {
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
		} else if(mutee instanceof PdbChain) {
			this.changeSelection((PdbChain)mutee);
		} else if(mutee instanceof WaterChain) {
			this.changeSelection((WaterChain)mutee);
		} else if(mutee instanceof MiscellaneousMoleculeChain) {
			this.changeSelection((MiscellaneousMoleculeChain)mutee);
		} else if(mutee instanceof Fragment) {
			this.changeSelection((Fragment)mutee);
		} else if(mutee instanceof Structure) {
			this.changeSelection((Structure)mutee);
		}
		
		this.previouslySelectedItem = mutee;
	}
	
	
	public void doMutation() {
		final Iterator it = Mutator.mutees.keySet().iterator();
		while(it.hasNext()) {
			final Object next = it.next();
			this.doMutationSingle(next);
		}
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
	
	public void changeSelection(final PdbChain c) {
		final Iterator it = c.getMbtChainIterator();
		while(it.hasNext()) {
			final Chain mbtChain = (Chain)it.next();
			this.changeSelection(mbtChain);
		}
	}
	
	public void changeSelection(final WaterChain c) {
		final Iterator it = c.getMbtChainIterator();
		while(it.hasNext()) {
			final Chain mbtChain = (Chain)it.next();
			this.changeSelection(mbtChain);
		}
	}
	
	public void changeSelection(final MiscellaneousMoleculeChain c) {
		final Iterator it = c.getMbtChainIterator();
		while(it.hasNext()) {
			final Chain mbtChain = (Chain)it.next();
			this.changeSelection(mbtChain);
		}
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
		final StructureMap sm = s.getStructureMap();
		
		final Iterator it = sm.getChains().iterator();
		while(it.hasNext()) {
			final Chain c = (Chain)it.next();
			this.changeSelection(c);
		}
	}
}
