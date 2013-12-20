/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.theme;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.junit.Assert;
import org.junit.Test;

public class ThemeExporterTest
{

	@Test
	public void testExport() throws Exception
	{
		TextmateImporter importer = new TextmateImporter();
		URL url = FileLocator.find(ThemePlugin.getDefault().getBundle(), Path.fromPortableString("Midnight.tmTheme"),
				null);
		url = FileLocator.toFileURL(url);
		Theme theme = importer.convert(new File(url.toURI()));

		ThemeExporter exporter = new ThemeExporter();
		File expected = File.createTempFile("theme_exporter", ".tmTheme");
		exporter.export(expected, theme);

		assertTrue(expected.isFile());
		Theme afterExport = importer.convert(expected);

		assertEquals(theme, afterExport);
	}

	@Test
	public void test4068() throws Exception
	{
		TextmateImporter importer = new TextmateImporter();
		URL url = FileLocator.find(ThemePlugin.getDefault().getBundle(), Path.fromPortableString("Netbeans_7.tmTheme"),
				null);
		url = FileLocator.toFileURL(url);
		Theme theme = importer.convert(new File(url.toURI()));

		ThemeExporter exporter = new ThemeExporter();
		File expected = File.createTempFile("theme_exporter", ".tmTheme");
		exporter.export(expected, theme);

		assertTrue(expected.isFile());
		Theme afterExport = importer.convert(expected);

		assertEquals(theme, afterExport);
	}

	protected void assertEquals(Theme theme, Theme afterExport)
	{
		// Make sure the global options remain equal after an import/export/import
		Assert.assertEquals("Foreground", theme.getForeground(), afterExport.getForeground());
		Assert.assertEquals("Background", theme.getBackground(), afterExport.getBackground());
		Assert.assertEquals("Line highlight", theme.getLineHighlight(), afterExport.getLineHighlight());
		Assert.assertEquals("Caret", theme.getCaret(), afterExport.getCaret());
		Assert.assertEquals("Selection", theme.getSelection(), afterExport.getSelection());
		Assert.assertEquals("Name", theme.getName(), afterExport.getName());

		List<ThemeRule> origRules = theme.getTokens();
		List<ThemeRule> afterRules = afterExport.getTokens();
		// Same amount of tokens
		Assert.assertEquals("Rule count", origRules.size(), afterRules.size());
		// Maintains ordering of rules
		for (int i = 0; i < origRules.size(); i++)
		{
			Assert.assertEquals("Rule " + (i + 1), origRules.get(i), afterRules.get(i));
		}
	}

}
