//  $Id: StructureInfo.java,v 1.3 2006/06/30 22:54:59 milton Exp $
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
//  $Log: StructureInfo.java,v $
//  Revision 1.3  2006/06/30 22:54:59  milton
//  *** empty log message ***
//
//  Revision 1.2  2006/06/30 19:32:34  milton
//  this is pretty close to the final in the kiosk viewer//
//  Revision 1.1  2006/05/10 21:47:43  milton
//  OutReach Code
//
//  Revision 1.1  2006/03/09 00:18:55  jbeaver
//  Initial commit
//
//  Revision 1.7  2004/10/27 20:03:30  moreland
//  Corrected javadoc SEE references.
//
//  Revision 1.6  2004/05/12 23:48:23  moreland
//  Changed release date from int array to String.
//  Removed get/setPrimaryReference and get/setSourceSpecies methods.
//
//  Revision 1.5  2004/05/05 00:42:32  moreland
//  Added StructureInfo support.
//
//  Revision 1.4  2004/04/09 00:17:01  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.3  2004/01/31 19:11:48  moreland
//  Removed outdated programming note.
//
//  Revision 1.2  2004/01/29 17:08:16  moreland
//  Updated copyright and class block comments.
//
//  Revision 1.1  2003/09/22 17:32:41  moreland
//  Added general structure information class.
//
//  Revision 1.0  2003/09/19 23:38:39  moreland
//  First version.
//


package org.rcsb.ks.model;

import java.util.ArrayList;
import java.util.Hashtable;

import org.rcsb.mbt.model.Residue;
import org.rcsb.mbt.model.StructureInfo;



/**
 *  Implements a StructureComponent container for Structure information data.
 *  This information includes data such as authors, dates, and data sources.
 *  <P>
 *  @author	John L. Moreland, Jeff Milton (modified for the outreach viewer)
 *  @see	org.rcsb.mbt.model.Structure
 *  
 *  This may either contain duplicate information in the MBT lib StructureInfo, or
 *  supercede it - not sure.  The *Primary Citation* field looks suspiciously like
 *  the *author* field in the lib version.
 *  
 *  Anyway, the only place the lib version is created is in cifStructureLoader, so
 *  if cif files are being read, it may either be clobbered by or clobber this, but
 *  only for cif files.
 *  
 *  More study required.
 *  
 *  TODO: reconcile this with the MBT lib StructureInfo.
 *  
 *  30-May-08 - rickb
 *  
 */
public class KSStructureInfo extends StructureInfo
{
	//
	// Constructor
	//


	private PrimaryCitation primaryCitation = null;
	
	/**
	 *  Constructs a new StructureInfo object and initializes the fields
	 *  to empty values.
	 */
	public KSStructureInfo( )
	{
	}
	
	public void setPrimaryCitation ( PrimaryCitation _primaryCitation )
	{
		primaryCitation = _primaryCitation;
	}

	private ArrayList descriptors = null;

	public PrimaryCitation getPrimaryCitation() {
		return primaryCitation;
	}

	public void setDescriptors(ArrayList entityDescriptors) {
		descriptors  = entityDescriptors;		
	}
	public void addDescriptor ( EntityDescriptor _descriptor ){
		descriptors.add( _descriptor );
	}
	
	public EntityDescriptor getDescriptor ( String _id )
	{
		for (int i = 0; i < descriptors.size (); i++) {
			EntityDescriptor desc = ( EntityDescriptor ) descriptors.get ( i );
			if ( desc.getEntityId ().equalsIgnoreCase ( _id ))
				return desc;
			
		}
		return null;
	}
	
	
	public ArrayList getDescriptors ()
	{
		return descriptors;
	}

	public EntityDescriptor getDescriptor ( Residue _r ) {
		return null;
		
	}
}

