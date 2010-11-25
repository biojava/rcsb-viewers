/**
 * 
 */
package org.rcsb.mbt.model.util;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Peter Rose
 *
 */
public class CartesianProductTest {

	List<String> list1 = null;
	List<String> list2 = null;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		list1 = Arrays.asList("A","B","C");
		list2 = Arrays.asList("X","Y");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link org.rcsb.mbt.model.util.CartesianProduct#getOrderedPairs()}.
	 */
	@Test
	public final void testGetOrderedPairs() {
		CartesianProduct<String> product = new CartesianProduct<String>(list1, list2);
		List<OrderedPair<String>> pairs = product.getOrderedPairs();
		assertEquals(6, pairs.size());
		assertEquals("[A,X]", pairs.get(0).toString());
		assertEquals("[A,Y]", pairs.get(1).toString());
		assertEquals("[B,X]", pairs.get(2).toString());
		assertEquals("[B,Y]", pairs.get(3).toString());
		assertEquals("[C,X]", pairs.get(4).toString());
		assertEquals("[C,Y]", pairs.get(5).toString());
	}
}