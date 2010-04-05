package com.aptana.editor.common.text;

import junit.framework.TestCase;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.jruby.RubyRegexp;

public class RubyRegexpAutoIndentStrategyTest extends TestCase
{

	private int matchCount;

	protected void setUp() throws Exception
	{
		super.setUp();
		matchCount = 0;
	}

	protected void tearDown() throws Exception
	{
		matchCount = 0;
		super.tearDown();
	}

	public void testDoesntEraseRestofLineAfternewlineCharSentAndDedentMatches()
	{
		RubyRegexpAutoIndentStrategy strategy = new RubyRegexpAutoIndentStrategy("", null, null)
		{
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
				return matchCount++ == 1;
			}
		};
		IDocument document = new Document("\n\t</script></head>");
		DocumentCommand command = createNewlineCommand(11);
		strategy.customizeDocumentCommand(document, command);
		// Looks wrong, but command still runs to insert the newline
		assertEquals("\n</script></head>", document.get());
		assertTrue(command.doit);
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
		IDocument document = new Document("\tvar advance = function()\n\t\t{\n\t\t\treturn word = that.getWord();\n\t\t};");
		DocumentCommand command = createNewlineCommand(66);
		strategy.customizeDocumentCommand(document, command);
		assertEquals("\n\t\t", command.text);
		assertTrue(command.doit);
	}

	protected DocumentCommand createNewlineCommand(int offset)
	{
		DocumentCommand command = new DocumentCommand()
		{
		};
		command.text = "\n";
		command.offset = offset;
		command.caretOffset = offset;
		command.doit = true;
		return command;
	}

}
