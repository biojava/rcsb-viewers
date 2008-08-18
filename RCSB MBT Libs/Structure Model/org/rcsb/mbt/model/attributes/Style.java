//  $Id: Style.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.//
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
//  $Log: Style.java,v $
//  Revision 1.1  2007/02/08 02:38:52  jbeaver
//  version 1.50
//
//  Revision 1.1  2006/09/20 16:50:42  jbeaver
//  first commit - branched from ProteinWorkshop
//
//  Revision 1.2  2006/09/02 18:52:28  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2006/08/24 17:39:02  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2006/03/09 00:18:54  jbeaver
//  Initial commit
//
//  Revision 1.1  2005/11/10 23:19:47  moreland
//  Switched style code to new StructureStyles API.
//
//  Revision 1.0 2005/04/04 00:12:54  moreland
//


package org.rcsb.mbt.model.attributes;


import java.util.*;


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
	public abstract boolean isTypeSafe( String scType );


	/**
	 * Please complete the missing tags for main
	 * <P>
	 * @param
	 * @return
	 * @throws
	 */
	public final void addStructureStylesEventListener(
		final StructureStylesEventListener listener )
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
		final StructureStylesEventListener listener )
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
			final StructureStylesEventListener listener =
				(StructureStylesEventListener) this.listeners.elementAt( i );
			listener.processStructureStylesEvent( structureStylesEvent );
		}
	}
}

