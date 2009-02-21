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
 * Created on 2008/12/22
 *
 */ 
package org.rcsb.mbt.model.util;

import java.util.HashMap;
import java.util.Vector;

import org.rcsb.mbt.model.Residue;



/**
 * This class should be made obsolete. We keep it for now since it requires significant code
 * changes in many places. ndbChainIds and ndbResidueIds are not used anymore. -Peter Rose
 * 
 * for converting residue and chain ids from pdb values to their ndb equivalents.
 *
 * These are ordered by chain id, first, then residue id (for each conversion map).
 * So, to get a conversion, you need to supply a chain id and residue id.
 *
 * @author John Beaver
 * @author rickb (rewrite)
 */

public class PdbToNdbConverter {
    /* Data structure:
     *   HashMap byPdbIds {
     *      key: String pdbChainId
     *      value: HashMap residueIds {
     *          key: String pdbResidueId
     *          value: Object[] {
     *              String ndbChainId
     *              Integer ndbResidueId
     *          }
     *      }
     *   }
     */
    protected HashMap<String, HashMap<String, Object[]>> byPdbIds = new HashMap<String, HashMap<String, Object[]>>();
	
	/* Data structure:
     *   HashMap byNdbIds {
     *      key: String ndbChainId
     *      value: HashMap residueIds {
     *          key: Integer ndbResidueId
     *          value: Object[] {
     *              String pdbChainId
     *              String pdbResidueId
     *          }
     *      }
     *   }
     */
    protected HashMap<String, HashMap<Integer, Object[]>> byNdbIds = new HashMap<String, HashMap<Integer, Object[]>>();

    
    //input: three parallel Vectors. 
    //   pdbChainIds: the pdb_strand_id column of pdbx_poly_seq_scheme (Strings)
    //   ndbChainIds: the ndb_strand_id column of pdbx_poly_seq_scheme (Strings)
    //   ndbResidueIds: the ndb_seq_num column of pdbx_poly_seq_scheme (Integers)
    //   pdbResidueIds: the pdb_seq_num column of pdbx_poly_seq_scheme (Strings)
    //
    public void append(final Vector<String> pdbChainIds, final Vector<String> ndbChainIds,
    				   final Vector<String> pdbResidueIds, final Vector<Integer> ndbResidueIds)
    {        

         assert (ndbChainIds.size() == pdbChainIds.size()); /* &&
        		 ndbChainIds.size() == ndbResidueIds.size() &&
        		 ndbChainIds.size() == pdbResidueIds.size()); */
         
      	String pdbChainId, ndbChainId, pdbResidueId;
    	Integer ndbResidueId;
            
 		 // populate byPdbIds.
         for (int ix = 0; ix < pdbChainIds.size(); ix++)
         {
         	pdbChainId = pdbChainIds.get(ix);
        	ndbChainId = ndbChainIds.get(ix);
        	pdbResidueId = pdbResidueIds.get(ix);
        	ndbResidueId = ndbResidueIds.get(ix);
        	
        	if (pdbChainId.length() > 0)
       			// 0 is special case: many nonpolymers have no pdb chain id. Not sure what to do with this.            
        	{
	            HashMap<String,Object[]> residueIds = byPdbIds.get(pdbChainId);
	            if(residueIds == null)
	            {
	                residueIds = new HashMap<String,Object[]>();
	                residueIds.put(pdbResidueIds.get(ix), new Object[] {ndbChainId, ndbResidueId});
	                
	                this.byPdbIds.put(pdbChainId, residueIds);
	                
	            } else
	                residueIds.put(pdbResidueIds.get(ix), new Object[] {ndbChainId, ndbResidueIds});
        	}
        }
		
		
		// populate byNdbIds
        for (int ix = 0; ix < ndbChainIds.size(); ix++)
        {
         	pdbChainId = pdbChainIds.get(ix);
        	ndbChainId = ndbChainIds.get(ix);
        	pdbResidueId = pdbResidueIds.get(ix);
        	ndbResidueId = ndbResidueIds.get(ix);
        	
        	if (pdbChainId.length() == 0)
        		pdbChainId = null;
                // 0 is special case: many nonpolymers have no pdb chain id. Not sure what to do with this.
 
            HashMap<Integer,Object[]> residueIds = byNdbIds.get(ndbChainId);
            if(residueIds == null)
            {
                residueIds = new HashMap<Integer,Object[]>();
                residueIds.put(ndbResidueId, new Object[] {pdbChainId, pdbResidueId});
                
                this.byNdbIds.put(ndbChainId, residueIds);
            }
            
            else
                residueIds.put(ndbResidueId, new Object[] {pdbChainId, pdbResidueId});

        }
    }
    
