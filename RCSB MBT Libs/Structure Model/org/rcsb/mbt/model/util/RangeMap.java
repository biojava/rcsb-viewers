//  $Id: RangeMap.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: RangeMap.java,v $
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
//  Revision 1.13  2004/04/09 00:15:21  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.12  2004/01/29 17:29:07  moreland
//  Updated copyright and class block comments.
//
//  Revision 1.11  2003/12/09 21:23:29  moreland
//  Commented out debug print statements.
//
//  Revision 1.10  2003/10/06 23:41:17  moreland
//  Corrected typo in exception string.
//
//  Revision 1.9  2003/10/03 23:58:40  moreland
//  Added collapseOn flag and get/set methods.
//
//  Revision 1.8  2003/04/28 21:58:34  moreland
//  Added setRangeValue method.
//
//  Revision 1.7  2003/04/25 17:27:07  moreland
//  Moved exhaustive collapse code into a "collapse" method.
//  The "append" method now keeps the last range automatically/cheaply collapsed.
//
//  Revision 1.6  2003/04/24 21:01:55  moreland
//  Added getRange method that returns a COPY of an (int[]) range.
//
//  Revision 1.5  2003/04/24 17:16:25  moreland
//  Added safe range walking methods.
//  Added append and remove methods.
//
//  Revision 1.4  2003/04/24 00:19:59  moreland
//  Removed outdated StructureComponentID references.
//
//  Revision 1.3  2003/04/23 22:56:57  moreland
//  Fixed off-by-one error at the end of the range.
//  Added "text diagrams" to better document the setRange method.
//  Added code to collapse/join ranges after a setRange is performed.
//
//  Revision 1.2  2003/03/19 22:25:23  moreland
//  Moved binary search code out of getValue into new private findRange method.
//  Added public getContiguousValue method to do efficient range value searches.
//
//  Revision 1.1  2003/02/27 21:03:59  moreland
//  First version.
//
//  Revision 1.0  2003/02/12 18:33:20  moreland
//  First implementation/check-in.
//


package org.rcsb.mbt.model.util;


import java.util.Hashtable;
import java.util.Vector;


/**
 *  The RangeMap class provides a means to map an integer value
 *  (such as an index) that falls in a fixed range (min and max)
 *  to application-supplied Objects. This can be used to efficiently
 *  associate properties (colors, radii, etc) to a numberd set of objects.
 *  This data structure continually condenses contiguous ranges of equal
 *  object value into a single range reference.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.attributes.StructureStyles
 */
public class RangeMap
{
	/**
	 * The minimum value for the spaned range.
	 */
	private int rangeMin = 0;

	/**
	 * The maximum value for the spaned range.
	 */
	private int rangeMax = 0;

	/**
	 *  The sorted range tuple objects (ie: int[2]).
	 */
	public Vector ranges = new Vector( );

	/**
	 *  The value objects keyed by range tuple objects (ie: int[2]).
	 */
	private final Hashtable values = new Hashtable( );

	/**
	 *  The transparency changed.
	 */
	private Object defaultValue = null;

	/**
	 *  Flag to enable/disable collapse feature.
	 */
	private boolean collapseOn = true;

	//
	// Constructor.
	//

	/**
	 *  Construct a RangeMap object with the specified min, max, and
	 *  default value.
	 */
	public RangeMap( final int min, final int max, final Object defaultObject )
	{
		this.reset( min, max, defaultObject );
	}

	//
	// Public RangeMap methods.
	//

	/**
	 *  Get the minimum range index.
	 */
	public int getMin( )
	{
		return this.rangeMin;
	}

	/**
	 *  Get the maximum range index.
	 */
	public int getMax( )
	{
		return this.rangeMax;
	}

	/**
	 *  Set flag to enable/disable collapse feature.
	 */
	public void setCollapseOn( final boolean state )
	{
		this.collapseOn = state;
	}

	/**
	 *  Get flag to enable/disable collapse feature.
	 */
	public boolean getCollapseOn( )
	{
		return this.collapseOn;
	}


