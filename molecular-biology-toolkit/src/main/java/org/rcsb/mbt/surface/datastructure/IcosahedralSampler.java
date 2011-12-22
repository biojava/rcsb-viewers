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
 * The code for surface generation was contributed by Dong Xu
 * and Yang Zhang at the University of Michigan, Ann Arbor. This
 * class represents the Java version translated from the original C++
 * code (http://zhanglab.ccmb.med.umich.edu/EDTSurf).
 * 
 * Please reference D. Xu, Y. Zhang (2009) 
 * Generating Triangulated Macromolecular Surfaces by Euclidean 
 * Distance Transform. PLoS ONE 4(12): e8140.
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
 * Created on 2011/11/08
 */

package org.rcsb.mbt.surface.datastructure;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Quat4d;


/**
 * 
 * @author Peter
 */
public final class IcosahedralSampler {
    private static Quat4d quat = new Quat4d();
         
	// this class cannot be instantiated
	private IcosahedralSampler() {
	};

	public static int getSphereCount() {
		return orientations.length;
	}
	
	public static Quat4d getQuat4d(int index) {
		Quat4d q = new Quat4d(orientations[index]);
		return q;
	}

	public static void getAxisAngle(int index, AxisAngle4d axisAngle) {
		quat.set(orientations[index]);
		axisAngle.set(quat);
	}

//	# Orientation set c600v, number = 60, radius = 44.48 degrees
//	# $Id: c600v.quat 6102 2006-02-21 19:45:40Z ckarney $
//	# For more information, eee http://charles.karney.info/orientation/
//	format quaternion
    private static double[][] orientations = {
	{1.000000000f, 0.000000000f, 0.000000000f, 0.000000000f, 1.000000f},
	{0.000000000f, 1.000000000f, 0.000000000f, 0.000000000f, 1.000000f},
	{0.000000000f, 0.000000000f, 1.000000000f, 0.000000000f, 1.000000f},
	{0.000000000f, 0.000000000f, 0.000000000f, 1.000000000f, 1.000000f},
	{0.000000000f, 0.500000000f, 0.309016994f, 0.809016994f, 1.000000f},
	{0.000000000f, -0.500000000f, 0.309016994f, 0.809016994f, 1.000000f},
	{0.000000000f, 0.500000000f, -0.309016994f, 0.809016994f, 1.000000f},
	{-0.000000000f, -0.500000000f, -0.309016994f, 0.809016994f, 1.000000f},
	{0.000000000f, 0.309016994f, 0.809016994f, 0.500000000f, 1.000000f},
	{0.000000000f, -0.309016994f, 0.809016994f, 0.500000000f, 1.000000f},
	{-0.000000000f, -0.309016994f, 0.809016994f, -0.500000000f, 1.000000f},
	{0.000000000f, 0.309016994f, 0.809016994f, -0.500000000f, 1.000000f},
	{0.000000000f, 0.809016994f, 0.500000000f, 0.309016994f, 1.000000f},
	{-0.000000000f, 0.809016994f, -0.500000000f, -0.309016994f, 1.000000f},
	{0.000000000f, 0.809016994f, -0.500000000f, 0.309016994f, 1.000000f},
	{0.000000000f, 0.809016994f, 0.500000000f, -0.309016994f, 1.000000f},
	{0.500000000f, 0.000000000f, 0.809016994f, 0.309016994f, 1.000000f},
	{-0.500000000f, 0.000000000f, 0.809016994f, 0.309016994f, 1.000000f},
	{-0.500000000f, -0.000000000f, 0.809016994f, -0.309016994f, 1.000000f},
	{0.500000000f, 0.000000000f, 0.809016994f, -0.309016994f, 1.000000f},
	{0.309016994f, 0.000000000f, 0.500000000f, 0.809016994f, 1.000000f},
	{-0.309016994f, 0.000000000f, 0.500000000f, 0.809016994f, 1.000000f},
	{0.309016994f, 0.000000000f, -0.500000000f, 0.809016994f, 1.000000f},
	{-0.309016994f, -0.000000000f, -0.500000000f, 0.809016994f, 1.000000f},
	{0.809016994f, 0.000000000f, 0.309016994f, 0.500000000f, 1.000000f},
	{0.809016994f, -0.000000000f, -0.309016994f, -0.500000000f, 1.000000f},
	{0.809016994f, 0.000000000f, -0.309016994f, 0.500000000f, 1.000000f},
	{0.809016994f, 0.000000000f, 0.309016994f, -0.500000000f, 1.000000f},
	{0.309016994f, 0.809016994f, 0.000000000f, 0.500000000f, 1.000000f},
	{-0.309016994f, 0.809016994f, 0.000000000f, 0.500000000f, 1.000000f},
	{-0.309016994f, 0.809016994f, -0.000000000f, -0.500000000f, 1.000000f},
	{0.309016994f, 0.809016994f, 0.000000000f, -0.500000000f, 1.000000f},
	{0.809016994f, 0.500000000f, 0.000000000f, 0.309016994f, 1.000000f},
	{0.809016994f, -0.500000000f, -0.000000000f, -0.309016994f, 1.000000f},
	{0.809016994f, -0.500000000f, 0.000000000f, 0.309016994f, 1.000000f},
	{0.809016994f, 0.500000000f, 0.000000000f, -0.309016994f, 1.000000f},
	{0.500000000f, 0.309016994f, 0.000000000f, 0.809016994f, 1.000000f},
	{-0.500000000f, 0.309016994f, 0.000000000f, 0.809016994f, 1.000000f},
	{0.500000000f, -0.309016994f, 0.000000000f, 0.809016994f, 1.000000f},
	{-0.500000000f, -0.309016994f, -0.000000000f, 0.809016994f, 1.000000f},
	{0.809016994f, 0.309016994f, 0.500000000f, 0.000000000f, 1.000000f},
	{0.809016994f, -0.309016994f, -0.500000000f, -0.000000000f, 1.000000f},
	{0.809016994f, -0.309016994f, 0.500000000f, 0.000000000f, 1.000000f},
	{0.809016994f, 0.309016994f, -0.500000000f, 0.000000000f, 1.000000f},
	{0.500000000f, 0.809016994f, 0.309016994f, 0.000000000f, 1.000000f},
	{-0.500000000f, 0.809016994f, 0.309016994f, 0.000000000f, 1.000000f},
	{-0.500000000f, 0.809016994f, -0.309016994f, -0.000000000f, 1.000000f},
	{0.500000000f, 0.809016994f, -0.309016994f, 0.000000000f, 1.000000f},
	{0.309016994f, 0.500000000f, 0.809016994f, 0.000000000f, 1.000000f},
	{-0.309016994f, 0.500000000f, 0.809016994f, 0.000000000f, 1.000000f},
	{0.309016994f, -0.500000000f, 0.809016994f, 0.000000000f, 1.000000f},
	{-0.309016994f, -0.500000000f, 0.809016994f, -0.000000000f, 1.000000f},
	{0.500000000f, 0.500000000f, 0.500000000f, 0.500000000f, 1.000000f},
	{0.500000000f, -0.500000000f, -0.500000000f, -0.500000000f, 1.000000f},
	{0.500000000f, -0.500000000f, 0.500000000f, 0.500000000f, 1.000000f},
	{0.500000000f, 0.500000000f, -0.500000000f, 0.500000000f, 1.000000f},
	{0.500000000f, 0.500000000f, 0.500000000f, -0.500000000f, 1.000000f},
	{0.500000000f, 0.500000000f, -0.500000000f, -0.500000000f, 1.000000f},
	{0.500000000f, -0.500000000f, 0.500000000f, -0.500000000f, 1.000000f},
	{0.500000000f, -0.500000000f, -0.500000000f, 0.500000000f, 1.000000f},
	};
}
