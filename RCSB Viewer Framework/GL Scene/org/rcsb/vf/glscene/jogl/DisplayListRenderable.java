package org.rcsb.vf.glscene.jogl;

//  $Id: DisplayListRenderable.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
//
//  Copyright 2000-2004 The Regents of the University of California.
//  All Rights Reserved.
//
//  Permission to use, copy, modify and distribute any part of this
//  Molecular Biology Toolkit (MBT)
//  for educational, research and non-profit purposes, without fee, and without
//  a written agreement is hereby granted, provided that the above copyright
//  notice, this paragraph and the following three paragraphs appear in all
//  copies.
//
//  Those desiring to incorporate this MBT into commercial products
//  or use for commercial purposes should contact the Technology Transfer &
//  Intellectual Property Services, University of California, San Diego, 9500
//  Gilman Drive, Mail Code 0910, La Jolla, CA 92093-0910, Ph: (858) 534-5815,
//  FAX: (858) 534-7345, E-MAIL:invent@ucsd.edu.
//
//  IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR
//  DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING
//  LOST PROFITS, ARISING OUT OF THE USE OF THIS MBT, EVEN IF THE
//  UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.//
//  THE MBT PROVIDED HEREIN IS ON AN "AS IS" BASIS, AND THE
//  UNIVERSITY OF CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT,
//  UPDATES, ENHANCEMENTS, OR MODIFICATIONS. THE UNIVERSITY OF CALIFORNIA MAKES
//  NO REPRESENTATIONS AND EXTENDS NO WARRANTIES OF ANY KIND, EITHER IMPLIED OR
//  EXPRESS, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
//  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE, OR THAT THE USE OF THE
//  MBT WILL NOT INFRINGE ANY PATENT, TRADEMARK OR OTHER RIGHTS.
//
//  For further information, please see:  http://mbt.sdsc.edu
//
//  History:
//  $Log: DisplayListRenderable.java,v $
//  Revision 1.1  2007/02/08 02:38:52  jbeaver
//  version 1.50
//
//  Revision 1.1  2006/09/20 16:50:42  jbeaver
//  first commit - branched from ProteinWorkshop
//
//  Revision 1.2  2006/09/02 18:52:28  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2006/08/24 17:39:03  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2006/03/09 00:18:55  jbeaver
//  Initial commit
//
//  Revision 1.0 2005/04/04 00:12:54  moreland
//


// JOGL (OpenGL)

// MBT
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.rcsb.mbt.model.*;
import org.rcsb.mbt.model.StructureComponentRegistry.ComponentType;
import org.rcsb.mbt.model.attributes.*;
import org.rcsb.vf.glscene.surfaces.Surface;

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
		if(this.structureComponent.getStructureComponentType() == ComponentType.SURFACE ||
			this.structureComponent.getStructureComponentType() == ComponentType.ATOM ||
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

