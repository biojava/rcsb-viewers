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
package org.rcsb.vf.controllers.scene.mutators;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.rcsb.mbt.model.Atom;
import org.rcsb.mbt.model.Bond;
import org.rcsb.mbt.model.Chain;
import org.rcsb.mbt.model.ExternChain;
import org.rcsb.mbt.model.Fragment;
import org.rcsb.mbt.model.Residue;
import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.StructureComponent;
import org.rcsb.mbt.model.StructureMap;
import org.rcsb.mbt.model.Surface;
import org.rcsb.mbt.model.attributes.StructureStyles;
import org.rcsb.uiApp.controllers.app.AppBase;
import org.rcsb.uiApp.controllers.update.IUpdateListener;
import org.rcsb.uiApp.controllers.update.UpdateEvent;
import org.rcsb.vf.controllers.app.VFAppBase;
import org.rcsb.vf.glscene.jogl.JoglSceneNode;


// class which all mutators extend
/* Contract of subclasses of this class that I don't want to explicitly specifiy via code:
 * 		Objects of this hierarchy operate on the Structure based on options and mutee(s).
 * 		Contains an object from the mutators.options package for holding any options associated with this class.
 * 		This hierachy can only operate on StructureComponent elements added via the addMutee() function.
 * 		This hierachy is not aware of the immediate/batch mode status. 
 * 
 */
public abstract class MutatorBase implements IUpdateListener
{
	private boolean isShiftDown = false;
	private boolean isCtrlDown = false;
	private boolean considerClickModifiers = false;
	
	private boolean considerSelectedFlag = false;
	private boolean isSelected = false;

	public static Set<Object> mutees = new HashSet<Object>();
					// normally 'StructureComonent', but 'Structure' also...?
	
	private Object previousMutee = null;
	
	public enum ActivationType { AUTO, ATOMS_AND_BONDS, RIBBONS, SURFACE }

	static protected ActivationType activationType;


	static public void setActivationType( final ActivationType type )
	{
		activationType = type;
	}

	static public ActivationType getActivationType( )
	{
		return activationType;
	}
	
	public MutatorBase()
	{
		activationType = ActivationType.AUTO;
		AppBase.sgetUpdateController().registerListener(this);
	}
	
	// returns: was the mutee "selection" turned on? (else, it was turned off)
	public boolean toggleMutee(final Object mutee)
	{
		boolean isMuteeOnNow = false;
		
		if(VFAppBase.sgetSceneController().isBatchMode()) {
			if(this.supportsBatchMode()) {
				if(mutees.contains(mutee)) {
					mutees.remove(mutee);
				} else {
					mutees.add(mutee);
					isMuteeOnNow = true;
				}
			}
		} else { // if this is immediate mode...
			MutatorBase.removeAllMutees();
			
			if(this.isShiftDown) {
				final Vector<StructureComponent> range = this.generateMuteeRange(mutee, this.previousMutee);
				if(range == null) {
					mutees.add(mutee);
				} else {
					for(int i = 0; i < range.size(); i++) {
						final Object mutee_ = range.get(i);
						mutees.add(mutee_);
					}
				}
			} else {
				mutees.add(mutee);
			}
			
			this.doMutation();
			
			this.previousMutee = mutee;
			
			for (Structure struc : AppBase.sgetModel().getStructures())
				((JoglSceneNode)struc.getStructureMap().getUData()).regenerateGlobalList();
			
			VFAppBase.sgetGlGeometryViewer().requestRepaint();
		}
		
//		if(mutee instanceof StructureComponent) {
//			this.model.getStructure().getStructureMap().getStructureStyles().setSelected((StructureComponent)mutee, isMuteeOnNow);
//		} else if(mutee instanceof Structure) {
//			Structure s = (Structure)mutee;
//			StructureMap sm = s.getStructureMap();
//			Iterator chainIt = s.getStructureMap().getChains().iterator();
//			while(chainIt.hasNext()) {
//				Chain c = (Chain)chainIt.next();
//				sm.getStructureStyles().setSelected(c, isMuteeOnNow);
//			}
//		} else {
//			(new Exception("Can't set " + mutee.getClass().getName() + " selected")).printStackTrace();
//		}
		
		return isMuteeOnNow;
	}
	
