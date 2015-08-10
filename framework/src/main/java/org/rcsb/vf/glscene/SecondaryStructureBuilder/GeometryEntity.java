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
package org.rcsb.vf.glscene.SecondaryStructureBuilder;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.glu.GLU;

import org.rcsb.mbt.model.StructureComponent;
import org.rcsb.vf.glscene.jogl.DisplayLists;

import com.jogamp.opengl.util.gl2.GLUT;


//***********************************************************************************
// Imports
//***********************************************************************************
//


/**
 * Geometry is the base class for the geometry description of all structure objects in a scene.  
 * <P>
 * @author      Apostol Gramada
 */
public class GeometryEntity {
    protected float quality = 0.5f; // Quality of the rendering in the 0.0 - 1.0 range
    public StructureComponent structureComponent;

    /**
     * Empty constructor for a GeometryEntity object.
     */
    public GeometryEntity(final StructureComponent sc) {
    	this.structureComponent = sc;
    }

    /**
     * Return the BranchGroup object representing the geometrical shape contained by this GeometryEntity object.
     * To be actually overridden in each subclass.
     */    
    public DisplayLists generateJoglGeometry(final GL gl, final GLU glu, final GLUT glut) {
    	return null;
    }

    /**
     * Set the quality parameter.
     */
    public void setQuality( final float quality ) {
	this.quality = quality;
    }

    /**
     * Get the quality parameter.
     */
    public float getQuality() {
	return this.quality;
    }
}


