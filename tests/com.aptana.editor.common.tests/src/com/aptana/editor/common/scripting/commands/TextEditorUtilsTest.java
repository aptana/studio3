/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.scripting.commands;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.aptana.editor.common.scripting.tests.SingleEditorTestCase;

public class TextEditorUtilsTest extends SingleEditorTestCase
{
	private static final String PROJECT_NAME = "text_editors_util";

	@Test
	public void testNonZeroCaretOffset() throws Exception
	{
		createAndOpenFile("non_zero_caret.txt", "Hello world!");
		setCaretOffset(5);
		assertEquals(5, TextEditorUtils.getCaretOffset(getEditor()));
	}

	@Test
	public void testCaretOffsetWithNull()
	{
		assertEquals(-1, TextEditorUtils.getCaretOffset(null));
	}

	@Test
	public void testCaretOffset() throws Exception
	{
		createAndOpenFile("newfile.txt", "This is a brand new file!");
		assertEquals(0, TextEditorUtils.getCaretOffset(getEditor()));
	}

	@Override
	protected String getProjectName()
	{
		return PROJECT_NAME;
	}
}
