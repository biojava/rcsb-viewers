package org.rcsb.mbt.model.util;

import org.rcsb.mbt.model.util.ChemicalComponentInfo;
import junit.framework.TestCase;


public class ChemicalComponentInfoTest  extends TestCase{


	public void  testChemCompInfo(){
		String letter = ChemicalComponentInfo.getLetterFromCode("ALA");

		assertNotNull("got a null instead of A", letter);
		assertEquals("Is not A!", letter,"A");

	}
}
