package com.aptana.parsing.lexer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RangeTest
{

	@Test
	public void testContains() throws Exception
	{
		Range r = new Range(0);
		assertTrue(r.contains(0));
		assertFalse(r.contains(1));
		assertFalse(r.contains(-1));

		r = new Range(0, 10);
		for (int i = 0; i <= 10; i++)
		{
			assertTrue(r.contains(i));
		}
		assertFalse(r.contains(-1));
		assertFalse(r.contains(11));
	}

	@Test
	public void testOffsets() throws Exception
	{
		Range r = new Range(0);
		assertEquals(0, r.getStartingOffset());
		assertEquals(0, r.getEndingOffset());
		assertEquals(1, r.getLength());

		r = new Range(0, 10);
		assertEquals(0, r.getStartingOffset());
		assertEquals(10, r.getEndingOffset());
		assertEquals(11, r.getLength());
	}

	@Test
	public void testIsEmpty() throws Exception
	{
		Range r = new Range(0);
		assertFalse(r.isEmpty());
		assertTrue(Range.EMPTY.isEmpty());
	}
}
