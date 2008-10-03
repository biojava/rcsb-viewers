//  $Id: ResidueColorByResidueIndexDna.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: ResidueColorByResidueIndexDna.java,v $
//  Revision 1.1  2007/02/08 02:38:52  jbeaver
//  version 1.50
//
//  Revision 1.1  2006/09/20 16:50:42  jbeaver
//  first commit - branched from ProteinWorkshop
//
//  Revision 1.1  2006/08/24 17:39:03  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2006/03/09 00:18:55  jbeaver
//  Initial commit
//
//  Revision 1.1  2005/10/06 17:07:53  jbeaver
//  Initial commit
//
//  Revision 1.7  2005/06/17 22:17:52  moreland
//  The default color map now uses a new molscript-like but pastel color ramp.
//
//  Revision 1.6  2004/06/18 17:55:39  moreland
//  All uses of InterpolatedColorMap are now non-static.
//
//  Revision 1.5  2004/04/09 00:12:54  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.4  2004/01/29 17:53:41  moreland
//  Updated copyright and class comment block.
//
//  Revision 1.3  2003/09/19 22:28:25  moreland
//  Now applies color based upon the chain-relative instead of global index.
//
//  Revision 1.2  2003/09/15 20:23:13  moreland
//  Simplified wording of registered descriptive names.
//
//  Revision 1.1  2003/09/11 22:23:56  moreland
//  Added ResidueColorByResidueIndex class and made it the default for ResidueColor.
//
//  Revision 1.1  2003/04/23 23:01:39  moreland
//  First version.
//
//  Revision 1.0  2003/02/25 18:33:19  moreland
//  First implementation.
//


package org.rcsb.mbt.model.attributes;


import org.rcsb.mbt.model.*;
import org.rcsb.mbt.model.attributes.*;


/**
 *  This class implements the ResidueColor interface by applying a color
 *  to the given Residue by using ResidueIndex.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.attributes.IResidueColor
 *  @see	org.rcsb.mbt.model.Residue
 */
public class ResidueColorByResidueIndexDna
	implements IResidueColor
{
	public static final String NAME = "By Index";

	// Holds a singleton instance of this class.
	private static ResidueColorByResidueIndexDna singleton = null;

	private IColorMap colorMap =
		new InterpolatedColorMap( InterpolatedColorMap.BLACK_TO_WHITE );

	/**
	 *  The constructor is PRIVATE so that the "create" method
	 *  is used to produce a singleton instance of this class.
	 */
	private ResidueColorByResidueIndexDna( )
	{
	}

	/**
	 *  Return the singleton instance of this class.
	 */
	public static ResidueColorByResidueIndexDna create( )
	{
		if ( ResidueColorByResidueIndexDna.singleton == null ) {
			ResidueColorByResidueIndexDna.singleton = new ResidueColorByResidueIndexDna( );
		}
		return ResidueColorByResidueIndexDna.singleton;
	}

	/**
	 *  Produce a color based upon the residue element type.
	 */
	public void getResidueColor( final Residue residue, final float[] color )
	{
		final StructureMap structureMap = residue.structure.getStructureMap( );
		final Chain chain = structureMap.getChain( residue.getChainId() );
		final int residueCount = chain.getResidueCount( );
		final int residueIndex = chain.getResidueIndex( residue );

		final float value = (float) residueIndex / (float) residueCount;
		this.colorMap.getColor( value, color );
	}

	/**
	 *  Set the ColorMap used to color by Chain-Residue Index.
	 */
	public void setColorMap( final IColorMap colorMap )
		throws IllegalArgumentException
	{
		if ( colorMap == null ) {
			throw new IllegalArgumentException( "null colorMap" );
		}
		this.colorMap = colorMap;
	}

	/**
	 *  Get the ColorMap used to color by Chain-Residue Index.
	 */
	public IColorMap getColorMap( )
	{
		return this.colorMap;
	}
}

