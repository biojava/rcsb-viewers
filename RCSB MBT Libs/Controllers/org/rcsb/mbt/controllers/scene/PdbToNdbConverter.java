package org.rcsb.mbt.controllers.scene;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.rcsb.mbt.model.util.Status;


/**
 * @author John Beaver
 */
// for converting residue and chain ids from pdb values to their ndb equivalents.
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
    private final HashMap byPdbIds = new HashMap();
	
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
    private final HashMap byNdbIds = new HashMap();

    
    //input: three parallel Vectors. 
    //   pdbChainIds: the pdb_strand_id column of pdbx_poly_seq_scheme (Strings)
    //   ndbChainIds: the ndb_strand_id column of pdbx_poly_seq_scheme (Strings)
    //   ndbResidueIds: the ndb_seq_num column of pdbx_poly_seq_scheme (Integers)
    //   pdbResidueIds: the pdb_seq_num column of pdbx_poly_seq_scheme (Strings)
    public void append(final Vector pdbChainIds, final Vector ndbChainIds, final Vector pdbResidueIds, final Vector ndbResidueIds) {
        
		// populate this.byPdbIds.
		Iterator ndbChainIt = ndbChainIds.iterator();
        Iterator ndbResidueIt = ndbResidueIds.iterator();
        Iterator pdbChainIt = pdbChainIds.iterator();
        Iterator pdbResidueIt = pdbResidueIds.iterator();
        
        while(ndbChainIt.hasNext()) {
            final String ndbChainId = (String)ndbChainIt.next();
            final String pdbChainId = (String)pdbChainIt.next();
            final Integer ndbResidueId = (Integer)ndbResidueIt.next();
            final String pdbResidueId = (String)pdbResidueIt.next();
            
            // special case: many nonpolymers have no pdb chain id. Not sure what to do with this.
            if(pdbChainId.length() == 0) {
                continue;
            }
            
            HashMap residueIds = (HashMap)this.byPdbIds.get(pdbChainId);
            if(residueIds == null) {
                residueIds = new HashMap();
                residueIds.put(pdbResidueId, new Object[] {ndbChainId, ndbResidueId});
                
                this.byPdbIds.put(pdbChainId, residueIds);
            } else {
                residueIds.put(pdbResidueId, new Object[] {ndbChainId, ndbResidueId});
            }
        }
		
		
		// populate this.byNdbIds
		ndbChainIt = ndbChainIds.iterator();
        ndbResidueIt = ndbResidueIds.iterator();
        pdbChainIt = pdbChainIds.iterator();
        pdbResidueIt = pdbResidueIds.iterator();
        
        while(ndbChainIt.hasNext()) {
            final String ndbChainId = (String)ndbChainIt.next();
            String pdbChainId = (String)pdbChainIt.next();
            final Integer ndbResidueId = (Integer)ndbResidueIt.next();
            final String pdbResidueId = (String)pdbResidueIt.next();
            
            // special case: many nonpolymers have no pdb chain id. Not sure what to do with this.
            if(pdbChainId.length() == 0) {
                pdbChainId = null;
            }
            
            HashMap residueIds = (HashMap)this.byNdbIds.get(ndbChainId);
            if(residueIds == null) {
                residueIds = new HashMap();
                residueIds.put(ndbResidueId, new Object[] {pdbChainId, pdbResidueId});
                
                this.byNdbIds.put(ndbChainId, residueIds);
            } else {
                residueIds.put(ndbResidueId, new Object[] {pdbChainId, pdbResidueId});
            }
        }
    }
    
    //private static Pattern endsWithLetters = Pattern.compile("[a-zA-Z]++$");
    /* returned data structure:
     *      Object[] ndbIds {
     *          String ndbChainId
     *          Integer ndbResidueId
     *      }
     */
    public Object[] getNdbIds(final String pdbChainId, final String pdbResidueId) {
        final HashMap residueIds = (HashMap)this.byPdbIds.get(pdbChainId); 
        
        if(residueIds == null) {
            Status.output(Status.LEVEL_DEBUG,"Error: " + pdbChainId + " is not a valid pdb chain id.");
            return null;
        }
        
        final Object[] ndbIds = (Object[])residueIds.get(pdbResidueId);
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
    public Object[] getPdbIds(final String ndbChainId, final Integer ndbResidueId) {
        final HashMap residueIds = (HashMap)this.byNdbIds.get(ndbChainId); 
        
        if(residueIds == null) {
            Status.output(Status.LEVEL_DEBUG,"Error: " + ndbChainId + " is not a valid pdb chain id.");
            return null;
        }
        
        final Object[] ndbIds = (Object[])residueIds.get(ndbResidueId);
        if(ndbIds == null) {
            Status.output(Status.LEVEL_DEBUG,"Error: " + ndbResidueId + " is not a valid pdb residue id in pdb chain " + ndbChainId);
            return null;
        }
        
        return ndbIds;
    }
    
    public String getFirstPdbChainId(final String ndbChainId) {
        final HashMap residueIds = (HashMap)this.byNdbIds.get(ndbChainId); 
        
        if(residueIds == null) {
            return null;
        }
        
        return (String)((Object[])residueIds.values().iterator().next())[0];
    }
}