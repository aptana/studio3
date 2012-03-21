/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aptana.core.util.IOUtil;

@SuppressWarnings("nls")
public class SnippetConverter
{

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{
		String userHome = System.getProperty("user.home");
		if (args == null || args.length == 0)
		{
			args = new String[] { userHome + "/Documents/Aptana Rubles/rails.ruble/Snippets" };
		}
		String outputFilePath;
		if (args.length < 2)
		{
			outputFilePath = userHome + "/Documents/Aptana Rubles/rails.ruble/Snippets/snippets.rb";
		}
		else
		{
			outputFilePath = args[1];
		}
		convert(new File(args[0]), outputFilePath);
	}

	private static List<String> convert(File snippetDirectory)
	{
		List<String> snippets = new ArrayList<String>();
		File[] plistFiles = findSnippetFiles(snippetDirectory);
		if (plistFiles == null)
			return snippets;
		for (File plistFile : plistFiles)
		{
			try
			{
				Map<String, Object> properties = BundleConverter.parse(plistFile);
				if (properties == null)
					continue;
				String name = BundleConverter.sanitize(properties, "name");
				StringBuilder buffer = new StringBuilder();
				buffer.append("snippet '").append(name).append("' do |s|\n");
				String trigger = BundleConverter.sanitize(properties, "tabTrigger");
				if (trigger != null)
				{
					buffer.append("  s.trigger = '").append(trigger).append("'\n");
				}
				else
				{
					buffer.append("  # FIXME No tab trigger, probably needs to become command\n");
				}
				String keyBinding = BundleConverter.sanitize(properties, "keyEquivalent");
				if (keyBinding != null)
				{
					keyBinding = BundleConverter.convertKeyBinding(keyBinding);
					buffer.append("  s.key_binding = '").append(keyBinding).append("'\n");
				}
				String scope = BundleConverter.sanitize(properties, "scope");
				if (scope != null)
				{
					buffer.append("  s.scope = '").append(scope).append("'\n");
				}
				String content = BundleConverter.sanitize(properties, "content");
				buffer.append("  s.expansion = '").append(content).append("'\n");
				buffer.append("end\n\n");
				snippets.add(buffer.toString());
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return snippets;
	}

	protected static File[] findSnippetFiles(File snippetDirectory)
	{
		File[] plistFiles = snippetDirectory.listFiles(new FilenameFilter()
		{
			public boolean accept(File dir, String name)
			{
				return name.endsWith("plist") || name.endsWith("tmSnippet");
			}
		});
		return plistFiles;
	}

	public static void convert(File snippetsDir, String outputFilePath) throws IOException
	{
		List<String> snippets = convert(snippetsDir);
		if (snippets == null || snippets.isEmpty())
			return;
		Writer writer = null;
		try
		{
			File outFile = new File(outputFilePath);
			outFile.getParentFile().mkdirs();
			writer = new java.io.OutputStreamWriter(new java.io.FileOutputStream(outFile), IOUtil.UTF_8);
			for (String snippet : snippets)
			{
				writer.write(snippet);
			}
		}
		finally
		{
			if (writer != null)
			{
				writer.close();
			}
		}
	}

	static Map<String, String> uuidNameMap(File snippetDirectory)
	{
		Map<String, String> snippets = new HashMap<String, String>();
		File[] plistFiles = findSnippetFiles(snippetDirectory);
		if (plistFiles == null)
			return snippets;
		for (File plistFile : plistFiles)
		{
			try
			{
				Map<String, Object> properties = BundleConverter.parse(plistFile);
				if (properties == null)
					continue;
				String name = BundleConverter.sanitize(properties, "name");
				String uuid = (String) properties.get("uuid");
				snippets.put(uuid, name);
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return snippets;
	}

}
