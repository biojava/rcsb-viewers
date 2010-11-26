package org.rcsb.mbt.model.util;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;


import junit.framework.TestCase;

/**
 * @author Peter Rose
 *
 */
public class ChemicalComponentInfoTest extends TestCase{
	
	
	/**
	 * Test method for getChemicalComponentType
	 */

	public  void testGetChemicalComponent() {
		ChemicalComponentType t = ChemicalComponentType.getChemicalComponentType("L-PEPTIDE LINKING");
		assertEquals(ChemicalComponentType.L_PEPTIDE_LINKING, t);	
	}
}
