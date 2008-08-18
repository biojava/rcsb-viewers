//  $Id: ResidueColorByResidueCompound.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: ResidueColorByResidueCompound.java,v $
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
//  Revision 1.8  2005/11/08 20:58:33  moreland
//  Switched style code to new StructureStyles API.
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


import java.awt.Color;
import java.util.TreeMap;

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
public class ResidueColorByResidueCompound
	implements IResidueColor
{
	public static final String NAME = "By Compound";

	// Holds a singleton instance of this class.
	private static ResidueColorByResidueCompound singleton = null;

	public static final TreeMap colorByCompound = new TreeMap();
	private static final Color unknownColor = new Color(204, 204, 204);
	
	{
		final Object[] mapping = {
			"phe" , new Color(153, 255, 153),
			"val" , new Color(0, 204, 0),
			"ala" , new Color(0, 153, 0),
			"ili" , new Color(51, 255, 51),
			"leu" , new Color(51, 255, 51),
			"pro" , new Color(204, 204, 0),
			"met" , new Color(51, 255, 51),
			"asp" , new Color(204, 0, 0),
			"glu" , new Color(255, 51, 51),
			"arg" , new Color(255, 204, 51),
			"lys" , new Color(255, 153, 51),
			"ser" , new Color(0, 0, 255),
			"thr" , new Color(0, 102, 255),
			"asn" , new Color(0, 153, 204),
			"gln" , new Color(0, 204, 255),
			"cys" , new Color(255, 51, 204),
			"tyr" , new Color(0, 255, 204),
			"his" , new Color(204, 102, 255),
			"trp" , new Color(255, 255, 0),
			"gly" , new Color(153, 153, 153),
			"unk" , new Color(204, 204, 204),
			"asx" , new Color(255, 255, 255),
			"glx" , new Color(255, 255, 255)
		};
		
		for(int i = 0; i < mapping.length; i += 2) {
			ResidueColorByResidueCompound.colorByCompound.put(mapping[i], mapping[i + 1]);
		}	
	}
	
	/**
	 *  The constructor is PRIVATE so that the "create" method
	 *  is used to produce a singleton instance of this class.
	 */
	private ResidueColorByResidueCompound( )
	{
	}

	/**
	 *  Return the singleton instance of this class.
	 */
	public static ResidueColorByResidueCompound create( )
	{
		if ( ResidueColorByResidueCompound.singleton == null ) {
			ResidueColorByResidueCompound.singleton = new ResidueColorByResidueCompound( );
		}
		return ResidueColorByResidueCompound.singleton;
	}

	/**
	 *  Produce a color based upon the residue element type.
	 */
	public void getResidueColor( final Residue residue, final float[] color )
	{
		final Color colorOb = (Color)ResidueColorByResidueCompound.colorByCompound.get(residue.getCompoundCode().toLowerCase());
		if(colorOb != null) {
			colorOb.getColorComponents(color);
		} else {
			ResidueColorByResidueCompound.unknownColor.getColorComponents(color);
		}
	}
}

