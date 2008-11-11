//  $Id: ResidueColorByFragmentType.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: ResidueColorByFragmentType.java,v $
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
//  Revision 1.2  2005/06/17 22:17:52  moreland
//  The default color map now uses a new molscript-like but pastel color ramp.
//
//  Revision 1.1  2004/05/14 22:53:53  moreland
//  First version.
//
//  Revision 1.0  2004/05/14 18:33:19  moreland
//  First implementation.
//


package org.rcsb.mbt.model.attributes;


import java.util.*;

import org.rcsb.mbt.model.*;
import org.rcsb.mbt.model.StructureComponentRegistry.ComponentType;



/**
 *  This class implements the ResidueColor interface by applying a color
 *  to the given Residue by using the secondary structure Fragment type.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.attributes.IResidueColor
 *  @see	org.rcsb.mbt.model.Residue
 */
public class ResidueColorByFragmentType
	implements IResidueColor
{
	public static final String NAME = "By Fragment Type";

	// Holds a singleton instance of this class.
	private static ResidueColorByFragmentType singleton = null;

	// Color hash for the different secondary structure fragment types.
    public Hashtable fragmentHash = null;


	/**
	 *  The constructor is PRIVATE so that the "create" method
	 *  is used to produce a singleton instance of this class.
	 */
	private ResidueColorByFragmentType( )
	{
    	this.fragmentHash = new Hashtable( );

    	this.fragmentHash.put( ComponentType.COIL,
			new float [] { 1.00f, 0.50f, 0.50f }  // Red (Sat=50%)
		);

    	this.fragmentHash.put( ComponentType.HELIX,
			new float [] { 0.50f, 1.00f, 0.50f }   // Green (Sat=50%)
		);

    	this.fragmentHash.put( ComponentType.STRAND,
			new float [] { 0.50f, 0.50f, 1.00f }   // Blue (Sat=50%)
		);

    	this.fragmentHash.put( ComponentType.TURN,
			new float [] { 0.50f, 1.00f, 1.00f }   // Cyan (Sat=50%)
		);

    	this.fragmentHash.put( ComponentType.UNDEFINED_CONFORMATION,
			new float [] { 0.66f, 0.66f, 0.66f }   // Gray (Bri=66%)
		);
	}


	/**
	 *  Return the singleton instance of this class.
	 */
	public static ResidueColorByFragmentType create( )
	{
		if ( ResidueColorByFragmentType.singleton == null ) {
			ResidueColorByFragmentType.singleton = new ResidueColorByFragmentType( );
		}

		return ResidueColorByFragmentType.singleton;
	}


	/**
	 *  Produce a color based upon the residue element type.
	 */
	public void getResidueColor( final Residue residue, final float[] color )
	{
    	final float[] typeColor = (float[]) this.fragmentHash.get(
			residue.getConformationType()
		);

    	if ( typeColor != null )
		{
			color[0] = typeColor[0];
			color[1] = typeColor[1];
			color[2] = typeColor[2];
		}
    	else // ( typeColor == null )
		{
			color[0] = color[1] = color[2] = 0.5f;
		}
	}


	/**
	 *  Set the color used to color the specified fragment type.
	 */
	public void setFragmentTypeColor( final String type, final float[] color )
		throws IllegalArgumentException
	{
		if ( type == null ) {
			throw new IllegalArgumentException( "null type" );
		}
		if ( color == null ) {
			throw new IllegalArgumentException( "null color" );
		}
		if ( color.length != 3 ) {
			throw new IllegalArgumentException( "color.length != 3" );
		}
		if ( color[0] < 0.0 ) {
			throw new IllegalArgumentException( "color[0] < 0.0" );
		}
		if ( color[0] > 1.0 ) {
			throw new IllegalArgumentException( "color[0] > 1.0" );
		}
		if ( color[1] < 0.0 ) {
			throw new IllegalArgumentException( "color[1] < 0.0" );
		}
		if ( color[1] > 1.0 ) {
			throw new IllegalArgumentException( "color[1] > 1.0" );
		}
		if ( color[2] < 0.0 ) {
			throw new IllegalArgumentException( "color[2] < 0.0" );
		}
		if ( color[2] > 1.0 ) {
			throw new IllegalArgumentException( "color[2] > 1.0" );
		}

    	this.fragmentHash.put( type, color );
	}


	/**
	 *  Get the ColorMap used to color by Chain-Residue Index.
	 */
	public float[] getFragmentTypeColor( final String type )
		throws IllegalArgumentException
	{
		if ( type == null ) {
			throw new IllegalArgumentException( "null type" );
		}

    	return (float[]) this.fragmentHash.get( type );
	}
}

