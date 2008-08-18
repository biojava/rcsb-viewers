package org.rcsb.ks.model;

public interface EntityDescriptor {

	public String getDescription ( float _x, float _y, float _z );
	public String getDescription ( org.rcsb.mbt.model.Residue _residue );
	public String getDescription ();
	public String getEntityId(); 	
}
