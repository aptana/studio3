package com.aptana.theme;

import java.io.File;
import java.net.URL;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;

public class ThemeExporterTest extends TestCase
{

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
		assertEquals("Foreground", theme.getForeground(), afterExport.getForeground());
		assertEquals("Background", theme.getBackground(), afterExport.getBackground());
		assertEquals("Line highlight", theme.getLineHighlight(), afterExport.getLineHighlight());
		assertEquals("Caret", theme.getCaret(), afterExport.getCaret());
		assertEquals("Selection", theme.getSelection(), afterExport.getSelection());
		assertEquals("Name", theme.getName(), afterExport.getName());

		List<ThemeRule> origRules = theme.getTokens();
		List<ThemeRule> afterRules = afterExport.getTokens();
		// Same amount of tokens
		assertEquals("Rule count", origRules.size(), afterRules.size());
		// Maintains ordering of rules
		for (int i = 0; i < origRules.size(); i++)
		{
			assertEquals("Rule " + (i + 1), origRules.get(i), afterRules.get(i));
		}
	}

}
