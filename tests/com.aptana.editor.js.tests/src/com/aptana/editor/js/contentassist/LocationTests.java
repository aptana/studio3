package com.aptana.editor.js.contentassist;

import org.eclipse.jface.text.BadLocationException;

import com.aptana.editor.common.contentassist.LexemeProvider;
import com.aptana.editor.js.contentassist.JSContentAssistProcessor.Location;
import com.aptana.editor.js.parsing.lexer.JSTokenType;

public class LocationTests extends EditorBasedTests
{
	/**
	 * testEmptyArgs
	 * 
	 * @throws BadLocationException 
	 */
	public void testEmptyArgs()
	{
		// setup everything for the test
		TestContext context = this.getTestContext("locations/global_in_empty_args.js");
		
		// find the offset to test within the source
		int offset = context.source.lastIndexOf(")");
		assertTrue(offset != -1);
		
		// lex around the offset
		LexemeProvider<JSTokenType> lexemeProvider = context.processor.createLexemeProvider(context.document, offset);

		Location location = context.processor.getLocation(lexemeProvider, offset);
		assertEquals(Location.IN_GLOBAL, location);
	}
	
	/**
	 * testInArg
	 */
	public void testInArgs()
	{
		// setup everything for the test
		TestContext context = this.getTestContext("locations/global_in_arg.js");
		
		// find the offset to test within the source
		int offset = context.source.lastIndexOf(",");
		assertTrue(offset != -1);
		offset++;
		
		// lex around the offset
		LexemeProvider<JSTokenType> lexemeProvider = context.processor.createLexemeProvider(context.document, offset);

		Location location = context.processor.getLocation(lexemeProvider, offset);
		assertEquals(Location.IN_GLOBAL, location);
	}
}
