/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist;

import static org.junit.Assert.assertEquals;

import java.text.MessageFormat;

import org.junit.Test;

import com.aptana.editor.js.tests.JSEditorBasedTestCase;

public class LocationTest extends JSEditorBasedTestCase
{
	private static class LocationTypeRange
	{
		public final LocationType location;
		public final int startingOffset;
		public final int endingOffset;

		public LocationTypeRange(LocationType location, int offset)
		{
			this.location = location;
			this.startingOffset = this.endingOffset = offset;
		}

		public LocationTypeRange(LocationType location, int startingOffset, int endingOffset)
		{
			this.location = location;
			this.startingOffset = startingOffset;
			this.endingOffset = endingOffset;
		}
	}

	/**
	 * testLocations
	 * 
	 * @param resource
	 * @param ranges
	 */
	protected void testLocations(String resource, LocationTypeRange... ranges)
	{
		// setup everything for the test
		this.setupTestContext(resource);

		for (LocationTypeRange range : ranges)
		{
			for (int offset = range.startingOffset; offset <= range.endingOffset; offset++)
			{
				LocationType location = this.processor.getLocationType(this.document, offset);
				// @formatter:off
				String message = MessageFormat.format(
					"Expected {0} at location {1} of ''{2}'': character = ''{3}''",
					range.location.toString(),
					Integer.toString(offset),
					this.source,
					(offset < this.source.length()) ? this.source.charAt(offset) : '\0'
				);
				// @formatter:on
				assertEquals(message, range.location, location);
			}
		}
	}

	// @formatter:off
	
	/**
	 * testInvokeWithoutParams
	 */
	@Test public void testInvokeWithoutParams()
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
	@Test public void testInvokeWithIncompleteParams()
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
	@Test public void testSimpleAssignment1()
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
	@Test public void testSimpleAssignment2()
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
	@Test public void testSimpleBinaryOperator1()
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
	@Test public void testSimpleBinaryOperator2()
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
	@Test public void testGetSimpleProperty1()
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
	@Test public void testSimpleGetProperty2()
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
	@Test public void testSimpleGetElement1()
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
	@Test public void testSimpleGetElement2()
	{
		this.testLocations(
			"locations/simpleGetElement2.js",
			new LocationTypeRange(LocationType.IN_GLOBAL, 0),
			new LocationTypeRange(LocationType.IN_VARIABLE_NAME, 1, 3),
			new LocationTypeRange(LocationType.IN_GLOBAL, 4, 6),
			new LocationTypeRange(LocationType.NONE, 7, 8),
			new LocationTypeRange(LocationType.IN_GLOBAL, 9, 13),
			new LocationTypeRange(LocationType.NONE, 14, 15),
			new LocationTypeRange(LocationType.IN_GLOBAL, 16, 17)
		);
	}
	
	/**
	 * testTryCatchFinally
	 */
	@Test public void testTryCatchFinally()
	{
		this.testLocations(
			"locations/tryCatchFinally.js",
			new LocationTypeRange(LocationType.IN_GLOBAL, 0),
			new LocationTypeRange(LocationType.NONE, 1, 4),
			new LocationTypeRange(LocationType.IN_GLOBAL, 5, 7),
			new LocationTypeRange(LocationType.NONE, 8, 19),
			new LocationTypeRange(LocationType.IN_GLOBAL, 20, 22),
			new LocationTypeRange(LocationType.NONE, 23, 32),
			new LocationTypeRange(LocationType.IN_GLOBAL, 33, 36)
		);
	}
	
