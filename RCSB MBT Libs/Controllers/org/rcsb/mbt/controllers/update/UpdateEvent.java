//  $Id: StructureDocumentEvent.java,v 1.1 2007/02/08 02:38:51 jbeaver Exp $
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
//  $Log: StructureDocumentEvent.java,v $
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
//  Revision 1.6  2004/04/09 00:12:54  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.5  2004/01/29 17:53:42  moreland
//  Updated copyright and class comment block.
//
//  Revision 1.4  2003/05/14 01:13:21  moreland
//  Fixed CHANGE_REMOVED value so it is not the same as CHANGE_ADDED (cut and paste bug!)
//
//  Revision 1.3  2003/04/24 00:22:09  moreland
//  Corrected Viewer SEE reference.
//
//  Revision 1.2  2003/04/23 23:23:13  moreland
//  Generarally implemented the code that was previously stubbed out.
//
//  Revision 1.1  2002/11/14 18:33:20  moreland
//  First implementation/check-in.
//
//  Revision 1.1.1.1  2002/07/16 18:00:19  moreland
//  Imported sources
//


package org.rcsb.mbt.controllers.update;

import org.rcsb.mbt.model.Structure;


/**
 *  An event generated when structural changes are made to a StructureDocument.
 *  This event class is used when Viewer or Structure objects are
 *  added or removed from a StructureDocument. But, this event type is NOT
 *  used when the internal state of any of those objects change (see the
 *  StructureEvent, StructureMapEvent, and StructureStyleEvent classes).
 * 
 *  @author rickb
 *  @see	org.rcsb.mbt.model.StructureModel
 *  @see	org.rcsb.mbt.controllers.update.IUpdateListener
 *  @see	org.rcsb.mbt.model.Structure
 */
public class UpdateEvent
{
	 /**
	  * The possible actions for this event type
	  * EXTENDED is for derived classes to use.
	  * 
	  * @author rickb
	  *
	  */
	 public enum Action { STRUCTURE_ADDED, STRUCTURE_REMOVED, VIEW_ADDED, VIEW_REMOVED, VIEW_RESET, VIEW_UPDATE, CLEAR_ALL, EXTENDED }
	 
	 /**
	  * Constructor - takes an action
	  * @param action = the action to apply
	  */
	 public UpdateEvent(final Action action)
	 {
		 this.action = action;
		 this.view = null;
		 this.structure = null;
	 }
	 
	 /**
	  * Constructor 
	  * @param action - {@inheritDoc}
	  * @param structure - the affected structure
	  */
	 public UpdateEvent(final Action action, final Structure structure)
	 {
		 this.action = action;
		 this.structure = structure;
		 this.view = null;
	 }
	 
	 /**
	  * Constructor
	  * @param action - {@inheritDoc}
	  * @param view - the affected view
	  */
	 public UpdateEvent(final Action action, final IUpdateListener view)
	 {
		 this.action = action;
		 this.view = view;
		 this.structure = null;
	 }
	 
	/**
	 * The Structure object that is affected in this change.
	 */
	 public Structure structure;
	 
	 /**
	  * The view affected in this change.
	  */
	 public IUpdateListener view;
	 
	 /**
	  * The action taken
	  */
	 public Action action;
}

