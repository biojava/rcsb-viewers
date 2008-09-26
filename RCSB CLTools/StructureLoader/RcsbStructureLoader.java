//  $Id: RcsbStructureLoader.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: RcsbStructureLoader.java,v $
//  Revision 1.1  2007/02/08 02:38:52  jbeaver
//  version 1.50
//
//  Revision 1.2  2007/01/03 19:33:49  jbeaver
//  *** empty log message ***
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
//  Revision 1.5  2005/05/24 23:14:01  moreland
//  Fixed bug which prematurely terminated the collection of PDB ID codes via FTP.
//
//  Revision 1.4  2004/10/22 23:30:12  moreland
//  Replaced a number of "return null" statements with thrown exceptions.
//
//  Revision 1.3  2004/04/09 00:06:41  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.2  2004/01/29 17:23:33  moreland
//  Updated copyright and class block comments.
//
//  Revision 1.1  2003/12/09 21:23:07  moreland
//  Added RcsbStructureLoader batch loader.
//
//  Revision 1.0  2003/12/08 18:00:19  moreland
//  First version.
//


package org.rcsb.mbt.structLoader;


// Core
import java.net.*;
import java.io.*;
import java.util.*;

import org.rcsb.mbt.controllers.scene.PdbToNdbConverter;
import org.rcsb.mbt.glscene.geometry.UnitCell;
import org.rcsb.mbt.glscene.jogl.Constants;
import org.rcsb.mbt.model.*;

// MBT


/**
 *  Implements a BatchStructureLoader that uses the CifStructureLoader
 *  to laod mmCIF data from the RCSB ftp archive site.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.Structure
 *  @see	org.rcsb.mbt.structLoader.StructureFactory
 */
public class RcsbStructureLoader
	implements IBatchStructureLoader
{
	private Vector idCodes = null;
	private CifStructureLoader cifStructureLoader = null;
	private final String rcsbSitePrefix =
		"ftp://beta.rcsb.org/pub/pdb/uniformity/data/mmCIF.gz/all/";
	private final String rcsbSitePostfix =
		".cif.gz";
	private Structure structure = null;

	/**
	 * Returns the names of all structures which are available to the
	 * RCSB batch structure loader. Applications might use this to enable a
	 * user to select a structure from a list.
	 */
	public Vector getStructureNames( )
	{
		if ( this.idCodes == null )
		{
			URL url = null;
			try
			{
				url = new URL( this.rcsbSitePrefix );
			}
			catch ( final MalformedURLException e )
			{
				return null;
			}

			InputStream inputStream = null;
			try
			{
				inputStream = url.openStream( );
			}
			catch ( final java.io.IOException e )
			{
				return null;
			}

			final InputStreamReader inputStreamReader =
				new InputStreamReader( inputStream );
			final BufferedReader bufferedReader =
				new BufferedReader( inputStreamReader );
			this.idCodes = new Vector( );

			String line = null;
			while ( true )
			{
				try
				{
					line = bufferedReader.readLine( );
					if ( line == null ) {
						break;
					}
				}
				catch ( final java.io.IOException e )
				{
					break;
				}

				if ( ! line.endsWith( ".gz" ) ) {
					continue;
				}
				final int lastSpace = line.lastIndexOf( ' ' );
				line = line.substring( lastSpace+1 );
				final int period = line.indexOf( '.' );
				line = line.substring( 0, period );

				this.idCodes.add( line );
			}
		}

		return this.idCodes;
	}

	/**
	 * Returns the common name for the loader implementation.
	 * This is the string that might appear in a user-selectable menu.
	 */
	public String getLoaderName( )
	{
		return "RCSB Structure Loader";
	}

	/**
	 * Returns a reference to a named structure as a Structure object.
	 * Uses the CifStructureLoader to laod mmCIF data from the RCSB ftp
	 * archive site.
	 */
	public Structure load( final String name )
	{
		if ( name == null ) {
			throw new NullPointerException( "null PDB ID" );
		}
		if ( ! this.canLoad( name ) ) {
			throw new IllegalArgumentException( "unknown PDB ID" );
		}

		if ( this.cifStructureLoader == null )
		{
			try
			{
				this.cifStructureLoader = new CifStructureLoader( );
			}
			catch ( final Exception e )
			{
				throw new NullPointerException( "null CifStructureLoader" );
			}
		}

		final String urlString = Constants.pdbFileBase + name + this.rcsbSitePostfix;

		URL url = null;
		try
		{
			url = new URL( urlString );
		}
		catch ( final MalformedURLException e )
		{
			throw new IllegalArgumentException( "bad PDB ID" );
		}

		structure = cifStructureLoader.load(url);
		return structure;
	}

	/**
	 * Returns true if the loader is capable of loading the structure,
	 * or false otherwise. This enables higher-level code to be able
	 * to build a context sensative menu of only the loaders that can
	 * load a given structure name.
	 */
	public boolean canLoad( final String name )
	{
		if ( name == null ) {
			return false;
		}
//		final Vector names = this.getStructureNames( );
//		if ( names == null ) {
//			return false;
//		}

//		final int nameCount = names.size( );
//		for ( int i=0; i<nameCount; i++ )
//		{
//			final String knownName = (String) names.elementAt( i );
//			if ( name.equalsIgnoreCase( knownName ) ) {
//				return true;
//			}
//		}

		return true;
	}

	public PdbToNdbConverter getIDConverter() {
		return null;
	}

	public String[] getNonProteinChainIds() {
		return null;
	}

	public Structure getStructure() {
		return structure;
	}

	public UnitCell getUnitCell() {
		return null;
	}
}

