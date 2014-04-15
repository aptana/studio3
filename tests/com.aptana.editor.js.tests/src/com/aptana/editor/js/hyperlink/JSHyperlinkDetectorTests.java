/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.hyperlink;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.junit.Test;

import com.aptana.core.util.ObjectUtil;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.js.tests.JSEditorBasedTestCase;
import com.aptana.index.core.IFileStoreIndexingParticipant;
import com.aptana.js.core.index.JSFileIndexingParticipant;

/**
 * JSHyperlinkDetectorTests
 */
public class JSHyperlinkDetectorTests extends JSEditorBasedTestCase
{
	@Override
	protected IFileStoreIndexingParticipant createIndexer()
	{
		return new JSFileIndexingParticipant();
	}

	public void assertHyperlinks(String resource, JSAbstractHyperlink... expectedHyperlinks)
	{
		setupTestContext(resource);

		if (editor instanceof AbstractThemeableEditor)
		{
			AbstractThemeableEditor themeableEditor = (AbstractThemeableEditor) editor;

			// only supporting testing of one offset per file
			assertTrue(cursorOffsets.size() > 0);
			int offset = cursorOffsets.get(0);

			// grab the links for our offset
			JSHyperlinkDetector detector = new JSHyperlinkDetector();
			IHyperlink[] hyperlinks = detector.detectHyperlinks(themeableEditor, new Region(offset, 0), true);

			// make sure we got as many links as we expected. Note that we expect a null result when no links are
			// expected
			if (expectedHyperlinks == null || expectedHyperlinks.length == 0)
			{
				assertNull(hyperlinks);
			}
			else
			{
				assertNotNull(hyperlinks);
				assertEquals(expectedHyperlinks.length, hyperlinks.length);
			}

			for (int i = 0; i < expectedHyperlinks.length; i++)
			{
				// check type
				IHyperlink hyperlink = hyperlinks[i];

				// check content
				if (hyperlink instanceof JSSearchStringHyperlink)
				{
					assertHyperlink((JSSearchStringHyperlink) expectedHyperlinks[i],
							(JSSearchStringHyperlink) hyperlink);
				}
				else if (hyperlink instanceof JSTargetRegionHyperlink)
				{
					assertHyperlink((JSTargetRegionHyperlink) expectedHyperlinks[i],
							(JSTargetRegionHyperlink) hyperlink);
				}
				else
				{
					fail("Unknown hyperlink type: " + hyperlink.getClass().getName());
				}
			}
		}
	}

	protected void assertHyperlink(JSSearchStringHyperlink a, JSSearchStringHyperlink b)
	{
		// @formatter:off
		assertEquals("Hyperlink regions do not match", a.getHyperlinkRegion(), b.getHyperlinkRegion());
		assertTrue(
			"Labels do not match. Expected '" + a.getTypeLabel() + "', but found '" + b.getTypeLabel() + "'",
			ObjectUtil.areEqual(a.getTypeLabel(), b.getTypeLabel())
		);
		assertTrue("Text values do not match", b.getHyperlinkText().endsWith(a.getHyperlinkText()));
		assertTrue("File paths do not match", b.getTargetFilePath().endsWith(a.getTargetFilePath()));
		assertTrue(
			"Search strings do not match. Expected '" + a.getSearchString() + "', but found '" + b.getSearchString() + "'",
			ObjectUtil.areEqual(a.getSearchString(), b.getSearchString())
		);
		// @formatter:on
	}

	protected void assertHyperlink(JSTargetRegionHyperlink a, JSTargetRegionHyperlink b)
	{
		// @formatter:off
		assertEquals("Hyperlink regions do not match", a.getHyperlinkRegion(), b.getHyperlinkRegion());
		assertTrue(
			"Labels do not match. Expected '" + a.getTypeLabel() + "', but found '" + b.getTypeLabel() + "'",
			ObjectUtil.areEqual(a.getTypeLabel(), b.getTypeLabel())
		);
		assertTrue("Text values do not match", b.getHyperlinkText().endsWith(a.getHyperlinkText()));
		assertTrue("File paths do not match", b.getTargetFilePath().endsWith(a.getTargetFilePath()));
		assertEquals("Target regions do not match", a.getTargetRegion(), b.getTargetRegion());
		// @formatter:on
	}

	@Test
	public void testGlobal()
	{
		String resource = "hyperlinks/global.js";

		assertHyperlinks(resource, new JSTargetRegionHyperlink(new Region(28, 3), "", resource, resource, new Region(9,
				3)));
	}

	@Test
	public void testPropertyIsFunctionDeclaration()
	{
		String resource = "hyperlinks/propertyIsFunctionDeclaration.js";

		// NOTE: we should not get any links here
		assertHyperlinks(resource);
	}

	@Test
	public void testPropertyIsFunction()
	{
		String resource = "hyperlinks/propertyIsFunction.js";
		JSSearchStringHyperlink link = new JSSearchStringHyperlink(new Region(34, 3), "", resource, resource, "def");

		assertHyperlinks(resource, link);
	}

	@Test
	public void testParameter()
	{
		String resource = "hyperlinks/parameter.js";
		JSTargetRegionHyperlink link = new JSTargetRegionHyperlink(new Region(39, 3), "parameter", resource, resource,
				new Region(13, 3));

		assertHyperlinks(resource, link);
	}

	@Test
	public void testNestedParameter()
	{
		String resource = "hyperlinks/nestedParameter.js";
		JSTargetRegionHyperlink link = new JSTargetRegionHyperlink(new Region(71, 3), "parameter", resource, resource,
				new Region(13, 3));

		assertHyperlinks(resource, link);
	}

	@Test
	public void testLocalDeclaration()
	{
		String resource = "hyperlinks/localDeclaration.js";
		JSTargetRegionHyperlink link = new JSTargetRegionHyperlink(new Region(42, 3), "local declaration", resource,
				resource, new Region(22, 3));

		assertHyperlinks(resource, link);
	}

	@Test
	public void testLocalAssignment()
	{
		String resource = "hyperlinks/localAssignment.js";
		JSTargetRegionHyperlink link = new JSTargetRegionHyperlink(new Region(38, 3), "local assignment", resource,
				resource, new Region(18, 3));

		assertHyperlinks(resource, link);
	}

	@Test
	public void testDuplicateSymbols()
	{
		String resource = "hyperlinks/duplicateSymbols.js";

		// test 'abc'
		JSTargetRegionHyperlink link = new JSTargetRegionHyperlink(new Region(93, 3), "local assignment", resource,
				resource, new Region(0, 3));
		assertHyperlinks(resource, link);
	}

	@Test
	public void testDuplicateSymbols2()
	{
		String resource = "hyperlinks/duplicateSymbols2.js";

		// test 'xyz'
		JSTargetRegionHyperlink link = new JSTargetRegionHyperlink(new Region(113, 3), "local assignment", resource,
				resource, new Region(37, 3));
		assertHyperlinks(resource, link);
	}
}
