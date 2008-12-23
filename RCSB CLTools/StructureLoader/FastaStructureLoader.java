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



import java.io.*;
import java.net.*;
import java.util.*;

import org.rcsb.mbt.controllers.scene.PdbToNdbConverter;
import org.rcsb.mbt.glscene.geometry.UnitCell;
import org.rcsb.mbt.model.*;
import org.rcsb.mbt.model.util.*;


/**
 *  Implements the StructureLoader interface to enable reading of Fasta files
 *  either from local disk or from a URL.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.structLoader.IStructureLoader
 *  @see	org.rcsb.mbt.structLoader.StructureFactory
 */
public class FastaStructureLoader
	implements IFileStructureLoader
{
	protected String urlString;
	private Structure structure = null;

	//
	// StructureLoader interface methods
	//

	/**
	 * Returns the common name for the loader implementation.
	 * This is the string that might appear in a user-selectable menu.
	 */
	public String getLoaderName( )
	{
		return new String( "Fasta Structure Loader" );
	}


	/**
	 * Returns a reference to a named structure as a Structure object.
	 * The "name" may be interpreted by the specific implementation
	 * of the StructureLoader class. For example, a file loader would
	 * interpret the "name" as a file or URL path, while a database loader
	 * would interpret the "name" as a structure name. This enables a
	 * common interface for all StructureLoader classes, yet does not
	 * prevent a specific implementation from implementing additional
	 * methods. Also, since each StructureLoader sub-class must
	 * implement the "canLoad" method, an application can always
	 * determine if a given loader is capable of delivering a specific
	 * structure or not.
	 */
	public Structure load( final String name )
	{
		if ( ! this.canLoad( name ) ) {
			return null;
		}

		structure = null;
		try
		{	
			final File file = new File( name );
			structure = this.load( file );
		}
		catch( final NullPointerException e )
		{
			try
			{	
				final URL url = new URL( name );
				structure = this.load( url );
			}
			catch( final MalformedURLException e2 )
			{
			}
		}
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
		// Cache the urlString for use by the Structure class
		this.urlString = name;

		if ( ! name.endsWith( ".fsa" ) ) {
			return false;
		}

		if ( name.startsWith( "file:" ) ||
			name.startsWith( "http:" ) ||
			name.startsWith( "ftp:" ) ) {
			return true;
		}

		return false;
	}

	//
	// FileStructureLoader interface methods
	//

	/**
	 * Returns a reference to a Structure read from the given File object.
	 */
	public Structure load( final File file )
	{
		if ( ! this.canLoad( file ) ) {
			return null;
		}

		try
		{
			final InputStream inputStream = new FileInputStream( file );
			return this.load( inputStream );
		}
		catch ( final FileNotFoundException e )
		{
			return null;
		}
		catch ( final SecurityException e )
		{
			return null;
		}
	}


	/**
	 * Returns true if the Structure can be read from the given File object.
	 */
	public boolean canLoad( final File file )
	{
		if ( ! file.exists() ) {
			return false;
		}

		try
		{
			String path = file.getCanonicalPath( );
			path = "file:" + path;
			return this.canLoad( path );
		}
		catch( final IOException e )
		{
			return false;
		}
	}


	/**
	 * Returns a reference to a Structure read from the given URL object.
	 */
	public Structure load( final URL url )
	{
		if ( ! this.canLoad( url ) ) {
			return null;
		}

		try
		{
			final InputStream inputStream = url.openStream( );
			return this.load( inputStream );
		}
		catch( final IOException e )
		{
			return null;
		}
	}


	/**
	 * Returns true if the Structure can be read from the given URL object.
	 */
	public boolean canLoad( final URL url )
	{
		return this.canLoad( url.toExternalForm( ) );
	}

	//
	// FastaStructureLoader methods
	//

	/**
	 * Returns a reference to a Structure read from the given InputStream.
	 */
	public Structure load( final InputStream inputStream )
	{
		// InputStreamReader isr = new InputStreamReader( inputStream );
		// BufferedReader br = new BufferedReader( isr );
		// String line = br.readline( );

		// Read the file into a raw byte buffer

		byte bytes[];
		int byteCount = 0;
		int bytesRead = 0;
		try
		{
			byteCount = inputStream.available( );
			bytes = new byte[ byteCount ];
			bytesRead = inputStream.read( bytes, 0, byteCount );
			inputStream.close( );
			if ( bytesRead != byteCount ) {
				return null;
			}
		}
		catch ( final IOException e )
		{
			try
			{
				inputStream.close( );
			}
			catch ( final IOException e3 )
			{
			}
			return null;
		}

		// Figure out the size of the header

		int headerLength = 0;
		if ( bytes[0] == '>' )
		{
			for ( headerLength=0; headerLength<byteCount; headerLength++ ) {
				if ( (bytes[headerLength] == '\n') || (bytes[headerLength] == '\r') ) {
					break;
				}
			}
			headerLength++; // Include the new line characer
		}

		// Figure out the number of amino acids

		int dataCount = 0;
		for ( int i=headerLength; i<byteCount; i++ ) {
			if ( (bytes[i] != '\n') && (bytes[i] != '\r') ) {
				dataCount++;
			}
		}

		// Copy the raw data bytes into an indexable buffer
		// (ie: strip out the header and the new lines)

		final byte rawData[] = new byte[ dataCount ];
		int j=0;
		for ( int i=headerLength; i<byteCount; i++ ) {
			if ( (bytes[i] != '\n') && (bytes[i] != '\r') ) {
				rawData[j++] = bytes[i];
			}
		}

		structure = new Structure()
		{
			String urlStr = FastaStructureLoader.this.urlString;
			byte data[] = rawData;

			
			public String getUrlString( )
			{
				return this.urlStr;
			}

			
			public int getStructureComponentCount( String scType )
			{
				if ( scType == StructureComponentRegistry.TYPE_RESIDUE ) {
					return this.data.length;
				} else {
					return 0;
				}
			}

			private Vector residues = null;
			
			public StructureComponent getStructureComponentByIndex( String type,
				int index )
				throws IndexOutOfBoundsException, IllegalArgumentException
			{
				if ( type != StructureComponentRegistry.TYPE_RESIDUE ) {
					throw new IllegalArgumentException( "type mismatch" );
				}

				if ( this.residues == null )
				{
					int residueCount = this.getStructureComponentCount(
						StructureComponentRegistry.TYPE_RESIDUE );
					this.residues = new Vector( residueCount );
					// Populate the vector with null so we can do cache checks.
					for ( int i=0; i<residueCount; i++ ) {
						this.residues.add( null );
					}
				}

				// Check the cache.
				Residue residue = (Residue) this.residues.elementAt( index );
				if ( residue != null ) {
					return residue;
				}

				// The residue is not in the cache.
				String compound = AminoAcidInfo.getCodeFromLetter( this.data[index] );
				residue = new Residue( compound );
				residue.setStructure( this );

				// Store the residue into the cache.
				this.residues.setElementAt( residue, index );

				return residue;
			}
		};

		return structure;
	}


	public PdbToNdbConverter getIDConverter() {
		return null;
	}


	public String[] getNonProteinChainIds() {
		return null;
	}


	public Structure getStructure() {
		return null;
	}


	public UnitCell getUnitCell() {
		return null;
	}

}

