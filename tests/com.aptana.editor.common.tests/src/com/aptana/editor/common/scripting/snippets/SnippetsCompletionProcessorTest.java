/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.scripting.snippets;

import org.junit.Test;
import static org.junit.Assert.*;
import junit.framework.TestCase;

import org.eclipse.jface.text.Document;

public class SnippetsCompletionProcessorTest
{

	@Test
	public void testExtractPrefixFromDocument()
	{
		// Currently as a single test since they are all variations on testing one function, but could be broken up
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
		assertEquals("other.echo", extractPrefixFromDocument("other.echo"));
		assertEquals("!", extractPrefixFromDocument("!"));
		assertEquals("m.", extractPrefixFromDocument("m."));
		assertEquals(".add().get()", extractPrefixFromDocument(".add().get()"));
		assertEquals("$", extractPrefixFromDocument("$"));
		assertEquals("$_", extractPrefixFromDocument("$_"));
		assertEquals("$.get", extractPrefixFromDocument("$.get"));
		assertEquals(":", extractPrefixFromDocument(":"));
		assertEquals(":,", extractPrefixFromDocument(":,"));
		assertEquals("\":f", extractPrefixFromDocument("\":f"));
		assertEquals("<<<", extractPrefixFromDocument("<<<"));
		assertEquals("if?", extractPrefixFromDocument("if?"));
		assertEquals("/**", extractPrefixFromDocument("/**"));
		assertEquals("Enum", extractPrefixFromDocument("Enum"));
		assertEquals("*p", extractPrefixFromDocument("*p"));
		assertEquals("c)", extractPrefixFromDocument("c)"));
		assertEquals("<a", extractPrefixFromDocument("<a"));
		assertEquals("<", extractPrefixFromDocument("<"));
		assertEquals(">", extractPrefixFromDocument(">"));
	}

	@Test
	public void testExtractPrefixFromDocumentWithWhitespace()
	{
		assertEquals(">echo", extractPrefixFromDocument(" >echo"));
		assertEquals("]echo", extractPrefixFromDocument("\t]echo"));
		assertEquals("]echo", extractPrefixFromDocument("\t\t\t]echo"));
		assertEquals("]echo", extractPrefixFromDocument("\n]echo"));
	}

	@Test
	public void testNarrowPrefix()
	{
		// Currently as a single test since they are all variations on testing one function, but could be broken up
		assertEquals("", SnippetsCompletionProcessor.narrowPrefix("echo"));
		assertEquals("", SnippetsCompletionProcessor.narrowPrefix("eecho"));
		assertEquals("echo", SnippetsCompletionProcessor.narrowPrefix(">echo"));
		assertEquals("echo", SnippetsCompletionProcessor.narrowPrefix("]echo"));

		// FIXME Textmate does not consider an umlauted u as part of the prefix
		assertEquals("", SnippetsCompletionProcessor.narrowPrefix("üecho"));

		assertEquals("echo", SnippetsCompletionProcessor.narrowPrefix("123-echo"));
		assertEquals("echo", SnippetsCompletionProcessor.narrowPrefix("something_echo"));
		assertEquals("", SnippetsCompletionProcessor.narrowPrefix("123echo"));
		assertEquals("echo", SnippetsCompletionProcessor.narrowPrefix("&echo"));
		assertEquals("echo", SnippetsCompletionProcessor.narrowPrefix("*echo"));
		assertEquals("echo", SnippetsCompletionProcessor.narrowPrefix("(echo"));
		assertEquals("(echo", SnippetsCompletionProcessor.narrowPrefix("((echo"));
		assertEquals("echo", SnippetsCompletionProcessor.narrowPrefix("}echo"));
		assertEquals("echo", SnippetsCompletionProcessor.narrowPrefix("!echo"));
		assertEquals("echo", SnippetsCompletionProcessor.narrowPrefix(":echo"));
		assertEquals("echo", SnippetsCompletionProcessor.narrowPrefix("_echo"));
		assertEquals("echo", SnippetsCompletionProcessor.narrowPrefix("-echo"));
		assertEquals("echo", SnippetsCompletionProcessor.narrowPrefix("'echo"));
		assertEquals("'echo", SnippetsCompletionProcessor.narrowPrefix("\"'echo"));
		assertEquals("echo", SnippetsCompletionProcessor.narrowPrefix("other.echo"));
		assertEquals(").echo", SnippetsCompletionProcessor.narrowPrefix("other().echo"));
	}

	private String extractPrefixFromDocument(String string)
	{
		return SnippetsCompletionProcessor.extractPrefixFromDocument(new Document(string), string.length());
	}

}
