package org.rcsb.vf.glscene.surfaces;

import org.rcsb.mbt.model.Atom;

public class AtomPair {
	public double atom1vdWRadius = 0;
	public double atom2vdWRadius = 0;
	public double voidDistance = 0;
	public double center2centerDistance = 0;
	public Atom[] atoms = new Atom[2];
	public double[] atom1toAtom2Vector = null;
	public double[] voidCenter = null;
}
