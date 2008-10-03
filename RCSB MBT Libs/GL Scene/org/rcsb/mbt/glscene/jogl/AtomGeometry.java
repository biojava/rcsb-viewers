package org.rcsb.mbt.glscene.jogl;

//  $Id: AtomGeometry.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: AtomGeometry.java,v $
//  Revision 1.1  2007/02/08 02:38:52  jbeaver
//  version 1.50
//
//  Revision 1.1  2006/09/20 16:50:42  jbeaver
//  first commit - branched from ProteinWorkshop
//
//  Revision 1.3  2006/09/06 04:46:12  jbeaver
//  Added initial capability for labeling ribbons
//
//  Revision 1.2  2006/09/02 18:52:28  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2006/08/24 17:39:03  jbeaver
//  *** empty log message ***
//
//  Revision 1.2  2006/05/30 09:43:44  jbeaver
//  Added lines and fog
//
//  Revision 1.1  2006/03/09 00:18:55  jbeaver
//  Initial commit
//
//  Revision 1.0 2005/04/04 00:12:54  moreland
//
                                                                                
                                                                                
// package org.rcsb.mbt.viewers.GlStructureViewer;


import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.rcsb.mbt.model.*;
import org.rcsb.mbt.model.attributes.*;

import com.sun.opengl.util.GLUT;

import java.util.*;



/**
 * AtomGeometry.java<BR>
 *
 * Please complete these missing tags
 * @author	John L. Moreland
 * @copyright	UCSD
 * @see
 */
public class AtomGeometry
	extends DisplayListGeometry
{
	// Shared display lists: key="form:quality" value=Integer(displayList)
	public static Hashtable sharedDisplayLists = new Hashtable( );

	// Attributes used to bound the quality setting.
	private static final int minSlices = 4;
	private static final int maxSlices = 20;
	private static final int minSegments = 4;
	private static final int maxSegments = 20;

	/**
	 *  Construct a new AtomGeometry object.
	 */
	public AtomGeometry( )
	{
	}


	/**
	 * Please complete the missing tags for main
	 * @param
	 * @return
	 * @throws
	 */
	public final int getDisplayList( final int displayList, final StructureComponent structureComponent, final Style style, final GL gl, final GLU glu, final GLUT glut)
	{
		return this.getDisplayList( displayList, structureComponent, style, gl, glu, glut);
	}

	
	
	/**
	 * Please complete the missing tags for main
	 * @param
	 * @return
	 * @throws
	 */
	
	public DisplayLists[] getDisplayLists(final StructureComponent structureComponent, final Style style, final GL gl, final GLU glu, final GLUT glut) {
		//
		// Handle quality, form, and shared display lists.
		//
		
		
		
		final Atom atom = (Atom)structureComponent;
		final AtomStyle atomStyle = (AtomStyle)style;
		
		final DisplayLists[] lists = new DisplayLists[1];
		
		final int form = this.getForm( );
		// Shared display lists: key="form:quality" value=Integer(displayList)
		final float quality = this.getQuality();
		final String dlKey = form + ":" + quality;
		lists[0] = (DisplayLists) AtomGeometry.sharedDisplayLists.get( dlKey );
		
		if ( lists[0] == null )
		{
			final int slices =
				AtomGeometry.minSlices + (int) ((AtomGeometry.maxSlices - AtomGeometry.minSlices) * quality);
			final int segments =
				AtomGeometry.minSegments + (int) ((AtomGeometry.maxSegments - AtomGeometry.minSegments) * quality);
//System.err.println("Atom: slices " + slices + ", segments " + segments);
			lists[0] = new DisplayLists(atom);
			lists[0].setupLists(1);
			
			lists[0].startDefine(0, gl, glu, glut);
			
			if ( form == Geometry.FORM_POINTS )
			{
				gl.glPointSize( 7.0f );
				gl.glBegin( GL.GL_POINTS );
				gl.glVertex3f( 0.0f, 0.0f, 0.0f );
				gl.glEnd( );
				gl.glPointSize( 1.0f );
			}
			else if ( form == Geometry.FORM_LINES )
			{
				glut.glutWireSphere( 1.0, slices, segments );
			}
			else if ( form == Geometry.FORM_FLAT )
			{
				glut.glutSolidSphere( 1.0, AtomGeometry.minSlices, AtomGeometry.minSegments );
			}
			else if ( form == Geometry.FORM_THICK )
			{
				glut.glutSolidSphere( 1.0, slices, segments );
			}
			else
			{
				throw new IllegalArgumentException( "unknown form " + form );
			}
			
			lists[0].endDefine(gl, glu, glut);

			AtomGeometry.sharedDisplayLists.put( dlKey, lists[0] );
		}
		lists[0] = lists[0].copy();
		lists[0].structureComponent = atom;

		//
		// Handle shared label display lists.
		//

		final float radius = atomStyle.getAtomRadius( atom );

		
		final String label = atomStyle.getAtomLabel( atom );
		int labelDl = -1;
		if ( label != null )
		{
			Integer labelDLInteger = (Integer)AtomGeometry.sharedDisplayLists.get( label );
			if ( labelDLInteger == null )
			{
				labelDl = gl.glGenLists( 1 );
				gl.glNewList( labelDl, GL.GL_COMPILE );
				
//				gl.glDisable(GL.GL_LIGHTING);
//				gl.glDepthFunc(GL.GL_ALWAYS);
				
				glut.glutBitmapString( GLUT.BITMAP_HELVETICA_12, label );
//				gl.glDepthFunc(GL.GL_LEQUAL);
//				gl.glEnable(GL.GL_LIGHTING);
				
				gl.glEndList( );

				labelDLInteger = new Integer( labelDl );
				AtomGeometry.sharedDisplayLists.put( label, labelDLInteger );
			}
			labelDl = labelDLInteger.intValue( );
		}

		//
		// Generate the display List
		//

		final Structure structure = atom.getStructure( );
		final StructureMap structureMap = structure.getStructureMap( );
		final StructureStyles structureStyles = structureMap.getStructureStyles( );
		final JoglSceneNode sn = (JoglSceneNode) structureMap.getUData();

		if ( form == Geometry.FORM_POINTS )
		{
			lists[0].mutableColorType = GL.GL_EMISSION;
			lists[0].specularColor = Constants.black;
			lists[0].ambientColor = Constants.black;
			lists[0].diffuseColor = Constants.black;
		}
		else
		{
			lists[0].mutableColorType = GL.GL_AMBIENT_AND_DIFFUSE;
			lists[0].specularColor = Constants.mat_specular;
			lists[0].shininess = Constants.atomHighShininess;
			lists[0].emissiveColor = Constants.black;
			
		}

		// Position
		lists[0].translation = new float[] {(float)atom.coordinate[0], (float)atom.coordinate[1], (float)atom.coordinate[2]};
		
		// Radius
		lists[0].scale = new float[] {radius, radius, radius};

		// Label
		if ( label != null )
			sn.registerLabel(atom, new Integer(labelDl), true, GlGeometryViewer.white);
		
		else
			sn.removeLabel(atom);

		return lists;
	}
	
	
}

