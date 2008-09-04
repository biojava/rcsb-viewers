//  $Id: Viewer.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: Viewer.java,v $
//  Revision 1.1  2007/02/08 02:38:52  jbeaver
//  version 1.50
//
//  Revision 1.1  2006/09/20 16:50:43  jbeaver
//  first commit - branched from ProteinWorkshop
//
//  Revision 1.1  2006/08/24 17:39:03  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2006/03/09 00:18:55  jbeaver
//  Initial commit
//
//  Revision 1.7  2004/04/09 00:06:11  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.6  2004/01/29 18:12:52  moreland
//  Updated copyright and class comment block.
//
//  Revision 1.5  2003/04/23 23:34:44  moreland
//  Removed processSelectionEvent and processViewableEvent methods.
//
//  Revision 1.4  2002/11/14 18:48:46  moreland
//  Corrected "see" document reference.
//
//  Revision 1.3  2002/11/14 18:18:25  moreland
//  Changed Viewer from class to interface.
//  Added new event callback methods.
//  Changed old "MbtController" references to new "StructureDocument" class.
//
//  Revision 1.2  2002/10/24 18:12:30  moreland
//  Changed base class from Object to JPanel to enable any Viewer sub-class to
//  be added as a GUI object. But, note that an instantiation of a Viewer
//  sub-class does not necessarily HAVE to be added to a GUI interface (it
//  can remain "invisible").
//
//  Revision 1.1.1.1  2002/07/16 18:00:21  moreland
//  Imported sources
//
//  Revision 1.0  2002/06/10 23:38:39  moreland
//


package org.rcsb.mbt.controllers.update;

import org.rcsb.mbt.model.StructureModel;


/**
 *  Anything that wants to be notified when the model changes (structures added, removed)
 *  will implement this interface
 *  
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.StructureModel
 *  @see	org.rcsb.mbt.controllers.update.UpdateEvent
 */
public interface IUpdateListener
{
	/**
	 * Process a StructureDocumentEvent message.
	 */
	public void handleUpdateEvent( UpdateEvent evt );
}

