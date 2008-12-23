/*
 * BioJava development code
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence. This should
 * be distributed with the code. If you do not have a copy,
 * see:
 *
 * http://www.gnu.org/copyleft/lesser.html
 *
 * Copyright for this code is held jointly by the individual
 * authors. These should be listed in @author doc comments.
 *
 * For more information on the BioJava project and its aims,
 * or to join the biojava-l mailing list, visit the home page
 * at:
 *
 * http://www.biojava.org/
 *
 * This code was contributed from the Molecular Biology Toolkit
 * (MBT) project at the University of California San Diego.
 *
 * Please reference J.L. Moreland, A.Gramada, O.V. Buzko, Qing
 * Zhang and P.E. Bourne 2005 The Molecular Biology Toolkit (MBT):
 * A Modular Platform for Developing Molecular Visualization
 * Applications. BMC Bioinformatics, 6:21.
 *
 * The MBT project was funded as part of the National Institutes
 * of Health PPG grant number 1-P01-GM63208 and its National
 * Institute of General Medical Sciences (NIGMS) division. Ongoing
 * development for the MBT project is managed by the RCSB
 * Protein Data Bank(http://www.pdb.org) and supported by funds
 * from the National Science Foundation (NSF), the National
 * Institute of General Medical Sciences (NIGMS), the Office of
 * Science, Department of Energy (DOE), the National Library of
 * Medicine (NLM), the National Cancer Institute (NCI), the
 * National Center for Research Resources (NCRR), the National
 * Institute of Biomedical Imaging and Bioengineering (NIBIB),
 * the National Institute of Neurological Disorders and Stroke
 * (NINDS), and the National Institute of Diabetes and Digestive
 * and Kidney Diseases (NIDDK).
 *
 * Created on 2007/02/08
 *
 */ 
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

