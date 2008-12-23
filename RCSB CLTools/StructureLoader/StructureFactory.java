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



import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;
import java.io.File;
import java.net.URL;

import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.StructureComponent;
import org.rcsb.mbt.model.StructureComponentRegistry;
import org.rcsb.mbt.model.util.Status;


/**
 *  Provides a central StructureLoader registry and Structure object factory.
 *  To load a Structure given a <B>String</B> (ie: PDB ID), <B>File</B>, or
 *  <B>URL</B>, an application can simply call one of the static <B>load</B>
 *  methods in this class and it will automatically call the <B>load</B>
 *  method on the first loader who's <B>canLoad</B> method returns true.
 *  <P>
 *  <CENTER>
 *  <IMG SRC="doc-files/StructureFactory.jpg">
 *  </CENTER>
 *  <P>
 *  This mechanism provides a single entry point for applications to load data
 *  and to automatically gain the ability to load from new data sources as new
 *  loaders are added to the toolkit.
 *  <P>
 *  This wrapper was also a nice place to add a format-independent mechanism
 *  to <B>verify</B> the integrity of data records. Since some loaders delay
 *  the process of parsing and loading their StructureComponent records until
 *  each record is requested, this "early verify" can be used to catch bad
 *  data before it "leaks" out into other parts of the toolkit and application.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.Structure
 *  @see	org.rcsb.mbt.structLoader.IStructureLoader
 */
