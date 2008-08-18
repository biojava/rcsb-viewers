package org.rcsb.ks.model;

import org.rcsb.mbt.model.Residue;

public class GeneralEntityDescriptor implements EntityDescriptor {

	private String description = "";
	private String entityId = "";
//	private StructureMap structureMap = null;
	
	public GeneralEntityDescriptor ( String _description, String _entityId ){
		description = _description;
		entityId = _entityId;
	}
	
	public String getDescription ()
	{
		return description;
	}
	
	public String getDescription(float _x, float _y, float _z) {
	
		
		return null;
	}

	public String getDescription(Residue _residue) {
		return null;
	}
	
	public String getEntityId ()
	{
		return entityId;
	}

}
