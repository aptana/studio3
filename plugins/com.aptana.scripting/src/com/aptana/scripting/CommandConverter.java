package com.aptana.scripting;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.Writer;

import org.xml.sax.InputSource;

import plistreader.AbstractReader;
import plistreader.PlistFactory;
import plistreader.PlistProperties;

public class CommandConverter
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
			args = new String[] { userHome + "/Documents/RadRails Bundles/ruby/commands" };
		}
		for (String commandDir : args)
		{
			File[] commandFiles = gatherCommands(new File(commandDir));
			if (commandFiles == null)
				continue;
			for (File commandFile : commandFiles)
			{
				convert(commandFile);
			}
		}
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
	private static void convert(File commandFile)
	{
		try
		{
			ProcessBuilder builder = new ProcessBuilder("/usr/bin/plutil", "-convert", "xml1", commandFile
					.getAbsolutePath());
			Process p = builder.start();
			int exitCode = p.waitFor();
			if (exitCode != 0)
				System.err.println("Bad exit code for conversion: " + exitCode);
			AbstractReader reader = PlistFactory.createReader();
			reader.setSource(new InputSource(new FileInputStream(commandFile)));
			PlistProperties properties = reader.parse();

			StringBuilder buffer = new StringBuilder();
			buffer.append("require 'radrails'\n\n");
			buffer.append("command '").append(sanitize(properties, "name")).append("' do |cmd|\n");
			buffer.append("  cmd.key_binding = '").append(sanitize(properties, "keyEquivalent")).append("'\n");
			buffer.append("  cmd.scope = '").append(sanitize(properties, "scope")).append("'\n");
			String outputType = sanitize(properties, "output");
			outputType = camelcaseToUnderscores(outputType);
			buffer.append("  cmd.output = :").append(outputType).append("\n");
			buffer.append("  cmd.input = :").append(sanitize(properties, "input"));
			String fallbackInput = sanitize(properties, "fallbackInput");
			if (fallbackInput != null)
				buffer.append(", :").append(fallbackInput);
			buffer.append("\n");
			buffer.append("  cmd.invoke =<<-EOF\n").append(properties.getProperty("command")).append("\nEOF\n");
			buffer.append("end\n");

			Writer writer = null;
			try
			{
				// convert spaces to underscores, remove file extension
				String name = commandFile.getName().toLowerCase();
				name = name.replace(' ', '_');
				int lastDot = name.lastIndexOf('.');
				if (lastDot != -1)
				{
					name = name.substring(0, lastDot);
				}
				File outFile = new File(commandFile.getParentFile(), name + ".rb");
				writer = new FileWriter(outFile);
				writer.write(buffer.toString());
			}
			finally
			{
				writer.close();
			}
		}
		catch (Exception e)
		{
			System.err.println("An error occurred processing: " + commandFile.getAbsolutePath());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	protected static String sanitize(PlistProperties properties, String key)
	{
		String content = (String) properties.getProperty(key);
		if (content == null)
			return null;
		return content.replace("'", "\\'"); //$NON-NLS-1$ //$NON-NLS-2$
	}

}
