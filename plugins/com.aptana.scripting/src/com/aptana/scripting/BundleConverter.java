package com.aptana.scripting;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{
		String userHome = System.getProperty("user.home");
		if (args == null || args.length == 0)
		{
			// User bundles
			// args = new String[] { userHome + "/Library/Application Support/TextMate/Bundles" };
			// Pre-installed TM bundle
			args = new String[] { "/Applications/TextMate.app/Contents/SharedSupport/Bundles" };
		}

		String outputDir = userHome + "/Documents/RadRails Bundles";
		if (args.length > 1)
		{
			outputDir = args[1];
		}

		// Only convert the following bundles
		String[] bundleFilter = new String[] { "Text" };
		File[] bundles = gatherBundles(new File(args[0]));
		if (bundles == null)
		{
			System.out.println("No bundles found in dir: " + args[0]);
			return;
		}
		for (File textmateBundleDir : bundles)
		{
			String nameWithoutExtension = textmateBundleDir.getName().substring(0,
					textmateBundleDir.getName().length() - 9);
			for (String bundleToConvert : bundleFilter)
			{
				if (bundleToConvert.equalsIgnoreCase(nameWithoutExtension))
				{
					convertBundle(textmateBundleDir, outputDir + File.separator + nameWithoutExtension);
					continue;
				}
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

	private static void convertBundle(File bundleDir, String outputBundlePath) throws IOException
	{
		Map<String, String> uuidToName = new HashMap<String, String>();
		// Run SnippetConverter on snippets sub dir
		File snippetsDir = new File(bundleDir, "Snippets");
		SnippetConverter.convert(snippetsDir, outputBundlePath + File.separator + "snippets" + File.separator
				+ "snippets.rb");
		uuidToName.putAll(SnippetConverter.uuidNameMap(snippetsDir));
		// Run Command Converter on Commands subdir
		File commandsDir = new File(bundleDir, "Commands");
		CommandConverter.convert(commandsDir, outputBundlePath + File.separator + "commands");
		uuidToName.putAll(CommandConverter.uuidNameMap(commandsDir));
		// Convert the Info.plist to a bundle.rb
		String bundleRBPath = outputBundlePath + File.separator + "bundle.rb";
		String contents = convertInfoPlist(new File(bundleDir, "Info.plist"), uuidToName);
		writeToFile(contents, bundleRBPath);
		// Copy tests dir
		copyDir(bundleDir + "/Tests/", outputBundlePath + "/tests");
		// Copy Support to our "lib"
		copyDir(bundleDir + "/Support/", outputBundlePath + "/lib");
		// Copy over common dirs that we don't handle...
		copyDir(bundleDir + "/Preferences/", outputBundlePath + "/unsupported/preferences");
		copyDir(bundleDir + "/Macros/", outputBundlePath + "/unsupported/macros");
		copyDir(bundleDir + "/DragCommands/", outputBundlePath + "/unsupported/drag_commands");
		copyDir(bundleDir + "/Templates/", outputBundlePath + "/unsupported/templates");
	}

	private static void copyDir(String srcPath, String destPath)
	{
		if (!new File(srcPath).exists())
			return;
		try
		{
			File dest = new File(destPath);
			dest.mkdirs();

			ProcessBuilder builder = new ProcessBuilder("cp", "-R", srcPath, destPath);
			Process p = builder.start();
			p.waitFor();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private static String convertInfoPlist(File plistFile, Map<String, String> uuidToName)
	{
		PlistProperties properties = parse(plistFile);

		StringBuilder buffer = new StringBuilder();
		buffer.append("require 'java'\n");
		buffer.append("require 'ruble'\n\n");
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
				String commandName = uuidToName.get(uuid);
				if (commandName == null)
					commandName = uuid;
				buffer.append(menuPrefix).append(".command '").append(commandName).append("'\n");
			}
		}
		return buffer.toString();
	}

	static PlistProperties parse(File plistFile)
	{
		try
		{
			ProcessBuilder builder = new ProcessBuilder("/usr/bin/plutil", "-convert", "xml1", "\""
					+ plistFile.getAbsolutePath() + "\"");
			Process p = builder.start();
			int exitCode = p.waitFor();
			if (exitCode != 0)
			{
				// Not necessarily an error, it may already be XML
				// System.err.println("Bad exit code for conversion: " + exitCode);
			}
			AbstractReader reader = PlistFactory.createReader();
			// FIXME Often these files will have special characters that aren't proper in XML (like say Ctrl+C as a
			// keybinding, 0x03 so we need it to become "&#x03;"), we need to massage the XML now!
			InputSource source = new InputSource(new InputStreamReader(new FileInputStream(plistFile), "UTF-8"));
			source.setEncoding("UTF-8");
			reader.setSource(source);
			return reader.parse();
		}
		catch (Exception e)
		{
			System.err.println("An error occurred processing: " + plistFile.getAbsolutePath());
		}
		return null;
	}

	protected static String sanitize(PlistProperties properties, String key)
	{
		String content = (String) properties.getProperty(key);
		if (content == null)
			return null;
		return content.replace("'", "\\'").replace("…", "...").replace("—", "-"); //$NON-NLS-1$ //$NON-NLS-2$
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

	static String convertKeyBinding(String keyBinding)
	{
		if (keyBinding == null)
			return ""; //$NON-NLS-1$
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < keyBinding.length(); i++)
		{
			char c = keyBinding.charAt(i);
			switch (c)
			{
				case '':
					builder.append("F5+"); //$NON-NLS-1$
					break;
				case '@':
					builder.append("M1+M2+"); //$NON-NLS-1$
					break;
				case '^':
					if ((keyBinding.length() > (i + 1)) && (keyBinding.charAt(i + 1) == '@'))
					{
						builder.append("CONTROL+COMMAND+SHIFT+"); //$NON-NLS-1$
						i++;
					}
					else
					{
						builder.append("CONTROL+M2+"); //$NON-NLS-1$
					}
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

		// Turn Shift+lowercase_letter into uppercase_letter
		String result = builder.toString();
		Pattern p = Pattern.compile("(SHIFT|M2)\\+([a-z])");
		Matcher m = p.matcher(result);
		StringBuffer sb = new StringBuffer();
		while (m.find())
		{
			m.appendReplacement(sb, m.group(2).toUpperCase());
		}
		m.appendTail(sb);
		result = sb.toString();
		return result;
	}
}
