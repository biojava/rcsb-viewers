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
package org.rcsb.lx.model;

import org.rcsb.lx.model.LXStructureComponentRegistry.LXComponentType;
import javax.vecmath.Point3d;
import org.rcsb.mbt.model.Atom;
import org.rcsb.mbt.model.LineSegment;
import org.rcsb.mbt.model.StructureComponent;
import org.rcsb.mbt.model.StructureComponentRegistry.ComponentType;


/**
 * An interaction describes the interaction between atoms of a given ligand and a residue
 * somewhere in the protein.
 * 
 * This is derived from LineSegment because that's a low level base available to the display
 * list caller.
 * 
 * @author rickb
 */
public class Interaction extends LineSegment {
	
	private Atom firstAtom;
	private Atom secondAtom;
	private String interactionType;
	private String distance;
	private double distanceDouble;
	
	public static final ComponentType componentType = ComponentType.EXTENDED;
	public static final LXComponentType lxComponentType = LXComponentType.INTERACTION;
						// extended ComponentType

	public Interaction(final Atom fAtom, final Atom sAtom, final String interactionType, final String distance, final double distanceDouble) {
		super(new Point3d(fAtom.coordinate[0], fAtom.coordinate[1], fAtom.coordinate[2]),
			  new Point3d(sAtom.coordinate[0], sAtom.coordinate[1], sAtom.coordinate[2]));
		this.firstAtom = fAtom;
		this.secondAtom = sAtom;
		this.interactionType = interactionType;
		this.distance = distance;
		this.distanceDouble = distanceDouble;
	}
	
	public void copy(final StructureComponent structureComponent) {

	}
	
	public ComponentType getStructureComponentType() {
		return Interaction.componentType;
	}
	
	public Atom getFirstAtom() {
		return this.firstAtom;
	}
	
	public Atom getSecondAtom() {
		return this.secondAtom;
	}

	public String getInteractionType() {
		return this.interactionType;
	}

	public String getDistance() {
		return this.distance;
	}
	
	public double getDistanceDouble() {
		return this.distanceDouble;
	}
}
