package com.aptana.editor.js.contentassist;

import java.text.MessageFormat;

import com.aptana.editor.common.contentassist.LexemeProvider;
import com.aptana.editor.js.parsing.lexer.JSTokenType;
import com.aptana.editor.js.tests.EditorBasedTests;

public class LocationTests extends EditorBasedTests
{
	/**
	 * testLocations
	 * 
	 * @param resource
	 * @param ranges
	 */
	protected void testLocations(String resource, LocationTypeRange... ranges)
	{
		// setup everything for the test
		TestContext context = this.getTestContext(resource);
		
		for (LocationTypeRange range : ranges)
		{
			for (int offset = range.startingOffset; offset <= range.endingOffset; offset++)
			{
				LexemeProvider<JSTokenType> lexemeProvider = context.processor.createLexemeProvider(context.document, offset);
				LocationType location = context.processor.getLocation(lexemeProvider, offset);
				String message = MessageFormat.format(
					"Expected {0} at location {1} of ''{2}'': character = ''{3}''",
					range.location.toString(),
					Integer.toString(offset),
					context.source,
					(offset < context.source.length()) ? context.source.charAt(offset) : '\0'
				);
				assertEquals(message, range.location, location);
			}
		}
	}
	
	public void testForIn()
	{
		this.testLocations(
			"ranges/forInStatement.js"
		);
	}
	
	public void testForStatement()
	{
		this.testLocations(
			"ranges/forStatement.js"
		);
	}
	
	/**
	 * testInvokeWithoutParams
	 */
	public void testInvokeWithoutParams()
	{
		this.testLocations(
			"locations/functionAndInvokeWithoutParams.js",
			new LocationTypeRange(LocationType.IN_GLOBAL, 0),
			new LocationTypeRange(LocationType.NONE, 1, 15),
			new LocationTypeRange(LocationType.IN_GLOBAL, 16, 20),
			new LocationTypeRange(LocationType.IN_VARIABLE_NAME, 21, 23),
			new LocationTypeRange(LocationType.IN_GLOBAL, 24, 25)
		);
	}
	
	/**
	 * testInvokeWithIncompleteParams
	 */
	public void testInvokeWithIncompleteParams()
	{
		this.testLocations(
			"locations/functionAndInvokeWithIncompleteParams.js",
			new LocationTypeRange(LocationType.IN_GLOBAL, 0),
			new LocationTypeRange(LocationType.NONE, 1, 15),
			new LocationTypeRange(LocationType.IN_GLOBAL, 16, 20),
			new LocationTypeRange(LocationType.IN_VARIABLE_NAME, 21, 23),
			new LocationTypeRange(LocationType.IN_GLOBAL, 24),
			new LocationTypeRange(LocationType.NONE, 25, 31),
			new LocationTypeRange(LocationType.IN_GLOBAL, 32, 33)
		);
	}
	
	/**
	 * testFunctionWithArgs
	 */
	public void testFunctionWithArgs()
	{
		this.testLocations(
			"ranges/functionWithArgs.js"
		);
	}
	
	/**
	 * testFunctionWithoutArgs
	 */
	public void testFunctionWithoutArgs()
	{
		this.testLocations(
			"ranges/functionWithoutArgs.js"
		);
	}
	
	/**
	 * testIfStatement
	 */
	public void testIfStatement()
	{
		this.testLocations(
			"ranges/functionWithoutArgs.js"
		);
	}
	
	/**
	 * testInvokeWithoutParams2
	 */
	public void testInvokeWithoutParams2()
	{
		this.testLocations(
			"ranges/invokeWithoutParams.js"
		);
	}
	
	/**
	 * testInvokeWithParams
	 */
	public void testInvokeWithParams()
	{
		this.testLocations(
			"ranges/invokeWithParams.js"
		);
	}
	
	/**
	 * testSimpleAssignment1
	 */
	public void testSimpleAssignment1()
	{
		this.testLocations(
			"locations/simpleAssignment1.js",
			new LocationTypeRange(LocationType.IN_GLOBAL, 0),
			new LocationTypeRange(LocationType.IN_VARIABLE_NAME, 1, 3),
			new LocationTypeRange(LocationType.IN_GLOBAL, 4),
			new LocationTypeRange(LocationType.NONE, 5, 6),
			new LocationTypeRange(LocationType.IN_GLOBAL, 7)
		);
	}
	
	/**
	 * testSimpleAssignment2
	 */
	public void testSimpleAssignment2()
	{
		this.testLocations(
			"locations/simpleAssignment2.js",
			new LocationTypeRange(LocationType.IN_GLOBAL, 0),
			new LocationTypeRange(LocationType.IN_VARIABLE_NAME, 1, 3),
			new LocationTypeRange(LocationType.IN_GLOBAL, 4),
			new LocationTypeRange(LocationType.NONE, 5, 6),
			new LocationTypeRange(LocationType.IN_GLOBAL, 7, 8)
		);
	}
	
	/**
	 * testSimpleAssignment3
	 */
	public void testSimpleAssignment3()
	{
		this.testLocations(
			"locations/simpleAssignment3.js",
			new LocationTypeRange(LocationType.IN_GLOBAL, 0),
			new LocationTypeRange(LocationType.IN_VARIABLE_NAME, 1, 3),
			new LocationTypeRange(LocationType.IN_GLOBAL, 4, 5),
			new LocationTypeRange(LocationType.NONE, 6, 7),
			new LocationTypeRange(LocationType.IN_GLOBAL, 8)
		);
	}
	
	/**
	 * testSimpleAssignment4
	 */
	public void testSimpleAssignment4()
	{
		this.testLocations(
			"locations/simpleAssignment4.js",
			new LocationTypeRange(LocationType.IN_GLOBAL, 0),
			new LocationTypeRange(LocationType.IN_VARIABLE_NAME, 1, 3),
			new LocationTypeRange(LocationType.IN_GLOBAL, 4, 5),
			new LocationTypeRange(LocationType.NONE, 6, 7),
			new LocationTypeRange(LocationType.IN_GLOBAL, 8, 9)
		);
	}
	
	/**
	 * testSimpleAssignment5
	 */
	public void testSimpleAssignment5()
	{
		this.testLocations(
			"locations/simpleAssignment5.js",
			new LocationTypeRange(LocationType.IN_GLOBAL, 0),
			new LocationTypeRange(LocationType.IN_VARIABLE_NAME, 1, 3),
			new LocationTypeRange(LocationType.NONE, 4),
			new LocationTypeRange(LocationType.IN_GLOBAL, 5, 6),
			new LocationTypeRange(LocationType.NONE, 7, 8),
			new LocationTypeRange(LocationType.IN_GLOBAL, 9)
		);
	}
	
	/**
	 * testSimpleAssignment6
	 */
	public void testSimpleAssignment6()
	{
		this.testLocations(
			"locations/simpleAssignment6.js",
			new LocationTypeRange(LocationType.IN_GLOBAL, 0),
			new LocationTypeRange(LocationType.IN_VARIABLE_NAME, 1, 3),
			new LocationTypeRange(LocationType.NONE, 4),
			new LocationTypeRange(LocationType.IN_GLOBAL, 5, 6),
			new LocationTypeRange(LocationType.NONE, 7, 8),
			new LocationTypeRange(LocationType.IN_GLOBAL, 9, 10)
		);
	}
	
	/**
	 * testWhileStatement
	 */
	public void testWhileStatement()
	{
		this.testLocations(
			"ranges/whileStatement.js"
		);
	}
}
