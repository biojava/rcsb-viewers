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

import java.awt.Color;
import java.util.Vector;

import org.rcsb.mbt.model.Atom;
import org.rcsb.mbt.model.Bond;
import org.rcsb.mbt.model.Chain;
import org.rcsb.mbt.model.Fragment;
import org.rcsb.mbt.model.LineSegment;
import org.rcsb.mbt.model.ExternChain;
import org.rcsb.mbt.model.Residue;
import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.StructureComponent;
import org.rcsb.mbt.model.StructureMap;
import org.rcsb.mbt.model.StructureComponentRegistry.ComponentType;
import org.rcsb.mbt.model.attributes.LineStyle;
import org.rcsb.pw.controllers.app.ProteinWorkshop;
import org.rcsb.pw.controllers.scene.mutators.options.LinesOptions;
import org.rcsb.uiApp.controllers.app.AppBase;
import org.rcsb.vf.controllers.scene.mutators.MutatorBase;
import org.rcsb.vf.glscene.jogl.DisplayListRenderable;
import org.rcsb.vf.glscene.jogl.JoglSceneNode;
import org.rcsb.vf.glscene.jogl.LineGeometry;

public class LinesMutator extends MutatorBase
{
	private LinesOptions options = null;
	public Vector<LineSegment> lines = new Vector<LineSegment>();

	private boolean isFirstClick = true;	// Is this the first object in an object pair? Else this is the second click.
	public LinesMutator()
	{
		super();
		this.options = new LinesOptions();
	}


	@Override
	public boolean supportsBatchMode()
	{
		return false;
	}


	@Override
	public void doMutationSingle(final Object mutee) {

		if(mutee instanceof LineSegment)
		{
			JoglSceneNode sn = (JoglSceneNode)AppBase.sgetModel().getStructures().get(0).getStructureMap().getUData();
			sn.removeRenderable((LineSegment)mutee);
			sn.removeLabel(mutee);
		}

		else
		{
			if(this.isFirstClick)
			{
				this.getOptions().setFirstPoint(this.getCoordinates(mutee));
				final String message = this.reportAtComponent(mutee);
				this.getOptions().setFirstDescription(message);
				ProteinWorkshop.sgetActiveFrame().getLinesPanel().updateObject1Text(this.getOptions().getFirstDescription());
			}

			else 
			{
				this.getOptions().setSecondPoint(this.getCoordinates(mutee));
				final String message = this.reportAtComponent(mutee);
				this.getOptions().setSecondDescription(message);
				ProteinWorkshop.sgetActiveFrame().getLinesPanel().updateObject2Text(this.getOptions().getSecondDescription());
				this.drawLine();
			}
		}

		this.isFirstClick = !this.isFirstClick;
	}

	public void drawLine() {
		final LineSegment line = new LineSegment(this.getOptions().getFirstPoint(), this.getOptions().getSecondPoint());
		final LineGeometry geometry = new LineGeometry();
		final LineStyle style = new LineStyle();
		style.lineStyle = this.options.getLineStyle();
		style.label = Double.toString(line.getFirstPoint().distance(line.getSecondPoint()));

		// 5 characters max
		style.label = style.label.substring(0, style.label.length() >= 5 ? 5 : style.label.length());

		final StructureMap sm = AppBase.sgetModel().getStructures().get(0).getStructureMap();
		sm.getStructureStyles().setStyle(line, style);
		System.arraycopy(this.getOptions().getColor(), 0, style.getColor(), 0, style.getColor().length);
		((JoglSceneNode)sm.getUData()).addRenderable(new DisplayListRenderable( line, style, geometry ));

		lines.add(line);
	}