	/**
	 *  Assign an object value to the specified range.
	 */
	public void setRange( final int start, final int stop, final Object value )
	{
		// Make sure that the parameters are valid.
		if ( start > stop ) {
			throw new IllegalArgumentException( "stop must be >= start" );
		}
		if ( start < this.rangeMin ) {
			throw new IllegalArgumentException( "start must be >= min" );
		}
		if ( stop > this.rangeMax ) {
			throw new IllegalArgumentException( "stop must be <= max" );
		}
		if ( value == null ) {
			throw new IllegalArgumentException( "value was null" );
		}

		final int newRange[] = { start, stop };
		boolean haveAddedNewRange = false;
	/*
		System.err.println( "DEBUG: before setRange of " + newRange[0] + " - " + newRange[1] );
		System.err.print( "   " );
		for ( int i=0; i<ranges.size(); i++ )
		{
			int[] range = (int[]) ranges.elementAt( i );
			System.err.print( range[0] + "-" + range[1] + " , " );
		}
		System.err.println( "" );
	*/

		// Overlay the set of objects spaned by the new range.
		for ( int i=0; i<this.ranges.size(); i++ )
		{
			final int[] range = (int[]) this.ranges.elementAt( i );

			if ( newRange[1] < range[0] )
			{
				// System.err.println( "JLM DEBUG: APPLY ALGORITHM 1" );

				// The new range falls fully to the left of the current range,
				// so do nothing to the current range (there's no overlap).
				//
				//  |                            |   |                            |
				//  |[0]      newRange        [1]|   |[0]       range          [1]|
				//  ------------------------------   ------------------------------
			}
			else if ( newRange[0] > range[1] )
			{
				// System.err.println( "JLM DEBUG: APPLY ALGORITHM 2" );

				// The new range falls fully to the right of the current range,
				// so do nothing to the current range (there's no overlap).
				//
				//  |                            |   |                            |
				//  |[0]       range          [1]|   |[0]      newRange        [1]|
				//  ------------------------------   ------------------------------
			}
			else if ( (newRange[0] == range[0]) && (newRange[1] == range[1]) )
			{
				// System.err.println( "JLM DEBUG: APPLY ALGORITHM 3" );

				// The new range exactly matches the current range,
				// so simply replace the value object and break.
				//
				//  |                            |
				//  |[0]        range         [1]|
				//  ------------------------------
				//  |                            |
				//  |[0]       newRange       [1]|
				//  ------------------------------

				this.values.put( range, value );
				break;
			}
			else if ( (newRange[0] > range[0]) && (newRange[1] < range[1]) )
			{
				// System.err.println( "JLM DEBUG: APPLY ALGORITHM 4" );

				// The new range lies completely within the current range,
				// so sub-divide the current range into 3 pieces, and break.
				//
				//  |                                               |
				//  |[0]               range                     [1]|
				//  ------------------------------------------------|
				//           |                            |
				//           |[0]      newRange        [1]|
				//           ------------------------------

				final Object oldValue = this.values.get( range );

				// right piece
				final int rightSide[] = { newRange[1]+1, range[1] };
				this.ranges.add( i+1, rightSide );
				this.values.put( rightSide, oldValue );

				// middle piece
				this.ranges.add( i+1, newRange );
				this.values.put( newRange, value );

				// left piece (original range and value objects)
				range[1] = newRange[0]-1;
				// Its already in the ranges Vector
				// Its already in the values Hashtable

				break;
			}
			else if ( (newRange[0] <= range[0]) && (newRange[1] >= range[1]) )
			{
				// System.err.println( "JLM DEBUG: APPLY ALGORITHM 5" );

				// The new range completely envelopes the current range.
				//
				//           |                            |
				//           |[0]        range         [1]|
				//  |        ------------------------------         |
				//  |[0]               newRange                  [1]|
				//  ------------------------------------------------|

				// Remove the current range/value
				this.ranges.remove( i );
				this.values.remove( range );

				// Insert the new range (if it hasn't been already)
				if ( ! haveAddedNewRange )
				{
					haveAddedNewRange = true;
					this.ranges.add( i, newRange );
					this.values.put( newRange, value );
				}
				else
				{
					// Since the ranges.remove shifts items left,
					// we need to reprocess the i-th index.
					i--; // It will be incremented to i again by the for loop.
					continue;
				}
			}
			else if ( (newRange[0] > range[0]) && (newRange[0] <= range[1]) && (newRange[1] >= range[1]) )
			{
				// System.err.println( "JLM DEBUG: APPLY ALGORITHM 6" );

				// There's a partial overlap covering inside and up to or
				// past the end of the current range.
				//
				//  |                            |
				//  |[0]        range         [1]|
				//  ------------------------------
				//                |                            |
				//                |[0]       newRange       [1]|
				//                ------------------------------

				// left piece (original range and value objects)
				range[1] = newRange[0]-1;
				// Its already in the ranges Vector
				// Its already in the values Hashtable

				// right piece
				haveAddedNewRange = true;
				this.ranges.add( i+1, newRange );
				this.values.put( newRange, value );

				// Don't process the new piece on the next iteration!
				i++;
			}
			else if ( (newRange[0] <= range[0]) && (newRange[1] < range[1]) )
			{
				// System.err.println( "JLM DEBUG: APPLY ALGORITHM 7" );

				// There's a partial overlap extending from a prior range
				// to somehwere inside the current range.
				//
				//                 |                            |
				//                 |[0]        range         [1]|
				//                 ------------------------------
				//  |                            |
				//  |[0]       newRange       [1]|
				//  ------------------------------

				// newRange:
				if ( newRange[0] == range[0] )
				{
					// The new piece starts at the begining of the original range,
					// so we need to insert a new range here.
					// System.err.println( "JLM DEBUG: add( " + i + ", " + value + ")" );
					this.ranges.add( i, newRange );
					this.values.put( newRange, value );
				}
				else
				{
					// The newRange should have already been added to the ranges Vector
					// in a prior pass. It should also already in the values Hashtable.
				}
				// right piece (original range and value objects)
				range[0] = newRange[1]+1;

				// We've fully inserted the new range.
				break;
			}
		}

		// Walk through all the ranges and collapse/join ranges
		// where adjacent values are equal.
		// JLM DEBUG: This should probably be custom collapse code
		// so that we only look at neigboring ranges not all ranges...
		this.collapse( );

	/*
		//
		// Check range consistancy
		//

		System.err.println( "   after setRange, size = " + ranges.size( ) );
		System.err.print( "   " );
		for ( int i=0; i<ranges.size(); i++ )
		{
			int[] range = (int[]) ranges.elementAt( i );
			System.err.print( range[0] + "-" + range[1] + " , " );
		}
		System.err.println( "" );

		// Check both endpoints
		int[] rangeA = (int[]) ranges.elementAt( 0 );
		if ( rangeA[0] != rangeMin )
			throw new IllegalArgumentException(
				"rangeA[0] " + rangeA[0] + " != rangeMin " + rangeMin );
		int[] rangeB = (int[]) ranges.elementAt( ranges.size() - 1 );
		if ( rangeB[1] != rangeMax )
			throw new IllegalArgumentException(
				"rangeA[1] " + rangeA[1] + " != rangeMax " + rangeMax );
		// Check internal ranges
		for ( int i=1; i<ranges.size(); i++ )
		{
			rangeB = (int[]) ranges.elementAt( i );
			if ( (rangeA[1]+1) != rangeB[0] )
			throw new IllegalArgumentException(
				"range " + i + " of " + (ranges.size() - 1) + ", " +
				"rangeA[1] " + rangeA[1] + " missmatch with rangeB[0] " + rangeB[0] );
			rangeA = rangeB;
		}
	*/
	}

