package org.rcsb.lx.glscene.jogl;

import java.util.Collection;
import java.util.Iterator;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.rcsb.lx.controllers.app.LigandExplorer;
import org.rcsb.lx.model.Interaction;
import org.rcsb.mbt.controllers.app.AppBase;
import org.rcsb.mbt.controllers.scene.SceneController;
import org.rcsb.mbt.glscene.jogl.Constants;
import org.rcsb.mbt.glscene.jogl.DisplayListRenderable;
import org.rcsb.mbt.glscene.jogl.GlGeometryViewer;
import org.rcsb.mbt.glscene.jogl.JoglSceneNode;
import org.rcsb.mbt.model.Atom;
import org.rcsb.mbt.model.Chain;
import org.rcsb.mbt.model.Fragment;
import org.rcsb.mbt.model.Residue;
import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.StructureComponent;
import org.rcsb.mbt.model.StructureMap;
import org.rcsb.mbt.model.attributes.ChainStyle;
import org.rcsb.mbt.model.geometry.Algebra;
import org.rcsb.mbt.model.geometry.TransformationMatrix;

import com.sun.opengl.util.GLUT;


public class LXSceneNode extends JoglSceneNode
{
	////////////////////////////////////////////////////////////////////
	// BEG Fog implementation
	////////////////////////////////////////////////////////////////////
	
	public double[] viewEye = { 0d, 0d, 0d };

	public double[] viewCenter = { 0d, 0d, 0d };

	public double[] viewUp = { 0d, 0d, 0d };

	public double[] rotationCenter = { 0d, 0d, 0d };

	public double[] scale = { 0d, 0d, 0d };

	public double bigX = -1;

	public double bigY = -1;

	public double bigZ = -1;

	public double[][] bounds = null;

	public float fogEnd = -1;

	public float fogStart = -1;

	public boolean isFogEnabled = false;

	public boolean requestFogChange = false;

	public boolean isInConstantFogMode = false;

	public float constantFogValue = 0;

	public int fogEquation = GL.GL_LINEAR;

	public float fogDensity = 0;

	/**
	 * Causes the node to appear faded, using fog. Does not cause a repaint -
	 * you must repaint the drawable separately.
	 * 
	 * @param faded
	 */
	public void setFaded(final boolean faded)
	{
		if (faded)
		{
			// fogDensity = 1.2f;
			// constantFogValue = 0.00000001f;
			this.fogEnd = 500f;
			this.fogStart = 1f;
			this.isInConstantFogMode = false;
			this.isFogEnabled = true;
			this.requestFogChange = true;
			this.fogEquation = GL.GL_LINEAR; // the computationally simplest
			// algorithm.
		}
		
		else
		{
			this.isFogEnabled = false;
			this.requestFogChange = true;
			this.fogEquation = GL.GL_LINEAR; // the best looking algorithm.
		}
	}
	////////////////////////////////////////////////////////////////////
	// END Fog implementation
	// BEG Viewpoint implementation
	////////////////////////////////////////////////////////////////////

	/**
	 * Move the camera to the specified eye point and orient the view toward the
	 * specified 3D center point with a given up vector. This provides complete
	 * camera control.
	 */
	public void lookAt(final double[] eye, final double[] center,
			final double[] up) {
		// Set the new eye location.
		this.viewEye[0] = eye[0];
		this.viewEye[1] = eye[1];
		this.viewEye[2] = eye[2];

		// final double zoomDistance = Algebra.distance(this.viewCenter,
		// center);

		// Set the new view center point.
		this.viewCenter[0] = center[0];
		this.viewCenter[1] = center[1];
		this.viewCenter[2] = center[2];

		// Set the new rotation center point.
		this.rotationCenter[0] = center[0];
		this.rotationCenter[1] = center[1];
		this.rotationCenter[2] = center[2];

		// Set the new view up vector.
		this.viewUp[0] = up[0];
		this.viewUp[1] = up[1];
		this.viewUp[2] = up[2];

		// double sign = viewCenter[0] + viewCenter[1] + viewCenter[2] <
		// center[0] + center[1] + center[2] ? -1 : 1;
		// zoomDistance *= sign;
		// fogStart += zoomDistance;
		// fogEnd += zoomDistance;

		// Model.getSingleton().getViewer().requestRepaint();
	}

