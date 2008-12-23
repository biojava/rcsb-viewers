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
package org.rcsb.vf.glscene.jogl;

import java.nio.FloatBuffer;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Point3d;

import org.rcsb.mbt.model.Atom;
import org.rcsb.mbt.model.Bond;
import org.rcsb.mbt.model.Chain;
import org.rcsb.mbt.model.Fragment;
import org.rcsb.mbt.model.LineSegment;
import org.rcsb.mbt.model.Residue;
import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.StructureComponent;
import org.rcsb.mbt.model.StructureComponentRegistry;
import org.rcsb.mbt.model.StructureMap;
import org.rcsb.mbt.model.StructureComponentRegistry.ComponentType;
import org.rcsb.mbt.model.StructureMap.BiologicUnitTransforms;
import org.rcsb.mbt.model.attributes.ChainStyle;
import org.rcsb.mbt.model.util.DebugState;
import org.rcsb.uiApp.controllers.app.AppBase;
import org.rcsb.vf.controllers.app.VFAppBase;
import org.rcsb.vf.glscene.surfaces.Surface;


import com.sun.opengl.util.GLUT;


public class JoglSceneNode
{
	public HashMap<Object, Object[]> labels = new HashMap<Object, Object[]>();

	// value: Integer
	// displaylist.
	public class RenderablesMap extends HashMap<StructureComponent, DisplayListRenderable>
	{
		private static final long serialVersionUID = -3286356986071701750L;		
	};
	public interface RenderablesIt extends Iterator<StructureComponent>{}
	
	protected RenderablesMap renderables = new RenderablesMap();
	public RenderablesMap getRenderablesMap() { return renderables; }
	
	protected boolean allowLighting = false;

	// StructureComponent,

	// value: MbtRenderable

	// currently, only for atoms.
	public List labelsToCreate = Collections.synchronizedList(new Vector());

	// -----------------------------------------------------------------------------

	private final double[] tempMidpoint = { 0, 0, 0 };
	
