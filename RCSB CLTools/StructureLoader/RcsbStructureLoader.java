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

