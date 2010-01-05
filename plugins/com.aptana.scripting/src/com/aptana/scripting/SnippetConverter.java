package com.aptana.scripting;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.InputSource;

import plistreader.AbstractReader;
import plistreader.PlistFactory;
import plistreader.PlistProperties;

public class SnippetConverter
{

	/**
	 * @param args
	 * @throws Exception
	 */
	@SuppressWarnings("nls")
	public static void main(String[] args) throws Exception
	{
		if (args == null || args.length == 0)
		{
			String userHome = System.getProperty("user.home");
			args = new String[] { userHome + "/Documents/RadRails Bundles/ruby/Snippets" };
		}
		for (String path : args)
		{
			List<String> snippets = convert(new File(path));
			Writer writer = null;
			try
			{
				File outFile = new File(path, "snippets.rb");
				writer = new FileWriter(outFile);
				for (String snippet : snippets)
				{
					writer.write(snippet);
					writer.write("\n");
				}
			}
			finally
			{
				writer.close();
			}
		}
	}

	@SuppressWarnings("nls")
	private static List<String> convert(File snippetDirectory)
	{
		List<String> snippets = new ArrayList<String>();
		File[] plistFiles = snippetDirectory.listFiles(new FilenameFilter()
		{
			public boolean accept(File dir, String name)
			{
				return name.endsWith("plist") || name.endsWith("tmSnippet");
			}
		});
		if (plistFiles == null)
			return snippets;
		for (File plistFile : plistFiles)
		{
			try
			{
				ProcessBuilder builder = new ProcessBuilder("/usr/bin/plutil", "-convert", "xml1", plistFile
						.getAbsolutePath());
				Process p = builder.start();
				int exitCode = p.waitFor();
				if (exitCode != 0)
					System.err.println("Bad exit code for conversion: " + exitCode);
				AbstractReader reader = PlistFactory.createReader();
				reader.setSource(new InputSource(new FileInputStream(plistFile)));
				PlistProperties properties = reader.parse();

				String trigger = sanitize(properties, "tabTrigger");
				String content = sanitize(properties, "content");
				// TODO Do more fiddling with the content?
				if (trigger != null)
				{
					String template = "snippet ''{0}'' do |s|\n  s.trigger = ''{1}''\n  s.expansion = ''{2}''\nend\n";
					snippets.add(MessageFormat.format(template, sanitize(properties, "name"), trigger, content));
				}
				else
				{
					String template = "# FIXME No tab trigger, probably needs to become command\nsnippet ''{0}'' do |s|\n  s.expansion = ''{1}''\nend\n";
					snippets.add(MessageFormat.format(template, sanitize(properties, "name"), content));
				}
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return snippets;
	}

	protected static String sanitize(PlistProperties properties, String key)
	{
		String content = (String) properties.getProperty(key);
		if (content == null)
			return null;
		return content.replace("'", "\\'"); //$NON-NLS-1$ //$NON-NLS-2$
	}

}
