package org.rcsb.lx.controllers.scene;

import org.rcsb.lx.controllers.app.LigandExplorer;
import org.rcsb.lx.glscene.jogl.LXSceneNode;
import org.rcsb.lx.model.LXModel;
import org.rcsb.vf.controllers.scene.ViewMovementThread;

public class LXViewMovementThread extends ViewMovementThread
{
	static public ViewMovementThread createMovementThread(
			double startOrientation[], double endOrientation[],
			double startPosition[], double endPosition[],
			double startUp[], double endUp[],
			float startFogNear, float endFogNear,
			float startFogFar, float endFogFar)
	{
		terminateMovementThread();
		movementThread = new LXViewMovementThread(startOrientation, endOrientation, startPosition, endPosition, startUp, endUp,
									  	        startFogNear, endFogNear, startFogFar, endFogFar); 
		return movementThread;
	}
	
	protected LXViewMovementThread(double[] startOrientation,
			double[] endOrientation, double[] startPosition,
			double[] endPosition, double[] startUpArray, double[] endUpArray,
			float startFogStart, float endFogStart, float startFogEnd,
			float endFogEnd)
	{
		super(startOrientation, endOrientation, startPosition, endPosition,
				startUpArray, endUpArray, startFogStart, endFogStart, startFogEnd,
				endFogEnd);
	}

	public void lookAt(double orientation[], double position[], double up[], float fogStart, float fogEnd)
	{
		LXModel model = LigandExplorer.sgetModel();
		final LXSceneNode node = (LXSceneNode)model.getStructures().get(0).getStructureMap().getUData();

		node.lookAt(orientation, position, up);
	}
}
