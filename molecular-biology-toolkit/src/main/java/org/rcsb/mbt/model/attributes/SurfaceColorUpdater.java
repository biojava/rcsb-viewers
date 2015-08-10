/*
 * BioJava development code
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence. This should
 * be distributed with the code. If you do not have a copy,
 * see:
 *
 * http://www.gnu.org/copyleft/lesser.html
 *
 * Copyright for this code is held jointly by the individual
 * authors. These should be listed in @author doc comments.
 *
 * For more information on the BioJava project and its aims,
 * or to join the biojava-l mailing list, visit the home page
 * at:
 *
 * http://www.biojava.org/
 *
 * This code was contributed from the Molecular Biology Toolkit
 * (MBT) project at the University of California San Diego.
 *
 * Please reference J.L. Moreland, A.Gramada, O.V. Buzko, Qing
 * Zhang and P.E. Bourne 2005 The Molecular Biology Toolkit (MBT):
 * A Modular Platform for Developing Molecular Visualization
 * Applications. BMC Bioinformatics, 6:21.
 *
 * The MBT project was funded as part of the National Institutes
 * of Health PPG grant number 1-P01-GM63208 and its National
 * Institute of General Medical Sciences (NIGMS) division. Ongoing
 * development for the MBT project is managed by the RCSB
 * Protein Data Bank(http://www.pdb.org) and supported by funds
 * from the National Science Foundation (NSF), the National
 * Institute of General Medical Sciences (NIGMS), the Office of
 * Science, Department of Energy (DOE), the National Library of
 * Medicine (NLM), the National Cancer Institute (NCI), the
 * National Center for Research Resources (NCRR), the National
 * Institute of Biomedical Imaging and Bioengineering (NIBIB),
 * the National Institute of Neurological Disorders and Stroke
 * (NINDS), and the National Institute of Diabetes and Digestive
 * and Kidney Diseases (NIDDK).
 *
 * Created on 2011/11/08
 *
 */ 

package org.rcsb.mbt.model.attributes;

import java.awt.*;
import java.util.List;

/**
 * Updates the color and transparency of surfaces
 * 
 * @author Peter Rose
 *
 */
import javax.vecmath.Color4f;

import org.jcolorbrewer.ColorBrewer;
import org.rcsb.mbt.model.Atom;
import org.rcsb.mbt.model.Surface;
import org.rcsb.mbt.model.util.ChemicalComponentInfo;
import org.rcsb.mbt.model.util.ColorConverter;
import org.rcsb.mbt.surface.datastructure.VertInfo;

public final class SurfaceColorUpdater {
	private static InterpolatedColorMap hydrophobicityMap = new InterpolatedColorMap(InterpolatedColorMap.HYDROPHOBICITY_RAMP);

	/**
     * Don't let anyone instantiate this class.
     */
    private SurfaceColorUpdater() {}
	
	public static void setHydrophobicSurfaceColor(Surface surface) {
		List<VertInfo> verts = surface.getTriangulatedSurface().getVertices();
		int vertexCount = verts.size();
		
		Color4f[] colors = surface.getColors();
		if (colors == null || colors.length != vertexCount) {
			colors = null;
			colors = new Color4f[vertexCount];
			surface.setColors(colors);
		}
		// retain current transparency w
		float transparency = 1.0f;
		if (colors[0] != null) {
			transparency = colors[0].w;
		}
		
		for (int i = 0; i < vertexCount; i++) {
			VertInfo v = verts.get(i);
			colors[i] = getHydrophobicityColorScheme((Atom)v.reference);
			colors[i].w = transparency;
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

		Color[] col = brewer.getColorPalette(colorCount);
		Color4f[] palette = ColorConverter.convertColor4f(col);
		Color4f color = palette[colorIndex];
		Color4f newColor = (Color4f) color.clone();
		
		// retain current transparency w
		if (colors[0] != null) {
			newColor.w = colors[0].w;
		}

		for (int i = 0; i < vertexCount; i++) {
			colors[i] = newColor;
		}
	}
	
	public static void setSurfaceColor(Surface surface, Color4f color) {
		List<VertInfo> verts = surface.getTriangulatedSurface().getVertices();
		int vertexCount = verts.size();
		
		Color4f[] colors = surface.getColors();
		if (colors == null || colors.length != vertexCount) {
			colors = null;
			colors = new Color4f[vertexCount];
			surface.setColors(colors);
		} 
		
        Color4f newColor = (Color4f) color.clone();
		
		// retain current transparency w
		if (colors[0] != null) {
			newColor.w = colors[0].w;
		}
		
		for (int i = 0; i < vertexCount; i++) {
			colors[i] = newColor;
		}
	}
	
	public static void setSurfaceTransparency(Surface surface, float transparency) {
		List<VertInfo> verts = surface.getTriangulatedSurface().getVertices();
		int vertexCount = verts.size();
		
		Color4f[] colors = surface.getColors();
		if (colors == null || colors.length != vertexCount) {
			colors = null;
			colors = new Color4f[vertexCount];
			surface.setColors(colors);
		}
		
		for (int i = 0; i < vertexCount; i++) {
			colors[i].w = transparency;
		}
	}
	
	public static void setSurfaceTransparencyToggle(Surface surface) {
		List<VertInfo> verts = surface.getTriangulatedSurface().getVertices();
		int vertexCount = verts.size();
		
		Color4f[] colors = surface.getColors();
		if (colors == null || colors.length != vertexCount) {
			colors = null;
			colors = new Color4f[vertexCount];
			surface.setColors(colors);
		}
		
		float transparency = 1.0f;
		if (colors[0] != null) {
			transparency = colors[0].w;
			if (transparency > 0.4f) {
				transparency = 0.4f;
			} else {
				transparency = 1.0f;
			}
			for (int i = 0; i < vertexCount; i++) {
				colors[i].w = transparency;
			}
		}
		
	}
	
	private static Color4f getHydrophobicityColorScheme(Atom atom) {
		float hydrophobicity = ChemicalComponentInfo.getHydrophobicityFromCode(atom.compound);
		float[] colors = new float[3];
		hydrophobicityMap.getColor(hydrophobicity, colors);
		return new Color4f(colors[0], colors[1], colors[2], 1.0f);
	}
}
