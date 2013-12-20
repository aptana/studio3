package com.aptana.js.core.inferencing;

import org.junit.Test;
import static org.junit.Assert.*;
import java.text.MessageFormat;
import java.util.List;

import com.aptana.js.core.inferencing.JSTypeUtil;

import junit.framework.TestCase;

public class JSTypeUtilTest
{

	@Test
	public void testGetFunctionSignatureReturnTypeNames() throws Exception
	{
		List<String> returnTypeNames = JSTypeUtil.getFunctionSignatureReturnTypeNames("Function<Object,Number>");
		assertEquals(2, returnTypeNames.size());
		assertTrue(returnTypeNames.contains("Object"));
		assertTrue(returnTypeNames.contains("Number"));
	}

	@Test
	public void testGetFunctionSignatureReturnTypeNames2() throws Exception
	{
		List<String> returnTypeNames = JSTypeUtil.getFunctionSignatureReturnTypeNames("Function<Object,Array<Number>>");
		assertEquals(2, returnTypeNames.size());
		assertTrue(returnTypeNames.contains("Object"));
		assertTrue(returnTypeNames.contains("Array<Number>"));
	}

	@Test
	public void testGetFunctionSignatureReturnTypeNames3() throws Exception
	{
		List<String> returnTypeNames = JSTypeUtil
				.getFunctionSignatureReturnTypeNames("Function<Object,Function<Number,String>>");
		assertEquals(2, returnTypeNames.size());
		assertTrue(returnTypeNames.contains("Object"));
		assertTrue(returnTypeNames.contains("Function<Number,String>"));
	}

	@Test
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

		// Legacy array type used in our metadata https://jira.appcelerator.org/browse/APSTUD-7438
		assertInvalid("Array<Anchor>", "Anchor[]");
	}

	private void assertInvalid(String fixed, String typeName)
	{
		assertEquals(
				MessageFormat.format("Expected to fix invalid type name ''{0}'' to become ''{1}''", typeName, fixed),
				fixed, JSTypeUtil.validateTypeName(typeName));
	}

	private void assertValid(String typeName)
	{
		assertEquals(MessageFormat.format("Expected ''{0}'' to be valid type name", typeName), typeName,
				JSTypeUtil.validateTypeName(typeName));
	}
}
