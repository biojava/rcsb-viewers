//  $Id: StructureStylesEvent.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: StructureStylesEvent.java,v $
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
//  Revision 1.4  2005/11/08 20:58:33  moreland
//  Switched style code to new StructureStyles API.
//
//  Revision 1.3  2004/04/09 00:12:54  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.2  2004/01/29 17:58:23  moreland
//  Updated copyright and class block comments.
//
//  Revision 1.1  2003/04/23 23:02:06  moreland
//  First version.
//
//  Revision 1.1  2002/11/14 18:33:20  moreland
//  First implementation/check-in.
//
//  Revision 1.1.1.1  2002/07/16 18:00:19  moreland
//  Imported sources
//


package org.rcsb.mbt.model.attributes;


import org.rcsb.mbt.model.*;
import org.rcsb.mbt.model.attributes.Style.StyleProperty;


/**
 *  An event generated when changes are made to a StructureStyles object.
 *  This event class is used when viewable style attributes are set.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.attributes.StructureStyles
 */
public class StructureStylesEvent
{
	/**
	 *  The StructureStyles in which the style changed.
	 */
	public StructureStyles structureStyles;

	/**
	 *  The StructureComponent for which style changed.
	 */
	public StructureComponent structureComponent;

	/**
	 *  The main attribute that was changed in the StructureStyles object
	 *  (see StructureStyles for a list of valid states). For example:
	 *  style, selection, or visibility.
	 */
	public int attribute;

	/**
	 *  The specific property that was changed in the Style object
	 *  (see each Style sub-classes for a list of valid states). For example:
	 *  color, radius, label, etc.
	 */
	public StyleProperty property;

	/**
	 *  The style that was changed in the StructureStyles object
	 *  (may be null for an event that effects the entire structure).
	 */
	public Style style;

	/**
	 *  The accelerator flag used for visibility and selections that
	 *  apply to the entire structure (see StructureStyles for a list of
	 *  valid states).
	 */
	public int flag;
}

