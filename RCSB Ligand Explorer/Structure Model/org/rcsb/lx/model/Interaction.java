package org.rcsb.lx.model;

import org.rcsb.mbt.glscene.geometry.Point3d;
import org.rcsb.mbt.model.Atom;
import org.rcsb.mbt.model.LineSegment;
import org.rcsb.mbt.model.StructureComponent;


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
	
	public static final String componentType = "Interaction";

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
	
	public String getStructureComponentType() {
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
