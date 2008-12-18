package org.rcsb.vf.glscene.jogl;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import javax.vecmath.Point3d;
import org.rcsb.mbt.model.LineSegment;
import org.rcsb.mbt.model.StructureComponent;
import org.rcsb.mbt.model.attributes.LineStyle;
import org.rcsb.mbt.model.attributes.Style;
import org.rcsb.uiApp.controllers.app.AppBase;


import com.sun.opengl.util.GLUT;


public class LineGeometry extends DisplayListGeometry {
	
	public LineGeometry() {
		
	}

	
	@Override
	public DisplayLists[] getDisplayLists(
			final StructureComponent structureComponent, final Style style, final GL gl, final GLU glu,
			final GLUT glut) {

		// Handle quality, form, and shared display lists.
		//

		// if(this.vertexShader == -1) {
		// this.createVertexShader(gl, glu, glut);
		// }
		//		
		// if(this.fragmentShader == -1) {
		// this.createFragmentShader(gl, glu, glut);
		// }
		//		
		// if(this.shaderProgram == -1) {
		// this.createShaderProgram(gl, glu, glut);
		// }

		final LineSegment line = (LineSegment) structureComponent;
		final LineStyle lineStyle = (LineStyle) style;

		final DisplayLists[] lists = new DisplayLists[1];

		lists[0] = new DisplayLists(line);
		lists[0].setupLists(1);

		lists[0].startDefine(0, gl, glu, glut);

		final Point3d firstPoint = line.getFirstPoint();
		final Point3d secondPoint = line.getSecondPoint();
		
		if(lineStyle.lineStyle == LineStyle.DASHED || lineStyle.lineStyle == LineStyle.DOTTED) {
			gl.glEnable(GL.GL_LINE_STIPPLE);
			if(lineStyle.lineStyle == LineStyle.DASHED) {
				gl.glLineStipple(1, (short)0xFFF);
			} else if(lineStyle.lineStyle == LineStyle.DOTTED) {
				gl.glLineStipple(0, (short)0x3);
			}
		} else if(lineStyle.lineStyle == LineStyle.SOLID) {
			gl.glDisable(GL.GL_LINE_STIPPLE);
		}
		
		gl.glPointSize(7.0f);
		gl.glBegin(GL.GL_LINES);
		gl.glVertex3d(firstPoint.x,firstPoint.y,firstPoint.z);
		gl.glVertex3d(secondPoint.x,secondPoint.y,secondPoint.z);
//		gl.glMaterialfv(GL.GL_FRONT, GL.GL_EMISSION, Constants.white, 0);
		gl.glEnd();
		gl.glPointSize(1.0f);
		
		if(lineStyle.lineStyle == LineStyle.DASHED || lineStyle.lineStyle == LineStyle.DOTTED) {
			gl.glDisable(GL.GL_LINE_STIPPLE);
		}

		// lists[0].shaderProgram = this.shaderProgram;

		// lists[0].disableLigting = true;

		lists[0].endDefine(gl, glu, glut);

		lists[0].structureComponent = line;

		int labelDl = -1;
		//label = null; // JLM DEBUG: Disable atom labels for now...
		JoglSceneNode sceneNode = (JoglSceneNode)AppBase.sgetModel().getStructures().get(0).getStructureMap().getUData();
		if ( lineStyle.label != null )
		{
			labelDl = gl.glGenLists( 1 );
			gl.glNewList( labelDl, GL.GL_COMPILE );
			
//			gl.glDisable(GL.GL_LIGHTING);
//			gl.glDisable(GL.GL_DEPTH_TEST);
			
			glut.glutBitmapString( GLUT.BITMAP_HELVETICA_12, lineStyle.label );
//			gl.glEnable(GL.GL_DEPTH_TEST);
//			gl.glEnable(GL.GL_LIGHTING);

			gl.glEndList( );
			
			sceneNode.registerLabel(line, new Integer(labelDl), false, GlGeometryViewer.white);
		}
		
		else
			sceneNode.removeLabel(line);
		
		//
		// Handle shared label display lists.
		//

		// gl.glMaterialfv( GL.GL_FRONT, GL.GL_AMBIENT_AND_DIFFUSE, black, 0 );
		// gl.glMaterialfv( GL.GL_FRONT, GL.GL_SPECULAR, black, 0 );
		// gl.glMaterialfv( GL.GL_FRONT, GL.GL_EMISSION, color, 0 );
		lists[0].mutableColorType = GL.GL_EMISSION;
		lists[0].specularColor = Constants.black;
		lists[0].ambientColor = Constants.black;
		lists[0].diffuseColor = Constants.black;
		lists[0].disableLigting = true;

		// gl.glPopMatrix( );

		// gl.glEndList( );

		return lists;
	}

}
