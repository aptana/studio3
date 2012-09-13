package com.aptana.editor.js.contentassist.model;

import java.text.MessageFormat;

import junit.framework.TestCase;

public class ReturnTypeElementTest extends TestCase
{

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

	public void testValidateTypeName() throws Exception
	{
		// Standard type
		assertValid("Object");

		// Array with type info
		assertValid("Array<Boolean>");

		// Module id with path separators
		assertValid("plugins/highlighter/shCore");

		// $
		assertValid("$");
		assertValid("$.e");

		// hyphens
		assertValid("-dynamic-type-jafg-asf2r3-frwrewvr-vew");

		// Underscores
		assertValid("_fixIESelection.n");

		// Invalid empty values
		assertInvalid("", "");
		assertInvalid("", null);

		// Invalid type string we're seeing in logs for https://jira.appcelerator.org/browse/APSTUD-7366
		assertInvalid("Number", "Number,Object,Array,Function");
	}

	private void assertInvalid(String fixed, String typeName)
	{
		assertEquals(
				MessageFormat.format("Expected to fix invalid type name ''{0}'' to become ''{1}''", typeName, fixed),
				fixed, new ReturnTypeElement().validateTypeName(typeName));
	}

	private void assertValid(String typeName)
	{
		assertEquals(MessageFormat.format("Expected ''{0}'' to be valid type name", typeName), typeName,
				new ReturnTypeElement().validateTypeName(typeName));
	}

}
