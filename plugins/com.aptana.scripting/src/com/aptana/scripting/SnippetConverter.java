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
		File file = new File("/Users/cwilliams/repos/red_core/plugins/com.aptana.scripting/bundles/css/snippets"); //$NON-NLS-1$
		File[] plistFiles = file.listFiles(new FilenameFilter()
		{
			public boolean accept(File dir, String name)
			{
				return name.endsWith("plist"); //$NON-NLS-1$
			}
		});
		for (File plistFile : plistFiles)
		{
			try
			{
				ProcessBuilder builder = new ProcessBuilder("/usr/bin/plutil", "-convert", "xml1", plistFile //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						.getAbsolutePath());
				Process p = builder.start();
				int exitCode = p.waitFor();
				if (exitCode != 0)
					System.out.println("Bad exit code for conversion: " + exitCode); //$NON-NLS-1$
				AbstractReader reader = PlistFactory.createReader();
				reader.setSource(new InputSource(new FileInputStream(plistFile)));
				PlistProperties properties = reader.parse();

				String template = "snippet \"{0}\" do |s|\n  s.trigger = \"{1}\"\n  s.expansion = ''{2}''\nend\n"; //$NON-NLS-1$

				String content = (String) properties.getProperty("content"); //$NON-NLS-1$
				content = content.replace("'", "\\'"); //$NON-NLS-1$ //$NON-NLS-2$
				System.out.println(MessageFormat.format(template, properties.getProperty("name"), properties //$NON-NLS-1$
						.getProperty("tabTrigger"), content)); //$NON-NLS-1$
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
