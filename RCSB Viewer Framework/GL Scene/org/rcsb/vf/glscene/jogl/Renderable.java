package org.rcsb.vf.glscene.jogl;

//  $Id: Renderable.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: Renderable.java,v $
//  Revision 1.1  2007/02/08 02:38:52  jbeaver
//  version 1.50
//
//  Revision 1.1  2006/09/20 16:50:42  jbeaver
//  first commit - branched from ProteinWorkshop
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
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import com.sun.opengl.util.GLUT;


/**
 *  Provides a renderable object which contains all properties needed to
 *  regenerate geometry for a visible representation of some data. As the
 *  viewer processes incomming state change events from the tookit, the
 *  viewer sets dirty bit states in instances of this class so that during
 *  the next render pass, the viewer can quickly determine whether content
 *  needs to be update before it is redrawn. The "setDirty" and "getDisplayList"
 *  methods are synchronized to avoid test/set race conditions between the
 *  application thread and rendering thread.
 */
public class Renderable
{
	protected boolean dirty = true;


	/**
	 * Constructor with basic initialization (empty display list and dirty).
	 */
	public Renderable( ) { }


	/**
	 *  Set the dirty state to true (generally called by an application
	 *  to indicate that the display list should be re-generated).
	 */
	public synchronized final void setDirty( )
	{
		this.dirty = true;
	}


	/**
	 *  If the renderable is dirty, regenerate the display list and set the
	 *  dirty state to false, then return the display list. This method
	 *  must be overridden by a sub-class.
	 */
	public void draw( final GL gl, final GLU glu, final GLUT glut, final boolean isSelectionMode )
	{
		throw new UnsupportedOperationException( "Child must implement!" );
	}
	
	public void destroy(final GL gl, final GLU glu, final GLUT glut) {
		
	}
}

