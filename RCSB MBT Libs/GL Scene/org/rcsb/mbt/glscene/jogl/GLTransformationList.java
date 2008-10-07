package org.rcsb.mbt.glscene.jogl;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.rcsb.mbt.model.geometry.ModelTransformationList;
import org.rcsb.mbt.model.geometry.ModelTransformationMatrix;

import com.sun.opengl.util.BufferUtil;



/**
 * Qualified class for list of transformations.
 * Used for Biological Unit or Non-Crystallographic transforms
 * 
 * Note this is different from the ModelTransformationList, which
 * holds ModelTransformationMatrices.  There is a utility to
 * create and return a GLTransformationList from a ModelTransformationList
 * 
 * @see org.rcsb.mbt.model.geometry.ModelTranformationMatrix
 * @see org.rcsb.mbt.model.geometry.ModelTransformationList
 * 
 * @author rickb
 */
public class GLTransformationList extends ArrayList<FloatBuffer>
{
	private static final long serialVersionUID = 8800626210014078311L;
	public GLTransformationList(int num) { super(num); }
	public GLTransformationList() { super(); }
	
	public static GLTransformationList fromModelTransformationList(ModelTransformationList modelList)
	{
		if (modelList == null) return null;
		
		GLTransformationList glList = new GLTransformationList();
		for (ModelTransformationMatrix modelTrans : modelList)
		{
			FloatBuffer buf = BufferUtil.newFloatBuffer(16);
			for (Float value : modelTrans.values)
				buf.put(value);
			glList.add(buf);
		}
		
		return glList;
	}
}