	/**
	 * Walk through all the ranges and collapse/join ranges
	 * where adjacent values are equal.
	 */
	public void collapse( )
	{
		if ( ! this.collapseOn ) {
			return;
		}

		int size = this.ranges.size();
		if ( size > 1 )
		{
			int prior[] = (int[]) this.ranges.elementAt( 0 );
			Object priorObj = this.values.get( prior );
			for ( int i=1; i<size; i++ )
			{
				final int current[] = (int[]) this.ranges.elementAt( i );
				final Object currentObj = this.values.get( current );

				if ( currentObj == priorObj )
				{
					// Same value, so collapse (extend prior and remove current).
					prior[1] = current[1];
					this.ranges.remove( i );
					this.values.remove( current );
					size--;
					// Since ranges have shifted, we nee to see the "new" i range.
					i--;
				}
				else
				{
					prior = current;
					priorObj = currentObj;
				}
			}
		}
	}

	/**
	 *  Clear the specified range by setting it to the default value.
	 */
	public void clearRange( final int start, final int stop )
	{
		this.setRange( start, stop, this.defaultValue );
	}

	/**
	 *  Clear the entire RangeMap by setting everything to the default value.
	 */
	public void clearAll( )
	{
		this.ranges.clear( );
		this.values.clear( );

		// Add an initial default range and value
		final int range[] = { this.rangeMin, this.rangeMax };
		this.ranges.add( range );
		this.values.put( range, this.defaultValue );
	}

