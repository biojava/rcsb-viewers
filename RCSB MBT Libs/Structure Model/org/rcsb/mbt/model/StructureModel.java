//  $Id: StructureDocument.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: StructureDocument.java,v $
//  Revision 1.1  2007/02/08 02:38:52  jbeaver
//  version 1.50
//
//  Revision 1.2  2007/01/03 19:33:49  jbeaver
//  *** empty log message ***
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
//  Revision 1.13  2004/04/09 00:12:54  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.12  2004/01/30 22:47:58  moreland
//  Added more detail descriptions for the class block comment.
//
//  Revision 1.11  2004/01/30 21:24:00  moreland
//  Added new diagrams.
//
//  Revision 1.10  2004/01/29 17:53:41  moreland
//  Updated copyright and class comment block.
//
//  Revision 1.9  2003/11/06 23:24:26  moreland
//  Added code to provide garbage collector hints for freeing of Structure objects.
//
//  Revision 1.8  2003/05/16 21:48:58  moreland
//  Fixed bug in removeAllStructures method.
//
//  Revision 1.7  2003/05/16 21:43:00  moreland
//  Added removeAllStructures method.
//
//  Revision 1.6  2003/04/23 23:22:11  moreland
//  Generarally implemented the code that was previously stubbed out.
//
//  Revision 1.5  2002/12/20 22:43:50  moreland
//  Temporarily disabled generation of viewable objects for new geomtry engine testing.
//
//  Revision 1.4  2002/12/16 06:42:41  moreland
//  Changed "fire" methods from private to public so that Viewers can
//  trigger an event to be propagated. This may be undone at some point
//  because a cleaner mechanism may be created to trigger an event after
//  batch modifications are applied to a StructureDocument.
//
//  Revision 1.3  2002/11/14 18:47:33  moreland
//  Corrected "see" document reference.
//
//  Revision 1.2  2002/11/14 18:30:54  moreland
//  First implementation of the StructureDocument class to encapsulate properties.
//
//  Revision 1.1  2002/10/24 18:06:54  moreland
//  Added the beginnings of a StructureDocument and Viewable object API.
//
//  Revision 1.1.1.1  2002/07/16 18:00:18  moreland
//  Imported sources
//


package org.rcsb.mbt.model;



import java.util.ArrayList;
import java.util.Vector;

import org.rcsb.mbt.controllers.app.AppBase;
import org.rcsb.mbt.controllers.update.UpdateController;
import org.rcsb.mbt.controllers.update.UpdateEvent;


/**
 *  This class implements the top-level container for one or more
 *  Structures.
 *  
 *  When a Structure object is added or removed from a StructureDocument,
 *  each registered Viewer will automatically receive a StructureDocumentEvent
 *  telling the Viewer that a given Structure was added (or removed as the case
 *  may be). It is then the responsibility of the Viewer to use the
 *  Structure object reference to display a suitable visual representation
 *  (often this entails getting the StructureMap and then the StructureStyles
 *  object in order to display a representation using the defined colors
 *  and other visual display properties).
 *  <P>
 *  <center>
 *  <img src="doc-files/StructureDocument.jpg" border=0></a>
 *  </center>
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.controllers.update.UpdateEvent
 *  @see	org.rcsb.mbt.model.attributes.StructureStyles
 *  @see	org.rcsb.mbt.model.StructureMap
 *  @see	org.rcsb.mbt.model.Structure
 */
public class StructureModel
{
	//
	// Private variables.
	//

	/**
	 * Stores the repository of Structure objects in this StructureDocument.
	 */
	public class StructureList extends ArrayList<Structure>{};
	
	private StructureList structures = new StructureList();
	public synchronized StructureList getStructures() { return structures; }
	
	/**
	 * @return - whether we have structures in the model.
	 */
	public synchronized boolean hasStructures() { return structures != null && structures.size() > 0; }

	/**
	 *  Add a structure to the list of managed/viewed structures,
	 *  then send an event to inform all registered Viewer objects.
	 */
	public synchronized void addStructure( final Structure structure )
		throws IllegalArgumentException
	{
		if ( structure == null ) {
			throw new IllegalArgumentException( "null structure" );
		}
		if ( this.structures.contains( structure ) ) {
			throw new IllegalArgumentException( "structure already added" );
		}

		this.structures.add( structure );

		UpdateController update = AppBase.sgetUpdateController();
		update.fireUpdateViewEvent(UpdateEvent.Action.STRUCTURE_ADDED, structure);
	}
	
	public synchronized void setStructures(Structure[] structure_array)
	{
		if (structure_array != null)
		{
			UpdateController update = AppBase.sgetUpdateController();
			
			for (Structure struc : structure_array)
			{
			   this.structures.add(struc);
			   update.fireUpdateViewEvent(UpdateEvent.Action.STRUCTURE_ADDED, struc);
			}
			
			update.fireUpdateViewEvent(UpdateEvent.Action.VIEW_UPDATE);
		}
	}

	/**
	 * Remove a Structure from this StructureDocument.
	 * Send the event, first, in case the receiver may want to do something from the list.
	 */
	public synchronized void removeStructure( final Structure structure )
		throws IllegalArgumentException
	{
		if ( structure == null ) {
			throw new IllegalArgumentException( "null structure" );
		}
		if ( ! this.structures.contains( structure ) ) {
			throw new IllegalArgumentException( "structure not found" );
		}
		
		UpdateController update = AppBase.sgetUpdateController();
		update.fireUpdateViewEvent(UpdateEvent.Action.STRUCTURE_REMOVED, structure);

		this.structures.remove( structure );
	}

	/**
	 * Remove all Structures from this StructureDocument.
	 */
	public synchronized void clear()
	{
		structures.clear();
	}
	
	/**
	 * override if you need to modify the residue name output
	 * 
	 * @param residue
	 * @return
	 */
	public String getModifiedResidueName(Residue residue)
	{
		return residue.getCompoundCode();
	}
}

