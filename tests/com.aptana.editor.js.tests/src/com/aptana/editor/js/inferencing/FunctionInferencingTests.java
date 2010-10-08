package com.aptana.editor.js.inferencing;

import java.util.List;

import com.aptana.editor.js.JSTypeConstants;

public class FunctionInferencingTests extends InferencingTestsBase
{
	/**
	 * testReturnsBoolean
	 */
	public void testReturnsBoolean()
	{
		String source = this.getContent("inferencing/function-returns-boolean.js");

		this.lastStatementTypeTests(source, "Boolean");
	}

	/**
	 * testReturnsFunction
	 */
	public void testReturnsFunction()
	{
		String source = this.getContent("inferencing/function-returns-function.js");

		this.lastStatementTypeTests(source, "Function");
	}

	/**
	 * testReturnsNumber
	 */
	public void testReturnsNumber()
	{
		String source = this.getContent("inferencing/function-returns-number.js");

		this.lastStatementTypeTests(source, "Number");
	}

	/**
	 * testReturnsRegExp
	 */
	public void testReturnsRegExp()
	{
		String source = this.getContent("inferencing/function-returns-regexp.js");

		this.lastStatementTypeTests(source, "RegExp");
	}

	/**
	 * testReturnsString
	 */
	public void testReturnsString()
	{
		String source = this.getContent("inferencing/function-returns-string.js");

		this.lastStatementTypeTests(source, "String");
	}

	/**
	 * testReturnsArray
	 */
	public void testReturnsArray()
	{
		String source = this.getContent("inferencing/function-returns-array.js");

		this.lastStatementTypeTests(source, "Array");
	}

	/**
	 * testReturnsArrayOfNumbers
	 */
	public void testReturnsArrayOfNumbers()
	{
		String source = this.getContent("inferencing/function-returns-array-of-numbers.js");

		this.lastStatementTypeTests(source, "Array<Number>");
	}

	/**
	 * testReturnsObject
	 */
	public void testReturnsObject()
	{
		String source = this.getContent("inferencing/function-returns-object.js");

		this.lastStatementTypeTests(source, "Object");
	}

	/**
	 * testReturnsUserObject
	 */
	public void testReturnsUserObject()
	{
		String source = this.getContent("inferencing/function-returns-user-object.js");
		List<String> types = this.getLastStatementTypes(source);

		assertNotNull(types);
		assertEquals(1, types.size());

		String type = types.get(0);
		assertTrue(type + " is not a user type", type.startsWith(JSTypeConstants.DYNAMIC_CLASS_PREFIX));

		this.lastStatementTypeTests(source, JSTypeConstants.DYNAMIC_CLASS_PREFIX);
	}
}
