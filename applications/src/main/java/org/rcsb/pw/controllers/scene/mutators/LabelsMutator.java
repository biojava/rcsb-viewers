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

import java.util.Vector;

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
import org.rcsb.vf.controllers.scene.mutators.MutatorBase;
import org.rcsb.vf.glscene.jogl.CustomAtomLabel;
import org.rcsb.vf.glscene.jogl.DisplayListRenderable;
import org.rcsb.vf.glscene.jogl.JoglSceneNode;




public class LabelsMutator extends MutatorBase {
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
