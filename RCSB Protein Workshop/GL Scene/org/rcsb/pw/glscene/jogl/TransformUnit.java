package org.rcsb.pw.glscene.jogl;

import org.rcsb.mbt.glscene.jogl.JoglSceneNode;
import org.rcsb.mbt.glscene.jogl.TransformationMatrix;
import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.StructureMap;
import org.rcsb.mbt.model.attributes.StructureStyles;


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
	TransformationMatrix globalTransform = null;
}
