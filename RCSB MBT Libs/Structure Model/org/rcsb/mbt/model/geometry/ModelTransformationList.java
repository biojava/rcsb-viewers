package org.rcsb.mbt.model.geometry;

import java.util.ArrayList;



/**
 * Qualified class for list of transformations.
 * Used for Biological Unit or Non-Crystallographic transforms
 *
 * Note this is the model version, not the GL version.
 * 
 * @see org.rcsb.vf.glscene.jogl.GLTransformationList
 * @see org.rcsb.mbt.model.geometry.ModelTranformationMatrix
 * 
 * @author rickb
 */
public class ModelTransformationList extends ArrayList<ModelTransformationMatrix>
{
	private static final long serialVersionUID = 8800626210014078311L;
	public ModelTransformationList(int num) { super(num); }
	public ModelTransformationList() { super(); }
}