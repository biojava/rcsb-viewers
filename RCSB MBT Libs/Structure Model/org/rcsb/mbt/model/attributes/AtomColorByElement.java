//  $Id: AtomColorByElement.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: AtomColorByElement.java,v $
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
//  Revision 1.8  2004/04/09 00:12:51  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.7  2004/01/29 17:53:38  moreland
//  Updated copyright and class comment block.
//
//  Revision 1.6  2003/09/16 16:34:01  moreland
//  Make unknown elements gray.
//
//  Revision 1.5  2003/09/15 20:23:11  moreland
//  Simplified wording of registered descriptive names.
//
//  Revision 1.4  2003/04/23 23:09:27  moreland
//  Changed get methods from using an index to an object reference for style context.
//
//  Revision 1.3  2003/04/03 18:27:22  moreland
//  Changed Atom field "type" to "element" due to naming and meaning conflict.
//
//  Revision 1.2  2003/02/27 21:45:11  moreland
//  Removed debug print statement.
//
//  Revision 1.1  2003/02/27 21:06:33  moreland
//  Began adding classes for viewable "Styles" (colors, sizes, forms, etc).
//
//  Revision 1.0  2003/02/25 18:33:19  moreland
//  First implementation.
//


package org.rcsb.mbt.model.attributes;


import org.rcsb.mbt.model.Atom;
import org.rcsb.mbt.model.attributes.ElementStyles;
import org.rcsb.mbt.model.attributes.IAtomColor;
import org.rcsb.mbt.model.util.Element;
import org.rcsb.mbt.model.util.PeriodicTable;


/**
 *  This class implements the AtomColor interface by applying a color
 *  to the given atomIndex+Atom by using the ElementColor class.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.attributes.IAtomColor
 *  @see	org.rcsb.mbt.model.Atom
 *  @see	org.rcsb.mbt.model.util.PeriodicTable
 *  @see	org.rcsb.mbt.model.util.Element
 *  @see	org.rcsb.mbt.model.attributes.ElementStyles
 */
public class AtomColorByElement
	implements IAtomColor
{
	public static final String NAME = "By Element";

	// Holds a singleton instance of this class.
	private static AtomColorByElement singleton = null;

	/**
	 *  The constructor is PRIVATE so that the "create" method
	 *  is used to produce a singleton instance of this class.
	 */
	private AtomColorByElement( )
	{
	}

	/**
	 *  Return the singleton instance of this class.
	 */
	public static AtomColorByElement create( )
	{
		if ( AtomColorByElement.singleton == null ) {
			AtomColorByElement.singleton = new AtomColorByElement( );
		}
		return AtomColorByElement.singleton;
	}

	/**
	 *  Produce a color based upon the atom element type.
	 */
	public void getAtomColor( final Atom atom, final float[] color )
	{
		final Element element = PeriodicTable.getElement( atom.element );
		if ( element == null )
		{
			// Unknown element - make it gray.
			color[0] = color[1] = color[2] = 0.5f;
			return;
		}
		final float rgb[] = ElementStyles.getElementColor( element.atomic_number );
		color[0] = rgb[0];
		color[1] = rgb[1];
		color[2] = rgb[2];
	}
}

