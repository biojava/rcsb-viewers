package org.rcsb.mbt.model.util;

import java.util.HashMap;
import java.util.Vector;

import org.rcsb.mbt.model.Residue;



/**
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
        final HashMap<String,Object[]> residueIds = byPdbIds.get(pdbChainId); 
        
        if(residueIds == null) {
            Status.output(Status.LEVEL_DEBUG,"Error: " + pdbChainId + " is not a valid pdb chain id.");
            return null;
        }
        
        final Object[] ndbIds = residueIds.get(pdbResidueId);
        if(ndbIds == null) {
            Status.output(Status.LEVEL_DEBUG,"Error: " + pdbResidueId + " is not a valid pdb residue id in pdb chain " + pdbChainId);
            return null;
        }
        
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
        final HashMap<Integer,Object[]> residueIds = byNdbIds.get(ndbChainId); 
        
        if(residueIds == null) {
            Status.output(Status.LEVEL_DEBUG,"Error: " + ndbChainId + " is not a valid pdb chain id.");
            return null;
        }
        
        final Object[] ndbIds = residueIds.get(ndbResidueId);
        if(ndbIds == null) {
            Status.output(Status.LEVEL_DEBUG,"Error: " + ndbResidueId + " is not a valid pdb residue id in pdb chain " + ndbChainId);
            return null;
        }
        
        return ndbIds;
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