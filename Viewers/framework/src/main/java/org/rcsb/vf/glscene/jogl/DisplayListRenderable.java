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
 * Created on 2007/02/08
 *
 */ 
package org.rcsb.vf.glscene.jogl;

// JOGL (OpenGL)

// MBT
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.rcsb.mbt.model.*;
import org.rcsb.mbt.model.StructureComponentRegistry.ComponentType;
import org.rcsb.mbt.model.attributes.*;

import com.sun.opengl.util.GLUT;



/**
 *  Provides a renderable object which contains all properties needed to
 *  regenerate geometry for a visible representation of the data. As the
 *  viewer processes incomming state change events from the tookit, the
 *  viewer sets dirty bit states in instances of this class so that during
 *  the next render pass, the viewer can quickly determine whether content
 *  needs to be updated before it is redrawn. The "set*" and "isDirty" methods
 *  are synchronized to avoid test/set race conditions between the
 *  application thread and rendering thread.
 *  
 *  One of these is created for every StructureComponent in the model.
 *  
 *  Note the renderable contains:
 *  
 *    - a reference to a model object (StructureComponent - Atom, Bond, etc.)
 *    - a reference to it's complementary geometry object (AtomGeometry, BondGeometry, etc.)
 *    
 */
public class DisplayListRenderable
	extends Renderable
{
	public StructureComponent structureComponent;
	public Style style;
	public DisplayListGeometry geometry;

	public DisplayLists[] displayLists = null;
	
	/**
	 * Flag to indicate whether the internal lists and unique colors should be deleted on destruction.
	 * 
	 * An instance where you would NOT want this set would be when display lists are shared, e.g.
	 * Atoms or Bonds, which share the primitive glut geometry (sphere and cylinder, respectively.)
	 */
	private boolean deleteListsOnDeconstruction = true;
	
	private final Object lockObject = new Object();
	
	/**
	 *  Construct a Renderable object.
	 */
	public DisplayListRenderable( final StructureComponent structureComponent,
		final Style style, final DisplayListGeometry geometry)
	{
		if ( structureComponent == null ) {
			throw new NullPointerException( "null structureComponent argument" );
		}
		if ( style == null ) {
			throw new NullPointerException( "null style argument" );
		}
		if ( geometry == null ) {
			throw new NullPointerException( "null geometry argument" );
		}

		this.structureComponent = structureComponent;
		this.style = style;
		this.geometry = geometry;
		
		// quick fix - only ribbons are dynamic enough to warrent automatic deletion of their display lists when they're destroyed. I never destroy ribbons, so the effect is that no array list is ever destroyed.
		if(this.structureComponent.getStructureComponentType() == ComponentType.ATOM ||
			this.structureComponent.getStructureComponentType() == ComponentType.BOND) {
			this.deleteListsOnDeconstruction = false;
		}
	}


	/**
	 *  If the renderable is dirty, regenerate the display list and set the
	 *  dirty state to false, then return the display list.
	 */
	
	public final void draw( final GL gl, final GLU glu, final GLUT glut, final boolean isPickMode )
	{
		if ( this.dirty )
		{
			synchronized(this.lockObject) {
				this.dirty = false;
				this.displayLists = this.geometry.getDisplayLists(this.structureComponent, this.style, gl, glu, glut );
			}
		}
		
		for(int i = 0; i < this.displayLists.length; i++) {
			if(this.displayLists[i] != null) {
//				this.displayLists[i].structureComponent.structure = Model.getSingleton().getStructure();	// quick fix.
				this.displayLists[i].draw(gl, glu, glut, isPickMode);
			}
		}
	}
	
	/**
	 * Destructor - if 'deleteListsOnDeconstruction' is true, clean up lists and unique colors lists
	 */
	public final void destroy(final GL gl, final GLU glu, final GLUT glut) {
		synchronized(this.lockObject) {
			if(this.displayLists != null) {
				for(int i = 0; i < this.displayLists.length; i++) {
					if(this.displayLists[i] != null) {
						if(this.deleteListsOnDeconstruction) {
							this.displayLists[i].deleteVideoMemory(gl, glu, glut);
						}
						this.displayLists[i].deleteUniqueColors();
			//			this.model.getStateOrganizer().removeArrayLists(this.arrayLists[i]);
					}
				}
			}
		
			JoglSceneNode sceneNode = (JoglSceneNode) structureComponent.structure.getStructureMap().getUData();
			sceneNode.removeLabel(this.structureComponent);
		}
	}


	public void setDeleteListsOnDeconstruction(final boolean deleteListsOnDeconstruction) {
		this.deleteListsOnDeconstruction = deleteListsOnDeconstruction;
	}
}