	public void lookAt(final float[] eye, final float[] center, final float[] up) {
		// Set the new eye location.
		this.viewEye[0] = eye[0];
		this.viewEye[1] = eye[1];
		this.viewEye[2] = eye[2];

		// Set the new view center point.
		this.viewCenter[0] = center[0];
		this.viewCenter[1] = center[1];
		this.viewCenter[2] = center[2];
		// Set the new rotation center point.
		this.rotationCenter[0] = center[0];
		this.rotationCenter[1] = center[1];
		this.rotationCenter[2] = center[2];

		// Set the new view up vector.
		this.viewUp[0] = up[0];
		this.viewUp[1] = up[1];
		this.viewUp[2] = up[2];

		// Model.getSingleton().getViewer().requestRepaint();
	}

	/**
	 * Rotate and refocus the camera at the specified 3D point, but keep the
	 * current eye location and up orientation of the camera. This provides a
	 * "turn your head and eyes toward an object" behavior. The new up-vector is
	 * computed automatically.
	 */
	public void lookAt(final double[] center) {
		this.lookAt(this.viewEye, center);
	}

	public void lookAt(final float[] center) {
		this.lookAt(this.viewEye, center);
	}

	/**
	 * Move the camera to the specified eye point and orient the view toward the
	 * specified 3D center point. This provides a "move your body to a point in
	 * space and look at an object" behavior. The new up-vector is computed
	 * automatically.
	 */
	public void lookAt(final double[] eye, final double[] center) {
		if (eye.length < 3) {
			throw new IllegalArgumentException("eye.length < 3");
		}
		if (center.length < 3) {
			throw new IllegalArgumentException("center.length < 3");
		}

		// We need to compute a new viewUp and but we need to ensure that it
		// is both perpendicular (orthonormal) to the new viewDirection and
		// also oriented in a "similar" direction to the old viewUp.
		// To do so, we project one vector onto the other and subtract
		// (Graham-Schmidt orthogonalization).

		final double newViewDirection[] = { center[0] - eye[0],
				center[1] - eye[1], center[2] - eye[2] };
		Algebra.normalizeVector(newViewDirection);

		// Use Graham-Schmidt orthogonalization: e3 = e2 - e1 * ( e2 DOT e1 )
		// to produce a newViewUp vector that is orthonormal to the
		// new view direction by starting with the old viewUp vector:
		//
		// e1 = newViewDirection
		// e2 = viewUp
		// e3 = newViewUp
		//
		// Note that the dot product of two orthonormal vectors is 0,
		// so it is safe to re-orthogonalize since the orthogonal vector
		// component will be the zero vector and you will be left with the
		// original input vector.
		Algebra.normalizeVector(this.viewUp);
		final double dot = Algebra.dotProduct(this.viewUp, newViewDirection);
		final double newViewUp[] = {
				this.viewUp[0] - newViewDirection[0] * dot,
				this.viewUp[1] - newViewDirection[1] * dot,
				this.viewUp[2] - newViewDirection[2] * dot };
		Algebra.normalizeVector(newViewUp);

		// Set the new eye location.
		this.viewEye[0] = eye[0];
		this.viewEye[1] = eye[1];
		this.viewEye[2] = eye[2];

		// Set the new view center point.
		this.viewCenter[0] = center[0];
		this.viewCenter[1] = center[1];
		this.viewCenter[2] = center[2];
		// Set the new rotation center point.
		this.rotationCenter[0] = center[0];
		this.rotationCenter[1] = center[1];
		this.rotationCenter[2] = center[2];

		// Set the new view up vector.
		this.viewUp[0] = newViewUp[0];
		this.viewUp[1] = newViewUp[1];
		this.viewUp[2] = newViewUp[2];

		// Model.getSingleton().getViewer().requestRepaint();
	}