	/**
	 *  Reset the entire RangeMap by setting new max, min and default values,
	 *  and by clearing all existing ranges.
	 */
	public void reset( final int min, final int max, final Object defaultValue )
	{
		if ( min > max ) {
			throw new IllegalArgumentException( "min > max" );
		}
		if ( defaultValue == null ) {
			throw new IllegalArgumentException( "default value was null" );
		}

		this.rangeMin = min;
		this.rangeMax = max;
		this.defaultValue = defaultValue;

		this.clearAll( );
	}

	/**
	 *  Set the default value to a new value.
	 *  Make sure that prior ranges with the default value are updated.
	 */
	public void setDefaultValue( final Object newDefault )
	{
		if ( newDefault == this.defaultValue ) {
			return;
		}
		if ( newDefault == null ) {
			return;
		}

		// Replace the current default values with the new default value
		for ( int i=0; i<this.ranges.size(); i++ )
		{
			final int[] range = (int[]) this.ranges.elementAt( i );
			final Object value = this.values.get( range );
			if ( value == this.defaultValue ) {
				this.values.put( range, newDefault );
			}
		}
		// Now set the new defaultValue
		this.defaultValue = newDefault;
	}

	/**
	 *  Find the range element asocciated with the given index.
	 */
	private int findRange( final int index )
	{
		if ( index < this.rangeMin ) {
			throw new IllegalArgumentException( "index " + index + " < rangeMin " + this.rangeMin );
		}
		if ( index > this.rangeMax ) {
			throw new IllegalArgumentException( "index " + index + " > rangeMax " + this.rangeMax );
		}

		// Do a binary search to find the range in which the index lives
		int low = 0;
		int high = this.ranges.size() - 1;
		while ( low <= high )
		{
			final int mid = (low + high) / 2;
			final int[] range = (int[]) this.ranges.elementAt( mid );

			// If the index is inside this range, return range index.
			if ( (index >= range[0]) && (index <= range[1]) ) {
				return mid;
			} else if ( index < range[0] ) {
				high = mid - 1;
			} else {
				low = mid + 1;
			}
		}

		// We should never get here, since we should always find a value.
	/*
		System.err.println( "index=" + index );
		System.err.println( "rangeMin=" + rangeMin );
		System.err.println( "rangeMax=" + rangeMax );
		System.err.println( "ranges.size=" + ranges.size() );
		for ( int i=0; i<ranges.size(); i++ )
		{
			int[] range = (int[]) ranges.elementAt( i );
			System.err.println( "range[" + i + "] = " + range[0] + " - " + range[1] );
			System.err.println( "   value = " + values.get( range ) );
		}
	*/
		throw new IllegalArgumentException( "RangeMap.findRange did not find a range!" );
	}

	/**
	 *  Assign an object value to the specified index.
	 */
	public void setValue( final int index, final Object value )
	{
		this.setRange( index, index, value );
	}

	/**
	 *  Get the value object asocciated with the given index.
	 */
	public Object getValue( final int index )
	{
		if ( index < this.rangeMin ) {
			throw new IllegalArgumentException( "index " + index + " < rangeMin " + this.rangeMin );
		}
		if ( index > this.rangeMax ) {
			throw new IllegalArgumentException( "index " + index + " > rangeMax " + this.rangeMax );
		}

		final int rangeIndex = this.findRange( index );
		final int[] range = (int[]) this.ranges.elementAt( rangeIndex );

		// If the index is inside this range, return the associated value.
		if ( (index >= range[0]) && (index <= range[1]) ) {
			return this.values.get( range );
		}

		return null;
	}

