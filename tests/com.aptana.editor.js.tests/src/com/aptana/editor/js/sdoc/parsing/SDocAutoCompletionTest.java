/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.sdoc.parsing;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.aptana.editor.js.contentassist.ParseUtil;
import com.aptana.editor.js.tests.JSEditorBasedTestCase;

/**
 * SDocAutoCompletionTests
 */
public class SDocAutoCompletionTest extends JSEditorBasedTestCase
{
	protected void assertParameters(String resource, String... parameters)
	{
		setupTestContext(resource);

		if (cursorOffsets.size() > 0)
		{
			List<String> extractedParameters = ParseUtil.getFunctionParameters(document, cursorOffsets.get(0));

			assertEquals("Wrong number of parameters", parameters.length, extractedParameters.size());

			for (int i = 0; i < parameters.length; i++)
			{
				String expected = parameters[i];
				String actual = extractedParameters.get(i);

				assertEquals("Wrong parameter name", expected, actual);
			}
		}
	}

	@Test
	public void testFunctionDeclaration()
	{
		// @formatter:off
		assertParameters(
			"sdoc/sdocCompletionOnFunctionDeclaration.js",
			"one",
			"two",
			"three"
		);
		// @formatter:on
	}

	@Test
	public void testFunctionDeclaration2()
	{
		// @formatter:off
		assertParameters(
			"sdoc/sdocCompletionOnFunctionDeclaration2.js",
			"one",
			"two",
			"three"
		);
		// @formatter:on
	}

	@Test
	public void testFunctionDeclaration3()
	{
		// @formatter:off
		assertParameters(
			"sdoc/sdocCompletionOnFunctionDeclaration3.js",
			"one",
			"two",
			"three"
		);
		// @formatter:on
	}

	@Test
	public void testSelfInvokingLambda()
	{
		// @formatter:off
		assertParameters(
			"sdoc/sdocCompletionOnSelfInvokingLambda.js",
			"one",
			"two",
			"three"
		);
		// @formatter:on
	}

	@Test
	public void testSelfInvokingLambda2()
	{
		// @formatter:off
		assertParameters(
			"sdoc/sdocCompletionOnSelfInvokingLambda2.js",
			"one",
			"two",
			"three"
		);
		// @formatter:on
	}

	@Test
	public void testSelfInvokingLambda3()
	{
		// @formatter:off
		assertParameters(
			"sdoc/sdocCompletionOnSelfInvokingLambda3.js",
			"one",
			"two",
			"three"
		);
		// @formatter:on
	}

	@Test
	public void testVarDeclaration()
	{
		// @formatter:off
		assertParameters(
			"sdoc/sdocCompletionOnVarDeclaration.js",
			"one",
			"two",
			"three"
		);
		// @formatter:on
	}

	@Test
	public void testKeyValuePair()
	{
		// @formatter:off
		assertParameters(
			"sdoc/sdocCompletionOnKeyValuePair.js",
			"one",
			"two",
			"three"
		);
		// @formatter:on
	}

	@Test
	public void testKeyValuePair2()
	{
		// @formatter:off
		assertParameters(
			"sdoc/sdocCompletionOnKeyValuePair2.js",
			"one",
			"two",
			"three"
		);
		// @formatter:on
	}

	@Test
	public void testAssignment()
	{
		// @formatter:off
		assertParameters(
			"sdoc/sdocCompletionOnAssignment.js",
			"one",
			"two",
			"three"
		);
		// @formatter:on
	}

	@Test
	public void testDottedAssignment()
	{
		// @formatter:off
		assertParameters(
			"sdoc/sdocCompletionOnDottedAssignment.js",
			"one",
			"two",
			"three"
		);
		// @formatter:on
	}
}
