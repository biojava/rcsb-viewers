package org.rcsb.lx.controllers.scene;

import org.rcsb.lx.controllers.app.LigandExplorer;
import org.rcsb.lx.glscene.jogl.LXSceneNode;
import org.rcsb.mbt.controllers.scene.SceneController;
import org.rcsb.mbt.model.StructureModel;
import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.StructureMap;


public class LXSceneController extends SceneController
{
	private static int firstReset = 0;

	/**
	 * Reset the view to look at the center of the data. JLM DEBUG: This will
	 * eventually be non-static method.
	 * 
	 * Deliberately hides base implementation
	 */
	public void resetView(final boolean forceRecalculation)
	{
		StructureModel model = LigandExplorer.sgetModel();
		if (!model.hasStructures())
			return;

		StructureModel.StructureList structures = model.getStructures();
		
		for (Structure struc : structures)
		{
			final StructureMap sm = struc.getStructureMap();
			final LXSceneNode scene = (LXSceneNode)sm.getUData();

			if (firstReset < structures.size() || forceRecalculation) {

				scene.rotationCenter = sm.getAtomCoordinateAverage();
				scene.bounds = sm.getAtomCoordinateBounds();
				scene.bigX = Math.max(Math.abs(scene.bounds[0][0]), Math
						.abs(scene.bounds[1][0]));
				scene.bigY = Math.max(Math.abs(scene.bounds[0][1]), Math
						.abs(scene.bounds[1][1]));
				scene.bigZ = Math.max(Math.abs(scene.bounds[0][2]), Math
						.abs(scene.bounds[1][2]));
				firstReset++;
			}
			final double maxDistance = Math.sqrt(scene.bigX * scene.bigX
					+ scene.bigY * scene.bigY + scene.bigZ * scene.bigZ);

			// float[] eye = { 0.0, 0.0, maxDistance * 1.4 };
			final double[] eye = { scene.rotationCenter[0],
					scene.rotationCenter[1],
					scene.rotationCenter[2] + maxDistance * 1.4f };
			final double[] up = { 0.0f, 1.0f, 0.0f };
			scene.lookAt(eye, scene.rotationCenter, up);
		}
	}
}
