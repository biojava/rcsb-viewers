package org.rcsb.mbt.model;

import java.util.ArrayList;

import org.rcsb.mbt.glscene.jogl.TransformationMatrix;


/**
 * Qualified class for list of transformations.
 * Used for Biological Unit or Non-Crystallographic transforms
 * 
 * @author rickb
 */
public class TransformationList extends ArrayList<TransformationMatrix>
{
	private static final long serialVersionUID = 8800626210014078311L;
	public TransformationList(int num) { super(num); }
	public TransformationList() { super(); }
}