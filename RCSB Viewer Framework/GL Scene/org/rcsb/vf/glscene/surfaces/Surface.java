package org.rcsb.vf.glscene.surfaces;

import java.util.Vector;

import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.StructureComponent;
import org.rcsb.mbt.model.StructureComponentRegistry.ComponentType;


public class Surface extends StructureComponent {
	public Vector atoms;
	
	public Surface(final Vector atoms, final Structure structure) {
		super();
		super.structure = structure;
		
		this.atoms = atoms;
	}

	
	public void copy(final StructureComponent structureComponent) {
		final Surface surface = (Surface)structureComponent;
		this.atoms = surface.atoms;
	}

	
	public ComponentType getStructureComponentType() {
		return ComponentType.SURFACE;
	}

}
