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
package org.rcsb.vf.controllers.scene;

import java.util.Vector;

/**
 * A range map which handles adding of single values, then collapses those values into ranges.
 * @author john beaver
 *
 */
public class RangeMap2 {
	// ranges, sorted by start value.
	// values: int[start,stop]
	private final Vector ranges = new Vector();
	
	public RangeMap2() {
	}

	public void addValue(final int index) {
		boolean valueAdded = false;
		for(int i = this.ranges.size() - 1; i >= 0; i--) {
			int[] range = (int[])this.ranges.get(i);
			if(range[0] == index ||  range[1] == index) {	// value already added. Break.
				valueAdded = true;
				break;
			}
			// if we should extend this range to the right...
			if(range[0] == index + 1) {
				range[0]--;
				valueAdded = true;
				break;
			}
			// if we should extend this range to the left...
			if(range[1] == index - 1) {
				range[1]++;
				valueAdded = true;
				break;
			}
			// if we've gone too far to the left, insert a new range here...
			if(range[1] < index) {
				range = new int[2];
				range[0] = range[1] = index;
				this.ranges.add(i + 1, range);
				valueAdded = true;
				break;
			}
		}
		
		// if we've gone past the end of the array, insert a new range at the beginning...
		if(!valueAdded) {
			final int[] range = {index,index};
			this.ranges.add(0, range);
		}
	}
	
	/**
	 * Collapse adjacent ranges...
	 *
	 */
	public void collapse() {
		int rangeSize = this.ranges.size();
		for(int i = 0; i < rangeSize - 1; i++) {
			final int[] curRange = (int[])this.ranges.get(i);
			final int[] nextRange = (int[])this.ranges.get(i + 1);
			
			if(curRange[1] + 1 == nextRange[0]) {
				curRange[1] = nextRange[1];
				this.ranges.remove(i + 1);
				rangeSize--;
				i--;	// do this again.
			}
		}
	}
	
	public int getRangeCount() {
		return this.ranges.size();
	}
	
	public int[] getRange(final int index) {
		return (int[]) this.ranges.get(index);
	}
}
