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
			new LocationTypeRange(LocationType.IN_GLOBAL, 4, 6),
			new LocationTypeRange(LocationType.NONE, 7, 8),
			new LocationTypeRange(LocationType.IN_GLOBAL, 9, 10)
		);
	}
	
	/**
	 * testSimpleBinaryOperator1
	 */
	public void testSimpleBinaryOperator1()
	{
		this.testLocations(
			"locations/simpleBinaryOperator1.js",
			new LocationTypeRange(LocationType.IN_GLOBAL, 0),
			new LocationTypeRange(LocationType.IN_VARIABLE_NAME, 1, 3),
			new LocationTypeRange(LocationType.NONE, 4),
			new LocationTypeRange(LocationType.IN_GLOBAL, 5),
			new LocationTypeRange(LocationType.NONE, 6, 7),
			new LocationTypeRange(LocationType.IN_GLOBAL, 8)
		);
	}
	
	/**
	 * testSimpleBinaryOperator2
	 */
	public void testSimpleBinaryOperator2()
	{
		this.testLocations(
			"locations/simpleBinaryOperator2.js",
			new LocationTypeRange(LocationType.IN_GLOBAL, 0),
			new LocationTypeRange(LocationType.IN_VARIABLE_NAME, 1, 3),
			new LocationTypeRange(LocationType.IN_GLOBAL, 4),
			new LocationTypeRange(LocationType.NONE, 5),
			new LocationTypeRange(LocationType.IN_GLOBAL, 6, 7),
			new LocationTypeRange(LocationType.NONE, 8, 9),
			new LocationTypeRange(LocationType.IN_GLOBAL, 10, 11)
		);
	}
	
	/**
	 * testGetSimpleProperty1
	 */
	public void testGetSimpleProperty1()
	{
		this.testLocations(
			"locations/simpleGetProperty1.js",
			new LocationTypeRange(LocationType.IN_GLOBAL, 0),
			new LocationTypeRange(LocationType.IN_VARIABLE_NAME, 1, 3),
			new LocationTypeRange(LocationType.IN_PROPERTY_NAME, 4, 7),
			new LocationTypeRange(LocationType.NONE, 8),
			new LocationTypeRange(LocationType.IN_GLOBAL, 9),
			new LocationTypeRange(LocationType.NONE, 10, 11),
			new LocationTypeRange(LocationType.IN_GLOBAL, 12)
		);
	}
	
	/**
	 * testSimpleGetProperty2
	 */
	public void testSimpleGetProperty2()
	{
		this.testLocations(
			"locations/simpleGetProperty2.js",
			new LocationTypeRange(LocationType.IN_GLOBAL, 0),
			new LocationTypeRange(LocationType.IN_VARIABLE_NAME, 1, 3),
			new LocationTypeRange(LocationType.IN_GLOBAL, 4),
			new LocationTypeRange(LocationType.IN_PROPERTY_NAME, 5, 9),
			new LocationTypeRange(LocationType.IN_GLOBAL, 10),
			new LocationTypeRange(LocationType.NONE, 11),
			new LocationTypeRange(LocationType.IN_GLOBAL, 12, 13),
			new LocationTypeRange(LocationType.NONE, 14, 15),
			new LocationTypeRange(LocationType.IN_GLOBAL, 16, 17)
		);
	}
	
	/**
	 * testSimpleGetElement1
	 */
	public void testSimpleGetElement1()
	{
		this.testLocations(
			"locations/simpleGetElement1.js",
			new LocationTypeRange(LocationType.IN_GLOBAL, 0),
			new LocationTypeRange(LocationType.IN_VARIABLE_NAME, 1, 3),
			new LocationTypeRange(LocationType.IN_GLOBAL, 4),
			new LocationTypeRange(LocationType.NONE, 5, 6),				// should 7 be NONE?
			new LocationTypeRange(LocationType.IN_GLOBAL, 7, 8),
			new LocationTypeRange(LocationType.NONE, 9, 10),
			new LocationTypeRange(LocationType.IN_GLOBAL, 11)
		);
	}
	
	/**
	 * testSimpleGetElement2
	 */
	public void testSimpleGetElement2()
	{
		this.testLocations(
			"locations/simpleGetElement2.js",
			new LocationTypeRange(LocationType.IN_GLOBAL, 0),
			new LocationTypeRange(LocationType.IN_VARIABLE_NAME, 1, 3),
			new LocationTypeRange(LocationType.IN_GLOBAL, 4, 6),
			new LocationTypeRange(LocationType.NONE, 7, 10),
			new LocationTypeRange(LocationType.IN_GLOBAL, 11, 13),
			new LocationTypeRange(LocationType.NONE, 14, 15),
			new LocationTypeRange(LocationType.IN_GLOBAL, 16, 17)
		);
	}
	
	/**
	 * testTryCatchFinally
	 */
	public void testTryCatchFinally()
	{
		this.testLocations(
			"locations/tryCatchFinally.js"
		);
	}
	
	/**
	 * testConditional
	 */
	public void testConditional()
	{
		this.testLocations(
			"locations/conditional.js"
		);
	}
	
	/**
	 * testNew
	 */
	public void testNew()
	{
		this.testLocations(
			"locations/new.js"
		);
	}
	
	/**
	 * testVar1
	 */
	public void testVar1()
	{
		this.testLocations(
			"locations/var1.js"
		);
	}
	
	/**
	 * testVar2
	 */
	public void testVar2()
	{
		this.testLocations(
			"locations/var2.js"
		);
	}
	
	/**
	 * testVar3
	 */
	public void testVar3()
	{
		this.testLocations(
			"locations/var3.js"
		);
	}
	
	/**
	 * testDo
	 */
	public void testDo()
	{
		this.testLocations(
			"locations/do.js"
		);
	}
	
	/**
	 * testForIn
	 */
	public void testForIn()
	{
		this.testLocations(
			"locations/forIn.js"
		);
	}
	
	/**
	 * testFor
	 */
	public void testFor()
	{
		this.testLocations(
			"locations/for.js"
		);
	}
	
	/**
	 * testIf
	 */
	public void testIf()
	{
		this.testLocations(
			"locations/if.js"
		);
	}
	
	/**
	 * testLabelledFor
	 */
	public void testLabelledFor()
	{
		this.testLocations(
			"locations/labelledFor.js"
		);
	}
	
	/**
	 * testArrayLiteral
	 */
	public void testArrayLiteral()
	{
		this.testLocations(
			"locations/arrayLiteral.js"
		);
	}
	
	/**
	 * testObjectLiteral
	 */
	public void testObjectLiteral()
	{
		this.testLocations(
			"locations/objectLiteral.js"
		);
	}
	
	/**
	 * testSwitch
	 */
	public void testSwitch()
	{
		this.testLocations(
			"locations/switch.js"
		);
	}
	
	/**
	 * testGroup
	 */
	public void testGroup()
	{
		this.testLocations(
			"locations/group.js"
		);
	}
	
	/**
	 * testFunctionWithReturn
	 */
	public void testFunctionWithReturn()
	{
		this.testLocations(
			"locations/functionWithReturn.js"
		);
	}
	
	/**
	 * testFunctionWithThrow
	 */
	public void testFunctionWithThrow()
	{
		this.testLocations(
			"locations/functionWithThrow.js"
		);
	}
	
	/**
	 * testWhile
	 */
	public void testWhile()
	{
		this.testLocations(
			"locations/while.js"
		);
	}
	
	/**
	 * testWith
	 */
	public void testWith()
	{
		this.testLocations(
			"locations/with.js"
		);
	}
}
