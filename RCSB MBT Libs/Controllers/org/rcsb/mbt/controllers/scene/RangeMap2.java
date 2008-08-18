package org.rcsb.mbt.controllers.scene;

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
