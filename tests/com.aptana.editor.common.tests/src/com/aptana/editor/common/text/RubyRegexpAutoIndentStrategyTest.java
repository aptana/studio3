/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.text;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.jruby.Ruby;
import org.jruby.RubyRegexp;
import org.jruby.util.ByteList;
import org.jruby.util.KCode;
import org.jruby.util.RegexpOptions;
import org.junit.Test;

public class RubyRegexpAutoIndentStrategyTest
{

	@Test
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

	@Test
	public void testDedent() throws Exception
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

		if (command.doit)
		{
			document.replace(command.offset, command.length, command.text);
		}
		assertEquals("if a.nil?\n  a=1\nelse", document.get());
	}

	@Test
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

	@Test
	public void testDoesntDedentWhenMultipleCharsArePasted() throws Exception
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

		if (command.doit)
		{
			document.replace(command.offset, command.length, command.text);
		}
		assertEquals("if a.nil?\n  a=1\n  else", document.get());
	}

	@Test
	public void testIndentAndPushTrailingContentAfterNewlineAndCursorForTagPair() throws Exception
	{
		RubyRegexpAutoIndentStrategy strategy = new AlwaysMatchRubyRegexpAutoIndentStrategy()
		{
			@Override
			protected String getIndentString()
			{
				return "\t";
			}
		};
		IDocument document = new Document("\t<div></div>");
		DocumentCommand command = createNewlineCommand(6);
		strategy.customizeDocumentCommand(document, command);
		assertEquals("\n\t\t\n\t", command.text);
		assertTrue(command.doit);
		if (command.doit)
		{
			document.replace(command.offset, command.length, command.text);
		}
		assertEquals("\t<div>\n\t\t\n\t</div>", document.get());

		document = new Document("    <div></div>");
		command = createNewlineCommand(9);
		strategy.customizeDocumentCommand(document, command);
		assertEquals("\n    \t\n    ", command.text);
		assertTrue(command.doit);
		if (command.doit)
		{
			document.replace(command.offset, command.length, command.text);
		}
		assertEquals("    <div>\n    \t\n    </div>", document.get());
	}

	@Test
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

	/**
	 *
	 */
	@Test
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

	/**
	 * APSTUD-1218
	 */
	@Test
	public void testDontAddStarAfterBlockComment() throws Exception
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
		assertTrue(command.doit);
		if (command.doit)
		{
			document.replace(command.offset, command.length, command.text);
		}
		assertEquals("/**\n * \n **/\nfunction name() {\n}\n", document.get());
	}

	/**
	 * APSTUD-1218
	 */
	@Test
	public void testInsideBlockCommentAddStar()
	{
		RubyRegexpAutoIndentStrategy strategy = new AlwaysMatchRubyRegexpAutoIndentStrategy()
		{
			@Override
			protected boolean matchesRegexp(RubyRegexp regexp, String lineContent)
			{
				return false;
			}

			@Override
			protected boolean isComment(int offset)
			{
				return true;
			}
		};
		IDocument document = new Document("/**\n * \n **/function name() {\n}\n");

		// Inside block comment, add star
		DocumentCommand command = createNewlineCommand(6);
		strategy.customizeDocumentCommand(document, command);
		assertEquals("\n * ", command.text);
		assertTrue(command.doit);
	}

	/**
	 * APSTUD-3326
	 */
	@Test
	public void testInsideBlockCommentAddStarSdoc()
	{
		// JavaScript and PHP have more than one multi-line comment type. Make sure we're checking
		// for those types too
		RubyRegexpAutoIndentStrategy strategy = new AlwaysMatchRubyRegexpAutoIndentStrategy()
		{
			@Override
			protected boolean matchesRegexp(RubyRegexp regexp, String lineContent)
			{
				return false;
			}

			@Override
			protected boolean isComment(int offset)
			{
				return true;
			}
		};
		IDocument document = new Document("/**\n * \n **/function name() {\n}\n");

		// Inside block comment, add star
		DocumentCommand command = createNewlineCommand(6);
		strategy.customizeDocumentCommand(document, command);
		assertEquals("\n * ", command.text);
		assertTrue(command.doit);
	}

	/**
	 * APSTUD-1218
	 */
	@Test
	public void testAddNewlineInsideEndBlockComment()
	{
		RubyRegexpAutoIndentStrategy strategy = new AlwaysMatchRubyRegexpAutoIndentStrategy()
		{
			@Override
			protected boolean matchesRegexp(RubyRegexp regexp, String lineContent)
			{
				return false;
			}

			@Override
			protected boolean isComment(int offset)
			{
				return true;
			}
		};
		IDocument document = new Document("/**\n * \n **/function name() {\n}\n");

		// Newline inside end of block comment
		DocumentCommand command = createNewlineCommand(10);
		strategy.customizeDocumentCommand(document, command);
		assertEquals("\n * ", command.text);
		assertTrue(command.doit);
	}

	/**
	 * APSTUD-1218
	 */
	@Test
	public void testNewlineInsideEndBlockComment()
	{
		RubyRegexpAutoIndentStrategy strategy = new AlwaysMatchRubyRegexpAutoIndentStrategy()
		{
			@Override
			protected boolean matchesRegexp(RubyRegexp regexp, String lineContent)
			{
				return false;
			}

			@Override
			protected boolean isComment(int offset)
			{
				return true;
			}
		};
		IDocument document = new Document("/**\n * \n **/function name() {\n}\n");

		// Newline inside end of block comment
		DocumentCommand command = createNewlineCommand(11);
		strategy.customizeDocumentCommand(document, command);
		assertEquals("\n *", command.text);
		assertTrue(command.doit);
	}

	/**
	 * APSTUD-2867
	 *
	 * @throws Exception
	 */
	@Test
	public void testNewlineAfterEndBlockComment() throws Exception
	{
		RubyRegexpAutoIndentStrategy strategy = new CSSRubleRubyRegexpAutoIndentStrategy();
		IDocument document = new Document("/* comment */hello");

		// After end of block comment, don't add a star
		DocumentCommand command = createNewlineCommand(13);
		strategy.customizeDocumentCommand(document, command);
		assertTrue(command.doit);

		if (command.doit)
		{
			document.replace(command.offset, command.length, command.text);
		}
		assertEquals("/* comment */\nhello", document.get());
	}

	/**
	 * APSTUD-2867
	 *
	 * @throws Exception
	 */
	@Test
	public void testNewlineAfterEndBlockCommentText() throws Exception
	{
		RubyRegexpAutoIndentStrategy strategy = new CSSRubleRubyRegexpAutoIndentStrategy();
		IDocument document = new Document("/* comment */hello");

		// After end of block comment, don't add a star
		DocumentCommand command = createNewlineCommand(18); // at end of input
		strategy.customizeDocumentCommand(document, command);
		assertTrue(command.doit);

		if (command.doit)
		{
			document.replace(command.offset, command.length, command.text);
		}
		assertEquals("/* comment */hello\n", document.get());
	}

	/**
	 * See APSTUD-2868
	 *
	 * @throws Exception
	 */
	@Test
	public void testNoIndentAfterComment() throws Exception
	{
		RubyRegexpAutoIndentStrategy strategy = new CSSRubleRubyRegexpAutoIndentStrategy()
		{
			@Override
			protected boolean isComment(int offset)
			{
				return false;
			}
		};
		IDocument document = new Document(" *");

		// Don't add stars if we're not inside a comment
		DocumentCommand command = createNewlineCommand(2);
		strategy.customizeDocumentCommand(document, command);
		assertTrue(command.doit);

		if (command.doit)
		{
			document.replace(command.offset, command.length, command.text);
		}
		// No stars added since we're not in a comment, but maintain the one-space indent level
		assertEquals(" *\n ", document.get());
	}

	@Test
	public void testNewlineAtBlockCommentClose() throws Exception
	{
		RubyRegexpAutoIndentStrategy strategy = new CSSRubleRubyRegexpAutoIndentStrategy();
		IDocument document = new Document("/* comment */hello");

		// Don't duplicate the close of the block comment
		DocumentCommand command = createNewlineCommand(11);
		strategy.customizeDocumentCommand(document, command);
		assertTrue(command.doit);

		if (command.doit)
		{
			document.replace(command.offset, command.length, command.text);
		}
		assertEquals("/* comment \n */hello", document.get());
	}

	@Test
	public void testAutoCloseBlockCommentStart() throws Exception
	{
		RubyRegexpAutoIndentStrategy strategy = new CSSRubleRubyRegexpAutoIndentStrategy();
		IDocument document = new Document("/*");

		DocumentCommand command = createNewlineCommand(2);
		strategy.customizeDocumentCommand(document, command);
		assertTrue(command.doit);
		// TODO Ensure that the caret is mid comment...
		if (command.doit)
		{
			document.replace(command.offset, command.length, command.text);
		}
		assertEquals("/*\n * \n */", document.get().replaceAll("\r\n|\n", "\n"));
	}

	/**
	 * APSTUD3640
	 *
	 * @throws Exception
	 */
	@Test
	public void testDontAddLeadingSpaceAfterBlockCOmmentCloseBeforeNextLineContent() throws Exception
	{
		RubyRegexpAutoIndentStrategy strategy = new CSSRubleRubyRegexpAutoIndentStrategy();
		IDocument document = new Document("/*\n * \n */body {}");

		DocumentCommand command = createNewlineCommand(10);
		strategy.customizeDocumentCommand(document, command);
		assertTrue(command.doit);
		if (command.doit)
		{
			document.replace(command.offset, command.length, command.text);
		}
		assertEquals("/*\n * \n */\nbody {}", document.get());
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

	@Test(timeout = 10000)
	public void testUmlautCharacters()
	{
		RubyRegexpAutoIndentStrategy strategy = new JSRubleRubyRegexpAutoIndentStrategy();

		Ruby runtime = Ruby.newInstance();
		String regPattern = "(?-mix:^(.*\\*\\/)?\\s*(\\}|\\))([^{]*\\{)?([;,]?\\s*|\\.[^{]*|\\s*\\)[;\\s]*)$)";
		RubyRegexp regexp = RubyRegexp.newRegexp(runtime, regPattern, new RegexpOptions(KCode.UTF8, true));

		strategy.matchesRegexp(regexp, "console.log('üadäf');");
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

	private static class JSRubleRubyRegexpAutoIndentStrategy extends RubyRegexpAutoIndentStrategy
	{
		JSRubleRubyRegexpAutoIndentStrategy()
		{
			super("", null, null, null);
		}

		// Use CSS ruble's indent regexps
		@Override
		protected RubyRegexp getDecreaseIndentRegexp(String scope)
		{
			return RubyRegexp.newRegexp(Ruby.getGlobalRuntime(), ByteList.create("(?<!\\*)\\*\\*\\/|^\\s*\\}"));
		}

		@Override
		protected RubyRegexp getIncreaseIndentRegexp(String scope)
		{

			return RubyRegexp.newRegexp(Ruby.getGlobalRuntime(),
					ByteList.create("\\/\\*\\*(?!\\*)|\\{\\s*($|\\/\\*(?!.*?\\*\\/.*\\S))"));
		}

		@Override
		protected String getScopeAtOffset(IDocument d, int offset) throws BadLocationException
		{
			// just return an empty scope for testing
			return "";
		}
	}

	private static class CSSRubleRubyRegexpAutoIndentStrategy extends RubyRegexpAutoIndentStrategy
	{
		CSSRubleRubyRegexpAutoIndentStrategy()
		{
			super("", null, null, null);
		}

		// Use CSS ruble's indent regexps
		@Override
		protected RubyRegexp getDecreaseIndentRegexp(String scope)
		{
			return RubyRegexp.newRegexp(Ruby.getGlobalRuntime(), ByteList.create("(?<!\\*)\\*\\*\\/|^\\s*\\}"));
		}

		@Override
		protected RubyRegexp getIncreaseIndentRegexp(String scope)
		{

			return RubyRegexp.newRegexp(Ruby.getGlobalRuntime(),
					ByteList.create("\\/\\*\\*(?!\\*)|\\{\\s*($|\\/\\*(?!.*?\\*\\/.*\\S))"));
		}

		@Override
		protected String getScopeAtOffset(IDocument d, int offset) throws BadLocationException
		{
			// just return an empty scope for testing
			return "";
		}
	}
}
