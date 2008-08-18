//$Id: ResidueColorByRandomFragmentColor.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: ResidueColorByRandomFragmentColor.java,v $
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
//  Revision 1.1  2005/10/06 17:07:53  jbeaver
//  Initial commit
//
//  Revision 1.1  2005/06/23 08:25:02  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2004/05/14 22:53:53  moreland
//  First version.
//
//  Revision 1.0  2004/05/14 18:33:19  moreland
//  First implementation.
//


package org.rcsb.mbt.model.attributes;


import java.util.HashMap;
import java.util.Random;

import org.rcsb.mbt.model.Fragment;
import org.rcsb.mbt.model.Residue;
import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.StructureMap;



/**
 *  This class implements the ResidueColor interface by applying a color
 *  to the given Residue by using the secondary structure Fragment type.
 *  <P>
 *  @author John L. Moreland
 *  @see    org.rcsb.mbt.model.attributes.IResidueColor
 *  @see    org.rcsb.mbt.model.Residue
 */
public class ResidueColorByRandomFragmentColor
    implements IResidueColor
{
    public static final String NAME = "By Fragment Type";

    // Holds a singleton instance of this class.
    private static ResidueColorByRandomFragmentColor singleton = null;

    /* Data structure:
     *  HashMap fragmentHash {
     *      key: Fragment fragment
     *      value: Color color
     *  }
    */
    private HashMap fragmentHash = null;

    public void randomizeColors(final Structure struct) {
        final StructureMap sm = struct.getStructureMap();
        
        final int fragCount = sm.getFragmentCount();
        for(int i = 0; i < fragCount; i++) {
            final Fragment frag = sm.getFragment(i);
            
            // generate a random color...
            final float red = this.random.nextFloat();
            final float green = this.random.nextFloat();
            final float blue = this.random.nextFloat();
            final float[] color = {red, green, blue};
            
            this.fragmentHash.put(frag, color);
        }
    }
    
    /**
     *  The constructor is PRIVATE so that the "create" method
     *  is used to produce a singleton instance of this class.
     */
    private final Random random = new Random();
    private ResidueColorByRandomFragmentColor( final Structure struct )
    {
        this.fragmentHash = new HashMap( );
        
        this.randomizeColors(struct);
    }


    /**
     *  Return the singleton instance of this class.
     */
    public static ResidueColorByRandomFragmentColor create( final Structure struct )
    {
        if ( ResidueColorByRandomFragmentColor.singleton == null ) {
			ResidueColorByRandomFragmentColor.singleton = new ResidueColorByRandomFragmentColor( struct );
		}

        return ResidueColorByRandomFragmentColor.singleton;
    }


    /**
     *  Produce a color based upon the residue element type.
     */
    public void getResidueColor( final Residue residue, final float[] color )
    {
        final float[] storedColor = (float[]) this.fragmentHash.get( residue.getFragment() );

        if ( storedColor != null )
        {
            color[0] = storedColor[0];
            color[1] = storedColor[1];
            color[2] = storedColor[2];
        }
        else // ( typeColor == null )
        {
            color[0] = color[1] = color[2] = 0.5f;
        }
    }


    /**
     *  Set the color used to color the specified fragment type.
     */
    public void setFragmentColor( final Fragment frag, final float[] color )
        throws IllegalArgumentException
    {
        if ( frag == null ) {
			throw new IllegalArgumentException( "null type" );
		}
        if ( color == null ) {
			throw new IllegalArgumentException( "null color" );
		}
        if ( color.length != 3 ) {
			throw new IllegalArgumentException( "color.length != 3" );
		}
        if ( color[0] < 0.0 ) {
			throw new IllegalArgumentException( "color[0] < 0.0" );
		}
        if ( color[0] > 1.0 ) {
			throw new IllegalArgumentException( "color[0] > 1.0" );
		}
        if ( color[1] < 0.0 ) {
			throw new IllegalArgumentException( "color[1] < 0.0" );
		}
        if ( color[1] > 1.0 ) {
			throw new IllegalArgumentException( "color[1] > 1.0" );
		}
        if ( color[2] < 0.0 ) {
			throw new IllegalArgumentException( "color[2] < 0.0" );
		}
        if ( color[2] > 1.0 ) {
			throw new IllegalArgumentException( "color[2] > 1.0" );
		}

        this.fragmentHash.put( frag, color );
    }


    /**
     *  Get the ColorMap used to color by Chain-Residue Index.
     */
    public float[] getFragmentColor( final Fragment frag )
        throws IllegalArgumentException
    {
        if ( frag == null ) {
			throw new IllegalArgumentException( "null type" );
		}

        return (float[]) this.fragmentHash.get( frag );
    }
}

