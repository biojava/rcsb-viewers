//  $Id: AtomColorByRgb.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: AtomColorByRgb.java,v $
//  Revision 1.1  2007/02/08 02:38:52  jbeaver
//  version 1.50
//
//  Revision 1.1  2006/09/20 16:50:42  jbeaver
//  first commit - branched from ProteinWorkshop
//
//  Revision 1.1  2006/08/24 17:39:02  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2006/03/09 00:18:54  jbeaver
//  Initial commit
//
//  Revision 1.1  2004/05/11 23:02:26  moreland
//  First revision.
//
//  Revision 1.0  2004/05/11 18:33:19  moreland
//  First implementation.
//


package org.rcsb.mbt.model.attributes;


import org.rcsb.mbt.model.*;


/**
 *  This class implements the AtomColor interface using a fixed RGB color.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.attributes.IAtomColor
 *  @see	org.rcsb.mbt.model.Atom
 */
public class AtomColorByRgb
	implements IAtomColor
{
	private final float[] color = new float[3];

	/**
	 *  Create an instance of this class using the specified RGB color.
	 */
	public AtomColorByRgb( final float[] color )
	{
		this.setColor( color );
	}

	/**
	 *  Create an instance of this class using the specified red, green,
	 *  and blue values.
	 */
	public AtomColorByRgb( final float red, final float green, final float blue )
	{
		this.color[0] = red;
		this.color[1] = green;
		this.color[2] = blue;
	}

	/**
	 *  Produce a fixed color for the Atom.
	 */
	public void getAtomColor( final Atom atom, final float[] color )
	{
		if ( color == null ) {
			throw new IllegalArgumentException( "null color" );
		}

		color[0] = this.color[0];
		color[1] = this.color[1];
		color[2] = this.color[2];
	}

	/**
	 *  Set the fixed RGB color used to color a atom.
	 */
	public void setColor( final float[] color )
		throws IllegalArgumentException
	{
		if ( color == null ) {
			throw new IllegalArgumentException( "null color" );
		}

		this.color[0] = color[0];
		this.color[1] = color[1];
		this.color[2] = color[2];
	}
}

