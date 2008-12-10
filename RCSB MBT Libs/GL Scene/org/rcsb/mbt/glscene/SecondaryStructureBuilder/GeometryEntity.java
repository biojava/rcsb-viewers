//  $Id: GeometryEntity.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//
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
//  $Log: GeometryEntity.java,v $
//  Revision 1.1  2007/02/08 02:38:52  jbeaver
//  version 1.50
//
//  Revision 1.1  2006/09/20 16:50:43  jbeaver
//  first commit - branched from ProteinWorkshop
//
//  Revision 1.1  2006/08/24 17:39:03  jbeaver
//  *** empty log message ***
//
//  Revision 1.2  2006/05/26 15:28:47  jbeaver
//  removed errors in source
//
//  Revision 1.1  2006/03/09 00:18:55  jbeaver
//  Initial commit
//
//  Revision 1.6  2004/04/09 00:04:28  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.5  2004/01/29 18:05:26  agramada
//  Removed General Atomics from copyright
//
//  Revision 1.4  2004/01/21 22:07:25  agramada
//  Added a label member and necessary methods to set/get.
//
//  Revision 1.3  2003/08/01 16:26:25  agramada
//  Added a quality parameter and its set and get methods in the GeometryEntity
//  class. Also, added stuff to handle thie new parameter in the PsGeometry and
//  SsGeometry classes.
//
//  Revision 1.2  2003/07/08 17:34:21  agramada
//  Added a BranchGroup member and a corresponding get methods to return it.
//  This field store the geometry object once calculated so that it can be
//  retrieved later without having to regenerate it.
//
//  Revision 1.1  2003/06/24 22:19:45  agramada
//  Reorganized the geometry package. Old classes removed, new classes added.
//
//


//***********************************************************************************
// Package
//***********************************************************************************
//
package org.rcsb.mbt.glscene.SecondaryStructureBuilder;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.rcsb.mbt.glscene.jogl.DisplayLists;
import org.rcsb.mbt.model.StructureComponent;

import com.sun.opengl.util.GLUT;


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


