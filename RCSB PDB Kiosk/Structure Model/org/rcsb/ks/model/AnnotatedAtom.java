package org.rcsb.ks.model;

import org.rcsb.mbt.model.Atom;



public class AnnotatedAtom extends Atom
{
	private String annotation = "";
	private String entity_id = "";
	
	public AnnotatedAtom ()
	{
		
	}
	public AnnotatedAtom ( String _annotation, String _entity_id ){
		annotation = _annotation;
		entity_id = _entity_id;
	}
	public String getAnnotation ()
	{
		return annotation;
	}
	public String getEntityId ()
	{
		return entity_id;
	}
	public void setEntityId(String eid) {
		entity_id = eid;
	}
	
	
}
