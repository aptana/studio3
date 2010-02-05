package com.aptana.editor.html;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;

public class HTMLAutoIndentStrategyTest extends TestCase
{

	private static final String INDENT = "\t";
	private static final String NEWLINE = "\n";
	private HTMLAutoIndentStrategy strat;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		strat = new HTMLAutoIndentStrategy(null, null, null)
		{
			@Override
			protected String getIndentationString(IDocument d, int offset, int end)
			{
				return INDENT;
			}
		};
	}

	protected DocumentCommand sendNewline(String line)
	{
		IDocument document = new Document(line);
		DocumentCommand command = new DocumentCommand()
		{
		};
		command.text = NEWLINE;
		command.offset = line.length();
		command.caretOffset = line.length();
		strat.customizeDocumentCommand(document, command);
		return command;
	}

	public void testIndentsAfterBlockOpenTag()
	{
		String[] tags = { "html", "head", "div" };
		for (String tag : tags)
			assertIndent("<" + tag + ">");
	}

	public void testDoesntIndentAfterNonBlockTags()
	{
		String[] tags = { "h1", "p", "br" };
		for (String tag : tags)
			assertDidntIndent("<" + tag + ">");
	}
	
	public void testDedentsAfterBlockCloseTag()
	{
		String[] tags = { "html", "head", "div" };
		for (String tag : tags)
			assertDedent("\t<p>Testing</p>\n\t</" + tag + ">", "\t<p>Testing</p>\n</" + tag + ">");
	}
	// TODO Add tests for when file has mixed indentation that doesn't match prefs!
	
	protected void assertDedent(String src, String expected)
	{
		IDocument document = new Document(src);
		DocumentCommand command = new DocumentCommand()
		{
		};
		command.text = NEWLINE;
		command.offset = src.length();
		command.caretOffset = src.length();
		strat.customizeDocumentCommand(document, command);
		assertEquals(expected, document.get());	
	}

	protected void assertIndent(String line)
	{
		DocumentCommand command = sendNewline(line);
		assertEquals(NEWLINE + INDENT, command.text);
		assertEquals(false, command.shiftsCaret);
		assertEquals(line.length() + 2, command.caretOffset);
	}

	protected void assertDidntIndent(String line)
	{
		DocumentCommand command = sendNewline(line);
		assertEquals(NEWLINE, command.text);
		assertEquals(false, command.shiftsCaret);
		assertEquals(line.length(), command.caretOffset);
	}
}
