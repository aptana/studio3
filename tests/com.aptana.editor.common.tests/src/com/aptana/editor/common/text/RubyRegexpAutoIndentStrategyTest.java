/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
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
			super("", null, null);
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
