package org.rcsb.mbt.glscene.surfaces;

import java.util.Vector;

import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.StructureComponent;


public class Surface extends StructureComponent {
	public Vector atoms;
	
	public static final String COMPONENT_TYPE = "Surface";
	
	public Surface(final Vector atoms, final Structure structure) {
		super();
		super.structure = structure;
		
		this.atoms = atoms;
	}

	
	public void copy(final StructureComponent structureComponent) {
		final Surface surface = (Surface)structureComponent;
		this.atoms = surface.atoms;
	}

	
	public String getStructureComponentType() {
		return Surface.COMPONENT_TYPE;
	}

}
