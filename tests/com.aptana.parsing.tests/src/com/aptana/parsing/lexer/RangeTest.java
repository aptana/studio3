package com.aptana.parsing.lexer;

import junit.framework.TestCase;

public class RangeTest extends TestCase
{

	protected void setUp() throws Exception
	{
		super.setUp();
	}

	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

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

	public void testIsEmpty() throws Exception
	{
		Range r = new Range(0);
		assertFalse(r.isEmpty());
		assertTrue(Range.EMPTY.isEmpty());
	}
}
