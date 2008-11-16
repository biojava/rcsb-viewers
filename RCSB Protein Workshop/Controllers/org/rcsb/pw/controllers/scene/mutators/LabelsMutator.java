package org.rcsb.pw.controllers.scene.mutators;

import java.util.Vector;

import org.rcsb.mbt.glscene.jogl.CustomAtomLabel;
import org.rcsb.mbt.glscene.jogl.DisplayListRenderable;
import org.rcsb.mbt.glscene.jogl.JoglSceneNode;
import org.rcsb.mbt.model.Atom;
import org.rcsb.mbt.model.Bond;
import org.rcsb.mbt.model.Chain;
import org.rcsb.mbt.model.ExternChain;
import org.rcsb.mbt.model.Fragment;
import org.rcsb.mbt.model.Residue;
import org.rcsb.mbt.model.Structure;
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
		for (Object next : mutees)
			if(next instanceof Atom) {
				this.changeLabelStyle((Atom)next);
			} else if(next instanceof Bond) {
				this.changeLabelStyle((Bond)next);
			} else if(next instanceof Residue) {
				this.changeLabelStyle((Residue)next);
			} else if(next instanceof Chain) {
				this.changeLabelStyle((Chain)next);
			} else if(next instanceof ExternChain) {
				this.changeLabelStyle((ExternChain)next);
			} else if(next instanceof Fragment) {
				this.changeLabelStyle((Fragment)next);
			} else if(next instanceof Structure) {
				this.changeLabelStyle((Structure)next);
			}
	}

	public LabelsOptions getOptions() {
		return this.options;
	}

	public void changeLabelStyle(final Atom a)
	{
	        final DisplayListRenderable renderable = ((JoglSceneNode)a.structure.getStructureMap().getUData()).getRenderable(a);
			
			if(renderable != null)
			{
				final AtomStyle oldStyle = (AtomStyle)renderable.style;
				final AtomStyle newStyle = new AtomStyle();
				IAtomLabel label = null;
				
				switch(this.options.getCurrentLabellingStyle())
				{
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
				
				if(oldStyle != null)
				{
					newStyle.setAtomColor(oldStyle.getAtomColor());
					newStyle.setAtomRadius(oldStyle.getAtomRadius());
				}
				
				newStyle.setAtomLabel(label);
				a.structure.getStructureMap().getStructureStyles().setStyle(a, newStyle);
				
				renderable.style = newStyle;
				renderable.setDirty();
			}
	}
	
	public void changeLabelStyle(final Bond b)
	{
			if(((JoglSceneNode)b.structure.getStructureMap().getUData()).getRenderable(b.getAtom(0)) != null) {
				this.changeLabelStyle(b.getAtom(0));
			} else {
				this.changeLabelStyle(b.getAtom(1));
			}
	}
	
	public void changeLabelStyle(final Residue r)
	{
		final Vector<Atom> atoms = r.getAtoms();

		for (final Atom a : atoms)
			this.changeLabelStyle(a);
		
		for (final Bond b : r.getStructure().getStructureMap().getBonds(atoms))
			this.changeLabelStyle(b);
	}
	
	public void changeLabelStyle(final Chain c) {
			for (final Fragment f : c.getFragments())
				this.changeLabelStyle(f);
	}
	
	public void changeLabelStyle(final ExternChain c) {
			for (final Chain mbtChain : c.getMbtChains())
				changeLabelStyle(mbtChain);
	}
	
	public void changeLabelStyle(final Fragment f) {
			for (final Residue r : f.getResidues())
				this.changeLabelStyle(r);
	}
	
	public void changeLabelStyle(final Structure s) {
		for (final Chain c : s.getStructureMap().getChains())
			this.changeLabelStyle(c);
	}
}
