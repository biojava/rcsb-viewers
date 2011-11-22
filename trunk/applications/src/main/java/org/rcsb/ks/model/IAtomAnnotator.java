package org.rcsb.ks.model;

/**
 * Needed to unify the annotation part of XAtom and Atom derivatives
 * 
 * @author rickb
 *
 */
public interface IAtomAnnotator
{
	public String getAnnotation ();
	public String getEntityId ();
	public void setEntityId(String eid);
}
