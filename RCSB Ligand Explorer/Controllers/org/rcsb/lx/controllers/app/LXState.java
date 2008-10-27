package org.rcsb.lx.controllers.app;

import org.rcsb.lx.controllers.app.LigandExplorer;
import org.rcsb.lx.glscene.jogl.LXSceneNode;
import org.rcsb.lx.model.LXModel;
import org.rcsb.mbt.controllers.app.StateBase;
import org.rcsb.mbt.glscene.jogl.GlGeometryViewer;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class LXState extends StateBase
{
	Element environment;
	Element fog;
	
	@Override
	protected void appendDocElements()
	{
		environment = appendChild(document, structureViewer, "Environment");
		fog = appendChild(document, environment, "Fog");		
	}
	
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
	
	public static class ViewMovementThread extends Thread {
		private static final int delay = 1;
		private static final int duration = 1000;
		
		private double[] startOrientation;
		private double[] endOrientation;
		private double[] startPosition;
		private double[] endPosition;
		private double[] startUpArray;
		private double[] endUpArray;
		
		private float startFogStart;
		private float endFogStart;
		
		private float startFogEnd;
		private float endFogEnd;
		
		private double[] intermediateOrientation;
		private double[] intermediatePosition;
		private double[] intermediateUpArray;
		private float intermediateFogStart = -1;
		private float intermediateFogEnd = -1;
		
		public ViewMovementThread(final double[] startOrientation, final double[] endOrientation, final double[] startPosition, final double[] endPosition, final double[] startUpArray, final double[] endUpArray, final float startFogStart, final float endFogStart, final float startFogEnd, final float endFogEnd) {
			this.startOrientation = startOrientation;
			this.endOrientation = endOrientation;
			this.startPosition = startPosition;
			this.endPosition = endPosition;
			this.startUpArray = startUpArray;
			this.endUpArray = endUpArray;
			
			this.startFogStart = startFogStart;
			this.endFogStart = endFogStart;
			this.startFogEnd = startFogEnd;
			this.endFogEnd = endFogEnd;
			
			intermediateOrientation = new double[3];
			intermediatePosition = new double[3];
			intermediateUpArray = new double[3];
		}
		
		public boolean isTerminated = false;
		public void terminate() {
			isTerminated = true;
		}

		private boolean isAnimationStarted = false;
		@Override
		public void run()
		{
			long startTime = -1;
			final LXModel model = LigandExplorer.sgetModel();
			final LXSceneNode node = (LXSceneNode)model.getStructures().get(0).getStructureMap().getUData();
			final GlGeometryViewer glViewer = LigandExplorer.sgetGlGeometryViewer();
			
			while(true) {
				try {
					Thread.sleep(ViewMovementThread.delay);
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}
				
				if(!isAnimationStarted) {
					isAnimationStarted = true;
					startTime = System.currentTimeMillis();
				}
				
				if(isTerminated) {
					return;
				}
				
				final long curTimeSinceStart = System.currentTimeMillis() - startTime;
				if(curTimeSinceStart >= ViewMovementThread.duration)
				{
					node.lookAt(endOrientation, endPosition, endUpArray);
					
					return;
				}
				
				final double stepRatio = (double)curTimeSinceStart / ViewMovementThread.duration;
				
				for(int i = 0; i < startOrientation.length; i++) {
					intermediateOrientation[i] = startOrientation[i] + (endOrientation[i] - startOrientation[i]) * stepRatio;
					intermediatePosition[i] = startPosition[i] + (endPosition[i] - startPosition[i]) * stepRatio;
					intermediateUpArray[i] = startUpArray[i] + (endUpArray[i] - startUpArray[i]) * stepRatio;
					intermediateFogStart = (float)(startFogStart + (endFogStart - startFogStart) * stepRatio);
					intermediateFogEnd = (float)(startFogEnd + (endFogEnd - startFogEnd) * stepRatio);
				}
				
				node.lookAt(intermediateOrientation, intermediatePosition, intermediateUpArray);
				node.fogStart = intermediateFogStart;
				node.fogEnd = intermediateFogEnd;
				
				glViewer.requestRepaint();
			}
		}
	}
	
	public static ViewMovementThread movementThread = null;
	@Override
	protected void enactViewerOptions() {
		final LXModel model = LigandExplorer.sgetModel();
		final LXSceneNode node = (LXSceneNode)model.getStructures().get(0).getStructureMap().getUData();
		final GlGeometryViewer glViewer = LigandExplorer.sgetGlGeometryViewer();
		
		try {
			final NodeList viewpoints = document.getElementsByTagName("Viewpoint");
			final NodeList fogs = document.getElementsByTagName("Fog");
			if(viewpoints != null && viewpoints.getLength() != 0 && fogs != null && fogs.getLength() != 0) {
				final Element viewpoint = (Element)viewpoints.item(0);
				final Element fog = (Element)fogs.item(0);
				
	//			String name = viewpoint.getAttribute("name");
				final String orientation = viewpoint.getAttribute("orientation");
				final String position = viewpoint.getAttribute("position");
				final String up = viewpoint.getAttribute("up");
				
				final String fogEnabledSt = fog.getAttribute("is_enabled");
				final String fogStartSt = fog.getAttribute("start");
				final String fogEndSt = fog.getAttribute("end");
				if(orientation != null && position != null && up != null && fogEnabledSt != null && fogEndSt != null && fogStartSt != null) {
					final String[] orientationSplit = spaces.split(orientation);
					final String[] positionSplit = spaces.split(position);
					final String[] upSplit = spaces.split(up);
					
					if(upSplit.length >= 3 && orientationSplit.length >= 3 && positionSplit.length >= 3) {
						final double[] orientationArray = new double[orientationSplit.length];
						final double[] positionArray = new double[positionSplit.length];
						final double[] upArray = new double[upSplit.length];
						
						for(int i = 0; i < orientationSplit.length; i++) {
							orientationArray[i] = Double.parseDouble(orientationSplit[i]);
							positionArray[i] = Double.parseDouble(positionSplit[i]);
							upArray[i] = Double.parseDouble(upSplit[i]);
						}
						
//						if(movementThread != null && movementThread.isRunning()) {
//							movementThread.stop();
//						}
						
						final boolean newIsFogEnabled = fogEnabledSt.equals("true");
						final float newFogStart = Float.parseFloat(fogStartSt);
						final float newFogEnd = Float.parseFloat(fogEndSt);
						
						node.isFogEnabled = newIsFogEnabled;	// just set this - no reason to transition to the new state, etc.
						final float curFogStart = node.fogStart;
						final float curFogEnd = node.fogEnd;
						
						final double[] currentOrientation = node.getEye();
						final double[] currentPosition = node.getCenter();
						final double[] currentUp = node.getUp();
//						viewer.lookAt(orientationArray, positionArray, upArray);
						
						if(movementThread != null) {
							movementThread.terminate();
						}
						
						movementThread = new ViewMovementThread(currentOrientation, orientationArray, currentPosition, positionArray, currentUp, upArray, curFogStart, newFogStart, curFogEnd, newFogEnd);
						movementThread.start();
					}
				}
			}
		} catch(final Exception e) {}
		
		try {
			final NodeList backgrounds = document.getElementsByTagName("Background");
			if(backgrounds != null && backgrounds.getLength() != 0) {
				final Element background = (Element)backgrounds.item(0);
				
				final String color = background.getAttribute("color");
				
				if(color != null) {
					final String[] colorSplit = spaces.split(color);
					
					if(colorSplit.length >= 3) {
						final float[] colorArray = new float[colorSplit.length];
						
						for(int i = 0; i < colorSplit.length; i++) {
							colorArray[i] = Float.parseFloat(colorSplit[i]);
						}
						
						glViewer.setBackgroundColor(colorArray[0], colorArray[1], colorArray[2], 1);
					}
				}
			}
		} catch(final Exception e) {}
	}
}
