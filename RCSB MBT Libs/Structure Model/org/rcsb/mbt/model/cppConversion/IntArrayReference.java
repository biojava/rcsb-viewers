package org.rcsb.mbt.model.cppConversion;

public class IntArrayReference {
	public IntArrayReference() {}
	
	public IntArrayReference(final int[] array, final int startIndex) {
		this.array = array;
		this.startIndex = startIndex;
	}
	
	public int[] array = null;
	public int startIndex = 0;
	
	public int get(final int index) {
		return this.array[index + this.startIndex];
	}
	
	public void set(final int index, final int value) {
		this.array[index + this.startIndex] = value;
	}
	
	public IntArrayReference slice(final int index) {
		final IntArrayReference r = new IntArrayReference(this.array, this.startIndex + index);
		return r;
	}
}
