//  $Id: TestStructureLoader.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: TestStructureLoader.java,v $
//  Revision 1.1  2007/02/08 02:38:52  jbeaver
//  version 1.50
//
//  Revision 1.1  2006/09/20 16:50:43  jbeaver
//  first commit - branched from ProteinWorkshop
//
//  Revision 1.2  2006/09/02 18:52:28  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2006/08/24 17:39:03  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2006/03/09 00:18:55  jbeaver
//  Initial commit
//
//  Revision 1.9  2005/11/08 20:58:16  moreland
//  Switched style code to new StructureStyles API.
//
//  Revision 1.8  2004/04/09 00:06:41  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.7  2004/01/29 17:23:34  moreland
//  Updated copyright and class block comments.
//
//  Revision 1.6  2003/04/30 17:56:04  moreland
//  Added Atom serial number field support.
//
//  Revision 1.5  2003/04/23 18:08:50  moreland
//  Removed StructureComponentID/scid and replaced it with a "structure" field
//  in the StructureComponent base class.
//  Changed "getType" method to "getStructureComponentType" to return dynamic
//  SC type (ie: class name).
//
//  Revision 1.4  2003/04/03 18:21:10  moreland
//  Changed Atom field "type" to "element" due to naming and meaning conflict.
//
//  Revision 1.3  2002/12/16 06:32:20  moreland
//  Changed code to support differentiation of Conformation into Coil, Helix,
//  Strand, and Turn sub-class types (at Eliot Clingman's suggestion).
//
//  Revision 1.2  2002/10/24 18:00:52  moreland
//  Added support to use the StructureComponentRegistry class.
//
//  Revision 1.1.1.1  2002/07/16 18:00:19  moreland
//  Imported sources
//
//  Revision 1.0  2002/06/10 23:38:39  moreland
//


package org.rcsb.mbt.structLoader;



import java.util.Vector;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.rcsb.mbt.controllers.scene.PdbToNdbConverter;
import org.rcsb.mbt.glscene.geometry.UnitCell;
import org.rcsb.mbt.model.Atom;
import org.rcsb.mbt.model.Conformation;
import org.rcsb.mbt.model.Helix;
import org.rcsb.mbt.model.Residue;
import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.StructureComponent;
import org.rcsb.mbt.model.StructureComponentRegistry;


/**
 *  Implements the StructureLoader interface to provide randomly generated PDB
 *  data for testing purposes.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.structLoader.IStructureLoader
 *  @see	org.rcsb.mbt.structLoader.StructureFactory
 */
public class TestStructureLoader
	implements IFileStructureLoader, IBatchStructureLoader
{
	private Structure structure = null;
	
	/**
	 * Returns the common name for the loader implementation.
	 * This is the string that might appear in a user-selectable menu.
	 */
	public String getLoaderName( )
	{
		return new String( "Test Structure Loader" );
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
		// JLM DEBUG - TEST CODE
		structure = new Structure()
		{
			private int sc_count = 50; // How many test objects to deliver
			private java.util.Random random = new java.util.Random( );

			
			public String getUrlString( )
			{
				return "file://c:/myFiles/5ebx.pdb";
			}

			@Override
			public int getStructureComponentCount( ComponentType scType )
			{
				if ( StructureComponentRegistry.getTypeName( scType ) != null ) {
					return this.sc_count;
				} else {
					return 0;
				}
			}

			public void getStructureComponentByIndex( int index,
				StructureComponent structureComponent )
			{
				if ( index < 0 ) {
					return;
				}
				if ( index >= this.sc_count ) {
					return;
				}

				float fRandom = this.random.nextFloat( );
				String scType = structureComponent.getStructureComponentType();

				structureComponent.setStructure( this );

				if ( scType == StructureComponentRegistry.TYPE_ATOM )
				{
					Atom atom = (Atom) structureComponent;

					atom.number = index;

					if ( fRandom <= 0.1 )
					{
						atom.element = "C";
						atom.name = "CA";
						atom.compound = "VAL";
						atom.chain_id = "A";
						atom.residue_id = 10;
					}
					else
					{
						atom.element = "N";
						atom.name = "N";
						atom.compound = "THR";
						atom.chain_id = "A";
						atom.residue_id = 10;
					}
					atom.coordinate[0] = fRandom * index * 1.0;
					atom.coordinate[1] = fRandom * index * 2.0;
					atom.coordinate[2] = fRandom * index * 3.0;
					atom.occupancy = fRandom * index * 0.3f;
					atom.bfactor = fRandom * index * 20.0f;
				}
				else if (
					(scType == StructureComponentRegistry.TYPE_COIL ) ||
					(scType == StructureComponentRegistry.TYPE_HELIX ) ||
					(scType == StructureComponentRegistry.TYPE_STRAND ) ||
					(scType == StructureComponentRegistry.TYPE_TURN )
				)
				{
					Conformation conformation =
						(Conformation) structureComponent;

					conformation.name = scType + index;
					conformation.start_compound = "VAL";
					conformation.start_chain = "A";
					conformation.start_residue = index;
					conformation.end_compound = "VAL";
					conformation.end_chain = "A";
					conformation.end_residue = index+20;

					if ( scType == StructureComponentRegistry.TYPE_HELIX )
					{
						Helix helix = (Helix) conformation;
							helix.setRighthand( false );
					}
				}
				else if ( scType == StructureComponentRegistry.TYPE_RESIDUE )
				{
					Residue residue = (Residue) structureComponent;

					residue.setCompoundCode( "ARG" );
				}
				else
				{
					throw new IllegalArgumentException( "unsupported type" );
				}
			}

			
			public StructureComponent getStructureComponentByIndex( String type,
				int index )
			{
				StructureComponent structureComponent = null;

                // Dynamically create a new StructureComponent subclass
                // from the type.
                try
                {
                    structureComponent = (StructureComponent)
                        Class.forName(type).newInstance( );
                }
                catch ( Exception e )
                {
                    return null;
                }

				// Fill in the record.
				this.getStructureComponentByIndex( index, structureComponent );

				return structureComponent;
			}
		};
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
		return true;  // DEBUG
	}

	/**
	 * Returns the names of all structures which are available to the
	 * batch structure loader. Applications might use this to enable a
	 * user to select a structure from a list.
	 */
	public Vector getStructureNames( )
	{
		return null; // DEBUG
	}

	//
	// FileStructureLoader interface methods
	//

	/**
	 * Load a structure from a File object.
	 */
	public Structure load( final File file )
	{
		try
		{
			final String path = file.getCanonicalPath();
			return this.load( path );
		}
		catch( final IOException e )
		{
			return null;
		}
	}

	/**
	 * Returns true if the loader is capable of loading the structure,
	 * or false otherwise. This enables higher-level code to be able
	 * to automatically select or show the loaders that can
	 * load a given structure.
	 */
	public boolean canLoad( final File file )
	{
		return true; // DEBUG
	}

	/**
	 * Load a structure from a URL object.
	 */
	public Structure load( final URL url )
	{
		return null; // DEBUG
	}

	/**
	 * Returns true if the loader is capable of loading the structure,
	 * or false otherwise. This enables higher-level code to be able
	 * to automatically select or show the loaders that can
	 * load a given structure.
	 */
	public boolean canLoad( final URL url )
	{
		return true; // DEBUG
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

