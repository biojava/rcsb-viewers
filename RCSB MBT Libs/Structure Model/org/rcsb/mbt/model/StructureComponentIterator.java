//  $Id: StructureComponentIterator.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: StructureComponentIterator.java,v $
//  Revision 1.1  2007/02/08 02:38:52  jbeaver
//  version 1.50
//
//  Revision 1.1  2006/09/20 16:50:42  jbeaver
//  first commit - branched from ProteinWorkshop
//
//  Revision 1.1  2006/08/24 17:39:03  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2006/03/09 00:18:55  jbeaver
//  Initial commit
//
//  Revision 1.5  2004/04/09 00:17:01  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.4  2004/01/29 17:08:16  moreland
//  Updated copyright and class block comments.
//
//  Revision 1.3  2003/04/23 17:39:36  moreland
//  Removed StructureComponentID/scid and replaced it with a "structure" field
//  in the StructureComponent base class.
//  Changed "getType" method to "getStructureComponentType" to return dynamic
//  SC type (ie: class name).
//
//  Revision 1.2  2002/10/24 17:54:01  moreland
//  Provides implementation for the improved Structure API/implementation.
//
//  Revision 1.1.1.1  2002/07/16 18:00:18  moreland
//  Imported sources
//
//  Revision 1.0  2002/06/10 23:38:39  moreland
//


package org.rcsb.mbt.model;


// MBT

// Core
import java.util.NoSuchElementException;

import org.rcsb.mbt.model.filters.*;


/**
 *  Defines a standard interface to describe a filter implementation class.
 *  A filter is repsonsible for implementing two methods which should return
 *  values according to what subset of StructureComponent objects it will
 *  accept or reject. The "type" method should return the
 *  StructureComponentRegistry TYPE that it will process (this will also
 *  automatically limit the type of StructureComponent objects passed to
 *  its "accept" method). The "accept" method should return TRUE if the given
 *  StructureComponent object should be accepted or FALSE if it should be
 *  rejected.
 *  <P>
 *  For example, to define a filter which filters Atom components
 *  which only accepts C-Alpha atoms, one could declare the following
 *  class (in this example, its declared as an internal class):
 *  <P>
 *  <PRE>
 *     StructureComponentFilter filter = new StructureComponentFilter( )
 *     {
 *         public String type( )
 *         {
 *             return StructureComponentRegistry.TYPE_ATOM;
 *         }
 *
 *         public boolean accept( StructureComponent sc )
 *         {
 *             return ((Atom)sc).name.startsWith( "CA" );
 *         }
 *     };
 *     StructureComponentIterator iterator =
 *        structure.getStructureComponentIterator( filter );
 *  </PRE>
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.filters.IStructureComponentFilter
 *  @see	org.rcsb.mbt.model.Structure
 *  @see	org.rcsb.mbt.model.StructureComponent
 */
public class StructureComponentIterator
{
	private Structure structure;
	private IStructureComponentFilter filter;
	private String type;
	private int count;
	private int index;
	private boolean foundNext;
	private StructureComponent tmpStructureComponent;

	/**
	 *  Construct a StructureComponentIterator for the given structure
	 *  using the given filter to describe the desired subset.
	 *  <P>
	 */
	public StructureComponentIterator( final Structure structure, final IStructureComponentFilter filter )
		throws ClassNotFoundException, InstantiationException,
			IllegalAccessException
	{
		this.structure = structure;
		this.filter = filter;
		this.type = filter.type( );

		this.count = structure.getStructureComponentCount( this.type );
		this.index = 0;
		this.foundNext = false;
	}

	/**
	 *  Return true if the Iterator has more matches or false otherwise.
	 *  <P>
	 */
	public boolean hasMore( )
	{
		if ( this.foundNext ) {
			return true;
		}
		while ( this.index<this.count )
		{
			this.tmpStructureComponent =
				this.structure.getStructureComponentByIndex( this.type, this.index );
			if ( this.filter.accept( this.tmpStructureComponent ) )
			{
				this.foundNext = true;
				return true;
			}
			this.index++;
		}
		this.foundNext = false;
		return false;
	}

	/**
	 *  Returns the index of the next match or -1 if there are no more matches.
	 *  <P>
	 */
	public int nextIndex( )
	{
		if ( this.hasMore() ) {
			return this.index;
		} else {
			return -1;
		}
	}

	/**
	 *  Iterates through a set of StructureComponent objects.
	 *  <P>
	 */
	public void next( final StructureComponent structureComponent )
		throws NoSuchElementException
	{
		if ( this.type != structureComponent.getStructureComponentType() ) {
			throw new NoSuchElementException( "StructureComponentIterator.next: type mismatch" );
		}

		if ( this.hasMore( ) )
		{
			structureComponent.copy( this.tmpStructureComponent );
			this.index++;
			this.foundNext = false;
			return;
		}
	}
}

