package com.aptana.editor.js.contentassist;

import java.text.MessageFormat;

import org.eclipse.jface.text.BadLocationException;

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
	 * testEmptyArgs
	 * 
	 * @throws BadLocationException 
	 */
	public void testEmptyArgs()
	{
		this.testLocations(
			"locations/global_in_empty_args.js",
			new LocationTypeRange(LocationType.IN_GLOBAL, 0),
			new LocationTypeRange(LocationType.NONE, 1, 15),
			new LocationTypeRange(LocationType.IN_GLOBAL, 16, 20),
			new LocationTypeRange(LocationType.IN_VARIABLE_NAME, 21, 23),
			new LocationTypeRange(LocationType.IN_GLOBAL, 24, 25)
		);
	}
	
	/**
	 * testInArg
	 */
	public void testInArgs()
	{
		this.testLocations(
			"locations/global_in_arg.js",
			new LocationTypeRange(LocationType.IN_GLOBAL, 0),
			new LocationTypeRange(LocationType.NONE, 1, 15),
			new LocationTypeRange(LocationType.IN_GLOBAL, 16, 20),
			new LocationTypeRange(LocationType.IN_VARIABLE_NAME, 21, 23),
			new LocationTypeRange(LocationType.IN_GLOBAL, 24),
			new LocationTypeRange(LocationType.NONE, 25, 31),
			new LocationTypeRange(LocationType.IN_GLOBAL, 32, 33)
		);
	}
}
