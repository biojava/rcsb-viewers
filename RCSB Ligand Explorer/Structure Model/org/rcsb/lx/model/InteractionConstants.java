/*
 * Created on Jan 9, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.rcsb.lx.model;

import java.awt.Color;

/**
 * @author qzhang
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class InteractionConstants {

	/**
	 * Color used to display distance monitors in <code>StructureViewer</code>
	 */
	//public static float monitorColor[] = { 0.0f, 0.9f, 0.1f };
	public static float hydrophobicBondColor[]={1.0f, 0.0f, 1.0f};
	public static float hydrophilicBondColor[]={0.0f, 1.0f, 0.0f};
//	public static float dkRedColor[]={0.5f, 0.0f, 0.0f};
	public static float otherBondColor[]={1.0f, 1.0f, 1.0f};
	public static float interLigandBondColor[]={1.0f, 0.0f, 0.0f};
	public static float waterBondColor[]={0.0f,0.0f,1.0f};
	
	public static Color hydrophobicBondColorOb = new Color(InteractionConstants.hydrophobicBondColor[0], InteractionConstants.hydrophobicBondColor[1], InteractionConstants.hydrophobicBondColor[2]);
	public static Color hydrophilicBondColorOb = new Color(InteractionConstants.hydrophilicBondColor[0], InteractionConstants.hydrophilicBondColor[1], InteractionConstants.hydrophilicBondColor[2]);
	public static Color otherColorOb = new Color(InteractionConstants.otherBondColor[0], InteractionConstants.otherBondColor[1], InteractionConstants.otherBondColor[2]);
	public static Color interLigandBondColorOb = new Color(InteractionConstants.interLigandBondColor[0], InteractionConstants.interLigandBondColor[1], InteractionConstants.interLigandBondColor[2]);
	public static Color waterBondColorOb = new Color(InteractionConstants.waterBondColor[0], InteractionConstants.waterBondColor[1], InteractionConstants.waterBondColor[2]);

	public static final String hydrophobicType = "Hydrophobic";
	public static final String hydrophilicType = "Hydrophilic";
	public static final String interLigandType = "Inter-ligand";
	public static final String waterMediatedType = "Bridged H-Bond";
	public static final String otherType = "Other";
}
