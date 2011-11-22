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

