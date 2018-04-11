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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.IOUtil;
import com.aptana.core.util.ProcessRunner;
import com.aptana.core.util.ProcessUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.plist.PListParserFactory;

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
			// args = new String[] { userHome + "/Library/Application Support/TextMate/Bundles" };
			// Pre-installed TM bundle
			args = new String[] { "/Applications/TextMate.app/Contents/SharedSupport/Bundles" };
		}

		String outputDir = userHome + "/Documents/Aptana Rubles";
		if (args.length > 1)
		{
			outputDir = args[1];
		}

		// Only convert the following bundles
		String[] bundleFilter = new String[] { "Property List" };
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

	public static void convertBundle(File bundleDir, String outputBundlePath) throws IOException
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

			Process p = new ProcessRunner().run("cp", "-R", srcPath.toOSString(), destPath.toOSString());
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
		catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private static String convertInfoPlist(File plistFile, Map<String, String> uuidToName)
	{
		Map<String, Object> properties = parse(plistFile);

		StringBuilder buffer = new StringBuilder();
		buffer.append("require 'ruble'\n\n");
		String name = sanitize(properties, "name");
		buffer.append("bundle '").append(name).append("' do |bundle|\n");
		// Author
		buffer.append("  bundle.author = '").append(sanitize(properties, "contactName")).append("'\n");
		// Contact Email
		buffer.append("  bundle.contact_email_rot_13 = '").append(sanitize(properties, "contactEmailRot13"))
				.append("'\n");
		// Description
		buffer.append("  bundle.description =  <<END\n").append((String) properties.get("description"))
				.append("\nEND\n");

		File prefsDir = new File(plistFile.getParentFile(), "Preferences");
		buffer.append(addIndents(prefsDir));

		File syntaxesDir = new File(plistFile.getParentFile(), "Syntaxes");
		buffer.append(addFolding(syntaxesDir));
		buffer.append(addFileTypes(syntaxesDir));

		// Menu
		Map<String, Object> mainMenu = (Map<String, Object>) properties.get("mainMenu");
		if (mainMenu != null)
		{
			Map<String, Object> submenus = (Map<String, Object>) mainMenu.get("submenus");
			buffer.append("\n  bundle.menu '").append(name).append("' do |main_menu|\n");
			buffer.append(handleMenu("    main_menu", submenus, (List<String>) mainMenu.get("items"), uuidToName));
			buffer.append("  end\n");
		}
		else
		{
			List<String> items = (List<String>) properties.get("ordering");
			buffer.append("\n  bundle.menu '").append(name).append("' do |main_menu|\n");
			buffer.append(handleMenu("    main_menu", new HashMap<String, Object>(), items, uuidToName));
			buffer.append("  end\n");
		}
		// end menu

		buffer.append("end\n");
		buffer.append(addEnv(prefsDir));
		buffer.append(addSmartTypingPairs(prefsDir));
		return buffer.toString();
	}

	private static String addIndents(File prefsDir)
	{
		StringBuilder builder = new StringBuilder();
		if (prefsDir == null || !prefsDir.isDirectory())
			return builder.toString();
		File[] files = prefsDir.listFiles(new FilenameFilter()
		{

			public boolean accept(File dir, String name)
			{
				return name.endsWith(".plist");
			}
		});
		if (files == null || files.length < 1)
			return builder.toString();

		for (File prefsFile : files)
		{
			Map<String, Object> properties = parse(prefsFile);
			if (properties == null || !properties.containsKey("settings"))
				continue;
			@SuppressWarnings("unchecked")
			Map<String, Object> settings = (Map<String, Object>) properties.get("settings");
			if (!settings.containsKey("increaseIndentPattern"))
				continue;

			String scope = (String) properties.get("scope");
			String increase = (String) settings.get("increaseIndentPattern");
			increase = sanitizeRegexp(increase);
			builder.append("  increase_indent = /").append(increase).append("/\n");
			if (settings.containsKey("decreaseIndentPattern"))
			{
				String decrease = (String) settings.get("decreaseIndentPattern");
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

			public boolean accept(File dir, String name)
			{
				return name.endsWith(".tmLanguage") || name.endsWith(".plist");
			}
		});
		if (files == null || files.length < 1)
			return builder.toString();

		for (File syntaxFile : files)
		{
			Map<String, Object> properties = parse(syntaxFile);
			String scope = (String) properties.get("scopeName");
			List<String> fileTypes = (List<String>) properties.get("fileTypes");
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

			public boolean accept(File dir, String name)
			{
				return name.endsWith(".tmLanguage") || name.endsWith(".plist");
			}
		});
		if (files == null || files.length < 1)
			return builder.toString();

		for (File syntaxFile : files)
		{
			Map<String, Object> properties = parse(syntaxFile);
			String scope = (String) properties.get("scopeName");
			boolean hasStart = properties.containsKey("foldingStartMarker");
			if (hasStart)
			{
				String folding = (String) properties.get("foldingStartMarker");
				folding = sanitizeRegexp(folding);
				builder.append("  start_folding = /").append(folding).append("/\n");
			}
			boolean hasStop = properties.containsKey("foldingStopMarker");
			if (hasStop)
			{
				String folding = (String) properties.get("foldingStopMarker");
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
	private static String addEnv(File syntaxesDir)
	{
		StringBuilder builder = new StringBuilder();
		if (syntaxesDir == null || !syntaxesDir.isDirectory())
			return builder.toString();
		File[] files = syntaxesDir.listFiles(new FilenameFilter()
		{

			public boolean accept(File dir, String name)
			{
				return name.endsWith(".tmLanguage") || name.endsWith(".plist");
			}
		});
		if (files == null || files.length < 1)
			return builder.toString();

		for (File syntaxFile : files)
		{
			Map<String, Object> properties = parse(syntaxFile);
			String scope = (String) properties.get("scope");
			Map<String, Object> settings = (Map<String, Object>) properties.get("settings");
			boolean hasStart = settings.containsKey("shellVariables");
			if (hasStart)
			{
				List<Map<String, Object>> variables = (List<Map<String, Object>>) settings.get("shellVariables");
				builder.append("env '").append(scope).append("' do |e|\n");
				for (Map<String, Object> var : variables)
				{
					builder.append("  e['").append(var.get("name")).append("'] = ");
					builder.append("'").append(var.get("value")).append("'\n");
				}
				builder.append("end\n");
			}
		}
		return builder.toString();
	}

	@SuppressWarnings("unchecked")
	private static String addSmartTypingPairs(File syntaxesDir)
	{
		StringBuilder builder = new StringBuilder();
		if (syntaxesDir == null || !syntaxesDir.isDirectory())
			return builder.toString();
		File[] files = syntaxesDir.listFiles(new FilenameFilter()
		{

			public boolean accept(File dir, String name)
			{
				return name.endsWith(".tmLanguage") || name.endsWith(".plist");
			}
		});
		if (files == null || files.length < 1)
			return builder.toString();

		for (File syntaxFile : files)
		{
			Map<String, Object> properties = parse(syntaxFile);
			String scope = (String) properties.get("scope");
			Map<String, Object> settings = (Map<String, Object>) properties.get("settings");
			boolean hasStart = settings.containsKey("smartTypingPairs");
			if (hasStart)
			{
				builder.append("smart_typing_pairs['").append(scope).append("'] = [");
				List<List<String>> pairs = (List<List<String>>) settings.get("smartTypingPairs");
				for (List<String> pair : pairs)
				{
					for (String pairChar : pair)
					{
						if (pairChar.equals("'"))
						{
							pairChar = "\\'";
						}
						builder.append("'").append(pairChar).append("', ");
					}
				}
				builder.deleteCharAt(builder.length() - 1);
				builder.deleteCharAt(builder.length() - 1);
				builder.append("]\n");
			}
		}
		return builder.toString();
	}

	@SuppressWarnings("unchecked")
	protected static String handleMenu(String menuPrefix, Map<String, Object> submenus, List<String> items,
			Map<String, String> uuidToName)
	{
		if (items == null)
		{
			return "";
		}
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
				Map<String, Object> props = (Map<String, Object>) submenus.get(uuid);
				if (props != null)
				{
					// it's a submenu
					String subMenuName = (String) props.get("name");
					buffer.append(menuPrefix).append(".menu '").append(subMenuName).append("' do |submenu|\n");
					buffer.append(handleMenu(indent + "  submenu", submenus, (List<String>) props.get("items"),
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

	static Map<String, Object> parse(File plistFile)
	{
		return parse(Path.fromOSString(plistFile.getAbsolutePath()));
	}

	static Map<String, Object> parse(IPath plistPath)
	{
		try
		{
			return PListParserFactory.parse(plistPath.toFile());
		}
		catch (IOException e)
		{
			IdeLog.logError(ScriptingActivator.getDefault(), e.getMessage(), e);
		}
		return Collections.emptyMap();
	}

	protected static String sanitize(Map<String, Object> properties, String key)
	{
		String content = (String) properties.get(key);
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
			writer = new java.io.OutputStreamWriter(new java.io.FileOutputStream(outFile), IOUtil.UTF_8);
			writer.write(output);
		}
		finally
		{
			try
			{
				if (writer != null)
				{
					writer.close();
				}
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
			return StringUtil.EMPTY; //$NON-NLS-1$
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < keyBinding.length(); i++)
		{
			char c = keyBinding.charAt(i);
			switch (c)
			{
				case '': // 63236
					builder.append("F1+"); //$NON-NLS-1$
					break;
				case 63237:
					builder.append("F2+"); //$NON-NLS-1$
					break;
				case '': // 63238
					builder.append("F3+"); //$NON-NLS-1$
					break;
				case 63239:
					builder.append("F4+"); //$NON-NLS-1$
					break;
				case '': // 63240
					builder.append("F5+"); //$NON-NLS-1$
					break;
				case 63241:
					builder.append("F6+"); //$NON-NLS-1$
					break;
				case 63242:
					builder.append("F7+"); //$NON-NLS-1$
					break;
				case 63243:
					builder.append("F8+"); //$NON-NLS-1$
					break;
				case 63244:
					builder.append("F9+"); //$NON-NLS-1$
					break;
				case 63245:
					builder.append("F10+"); //$NON-NLS-1$
					break;
				case 63246:
					builder.append("F11+"); //$NON-NLS-1$
					break;
				case 63247:
					builder.append("F12+"); //$NON-NLS-1$
					break;
				case '@': // COMMAND, which is M1 on Mac
					builder.append("M1+"); //$NON-NLS-1$				
					break;
				case '^': // CTRL, which is M4 on Mac
					builder.append("M4+"); //$NON-NLS-1$					
					break;
				case '~': // M3, ALT/OPTION
					builder.append("M3+"); //$NON-NLS-1$
					break;
				case '$':
					builder.append("M2+"); //$NON-NLS-1$
					break;
				case '\n':
					builder.append("ENTER+"); //$NON-NLS-1$
					break;
				case '': // invisible escape character
					builder.append("ESCAPE+"); //$NON-NLS-1$
					break;
				case '': // invisible backspace character
					builder.append("DEL+"); //$NON-NLS-1$
					break;
				default:
					if (Character.isLetter(c) && Character.isUpperCase(c))
					{
						builder.append("M2+"); //$NON-NLS-1$
					}
					builder.append(Character.toUpperCase(c)).append('+');
					break;
			}
		}
		if (keyBinding.length() > 0)
			builder.deleteCharAt(builder.length() - 1);

		return builder.toString();
	}
}
