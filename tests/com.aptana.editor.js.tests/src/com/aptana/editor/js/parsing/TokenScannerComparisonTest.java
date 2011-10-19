/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.parsing;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.Document;

import com.aptana.core.util.IOUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.js.JSPlugin;
import com.aptana.editor.js.parsing.lexer.JSLexemeProvider;
import com.aptana.editor.js.parsing.lexer.JSTokenType;
import com.aptana.parsing.lexer.Lexeme;
import com.aptana.parsing.lexer.Range;

/**
 * ScannerComparisonTest
 */
public class TokenScannerComparisonTest extends TestCase
{
	protected void assertListCrossProducts(String[][] lists)
	{
		long start = System.currentTimeMillis();

		// accumulator used to determine the number of enumerations we have
		long count = 1;

		// current offset within each sub-list
		int[] offsets = new int[lists.length];

		// initialize offsets and get total enumeration count
		for (int i = 0; i < lists.length; i++)
		{
			offsets[i] = 0;

			count *= lists[i].length;
		}

		// System.out.println(count + " tests");
		List<String> errors = new ArrayList<String>();

		// walk through all enumerations
		for (long enumeration = 0; enumeration < count; enumeration++)
		{
			StringBuilder buffer = new StringBuilder();

			// concatenate the current item from each sub-list into a single string
			for (int i = 0; i < lists.length; i++)
			{
				buffer.append(lists[i][offsets[i]]);
			}

			// check token types
			String source = buffer.toString();
			assertTokens(source, errors);

			// advance each offset, taking carries into account
			for (int j = lists.length - 1; j >= 0; j--)
			{
				int current = offsets[j] + 1;

				if (current > lists[j].length - 1)
				{
					// reset offset and continue processing to account for carry
					offsets[j] = 0;
				}
				else
				{
					// value is in range, save it and stop processing
					offsets[j] = current;
					break;
				}
			}
		}

		long diff = System.currentTimeMillis() - start;
		System.out.println((diff / 1000) + " seconds");

		if (!errors.isEmpty())
		{
			String allErrors = StringUtil.join("\n", errors);

			fail(errors.size() + " total errors\n" + allErrors);
		}
	}

	private void assertResourceTokens(String... resources)
	{
		for (String resource : resources)
		{
			System.out.println("Processing " + resource);

			assertResourceTokens(resource);
		}
	}

	private void assertResourceTokens(String resourceName)
	{
		try
		{
			assertTokens(getSource(resourceName));
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}

	private void assertTokens(String source)
	{
		assertTokens(source, null);
	}

	private void assertTokens(String source, List<String> errors)
	{
		Document document = new Document(source);
		Range range = new Range(0, source.length());

		JSLexemeProvider _ruleBasedProvider = new JSLexemeProvider(document, range, new JSTokenScanner());
		JSLexemeProvider _dfaBasedProvider = new JSLexemeProvider(document, range, new JSFlexTokenScanner());

		assertEquals(_ruleBasedProvider.size(), _dfaBasedProvider.size());

		for (int i = 0; i < Math.min(_ruleBasedProvider.size(), _dfaBasedProvider.size()); i++)
		{
			Lexeme<JSTokenType> ruleBasedLexeme = _ruleBasedProvider.getLexeme(i);
			Lexeme<JSTokenType> dfaBasedLexeme = _dfaBasedProvider.getLexeme(i);

			if (errors != null)
			{
				if (!ruleBasedLexeme.equals(dfaBasedLexeme))
				{
					errors.add(ruleBasedLexeme + " does not match " + dfaBasedLexeme + " in " + source);
				}
			}
			else
			{
				assertEquals(ruleBasedLexeme, dfaBasedLexeme);
			}
		}
	}

	private String getSource(InputStream stream) throws IOException
	{
		return IOUtil.read(stream);
	}

	private String getSource(String resourceName) throws IOException
	{
		InputStream stream = FileLocator.openStream(Platform.getBundle(JSPlugin.PLUGIN_ID), new Path(resourceName),
				false);
		return getSource(stream);
	}

	protected String[] getTestCharacters()
	{
		String[] characters = new String[128 - 31];

		// first 'character' is empty string
		characters[0] = StringUtil.EMPTY;

		// remaining characters are ASCII 32-127
		for (int i = 32; i < 128; i++)
		{
			characters[i - 31] = Character.toString((char) i);
		}

		return characters;
	}

	public void testDojo() throws Exception
	{
		assertResourceTokens(ITestFiles.DOJO_FILES);
	}

	public void testExt() throws Exception
	{
		assertResourceTokens(ITestFiles.EXT_FILES);
	}

	public void testJaxerFiles() throws Exception
	{
		assertResourceTokens(ITestFiles.JAXER_FILES);
	}

	public void testLists()
	{
		int maxLength = 3;
		String[][] lists = new String[maxLength][];
		String[] characters = getTestCharacters();

		for (int i = 0; i < maxLength; i++)
		{
			lists[i] = characters;
		}

		assertListCrossProducts(lists);
	}

	public void testTiMobile() throws Exception
	{
		assertResourceTokens(ITestFiles.TIMOBILE_FILES);
	}
}
