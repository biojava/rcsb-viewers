//  $Id: ResidueColorByHydrophobicity.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: ResidueColorByHydrophobicity.java,v $
//  Revision 1.1  2007/02/08 02:38:52  jbeaver
//  version 1.50
//
//  Revision 1.1  2006/09/20 16:50:42  jbeaver
//  first commit - branched from ProteinWorkshop
//
//  Revision 1.1  2006/08/24 17:39:02  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2006/03/09 00:18:55  jbeaver
//  Initial commit
//
//  Revision 1.5  2004/06/18 17:55:39  moreland
//  All uses of InterpolatedColorMap are now non-static.
//
//  Revision 1.4  2004/04/09 00:12:54  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.3  2004/01/29 17:53:41  moreland
//  Updated copyright and class comment block.
//
//  Revision 1.2  2003/09/15 20:23:12  moreland
//  Simplified wording of registered descriptive names.
//
//  Revision 1.1  2003/04/23 23:01:39  moreland
//  First version.
//
//  Revision 1.0  2003/02/25 18:33:19  moreland
//  First implementation.
//


package org.rcsb.mbt.model.attributes;


import org.rcsb.mbt.model.*;


/**
 *  This class implements the ResidueColor interface by applying a color
 *  to the given Residue by using hydrophobicity.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.attributes.IResidueColor
 *  @see	org.rcsb.mbt.model.Residue
 */
public class ResidueColorByHydrophobicity
	implements IResidueColor
{
	public static final String NAME = "By Hydrophobicity";

	// Holds a singleton instance of this class.
	private static ResidueColorByHydrophobicity singleton = null;

	public InterpolatedColorMap colorMap =
		new InterpolatedColorMap( InterpolatedColorMap.COLD_TO_HOT );

	/**
	 *  The constructor is PRIVATE so that the "create" method
	 *  is used to produce a singleton instance of this class.
	 */
	private ResidueColorByHydrophobicity( )
	{
	}

	/**
	 *  Return the singleton instance of this class.
	 */
	public static ResidueColorByHydrophobicity create( )
	{
		if ( ResidueColorByHydrophobicity.singleton == null ) {
			ResidueColorByHydrophobicity.singleton = new ResidueColorByHydrophobicity( );
		}
		return ResidueColorByHydrophobicity.singleton;
	}

	/**
	 *  Produce a color based upon the residue element type.
	 */
	public void getResidueColor( final Residue residue, final float[] color )
	{
		final float hydrophobicity = residue.getHydrophobicity( );
		this.colorMap.getColor( hydrophobicity, color );
	}

	/**
	 *  Set the ColorMap used to color by hydrophobicity.
	 */
	public void setColorMap( final InterpolatedColorMap colorMap )
		throws IllegalArgumentException
	{
		if ( colorMap == null ) {
			throw new IllegalArgumentException( "null colorMap" );
		}
		this.colorMap = colorMap;
	}

	/**
	 *  Get the ColorMap used to color by hydrophobicity.
	 */
	public InterpolatedColorMap getColorMap( )
	{
		return this.colorMap;
	}
}