	// parameters can be null.
	// if returned value is null, there is no range associated with these mutees.
	// range is between mutee1 and mutee2, not including mutee2, but including mutee1.
	private Vector<StructureComponent> generateMuteeRange(final Object mutee1, final Object mutee2)
	{
		if (mutee1 == null || mutee2 == null || mutee1 == mutee2 ||
		   !mutee1.getClass().equals(mutee2.getClass()) || mutee1 instanceof Structure)
			return null;

		final Vector<StructureComponent> mutees = new Vector<StructureComponent>();
		
		if(mutee1 instanceof Atom) {
			final Atom a1 = (Atom)mutee1;
			final Atom a2 = (Atom)mutee2;
			
			final StructureMap sm = a1.structure.getStructureMap();
			
			final int a1Index = sm.getAtomIndex(a1);
			final int a2Index = sm.getAtomIndex(a2);
			if(a1Index < a2Index) {
				for(int i = a1Index; i < a2Index; i++) {
					mutees.add(sm.getAtom(i));
				}
			} else {
				for(int i = a2Index + 1; i <= a1Index; i++) {
					mutees.add(sm.getAtom(i));
				}
			}
		} else if(mutee1 instanceof Bond) {
			final Bond b1 = (Bond)mutee1;
			final Bond b2 = (Bond)mutee2;
			
			final StructureMap sm = b1.structure.getStructureMap();
			
			final int b1Index = sm.getBondIndex(b1);
			final int b2Index = sm.getBondIndex(b2);
			if(b1Index < b2Index) {
				for(int i = b1Index; i < b2Index; i++) {
					mutees.add(sm.getBond(i));
				}
			} else {
				for(int i = b2Index + 1; i <= b1Index; i++) {
					mutees.add(sm.getBond(i));
				}
			}
		} else if(mutee1 instanceof Residue) {
			final Residue r1 = (Residue)mutee1;
			final Residue r2 = (Residue)mutee2;
			
			final StructureMap sm = r1.structure.getStructureMap();
			
			final int r1Index = sm.getResidueIndex(r1);
			final int r2Index = sm.getResidueIndex(r2);
			if(r1Index < r2Index) {
				for(int i = r1Index; i < r2Index; i++) {
					mutees.add(sm.getResidue(i));
				}
			} else {
				for(int i = r2Index + 1; i <= r1Index; i++) {
					mutees.add(sm.getResidue(i));
				}
			}
		} else if(mutee1 instanceof Fragment) {
			final Fragment f1 = (Fragment)mutee1;
			final Fragment f2 = (Fragment)mutee2;
			
			final StructureMap sm = f1.structure.getStructureMap();
			
			final int f1Index = sm.getFragmentIndex(f1);
			final int f2Index = sm.getFragmentIndex(f2);
			if(f1Index < f2Index) {
				for(int i = f1Index; i < f2Index; i++) {
					mutees.add(sm.getFragment(i));
				}
			} else {
				for(int i = f2Index + 1; i <= f1Index; i++) {
					mutees.add(sm.getFragment(i));
				}
			}
		} else if(mutee1 instanceof Chain) {
			final Chain c1 = (Chain)mutee1;
			final Chain c2 = (Chain)mutee2;
			
			final StructureMap sm = c1.structure.getStructureMap();
			
			final int c1Index = sm.getChainIndex(c1);
			final int c2Index = sm.getChainIndex(c2);
			if(c1Index < c2Index) {
				for(int i = c1Index; i < c2Index; i++) {
					mutees.add(sm.getChain(i));
				}
			} else {
				for(int i = c2Index + 1; i <= c1Index; i++) {
					mutees.add(sm.getChain(i));
				}
			}
		}
		
		else if(mutee1 instanceof ExternChain)
		{
			ExternChain xcArray[] = {(ExternChain)mutee1, (ExternChain)mutee2 };
			
			if (xcArray[0].getExternChainType() != xcArray[1].getExternChainType())
				return null;
								// ok, this would have been caught in the actual type test
								// above, but we've removed *so* much duplicate code by
								// condensing, this is a small price to pay.
								//
								// 22-Oct-08 - rickb
			
			final StructureMap sm = xcArray[0].structure.getStructureMap();
			
			int minIndex = Integer.MAX_VALUE;
			int maxIndex = -1;

			for (ExternChain xc : xcArray)
				for (Chain c : xc.getMbtChains())
				{
					final int index = sm.getChainIndex(c);
					minIndex = Math.min(index, minIndex);
					maxIndex = Math.max(index, maxIndex);
				}

			for(int i = minIndex; i <= maxIndex; i++) {
				mutees.add(sm.getChain(i));
			}
		}
		
		else if(mutee1 instanceof Surface) {
			mutees.add((Surface)mutee1);
		}

		return mutees;
	}
	
	public void doBatchMutation() 
	{
		if(this.supportsBatchMode()) 
		{
			for (Structure structure : AppBase.sgetModel().getStructures())
			{
				final StructureStyles ss = structure.getStructureMap().getStructureStyles();
				final ArrayList<StructureComponent> items = ss.getSelectedItems();
				for(int j = 0; j < items.size(); j++)
					this.doMutationSingle(items.get(j));
				}
			}
		}
	
	public static void removeMutee(final Object mutee) {
		mutees.remove(mutee);
	}
	
	public static void removeAllMutees() {
		mutees.clear();
	}
	
	public abstract void doMutationSingle(Object mutee);
	
	public abstract void doMutation();
	
	public abstract boolean supportsBatchMode();

	public boolean isCtrlDown() {
		if(this.considerClickModifiers) {
			return this.isCtrlDown;
		}
		
		return false;
	}

	public void setCtrlDown(final boolean isCtrlDown) {
		this.isCtrlDown = isCtrlDown;
	}

	public boolean isShiftDown() {
		if(this.considerClickModifiers) {
			return this.isShiftDown;
		}
		
		return false;
	}

	public void setShiftDown(final boolean isShiftDown) {
		this.isShiftDown = isShiftDown;
	}

	public boolean shouldConsiderClickModifiers() {
		return this.considerClickModifiers;
	}

	public void setConsiderClickModifiers(final boolean considerClickModifiers) {
		this.considerClickModifiers = considerClickModifiers;
	}

	public boolean shouldConsiderSelectedFlag() {
		return this.considerSelectedFlag;
	}

	public void setConsiderSelectedFlag(final boolean considerSelectedFlag) {
		this.considerSelectedFlag = considerSelectedFlag;
	}

	public boolean isSelected() {
		return this.isSelected;
	}

	public void setSelected(final boolean isSelected) {
		this.isSelected = isSelected;
	}
	
    public void clearStructure() {
    	this.previousMutee = null;
    }

	/* (non-Javadoc)
	 * @see edu.sdsc.mbt.views_controller.IViewUpdateListener#handleModelChangedEvent(edu.sdsc.mbt.views_controller.ViewUpdateEvent)
	 */
	public void handleUpdateEvent(UpdateEvent evt)
	{
		switch (evt.action)
		{
		case CLEAR_ALL:
			clearStructure();
			break;
		}
	}
}
