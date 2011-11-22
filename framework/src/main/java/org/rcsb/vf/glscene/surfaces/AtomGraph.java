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
package org.rcsb.vf.glscene.surfaces;

import java.util.HashMap;
import java.util.Vector;

import org.rcsb.mbt.model.Atom;



public class AtomGraph {
//  a cyclic graph. Not necessarily organized - the only reestriction is that elements must be at most a
//  certain distance apart physically.
//	
	
	public Vector<AtomTriangle> atomTriangles = new Vector<AtomTriangle>();
		
	public AtomGraph(final Vector<AtomPair> atomPairs)
	{
		if(atomPairs.size() == 0)
		{
			new Exception("").printStackTrace();
			return;
		}
		
		// key: Atom. value: AtomEntry of atoms close to the key. See below AtomPair generation logic.
		final HashMap<Atom, Vector<AtomPair>> indexByAtom = new HashMap<Atom, Vector<AtomPair>>();
		
		for(int i = 0; i < atomPairs.size(); i++) {
			final AtomPair pair = (AtomPair)atomPairs.get(i);
			
			for(int j = 0; j < pair.atoms.length; j++) {
				final Atom atom = pair.atoms[j];
				Vector<AtomPair> edges = indexByAtom.get(atom);
				if(edges == null) {
					edges = new Vector<AtomPair>();
					indexByAtom.put(atom, edges);
				}
				
				edges.add(pair);
			}
		}
		
		for (Atom atom : indexByAtom.keySet())
		{
			final Vector<AtomPair> edges = indexByAtom.get(atom);
			
			for(int i = 0; i < edges.size(); i++) {
				final AtomPair pair1 = (AtomPair)edges.get(i);
				Atom atom2 = pair1.atoms[0];
				if(atom2 == atom) {
					atom2 = pair1.atoms[1];
				}
				
				// skip if this node has already been visited.
				if(!indexByAtom.containsKey(atom2)) {
					continue;
				}
				
				// to maintain uniqueness of triangles, avoid re-checking edge pairs.
				for(int j = i + 1; j < edges.size(); j++) {
					final AtomPair pair2 = (AtomPair)edges.get(j);
					Atom atom3 = pair2.atoms[0];
					if(atom3 == atom) {
						atom3 = pair2.atoms[1];
					}
					
					// skip if this node has already been visited.
					final Vector<AtomPair> edges2 = indexByAtom.get(atom3);
					if(edges2 == null) {
						continue;
					}
					
					// finally, see if we can find a third edge in common. This makes a triangle.
					for(int k = 0; k < edges2.size(); k++) {
						final AtomPair pair3 = (AtomPair)edges2.get(k);
						
						if(pair3.atoms[0] == atom2 || pair3.atoms[1] == atom2) {
							final AtomTriangle triangle = new AtomTriangle();
							triangle.atomPairs[0] = pair1;
							triangle.atomPairs[1] = pair2;
							triangle.atomPairs[2] = pair3;
							this.atomTriangles.add(triangle);
						}
					}
				}
			}
			
			indexByAtom.remove(atom);
			       	// no more triangles contain this node, so remove it. Used with the code above, this helps maintain triangle uniqueness.
		}
	}	
}
