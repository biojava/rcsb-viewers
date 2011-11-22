/**
 * 
 */
package org.rcsb.mbt.structLoader;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.rcsb.mbt.model.util.OrderedPair;

/**
 * @author Peter Rose
 *
 */
public class StructAssemblyGenItemTest {

	/**
	 * Test method for {@link org.rcsb.mbt.structLoader.StructAssemblyGenItem#parseAsymIdString(java.lang.String)}.
	 */
	@Test
	public final void testParseAsymIdString() {
		String asymIdString = "A,B,C";
		StructAssemblyGenItem item = new StructAssemblyGenItem();
		item.parseAsymIdString(asymIdString);
		List<String> expected = Arrays.asList("A", "B", "C");
		assertEquals(expected, item.getAsymIdList());
	}

	/**
	 * Test method for {@link org.rcsb.mbt.structLoader.StructAssemblyGenItem#parseOperatorExpressionString(java.lang.String)}.
	 */
	@Test
	public final void testParseOperatorExpressionStringUnary() {
		String operatorList ="1,2,3";
		StructAssemblyGenItem item = new StructAssemblyGenItem();
		item.parseOperatorExpressionString(operatorList);
		List<String> expected = Arrays.asList("1", "2", "3");
		assertEquals(expected, item.getUnaryOperators());
	}
	
	/**
	 * Test method for {@link org.rcsb.mbt.structLoader.StructAssemblyGenItem#parseOperatorExpressionString(java.lang.String)}.
	 */
	@Test
	public final void testParseOperatorExpressionStringUnaryInParenthesis() {
		String operatorList ="(1,2,3)";
		StructAssemblyGenItem item = new StructAssemblyGenItem();
		item.parseOperatorExpressionString(operatorList);
		List<String> expected = Arrays.asList("1", "2", "3");
		assertEquals(expected, item.getUnaryOperators());
	}

	
	/**
	 * Test method for {@link org.rcsb.mbt.structLoader.StructAssemblyGenItem#parseOperatorExpressionString(java.lang.String)}.
	 */
	@Test
	public final void testParseOperatorExpressionStringUnaryRange() {
		String operatorList ="1-3";
		StructAssemblyGenItem item = new StructAssemblyGenItem();
		item.parseOperatorExpressionString(operatorList);
		List<String> expected = Arrays.asList("1", "2", "3");
		assertEquals(expected, item.getUnaryOperators());
	}
	
	/**
	 * Test method for {@link org.rcsb.mbt.structLoader.StructAssemblyGenItem#parseOperatorExpressionString(java.lang.String)}.
	 */
	@Test
	public final void testParseOperatorExpressionStringBinary() {
		String operatorList ="(1,2,3)(4,5)";
		StructAssemblyGenItem item = new StructAssemblyGenItem();
		item.parseOperatorExpressionString(operatorList);
		List<OrderedPair<String>> pairs = item.getBinaryOperators();
		assertEquals(6, pairs.size());
		assertEquals("[1,4]", pairs.get(0).toString());
		assertEquals("[1,5]", pairs.get(1).toString());
		assertEquals("[2,4]", pairs.get(2).toString());
		assertEquals("[2,5]", pairs.get(3).toString());
		assertEquals("[3,4]", pairs.get(4).toString());
		assertEquals("[3,5]", pairs.get(5).toString());
	}
	
	/**
	 * Test method for {@link org.rcsb.mbt.structLoader.StructAssemblyGenItem#parseOperatorExpressionString(java.lang.String)}.
	 */
	@Test
	public final void testParseOperatorExpressionStringBinaryRange() {
		String operatorList ="(1-3)(4,5)";
		StructAssemblyGenItem item = new StructAssemblyGenItem();
		item.parseOperatorExpressionString(operatorList);
		List<OrderedPair<String>> pairs = item.getBinaryOperators();
		assertEquals(6, pairs.size());
		assertEquals("[1,4]", pairs.get(0).toString());
		assertEquals("[1,5]", pairs.get(1).toString());
		assertEquals("[2,4]", pairs.get(2).toString());
		assertEquals("[2,5]", pairs.get(3).toString());
		assertEquals("[3,4]", pairs.get(4).toString());
		assertEquals("[3,5]", pairs.get(5).toString());
	}
	
	/**
	 * Test method for {@link org.rcsb.mbt.structLoader.StructAssemblyGenItem#parseOperatorExpressionString(java.lang.String)}.
	 */
	@Test
	public final void testParseOperatorExpressionStringBinaryMixed() {
		String operatorList ="(1,2-3)(4,5)";
		StructAssemblyGenItem item = new StructAssemblyGenItem();
		item.parseOperatorExpressionString(operatorList);
		List<OrderedPair<String>> pairs = item.getBinaryOperators();
		assertEquals(6, pairs.size());
		assertEquals("[1,4]", pairs.get(0).toString());
		assertEquals("[1,5]", pairs.get(1).toString());
		assertEquals("[2,4]", pairs.get(2).toString());
		assertEquals("[2,5]", pairs.get(3).toString());
		assertEquals("[3,4]", pairs.get(4).toString());
		assertEquals("[3,5]", pairs.get(5).toString());
	}
	
	/**
	 * Test method for {@link org.rcsb.mbt.structLoader.StructAssemblyGenItem#parseOperatorExpressionString(java.lang.String)}.
	 */
	@Test
	public final void testParseOperatorExpressionStringException() {
		String operatorList ="(1,2-3(4,5)";
		StructAssemblyGenItem item = new StructAssemblyGenItem();
		try {
			item.parseOperatorExpressionString(operatorList);
			fail("Should raise an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
		}
	}

}