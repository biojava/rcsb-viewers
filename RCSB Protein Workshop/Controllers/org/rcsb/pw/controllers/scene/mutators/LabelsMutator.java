package org.rcsb.pw.controllers.scene.mutators;

import java.util.Iterator;
import java.util.Vector;

import org.rcsb.mbt.controllers.app.AppBase;
import org.rcsb.mbt.glscene.jogl.CustomAtomLabel;
import org.rcsb.mbt.glscene.jogl.DisplayListRenderable;
import org.rcsb.mbt.glscene.jogl.GlGeometryViewer;
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
import org.rcsb.mbt.model.attributes.AtomLabelByAtomCompound;
import org.rcsb.mbt.model.attributes.AtomLabelByAtomName;
import org.rcsb.mbt.model.attributes.AtomLabelNone;
import org.rcsb.mbt.model.attributes.AtomStyle;
import org.rcsb.mbt.model.attributes.IAtomLabel;
import org.rcsb.pw.controllers.scene.mutators.options.LabelsOptions;




public class LabelsMutator extends Mutator {
	LabelsOptions options = null;
	
	public LabelsMutator() {
		super();
		this.options = new LabelsOptions();
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
				this.changeLabelStyle((Atom)next);
			} else if(next instanceof Bond) {
				this.changeLabelStyle((Bond)next);
			} else if(next instanceof Residue) {
				this.changeLabelStyle((Residue)next);
			} else if(next instanceof Chain) {
				this.changeLabelStyle((Chain)next);
			} else if(next instanceof PdbChain) {
				this.changeLabelStyle((PdbChain)next);
			} else if(next instanceof WaterChain) {
				this.changeLabelStyle((WaterChain)next);
			} else if(next instanceof MiscellaneousMoleculeChain) {
				this.changeLabelStyle((MiscellaneousMoleculeChain)next);
			} else if(next instanceof Fragment) {
				this.changeLabelStyle((Fragment)next);
			} else if(next instanceof Structure) {
				this.changeLabelStyle((Structure)next);
			}
		}
	}

	public LabelsOptions getOptions() {
		return this.options;
	}

	public void changeLabelStyle(final Atom a) {
		final GlGeometryViewer glViewer = AppBase.sgetGlGeometryViewer();
		
//		if(PickLevel.pickLevel == PickLevel.COMPONENTS_ATOMS_BONDS) {
	        final DisplayListRenderable renderable = a.structure.getStructureMap().getSceneNode().getRenderable(a);
			
			if(renderable != null) {
				final AtomStyle oldStyle = (AtomStyle)renderable.style;
				final AtomStyle newStyle = new AtomStyle();
				IAtomLabel label = null;
				
				switch(this.options.getCurrentLabellingStyle()) {
				case LabelsOptions.LABEL_BY_ATOM:
					label = AtomLabelByAtomName.create();
					break;
				case LabelsOptions.LABEL_BY_RESIDUE:
					final Residue r = a.getStructure().getStructureMap().getResidue(a);
					// label only if this is this either the alpha atom (amino acid) or the center atom (non-amino acid)?
					if((r.getAlphaAtomIndex() == -1 && r.getAtoms().indexOf(a) == r.getAtomCount() / 2) || (r.getAlphaAtom() == a)) {
						label = AtomLabelByAtomCompound.create();
					} else {
						label = AtomLabelNone.create();
					}
					break;
				case LabelsOptions.LABEL_CUSTOM:
					CustomAtomLabel label_ = null;
					
					if(oldStyle.getAtomLabel() instanceof CustomAtomLabel) {
						label_ = (CustomAtomLabel)oldStyle.getAtomLabel();
						label_.setAtomLabel(this.options.getCustomLabel());
					} else {
						label_ = new CustomAtomLabel(this.options.getCustomLabel());
					}
					
					label = label_;
					break;
				case LabelsOptions.LABELS_OFF:
					label = AtomLabelNone.create();
					break;
				default:
					(new IllegalArgumentException(this.options.getCurrentLabellingStyle() + "")).printStackTrace();
				}
				
				if(oldStyle != null) {
					newStyle.setAtomColor(oldStyle.getAtomColor());
					newStyle.setAtomRadius(oldStyle.getAtomRadius());
				}
				newStyle.setAtomLabel(label);
				a.structure.getStructureMap().getStructureStyles().setStyle(a, newStyle);
				
				renderable.style = newStyle;
				renderable.setDirty();
			}
//		} else {	// if ribbon residues, delegate up to the residue
//			final Residue r = a.structure.getStructureMap().getResidue(a);
//			this.changeLabelStyle(r);
//		}
	}
	
	public void changeLabelStyle(final Bond b) {
		final GlGeometryViewer glViewer = AppBase.sgetGlGeometryViewer();
		
//		if(PickLevel.pickLevel == PickLevel.COMPONENTS_ATOMS_BONDS) {
			// arbitrarily delegate to first visible atom.
			if(b.structure.getStructureMap().getSceneNode().getRenderable(b.getAtom(0)) != null) {
				this.changeLabelStyle(b.getAtom(0));
			} else {
				this.changeLabelStyle(b.getAtom(1));
			}
//		} else { // if ribbon residues, delegate up to the residue
//			Atom a = b.getAtom(0);
//			if(a == null) {
//				a = b.getAtom(1);
//			}
//			final Residue r = b.structure.getStructureMap().getResidue(a);
//			this.changeLabelStyle(r);
//		}
	}
	
	public void changeLabelStyle(final Residue r) {
//		if(PickLevel.pickLevel == PickLevel.COMPONENTS_ATOMS_BONDS) {
			final Vector atoms = r.getAtoms();
			final Iterator atomsIt = atoms.iterator();
			while(atomsIt.hasNext()) {
				final Atom a = (Atom)atomsIt.next();
				this.changeLabelStyle(a);
			}
			
			final Iterator bondsIt = r.getStructure().getStructureMap().getBonds(atoms).iterator();
			while(bondsIt.hasNext()) {
				final Bond b = (Bond)bondsIt.next();
				this.changeLabelStyle(b);
			}
//		} else {	// if this is a ribbon pick level, then we're at the right place.
//			final StructureMap sm = r.structure.getStructureMap();
//			final StructureStyles ss = sm.getStructureStyles();
//			final Chain c = sm.getChain(r.getChainId());
//			final ChainStyle cs = (ChainStyle)ss.getStyle(c);
//			
//			final ResidueLabel label = cs.getResidueLabel();
//			label.setResidueLabel(r, r.getCompoundCode());	// actually not used right now...
//			sm.getSceneNode().createAndAddLabel(r, r.getCompoundCode());
//			Model.getSingleton().getViewer().requestRepaint();
//		}
	}
	
	public void changeLabelStyle(final Chain c) {
//		if(PickLevel.pickLevel == PickLevel.COMPONENTS_ATOMS_BONDS) {
			final Iterator it = c.getFragments().iterator();
			while(it.hasNext()) {
				final Fragment f = (Fragment)it.next();
				this.changeLabelStyle(f);
			}
//		} else { // if this is a ribbon pick level, then delegate to the middle residue.
//			this.changeLabelStyle(c.getResidue(c.getResidueCount() / 2));
//		}
	}
	
	public void changeLabelStyle(final PdbChain c) {
//		if(PickLevel.pickLevel == PickLevel.COMPONENTS_ATOMS_BONDS) {
			final Iterator it = c.getMbtChainIterator();
			while(it.hasNext()) {
				final Chain mbtChain = (Chain)it.next();
				this.changeLabelStyle(mbtChain);
			}
//		} else { // if this is a ribbon pick level, then delegate to the middle residue.
//			this.changeLabelStyle(c.getResidue(c.getResidueCount() / 2));
//		}
	}
	
	public void changeLabelStyle(final WaterChain c) {
//		if(PickLevel.pickLevel == PickLevel.COMPONENTS_ATOMS_BONDS) {
			final Iterator it = c.getMbtChainIterator();
			while(it.hasNext()) {
				final Chain mbtChain = (Chain)it.next();
				this.changeLabelStyle(mbtChain);
			}
//		} else { // if this is a ribbon pick level, then delegate to the middle residue.
//			this.changeLabelStyle(c.getResidue(c.getResidueCount() / 2));
//		}	
	}
	
	public void changeLabelStyle(final MiscellaneousMoleculeChain c) {
//		if(PickLevel.pickLevel == PickLevel.COMPONENTS_ATOMS_BONDS) {
			final Iterator it = c.getMbtChainIterator();
			while(it.hasNext()) {
				final Chain mbtChain = (Chain)it.next();
				this.changeLabelStyle(mbtChain);
			}
//		} else { // if this is a ribbon pick level, then delegate to the middle residue.
//			this.changeLabelStyle(c.getResidue(c.getResidueCount() / 2));
//		}
	}
	
	public void changeLabelStyle(final Fragment f) {
//		if(PickLevel.pickLevel == PickLevel.COMPONENTS_ATOMS_BONDS) {
			final int resCount = f.getResidueCount();
			for(int i = 0; i < resCount; i++) {
				this.changeLabelStyle(f.getResidue(i));
			}
//		} else { // if this is a ribbon pick level, then delegate to the middle residue.
//			this.changeLabelStyle(f.getResidue(f.getResidueCount() / 2));
//		}	
	}
	
	public void changeLabelStyle(final Structure s) {
		final StructureMap sm = s.getStructureMap();
		
		final Iterator it = sm.getChains().iterator();
		while(it.hasNext()) {
			final Chain c = (Chain)it.next();
			this.changeLabelStyle(c);
		}
	}
}
