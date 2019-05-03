/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.util;

import static org.junit.Assert.assertEquals;

import org.eclipse.core.filesystem.IFileStore;
import org.junit.Test;
import org.osgi.framework.Bundle;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.tests.EditorBasedTests;
import com.aptana.ui.util.UIUtils;

/**
 * The class <code>EditorUtilTest</code> contains tests for the class <code>{@link EditorUtil}</code>.
 */
public class EditorUtilTest extends EditorBasedTests
{
	/**
	 * Run the String convertIndent(String,int,boolean) method test.
	 */
	@Test
	public void testConvertIndentSpacesNoTabs()
	{
		assertEquals(" ", EditorUtil.convertIndent(" ", 1, false));
		assertEquals(" ", EditorUtil.convertIndent("\t", 1, false));

		assertEquals(" ", EditorUtil.convertIndent(" ", 1, false));
		assertEquals("  ", EditorUtil.convertIndent("  ", 2, false));
		assertEquals("   ", EditorUtil.convertIndent("   ", 3, false));
		assertEquals("    ", EditorUtil.convertIndent("    ", 3, false));

		assertEquals("     ", EditorUtil.convertIndent("\t", 5, false));
		assertEquals("  ", EditorUtil.convertIndent("\t\t", 1, false));
		assertEquals("      ", EditorUtil.convertIndent("\t\t\t", 2, false));
	}

	/**
	 * Run the String convertIndent(String,int,boolean) method test.
	 */
	@Test
	public void testConvertIndentMixedSpaces()
	{
		assertEquals("   ", EditorUtil.convertIndent("\t\t ", 1, false));
		assertEquals("   ", EditorUtil.convertIndent(" \t\t", 1, false));
		assertEquals("   ", EditorUtil.convertIndent("\t \t", 1, false));
		assertEquals("     ", EditorUtil.convertIndent("\t\t ", 2, false));
		assertEquals("     ", EditorUtil.convertIndent(" \t\t", 2, false));
		assertEquals("     ", EditorUtil.convertIndent("\t \t", 2, false));
	}

	/**
	 * Run the String convertIndent(String,int,boolean) method test.
	 */
	@Test
	public void testConvertIndentTabsNoSpaces()
	{
		assertEquals("\t", EditorUtil.convertIndent(" ", 1, true));
		assertEquals("\t", EditorUtil.convertIndent("\t", 1, true));
		assertEquals(" ", EditorUtil.convertIndent(" ", 5, true));

		assertEquals("\t\t", EditorUtil.convertIndent("\t\t", 3, true));
		assertEquals("\t\t\t", EditorUtil.convertIndent("\t\t\t", 3, true));

		assertEquals("\t\t", EditorUtil.convertIndent("  ", 1, true));
		assertEquals("\t\t", EditorUtil.convertIndent("\t\t", 1, true));

		assertEquals("\t", EditorUtil.convertIndent(" ", 1, true));
		assertEquals("\t", EditorUtil.convertIndent("  ", 2, true));
		assertEquals("\t", EditorUtil.convertIndent("   ", 3, true));
		assertEquals("\t ", EditorUtil.convertIndent("    ", 3, true));

		assertEquals("\t", EditorUtil.convertIndent("\t", 5, true));
		assertEquals("\t\t", EditorUtil.convertIndent("\t\t", 1, true));

		assertEquals("\t\t\t", EditorUtil.convertIndent("\t\t\t", 2, true));
	}

	/**
	 * Run the String convertIndent(String,int,boolean) method test.
	 */
	@Test
	public void testConvertIndentMixedTabs()
	{
		assertEquals("\t\t\t", EditorUtil.convertIndent("\t\t ", 1, true));
		assertEquals("\t\t\t", EditorUtil.convertIndent("\t \t", 1, true));
		assertEquals("\t\t\t", EditorUtil.convertIndent(" \t\t", 1, true));
		assertEquals("\t\t ", EditorUtil.convertIndent("\t\t ", 2, true));
		assertEquals("\t\t ", EditorUtil.convertIndent("\t \t", 2, true));
		assertEquals("\t\t ", EditorUtil.convertIndent(" \t\t", 2, true));
	}

	/**
	 * Run the String convertIndent(String,int,boolean) method test.
	 */
	@Test
	public void testConvertIndentNull()
	{
		assertEquals("", EditorUtil.convertIndent(null, 1, false));
		assertEquals("", EditorUtil.convertIndent(null, 10, false));
		assertEquals("", EditorUtil.convertIndent(null, 1, true));
	}

	/**
	 * Run the String convertIndent(String,int,boolean) method test.
	 */
	@Test
	public void testConvertIndentTabsUseTabs()
	{
		assertEquals("", EditorUtil.convertIndent("", 1, false));
		assertEquals("", EditorUtil.convertIndent("", 1, true));
	}

	/**
	 * Run the int getSpaceIndentSize() method test.
	 */
	@Test
	public void testGetSpaceIndentSizeNoEditor()
	{
		int result = EditorUtil.getSpaceIndentSize();
		if (UIUtils.getActiveWorkbenchWindow() != null)
		{
			// running as a plugin test, prefs are enabled, default pref is 4
			assertEquals(4, result);
		}
		else
		{
			// non-ui test
			assertEquals(EditorUtil.DEFAULT_SPACE_INDENT_SIZE, result);
		}
	}

	/**
	 * Run the int getSpaceIndentSize() method test.
	 */
	@Test
	public void testGetSpaceIndentSizeEditor()
	{
		IFileStore fileStore = createFileStore("proposal_tests", "test", "");
		this.setupTestContext(fileStore);

		int result = EditorUtil.getSpaceIndentSize();

		// default is 4 for TEST editor
		assertEquals(4, result);

	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.EditorBasedTests#getBundle()
	 */
	@Override
	protected Bundle getBundle()
	{
		return CommonEditorPlugin.getDefault().getBundle();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.EditorBasedTests#getPluginId()
	 */
	@Override
	protected String getEditorId()
	{
		// straight text editor
		return "com.aptana.editor.test";
	}

}