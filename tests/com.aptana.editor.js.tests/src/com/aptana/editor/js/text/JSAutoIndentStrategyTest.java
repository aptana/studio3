/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.text;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.junit.Test;

import com.aptana.editor.js.JSSourceConfiguration;

/**
 * Additional tests to ensure that JavaScript partitions are being correctly reported as comments or not.
 * 
 * @author Ingo Muschenetz
 */
public class JSAutoIndentStrategyTest
{
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

	@Test
	public void testIsComment()
	{
		List<String> validTokenTypes = new ArrayList<String>(Arrays.asList(JSSourceConfiguration.JS_MULTILINE_COMMENT,
				JSSourceConfiguration.JS_SINGLELINE_COMMENT, JSSourceConfiguration.JS_DOC));

		for (String ct : JSSourceConfiguration.CONTENT_TYPES)
		{
			TestJSAutoIndentStrategy strategy = new TestJSAutoIndentStrategy(ct);
			if (validTokenTypes.contains(ct))
			{
				assertTrue(MessageFormat.format("{0} is a valid comment type", ct), strategy.isComment(0));
			}
			else
			{
				assertFalse(MessageFormat.format("{0} is not a valid comment type", ct), strategy.isComment(0));
			}
		}
	}

	/**
	 * Test empty
	 */
	@Test
	public void testEmpty()
	{
		assertSDocParams("/**\n", "\n * ");
	}

	/**
	 * Test no params
	 */
	@Test
	public void testSDocNoParams()
	{
		assertSDocParams("/**\nfunction abc() {\n}\n", "\n * ");
	}

	/**
	 * Test /**function abc(a, b) {} 2. <sdoc-comment>(function() {})()<br>
	 */
	@Test
	public void testSDocAddParams1()
	{
		assertSDocParams("/**\nfunction abc(a, b) {\n}\n", "\n * \n * @param {Object} a\n * @param {Object} b");
	}

	/**
	 * Test /**(function(a, b) {})()
	 */
	@Test
	public void testSDocAddParams2()
	{
		assertSDocParams("/**(function(a, b) {})()", "\n * \n * @param {Object} a\n * @param {Object} b");
	}

	/**
	 * Test /**key: function(a, b) {}
	 */
	@Test
	public void testSDocAddParams3()
	{
		assertSDocParams("/**key: function(a, b) {}", "\n * \n * @param {Object} a\n * @param {Object} b");
	}

	/**
	 * Test /**"key": function(a, b) {}
	 */
	@Test
	public void testSDocAddParams4()
	{
		assertSDocParams("/**\"key\": function(a, b) {}", "\n * \n * @param {Object} a\n * @param {Object} b");
	}

	/**
	 * Test /**var x = function(a, b) {}
	 */
	@Test
	public void testSDocAddParams5()
	{
		assertSDocParams("/**var x = function(a, b) {}", "\n * \n * @param {Object} a\n * @param {Object} b");
	}

	/**
	 * Test /**x = function(a, b) {}
	 */
	@Test
	public void testSDocAddParams6()
	{
		assertSDocParams("/**x = function(a, b) {}", "\n * \n * @param {Object} a\n * @param {Object} b");
	}

	/**
	 * Test /**x.y = function(a, b) {}
	 */
	@Test
	public void testSDocAddParams7()
	{
		assertSDocParams("/**x.y = function(a, b) {}", "\n * \n * @param {Object} a\n * @param {Object} b");
	}

	private void assertSDocParams(String documentSource, String insertedParameters)
	{
		JSAutoIndentStrategy strategy = new JSAutoIndentStrategy("", null, null, null);
		IDocument document = new Document(documentSource);

		// Inside block comment, add star
		DocumentCommand command = createNewlineCommand(3);
		strategy.customizeDocumentCommand(document, command);
		// Test this after converting the line delimiters to \n only.
		String commandText = (command.text != null) ? command.text.replaceAll("\r\n|\r", "\n") : null;
		assertEquals(insertedParameters, commandText);
		assertEquals(command.caretOffset, 7);
		assertTrue(command.doit);
	}

	private static class TestJSAutoIndentStrategy extends JSAutoIndentStrategy
	{
		TestJSAutoIndentStrategy(String contentType)
		{
			super(contentType, null, null, null);
		}

		public void customizeDocumentCommand(IDocument document, DocumentCommand command)
		{
		}

		@Override
		protected boolean autoIndent(IDocument d, DocumentCommand c)
		{
			return false;
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.editor.common.text.CommonAutoIndentStrategy#isComment(int)
		 */
		@Override
		public boolean isComment(int offset)
		{
			return super.isComment(offset);
		}

	}
}
