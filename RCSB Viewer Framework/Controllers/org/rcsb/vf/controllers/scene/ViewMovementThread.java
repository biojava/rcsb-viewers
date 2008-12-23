/*
 * BioJava development code
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence. This should
 * be distributed with the code. If you do not have a copy,
 * see:
 *
 * http://www.gnu.org/copyleft/lesser.html
 *
 * Copyright for this code is held jointly by the individual
 * authors. These should be listed in @author doc comments.
 *
 * For more information on the BioJava project and its aims,
 * or to join the biojava-l mailing list, visit the home page
 * at:
 *
 * http://www.biojava.org/
 *
 * This code was contributed from the Molecular Biology Toolkit
 * (MBT) project at the University of California San Diego.
 *
 * Please reference J.L. Moreland, A.Gramada, O.V. Buzko, Qing
 * Zhang and P.E. Bourne 2005 The Molecular Biology Toolkit (MBT):
 * A Modular Platform for Developing Molecular Visualization
 * Applications. BMC Bioinformatics, 6:21.
 *
 * The MBT project was funded as part of the National Institutes
 * of Health PPG grant number 1-P01-GM63208 and its National
 * Institute of General Medical Sciences (NIGMS) division. Ongoing
 * development for the MBT project is managed by the RCSB
 * Protein Data Bank(http://www.pdb.org) and supported by funds
 * from the National Science Foundation (NSF), the National
 * Institute of General Medical Sciences (NIGMS), the Office of
 * Science, Department of Energy (DOE), the National Library of
 * Medicine (NLM), the National Cancer Institute (NCI), the
 * National Center for Research Resources (NCRR), the National
 * Institute of Biomedical Imaging and Bioengineering (NIBIB),
 * the National Institute of Neurological Disorders and Stroke
 * (NINDS), and the National Institute of Diabetes and Digestive
 * and Kidney Diseases (NIDDK).
 *
 * Created on 2008/12/22
 *
 */ 
package org.rcsb.vf.controllers.scene;

import org.rcsb.uiApp.controllers.app.AppBase;
import org.rcsb.vf.controllers.app.VFAppBase;

public class ViewMovementThread extends Thread
{
	protected static final int delay = 1;
	protected static int duration = 1000;
	public static void setDuration(int in_duration) { duration = Math.max(in_duration, 1000); }
	public static int getDuration() { return duration; }
	
	protected double[] startOrientation;
	protected double[] endOrientation;
	protected double[] startPosition;
	protected double[] endPosition;
	protected double[] startUpArray;
	protected double[] endUpArray;
	
	protected float startFogNear;
	protected float endFogNear;
	
	protected float startFogFar;
	protected float endFogFar;
	
	protected double[] intermediateOrientation;
	protected double[] intermediatePosition;
	protected double[] intermediateUpArray;
	protected float intermediateFogStart = -1;
	protected float intermediateFogEnd = -1;
	
	
	//
	// Beg define singleton and ops
	//
	static protected ViewMovementThread movementThread = null;
	static public void setMovementThread(ViewMovementThread in_movementThread)
	{
		terminateMovementThread();
		movementThread = in_movementThread;
	}
	
	static public ViewMovementThread getMovementThread() { return movementThread; }
	
	static public void terminateMovementThread()
	{
		if (movementThread != null)
			movementThread.terminate();
		movementThread = null;
	}
	
	static public ViewMovementThread createMovementThread(
			double startOrientation[], double endOrientation[],
			double startPosition[], double endPosition[],
			double startUp[], double endUp[],
			float startFogNear, float endFogNear,
			float startFogFar, float endFogFar)
	{
		terminateMovementThread();
		movementThread = new ViewMovementThread(startOrientation, endOrientation, startPosition, endPosition, startUp, endUp,
									  	        startFogNear, endFogNear, startFogFar, endFogFar); 
		return movementThread;
	}
	//
	// End define singleton and ops
	//
	

	/**
	 * Constructor version specifies fog (fog not supported, yet.)
	 * 
	 * @param startOrientation
	 * @param endOrientation
	 * @param startPosition
	 * @param endPosition
	 * @param startUpArray
	 * @param endUpArray
	 * @param startFogStart
	 * @param endFogStart
	 * @param startFogEnd
	 * @param endFogEnd
	 */
	protected ViewMovementThread(final double[] startOrientation, final double[] endOrientation,
			  final double[] startPosition, final double[] endPosition,
			  final double[] startUpArray, final double[] endUpArray,
			  final float startFogStart, final float endFogStart,
			  final float startFogEnd, final float endFogEnd)
	{
		commonConstructor(startOrientation, endOrientation, startPosition, endPosition, startUpArray, endUpArray,
				   startFogStart, endFogStart, startFogEnd, endFogEnd);
	}

	protected void commonConstructor (final double[] startOrientation, final double[] endOrientation,
							  final double[] startPosition, final double[] endPosition,
							  final double[] startUpArray, final double[] endUpArray,
							  final float startFogStart, final float endFogStart,
							  final float startFogEnd, final float endFogEnd)
	{
		this.startOrientation = startOrientation;
		this.endOrientation = endOrientation;
		this.startPosition = startPosition;
		this.endPosition = endPosition;
		this.startUpArray = startUpArray;
		this.endUpArray = endUpArray;
		
		this.startFogNear = startFogStart;
		this.endFogNear = endFogStart;
		this.startFogFar = startFogEnd;
		this.endFogFar = endFogEnd;
		
		intermediateOrientation = new double[3];
		intermediatePosition = new double[3];
		intermediateUpArray = new double[3];
	}
	
	public boolean isTerminated = false;
	public void terminate() {
		isTerminated = true;
	}

	protected boolean isAnimationStarted = false;
	@Override
	public void run()
	{
		long startTime = -1;
		
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
				lookAt(endOrientation, endPosition, endUpArray, endFogNear, endFogFar);
				
				return;
			}
			
			final double stepRatio = (double)curTimeSinceStart / ViewMovementThread.duration;
			
			for(int i = 0; i < startOrientation.length; i++) {
				intermediateOrientation[i] = startOrientation[i] + (endOrientation[i] - startOrientation[i]) * stepRatio;
				intermediatePosition[i] = startPosition[i] + (endPosition[i] - startPosition[i]) * stepRatio;
				intermediateUpArray[i] = startUpArray[i] + (endUpArray[i] - startUpArray[i]) * stepRatio;
				intermediateFogStart = (float)(startFogNear + (endFogNear - startFogNear) * stepRatio);
				intermediateFogEnd = (float)(startFogFar + (endFogFar - startFogFar) * stepRatio);
			}
			
			lookAt(intermediateOrientation, intermediatePosition, intermediateUpArray,
					intermediateFogStart, intermediateFogEnd);
			
			VFAppBase.sgetGlGeometryViewer().requestRepaint();
		}
	}
	
	/**
	 * This is the default implementation - invoke the glViewer version of 'lookAt'.
	 * Note that Ligand Explorer overrides this and uses the LXSceneNode 'lookAt'
	 * <p style="color:red">
	 * Note: Fog not implemented, yet.</p>
	 * 
	 * @param orientation
	 * @param position
	 * @param up
	 */
	public void lookAt(double orientation[], double position[], double up[], float fogStart, float fogEnd)
	{
		VFAppBase.sgetGlGeometryViewer().lookAt(orientation, position, up);
	}
}