	/**
	 * testConditional
	 */
	@Test public void testConditional()
	{
		this.testLocations(
			"locations/conditional.js",
			new LocationTypeRange(LocationType.IN_GLOBAL, 0),
			new LocationTypeRange(LocationType.IN_VARIABLE_NAME, 1),
			new LocationTypeRange(LocationType.IN_GLOBAL, 2, 5),
			new LocationTypeRange(LocationType.IN_VARIABLE_NAME, 6),
			new LocationTypeRange(LocationType.IN_GLOBAL, 7),
			new LocationTypeRange(LocationType.NONE, 8),
			new LocationTypeRange(LocationType.IN_GLOBAL, 9, 10),
			new LocationTypeRange(LocationType.NONE, 11, 13),
			new LocationTypeRange(LocationType.IN_GLOBAL, 14, 15),
			new LocationTypeRange(LocationType.NONE, 16, 17),
			new LocationTypeRange(LocationType.IN_GLOBAL, 18, 20),
			new LocationTypeRange(LocationType.NONE, 21),
			new LocationTypeRange(LocationType.IN_GLOBAL, 22, 23)
		);
	}
	
	/**
	 * testNew
	 */
	@Test public void testNew()
	{
		this.testLocations(
			"locations/new.js",
			new LocationTypeRange(LocationType.IN_GLOBAL, 0),
			new LocationTypeRange(LocationType.NONE, 1, 3),
			new LocationTypeRange(LocationType.IN_GLOBAL, 4),
			new LocationTypeRange(LocationType.IN_VARIABLE_NAME, 5, 7),
			new LocationTypeRange(LocationType.IN_GLOBAL, 8),
			new LocationTypeRange(LocationType.NONE, 9),
			new LocationTypeRange(LocationType.IN_GLOBAL, 10, 11)
		);
	}
	
	/**
	 * testVar1
	 */
	@Test public void testVar1()
	{
		this.testLocations(
			"locations/var1.js",
			new LocationTypeRange(LocationType.IN_GLOBAL, 0),
			new LocationTypeRange(LocationType.NONE, 1, 4),
			new LocationTypeRange(LocationType.IN_VARIABLE_DECLARATION, 5, 7),
			new LocationTypeRange(LocationType.IN_GLOBAL, 8, 9)
		);
	}
	
	/**
	 * testVar2
	 */
	@Test public void testVar2()
	{
		this.testLocations(
			"locations/var2.js",
			new LocationTypeRange(LocationType.IN_GLOBAL, 0),
			new LocationTypeRange(LocationType.NONE, 1, 4),
			new LocationTypeRange(LocationType.IN_VARIABLE_DECLARATION, 5, 7),
			new LocationTypeRange(LocationType.NONE, 8, 9),
			new LocationTypeRange(LocationType.IN_VARIABLE_DECLARATION, 10, 12),
			new LocationTypeRange(LocationType.IN_GLOBAL, 13, 14)
		);
	}
	
	/**
	 * testVar3
	 */
	@Test public void testVar3()
	{
		this.testLocations(
			"locations/var3.js",
			new LocationTypeRange(LocationType.IN_GLOBAL, 0),
			new LocationTypeRange(LocationType.NONE, 1, 4),
			new LocationTypeRange(LocationType.IN_VARIABLE_DECLARATION, 5, 7),
			new LocationTypeRange(LocationType.NONE, 8),
			new LocationTypeRange(LocationType.IN_GLOBAL, 9, 10),
			new LocationTypeRange(LocationType.NONE, 11, 12),
			new LocationTypeRange(LocationType.IN_GLOBAL, 13, 14)
		);
	}
	
	/**
	 * testDo
	 */
	@Test public void testDo()
	{
		this.testLocations(
			"locations/do.js",
			new LocationTypeRange(LocationType.IN_GLOBAL, 0),
			new LocationTypeRange(LocationType.NONE, 1, 3),
			new LocationTypeRange(LocationType.IN_GLOBAL, 4),
			new LocationTypeRange(LocationType.NONE, 5, 12),
			new LocationTypeRange(LocationType.IN_GLOBAL, 13),
			new LocationTypeRange(LocationType.NONE, 14, 18),
			new LocationTypeRange(LocationType.IN_GLOBAL, 19, 20)
		);
	}
	
