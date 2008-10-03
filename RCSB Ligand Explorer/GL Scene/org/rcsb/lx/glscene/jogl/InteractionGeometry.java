package org.rcsb.lx.glscene.jogl;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.rcsb.lx.controllers.app.LigandExplorer;
import org.rcsb.lx.model.Interaction;
import org.rcsb.mbt.glscene.jogl.Constants;
import org.rcsb.mbt.glscene.jogl.DisplayListGeometry;
import org.rcsb.mbt.glscene.jogl.DisplayLists;
import org.rcsb.mbt.glscene.jogl.JoglSceneNode;
import org.rcsb.mbt.model.StructureComponent;
import org.rcsb.mbt.model.attributes.LineStyle;
import org.rcsb.mbt.model.attributes.Style;

import com.sun.opengl.util.GLUT;


public class InteractionGeometry extends DisplayListGeometry
{	
	public InteractionGeometry()
	{
		
	}
	
	public DisplayLists[] getDisplayLists(
			final StructureComponent structureComponent, final Style style, final GL gl, final GLU glu,
			final GLUT glut)
	{
		final Interaction line = (Interaction) structureComponent;
		final LineStyle interactionStyle = (LineStyle) style;

		final DisplayLists[] lists = new DisplayLists[1];
		JoglSceneNode topsn = (JoglSceneNode)LigandExplorer.sgetModel().getStructures().get(0).getStructureMap().getUData();

		lists[0] = new DisplayLists(line);
		lists[0].setupLists(1);

		lists[0].startDefine(0, gl, glu, glut);

		final double[] firstPoint = line.getFirstAtom().coordinate;
		final double[] secondPoint = line.getSecondAtom().coordinate;
		
		if(interactionStyle.lineStyle == LineStyle.DASHED || interactionStyle.lineStyle == LineStyle.DOTTED) {
			gl.glEnable(GL.GL_LINE_STIPPLE);
			if(interactionStyle.lineStyle == LineStyle.DASHED) {
				gl.glLineStipple(1, (short)0xFFF);
			} else if(interactionStyle.lineStyle == LineStyle.DOTTED) {
				gl.glLineStipple(0, (short)0x3);
			}
		} else if(interactionStyle.lineStyle == LineStyle.SOLID) {
			gl.glDisable(GL.GL_LINE_STIPPLE);
		}

		gl.glPointSize(7.0f);
		gl.glBegin(GL.GL_LINES);
		gl.glVertex3d(firstPoint[0],firstPoint[1],firstPoint[2]);
		gl.glVertex3d(secondPoint[0],secondPoint[1],secondPoint[2]);
//		gl.glMaterialfv(GL.GL_FRONT, GL.GL_EMISSION, Constants.white, 0);
		gl.glEnd();
		gl.glPointSize(1.0f);
		
		if(interactionStyle.lineStyle == LineStyle.DASHED || interactionStyle.lineStyle == LineStyle.DOTTED) {
			gl.glDisable(GL.GL_LINE_STIPPLE);
		}

		// lists[0].shaderProgram = this.shaderProgram;

		// lists[0].disableLigting = true;

		lists[0].endDefine(gl, glu, glut);

		lists[0].structureComponent = line;

		int labelDl = -1;
		//label = null; // JLM DEBUG: Disable atom labels for now...

		if ( interactionStyle.label != null )
		{
			labelDl = gl.glGenLists( 1 );
			gl.glNewList( labelDl, GL.GL_COMPILE );
			
			gl.glDisable(GL.GL_LIGHTING);
			gl.glDepthFunc(GL.GL_ALWAYS);
			
			glut.glutBitmapString( GLUT.BITMAP_HELVETICA_12, interactionStyle.label );
			gl.glDepthFunc(GL.GL_LEQUAL);
			gl.glEnable(GL.GL_LIGHTING);

			gl.glEndList( );
			
			topsn.registerLabel(line, new Integer(labelDl), false, interactionStyle.getColor());
		}
		
		else
			topsn.removeLabel(line);
		

		// gl.glMaterialfv( GL.GL_FRONT, GL.GL_AMBIENT_AND_DIFFUSE, black, 0 );
		// gl.glMaterialfv( GL.GL_FRONT, GL.GL_SPECULAR, black, 0 );
		// gl.glMaterialfv( GL.GL_FRONT, GL.GL_EMISSION, color, 0 );
		lists[0].mutableColorType = GL.GL_EMISSION;
		lists[0].specularColor = Constants.black;  // these were all black
		lists[0].ambientColor = Constants.black;
		lists[0].diffuseColor = Constants.black;
		lists[0].disableLigting = true;			// this was disabled

		// gl.glPopMatrix( );

		// gl.glEndList( );

		return lists;
	}

}
