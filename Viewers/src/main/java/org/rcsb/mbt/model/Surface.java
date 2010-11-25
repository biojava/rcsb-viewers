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
 * Created on 2008/12/22
 *
 */ 
package org.rcsb.mbt.model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

import javax.vecmath.Color4f;
import javax.vecmath.Point3f;

import org.rcsb.mbt.model.StructureComponentRegistry.ComponentType;



public class Surface extends StructureComponent {
	public Vector atoms;
	private Point3f[] vertices;
	private Color4f[] colors;
	private int[][] faces;
	
	public Surface(final Vector atoms, final Structure structure) {
		super();
		super.structure = structure;
		this.atoms = atoms;
		
		// trial implementation, read sample surface from file
//		PlyReader reader = new PlyReader();
//		EmvReader reader = new EmvReader();
//		try {
//			reader.readPly("C:/RCSBViewerTrunk/RCSB MBT Libs/test-input/PlyReaderTest/2ptn.ply");
//			reader.readEmv("C:/RCSBViewerTrunk/RCSB MBT Libs/test-input/EmvReaderTest/emd_5127-i2.8-t50000.emv");
//		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		vertices = reader.getVertices();
//		colors = reader.getVertexColors();
////		faces = reader.getFaces();
		
		Color4f defaultColor = new Color4f(0.1f, 0.8f, 1.0f, 0.0f);
		for (int i = 0; i < colors.length; i++) {
			colors[i] = defaultColor; // default transparency
		}
	}

	
	public void copy(final StructureComponent structureComponent) {
		final Surface surface = (Surface)structureComponent;
		this.atoms = surface.atoms;
		// TODO copy other members
	}

	
	public ComponentType getStructureComponentType() {
		return ComponentType.SURFACE;
	}
	
	public Point3f[] getVertices() {
		return vertices;
	}

	public Color4f[] getColors() {
		return colors;
	}

	public int[][] getFaces() {
		return faces;
	}

	public void setColors(Color4f[] colors) {
		this.colors = colors;
	}
}