	/**
	 * Returns the array describing the camera's orientation in the scene.
	 * Changing the contents of this array will have an immediate effect on the
	 * camera.
	 */
	public double[] getEye() {
		final double[] eye = new double[this.viewEye.length];
		for (int i = 0; i < eye.length; i++) {
			eye[i] = this.viewEye[i];
		}

		return eye;
	}

	/**
	 * Returns the array describing the camera's position in the scene. Changing
	 * the contents of this array will have an immediate effect on the camera.
	 */
	public double[] getCenter() {
		final double[] center = new double[this.viewCenter.length];
		for (int i = 0; i < center.length; i++) {
			center[i] = this.viewCenter[i];
		}

		return center;
	}

	/**
	 * Returns the array describing the camera's up vector in the scene.
	 * Changing the contents of this array will have an immediate effect on the
	 * camera.
	 */
	public double[] getUp() {
		final double[] up = new double[this.viewUp.length];
		for (int i = 0; i < up.length; i++) {
			up[i] = this.viewUp[i];
		}

		return up;
	}

	public void lookAt(final double[] eye, final float[] center) {
		if (eye.length < 3) {
			throw new IllegalArgumentException("eye.length < 3");
		}
		if (center.length < 3) {
			throw new IllegalArgumentException("center.length < 3");
		}

		// We need to compute a new viewUp and but we need to ensure that it
		// is both perpendicular (orthonormal) to the new viewDirection and
		// also oriented in a "similar" direction to the old viewUp.
		// To do so, we project one vector onto the other and subtract
		// (Graham-Schmidt orthogonalization).

		final double newViewDirection[] = { center[0] - eye[0],
				center[1] - eye[1], center[2] - eye[2] };
		Algebra.normalizeVector(newViewDirection);

		// Use Graham-Schmidt orthogonalization: e3 = e2 - e1 * ( e2 DOT e1 )
		// to produce a newViewUp vector that is orthonormal to the
		// new view direction by starting with the old viewUp vector:
		//
		// e1 = newViewDirection
		// e2 = viewUp
		// e3 = newViewUp
		//
		// Note that the dot product of two orthonormal vectors is 0,
		// so it is safe to re-orthogonalize since the orthogonal vector
		// component will be the zero vector and you will be left with the
		// original input vector.
		Algebra.normalizeVector(this.viewUp);
		final double dot = Algebra.dotProduct(this.viewUp, newViewDirection);
		final double newViewUp[] = {
				this.viewUp[0] - newViewDirection[0] * dot,
				this.viewUp[1] - newViewDirection[1] * dot,
				this.viewUp[2] - newViewDirection[2] * dot };
		Algebra.normalizeVector(newViewUp);

		// Set the new eye location.
		this.viewEye[0] = eye[0];
		this.viewEye[1] = eye[1];
		this.viewEye[2] = eye[2];

		// Set the new view center point.
		this.viewCenter[0] = center[0];
		this.viewCenter[1] = center[1];
		this.viewCenter[2] = center[2];
		// Set the new rotation center point.
		this.rotationCenter[0] = center[0];
		this.rotationCenter[1] = center[1];
		this.rotationCenter[2] = center[2];

		// Set the new view up vector.
		this.viewUp[0] = newViewUp[0];
		this.viewUp[1] = newViewUp[1];
		this.viewUp[2] = newViewUp[2];

		// Model.getSingleton().getViewer().requestRepaint();
	}
	////////////////////////////////////////////////////////////////////
    // END viewpoint implementation
	////////////////////////////////////////////////////////////////////

	// -----------------------------------------------------------------------------

	public LXSceneNode()
	{
		allowLighting = true;
	}
	
	private final double[] tempMidpoint = { 0, 0, 0 };

