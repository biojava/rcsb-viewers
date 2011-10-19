package org.rcsb.mbt.model.attributes;

import java.util.List;

import javax.vecmath.Color4f;

import org.rcsb.mbt.model.Atom;
import org.rcsb.mbt.model.Surface;
import org.rcsb.mbt.model.util.ChemicalComponentInfo;
import org.rcsb.mbt.surface.datastructure.VertInfo;

public final class SurfaceColorUpdater {
	private static InterpolatedColorMap hydrophobicityMap = new InterpolatedColorMap(InterpolatedColorMap.HYDROPHOBICITY_RAMP);

	/**
     * Don't let anyone instantiate this class.
     */
    private SurfaceColorUpdater() {}
	
	public static void setHydrophobicSurfaceColor(Surface surface) {
		// set color
		List<VertInfo> verts = surface.getTriangulatedSurface().getVertices();
		int vertexCount = verts.size();
		System.out.println("# vertices: " + vertexCount);
		
		Color4f[] colors = surface.getColors();
		if (colors == null || colors.length != vertexCount) {
			colors = null;
			colors = new Color4f[vertexCount];
			surface.setColors(colors);
		}
		
		for (int i = 0; i < vertexCount; i++) {
			VertInfo v = verts.get(i);
			colors[i] = getHydrophobicityColorScheme((Atom)v.reference);
		}
	}
	
	public static void setPaletteColor(Surface surface, ColorBrewer brewer, int colorCount, int colorIndex) {
		List<VertInfo> verts = surface.getTriangulatedSurface().getVertices();
		int vertexCount = verts.size();
		
		Color4f[] colors = surface.getColors();
		if (colors == null || colors.length != vertexCount) {
			colors = null;
			colors = new Color4f[vertexCount];
			surface.setColors(colors);
		}
		Color4f[] palette = brewer.getColor4fPalette(colorCount);
		Color4f color = palette[colorIndex];
		
		for (int i = 0; i < vertexCount; i++) {
			colors[i] = color;
		}
	}
	
	public static void setSurfaceColor(Surface surface, Color4f color) {
		// set color
		List<VertInfo> verts = surface.getTriangulatedSurface().getVertices();
		int vertexCount = verts.size();
		System.out.println("# vertices: " + vertexCount);
		
		Color4f[] colors = surface.getColors();
		if (colors == null || colors.length != vertexCount) {
			colors = null;
			colors = new Color4f[vertexCount];
			surface.setColors(colors);
		}
		
		for (int i = 0; i < vertexCount; i++) {
			colors[i] = color;
		}
	}
	
	public static void setSurfaceTransparency(Surface surface, float transparency) {
		// set color
		List<VertInfo> verts = surface.getTriangulatedSurface().getVertices();
		int vertexCount = verts.size();
		System.out.println("# vertices: " + vertexCount);
		
		Color4f[] colors = surface.getColors();
		if (colors == null || colors.length != vertexCount) {
			colors = null;
			colors = new Color4f[vertexCount];
			surface.setColors(colors);
		}
		
		for (int i = 0; i < vertexCount; i++) {
			colors[i].setW(transparency);
		}
	}
	
	private static Color4f getHydrophobicityColorScheme(Atom atom) {
		float hydrophobicity = ChemicalComponentInfo.getHydrophobicityFromCode(atom.compound);
		float[] colors = new float[3];
		hydrophobicityMap.getColor(hydrophobicity, colors);
		return new Color4f(colors[0], colors[1], colors[2], 1.0f);
	}
}
