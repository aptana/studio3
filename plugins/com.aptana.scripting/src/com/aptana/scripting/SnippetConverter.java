/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
			writer = new FileWriter(outFile);
			for (String snippet : snippets)
			{
				writer.write(snippet);
			}
		}
		finally
		{
			writer.close();
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