	/**
	 *  Get the value object asocciated with the given start and end indexes.
	 *  If a single range does not span the entire start and end indexes, null is returned.
	 *  <P>
	 *  This method is useful for efficiently determining if an entire index range maps
	 *  contiguously (unbroken) to the same value object.
	 *  <P>
	 *  Example, in order to test that a given chain is selected, simply pass the
	 *  chain's start atom index and end atom index to this method. If the chain is
	 *  selected, the return value should be a boolean "True" object. If all atoms
	 *  in the given chain are not all selected, then this method would return null.
	 */
	public Object getContiguousValue( final int start_index, final int end_index )
	{
		final int rangeIndex = this.findRange( start_index );
		final int[] range = (int[]) this.ranges.elementAt( rangeIndex );
		if ( range == null ) {
			return null;
		}

		if ( (end_index >= range[0]) && (end_index <= range[1]) ) {
			return this.values.get( range );
		}

		return null;
	}

	/**
	 *  Get the number of ranges currently assigned to this map.
	 */
	public int getRangeCount( )
	{
		return this.ranges.size( );
	}

	/**
	 *  Get the value currently associated with the given rangeIndex.
	 */
	public Object getRangeValue( final int rangeIndex )
	{
		final int[] range = (int[]) this.ranges.elementAt( rangeIndex );
		return this.values.get( range );
	}

	/**
	 *  Set the value currently associated with the given rangeIndex.
	 */
	public void setRangeValue( final int rangeIndex, final Object value )
	{
		if ( value == null ) {
			throw new IllegalArgumentException( "null value" );
		}
		final int[] range = (int[]) this.ranges.elementAt( rangeIndex );
		this.values.put( range, value );
	}

	/**
	 *  Get the range currently associated with the given rangeIndex.
	 */
	public int[] getRange( final int rangeIndex )
	{
		final int[] range = (int[]) this.ranges.elementAt( rangeIndex );
		// Copy it so the user can't change our copy out from under us!
//		int[] result = { range[0], range[1] };
		return range;
	}

	/**
	 *  Get the startIndex for the given rangeIndex.
	 */
	public int getRangeStart( final int rangeIndex )
	{
		final int[] range = (int[]) this.ranges.elementAt( rangeIndex );
		return range[0];
	}

	/**
	 *  Get the endIndex for the given rangeIndex.
	 */
	public int getRangeEnd( final int rangeIndex )
	{
		final int[] range = (int[]) this.ranges.elementAt( rangeIndex );
		return range[1];
	}

	/**
	 * Append a new value (increasing the range maximum by 1).
	 */
	public void append( final Object value )
	{
		if ( value == null ) {
			throw new IllegalArgumentException( "null value" );
		}

		this.rangeMax++;

		// If last range value matches the new value, just extend last range.
		// This keeps the last/appended range automatically "collapsed".

		final int[] lastRange = (int[]) this.ranges.elementAt( this.ranges.size()-1 );
		final Object lastValue = this.values.get( lastRange );
		if ( lastValue == value )
		{
			lastRange[1] += 1;
		}
		else
		{
			final int range[] = { this.rangeMax, this.rangeMax };
			this.ranges.add( range );
			this.values.put( range, value );
		}
	}

	/**
	 * Remove an index (decreasing the range maximum by 1 and causing
	 * all subsequent index values to be decreased by 1).
	 */
	public void remove( final int index )
	{
		if ( (index < this.rangeMin) || (index > this.rangeMax) ) {
			throw new IllegalArgumentException( "index out of bounds" );
		}

		int rangeIndex = this.findRange( index );
		int[] range = (int[]) this.ranges.elementAt( rangeIndex );
		if ( range[0] == range[1] )
		{
			this.values.remove( range );
			this.ranges.remove( rangeIndex );
		}
		else
		{
			range[1] -= 1;
			rangeIndex++;
		}

		// Simply subtract 1 from the remaining range start/end tuples.
		final int rangeCount = this.ranges.size( );
		for ( int r=rangeIndex; r<rangeCount; r++ )
		{
			range = (int[]) this.ranges.elementAt( r );
			range[0] -= 1;
			range[1] -= 1;
		}

		// JLM DEBUG: This should probably be custom collapse code
		// so that we only look at neigboring ranges not all ranges...
		this.collapse( );
	}

	// public void insert( index, value );
}

