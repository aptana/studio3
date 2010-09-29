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
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.aptana.scripting.model.InputType;
import com.aptana.scripting.model.OutputType;

@SuppressWarnings("nls")
public class CommandConverter
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
			args = new String[] { userHome + "/Documents/Aptana Rubles/sass/commands" };
		}

		String outputFilePath;
		if (args.length < 2)
		{
			outputFilePath = userHome + "/Documents/Aptana Rubles/sass/commands";
		}
		else
		{
			outputFilePath = args[1];
		}
		convert(new File(args[0]), outputFilePath);
	}

	private static File[] gatherCommands(File commandDirectory)
	{
		return commandDirectory.listFiles(new FilenameFilter()
		{
			public boolean accept(File dir, String name)
			{
				return name.endsWith("plist") || name.endsWith("tmCommand");
			}
		});
	}

	private static String convert(File commandFile)
	{
		Map<String, Object> properties = BundleConverter.parse(commandFile);
		if (properties == null)
			return null;
		StringBuilder buffer = new StringBuilder();
		buffer.append("require 'ruble'\n\n");
		buffer.append("command '").append(BundleConverter.sanitize(properties, "name")).append("' do |cmd|\n");
		String keyBinding = BundleConverter.sanitize(properties, "keyEquivalent");
		if (keyBinding != null)
		{
			keyBinding = BundleConverter.convertKeyBinding(keyBinding);
			buffer.append("  cmd.key_binding = '").append(keyBinding).append("'\n");
		}
		String scope = BundleConverter.sanitize(properties, "scope");
		if (scope != null)
		{
			buffer.append("  cmd.scope = '").append(scope).append("'\n");
		}
		String trigger = BundleConverter.sanitize(properties, "tabTrigger");
		if (trigger != null)
		{
			buffer.append("  cmd.trigger = '").append(trigger).append("'\n");
		}
		String outputType = convertOutputTypes(camelcaseToUnderscores(BundleConverter.sanitize(properties, "output")));
		buffer.append("  cmd.output = :").append(outputType).append("\n");
		String inputType = convertInputTypes(BundleConverter.sanitize(properties, "input"));
		buffer.append("  cmd.input = :").append(inputType);
		// TODO If fallbackInput is null, it actually can often mean :document! (and I think it gets shoved into
		// TM_SELECTED_TEXT!)
		String fallbackInput = convertInputTypes(BundleConverter.sanitize(properties, "fallbackInput"));
		if (fallbackInput != null)
			buffer.append(", :").append(fallbackInput);
		buffer.append("\n");
		buffer.append("  cmd.invoke =<<-EOF\n").append(properties.get("command")).append("\nEOF\n");
		buffer.append("end\n");
		return buffer.toString();
	}

	protected static String convertFilename(File commandFile)
	{
		// convert spaces to underscores, remove file extension
		String name = commandFile.getName().toLowerCase();
		name = name.replace(' ', '_');
		int lastDot = name.lastIndexOf('.');
		if (lastDot != -1)
		{
			name = name.substring(0, lastDot);
		}
		return name + ".rb"; //$NON-NLS-1$
	}

	private static String convertOutputTypes(String outputType)
	{
		if (outputType.equals("replace_selected_text"))
		{
			return OutputType.REPLACE_SELECTION.getName().toLowerCase();
		}
		if (outputType.equals("after_selected_text"))
		{
			return OutputType.INSERT_AS_TEXT.getName().toLowerCase();
		}
		if (outputType.equals("open_as_new_document"))
		{
			return OutputType.CREATE_NEW_DOCUMENT.getName().toLowerCase();
		}
		return outputType;
	}

	private static String convertInputTypes(String inputType)
	{
		if (inputType == null)
			return null;
		if (inputType.equals("character"))
		{
			return InputType.RIGHT_CHAR.getName().toLowerCase();
		}
		return inputType;
	}

	private static String camelcaseToUnderscores(String outputType)
	{
		boolean lastWasCaps = false;
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < outputType.length(); i++)
		{
			char c = outputType.charAt(i);
			if (Character.isUpperCase(c))
			{
				if (!lastWasCaps)
				{
					builder.append("_"); //$NON-NLS-1$
				}
				builder.append(Character.toLowerCase(c));
				lastWasCaps = true;
			}
			else
			{
				builder.append(c);
				lastWasCaps = false;
			}
		}
		return builder.toString();
	}

	public static void convert(File textmateCommandsDir, String outputCommandsDir)
	{

		File[] commandFiles = gatherCommands(textmateCommandsDir);
		if (commandFiles == null)
			return;
		for (File commandFile : commandFiles)
		{
			convertSingleFile(commandFile, outputCommandsDir + File.separator + convertFilename(commandFile));
		}
	}

	private static void convertSingleFile(File commandFile, String outFilePath)
	{
		String output = convert(commandFile);
		if (output == null)
		{
			// TODO Spit out an error!
			return;
		}
		try
		{
			BundleConverter.writeToFile(output, outFilePath);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Map<String, String> uuidNameMap(File commandsDir)
	{
		Map<String, String> uuidNameMap = new HashMap<String, String>();
		File[] commandFiles = gatherCommands(commandsDir);
		if (commandFiles == null)
			return uuidNameMap;
		for (File commandFile : commandFiles)
		{
			Map<String, Object> properties = BundleConverter.parse(commandFile);
			if (properties == null)
				continue;
			String name = BundleConverter.sanitize(properties, "name");
			String uuid = (String) properties.get("uuid");
			uuidNameMap.put(uuid, name);
		}
		return uuidNameMap;
	}

}
