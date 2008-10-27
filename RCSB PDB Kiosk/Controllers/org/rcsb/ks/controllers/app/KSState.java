package org.rcsb.ks.controllers.app;

import org.rcsb.ks.glscene.jogl.KSGlGeometryViewer;
import org.rcsb.mbt.controllers.app.StateBase;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class KSState extends StateBase
{
	private class ViewMovementThread extends Thread
	{
		private static final int delay = 1;
		private static final int duration = 5000;
		private double[] startOrientation;
		private double[] endOrientation;
		private double[] startPosition;
		private double[] endPosition;
		private double[] startUpArray;
		private double[] endUpArray;
		private double[] intermediateOrientation;
		private double[] intermediatePosition;
		private double[] intermediateUpArray;

		public ViewMovementThread(double[] startOrientation,
				double[] endOrientation, double[] startPosition,
				double[] endPosition, double[] startUpArray, double[] endUpArray) {
			this.startOrientation = startOrientation;
			this.endOrientation = endOrientation;
			this.startPosition = startPosition;
			this.endPosition = endPosition;
			this.startUpArray = startUpArray;
			this.endUpArray = endUpArray;

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
		public void run() {
			long startTime = -1;
			KSGlGeometryViewer glViewer = KioskViewer.sgetGlGeometryViewer();

			while (true) {
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				if (!isAnimationStarted) {
					isAnimationStarted = true;
					startTime = System.currentTimeMillis();
				}

				if (isTerminated) {
					return;
				}

				long curTimeSinceStart = System.currentTimeMillis() - startTime;
				if (curTimeSinceStart >= duration) {
					glViewer.lookAt(endOrientation, endPosition,
							endUpArray);

					return;
				}

//				double stepRatio = 1.0;//(double) curTimeSinceStart / duration;
				double stepRatio = (double) curTimeSinceStart / duration;

				for (int i = 0; i < startOrientation.length; i++)
				{					
					intermediateOrientation[i] = startOrientation[i]
							+ (endOrientation[i] - startOrientation[i])
							* stepRatio;

					intermediatePosition[i] = startPosition[i]
							+ (endPosition[i] - startPosition[i])
							* stepRatio;
					intermediateUpArray[i] = startUpArray[i]
							+ (endUpArray[i] - startUpArray[i])
							* stepRatio;
				}

				glViewer.lookAt(intermediateOrientation,
						intermediatePosition, intermediateUpArray);
			}
		}
	}

	private static ViewMovementThread movementThread = null;

	@Override
	protected void enactViewerOptions()
	{
		KSGlGeometryViewer glViewer = KioskViewer.sgetGlGeometryViewer();

		try
		{
			NodeList viewpoints = document
					.getElementsByTagName("Viewpoint");
			if (viewpoints != null && viewpoints.getLength() != 0) {
				Element viewpoint = (Element) viewpoints.item(0);

				// String name = viewpoint.getAttribute("name");
				String orientation = viewpoint.getAttribute("orientation");
				String position = viewpoint.getAttribute("position");
				String up = viewpoint.getAttribute("up");
				if (orientation != null && position != null && up != null) {
					String[] orientationSplit = spaces.split(orientation);
					String[] positionSplit = spaces.split(position);
					String[] upSplit = spaces.split(up);

					if (upSplit.length >= 3 && orientationSplit.length >= 3
							&& positionSplit.length >= 3) {
						double[] orientationArray = new double[orientationSplit.length];
						double[] positionArray = new double[positionSplit.length];
						double[] upArray = new double[upSplit.length];

						for (int i = 0; i < orientationSplit.length; i++) {
							orientationArray[i] = Double
									.parseDouble(orientationSplit[i]);
							positionArray[i] = Double
									.parseDouble(positionSplit[i]);
							upArray[i] = Double.parseDouble(upSplit[i]);
						}

						// if(movementThread != null &&
						// movementThread.isRunning()) {
						// movementThread.stop();
						// }

						double[] currentOrientation = glViewer.getEye();
						double[] currentPosition = glViewer.getCenter();//positionArray;//viewer.getCenter();
						double[] currentUp = glViewer.getUp();
						// viewer.lookAt(orientationArray, positionArray,
						// upArray);

						if (movementThread != null) {
							movementThread.terminate();
						}

						movementThread = new ViewMovementThread(
								currentOrientation, orientationArray,
								currentPosition, positionArray, currentUp,
								upArray);
						movementThread.start();
					}
				}
			}
		} catch (Exception e) {
		}

		try {
			NodeList backgrounds = document
					.getElementsByTagName("Background");
			if (backgrounds != null && backgrounds.getLength() != 0) {
				Element background = (Element) backgrounds.item(0);

				String color = background.getAttribute("color");

				if (color != null) {
					String[] colorSplit = spaces.split(color);

					if (colorSplit.length >= 3) {
						float[] colorArray = new float[colorSplit.length];

						for (int i = 0; i < colorSplit.length; i++) {
							colorArray[i] = Float.parseFloat(colorSplit[i]);
						}

// XXX						viewer.setBackgroundColor(colorArray[0], colorArray[1],
// XXX								colorArray[2], 1);
					}
				}
			}
		} catch (Exception e) {
		}
	}
}
