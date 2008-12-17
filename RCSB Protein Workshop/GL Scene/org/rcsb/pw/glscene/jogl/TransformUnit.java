package org.rcsb.pw.glscene.jogl;

import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.StructureMap;
import org.rcsb.mbt.model.attributes.StructureStyles;
import org.rcsb.mbt.model.geometry.ModelTransformationMatrix;
import org.rcsb.vf.glscene.jogl.JoglSceneNode;


/**
 * Used mostly for biological units and structural alignments.
 * @author John Beaver
 *
 */
public class TransformUnit {
	Structure struc = null;
	StructureMap sm = null;
	JoglSceneNode node = null;
	StructureStyles ss = null;
	ModelTransformationMatrix globalTransform = null;
}