	/**
	 * Completely hides base implementation.
	 */
	@Override
	public boolean draw(final GL gl, final GLU glu, final GLUT glut, final boolean isPick, final Structure struc)
	{
		if (Double.isNaN(this.viewEye[0]) || Double.isNaN(this.viewEye[1])
				|| Double.isNaN(this.viewEye[2])
				|| Double.isNaN(this.viewCenter[0])
				|| Double.isNaN(this.viewCenter[1])
				|| Double.isNaN(this.viewCenter[2])
				|| Double.isNaN(this.viewUp[0]) || Double.isNaN(this.viewUp[1])
				|| Double.isNaN(this.viewUp[2])
				|| Double.isNaN(this.rotationCenter[0])
				|| Double.isNaN(this.rotationCenter[1])
				|| Double.isNaN(this.rotationCenter[2]))
			LigandExplorer.sgetGlGeometryViewer().resetView(false, false);
	
		createLabels(gl, glut);
		
		// Set the viewpoint.
		glu.gluLookAt(this.viewEye[0], this.viewEye[1], this.viewEye[2],
				this.viewCenter[0], this.viewCenter[1], this.viewCenter[2],
				this.viewUp[0], this.viewUp[1], this.viewUp[2]);

		return innerDraw(gl, glu, glut, isPick, struc, null);
	}

	//
	// Scene content methods.
	//
	public boolean isRendered(final Object component)
	{
		synchronized (this.renderables)
		{
			return this.renderables.containsKey(component);
		}
	}


	/**
	 * Completely hides base implementation.
	 * 
	 * I'm not sure why I have to do this - it's virtually identical to the base implementation,
	 * with the exception that the base implementation is sensitized to BiologicUnit transformations.
	 * 
	 * I can turn those off with 'Appbase.setDisableGlobalTransformations(true)', which almost works,
	 * but the colors don't come out right.
	 * 
	 * So, this seems to be as close as I can get it.  Not good, because there is a considerable amount
	 * of duplicate code, here.  The only other thing I can think of is that either this is referencing
	 * something the base is not, or there's something in the 'synchronized' execution that isn't
	 * happening if I try to use the base version.
	 * 
	 * 31-Jul-08 - rickb
	 */
	@Override
	protected boolean innerDraw(final GL gl, final GLU glu, final GLUT glut, boolean isPick,
			final Structure struc, final TransformationMatrix nc_transform)
	{
		gl.glPushMatrix();

		final GlGeometryViewer glViewer = AppBase.sgetGlGeometryViewer();

		if (nc_transform != null)
		{
			nc_transform.transformationMatrix.rewind();
			gl.glMultMatrixf(nc_transform.transformationMatrix);
		}

		synchronized (this.renderables)
		{
			for (final DisplayListRenderable renderable : renderables.values())
			{
				renderable.draw(gl, glu, glut, isPick);

				// pick cycles do not need to finish, and paints get priority.
				if (isPick && glViewer.needsRepaint) {
					gl.glPopMatrix(); // make sure we clean up the stack...
					return false;
				}
			}
		}

		if (!isPick)
		{
			synchronized (this.labels)
			{
				final Collection values = this.labels.values();
				if (!values.isEmpty()) {
					gl.glDisable(GL.GL_DEPTH_TEST);

					if (LXGlGeometryViewer.currentProgram != 0)
						gl.glUseProgram(0);

					gl.glDisable(GL.GL_LIGHTING);

					final Iterator it = this.labels.values().iterator();
					final Iterator keyIt = this.labels.keySet().iterator();
					while (it.hasNext()) {
						final Object[] tmp = (Object[]) it.next();
						final Integer label = (Integer) tmp[0];
						final float[] color = (float[]) tmp[1];
						gl.glMaterialfv(GL.GL_FRONT, GL.GL_EMISSION, color, 0);

						final Object key = keyIt.next();
						drawTypeLabels(gl, key, label);
					}

					gl.glEnable(GL.GL_DEPTH_TEST);

					if (GlGeometryViewer.currentProgram != 0)
						gl.glUseProgram(GlGeometryViewer.currentProgram);

					gl.glEnable(GL.GL_LIGHTING);
				}
			}
		}

		gl.glPopMatrix();
		return true;
	}
	
	@Override
	public void registerLabel(final StructureComponent sc,
			final Integer displayList, final boolean isDisplayListShared,
			final float[] color)
	{
		synchronized (this.labels)
		{
			this.labels.put(sc,
				new Object[]
			    {
					displayList,
					color,
					isDisplayListShared ? Boolean.TRUE : Boolean.FALSE
				});
		}
	}

