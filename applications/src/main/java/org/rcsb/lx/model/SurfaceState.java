package org.rcsb.lx.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.vecmath.Color4f;

import org.jcolorbrewer.ColorBrewer;
import org.rcsb.mbt.model.Residue;
import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.Surface;
import org.rcsb.mbt.model.attributes.SurfaceColorUpdater;
import org.rcsb.uiApp.controllers.doc.SurfaceThread;

public class SurfaceState {
	private List<Integer> entitySet = null;
	
	private Structure structure = null;
	private boolean structureModified = true;
	
	private Residue[] ligands = null;
	private boolean ligandsModified = true;
	
    private float transparency = 0.0f;  
    private boolean transparencyModified = true;
    
    private int colorType = 0;
    private boolean colorTypeModified = true;
    
	private Color color = null;
	private boolean colorModified = true;
	
	private ColorBrewer colorBrewer = null;
	private boolean colorBrewerModified = true;
	
	private boolean useDistanceCutoff = false;
//	private boolean useDistanceCutoffModified = true;
	
	private float distance = 6.5f;
	private boolean distanceModified = true;
	
	private int surfaceType = 0;
	private boolean surfaceTypeModified = true;
	
	public SurfaceState(Structure structure) {
		this.structure = structure;
//		System.out.println("New surface state for: " + structure.getUrlString());
	}

	/**
	 * @return the structure
	 */
	public Structure getStructure() {
		return structure;
	}

	/**
	 * @param structure the structure to set
	 */
	public void setStructure(Structure structure) {
		this.structure = structure;
		structureModified = true;
	}

	/**
	 * @return the ligands
	 */
	public Residue[] getLigands() {
		return ligands;
	}

	/**
	 * @param ligands the ligands to set
	 */
	public void setLigands(Residue[] ligands) {
		this.ligands = ligands;
		ligandsModified = true;
	}

	/**
	 * @return the transparency
	 */
	public float getTransparency() {
		return transparency;
	}

	/**
	 * @param transparency the transparency to set
	 */
	public void setTransparency(float transparency) {
		this.transparency = transparency;
		transparencyModified = true;
	}

	/**
	 * @return the colorType
	 */
	public int getColorType() {
		return colorType;
	}

	/**
	 * @param colorType the colorType to set
	 */
	public void setColorType(int colorType) {
		this.colorType = colorType;
		colorTypeModified = true;
	}

	/**
	 * @return the color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * @param color the color to set
	 */
	public void setColor(Color color) {
		this.color = color;
		colorModified = true;
	}

	/**
	 * @return the colorBrewer
	 */
	public ColorBrewer getColorBrewer() {
		return colorBrewer;
	}

	/**
	 * @param colorBrewer the colorBrewer to set
	 */
	public void setColorBrewer(ColorBrewer colorBrewer) {
		this.colorBrewer = colorBrewer;
		colorBrewerModified = true;
	}

	/**
	 * @return the useDistanceCutoff
	 */
	public boolean isUseDistanceCutoff() {
		return useDistanceCutoff;
	}

	/**
	 * @param useDistanceCutoff the useDistanceCutoff to set
	 */
	public void setUseDistanceCutoff(boolean useDistanceCutoff) {
		this.useDistanceCutoff = useDistanceCutoff;
//		useDistanceCutoffModified = true;
	}

	/**
	 * @return the distance
	 */
	public float getDistance() {
		return distance;
	}

	/**
	 * @param distance the distance to set
	 */
	public void setDistance(float distance) {
		this.distance = distance;
		distanceModified = true;
	}

	/**
	 * @return the surfaceType
	 */
	public int getSurfaceType() {
		return surfaceType;
	}

	/**
	 * @param surfaceType the surfaceType to set
	 */
	public void setSurfaceType(int surfaceType) {
		this.surfaceType = surfaceType;
		surfaceTypeModified = true;
	}

	public void updateState() {
		if (structureModified || ligandsModified || distanceModified || surfaceTypeModified) {
			removeSurfaces();
			SurfaceThread thread = new SurfaceThread();	
			thread.createBindingSiteSurface(ligands, surfaceType, distance);
			// entity set need to be recalculated for new structures. It is used for coloring by entity.
			if (structureModified) {
				createEntitySet();
			}
			updateColors();
            updateTransparency();
		} else {
	       if (colorModified || colorBrewerModified || colorTypeModified) {
	    	   updateColors();
	       }
	       if (transparencyModified) {
	    	   updateTransparency();
	       }
		}
		resetState(false);
	}
	
	public void resetState(boolean value) {
		structureModified = value;
		ligandsModified = value;
	    transparencyModified = value;
	    colorTypeModified = value;
		colorModified = value;
		colorBrewerModified = value;
//		useDistanceCutoffModified = value;
		distanceModified = value;
		surfaceTypeModified = value;
	}
	
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("Structure: ");
		b.append(structure.getUrlString());
		b.append(", ligand: ");
		b.append(Arrays.toString(ligands));
		b.append(", transparency: ");
		b.append(transparency);
		b.append(", surfaceType: ");
		b.append(surfaceType);
		return b.toString();
	}
	
	private void updateColors() {
		int surfaceCount = structure.getStructureMap().getSurfaceCount();
		int sIndex = 0;
		for (Surface s: structure.getStructureMap().getSurfaces()) {
			if (colorType == 2) {
				// color by chain
				SurfaceColorUpdater.setPaletteColor(s, colorBrewer, surfaceCount, sIndex);
				sIndex++;
			} else if (colorType == 3) {
				// color by chain type (entity id)			
				int entityId = s.getChain().getEntityId();
				SurfaceColorUpdater.setPaletteColor(s, colorBrewer, entitySet.size(), entitySet.indexOf(entityId));
			} else if (colorType == 1) {
				// color by a single color
				SurfaceColorUpdater.setSurfaceColor(s, new Color4f(color));
			} else if (colorType == 0) {
				// color by hydrophobicity
				SurfaceColorUpdater.setHydrophobicSurfaceColor(s);
			}
		}
	}
	
	private void updateTransparency() {
		for (Surface s: structure.getStructureMap().getSurfaces()) {
			SurfaceColorUpdater.setSurfaceTransparency(s, transparency);
		}
	}
	
	/**
	 * @return
	 */
	private void createEntitySet() {
		entitySet = new ArrayList<Integer>();
		for (Surface s: structure.getStructureMap().getSurfaces()) {
			if (! entitySet.contains(s.getChain().getEntityId())) {
				entitySet.add(s.getChain().getEntityId());
			}
		}
	}
	
	private void removeSurfaces() {
		int n = structure.getStructureMap().getSurfaceCount();
		for (int i = n-1; i >= 0; i--) {
			Surface s= structure.getStructureMap().getSurface(i);
			structure.getStructureMap().removeSurface(s);
		}
	}
}