	/**
	 * testForIn
	 */
	@Test public void testForIn()
	{
		this.testLocations(
			"locations/forIn.js",
			new LocationTypeRange(LocationType.IN_GLOBAL, 0),
			new LocationTypeRange(LocationType.NONE, 1, 9),
			new LocationTypeRange(LocationType.IN_VARIABLE_DECLARATION, 10),
			new LocationTypeRange(LocationType.NONE, 11, 13),
			new LocationTypeRange(LocationType.IN_GLOBAL, 14),
			new LocationTypeRange(LocationType.IN_VARIABLE_NAME, 15, 17),
			new LocationTypeRange(LocationType.NONE, 18, 19),
			new LocationTypeRange(LocationType.IN_GLOBAL, 20, 22)
		);
	}
	
	/**
	 * testFor
	 */
	@Test public void testFor()
	{
		this.testLocations(
			"locations/for.js",
			new LocationTypeRange(LocationType.IN_GLOBAL, 0),
			new LocationTypeRange(LocationType.NONE, 1, 9),
			new LocationTypeRange(LocationType.IN_VARIABLE_DECLARATION, 10),
			new LocationTypeRange(LocationType.NONE, 11),
			new LocationTypeRange(LocationType.IN_GLOBAL, 12, 13),
			new LocationTypeRange(LocationType.NONE, 14),
			new LocationTypeRange(LocationType.IN_GLOBAL, 15, 16),
			new LocationTypeRange(LocationType.IN_VARIABLE_NAME, 17),
			new LocationTypeRange(LocationType.IN_GLOBAL, 18, 20),
			new LocationTypeRange(LocationType.NONE, 21, 22),
			new LocationTypeRange(LocationType.IN_GLOBAL, 23, 24),
			new LocationTypeRange(LocationType.IN_VARIABLE_NAME, 25),
			new LocationTypeRange(LocationType.NONE, 26, 29),
			new LocationTypeRange(LocationType.IN_GLOBAL, 30, 33)
		);
	}
	
	/**
	 * testIf
	 */
	@Test public void testIf()
	{
		this.testLocations(
			"locations/if.js",
			new LocationTypeRange(LocationType.IN_GLOBAL, 0),
			new LocationTypeRange(LocationType.NONE, 1, 3),
			new LocationTypeRange(LocationType.IN_GLOBAL, 4),
			new LocationTypeRange(LocationType.NONE, 5, 10),
			new LocationTypeRange(LocationType.IN_GLOBAL, 11, 12),
			new LocationTypeRange(LocationType.NONE, 13, 19),
			new LocationTypeRange(LocationType.IN_GLOBAL, 20, 23)
		);
	}

	/**
	 * testIfAtEOF
	 */
	@Test public void testIfAtEOF()
	{
		this.testLocations(
			"locations/if-eof.js",
			new LocationTypeRange(LocationType.IN_PROPERTY_NAME, 21)
		);
	}
	
	/**
	 * testLabelledFor
	 */
	@Test public void testLabelledFor()
	{
		this.testLocations(
			"locations/labelledFor.js",
			new LocationTypeRange(LocationType.IN_GLOBAL, 0),
			new LocationTypeRange(LocationType.IN_LABEL, 1, 4),
			new LocationTypeRange(LocationType.NONE, 5, 15),
			new LocationTypeRange(LocationType.IN_VARIABLE_DECLARATION, 16),
			new LocationTypeRange(LocationType.NONE, 17),
			new LocationTypeRange(LocationType.IN_GLOBAL, 18, 19),
			new LocationTypeRange(LocationType.NONE, 20),
			new LocationTypeRange(LocationType.IN_GLOBAL, 21, 22),
			new LocationTypeRange(LocationType.IN_VARIABLE_NAME, 23),
			new LocationTypeRange(LocationType.IN_GLOBAL, 24, 26),
			new LocationTypeRange(LocationType.NONE, 27, 28),
			new LocationTypeRange(LocationType.IN_GLOBAL, 29, 30),
			new LocationTypeRange(LocationType.IN_VARIABLE_NAME, 31),
			new LocationTypeRange(LocationType.NONE, 32, 35),
			new LocationTypeRange(LocationType.IN_GLOBAL, 36, 38),
			new LocationTypeRange(LocationType.NONE, 39, 41),
			new LocationTypeRange(LocationType.IN_GLOBAL, 42),
			new LocationTypeRange(LocationType.NONE, 43, 48),
			new LocationTypeRange(LocationType.IN_GLOBAL, 49, 52),
			new LocationTypeRange(LocationType.NONE, 53, 57),
			new LocationTypeRange(LocationType.IN_LABEL, 58, 62),
			new LocationTypeRange(LocationType.IN_GLOBAL, 63, 65),
			new LocationTypeRange(LocationType.NONE, 66, 72),
			new LocationTypeRange(LocationType.IN_GLOBAL, 73, 76),
			new LocationTypeRange(LocationType.NONE, 77, 84),
			new LocationTypeRange(LocationType.IN_LABEL, 85, 89),
			new LocationTypeRange(LocationType.IN_GLOBAL, 90, 96)
		);
	}
	
