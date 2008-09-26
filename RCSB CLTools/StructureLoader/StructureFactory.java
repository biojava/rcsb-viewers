//  $Id: StructureFactory.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: StructureFactory.java,v $
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
//  Revision 1.15  2004/09/09 16:22:34  moreland
//  Corrected Status.output statement.
//
//  Revision 1.14  2004/08/30 17:58:32  moreland
//  Improved exception handling and error messages.
//
//  Revision 1.13  2004/04/09 00:06:41  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.12  2004/01/31 19:59:59  moreland
//  Updated comments to reflect the new diagram and updated functionality.
//
//  Revision 1.11  2004/01/31 19:32:51  moreland
//  Corrected missing close quote in html img tag.
//
//  Revision 1.10  2004/01/31 19:31:08  moreland
//  Added new diagram.
//
//  Revision 1.9  2004/01/29 17:23:34  moreland
//  Updated copyright and class block comments.
//
//  Revision 1.8  2004/01/08 22:12:59  moreland
//  Improved exception handling for bad data.
//  Added "verify" method to StructureFactory to check StructureComponent integrity.
//
//  Revision 1.7  2003/12/09 21:23:07  moreland
//  Added RcsbStructureLoader batch loader.
//
//  Revision 1.6  2003/11/20 21:36:49  moreland
//  Added code to call Status.output to show users some minimal load progress.
//
//  Revision 1.5  2003/11/10 22:27:16  moreland
//  Better handles Exception(s) when the mmCIF dictionary can't be found or parsed.
//
//  Revision 1.4  2003/02/07 18:29:40  moreland
//  Added some missing method comment headers.
//
//  Revision 1.3  2003/01/13 23:43:37  moreland
//  Commented out debug/print statements.
//
//  Revision 1.2  2002/10/24 18:00:51  moreland
//  Added support to use the StructureComponentRegistry class.
//
//  Revision 1.1.1.1  2002/07/16 18:00:19  moreland
//  Imported sources
//
//  Revision 1.0  2002/06/10 23:38:39  moreland
//


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

