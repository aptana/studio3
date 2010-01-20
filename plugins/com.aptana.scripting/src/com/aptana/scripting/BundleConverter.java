package com.aptana.scripting;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.InputSource;

import plistreader.AbstractReader;
import plistreader.PlistFactory;
import plistreader.PlistProperties;

/**
 * This is a rather ugly but mostly working converter for textmate bundles. Run with no args it will search the user's
 * Textmate bundle directory and try to convert the bundle into our RadRails format and stick it in the user's RadRails
 * bundles dir. Currently this will just do basic command and snippet conversion and the main bundle.rb (in particular
 * the menu). The first arg given is the dir to search for Textmate bundles (dirs ending in "tmbundle") The second arg
 * is optional and is the directory in which to throw the converted bundle.
 * 
 * @author cwilliams
 */
@SuppressWarnings("nls")
public class BundleConverter
{

	// TODO Copy over the Support dir as lib.
	// TODO Copy over other things we don't yet handle, like Preferences, DragCommands, Macros

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{
		String userHome = System.getProperty("user.home");
		if (args == null || args.length == 0)
		{
			args = new String[] { userHome + "/Library/Application Support/TextMate/Bundles" };
		}

		String outputDir = userHome + "/Documents/RadRails Bundles";
		for (String bundlesDir : args)
		{
			File[] bundles = gatherBundles(new File(bundlesDir));
			if (bundles == null)
				continue;
			for (File commandFile : bundles)
			{
				String nameWithoutExtension = commandFile.getName().substring(0, commandFile.getName().length() - 9);
				convert(commandFile, outputDir + File.separator + nameWithoutExtension);
			}
		}
	}

	/**
	 * Collect all the bundles.
	 * 
	 * @param bundlesDir
	 * @return
	 */
	private static File[] gatherBundles(File bundlesDir)
	{
		return bundlesDir.listFiles(new FilenameFilter()
		{
			public boolean accept(File dir, String name)
			{
				return name.endsWith("tmbundle");
			}
		});
	}

	private static void convert(File bundleDir, String outputBundlePath) throws IOException
	{
		Map<String, String> uuidToName = new HashMap<String, String>();
		// TODO Run SnippetConverter on snippets sub dir
		File snippetsDir = new File(bundleDir, "Snippets");
		SnippetConverter.convert(snippetsDir, outputBundlePath + File.separator + "snippets" + File.separator
				+ "snippets.rb");
		uuidToName.putAll(SnippetConverter.uuidNameMap(snippetsDir));
		// TODO Run Command Converter on Commands subdir
		File commandsDir = new File(bundleDir, "Commands");
		CommandConverter.convert(commandsDir, outputBundlePath + File.separator + "commands");
		uuidToName.putAll(CommandConverter.uuidNameMap(commandsDir));
		// TODO Convert the Info.plist to a bundle.rb
		String bundleRBPath = outputBundlePath + File.separator + "bundle.rb";
		String contents = convertMain(new File(bundleDir, "Info.plist"), uuidToName);
		writeToFile(contents, bundleRBPath);
	}

	@SuppressWarnings("unchecked")
	private static String convertMain(File plistFile, Map<String, String> uuidToName)
	{
		PlistProperties properties = parse(plistFile);

		StringBuilder buffer = new StringBuilder();
		buffer.append("require 'java'\n");
		buffer.append("require 'radrails'\n\n");
		String name = sanitize(properties, "name");
		buffer.append("bundle '").append(name).append("' do |bundle|\n");
		// Author
		buffer.append("  bundle.author = '").append(sanitize(properties, "contactName")).append("'\n");
		// Contact Email
		buffer.append("  bundle.contact_email_rot_13 = '").append(sanitize(properties, "contactEmailRot13")).append(
				"'\n");
		// Description
		buffer.append("  bundle.description =  <<END\n").append((String) properties.getProperty("description")).append(
				"\nEND\n");

		// Menu
		PlistProperties mainMenu = (PlistProperties) properties.getProperty("mainMenu");
		PlistProperties submenus = (PlistProperties) mainMenu.getProperty("submenus");
		buffer.append("\n  bundle.menu '").append(name).append("' do |main_menu|\n");
		buffer.append(handleMenu("    main_menu", submenus, (List<String>) mainMenu.getProperty("items"), uuidToName));
		buffer.append("  end\n");
		// end menu

		buffer.append("end\n");
		return buffer.toString();
	}

	@SuppressWarnings("unchecked")
	protected static String handleMenu(String menuPrefix, PlistProperties submenus, List<String> items,
			Map<String, String> uuidToName)
	{
		// Calculate indent from our current one
		int spaces = menuPrefix.length() - menuPrefix.trim().length();
		String indent = "";
		for (int i = 0; i < spaces; i++)
		{
			indent += " ";
		}

		StringBuilder buffer = new StringBuilder();
		for (String uuid : items)
		{
			if (uuid.contains("-------"))
			{
				buffer.append(menuPrefix).append(".separator\n");
			}
			else
			{
				PlistProperties props = (PlistProperties) submenus.getProperty(uuid);
				if (props != null)
				{
					// it's a submenu
					String subMenuName = (String) props.getProperty("name");
					buffer.append(menuPrefix).append(".menu '").append(subMenuName).append("' do |submenu|\n");
					buffer.append(handleMenu(indent + "  submenu", submenus, (List<String>) props.getProperty("items"),
							uuidToName));
					buffer.append(indent).append("end\n");
					continue;
				}
				// Not a sub-menu, must be an item
				buffer.append(menuPrefix).append(".command '").append(uuidToName.get(uuid)).append("'\n");
			}
		}
		return buffer.toString();
	}

	static PlistProperties parse(File plistFile)
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
			return reader.parse();
		}
		catch (Exception e)
		{
			System.err.println("An error occurred processing: " + plistFile.getAbsolutePath());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	protected static String sanitize(PlistProperties properties, String key)
	{
		String content = (String) properties.getProperty(key);
		if (content == null)
			return null;
		return content.replace("'", "\\'").replace("É", "..."); //$NON-NLS-1$ //$NON-NLS-2$
	}

	static void writeToFile(String output, String outFilePath) throws IOException
	{
		Writer writer = null;
		try
		{
			File outFile = new File(outFilePath);
			outFile.getParentFile().mkdirs();
			writer = new FileWriter(outFile);
			writer.write(output);
		}
		finally
		{
			try
			{
				writer.close();
			}
			catch (IOException e)
			{
				// ignore
			}
		}
	}
}
