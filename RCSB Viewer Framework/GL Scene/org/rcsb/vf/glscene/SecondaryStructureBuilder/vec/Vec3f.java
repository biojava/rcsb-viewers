//  $Id: Vec3f.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//******************************************************************************
// Package
//******************************************************************************

package org.rcsb.vf.glscene.SecondaryStructureBuilder.vec;

//******************************************************************************
// Class
//******************************************************************************
/**
 * Vec3f is simply a data container class for a 3-element vector.
 * It is typically used by generic 3D applications which may or may not
 * use Java3D as the rendering API. This enables applications to be
 * written in an API-independant manner.
 *
 * @author      John L. Moreland
 * @author      John Tate
 * @version     $Revision: 1.1 $
 * @since       JDK1.2.2
 */
public class Vec3f
{
    //**************************************************************************
    // Instance variables
    //**************************************************************************

    public float[] value = new float[3];

    //**************************************************************************
    // Constructors
    //**************************************************************************

    // Not sure why, but we seem to need to explicitly put the empty constructor
    // here...

    public Vec3f()
    {
	// This space deliberately left blank...
    }

    //--------------------------------------------------------------------------

    public Vec3f( final float[] array )
    {
	this.value = array;
    }

    //--------------------------------------------------------------------------

    public Vec3f( final float x, final float y, final float z )
    {
	this.value[0] = x;
	this.value[1] = y;
	this.value[2] = z;
    }

    //**************************************************************************
    // Methods
    //**************************************************************************

    // None... deliberately lightweight.

    public Vec3d toVec3d() {
	return new Vec3d( this.value[0], this.value[1], this.value[2] );
    }
}

