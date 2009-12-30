package com.aptana.scripting;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.text.MessageFormat;

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
	public static void main(String[] args) throws Exception
	{
		File file = new File("/Users/cwilliams/repos/red_core/plugins/com.aptana.scripting/bundles/css/snippets");
		File[] plistFiles = file.listFiles(new FilenameFilter()
		{

			@Override
			public boolean accept(File dir, String name)
			{
				return name.endsWith("plist");
			}
		});
		for (File plistFile : plistFiles)
		{
			try
			{
				ProcessBuilder builder = new ProcessBuilder("/usr/bin/plutil", "-convert", "xml1", plistFile
						.getAbsolutePath());
				Process p = builder.start();
				int exitCode = p.waitFor();
				if (exitCode != 0)
					System.out.println("Bad exit code for conversion: " + exitCode);
				AbstractReader reader = PlistFactory.createReader();
				reader.setSource(new InputSource(new FileInputStream(plistFile)));
				PlistProperties properties = reader.parse();

				String template = "snippet \"{0}\" do |s|\n  s.trigger = \"{1}\"\n  s.expansion = ''{2}''\nend\n";

				String content = (String) properties.getProperty("content");
				content = content.replace("'", "\\'");
				System.out.println(MessageFormat.format(template, properties.getProperty("name"), properties
						.getProperty("tabTrigger"), content));
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
