package com.aptana.editor.common.scripting.snippets;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;

public class SnippetsCompletionProcessorTest extends TestCase
{

	public void testExtractPrefixFromDocument()
	{
		assertEquals("echo", extractPrefixFromDocument("  echo"));
		assertEquals(">echo", extractPrefixFromDocument(">echo"));
		assertEquals("]echo", extractPrefixFromDocument("]echo"));
		assertEquals("123-echo", extractPrefixFromDocument("123-echo"));
		assertEquals("something_echo", extractPrefixFromDocument("something_echo"));
		assertEquals("123echo", extractPrefixFromDocument("123echo"));
		assertEquals("&echo", extractPrefixFromDocument("&echo"));
		assertEquals("*echo", extractPrefixFromDocument("*echo"));
		assertEquals("(echo", extractPrefixFromDocument("(echo"));
		assertEquals(":echo", extractPrefixFromDocument(":echo"));
		assertEquals("'echo", extractPrefixFromDocument("'echo"));
	}
	
	public void testNarrowPrefix()
	{
		assertEquals("", SnippetsCompletionProcessor.narrowPrefix("echo"));
		assertEquals("echo", SnippetsCompletionProcessor.narrowPrefix(">echo"));
		assertEquals("echo", SnippetsCompletionProcessor.narrowPrefix("]echo"));
		assertEquals("echo", SnippetsCompletionProcessor.narrowPrefix("123-echo"));
		assertEquals("echo", SnippetsCompletionProcessor.narrowPrefix("something_echo"));
		assertEquals("", SnippetsCompletionProcessor.narrowPrefix("123echo"));
		assertEquals("echo", SnippetsCompletionProcessor.narrowPrefix("&echo"));
		assertEquals("echo", SnippetsCompletionProcessor.narrowPrefix("*echo"));
		assertEquals("echo", SnippetsCompletionProcessor.narrowPrefix("(echo"));
		assertEquals("echo", SnippetsCompletionProcessor.narrowPrefix(":echo"));
		assertEquals("echo", SnippetsCompletionProcessor.narrowPrefix("'echo"));
		assertEquals("echo", SnippetsCompletionProcessor.narrowPrefix("other.echo"));
	}

	private String extractPrefixFromDocument(String string)
	{
		return SnippetsCompletionProcessor.extractPrefixFromDocument(new Document(string), string.length());
	}

}
