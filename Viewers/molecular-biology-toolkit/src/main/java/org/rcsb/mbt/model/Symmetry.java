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

import org.rcsb.mbt.model.StructureComponentRegistry.ComponentType;


/**
 *  Implements a StructureComponent container for Symmetry data.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.StructureComponent
 *  @see	org.rcsb.mbt.model.Structure
 */
public class Symmetry
	extends StructureComponent
	implements java.lang.Cloneable
{
	//
	// Symmetry fields
	//

	/**
	 *  The symmetry space group name.
	 *  For example, "C 2", "I 2"
	 */
	private String name = null; // _symmetry.space_group_name_H-M

	/**
	 *  The symmetry matricies expressed as an array of 4x4 3D transforms.
	 *  For example, 
	 *   [1.0, 0.0, 0.0, 0.0]
	 *   [0.0, 1.0, 0.0, 0.0]
	 *   [0.0, 0.0, 1.0, 0.0]
	 *   [0.0, 0.0, 0.0, 1.0]
	 *  The 0th array index is the symetry index.
	 *  The 1st array index is the matrix number.
	 *  The 2nd array index is the row number.
	 *  The 3rd array index is the column number.
	 */
	private double matrix[][][] = null;

	//
	// Constructor
	//

	/**
	 *  Creates a new Symmetry object.
	 */
	public Symmetry( )
	{
	}

	//
	// StructureComponent methods
	//

	/**
	 *  Copy all of the field values from the parameter object into "this".
	 */
	
	public void copy( final StructureComponent structureComponent )
	{
		this.setStructure( structureComponent.getStructure() );
		final Symmetry symmetry = (Symmetry) structureComponent;
		for ( int i=0; i<this.matrix.length; i++ ) {
			for ( int j=0; j<this.matrix[i].length; j++ ) {
				for ( int k=0; k<this.matrix[i][j].length; k++ ) {
					this.matrix[i][j][k] = symmetry.matrix[i][j][k];
				}
			}
		}
	}

	/**
	 *  Clone this object.
	 */
	
	public Object clone( )
		throws CloneNotSupportedException
	{
		return super.clone( );
	}

	/**
	 *  This method returns the fully qualified name of this class.
	 *  <P>
	 *  This name is used by the StructureComponentRegistry class to enable
	 *  dynamic registration and discovery of new StructureComponent
	 *  sub-classes/types. The name is also used to create a unique integer
	 *  indentifier for each type in order to make run-time type comparisons
	 *  fast.
	 */
	private static String className = null;
	public static String getClassName()
	{
		if ( Symmetry.className == null ) {
			Symmetry.className = ((new Throwable()).getStackTrace())[0].getClassName();
		}
		return Symmetry.className;
	}

	
	public ComponentType getStructureComponentType( )
	{
		return ComponentType.SYMMETRY;
	}

	//
	// Symmetry methods
	//

	/**
	 *  Set the symmetry space group name.
	 *  For example, "C 2", "I 2"
	 */
	public void setName( final String name )
	{
		this.name = name;
	}

	/**
	 *  Get the symmetry space group name.
	 *  For example, "C 2", "I 2"
	 */
	public String getName( )
	{
		return this.name;
	}

	/**
	 *  Set the symmetry matricies expressed as an array of 4x4 3D transforms.
	 *  The upper-left 3x1 sub-matrix is the rotation.
	 *   [1.0, 0.0, 0.0, 0.0]
	 *   [0.0, 1.0, 0.0, 0.0]
	 *   [0.0, 0.0, 1.0, 0.0]
	 *   [0.0, 0.0, 0.0, 1.0]
	 *  The 1st array index is the matrix number.
	 *  The 2nd array index is the row number.
	 *  The 3rd array index is the column number.
	 */
	public void set( final double matrix[][][] )
	{
		this.matrix = matrix;
	}

	/**
	 *  Get the symmetry matricies expressed as an array of 4x4 3D transforms.
	 *  The upper-left 3x1 sub-matrix is the rotation.
	 *   [1.0, 0.0, 0.0, 0.0]
	 *   [0.0, 1.0, 0.0, 0.0]
	 *   [0.0, 0.0, 1.0, 0.0]
	 *   [0.0, 0.0, 0.0, 1.0]
	 *  The 1st array index is the matrix number.
	 *  The 2nd array index is the row number.
	 *  The 3rd array index is the column number.
	 */
	public double[][][] get( )
	{
		return this.matrix;
	}
}