	public void createLabels(final GL gl, final GLUT glut)
	{
		synchronized (this.labelsToCreate)
		{
			synchronized (this.labels)
			{
				final Iterator labelsToCreateIt = this.labelsToCreate.iterator();
				while (labelsToCreateIt.hasNext())
				{
					final Object[] tmp = (Object[]) labelsToCreateIt.next();
					final Object component = tmp[0];
					final String label = (String) tmp[1];
					final float[] color = (float[]) tmp[2];

					if (this.isLabelActive(component))
						continue;

					final int labelDl = gl.glGenLists(1);
					gl.glNewList(labelDl, GL.GL_COMPILE);
	
					if (allowLighting)
					{
						gl.glDisable(GL.GL_LIGHTING);
						gl.glDepthFunc(GL.GL_ALWAYS);
					}
	
					glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, label);
					if (allowLighting)
					{
						gl.glDepthFunc(GL.GL_LEQUAL);
						gl.glEnable(GL.GL_LIGHTING);
					}
	
					gl.glEndList();
	
					this.labels.put(component, new Object[] { new Integer(labelDl),
							color, Boolean.FALSE });	
				}
				this.labelsToCreate.clear();
			}
		}
	}

	public DisplayListRenderable getRenderable(final Object key)
	{
		synchronized (this.renderables)
		{
			return (DisplayListRenderable) this.renderables.get(key);
		}
	}
	
	/**
	 * Sets up drawing renderables associated with this scene node.
	 * 'innerDraw()' does the real work.
	 * 
	 * @param gl
	 * @param glu
	 * @param glut
	 * @param isPick
	 * @param struc
	 * @return
	 */
	public boolean draw(final GL gl, final GLU glu, final GLUT glut, boolean isPick,
			final Structure struc)
	{
			createLabels(gl, glut);
			
			if (VFAppBase.sgetSceneController().areGlobalTransformsDisabled() ||
				VFAppBase.sgetSceneController().showAsymmetricUnitOnly())
					this.innerDraw(gl, glu, glut, isPick, struc, null);
							// short circuit if these are set
			
			else
			{
				final StructureMap sm = struc.getStructureMap();
				GLTransformationList matrices = null;
				
				if (sm.hasBiologicUnitTransforms())
									// look for biologic unit transforms
				{
					StructureMap.BiologicUnitTransforms bu = sm.getBiologicUnitTransforms();
					matrices = GLTransformationList.fromModelTransformationList(bu.getBiologicalUnitGenerationMatrixVector());
				}
				
				else if (sm.hasNonCrystallographicTransforms())
									// look for non crystallographic transforms
				{
					StructureMap.NonCrystallographicTransforms nc = sm.getNonCrystallographicTransforms();
					matrices = GLTransformationList.fromModelTransformationList(nc.getNonCrystallographicTranslations());
				}
	
				if (matrices == null)
					this.innerDraw(gl, glu, glut, isPick, struc, null);
									// no transforms - short circuit, again
					
				else
				{
					for (int j = 0; j < matrices.size(); j++)
					{
						final FloatBuffer transformation = matrices
								.get(j);
	
						boolean completed = this.innerDraw(gl, glu, glut, isPick,
								struc, transformation);
						// pick cycles do not need to finish, and paints get
						// priority.
						if (isPick && !completed)
							return false;
					}
				}
			}

		return true;
	}

	public int globalDisplayList = -1;

	public int numDraws = 0;

	public void regenerateGlobalList() {
		this.numDraws = 0;
	}
	
	public boolean isLabelActive(final Object component) {
		return this.labels.get(component) != null;
	}

	//
	// Scene content methods.
	//

	/**
	 * Add a new renderable object to the scene.
	 */
	public void addRenderable(final DisplayListRenderable renderable) {
		if (renderable == null) {
			throw new NullPointerException("null renderable");
		}

		synchronized (this.renderables) {
			this.renderables.put(renderable.structureComponent, renderable);
		}

		VFAppBase.sgetGlGeometryViewer().requestRepaint();
	}

	/**
	 * Remove a renderable object from the scene.
	 */
	public void removeRenderable(final DisplayListRenderable renderable) {
		if (renderable == null) {
			throw new NullPointerException("null renderable");
		}

		synchronized (this.renderables) {
			this.renderables.remove(renderable.structureComponent);

			// if(renderable.arrayLists != null) {
			// for(int i = 0; i < renderable.arrayLists.length; i++) {
			// if(renderable.arrayLists[i] != null) {
			// this.model.getStateOrganizer().removeArrayLists(renderable.arrayLists[i]);
			// }
			// }
			// }
		}

		final GlGeometryViewer glViewer = VFAppBase.sgetGlGeometryViewer();
		synchronized (glViewer.renderablesToDestroy)
		{
			glViewer.renderablesToDestroy.add(renderable);
		}

		glViewer.requestRepaint();
	}

	public void removeRenderable(final StructureComponent component) {
		if (component == null) {
			throw new NullPointerException("null component");
		}

		DisplayListRenderable renderable = null;
		synchronized (this.renderables) {
			renderable = this.renderables
					.get(component);
		}
		if (renderable != null) {
			this.removeRenderable(renderable);
		}

		VFAppBase.sgetGlGeometryViewer().requestRepaint();
	}

	/**
	 * Remove all renderable objects from the scene.
	 */
	public void clearRenderables() {
		final GlGeometryViewer viewer = VFAppBase.sgetGlGeometryViewer();
		synchronized (this.renderables) {
			synchronized (viewer.renderablesToDestroy) {
				viewer.renderablesToDestroy.addAll(this.renderables.values());
				this.renderables.clear();
				// this.model.getStateOrganizer().clearData();
			}
		}

		viewer.requestRepaint();
	}

	public void registerLabel(final StructureComponent sc,
			final Integer displayList, final boolean isDisplayListShared, final float[] color)
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

	public void removeLabel(final Object sc) {
		synchronized (this.labels) {
			this.labels.remove(sc);
		}
	}

	public void clearLabels()
	{
		synchronized (this.labels)
		{
			for (Object sc : labels.keySet())
			{
				final Object[] tmp = labels.get(sc);
				final Boolean isDisplayListShared = (Boolean) tmp[2];
				if (!isDisplayListShared.booleanValue()) {
					this.deallocateLabel(sc);
				}
			}

			this.labels.clear();
		}
	}

	public void removeLabelAndDeallocate(final Object sc) {
		Integer list = null;
		synchronized (this.labels) {
			final Object[] tmp = this.labels.remove(sc);
			list = (Integer) tmp[0];
		}

		if (list != null) {
			synchronized (VFAppBase.sgetGlGeometryViewer().simpleDisplayListsToDestroy)
			{
				VFAppBase.sgetGlGeometryViewer().simpleDisplayListsToDestroy.add(list);
			}
		}
	}

	public void deallocateLabel(final Object sc) {
		Integer list = null;
		synchronized (this.labels) {
			final Object[] tmp = this.labels.get(sc);
			list = (Integer) tmp[0];
		}

		if (list != null) {
			synchronized (VFAppBase.sgetGlGeometryViewer().simpleDisplayListsToDestroy) {
				VFAppBase.sgetGlGeometryViewer().simpleDisplayListsToDestroy.add(list);
			}
		}
	}

	/**
	 * Use this to create a label on an arbitrary structure component(s). The
	 * component(s) does not need to be visible. End result is a label on a
	 * (possibly invisible) alpha carbon.
	 * 
	 * @param sc
	 *            may be a Residue, Atom, Fragment, or a set of Residues.
	 *            Residue array is assumed to be physically contiguous and in
	 *            physical order.
	 */
	public void createAndAddLabel(final Object sc, final String label)
	{
		Atom atomToLabel = null;
		final float[] color = { 1, 1, 1, 1 }; // default to white

		if (sc instanceof Residue)
			atomToLabel = ((Residue) sc).getAlphaAtom();
		
		else if (sc instanceof Residue[])
		{
			final Residue[] reses = (Residue[]) sc;
			final Residue r = reses[reses.length / 2];
			final StructureMap sm = r.getStructure().getStructureMap();
			final Chain c = sm.getChain(r.getChainId());
			atomToLabel = r.getAlphaAtom();
			final ChainStyle style = (ChainStyle) sm.getStructureStyles().getStyle(c);
			style.getResidueColor(r, color);
		}
		
		else if (sc instanceof Fragment)
		{
			final Fragment frag = (Fragment) sc;
			final Residue middleRes = frag.getResidue(frag.getResidueCount() / 2);
			final StructureMap sm = middleRes.getStructure().getStructureMap();
			final Chain c = sm.getChain(middleRes.getChainId());
			atomToLabel = middleRes.getAlphaAtom();

			final ChainStyle style = (ChainStyle) sm.getStructureStyles().getStyle(c);
			style.getResidueColor(middleRes, color);
		}
		
		else if (sc instanceof Atom)
			atomToLabel = (Atom) sc;
		
		else if (sc instanceof Chain)
		{
			final Chain ch = (Chain) sc;
			final Residue middleRes = ch.getResidue(ch.getResidueCount() / 2);
			final StructureMap sm = middleRes.getStructure().getStructureMap();
			final Chain c = sm.getChain(middleRes.getChainId());
			atomToLabel = middleRes.getAlphaAtom();

			final ChainStyle style = (ChainStyle) sm.getStructureStyles().getStyle(c);
			style.getResidueColor(middleRes, color);
		}

		synchronized (this.labelsToCreate) {
			this.labelsToCreate.add(new Object[] { atomToLabel, label, color });
		}
	}

	/**
	 * Creates the display lists for the various kinds of objects, after setting parameters.
	 * 
	 * @param gl
	 * @param glu
	 * @param glut
	 * @param isPick
	 * @param struc
	 * @param inTransform
	 * @return
	 */
	protected boolean innerDraw(final GL gl, final GLU glu, final GLUT glut, boolean isPick,
			final Structure struc, final FloatBuffer inTransform)
	{
		try {
			gl.glPushMatrix();

			final GlGeometryViewer viewer = VFAppBase.sgetGlGeometryViewer();
			/**
			 * This only works because this function is completely overridden for the NC (LX)
			 * version.
			 */
			
			BiologicUnitTransforms.BiologicalUnitGenerationMapByChain buMatrices = null;
			
			StructureMap sm = struc.getStructureMap();

			if (inTransform != null)
							// supplied transform overrides anything else(?)
			{
				inTransform.rewind();
				gl.glMultMatrixf(inTransform);
			}
			
			else if (sm.hasBiologicUnitTransforms() &&
					!(VFAppBase.sgetSceneController().areGlobalTransformsDisabled() ||
					  VFAppBase.sgetSceneController().showAsymmetricUnitOnly()))
			{
				StructureMap.BiologicUnitTransforms bu = sm.getBiologicUnitTransforms();
				buMatrices = bu.getBiologicalUnitGenerationMatricesByChain();
			}

			synchronized (this.renderables)
			{
				for (StructureComponent sc : renderables.keySet())
				{
					DisplayListRenderable renderable = renderables.get(sc);
					
					if (buMatrices != null)
					{
						String chainId = null;
						if (sc.getStructureComponentType() == ComponentType.ATOM) {
							final Atom a = (Atom) sc;
							chainId = a.chain_id;
						} else if (sc.getStructureComponentType() == ComponentType.BOND) {
							final Bond b = (Bond) sc;
							chainId = b.getAtom(0).chain_id;
						} else if (sc.getStructureComponentType() == ComponentType.CHAIN) {
							final Chain c = (Chain) sc;
							chainId = c.getChainId();
						} 

						// if the chain id is not listed, don't draw this.
						if (VFAppBase.sgetSceneController().showAsymmetricUnitOnly() || sc.getStructureComponentType() == ComponentType.SURFACE)
						{
							try {
								gl.glPushMatrix();
								renderable.draw(gl, glu, glut, isPick);
							} catch (Exception e)
							{
								if (DebugState.isDebug())
									e.printStackTrace();
							}
							gl.glPopMatrix();
						}
						
						else
						{
							final GLTransformationList matricesVec =
								GLTransformationList.fromModelTransformationList(buMatrices.get(chainId));
							
							if (matricesVec != null) {
								for (int i = 0; i < matricesVec.size(); i++) {
									final FloatBuffer transformation = matricesVec.get(i);

									try {
										gl.glPushMatrix();
										transformation.rewind();
										gl.glMultMatrixf(transformation);
										renderable.draw(gl, glu, glut, isPick);
									} catch (Exception e)
									{
										if (DebugState.isDebug())
											e.printStackTrace();
									}
									gl.glPopMatrix();
								}
							}
						}
//					System.err.println("End " + sc.getStructureComponentType() + "\n\n");
					}
					
					else
						renderable.draw(gl, glu, glut, isPick);

					// pick cycles do not need to finish, and paints get priority.
					if (isPick && viewer.needsRepaint) {
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

						if (GlGeometryViewer.currentProgram != 0) {
							gl.glUseProgram(0);
						}
						gl.glDisable(GL.GL_LIGHTING);

						gl.glColorMaterial(GL.GL_FRONT, GL.GL_EMISSION);
						gl.glEnable(GL.GL_COLOR_MATERIAL);
						
						final Iterator it = this.labels.values().iterator();
						final Iterator keyIt = this.labels.keySet().iterator();
						while (it.hasNext()) {
							final Object[] tmp = (Object[]) it.next();
							final Integer label = (Integer) tmp[0];
							final float[] color = (float[]) tmp[1];
							gl.glColor3fv(color, 0);

							final Object key = keyIt.next();
							drawTypeLabels(gl, key, label);
						}

						gl.glDisable(GL.GL_COLOR_MATERIAL);
						
						gl.glEnable(GL.GL_DEPTH_TEST);

						if (GlGeometryViewer.currentProgram != 0)
							gl.glUseProgram(GlGeometryViewer.currentProgram);

						gl.glEnable(GL.GL_LIGHTING);
					}
				}
			}
		} catch (Exception e)
		{
			if (DebugState.isDebug())
				e.printStackTrace();
		}
		
		gl.glPopMatrix();

		return true;
	}
	
	/**
	 * Called by 'innerDraw()' draw objects by type.
	 * This can be overridden to support application-defined types.
	 * 
	 * @param gl
	 * @param key
	 * @param label
	 */
	protected void drawTypeLabels(GL gl, Object key, Integer label)
	{
		if (key instanceof Atom) {
			final Atom atom = (Atom) key;
			final int list = label.intValue();
			if (list >= 0) {

				try {
					gl.glPushMatrix();
					gl.glTranslated(atom.coordinate[0],
							atom.coordinate[1], atom.coordinate[2]);
					gl.glRasterPos3f(0, 0, 0);

					gl.glCallList(list);
				} catch (Exception e)
				{
					if (DebugState.isDebug())
						e.printStackTrace();
				}
				
				gl.glPopMatrix();
			}
		}
		
		else if (key instanceof Residue)
		{
			final Residue res = (Residue) key;
			Atom atom = res.getAlphaAtom();
			final int list = label.intValue();
			
			if (atom == null)
				atom = res.getAtom(res.getAtomCount() / 2);
			
			if (list >= 0)
			{
				try {
					gl.glPushMatrix();
					gl.glTranslated(atom.coordinate[0],
							atom.coordinate[1], atom.coordinate[2]);
					gl.glRasterPos3f(0, 0, 0);

					gl.glCallList(list);
				} catch (Exception e)
				{
					if (DebugState.isDebug())
						e.printStackTrace();
				}
				
				gl.glPopMatrix();
			}
		}
		
		else if (key instanceof LineSegment)
		{
			final LineSegment line = (LineSegment) key;

			final int list = label.intValue();
			if (list >= 0) {
				try {
					gl.glPushMatrix();

					double firstPointArray[] = new double[3], secondPointArray[] = new double[3];
					line.getFirstPoint().get(firstPointArray);
					line.getSecondPoint().get(secondPointArray);
					for (int i = 0; i < 3; i++)
						this.tempMidpoint[i] = (firstPointArray[i] + secondPointArray[i]) / 2;

					gl.glTranslated(this.tempMidpoint[0] + .5f,
							this.tempMidpoint[1] - .5f,
							this.tempMidpoint[2] + .5f);
					// constants represent an arbitrary displacement to separate
					// the label from the line.

					gl.glRasterPos3f(0, 0, 0);

					gl.glCallList(list);
				} catch (Exception e) {
					if (DebugState.isDebug())
						e.printStackTrace();
				}
				
				gl.glPopMatrix();
			}
		}
	}
}
