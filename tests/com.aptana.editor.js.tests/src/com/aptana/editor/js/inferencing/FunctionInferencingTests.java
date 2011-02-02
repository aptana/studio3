/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.inferencing;

import java.util.List;

import org.eclipse.core.runtime.Path;

import com.aptana.editor.js.JSTypeConstants;

public class FunctionInferencingTests extends InferencingTestsBase
{
	/**
	 * testReturnsBoolean
	 */
	public void testReturnsBoolean()
	{
		this.lastStatementTypeTests(Path.fromPortableString("inferencing/function-returns-boolean.js"), "Boolean");
	}

	/**
	 * testReturnsFunction
	 */
	public void testReturnsFunction()
	{
		this.lastStatementTypeTests(Path.fromPortableString("inferencing/function-returns-function.js"), "Function");
	}

	/**
	 * testReturnsNumber
	 */
	public void testReturnsNumber()
	{
		this.lastStatementTypeTests(Path.fromPortableString("inferencing/function-returns-number.js"), "Number");
	}

	/**
	 * testReturnsRegExp
	 */
	public void testReturnsRegExp()
	{
		this.lastStatementTypeTests(Path.fromPortableString("inferencing/function-returns-regexp.js"), "RegExp");
	}

	/**
	 * testReturnsString
	 */
	public void testReturnsString()
	{
		this.lastStatementTypeTests(Path.fromPortableString("inferencing/function-returns-string.js"), "String");
	}

	/**
	 * testReturnsArray
	 */
	public void testReturnsArray()
	{
		this.lastStatementTypeTests(Path.fromPortableString("inferencing/function-returns-array.js"), "Array");
	}

	/**
	 * testReturnsArrayOfNumbers
	 */
	public void testReturnsArrayOfNumbers()
	{
		this.lastStatementTypeTests(Path.fromPortableString("inferencing/function-returns-array-of-numbers.js"), "Array<Number>");
	}

	/**
	 * testReturnsObject
	 */
	public void testReturnsObject()
	{
		this.lastStatementTypeTests(Path.fromPortableString("inferencing/function-returns-object.js"), "Object");
	}

	/**
	 * testReturnsUserObject
	 */
	public void testReturnsUserObject()
	{
		List<String> types = this.getLastStatementTypes(Path.fromPortableString("inferencing/function-returns-user-object.js"));

		assertNotNull(types);
		assertEquals(1, types.size());

		String type = types.get(0);
		assertTrue(type + " is not a user type", type.startsWith(JSTypeConstants.DYNAMIC_CLASS_PREFIX));

		this.lastStatementTypeTests(Path.fromPortableString("inferencing/function-returns-user-object.js"), JSTypeConstants.DYNAMIC_CLASS_PREFIX);
	}
}
