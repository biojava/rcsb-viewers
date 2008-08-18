//  $Id: ResidueColorByRgb.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: ResidueColorByRgb.java,v $
//  Revision 1.1  2007/02/08 02:38:52  jbeaver
//  version 1.50
//
//  Revision 1.1  2006/09/20 16:50:42  jbeaver
//  first commit - branched from ProteinWorkshop
//
//  Revision 1.2  2006/09/06 04:46:12  jbeaver
//  Added initial capability for labeling ribbons
//
//  Revision 1.1  2006/08/24 17:39:02  jbeaver
//  *** empty log message ***
//
//  Revision 1.2  2006/03/13 15:06:23  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2006/03/09 00:18:54  jbeaver
//  Initial commit
//
//  Revision 1.2  2004/04/09 00:12:54  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.1  2004/02/12 17:25:38  moreland
//  First version.
//
//  Revision 1.0  2004/02/11 18:33:19  moreland
//  First implementation.
//


package org.rcsb.mbt.model.attributes;


import java.util.HashMap;

import org.rcsb.mbt.model.*;



/**
 *  This class implements the ResidueColor interface using a fixed RGB color.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.attributes.IResidueColor
 *  @see	org.rcsb.mbt.model.Residue
 */
public class ResidueColorByRgb
	implements IResidueColor
{
	private float[] defaultColor = null;

	private final HashMap colorByResidue = new HashMap();
	
	private IResidueColor defaultColorGenerator = null;
	
	public ResidueColorByRgb() {
		
	}
	
	/**
	 *  Create an instance of this class using the specified RGB color.
	 */
	public ResidueColorByRgb( final float[] defaultColor )
	{
		this.setDefaultColor( defaultColor );
	}

	/**
	 *  Create an instance of this class using the specified red, green,
	 *  and blue values.
	 */
	public ResidueColorByRgb( final float red, final float green, final float blue )
	{
		this.defaultColor = new float[3];
		this.defaultColor[0] = red;
		this.defaultColor[1] = green;
		this.defaultColor[2] = blue;
	}
	
	public ResidueColorByRgb(final IResidueColor defaultColorGenerator) {
		this.defaultColorGenerator = defaultColorGenerator;
	}

	/**
	 *  Produce a fixed color for the residue.
	 */
	public void getResidueColor( final Residue residue, final float[] color )
	{
		if ( color == null ) {
			throw new IllegalArgumentException( "null color" );
		}

		final float[] savedColor = (float[])this.colorByResidue.get(residue);
		
		if(savedColor != null) {
			color[0] = savedColor[0];
			color[1] = savedColor[1];
			color[2] = savedColor[2];
		} else if(this.defaultColorGenerator != null) {
			this.defaultColorGenerator.getResidueColor(residue, color);
		} else {
			try {
				color[0] = this.defaultColor[0];
				color[1] = this.defaultColor[1];
				color[2] = this.defaultColor[2];
			} catch(final Exception e) {
				System.err.flush();
			}
		}
	}

	public boolean usesDefaultColorGenerator(final Residue r) {
		return !this.colorByResidue.containsKey(r);
	}
	
	public IResidueColor getDefaultColorGenerator() {
		return this.defaultColorGenerator;
	}
	
	/**
	 *  Set the fixed RGB color used to color a residue.
	 */
	public void setDefaultColor( final float[] color )
		throws IllegalArgumentException
	{
		if ( color == null ) {
			throw new IllegalArgumentException( "null color" );
		}

		if(this.defaultColor == null) {
			this.defaultColor = new float[3];
		}
		this.defaultColor[0] = color[0];
		this.defaultColor[1] = color[1];
		this.defaultColor[2] = color[2];
	}
	
	public void setColor(final Residue residue, final float[] color) {
		final float[] color_ = new float[] {color[0], color[1], color[2]};
		
		this.colorByResidue.put(residue, color_);
	}

	public void setDefaultColorGenerator(final IResidueColor defaultColorGenerator) {
		this.defaultColorGenerator = defaultColorGenerator;
	}
}

