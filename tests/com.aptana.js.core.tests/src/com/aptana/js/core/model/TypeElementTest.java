package com.aptana.js.core.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TypeElementTest
{

	private TypeElement te;

	@Before
	public void setUp() throws Exception
	{
		te = new TypeElement();
	}

	@After
	public void tearDown() throws Exception
	{
		te = null;
	}

	@Test
	public void testDontAllowSettingSelfAsParentType()
	{
		te.setName("x.b");
		te.addParentType("Object");
		assertTrue(te.getParentTypes().contains("Object"));
		te.addParentType("x.b");
		assertFalse(te.getParentTypes().contains("x.b"));
	}

}
