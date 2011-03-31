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
		// TODO Read in the contents and make sure it's what we expect.
		// String contents = IOUtil.read(new FileInputStream(expected));
		Theme afterExport = importer.convert(expected);

		// Make sure the global options remain equal after an import/export/import
		assertEquals(theme.getForeground(), afterExport.getForeground());
		assertEquals(theme.getBackground(), afterExport.getBackground());
		assertEquals(theme.getLineHighlight(), afterExport.getLineHighlight());
		assertEquals(theme.getCaret(), afterExport.getCaret());
		assertEquals(theme.getSelection(), afterExport.getSelection());
		assertEquals(theme.getName(), afterExport.getName());

		List<ThemeRule> origRules = theme.getTokens();
		List<ThemeRule> afterRules = afterExport.getTokens();
		// Same amount of tokens
		assertEquals(origRules.size(), afterRules.size());
		// Maintains ordering of rules
		for (int i = 0; i < origRules.size(); i++)
		{
			assertEquals(origRules.get(i), afterRules.get(i));
		}
	}

}
