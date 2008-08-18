//  $Id: Element.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: Element.java,v $
//  Revision 1.1  2007/02/08 02:38:52  jbeaver
//  version 1.50
//
//  Revision 1.1  2006/09/20 16:50:43  jbeaver
//  first commit - branched from ProteinWorkshop
//
//  Revision 1.2  2006/09/02 18:52:28  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2006/08/24 17:39:03  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2006/03/09 00:18:55  jbeaver
//  Initial commit
//
//  Revision 1.4  2004/04/09 00:15:20  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.3  2004/01/29 17:29:07  moreland
//  Updated copyright and class block comments.
//
//  Revision 1.2  2003/11/20 21:37:30  moreland
//  Added custom toString method.
//
//  Revision 1.1  2003/02/19 02:37:43  moreland
//  Added basic atomic element and periodic table information classes.
//
//  Revision 1.0  2003/02/18 18:06:54  moreland
//  First version.
//


package org.rcsb.mbt.model.util;


/**
 *  A container object to hold information about an element
 *  from the periodic table.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.util.PeriodicTable
 */
public class Element
{
	public String name = null;        // eg: "Helium"
	public String symbol = null;      // eg: "He"
	public int atomic_number = 0;     // eg: 2
	public double atomic_weight = 0;  // eg: 4.002602
	public int group_number = 0;      // eg: 18
	public String group_name = null;  // eg: "Noble gas"
	public int period_number = 0;     // eg: 1
	public String block = null;       // eg: "p-block"

	/**
	 *  Construct an Element object from the given attribute values.
	 */
	public Element(
		final String name,
		final String symbol,
		final int atomic_number,
		final double atomic_weight,
		final int group_number,
		final String group_name,
		final int period_number,
		final String block
	)
	{
		this.name = name;
		this.symbol = symbol;
		this.atomic_number = atomic_number;
		this.atomic_weight = atomic_weight;
		this.group_number = group_number;
		this.group_name = group_name;
		this.period_number = period_number;
		this.block = block;
	}

	/**
	 *  Return a String representation of this object.
	 */
	
	public String toString( )
	{
		return "{ " +
			"name=" + this.name + ", " +
			"symbol=" + this.symbol + ", " +
			"atomic_number=" + this.atomic_number + ", " +
			"atomic_weight=" + this.atomic_weight + ", " +
			"group_number=" + this.group_number + ", " +
			"group_name=" + this.group_name + ", " +
			"period_number=" + this.period_number + ", " +
			"block=" + this.block + " }";
	}
}