	/**
	 * testArrayLiteral
	 */
	@Test public void testArrayLiteral()
	{
		this.testLocations(
			"locations/arrayLiteral.js",
			new LocationTypeRange(LocationType.IN_GLOBAL, 0),
			new LocationTypeRange(LocationType.NONE, 1, 4),
			new LocationTypeRange(LocationType.IN_VARIABLE_DECLARATION, 5, 7),
			new LocationTypeRange(LocationType.NONE, 8),
			new LocationTypeRange(LocationType.IN_GLOBAL, 9, 12),
			new LocationTypeRange(LocationType.NONE, 13, 18),
			new LocationTypeRange(LocationType.IN_GLOBAL, 19, 20),
			new LocationTypeRange(LocationType.NONE, 21, 26),
			new LocationTypeRange(LocationType.IN_GLOBAL, 27, 28),
			new LocationTypeRange(LocationType.NONE, 29, 30),
			new LocationTypeRange(LocationType.IN_GLOBAL, 31, 34)
		);
	}
	
	/**
	 * testObjectLiteral
	 */
	@Test public void testObjectLiteral()
	{
		this.testLocations(
			"locations/objectLiteral.js",
			new LocationTypeRange(LocationType.IN_GLOBAL, 0),
			new LocationTypeRange(LocationType.NONE, 1, 4),
			new LocationTypeRange(LocationType.IN_VARIABLE_DECLARATION, 5, 7),
			new LocationTypeRange(LocationType.NONE, 8),
			new LocationTypeRange(LocationType.IN_GLOBAL, 9, 10),
			new LocationTypeRange(LocationType.IN_OBJECT_LITERAL_PROPERTY, 11, 17),
			new LocationTypeRange(LocationType.IN_GLOBAL, 18, 19),
			new LocationTypeRange(LocationType.NONE, 20, 25),
			new LocationTypeRange(LocationType.IN_OBJECT_LITERAL_PROPERTY, 26, 31),
			new LocationTypeRange(LocationType.IN_GLOBAL, 32, 33),
			new LocationTypeRange(LocationType.NONE, 34, 35),
			new LocationTypeRange(LocationType.IN_OBJECT_LITERAL_PROPERTY, 36),
			new LocationTypeRange(LocationType.NONE, 37),
			new LocationTypeRange(LocationType.IN_GLOBAL, 38, 39)
		);
	}
	
	/**
	 * testSwitch
	 */
	@Test public void testSwitch()
	{
		this.testLocations(
			"locations/switch.js",
			new LocationTypeRange(LocationType.IN_GLOBAL, 0),
			new LocationTypeRange(LocationType.NONE, 1, 7),
			new LocationTypeRange(LocationType.IN_GLOBAL, 8),
			new LocationTypeRange(LocationType.IN_VARIABLE_NAME, 9, 11),
			new LocationTypeRange(LocationType.NONE, 12, 22),	// may want 21 to be IN_GLOBAL
			new LocationTypeRange(LocationType.IN_GLOBAL, 23, 26),
			new LocationTypeRange(LocationType.NONE, 27, 31),
			new LocationTypeRange(LocationType.IN_GLOBAL, 32, 37),
			new LocationTypeRange(LocationType.NONE, 38, 44),
			new LocationTypeRange(LocationType.IN_GLOBAL, 45, 48),
			new LocationTypeRange(LocationType.NONE, 49, 53),
			new LocationTypeRange(LocationType.IN_GLOBAL, 54),
			new LocationTypeRange(LocationType.NONE, 55),
			new LocationTypeRange(LocationType.IN_GLOBAL, 56, 57)
		);
	}
	
