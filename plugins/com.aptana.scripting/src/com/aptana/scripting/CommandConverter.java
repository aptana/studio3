package com.aptana.scripting;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import plistreader.PlistProperties;

import com.aptana.scripting.model.OutputType;

public class CommandConverter
{

	/**
	 * @param args
	 * @throws Exception
	 */
	@SuppressWarnings("nls")
	public static void main(String[] args) throws Exception
	{
		String userHome = System.getProperty("user.home");
		if (args == null || args.length == 0)
		{
			args = new String[] { userHome + "/Documents/RadRails Bundles/sass/commands" };
		}

		String outputFilePath;
		if (args.length < 2)
		{
			outputFilePath = userHome + "/Documents/RadRails Bundles/sass/commands";
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
			@SuppressWarnings("nls")
			public boolean accept(File dir, String name)
			{
				return name.endsWith("plist") || name.endsWith("tmCommand");
			}
		});
	}

	@SuppressWarnings("nls")
	private static String convert(File commandFile)
	{
		PlistProperties properties = BundleConverter.parse(commandFile);

		StringBuilder buffer = new StringBuilder();
		buffer.append("require 'radrails'\n\n");
		buffer.append("command '").append(BundleConverter.sanitize(properties, "name")).append("' do |cmd|\n");
		String keyBinding = BundleConverter.sanitize(properties, "keyEquivalent");
		keyBinding = convertKeyBinding(keyBinding);
		buffer.append("  cmd.key_binding = '").append(keyBinding).append("'\n");
		buffer.append("  cmd.scope = '").append(BundleConverter.sanitize(properties, "scope")).append("'\n");
		String outputType = BundleConverter.sanitize(properties, "output");
		outputType = camelcaseToUnderscores(outputType);
		outputType = convertOutputTypes(outputType);
		buffer.append("  cmd.output = :").append(outputType).append("\n");
		buffer.append("  cmd.input = :").append(BundleConverter.sanitize(properties, "input"));
		String fallbackInput = BundleConverter.sanitize(properties, "fallbackInput");
		if (fallbackInput != null)
			buffer.append(", :").append(fallbackInput);
		buffer.append("\n");
		buffer.append("  cmd.invoke =<<-EOF\n").append(properties.getProperty("command")).append("\nEOF\n");
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

	private static String convertKeyBinding(String keyBinding)
	{
		if (keyBinding == null)
			return ""; //$NON-NLS-1$
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < keyBinding.length(); i++)
		{
			char c = keyBinding.charAt(i);
			switch (c)
			{
				case '@':
					builder.append("M1+M2+"); //$NON-NLS-1$
					break;
				case '^':
					builder.append("CONTROL+M2+"); //$NON-NLS-1$
					break;
				case '~':
					if ((keyBinding.length() > (i + 1)) && (keyBinding.charAt(i + 1) == '@'))
					{
						builder.append("OPTION+COMMAND+"); //$NON-NLS-1$
						i++;
					}
					else
					{
						builder.append(c).append('+');
					}
					break;
				default:
					builder.append(c).append('+');
					break;
			}
		}
		if (keyBinding.length() > 0)
			builder.deleteCharAt(builder.length() - 1);
		return builder.toString();
	}

	@SuppressWarnings("nls")
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
		return outputType;
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
			PlistProperties properties = BundleConverter.parse(commandFile);

			String name = BundleConverter.sanitize(properties, "name");
			String uuid = (String) properties.getProperty("uuid");
			uuidNameMap.put(uuid, name);
		}
		return uuidNameMap;
	}

}
