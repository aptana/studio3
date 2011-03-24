/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.text;

import junit.framework.TestCase;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.jruby.RubyRegexp;

public class RubyRegexpAutoIndentStrategyTest extends TestCase
{

	public void testDoesntEraseRestofLineAfternewlineCharSentAndDedentMatches()
	{
		RubyRegexpAutoIndentStrategy strategy = new AlwaysMatchRubyRegexpAutoIndentStrategy();
		IDocument document = new Document("\n\t</script</head>");
		DocumentCommand command = createTextCommand(10, ">");
		strategy.customizeDocumentCommand(document, command);

		// Looks wrong, but command still runs to insert the >
		assertEquals("\n\t</script</head>", document.get());
		assertTrue(command.doit);
		assertEquals(">", command.text);
	}
	
	public void testDedent()
	{
		RubyRegexpAutoIndentStrategy strategy = new AlwaysMatchRubyRegexpAutoIndentStrategy()
		{
			@Override
			protected String getIndentString()
			{
				return "  ";
			}
		};
		IDocument document = new Document("if a.nil?\n  a=1\n  els");
		DocumentCommand command = createTextCommand(21, "e");
		strategy.customizeDocumentCommand(document, command);

		assertEquals("if a.nil?\n  a=1\nels", document.get());
		assertTrue(command.doit);
		assertEquals("e", command.text);
		assertEquals(19, command.offset);
	}
	
	public void testDedentWontPushPastMatchingIndentLevel()
	{
		RubyRegexpAutoIndentStrategy strategy = new AlwaysMatchRubyRegexpAutoIndentStrategy()
		{
			@Override
			protected String getIndentString()
			{
				return "  ";
			}
			
			@Override
			protected String findCorrectIndentString(IDocument d, int lineNumber, String currentLineIndent)
					throws BadLocationException
			{
				return " ";
			}
		};
		IDocument document = new Document(" if a.nil?\n   a=1\n  els");
		DocumentCommand command = createTextCommand(23, "e");
		strategy.customizeDocumentCommand(document, command);

		assertEquals(" if a.nil?\n   a=1\n  els", document.get());
		assertTrue(command.doit);
		assertEquals("e", command.text);
		assertEquals(23, command.offset);
	}
	
	public void testDoesntDedentWhenMultipleCharsArePasted()
	{
		RubyRegexpAutoIndentStrategy strategy = new AlwaysMatchRubyRegexpAutoIndentStrategy()
		{
			@Override
			protected String getIndentString()
			{
				return "  ";
			}
		};
		IDocument document = new Document("if a.nil?\n  a=1\n  el");
		DocumentCommand command = createTextCommand(20, "se");
		strategy.customizeDocumentCommand(document, command);

		assertEquals("if a.nil?\n  a=1\n  el", document.get());
		assertTrue(command.doit);
		assertEquals("se", command.text);
		assertEquals(20, command.offset);
	}

	public void testIndentAndPushTrailingContentAfterNewlineAndCursorForTagPair()
	{
		RubyRegexpAutoIndentStrategy strategy = new AlwaysMatchRubyRegexpAutoIndentStrategy();
		IDocument document = new Document("\t<div></div>");
		DocumentCommand command = createNewlineCommand(6);
		strategy.customizeDocumentCommand(document, command);
		assertEquals("\n\t\t\n\t", command.text);
		assertTrue(command.doit);
		document = new Document("    <div></div>");
		command = createNewlineCommand(9);
		strategy.customizeDocumentCommand(document, command);
		assertEquals("\n    \t\n    ", command.text);
		assertTrue(command.doit);
	}

	public void testDoesNotIndentAndPushTrailingContentAfterNewlineAndCursorForTagTag()
	{
		RubyRegexpAutoIndentStrategy strategy = new AlwaysMatchRubyRegexpAutoIndentStrategy();
		IDocument document = new Document("\t<div><div>");
		DocumentCommand command = createNewlineCommand(6);
		strategy.customizeDocumentCommand(document, command);
		assertEquals("\n\t\t", command.text);
		assertTrue(command.doit);
		document = new Document("    <div><div>");
		command = createNewlineCommand(9);
		strategy.customizeDocumentCommand(document, command);
		assertEquals("\n    \t", command.text);
		assertTrue(command.doit);
	}

	public void testRR3_129()
	{
		RubyRegexpAutoIndentStrategy strategy = new AlwaysMatchRubyRegexpAutoIndentStrategy()
		{
			private int count = 0;

			@Override
			protected boolean matchesRegexp(RubyRegexp regexp, String lineContent)
			{
				// match the second call, decrease regexp
				return count++ == 1;
			}

			@Override
			protected String findCorrectIndentString(IDocument d, int lineNumber, String currentLineIndent)
					throws BadLocationException
			{
				return "\t\t";
			}
		};
		IDocument document = new Document(
				"\tvar advance = function()\n\t\t{\n\t\t\treturn word = that.getWord();\n\t\t};");
		DocumentCommand command = createNewlineCommand(66);
		strategy.customizeDocumentCommand(document, command);
		assertEquals("\n\t\t", command.text);
		assertTrue(command.doit);
	}
	
	// https://aptana.lighthouseapp.com/projects/35272/tickets/1218
	public void testStudio3_1218()
	{
		RubyRegexpAutoIndentStrategy strategy = new AlwaysMatchRubyRegexpAutoIndentStrategy()
		{
			@Override
			protected boolean matchesRegexp(RubyRegexp regexp, String lineContent)
			{
				return false;
			}
		};
		IDocument document = new Document("/**\n * \n **/function name() {\n}\n");
		
		// After end of block comment, don't add a star
		DocumentCommand command = createNewlineCommand(12);
		strategy.customizeDocumentCommand(document, command);
		assertEquals("\n ", command.text);
		assertTrue(command.doit);
		
		// Inside block comment, add star
		command = createNewlineCommand(6);
		strategy.customizeDocumentCommand(document, command);
		assertEquals("\n * ", command.text);
		assertTrue(command.doit);
		
		// Newline inside end of block comment
		command = createNewlineCommand(10);
		strategy.customizeDocumentCommand(document, command);
		assertEquals("\n * ", command.text);
		assertTrue(command.doit);
		
		command = createNewlineCommand(11);
		strategy.customizeDocumentCommand(document, command);
		assertEquals("\n *", command.text);
		assertTrue(command.doit);
	}

	protected DocumentCommand createNewlineCommand(int offset)
	{
		return createTextCommand(offset, "\n");
	}

	protected DocumentCommand createTextCommand(int offset, String text)
	{
		DocumentCommand command = new DocumentCommand()
		{
		};
		command.text = text;
		command.offset = offset;
		command.caretOffset = offset;
		command.doit = true;
		return command;
	}

	private static class AlwaysMatchRubyRegexpAutoIndentStrategy extends RubyRegexpAutoIndentStrategy
	{
		AlwaysMatchRubyRegexpAutoIndentStrategy()
		{
			super("", null, null, null);
		}

		@Override
		protected String findCorrectIndentString(IDocument d, int lineNumber, String currentLineIndent)
				throws BadLocationException
		{
			return "";
		}

		@Override
		protected RubyRegexp getIncreaseIndentRegexp(String scope)
		{
			return null;
		}

		@Override
		protected RubyRegexp getDecreaseIndentRegexp(String scope)
		{
			return null;
		}

		@Override
		protected String getScopeAtOffset(IDocument d, int offset) throws BadLocationException
		{
			return "";
		}

		@Override
		protected boolean matchesRegexp(RubyRegexp regexp, String lineContent)
		{
			return true;
		}
	}

}
