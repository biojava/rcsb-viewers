package org.rcsb.pw.controllers.app;

import org.rcsb.mbt.controllers.app.AppBase;
import org.rcsb.mbt.controllers.app.StateBase;
import org.rcsb.mbt.glscene.jogl.GlGeometryViewer;
import org.rcsb.mbt.glscene.jogl.JoglSceneNode;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class PWState extends StateBase
{
	private class ViewMovementThread extends Thread {
		private static final int delay = 1;
		private static final int duration = 1000;
		
		private double[] startOrientation;
		private double[] endOrientation;
		private double[] startPosition;
		private double[] endPosition;
		private double[] startUpArray;
		private double[] endUpArray;
		
		private double[] intermediateOrientation;
		private double[] intermediatePosition;
		private double[] intermediateUpArray;
		
		public ViewMovementThread(final double[] startOrientation, final double[] endOrientation,
								  final double[] startPosition, final double[] endPosition,
								  final double[] startUpArray, final double[] endUpArray)
		{
			this.startOrientation = startOrientation;
			this.endOrientation = endOrientation;
			this.startPosition = startPosition;
			this.endPosition = endPosition;
			this.startUpArray = startUpArray;
			this.endUpArray = endUpArray;
			
			this.intermediateOrientation = new double[3];
			this.intermediatePosition = new double[3];
			this.intermediateUpArray = new double[3];

/* **
			AppBase.sgetGlGeometryViewer().setHasRenderedFlag();
* **/
			
		}
		
		public boolean isTerminated = false;
		public void terminate() {
			this.isTerminated = true;
		}

		private boolean isAnimationStarted = false;
		@Override
		public void run() {
			long startTime = -1;
			final GlGeometryViewer glViewer = AppBase.sgetGlGeometryViewer();
			
			while(true) {
				try {
					Thread.sleep(ViewMovementThread.delay);
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}

				if(!this.isAnimationStarted) {
					this.isAnimationStarted = true;
					startTime = System.currentTimeMillis();
				}
				
				if(this.isTerminated) {
					return;
				}
				
				final long curTimeSinceStart = System.currentTimeMillis() - startTime;
				if(curTimeSinceStart >= ViewMovementThread.duration) {
					glViewer.lookAt(this.endOrientation, this.endPosition, this.endUpArray);
					
					return;
				}
				
				final double stepRatio = (double)curTimeSinceStart / ViewMovementThread.duration;
				
				for(int i = 0; i < this.startOrientation.length; i++) {
					this.intermediateOrientation[i] = this.startOrientation[i] + (this.endOrientation[i] - this.startOrientation[i]) * stepRatio;
					this.intermediatePosition[i] = this.startPosition[i] + (this.endPosition[i] - this.startPosition[i]) * stepRatio;
					this.intermediateUpArray[i] = this.startUpArray[i] + (this.endUpArray[i] - this.startUpArray[i]) * stepRatio;
				}
				
				glViewer.lookAt(this.intermediateOrientation, this.intermediatePosition, this.intermediateUpArray);
			}
		}
	}
	
	private static ViewMovementThread movementThread = null;
	@Override
	protected void enactViewerOptions() {
		final GlGeometryViewer glViewer = AppBase.sgetGlGeometryViewer();
		final JoglSceneNode node = (JoglSceneNode)AppBase.sgetModel().getStructures().get(0).getStructureMap().getUData();
		
		try {
			final NodeList viewpoints = this.document.getElementsByTagName("Viewpoint");
			if(viewpoints != null && viewpoints.getLength() != 0) {
				final Element viewpoint = (Element)viewpoints.item(0);
				
	//			String name = viewpoint.getAttribute("name");
				final String orientation = viewpoint.getAttribute("orientation");
				final String position = viewpoint.getAttribute("position");
				final String up = viewpoint.getAttribute("up");
				
				if(orientation != null && position != null && up != null) {
					final String[] orientationSplit = this.spaces.split(orientation);
					final String[] positionSplit = this.spaces.split(position);
					final String[] upSplit = this.spaces.split(up);
					
					if(upSplit.length >= 3 && orientationSplit.length >= 3 && positionSplit.length >= 3) {
						final double[] orientationArray = new double[orientationSplit.length];
						final double[] positionArray = new double[positionSplit.length];
						final double[] upArray = new double[upSplit.length];
						
						for(int i = 0; i < orientationSplit.length; i++) {
							orientationArray[i] = Double.parseDouble(orientationSplit[i]);
							positionArray[i] = Double.parseDouble(positionSplit[i]);
							upArray[i] = Double.parseDouble(upSplit[i]);
						}
						

						final double[] currentOrientation = glViewer.getEye();
						final double[] currentPosition = glViewer.getCenter();
						final double[] currentUp = glViewer.getUp();
//						viewer.lookAt(orientationArray, positionArray, upArray);
						
						if(movementThread != null) {
							movementThread.terminate();
						}
						
						movementThread = new ViewMovementThread(currentOrientation, orientationArray, currentPosition, positionArray, currentUp, upArray);
						movementThread.start();
					}
				}
			}
		} catch(final Exception e) {}
		
		try {
			final NodeList backgrounds = this.document.getElementsByTagName("Background");
			if(backgrounds != null && backgrounds.getLength() != 0) {
				final Element background = (Element)backgrounds.item(0);
				
				final String color = background.getAttribute("color");
				
				if(color != null) {
					final String[] colorSplit = this.spaces.split(color);
					
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
