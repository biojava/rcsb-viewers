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

