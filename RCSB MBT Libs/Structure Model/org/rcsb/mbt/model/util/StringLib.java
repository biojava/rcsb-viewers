//  $Id: StringLib.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: StringLib.java,v $
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
//  Revision 1.4  2004/04/09 00:15:21  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.3  2004/01/29 17:29:07  moreland
//  Updated copyright and class block comments.
//
//  Revision 1.2  2003/12/09 17:33:48  moreland
//  Updated package statement, header, comments, and format.
//
//  Revision 1.10  2003/10/06 23:41:17  moreland
//  Initial version.
//


package org.rcsb.mbt.model.util;


/**
 *  This class provides a number of general String processing utility methods.
 *  <P>
 *  @author	John L. Moreland
 */
public class StringLib
{
	/**
	 * Split the supplied string in two, returning the resulting strings as
	 * elements of an array.
	 * 
	 * @param str the string to split in two
	 * @param sep the separator character
	 * @return the strings resulting from the split operation
	 */
	public static String[] split( final String str, final String sep )
	{
		final String[] ret = new String[2];
	
		final int separator = str.indexOf( sep );
		ret[0] = str.substring( 0, separator );
		ret[1] = str.substring( separator+1 );
	
		return ret;
	}	
	
	/**
	 * Join separate strings to make a single string.
	 * 
	 * @param strs the array containing the strings to concatenate into a single
	 * string
	 * @param sep the character that will separate the individual strings
	 * @return the string that results from joining the supplied strings
	 */
	public static String join( final String[] strs, final String sep )
	{
		if ( strs == null ) {
			return null;
		}
		if ( sep == null ) {
			return null;
		}
	
		String ret = new String( strs[0] );
		for ( int i=1; i<strs.length; i++ ) {
			ret = ret.concat( sep + strs[i] );
		}
	
		return ret;
	}
	
	/**
	 * Sort the supplied array of strings in lexicographic order.
	 *
	 * @param theList the array of strings to sort
	 */
	public static void sort( final String[] theList )
	{
		StringLib.QuickSort( theList, 0, theList.length - 1 );
	}

	/**
	 * Swap the positions of two strings within an array
	 */
	private static void swap( final String[] s, final int i, final int j )
	{
		String S;
		S = s[i];
		s[i] = s[j];
		s[j] = S;
	}
	
	/**
	 * Sort an array of strings using the Quick Sort algorithm.
	 *
	 * @param a the array of strings to sort
	 * @param lo0 the low watermark for the sort
	 * @param hi0 the high watermark for the sort
	 */
	public static void QuickSort( final String[] a, final int lo0, final int hi0 )
	{
		int lo = lo0;
		int hi = hi0;
		String mid;
	
		if ( hi0 > lo0 )
		{
			// Establish partition element as the midpoint of the array
			mid = a[ ( lo0 + hi0 ) / 2 ];
		
			// loop through the array until indices cross
			while ( lo <= hi )
			{
				// Find 1st element >= partition element
				// starting from the left Index
				while ( ( lo < hi0 ) && ( a[lo].compareTo(mid) < 0 ) )
				{
					++lo;
				}
				// Find element <= partition element
				// starting from the right Index
				while ( ( hi > lo0 ) && ( a[hi].compareTo(mid) > 0 ) )
				{
					--hi;
				}
				// If the indexes have not crossed, then swap
				if ( lo <= hi )
				{
					StringLib.swap( a, lo, hi );
					++lo;
					--hi;
				}
			}
		
			// If the right index has not reached the left side of array
			// then sort the left partition
			if ( lo0 < hi )
			{
				StringLib.QuickSort( a, lo0, hi );
			}
		
			// If the left index has not reached the right side of array
			// then sort the right partition
			if ( lo < hi0 )
			{
				StringLib.QuickSort( a, lo, hi0 );
			}
		}
	}
}

