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
