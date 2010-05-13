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

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.xml.sax.InputSource;

import com.aptana.core.util.ProcessUtil;

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
	 * Characters that naturally occur only with hitting shift, so if keybinding contains shift plus these characters,
	 * replace with their base character.
	 */
	private static Map<Character, Character> shiftChars = new HashMap<Character, Character>();
	static
	{
		shiftChars.put('_', '-');
		shiftChars.put('!', '1');
		shiftChars.put('@', '2');
		shiftChars.put('#', '3');
		shiftChars.put('$', '4');
		shiftChars.put('%', '5');
		shiftChars.put('^', '6');
		shiftChars.put('&', '7');
		shiftChars.put('*', '8');
		shiftChars.put('(', '9');
		shiftChars.put(')', '0');
		shiftChars.put('+', '=');
		shiftChars.put('{', '[');
		shiftChars.put('}', ']');
		shiftChars.put('|', '\\');
		shiftChars.put(':', ';');
		shiftChars.put('"', '\'');
		shiftChars.put('<', ',');
		shiftChars.put('>', '.');
		shiftChars.put('?', '/');
		shiftChars.put('~', '`');
	}

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
			args = new String[] { userHome + "/Library/Application Support/TextMate/Bundles" };
			// Pre-installed TM bundle
			// args = new String[] { "/Applications/TextMate.app/Contents/SharedSupport/Bundles" };
		}

		String outputDir = userHome + "/Documents/Aptana Rubles";
		if (args.length > 1)
		{
			outputDir = args[1];
		}

		// Only convert the following bundles
		String[] bundleFilter = new String[] { "json" };
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
					convertBundle(textmateBundleDir, outputDir + File.separator + nameWithoutExtension + ".ruble");
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

	private static boolean copyDir(String srcPath, String destPath)
	{
		return copyDir(Path.fromOSString(srcPath), Path.fromOSString(destPath));
	}

	private static boolean copyDir(IPath srcPath, IPath destPath)
	{
		if (!srcPath.toFile().exists())
			return true;
		try
		{
			destPath.toFile().mkdirs();

			Process p = ProcessUtil.run("cp", null, "-R", srcPath.toOSString(), destPath.toOSString());
			p.waitFor();
			return true;
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
		return false;
	}

	@SuppressWarnings("unchecked")
	private static String convertInfoPlist(File plistFile, Map<String, String> uuidToName)
	{
		PlistProperties properties = parse(plistFile);

		StringBuilder buffer = new StringBuilder();
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

		buffer.append(addIndents(new File(plistFile.getParentFile(), "Preferences")));

		File syntaxesDir = new File(plistFile.getParentFile(), "Syntaxes");
		buffer.append(addFolding(syntaxesDir));
		buffer.append(addFileTypes(syntaxesDir));

		// Menu
		PlistProperties mainMenu = (PlistProperties) properties.getProperty("mainMenu");
		if (mainMenu != null)
		{
			PlistProperties submenus = (PlistProperties) mainMenu.getProperty("submenus");
			buffer.append("\n  bundle.menu '").append(name).append("' do |main_menu|\n");
			buffer.append(handleMenu("    main_menu", submenus, (List<String>) mainMenu.getProperty("items"),
					uuidToName));
			buffer.append("  end\n");
		}
		else
		{
			List<String> items = (List<String>) properties.getProperty("ordering");
			buffer.append("\n  bundle.menu '").append(name).append("' do |main_menu|\n");
			buffer.append(handleMenu("    main_menu", new PlistProperties(), items, uuidToName));
			buffer.append("  end\n");
		}
		// end menu

		buffer.append("end\n");
		return buffer.toString();
	}

	private static String addIndents(File prefsDir)
	{
		StringBuilder builder = new StringBuilder();
		if (prefsDir == null || !prefsDir.isDirectory())
			return builder.toString();
		File[] files = prefsDir.listFiles(new FilenameFilter()
		{

			@Override
			public boolean accept(File dir, String name)
			{
				return name.endsWith(".plist");
			}
		});
		if (files == null || files.length < 1)
			return builder.toString();

		for (File prefsFile : files)
		{
			PlistProperties properties = parse(prefsFile);
			if (properties == null || !properties.hasKey("settings"))
				continue;
			PlistProperties settings = (PlistProperties) properties.getProperty("settings");
			if (!settings.hasKey("increaseIndentPattern"))
				continue;

			String scope = (String) properties.getProperty("scope");
			String increase = (String) settings.getProperty("increaseIndentPattern");
			increase = sanitizeRegexp(increase);
			builder.append("  increase_indent = /").append(increase).append("/\n");
			if (settings.hasKey("decreaseIndentPattern"))
			{
				String decrease = (String) settings.getProperty("decreaseIndentPattern");
				decrease = sanitizeRegexp(decrease);
				builder.append("  decrease_indent = /").append(decrease).append("/\n");
				builder.append("  bundle.indent['").append(scope).append("'] = increase_indent, decrease_indent\n");
			}
		}
		return builder.toString();
	}

	protected static String sanitizeRegexp(String regexp)
	{
		return regexp.replace("''", "'").replace("/", "\\/");
	}

	@SuppressWarnings("unchecked")
	private static String addFileTypes(File syntaxesDir)
	{
		StringBuilder builder = new StringBuilder();
		if (syntaxesDir == null || !syntaxesDir.isDirectory())
			return builder.toString();
		File[] files = syntaxesDir.listFiles(new FilenameFilter()
		{

			@Override
			public boolean accept(File dir, String name)
			{
				return name.endsWith(".tmLanguage") || name.endsWith(".plist");
			}
		});
		if (files == null || files.length < 1)
			return builder.toString();

		for (File syntaxFile : files)
		{
			PlistProperties properties = parse(syntaxFile);
			String scope = (String) properties.getProperty("scopeName");
			List<String> fileTypes = (List<String>) properties.getProperty("fileTypes");
			if (fileTypes != null && !fileTypes.isEmpty())
			{
				builder.append("  bundle.file_types['").append(scope).append("'] = ");
				for (String fileType : fileTypes)
				{
					String pattern = "*." + fileType;
					// If fileType has a period or begins with a capital letter we should assume exact filename match
					if (fileType.contains(".") || Character.isUpperCase(fileType.charAt(0)))
					{
						pattern = fileType;
					}
					builder.append("'").append(pattern).append("', ");
				}
				builder.delete(builder.length() - 2, builder.length());
				builder.append("\n");
			}
		}
		return builder.toString();
	}

	private static String addFolding(File syntaxesDir)
	{
		StringBuilder builder = new StringBuilder();
		if (syntaxesDir == null || !syntaxesDir.isDirectory())
			return builder.toString();
		File[] files = syntaxesDir.listFiles(new FilenameFilter()
		{

			@Override
			public boolean accept(File dir, String name)
			{
				return name.endsWith(".tmLanguage") || name.endsWith(".plist");
			}
		});
		if (files == null || files.length < 1)
			return builder.toString();

		for (File syntaxFile : files)
		{
			PlistProperties properties = parse(syntaxFile);
			String scope = (String) properties.getProperty("scopeName");
			boolean hasStart = properties.hasKey("foldingStartMarker");
			if (hasStart)
			{
				String folding = (String) properties.getProperty("foldingStartMarker");
				folding = sanitizeRegexp(folding);
				builder.append("  start_folding = /").append(folding).append("/\n");
			}
			boolean hasStop = properties.hasKey("foldingStopMarker");
			if (hasStop)
			{
				String folding = (String) properties.getProperty("foldingStopMarker");
				folding = sanitizeRegexp(folding);
				builder.append("  end_folding = /").append(folding).append("/\n");
			}
			if (hasStart && hasStop)
			{
				builder.append("  bundle.folding['").append(scope).append("'] = start_folding, end_folding\n");
			}
		}
		return builder.toString();
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
				{
					buffer.append("#").append(menuPrefix).append(".command '").append(uuid).append("'\n");
				}
				else
				{
					buffer.append(menuPrefix).append(".command '").append(commandName).append("'\n");
				}
			}
		}
		return buffer.toString();
	}

	static PlistProperties parse(File plistFile)
	{
		return parse(Path.fromOSString(plistFile.getAbsolutePath()));
	}

	static PlistProperties parse(IPath plistPath)
	{
		try
		{
			Process p = ProcessUtil.run("/usr/bin/plutil", plistPath.removeLastSegments(1), "-convert", "xml1", plistPath.lastSegment());
			int exitCode = p.waitFor();
			if (exitCode != 0)
			{
				// Not necessarily an error, it may already be XML
				// System.err.println("Bad exit code for conversion: " + exitCode);
			}
			AbstractReader reader = PlistFactory.createReader();
			// FIXME Often these files will have special characters that aren't proper in XML (like say Ctrl+C as a
			// keybinding, 0x03 so we need it to become "&#x03;"), we need to massage the XML now!
			InputSource source = new InputSource(new InputStreamReader(new FileInputStream(plistPath.toFile()), "UTF-8"));
			source.setEncoding("UTF-8");
			reader.setSource(source);
			return reader.parse();
		}
		catch (Exception e)
		{
			System.err.println("An error occurred processing: " + plistPath.toOSString());
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

		// We really need to convert any characters that only occur with a shift into their "unshifted chars + shift"
		for (Map.Entry<Character, Character> entry : shiftChars.entrySet())
		{
			result = result.replace("SHIFT+" + entry.getKey(), "SHIFT+" + entry.getValue());
			result = result.replace("M2+" + entry.getKey(), "M2+" + entry.getValue());
		}
		return result;
	}
}
