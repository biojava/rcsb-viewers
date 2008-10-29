package org.rcsb.lx.controllers.scene;

import org.rcsb.lx.controllers.app.LigandExplorer;
import org.rcsb.lx.glscene.jogl.LXSceneNode;
import org.rcsb.lx.model.LXModel;
import org.rcsb.mbt.controllers.app.AppBase;
import org.rcsb.mbt.controllers.scene.SceneState;
import org.rcsb.mbt.glscene.jogl.GlGeometryViewer;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class LXSceneState extends SceneState
{	
	@Override
	protected void setViewerInfo()
	{
		LXModel model = LigandExplorer.sgetModel();
		final LXSceneNode node = (LXSceneNode)model.getStructures().get(0).getStructureMap().getUData();
		setViewerInfo(node.getEye(), node.getCenter(), node.getUp());
		fog.setAttribute("is_enabled", node.isFogEnabled ? "true" : "false");
		fog.setAttribute("start", node.fogStart + "");
		fog.setAttribute("end", node.fogEnd + "");
	}
	
	@Override
	protected void fillLookInfo()
	{
		LXModel model = LigandExplorer.sgetModel();
		final LXSceneNode node = (LXSceneNode)model.getStructures().get(0).getStructureMap().getUData();
		
		currentOrientation = node.getEye();
		currentPosition = node.getCenter();
		currentUp = node.getUp();
		
		curFogStart = node.fogStart;
		curFogEnd = node.fogEnd;
	}

}
