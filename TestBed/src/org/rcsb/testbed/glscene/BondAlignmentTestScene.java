package org.rcsb.testbed.glscene;

import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;

import org.rcsb.mbt.model.geometry.ArrayLinearAlgebra;
import org.rcsb.testbed.app.TestBed;
import org.rcsb.testbed.util.PrimitiveDisplayListsFactory;

import com.sun.opengl.util.GLUT;

public class BondAlignmentTestScene extends SceneBase implements IScene
{
	private int cylListId, ballListId, secondBondListId;
	private int currentCaseIX;
	
	private ArrayList<double[][]> testSets = new ArrayList<double[][]>();
	
	static final double bondRadius = 0.01,
		                atomRadius = bondRadius * 3.0;
	
	static final double r2d = 180.0f / Math.PI;  // radians to degrees conversion factor
	static final double rotAngleInc = 10.0;
	private double thirdAtomRotAngle = 0.0;
	private boolean transformConnected = false;
	
	public void incRotAngle() { thirdAtomRotAngle += rotAngleInc; if (thirdAtomRotAngle >= 360.0) thirdAtomRotAngle -= 360.0; }
	public void decRotAngle() { thirdAtomRotAngle -= rotAngleInc; if (thirdAtomRotAngle <= -360.0) thirdAtomRotAngle += 360.0; }
	public void nextCase() { currentCaseIX++; if (currentCaseIX >= testSets.size()) currentCaseIX = 0; reset();}
	public void connectTransform(boolean flag) { transformConnected = flag; }
	/**
	 * So, we want to do this the same way that MBT does it, which is to draw each bond in a double
	 * bond separately, and then add the atoms and such.
	 */
	@Override
	public void display(GLAutoDrawable drawable, GLUT glut, GLU glu)
	{
		super.display(drawable, glut, glu);
		
		GL gl = drawable.getGL();
		
		double testSet[][] = testSets.get(currentCaseIX);
		boolean doubleBond = true;
		double drawRadius = bondRadius / 2.0;
		int nBondParts = 2;
		
		double firstBondVec[] = null;

		double newThirdAtomPosition[] = null;
		
//
// Beg define bonds
// first (double bond) is drawn directly.
// second (single bond) is put into a display list so it can be drawn under a rotation scheme.
//
		for (int bondIX = 0; bondIX < 2; bondIX++)
		{
			double offset = doubleBond? atomRadius : 0.0;
			double preTranslateX = 0.0;
			boolean doPreRot = false;
			double preRotYcos = 0.0, preRotYsin = 0.0;
			
			double atomPt1[] = testSet[bondIX],
			       atomPt2[] = testSet[bondIX + 1];
			
			double bondVec[] =
				new double[]{ atomPt2[0] - atomPt1[0], atomPt2[1] - atomPt1[1], atomPt2[2] - atomPt1[2] };
									// vector of bond from atomPt2 to atomPt2
			
			double bondLength = ArrayLinearAlgebra.vectorLength(bondVec);
			double normalizedBondVec[] = new double[3];
			ArrayLinearAlgebra.normalizeVector(bondVec, normalizedBondVec);
									// normalized vector is used to get the rotator vector (ortho) and the angle of rotation.
			
// Following is adapted from BondGeometry.java

			// The rotation vector is just the cross-product of the two vectors:
			// (0.0, 1.0, 0.0) for the normalized Y-Up geometry/vector, and,
			// (direction[0], direction[1], direction[2]) for the bond direction
			// which, when multiplied out, simplifies to just:
			final double rotVec[] = { normalizedBondVec[2], 0.0f, -normalizedBondVec[0] };

			// The rotation angle is just the angle between the two vectors
			// which is "acos( v1 DOT v2 / |v1| * |v2| )" which simplifies to:
			final double rotAngle = Math.acos( normalizedBondVec[1] );
							// these are radians....
//
			
			if (bondIX == 0)
				firstBondVec = bondVec;
			
			double stance[] = new double[nBondParts];
			if (doubleBond)
			{
				stance[0] = -offset;
				stance[1] = offset;
			}
			
			else
				stance[0] = 0.0;
			
			if (bondIX == 1)
			{
				secondBondListId = gl.glGenLists(1);
				gl.glNewList(secondBondListId, GL.GL_COMPILE);
			}
			
/* **/
			if (doubleBond)
			{
				if (transformConnected)  // (for demo)
				{
				//
				// beg analyze third point rotation angle
				//
				double[] thirdAtomPoint = testSets.get(currentCaseIX)[2];
							// the third point defines the plane...				
				
				newThirdAtomPosition =
					new double[] { thirdAtomPoint[0] - atomPt2[0], thirdAtomPoint[1] - atomPt2[1], thirdAtomPoint[2] - atomPt2[2] };
				double[] rotator =
					new double[] { thirdAtomRotAngle / r2d, bondVec[0], bondVec[1], bondVec[2] };
				ArrayLinearAlgebra.angleAxisRotate(rotator, newThirdAtomPosition);
				newThirdAtomPosition[0] += atomPt2[0];
				newThirdAtomPosition[1] += atomPt2[1];
				newThirdAtomPosition[2] += atomPt2[2];
									// new atom third positioned (after click...) (this part is for demo only)
				             

				double[] xlatedThirdAtomPoint = new double[]{
				newThirdAtomPosition[0] - atomPt1[0],
				newThirdAtomPosition[1] - atomPt1[1],
				newThirdAtomPosition[2] - atomPt1[2]
								// translate third point to zero based vector
				};

				
				rotator[0] = -rotAngle; rotator[1] = rotVec[0]; rotator[2] = rotVec[1]; rotator[3] = rotVec[2];			
				ArrayLinearAlgebra.angleAxisRotate(rotator, xlatedThirdAtomPoint);
							// apply the reverse rotation to the third atom point, so that it is in reference
							// to the y-aligned initial bond vector
				
				double xzLen = Math.sqrt(xlatedThirdAtomPoint[0] * xlatedThirdAtomPoint[0] +
										 xlatedThirdAtomPoint[2] * xlatedThirdAtomPoint[2]);
							// the pre-rotation is only about the y axis, so we need to get the values for the xz plane, only
				
				preRotYcos = xlatedThirdAtomPoint[0] / xzLen;
				preRotYsin = xlatedThirdAtomPoint[2] / xzLen;
							// sin-cos fully define the rotation angle (see below)
				
				doPreRot = true;
				}
			}
/* **/

			for (int bondPartIX = 0; bondPartIX < nBondParts; bondPartIX++)
			{
				if (doubleBond)
				{
					preTranslateX = stance[bondPartIX];	
					if (bondPartIX == 0) gl.glColor3f(1.0f, 0.0f, 1.0f);
					else				 gl.glColor3f(0.0f, 0.0f, 1.0f);
				}
				
				else
					gl.glColor3f(1.0f, 1.0f, 0.0f);
									// colors are for demo to help differentiate the various items.
				
				
				gl.glPushMatrix();
								// (remember, gl transforms are applied (effectively) from the bottom up,
								// due to the way the transform is accumulated.)
				
				gl.glTranslated(atomPt1[0], atomPt1[1], atomPt1[2]);
								// Fifth, translate the rotated bond to the departure atom.
				
				gl.glRotated(rotAngle * r2d, rotVec[0], rotVec[1], rotVec[2]);
								// Fourth, rotate the vec to point to the second atom.
				
				if (doPreRot)
					gl.glMultMatrixd(new double[] {preRotYcos, 0.0, preRotYsin,  0.0,
									 			   0.0, 	   1.0, 	0.0,	 0.0,
									 			   -preRotYsin, 0.0, preRotYcos, 0.0,
									 			   0.0, 	    0.0,    0.0,     1.0}, 0);
								// Third, do pre-rotation.  The pre-rotation is about the 'y' axis.
								//
								// We have to specify a fully qualified rotation matrice for this rotation, otherwise,
								// we can get 'opposite' rotations on one half of the sphere due to the rotation angle
								// being only partially defined by the cosine in the x-z plane.  Sin and Cos fully
								// describe the rotation.
				
				if (preTranslateX != 0.0)
					gl.glTranslated(preTranslateX, 0.0, 0.0);
								// second, do pretranslation if defined.  Translation is along the x axis, plus or minus some delta

				gl.glScaled(drawRadius, bondLength, drawRadius);
								// first, scale by draw radius and the bond length.
				
				gl.glCallList(cylListId);
				gl.glPopMatrix();
			}
			
			if (bondIX == 1)
				gl.glEndList();
			
			doubleBond = false;
			drawRadius = bondRadius;
			nBondParts = 1;
						// second bond is single
		}
		
//
// Beg draw second bond and third atom
//
		// draw the first two atoms using ogl transform atoms
		//
		int ixAtom = 0;
		double atomPoint[];
		float color[];
		for (ixAtom = 0; ixAtom < 2; ixAtom++)
		{
			atomPoint = testSets.get(currentCaseIX)[ixAtom];
			color = new float[] { 0.0f, 0.0f, 0.0f };
			color[ixAtom] = 1.0f;
			gl.glColor4f(color[0], color[1], color[2] , 1.0f);
			gl.glPushMatrix();
			gl.glTranslated(atomPoint[0], atomPoint[1], atomPoint[2]);
			gl.glCallList(ballListId);
			gl.glPopMatrix();
		}
		
		double endBondVec1Point[] = testSets.get(currentCaseIX)[1];
		atomPoint = testSets.get(currentCaseIX)[2];
		gl.glPushMatrix();
		gl.glTranslated(endBondVec1Point[0], endBondVec1Point[1], endBondVec1Point[2]);
		gl.glRotated(thirdAtomRotAngle, firstBondVec[0], firstBondVec[1], firstBondVec[2]);
		gl.glTranslated(-endBondVec1Point[0], -endBondVec1Point[1], -endBondVec1Point[2]);
		gl.glColor4f(1.0f, 1.0f, 0.0f, 1.0f);
		gl.glCallList(secondBondListId);

		gl.glTranslated(atomPoint[0], atomPoint[1], atomPoint[2]);
		gl.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
		gl.glCallList(ballListId);
		gl.glPopMatrix();
		
		if (newThirdAtomPosition != null)
		{
			gl.glPushMatrix();
			gl.glColor4f(0.0f, 1.0f, 1.0f, 1.0f);
			gl.glTranslated(newThirdAtomPosition[0], newThirdAtomPosition[1], newThirdAtomPosition[2]);
			gl.glCallList(ballListId);
			gl.glPopMatrix();
		}
//
// End draw second bond and third atom
//
	}

	@Override
	public void init(GLAutoDrawable drawable, GLUT glut, GLU glu)
	{
		super.init(drawable, glut, glu);
		GL gl = drawable.getGL();
		
		currentCaseIX = TestBed.sgetCaseNum();
		
		cylListId = PrimitiveDisplayListsFactory.genCylinder(1.0, false, gl, glut, glu);
		ballListId = PrimitiveDisplayListsFactory.genSphere(atomRadius, gl, glut, glu);
		
		createTestSets();
	}
	
	private void createTestSets()
	{
		testSets.add(
			new double[][] {
				{ -0.2, -0.2, 0.4 },
				{ 0.2, 0.6, -0.1 },			// inverted peak, left to right, front to back
				{ 0.4, -0.5, -0.8 } });
		
		testSets.add(
			new double[][] {
				{ 0.3, -0.1, -0.4 },
				{ 0.6, -0.6, 0.2 },
				{ -0.4, 0.3, 0.4 } });
	}

	@Override
	public void reset()
	{
		thirdAtomRotAngle = 0.0;
	}
}
