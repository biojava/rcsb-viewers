//  $Id: StructureLoader.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: StructureLoader.java,v $
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
//  Revision 1.3  2004/04/09 00:06:41  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.2  2004/01/29 17:23:34  moreland
//  Updated copyright and class block comments.
//
//  Revision 1.1.1.1  2002/07/16 18:00:19  moreland
//  Imported sources
//
//  Revision 1.0  2002/06/10 23:38:39  moreland
//


package org.rcsb.mbt.structLoader;


import java.io.IOException;

import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.TransformationList;
import org.rcsb.mbt.model.UnitCell;
import org.rcsb.mbt.model.util.PdbToNdbConverter;


/**
 *  Defines the standard interface for classes which know how to load
 *  Structure objects. While a StructureLoader sub-class can
 *  be instantiated and used directly to load Structure objects, the
 *  StructureFactory class provides a wrapper to enable an application£
 *  to make calls to a single common interface which provides the logic
 *  to determine which loader is capable of loading a given named structure.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.Structure
 */
public interface IStructureLoader
{
	/**
	 * Returns the common name for the loader implementation.
	 * This is the string that might appear in a user-selectable menu.
	 */
	public String getLoaderName( );

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
	 * @throws IOException 
	 */
	public Structure load( String name ) throws IOException;

	/**
	 * Returns true if the loader is capable of loading the structure,
	 * or false otherwise. This enables higher-level code to be able
	 * to build a context sensitive menu of only the loaders that can
	 * load a given structure name.
	 */
	public boolean canLoad( String name );
	
	/**
	 * get the completed structure.
	 * @return
	 */
    public abstract Structure getStructure();

    /**
     */
	public abstract PdbToNdbConverter getIDConverter();

	/**
	 */
	public abstract String[] getNonProteinChainIds();
	
	/**
	 * get the unit cell for biological units
	 * @return
	 */
	public abstract UnitCell getUnitCell();
	
	/**
	 * Test
	 * @return
	 */
    public abstract boolean hasBiologicUnitTransformationMatrices();
    
    /**
     * Accessor
     * @return
     */
    public abstract TransformationList getBiologicalUnitTransformationMatrices();
    
    /**
     * Test
     */
    public abstract boolean hasNonCrystallographicOperations();
    
    /**
     * Accessor
     * @return
     */
    public abstract TransformationList getNonCrystallographicOperations();
}

