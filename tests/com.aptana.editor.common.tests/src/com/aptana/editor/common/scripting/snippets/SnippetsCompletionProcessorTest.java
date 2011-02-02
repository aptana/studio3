/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
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
