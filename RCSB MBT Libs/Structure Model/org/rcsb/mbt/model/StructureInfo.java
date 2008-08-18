//  $Id: StructureInfo.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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


package org.rcsb.mbt.model;


/**
 *  Implements a StructureComponent container for Structure information data.
 *  This information includes data such as authors, dates, and data sources.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.Structure
 */
public class StructureInfo
{
	//
	// Constructor
	//

	/**
	 *  Constructs a new StructureInfo object and initializes the fields
	 *  to empty values.
	 */
	public StructureInfo( )
	{
	}

	//
	// StructureInfo fields
	//

	/**
	 *  The unique ID code by which this entry is commonly known.
	 *  For example: "2CPK"
	 */
	private String idCode = null;

	/**
	 *  The short name of this structure suitable for displaying in a menu.
	 *  For example, "Protein Kinase (2CPK)"
	 *  (Catalytic Subunit)".
	 */
	private String shortName = null;

	/**
	 *  The long name of this structure suitable as a unique description.
	 *  For example, "c-AMP-Dependent Protein Kinase (E.C. 2.7.1.37) (cAPK)
	 *  (Catalytic Subunit)".
	 */
	private String longName = null;


	/**
	 *  The date on which this entry was released or deposited.
	 *  For example, "02-JUN-93".
	 */
	private String releaseDate = null;

	/**
	 *  The authors of this entry.
	 *  For example, "D. R. Knighton, J. Zheng, L. F. Ten Eyck, V. A. Ashford,
	 *  N.-H. Xuong, S. S. Taylor, J. M. Sowadski".
	 */
	private String authors = null;

	/**
	 *  The exerimental method used to determine this compound.
	 *  For example, "X-ray Diffraction".
	 */
	private String determinationMethod = null;


	//
	// StructureInfo methods
	//


	/**
	 *  Set the PDB-ID code by which this entry is registered.
	 *  For example, "2CPK", "4HHB", "10MH", etc.
	 */
	public void setIdCode( final String code )
	{
		if ( code != null ) {
			this.idCode = code;
		}
	}


	/**
	 *  Get the PDB-ID code by which this entry is registered.
	 *  For example, "2CPK", "4HHB", "10MH", etc.
	 */
	public String getIdCode( )
	{
		return this.idCode;
	}


	/**
	 *  Set the short name of this structure suitable for displaying in a menu.
	 *  For example, "Protein Kinase (2CPK)"
	 *  (Catalytic Subunit)".
	 */
	public void setShortName( final String name )
	{
		if ( name != null ) {
			this.shortName = name;
		}
	}


	/**
	 *  Get the short name of this structure suitable for displaying in a menu.
	 *  For example, "Protein Kinase (2CPK)"
	 *  (Catalytic Subunit)".
	 */
	public String getShortName( )
	{
		return this.shortName;
	}


	/**
	 *  Set the long name of this structure suitable as a unique description.
	 *  For example, "c-AMP-Dependent Protein Kinase (E.C. 2.7.1.37) (cAPK)
	 *  (Catalytic Subunit)".
	 */
	public void setLongName( final String name )
	{
		if ( name != null ) {
			this.longName = name;
		}
	}


	/**
	 *  Get the long name of this structure suitable as a unique description.
	 *  For example, "c-AMP-Dependent Protein Kinase (E.C. 2.7.1.37) (cAPK)
	 *  (Catalytic Subunit)".
	 */
	public String getLongName( )
	{
		return this.longName;
	}


	/**
	 *  Set the date on which this entry was released or deposited.
	 *  For example, "02-JUN-93".
	 */
	public void setReleaseDate( final String date )
	{
		this.releaseDate = date;
	}


	/**
	 *  Get the date on which this entry was released or deposited.
	 *  For example, "02-JUN-93".
	 */
	public String getReleaseDate( )
	{
		return this.releaseDate;
	}


	/**
	 *  Set the authors of this entry.
	 *  For example, "D. R. Knighton, J. Zheng, L. F. Ten Eyck, V. A. Ashford,
	 *  N.-H. Xuong, S. S. Taylor, J. M. Sowadski".
	 */
	public void setAuthors( final String names )
	{
		if ( names != null ) {
			this.authors = names;
		}
	}


	/**
	 *  Get the authors of this entry.
	 *  For example, "D. R. Knighton, J. Zheng, L. F. Ten Eyck, V. A. Ashford,
	 *  N.-H. Xuong, S. S. Taylor, J. M. Sowadski".
	 */
	public String getAuthors( )
	{
		return this.authors;
	}


	/**
	 *  Set the exerimental method used to determine this compound.
	 *  For example, "X-ray Diffraction".
	 */
	public void setDeterminationMethod( final String method )
	{
		if ( method != null ) {
			this.determinationMethod = method;
		}
	}


	/**
	 *  Get the exerimental method used to determine this compound.
	 *  For example, "X-ray Diffraction".
	 */
	public String getDeterminationMethod( )
	{
		return this.determinationMethod;
	}
}