public class StructureFactory
	extends Object
{
	// Storage to maintain a registry of StructureLoader objects.
	private static final Hashtable fileStructureLoaders = new Hashtable( );
	private static final Hashtable batchStructureLoaders = new Hashtable( );
	private static IBatchStructureLoader defaultBatchStructureLoader;

	// If set to true, the "verify" method will be called before
	// a structure is returned by a load method. If the verify
	// fails, a null structure will be returned.
	private static boolean doVerify = true;

	static
	{
		// Pre-load the known StructureLoader implementations

		// StructureFactory.addStructureLoader( new TestStructureLoader( ) );

		// StructureFactory.addStructureLoader( new OpenmmsStructureLoader( ) );

		// FASTA
		StructureFactory.addStructureLoader( new FastaStructureLoader( ) );

		// mmCIF
		try
		{
			final CifStructureLoader cifStructureLoader = new CifStructureLoader( );
			StructureFactory.addStructureLoader( cifStructureLoader );
		}
		catch ( final Exception e )
		{
			Status.output( Status.LEVEL_ERROR, e.getMessage() );
		}

		// PDB
		StructureFactory.addStructureLoader( new PdbStructureLoader( ) );

		// RCSB (Batch)
		StructureFactory.addStructureLoader( new RcsbStructureLoader( ) );
	}


	//
	//  StructureLoader management methods
	//

	/**
	 * Given a Hastable object, return an array of keys as String values.
	 */
	private static String[] getHashNames( final Hashtable hash )
	{
		final int count = hash.size( );
		final String names[] = new String[count];
		final Enumeration nameEnum = hash.keys( );
		int i=0;
		while ( nameEnum.hasMoreElements() )
		{
			names[i] = (String) nameEnum.nextElement( );
			i++;
		}
		return names;
	}


	/**
	 * Return a list of currently registered FileStructureLoader names.
	 */
	public static String[] getFileStructureLoaderNames( )
	{
		return StructureFactory.getHashNames( StructureFactory.fileStructureLoaders );
	}


	/**
	 * Return a list of currently registered BatchStructureLoader names.
	 */
	public static String[] getBatchStructureLoaderNames( )
	{
		return StructureFactory.getHashNames( StructureFactory.batchStructureLoaders );
	}


	/**
	 * Return a reference to a named FileStructureLoader object.
	 */
	public static IFileStructureLoader getFileStructureLoader( final String name )
	{
		return (IFileStructureLoader) StructureFactory.fileStructureLoaders.get( name );
	}


	/**
	 * Return a reference to a named BatchStructureLoader object.
	 */
	public static IBatchStructureLoader getBatchStructureLoader( final String name )
	{
		return (IBatchStructureLoader) StructureFactory.batchStructureLoaders.get( name );
	}


	/**
	 * Add (register) a StructureLoader object thus enabling
	 * the StructureFactor class to load data with it.
	 */
	public static void addStructureLoader( final IStructureLoader structureLoader )
	{
		final String loaderName = structureLoader.getLoaderName( );

		if ( structureLoader instanceof IFileStructureLoader )
		{
			StructureFactory.fileStructureLoaders.put( loaderName, structureLoader );
		}

		// Don't do an "else if" here because a loader might
		// implement both batch loading and file loading.

		if ( structureLoader instanceof IBatchStructureLoader )
		{
			StructureFactory.batchStructureLoaders.put( loaderName, structureLoader );
			if ( StructureFactory.defaultBatchStructureLoader == null ) {
				StructureFactory.defaultBatchStructureLoader =
					(IBatchStructureLoader) structureLoader;
			}
		}
	}


	/**
	 * Remove (un-register) a StructureLoader object by its name thus
	 * preventing the StructureFactor class from loadng data with it.
	 */
	public static void removeStructureLoader( final String name )
	{
		StructureFactory.fileStructureLoaders.remove( name );
		StructureFactory.batchStructureLoaders.remove( name );
	}


	/**
	 * Enables an implementation-specific chooser GUI to enable a user
	 * to select a structure to load. For example, a file loader
	 * implementation might open an "Open file" dialog box, while a
	 * database loader implementation might open a selectable list.
	 */
	public static Vector getBatchStructureNames( )
	{
		if ( StructureFactory.defaultBatchStructureLoader == null ) {
			return null;
		}
		return StructureFactory.defaultBatchStructureLoader.getStructureNames( );
	}


	/**
	 *  Set the default BatchStructureLoader to use for subsequent calls
	 *  to the "load" method.
	 */
	public static void setDefaultBatchStructureLoader(
		final IBatchStructureLoader bsl )
	{
		StructureFactory.defaultBatchStructureLoader = bsl;
	}


	//
	// Load Methods
	//


	/**
	 *  Walk through the set of registered FileStructureLoader implementations
	 *  and use the first capable loader to load the Structure the file.
	 */
	public static Structure load( final File file )
	{
		Structure structure = null;
		Status.output( Status.LEVEL_REMARK, "Loading file..." );
		final Enumeration fslEnum = StructureFactory.fileStructureLoaders.elements( );
		while ( fslEnum.hasMoreElements( ) )
		{
			final IFileStructureLoader fsl =
				(IFileStructureLoader) fslEnum.nextElement( );

			if ( fsl.canLoad( file ) )
			{
				// System.err.println( "DEBUG: StructureFactory.load(" + file.toString() + "), using \"" + fsl.getLoaderName() + "\"" );
				try
				{
					structure = fsl.load( file );
					if ( structure == null ) {
						break; // The load failed!
					}
					if ( StructureFactory.doVerify && ! StructureFactory.verify( structure ) )
					{
						structure = null;
						Status.output( Status.LEVEL_ERROR, "StructureFactory.load, verify failed." );
					}
				}
				catch ( final Exception e )
				{
					structure = null;
					Status.output( Status.LEVEL_ERROR, "StructureFactory.load, exception: " + e );
				}
				catch ( final Error e )
				{
					structure = null;
					Status.output( Status.LEVEL_REMARK, "StructureFactory.load, error: " + e );
				}
				if ( structure != null ) {
					break; // The load worked!
				}
			}
		}

		if ( structure == null ) {
			Status.output( Status.LEVEL_ERROR, "StructureFactory.load failed!" );
		} else {
			Status.output( Status.LEVEL_REMARK, "Done loading file." );
		}

		return structure;
	}


	/**
	 *  Walk through the set of registered FileStructureLoader implementations
	 *  and use the first capable loader to load the Structure from the URL.
	 */
	public static Structure load( final URL url )
	{
		Structure structure = null;
		Status.output( Status.LEVEL_REMARK, "Loading URL..." );
		final Enumeration fslEnum = StructureFactory.fileStructureLoaders.elements( );
		while ( fslEnum.hasMoreElements( ) )
		{
			final IFileStructureLoader fsl =
				(IFileStructureLoader) fslEnum.nextElement( );

			if ( fsl.canLoad( url ) )
			{
				// System.err.println( "DEBUG: StructureFactory.load(URL), using \"" + fsl.getLoaderName() + "\"" );
				try
				{
					structure = fsl.load( url );
					if ( structure == null ) {
						break; // The load failed!
					}
					if ( StructureFactory.doVerify && ! StructureFactory.verify( structure ) )
					{
						structure = null;
						Status.output( Status.LEVEL_ERROR, "StructureFactory.load, verify failed." );
					}
				}
				catch ( final Exception e )
				{
					structure = null;
					Status.output( Status.LEVEL_ERROR, "StructureFactory.load, exception: " + e );
				}
				catch ( final Error e )
				{
					structure = null;
					Status.output( Status.LEVEL_ERROR, "StructureFactory.load, error: " + e );
				}
				if ( structure != null ) {
					break; // The load worked!
				}
			}
		}

		if ( structure == null ) {
			Status.output( Status.LEVEL_ERROR, "StructureFactory.load failed!" );
		} else {
			Status.output( Status.LEVEL_REMARK, "Done loading URL." );
		}

		return structure;
	}


	/**
	 *  Walk through all registered StructureLoader implementations
	 *  and use the first capable loader to load the Structure.
	 */
	public static Structure load( final String name )
	{
		Structure structure = null;
		Status.output( Status.LEVEL_REMARK, "Loading " + name + "..." );

		// Try the file loaders
		final Enumeration fslEnum = StructureFactory.fileStructureLoaders.elements( );
		while ( fslEnum.hasMoreElements( ) )
		{
			final IFileStructureLoader fsl =
				(IFileStructureLoader) fslEnum.nextElement( );

			if ( fsl.canLoad( name ) )
			{
				// System.err.println( "DEBUG: StructureFactory.load(" + name + "), using \"" + fsl.getLoaderName() + "\"" );
				try
				{
					structure = fsl.load( name );
					if ( structure == null ) {
						break; // The load failed!
					}
					if ( StructureFactory.doVerify && ! StructureFactory.verify( structure ) )
					{
						structure = null;
						Status.output( Status.LEVEL_ERROR, "StructureFactory.load, verify failed." );
					}
				}
				catch ( final Exception e )
				{
					structure = null;
					Status.output( Status.LEVEL_ERROR, "StructureFactory.load, exception: " + e );
				}
				catch ( final Error e )
				{
					structure = null;
					Status.output( Status.LEVEL_REMARK, "StructureFactory.load, error: " + e );
				}
				if ( structure != null ) {
					break; // The load worked!
				}
			}
		}

		if ( structure != null )
		{
			Status.output( Status.LEVEL_REMARK, "Done loading URL." );
			return structure;
		}

		// Try the batch loaders
		final Enumeration bslEnum = StructureFactory.batchStructureLoaders.elements( );
		while ( bslEnum.hasMoreElements( ) )
		{
			final IBatchStructureLoader bsl =
				(IBatchStructureLoader) bslEnum.nextElement( );

			if ( bsl.canLoad( name ) )
			{
				// System.err.println( "DEBUG: StructureFactory.load(" + name + "), using \"" + bsl.getLoaderName() + "\"" );
				try
				{
					structure = bsl.load( name );
					if ( structure == null ) {
						break; // The load failed!
					}
					if ( StructureFactory.doVerify && ! StructureFactory.verify( structure ) )
					{
						structure = null;
						Status.output( Status.LEVEL_ERROR, "StructureFactory.load, verify failed." );
					}
				}
				catch ( final Exception e )
				{
					structure = null;
					Status.output( Status.LEVEL_ERROR, "StructureFactory.load, exception: " + e );
				}
				catch ( final Error e )
				{
					structure = null;
					Status.output( Status.LEVEL_REMARK, "StructureFactory.load, error: " + e );
				}
				if ( structure != null ) {
					break; // The load worked!
				}
			}
		}

		if ( structure == null ) {
			Status.output( Status.LEVEL_ERROR, "StructureFactory.load failed!" );
		} else {
			Status.output( Status.LEVEL_REMARK, "Done loading " + name );
		}

		return structure;
	}


	/**
	 * If set to true, the "verify" method will be called before
	 * structure is returned by a load method. If the verify
	 * fails, a null structure will be returned.
	 */
	public static void setVerify( final boolean flag )
	{
		StructureFactory.doVerify = flag;
	}

	/**
	 * If set to true, the "verify" method will be called before
	 * structure is returned by a load method. If the verify
	 * fails, a null structure will be returned.
	 */
	public static boolean getVerify( )
	{
		return StructureFactory.doVerify;
	}

	/**
	 *  Walk through the specified Structure to make sure
	 *  that all StructureComponent types supported by the
	 *  the Structure are returned as non-null and that all "get"
	 *  requests do not throw any exceptions.
	 *  <P>
	 *  This is particularly useful when a StructureLoader
	 *  delays processing of the raw records until requested.
	 *  This method will force the processing to happen and catch
	 *  any thrown exceptions.
	 *  <P>
	 */
	public static boolean verify( final Structure structure )
	{
		if ( structure == null ) {
			return false;
		}
    	final Enumeration types = StructureComponentRegistry.getTypeNames( );
		try
		{
			while ( types.hasMoreElements() )
			{
				final String type = (String) types.nextElement( );
				final int count = structure.getStructureComponentCount( type );
				StructureComponent structureComponent = null;
				for ( int i=0; i<count; i++ )
				{
					structureComponent =
						structure.getStructureComponentByIndex( type, i );
					if ( structureComponent == null )
					{
						final String msg = "Null " + type + " at index " + i;
						Status.output( Status.LEVEL_ERROR, msg  );
						return false;
					}
				}
			}
			return true;
		}
		catch ( final Exception e )
		{
			Status.output( Status.LEVEL_ERROR, e.toString() );
			return false;
		}
	}
}

