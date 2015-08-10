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

// package org.rcsb.mbt.viewers.GlStructureViewer;


import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

import org.rcsb.mbt.model.*;
import org.rcsb.mbt.model.attributes.*;

import com.jogamp.opengl.util.gl2.GLUT;

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
		
		GL2 gl2 = gl.getGL2();
		
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
				gl2.glPointSize( 7.0f );
				gl2.glBegin( GL.GL_POINTS );
				gl2.glVertex3f( 0.0f, 0.0f, 0.0f );
				gl2.glEnd( );
				gl2.glPointSize( 1.0f );
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
				labelDl = gl2.glGenLists( 1 );
				gl2.glNewList( labelDl, GL2.GL_COMPILE );
				
//				gl.glDisable(GL.GL_LIGHTING);
//				gl.glDepthFunc(GL.GL_ALWAYS);
				
				glut.glutBitmapString( GLUT.BITMAP_HELVETICA_12, label );
//				gl.glDepthFunc(GL.GL_LEQUAL);
//				gl.glEnable(GL.GL_LIGHTING);
				
				gl2.glEndList( );

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
		final JoglSceneNode sn = (JoglSceneNode) structureMap.getUData();

		if ( form == Geometry.FORM_POINTS )
		{
			lists[0].mutableColorType = GL2.GL_EMISSION;
			lists[0].specularColor = Constants.black;
			lists[0].ambientColor = Constants.black;
			lists[0].diffuseColor = Constants.black;
		}
		else
		{
			lists[0].mutableColorType = GL2.GL_AMBIENT_AND_DIFFUSE;
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

