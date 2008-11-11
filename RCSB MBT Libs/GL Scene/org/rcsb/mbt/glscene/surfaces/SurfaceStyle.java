package org.rcsb.mbt.glscene.surfaces;

import org.rcsb.mbt.model.StructureComponentRegistry.ComponentType;
import org.rcsb.mbt.model.attributes.Style;

public class SurfaceStyle extends Style {

	public SurfaceStyle() {
		super();
	}

	
	public boolean isTypeSafe(final ComponentType scType) {
		return true;
	}

}