	@Override
	public void removeLabelAndDeallocate(final Object sc)
	{
		Integer list = null;
		synchronized (this.labels)
		{
			final Object[] tmp = this.labels.remove(sc);
			list = (Integer) tmp[0];
		}

		if (list != null)
		{
			LXGlGeometryViewer glViewer = LigandExplorer.sgetGlGeometryViewer();
			synchronized (glViewer.simpleDisplayListsToDestroy)
			{
				glViewer.simpleDisplayListsToDestroy.add(list);
			}
		}
	}

	/**
	 * The base version of createAndAddLabel uses 'atomToLabel', whereas this uses
	 * 'residueToLabel'.  So, we need to keep them differentiated, at the moment.
	 * This replaces, but does not override the base version for the Ligand Explorer
	 * 
	 * @param sc
	 *            may be a Residue, Fragment, or a set of Residues. Atoms should
	 *            be labelled via their AtomStyle instance. Residue array is
	 *            assumed to be physically contiguous and in physical order.
	 */
	public void lxCreateAndAddLabel(final Object sc, final String label, final float[] color_)
	{
		Residue residueToLabel = null;
		float[] color = color_;

		if (sc instanceof Residue)
		{
			residueToLabel = (Residue) sc;
		}
		
		else if (sc instanceof Residue[])
		{
			final Residue[] reses = (Residue[]) sc;
			final Residue r = reses[reses.length / 2];
			residueToLabel = r;
			if (color == null) {
				final StructureMap sm = r.getStructure().getStructureMap();
				final Chain c = sm.getChain(r.getChainId());
				color = new float[4];
				final ChainStyle style = (ChainStyle) sm.getStructureStyles()
						.getStyle(c);
				style.getResidueColor(r, color);
			}
		}
		
		else if (sc instanceof Fragment)
		{
			final Fragment frag = (Fragment) sc;
			final Residue middleRes = frag.getResidue(frag.getResidueCount() / 2);
			residueToLabel = middleRes;

			if (color == null) {
				final StructureMap sm = middleRes.getStructure().getStructureMap();
				final Chain c = sm.getChain(middleRes.getChainId());
				color = new float[4];
				final ChainStyle style = (ChainStyle) sm.getStructureStyles()
						.getStyle(c);
				style.getResidueColor(middleRes, color);
			}
		}
		
		else if (sc instanceof Chain)
		{
			final Chain ch = (Chain) sc;
			final Residue middleRes = ch.getResidue(ch.getResidueCount() / 2);
			residueToLabel = middleRes;

			if (color == null) {
				final StructureMap sm = middleRes.getStructure().getStructureMap();
				final Chain c = sm.getChain(middleRes.getChainId());
				color = new float[4];
				final ChainStyle style = (ChainStyle) sm.getStructureStyles()
						.getStyle(c);
				style.getResidueColor(middleRes, color);
			}
		}

		if (color == null) {
			color = Constants.white; // default to white
		}

		synchronized (this.labelsToCreate)
		{
			this.labelsToCreate.add(new Object[] { residueToLabel, label, color });
		}
	}

	/**
	 * @see org.rcsb.mbt.glscene.jogl.JoglSceneNode#drawTypeLabels(javax.media.opengl.GL, java.lang.Object, java.lang.Integer)
	 */
	@Override
	protected void drawTypeLabels(GL gl, Object key, Integer label)
	{
		if (key instanceof Interaction)
		{
			final Interaction line = (Interaction) key;

			final int list = label.intValue();
			if (list >= 0)
			{
				gl.glPushMatrix();

				for (int i = 0; i < line.getFirstAtom().coordinate.length; i++)
				{
					this.tempMidpoint[i] = (line
							.getFirstAtom().coordinate[i] + line
							.getSecondAtom().coordinate[i]) / 2;
				}
				gl.glTranslated(this.tempMidpoint[0] + .5f,
						this.tempMidpoint[1] - .5f,
						this.tempMidpoint[2] + .5f);
				// constants represent arbitrary displacement to separate
				// the label from the line.

				gl.glRasterPos3f(0, 0, 0);

				gl.glCallList(list);
				gl.glPopMatrix();
			}
		}
		
		else super.drawTypeLabels(gl, key, label);
	}
}
