package com.aptana.js.core.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ReturnTypeElementTest
{

	@Test
	public void testEquals() throws Exception
	{
		ReturnTypeElement first = new ReturnTypeElement();
		first.setType("Object");

		ReturnTypeElement other = new ReturnTypeElement();
		other.setType("Object");
		assertEquals("same type should be equal", first, other);

		// Different descriptions.
		first.setDescription("first");
		other.setDescription("other");
		assertEquals("Expected same type different description to still be equal", first, other);
	}

}
