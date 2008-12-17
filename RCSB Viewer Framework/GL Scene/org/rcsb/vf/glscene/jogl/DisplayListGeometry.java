package org.rcsb.vf.glscene.jogl;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.rcsb.mbt.model.StructureComponent;
import org.rcsb.mbt.model.attributes.Style;

import com.sun.opengl.util.GLUT;


public abstract class DisplayListGeometry extends Geometry {
	public abstract DisplayLists[] getDisplayLists(StructureComponent structureComponent, Style style, GL gl, GLU glu, GLUT glut);

}
