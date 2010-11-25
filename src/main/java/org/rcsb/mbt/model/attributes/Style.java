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
package org.rcsb.mbt.model.attributes;


import java.util.*;

import org.rcsb.mbt.model.StructureComponentRegistry.ComponentType;


/**
 * Style.java<BR>
 * Please complete these missing tags
 * <P>
 * @author	John L. Moreland
 * @copyright	UCSD
 * @see StructureStyles
 */
public abstract class Style
	implements Cloneable
{
	// Vector of StructureStylesEventListener objects. Typically, the
	// StructureStyles class is the primary listener to individual Style
	// objects. Then, viewers will listen to the concentrated events that
	// are re-fired by a StructureStyles.
	private final Vector listeners = new Vector( );
	
	public enum StyleProperty
	{
		PROPERTY_NONE,
		PROPERTY_COLOR,
		PROPERTY_BINDING
	}
	// Properties
	// none.


	//
	// Constructors
	//


	/**
	 * Please complete the missing tags for main
	 * <P>
	 * @param	NA
	 * @return	NA
	 * @throws	NA
	 */
	public Style( )
	{
	}


	//
	// Cloneable interface
	//


	/**
	 * Please complete the missing tags for main
	 * <P>
	 * @param	NA
	 * @return	NA
	 * @throws	NA
	 */
	public void copy( final Style style )
	{
		// no properties.
	}


	/**
	 * Please complete the missing tags for main
	 * <P>
	 * @param	NA
	 * @return	NA
	 * @throws	NA
	 */
	
	public Object clone( )
		throws CloneNotSupportedException
	{
		return super.clone( );
	}


	//
	// Style methods
	//


	/**
	 * Return true if the Style implementation is type safe for
	 * the given StructureComponent type. That is, can the style
	 * be applied to the type?
	 * <P>
	 * @return
	 * @throws
	 */
	public abstract boolean isTypeSafe( ComponentType scType );


	/**
	 * Please complete the missing tags for main
	 * <P>
	 * @param
	 * @return
	 * @throws
	 */
	public final void addStructureStylesEventListener(
		final IStructureStylesEventListener listener )
	{
		if ( listener == null ) {
			throw new NullPointerException( "null listener" );
		}

		// Ensure that only one copy of a given listener is added.
		if ( ! this.listeners.contains( listener ) ) {
			this.listeners.add( listener );
		}
	}


	/**
	 * Please complete the missing tags for main
	 * <P>
	 * @param
	 * @return
	 * @throws
	 */
	public final void removeStructureStylesEventListener(
		final IStructureStylesEventListener listener )
	{
		if ( listener == null ) {
			throw new NullPointerException( "null listener" );
		}

		this.listeners.remove( listener );
	}


	/**
	 * Please complete the missing tags for main
	 * <P>
	 * @param
	 * @return
	 * @throws
	 */
	protected final void fireStructureStylesEvent(
		final StructureStylesEvent structureStylesEvent )
	{
		final int listenerCount = this.listeners.size( );
		for ( int i=0; i<listenerCount; i++ )
		{
			final IStructureStylesEventListener listener =
				(IStructureStylesEventListener) this.listeners.elementAt( i );
			listener.processStructureStylesEvent( structureStylesEvent );
		}
	}
}

