package org.rcsb.mbt.model.util;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;

import junit.framework.Test;

/**
 * @author Peter Rose
 *
 */
public class ChemicalComponentInfoTest {
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	/**
	 * Test method for getChemicalComponentType
	 */
//	@Test
	public final void testGetChemicalComponent() {
		ChemicalComponentType t = ChemicalComponentType.getChemicalComponentType("L-PEPTIDE LINKING");
		assertEquals(ChemicalComponentType.L_PEPTIDE_LINKING, t);	
	}
}
