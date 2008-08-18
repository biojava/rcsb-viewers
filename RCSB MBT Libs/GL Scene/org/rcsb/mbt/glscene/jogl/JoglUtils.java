package org.rcsb.mbt.glscene.jogl;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import com.sun.opengl.util.GLUT;

public class JoglUtils {

	public static void accFrustum(final GL gl, final GLU glu, final GLUT glut, final double left,
			final double right, final double bottom, final double top, final double zNear, final double zFar,
			final double pixdx, final double pixdy, final double eyedx, final double eyedy, final double focus) {
		double xwSize, ywSize;
		double dx, dy;
		final int[] viewport = new int[4];

		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);

		xwSize = right - left;
		ywSize = top - bottom;
		dx = -(pixdx * xwSize / viewport[2] + eyedx * zNear / focus);
		dy = -(pixdy * ywSize / viewport[3] + eyedy * zNear / focus);

		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glFrustum(left + dx, right + dx, bottom + dy, top + dy, zNear, zFar);
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glTranslatef(-(float) eyedx, -(float) eyedy, 0f);
	}

	public static void accPerspective(final GL gl, final GLU glu, final GLUT glut, final double fovy,
			final double aspect, final double zNear, final double zFar, final double pixDx,
			final double pixdy, final double eyedx, final double eyedy, final double focus) {
		final double fov2 = ((fovy * Math.PI) / 180) / 2;

		double top = zNear / (Math.cos(fov2) / Math.sin(fov2));
		final double bottom = -top;
		double right = top * aspect;
		final double left = -right;

		JoglUtils.accFrustum(gl, glu, glut, left, right, bottom, top, zNear, zFar, pixDx,
				pixdy, eyedx, eyedy, focus);
//		frustum_jitter(gl, glu, glut, left, right, bottom, top, zNear, zFar, pixDx, pixdy);
		
		// double ymax = zNear * Math.tan( fovy * 3.14159265 / 360.0 );
		// double ymin = -ymax;
		// double xmin = ymin * aspect;
		// double xmax = ymax * aspect;
		// accFrustum(gl, glu, glut, xmin, xmax, ymin, ymax, zNear, zFar, pixDx,
		// pixdy, eyedx, eyedy, focus );

	}
}