	public String reportAtComponent(final Object mutee) {
		String message = null;

		if(mutee instanceof StructureComponent) {
			final StructureComponent structureComponent = (StructureComponent)mutee;
			final ComponentType scType = structureComponent.getStructureComponentType();

			if (scType == ComponentType.ATOM) {
				final Atom atom = (Atom) structureComponent;

				message =
					"Atom: "
					+ atom.authorChain_id + "/" 
					+ atom.authorResidue_id + "/" 
					+ atom.name	+ "/"
					+ atom.compound;
			}

			else if (scType == ComponentType.BOND)
			{
				final Bond bond = (Bond) structureComponent;

				final Atom a1 = bond.getAtom(0);
				final Atom a2 = bond.getAtom(1);

				message = "Covalent bond. Atom 1: "
					+ a1.name + ", res: " + a1.authorResidue_id + ", chain "
					+ a1.authorChain_id + "; Atom 2: " + a2.name + ", res "
					+ a2.authorResidue_id + ", chain " + a2.authorChain_id;
			}

			else if (scType == ComponentType.RESIDUE)
			{
				final Residue r = (Residue) structureComponent;
				final Fragment f = r.getFragment();
				ComponentType conformationType = (f.getConformationType().isConformationType())? 
						f.getConformationType() : ComponentType.UNDEFINED_CONFORMATION;

						message = "Residue " + r.getAuthorResidueId()
						+ ", from chain " + r.getAuthorChainId() + "; "
						+ conformationType + " conformation; "
						+ r.getCompoundCode() + " compound.";
			}

			else if (scType == ComponentType.FRAGMENT)
			{
				final Fragment f = (Fragment) structureComponent;

				// remove all but the local name for the secondary structure class.
				ComponentType conformation = f.getConformationType();

				message = conformation
				+ " fragment: chain " + f.getChain().getAuthorChainId()
				+ ", " + f.getResidue(0).getAuthorResidueId()
				+ " - " + f.getResidue(f.getResidueCount() - 1)
				.getAuthorResidueId();

			} else if (scType == ComponentType.CHAIN) {
				final Chain c = (Chain) structureComponent;

				message = "Chain " + c.getAuthorChainId()
				+ " backbone";
			}

			else if (mutee instanceof ExternChain)
			{
				final ExternChain c = (ExternChain) structureComponent;

				// remove all but the local name for the secondary structure class.
				message = (c.isBasicChain())? "Chain " + c.getChainId() : 
					(c.isWaterChain())? "Water Molecules" :
						"Miscellaneous Molecules";
			} 

		} else if(mutee instanceof Structure){	// not a StructureComponent
			final Structure struct = (Structure)mutee;
			message = struct.toString();
		}

		return message;
	}

	public double[] getCoordinates(final Object mutee) {
		final double[] coordinates = {0,0,0};

		if(mutee instanceof StructureComponent) {
			final StructureComponent structureComponent = (StructureComponent)mutee;
			final ComponentType scType = structureComponent.getStructureComponentType();

			if (scType == ComponentType.ATOM) {
				final Atom atom = (Atom) structureComponent;

				System.arraycopy(atom.coordinate, 0, coordinates, 0, atom.coordinate.length);
			} else if (scType == ComponentType.BOND) {
				final Bond bond = (Bond) structureComponent;

				final Atom a1 = bond.getAtom(0);
				final Atom a2 = bond.getAtom(1);

				// the point half way along the bond.
				for(int i = 0; i < a1.coordinate.length; i++) {
					coordinates[i] = (a1.coordinate[i] + a2.coordinate[i]) / 2;
				}
			} else if (scType == ComponentType.RESIDUE) {
				final Residue r = (Residue) structureComponent;
				Atom atom;
				if(r.getAlphaAtomIndex() >= 0) {
					atom = r.getAlphaAtom();
				} else {
					atom = r.getAtom(r.getAtomCount() / 2);
				}

				return this.getCoordinates(atom);
			} else if (scType == ComponentType.FRAGMENT) {
				final Fragment f = (Fragment) structureComponent;
				final Residue r = f.getResidue(f.getResidueCount() / 2);

				return this.getCoordinates(r);
			} else if (scType == ComponentType.CHAIN) {
				final Chain c = (Chain) structureComponent;
				final Residue r = c.getResidue(c.getResidueCount() / 2);

				return this.getCoordinates(r);
			} else if(structureComponent instanceof ExternChain) {
				final ExternChain c = (ExternChain) structureComponent;
				final Residue r = c.getResidue(c.getResidueCount() / 2);

				return this.getCoordinates(r); 
			}
		} else if(mutee instanceof Structure){	// not a StructureComponent
			final Structure struct = (Structure)mutee;
			final StructureMap sm = struct.getStructureMap();
			final Residue r = sm.getResidue(sm.getResidueCount() / 2);

			return this.getCoordinates(r);
		}

		return coordinates;
	}



	@Override
	public void doMutation()
	{
		for (Object next : mutees)
			this.doMutationSingle(next);
	}

	public LinesOptions getOptions() {
		return this.options;
	}	


	public void reset() {
		this.isFirstClick = true;
		Color.WHITE.getComponents(this.options.getColor());
	}


	@Override
	public void clearStructure() {
		this.reset();
	}
}