	/**
	 * testGroup
	 */
	@Test public void testGroup()
	{
		this.testLocations(
			"locations/group.js",
			new LocationTypeRange(LocationType.IN_GLOBAL, 0, 1),
			new LocationTypeRange(LocationType.NONE, 2, 12),
			new LocationTypeRange(LocationType.IN_GLOBAL, 13, 20)
		);
	}
	
	/**
	 * testFunctionWithReturn
	 */
	@Test public void testFunctionWithReturn()
	{
		this.testLocations(
			"locations/functionWithReturn.js",
			new LocationTypeRange(LocationType.IN_GLOBAL, 0),
			new LocationTypeRange(LocationType.NONE, 1, 15),
			new LocationTypeRange(LocationType.IN_GLOBAL, 16, 18),
			new LocationTypeRange(LocationType.NONE, 19, 24),
			new LocationTypeRange(LocationType.IN_GLOBAL, 25),
			new LocationTypeRange(LocationType.NONE, 26, 29),
			new LocationTypeRange(LocationType.IN_GLOBAL, 30, 33)
		);
	}
	
	/**
	 * testFunctionWithThrow
	 */
	@Test public void testFunctionWithThrow()
	{
		this.testLocations(
			"locations/functionWithThrow.js",
			new LocationTypeRange(LocationType.IN_GLOBAL, 0),
			new LocationTypeRange(LocationType.NONE, 1, 15),
			new LocationTypeRange(LocationType.IN_GLOBAL, 16, 18),
			new LocationTypeRange(LocationType.NONE, 19, 23),
			new LocationTypeRange(LocationType.IN_GLOBAL, 24),
			new LocationTypeRange(LocationType.NONE, 25, 31),
			new LocationTypeRange(LocationType.IN_GLOBAL, 32, 35)
		);
	}
	
	/**
	 * testWhile
	 */
	@Test public void testWhile()
	{
		this.testLocations(
			"locations/while.js",
			new LocationTypeRange(LocationType.IN_GLOBAL, 0),
			new LocationTypeRange(LocationType.NONE, 1, 6),
			new LocationTypeRange(LocationType.IN_GLOBAL, 7),
			new LocationTypeRange(LocationType.NONE, 8, 13),
			new LocationTypeRange(LocationType.IN_GLOBAL, 14, 17)
		);
	}
	
	/**
	 * testWith
	 */
	@Test public void testWith()
	{
		this.testLocations(
			"locations/with.js",
			new LocationTypeRange(LocationType.IN_GLOBAL, 0),
			new LocationTypeRange(LocationType.NONE, 1, 5),
			new LocationTypeRange(LocationType.IN_GLOBAL, 6),
			new LocationTypeRange(LocationType.IN_VARIABLE_NAME, 7, 9),
			new LocationTypeRange(LocationType.NONE, 10, 11),
			new LocationTypeRange(LocationType.IN_GLOBAL, 12, 14)
		);
	}

	/**
	 * testErrorInObjectLiteral
	 */
	@Test public void testErrorInObjectLiteral()
	{
		this.testLocations(
			"locations/errorInObjectLiteral.js",
			new LocationTypeRange(LocationType.NONE, 6)
		);
	}
	
	@Test public void testThis()
	{
		this.testLocations(
			"locations/this.js",
			new LocationTypeRange(LocationType.IN_THIS, 5)
		);
	}
}
