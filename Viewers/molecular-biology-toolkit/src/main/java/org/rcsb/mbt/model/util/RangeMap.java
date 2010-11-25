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
	public Vector<int[]> ranges = new Vector<int[]>( );

	/**
	 *  The value objects keyed by range tuple objects (ie: int[2]).
	 */
	private final Hashtable<int[], Object> values = new Hashtable<int[], Object>( );

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
		reset( min, max, defaultObject );
	}

	//
	// Public RangeMap methods.
	//

	/**
	 *  Get the minimum range index.
	 */
	public int getMin( )
	{
		return rangeMin;
	}

	/**
	 *  Get the maximum range index.
	 */
	public int getMax( )
	{
		return rangeMax;
	}

	/**
	 *  Set flag to enable/disable collapse feature.
	 */
	public void setCollapseOn( final boolean state )
	{
		collapseOn = state;
	}

	/**
	 *  Get flag to enable/disable collapse feature.
	 */
	public boolean getCollapseOn( )
	{
		return collapseOn;
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
		if ( start < rangeMin ) {
			throw new IllegalArgumentException( "start must be >= min" );
		}
		if ( stop > rangeMax ) {
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
		for ( int i = 0; i < ranges.size(); i++ )
		{
			final int[] range = (int[]) ranges.elementAt( i );

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

				values.put( range, value );
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

				final Object oldValue = values.get( range );

				// right piece
				final int rightSide[] = { newRange[1]+1, range[1] };
				ranges.add( i+1, rightSide );
				values.put( rightSide, oldValue );

				// middle piece
				ranges.add( i+1, newRange );
				values.put( newRange, value );

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
				ranges.remove( i );
				values.remove( range );

				// Insert the new range (if it hasn't been already)
				if ( ! haveAddedNewRange )
				{
					haveAddedNewRange = true;
					ranges.add( i, newRange );
					values.put( newRange, value );
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
				ranges.add( i+1, newRange );
				values.put( newRange, value );

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
					ranges.add( i, newRange );
					values.put( newRange, value );
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
		collapse( );

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
		if ( ! collapseOn ) {
			return;
		}

		int size = ranges.size();
		if ( size > 1 )
		{
			int prior[] = (int[]) ranges.elementAt( 0 );
			Object priorObj = values.get( prior );
			for ( int i=1; i<size; i++ )
			{
				final int current[] = (int[]) ranges.elementAt( i );
				final Object currentObj = values.get( current );

				if ( currentObj == priorObj )
				{
					// Same value, so collapse (extend prior and remove current).
					prior[1] = current[1];
					ranges.remove( i );
					values.remove( current );
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
		setRange( start, stop, defaultValue );
	}

	/**
	 *  Clear the entire RangeMap by setting everything to the default value.
	 */
	public void clearAll( )
	{
		ranges.clear( );
		values.clear( );

		// Add an initial default range and value
		final int range[] = { rangeMin, rangeMax };
		ranges.add( range );
		values.put( range, defaultValue );
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

		rangeMin = min;
		rangeMax = max;
		this.defaultValue = defaultValue;

		clearAll( );
	}

	/**
	 *  Set the default value to a new value.
	 *  Make sure that prior ranges with the default value are updated.
	 */
	public void setDefaultValue( final Object newDefault )
	{
		if ( newDefault == defaultValue ) {
			return;
		}
		if ( newDefault == null ) {
			return;
		}

		// Replace the current default values with the new default value
		for ( int i=0; i<ranges.size(); i++ )
		{
			final int[] range = (int[]) ranges.elementAt( i );
			final Object value = values.get( range );
			if ( value == defaultValue ) {
				values.put( range, newDefault );
			}
		}
		// Now set the new defaultValue
		defaultValue = newDefault;
	}

	/**
	 *  Find the range element asocciated with the given index.
	 */
	private int findRange( final int index )
	{
		if ( index < rangeMin ) {
			throw new IllegalArgumentException( "index " + index + " < rangeMin " + rangeMin );
		}
		if ( index > rangeMax ) {
			throw new IllegalArgumentException( "index " + index + " > rangeMax " + rangeMax );
		}

		// Do a binary search to find the range in which the index lives
		int low = 0;
		int high = ranges.size() - 1;
		while ( low <= high )
		{
			final int mid = (low + high) / 2;
			final int[] range = (int[]) ranges.elementAt( mid );

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
		setRange( index, index, value );
	}

	/**
	 *  Get the value object asocciated with the given index.
	 */
	public Object getValue( final int index )
	{
		if ( index < rangeMin ) {
			throw new IllegalArgumentException( "index " + index + " < rangeMin " + rangeMin );
		}
		if ( index > rangeMax ) {
			throw new IllegalArgumentException( "index " + index + " > rangeMax " + rangeMax );
		}

		final int rangeIndex = findRange( index );
		final int[] range = (int[]) ranges.elementAt( rangeIndex );

		// If the index is inside this range, return the associated value.
		if ( (index >= range[0]) && (index <= range[1]) ) {
			return values.get( range );
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
		final int rangeIndex = findRange( start_index );
		final int[] range = (int[]) ranges.elementAt( rangeIndex );
		if ( range == null ) {
			return null;
		}

		if ( (end_index >= range[0]) && (end_index <= range[1]) ) {
			return values.get( range );
		}

		return null;
	}

	/**
	 *  Get the number of ranges currently assigned to this map.
	 */
	public int getRangeCount( )
	{
		return ranges.size( );
	}

	/**
	 *  Get the value currently associated with the given rangeIndex.
	 */
	public Object getRangeValue( final int rangeIndex )
	{
		final int[] range = (int[]) ranges.elementAt( rangeIndex );
		return values.get( range );
	}

	/**
	 *  Set the value currently associated with the given rangeIndex.
	 */
	public void setRangeValue( final int rangeIndex, final Object value )
	{
		if ( value == null ) {
			throw new IllegalArgumentException( "null value" );
		}
		final int[] range = (int[]) ranges.elementAt( rangeIndex );
		values.put( range, value );
	}

	/**
	 *  Get the range currently associated with the given rangeIndex.
	 */
	public int[] getRange( final int rangeIndex )
	{
		final int[] range = (int[]) ranges.elementAt( rangeIndex );
		// Copy it so the user can't change our copy out from under us!
//		int[] result = { range[0], range[1] };
		return range;
	}

	/**
	 *  Get the startIndex for the given rangeIndex.
	 */
	public int getRangeStart( final int rangeIndex )
	{
		final int[] range = (int[]) ranges.elementAt( rangeIndex );
		return range[0];
	}

	/**
	 *  Get the endIndex for the given rangeIndex.
	 */
	public int getRangeEnd( final int rangeIndex )
	{
		final int[] range = (int[]) ranges.elementAt( rangeIndex );
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

		rangeMax++;

		// If last range value matches the new value, just extend last range.
		// This keeps the last/appended range automatically "collapsed".

		final int[] lastRange = (int[]) ranges.elementAt( ranges.size()-1 );
		final Object lastValue = values.get( lastRange );
		if ( lastValue == value )
		{
			lastRange[1] += 1;
		}
		else
		{
			final int range[] = { rangeMax, rangeMax };
			ranges.add( range );
			values.put( range, value );
		}
	}

	/**
	 * Remove an index (decreasing the range maximum by 1 and causing
	 * all subsequent index values to be decreased by 1).
	 */
	public void remove( final int index )
	{
		if ( (index < rangeMin) || (index > rangeMax) ) {
			throw new IllegalArgumentException( "index out of bounds" );
		}

		int rangeIndex = findRange( index );
		int[] range = (int[]) ranges.elementAt( rangeIndex );
		if ( range[0] == range[1] )
		{
			values.remove( range );
			ranges.remove( rangeIndex );
		}
		else
		{
			range[1] -= 1;
			rangeIndex++;
		}

		// Simply subtract 1 from the remaining range start/end tuples.
		final int rangeCount = ranges.size( );
		for ( int r=rangeIndex; r<rangeCount; r++ )
		{
			range = (int[]) ranges.elementAt( r );
			range[0] -= 1;
			range[1] -= 1;
		}

		// JLM DEBUG: This should probably be custom collapse code
		// so that we only look at neigboring ranges not all ranges...
		collapse( );
	}

	// public void insert( index, value );
}

