/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.theme;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.junit.Test;

public class TextmateImporterTest
{

	@Test
	public void testImportOfMidnightTheme() throws Exception
	{
		TextmateImporter importer = new TextmateImporter();
		URL url = FileLocator.find(ThemePlugin.getDefault().getBundle(), Path.fromPortableString("Midnight.tmTheme"),
				null);
		url = FileLocator.toFileURL(url);
		Theme theme = importer.convert(new File(url.toURI()));
		assertEquals("Midnight", theme.getName());
		assertEquals(new RGB(248, 248, 248), theme.getForeground());
		assertEquals(new RGB(10, 0, 31), theme.getBackground());
		assertEquals(new RGBa(37, 0, 255, 133), theme.getSelection());
		assertEquals(new RGB(169, 166, 177), theme.getCaret());
		assertEquals(new RGBa(60, 30, 255, 77), theme.getLineHighlight());

		assertEquals(new RGB(105, 0, 161), theme.getForegroundAsRGB("comment"));
		assertEquals(new RGB(171, 42, 29), theme.getForegroundAsRGB("invalid.deprecated"));
		assertEquals(SWT.ITALIC, theme.getTextAttribute("invalid.deprecated").getStyle());
		assertEquals(new RGB(157, 30, 21), theme.getBackgroundAsRGB("invalid.illegal"));

		assertEquals(new RGB(255, 213, 0), theme.getForegroundAsRGB("constant"));
		assertEquals(new RGB(89, 158, 255), theme.getForegroundAsRGB("keyword"));
		assertEquals(new RGB(117, 175, 255), theme.getForegroundAsRGB("storage"));
		assertEquals(new RGB(0, 241, 58), theme.getForegroundAsRGB("string"));
		assertEquals(new RGB(0, 241, 58), theme.getForegroundAsRGB("meta.verbatim"));

		assertTrue(theme.hasEntry("meta.tag, meta.tag entity"));
	}

}