    //private static Pattern endsWithLetters = Pattern.compile("[a-zA-Z]++$");
    /* returned data structure:
     *      Object[] ndbIds {
     *          String ndbChainId
     *          Integer ndbResidueId
     *      }
     */
    public Object[] getNdbIds(final String pdbChainId, final String pdbResidueId)
    {
    	// ndb and pdb chainIds/residueIds are now the same.
    	Object[] ndbIds = new Object[2];
    	ndbIds[0] = pdbChainId;
    	ndbIds[1] = pdbResidueId;
    	
//        final HashMap<String,Object[]> residueIds = byPdbIds.get(pdbChainId); 
//        
//        if(residueIds == null) {
//            Status.output(Status.LEVEL_DEBUG,"Error: " + pdbChainId + " is not a valid pdb chain id.");
//            return null;
//        }
//        
//        final Object[] ndbIds = residueIds.get(pdbResidueId);
//        if(ndbIds == null) {
//            Status.output(Status.LEVEL_DEBUG,"Error: " + pdbResidueId + " is not a valid pdb residue id in pdb chain " + pdbChainId);
//            return null;
//        }
        
        return ndbIds;
    }
    
    /* returned data structure:
     *      Object[] pdbIds {
     *          String pdbChainId
     *          String pdbResidueId
     *      }
     */
    public Object[] getPdbIds(final String ndbChainId, final Integer ndbResidueId)
    {
    	// ndb and pdb chainIds/residueIds are now the same.
    	Object[] pdbIds = new Object[2];
    	pdbIds[0] = ndbChainId;
    	pdbIds[1] = String.valueOf(ndbResidueId);
    	return pdbIds;
//        final HashMap<Integer,Object[]> residueIds = byNdbIds.get(ndbChainId); 
//        
//        System.out.println("PdbTpNdbConverter.getPdbIds: " + ndbChainId + ", " + ndbResidueId);
//        if(residueIds == null) {
//        	System.out.println(ndbChainId + " is not a valid pdb chain id.");
//            Status.output(Status.LEVEL_DEBUG,"Error: " + ndbChainId + " is not a valid pdb chain id.");
//            return null;
//        }
//        
//        final Object[] ndbIds = residueIds.get(ndbResidueId);
//        if(ndbIds == null) {
//        	System.out.println(ndbResidueId + " is not a valid pdb residue id in pdb chain " + ndbChainId);
//            Status.output(Status.LEVEL_DEBUG,"Error: " + ndbResidueId + " is not a valid pdb residue id in pdb chain " + ndbChainId);
//            return null;
//        }
//        
 //       return ndbIds;
    }
    
    public String getFirstPdbChainId(final String ndbChainId)
    {
        final HashMap<Integer,Object[]> residueIds = byNdbIds.get(ndbChainId); 
        
        if(residueIds == null) {
            return null;
        }
        
        return (String)(residueIds.values().iterator().next())[0];
    }
    
    /**
     * Convenience function to get an Ndb Residue id directly from a residue.
     * Uses the chain id embedded in the residue.
     * 
     * @param residue - the residue to get the ndb id.
     * @param notFoundValue - if not found, it will return this.
     * @return - the ndb id of the residue.
     */
    public String getResidueNdbId(final Residue residue, String notFoundValue)
    {
    	Object ndbIds[] = getNdbIds(residue.getChainId(), Integer.toString(residue.getResidueId()));    	
    	return ndbIds!= null && ndbIds[1] != null? ndbIds[1].toString() : notFoundValue;
    }
}
