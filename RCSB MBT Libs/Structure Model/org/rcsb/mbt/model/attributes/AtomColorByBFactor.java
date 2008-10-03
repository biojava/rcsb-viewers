//  $Id: AtomColorByBFactor.java,v 1.1 2007/02/08 02:38:51 jbeaver Exp $
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
//  $Log: AtomColorByBFactor.java,v $
//  Revision 1.1  2007/02/08 02:38:51  jbeaver
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
//  Revision 1.1  2004/11/08 17:28:02  moreland
//  First implementation.
//
//  Revision 1.0  2004/11/02 18:33:19  moreland
//  First implementation.
//


package org.rcsb.mbt.model.attributes;


import org.rcsb.mbt.model.*;


/**
 *  This class implements the AtomColor interface using B-Factor.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.attributes.IAtomColor
 *  @see	org.rcsb.mbt.model.Atom
 */
public class AtomColorByBFactor
	implements IAtomColor
{
	public static final String NAME = "By B-Factor";

	// Holds a singleton instance of this class.
	private static AtomColorByBFactor singleton = null;

	private final IColorMap colorMap =
		new InterpolatedColorMap( InterpolatedColorMap.COLD_TO_HOT );


	/**
	 *  The constructor is PRIVATE so that the "create" method
	 *  is used to produce a singleton instance of this class.
	 */
	private AtomColorByBFactor( )
	{
	}


	/**
	 *  Return the singleton instance of this class.
	 */
	public static AtomColorByBFactor create( )
	{
		if ( AtomColorByBFactor.singleton == null ) {
			AtomColorByBFactor.singleton = new AtomColorByBFactor( );
		}
		return AtomColorByBFactor.singleton;
	}


	/**
	 *  Produce a fixed color for the Atom.
	 */
	public void getAtomColor( final Atom atom, final float[] color )
	{
		if ( atom == null ) {
			throw new NullPointerException( "null atom" );
		}
		if ( color == null ) {
			throw new NullPointerException( "null color" );
		}

		final float bfactor = atom.bfactor / 100.0f;  // Normalize range
		this.colorMap.getColor( bfactor, color );
	}
}

