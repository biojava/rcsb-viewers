package org.rcsb.ks.glscene.jogl;

import javax.media.opengl.GLAutoDrawable;

import org.rcsb.vf.glscene.jogl.GlGeometryViewer;

public class KSGlGeometryViewer extends GlGeometryViewer
{	
	private boolean suspendAllPainting = false;
	
	@Override
	public void resetView(boolean forceRecalculation, boolean repaint)
	{
		viewEye[0] = viewEye[1] = 0.0;
		viewEye[2] = 1.0;
		
		viewCenter[0] = viewCenter[1] = viewCenter[2] = 0.0;

		viewUp[0] = viewUp[2] = 0.0;
		viewUp[1] = 1.0;
	}
		
	@Override
	public void display(GLAutoDrawable drawable)
	{
		if (!suspendAllPainting) super.display(drawable);
	}
		
	public void startAnimation()
	{
		this.requestRepaint();
	}

	public void suspendAllPainting() {
		suspendAllPainting = true;
	}

	public void resumePainting() {
		suspendAllPainting = false;
	}
}
