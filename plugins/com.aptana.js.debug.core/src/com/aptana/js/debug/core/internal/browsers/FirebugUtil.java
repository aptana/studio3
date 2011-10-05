/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable variableDeclaredInLoop

package com.aptana.js.debug.core.internal.browsers;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IPath;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.FirefoxUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.js.debug.core.JSDebugPlugin;

/**
 * @author Max Stepanov
 */
public final class FirebugUtil {

	private static final String PREF_FORMAT = "user_pref(\"extensions.firebug.externalEditors{0}\", \"{1}\");"; //$NON-NLS-1$

	/**
	 * 
	 */
	private FirebugUtil() {
	}

	public static boolean registerEditor(String name, IPath path, String cmdLine) {
		String id = name.replaceAll("\\W", "_"); //$NON-NLS-1$ //$NON-NLS-2$
		return registerEditor(id, name, path, cmdLine);
	}

	public static boolean registerEditor(String id, String name, IPath path, String cmdLine) {
		IPath profile = FirefoxUtil.findDefaultProfileLocation();
		if (profile == null) {
			return false;
		}
		HashMap<String, String> options = new HashMap<String, String>();
		options.put("label", name); //$NON-NLS-1$
		options.put("executable", path.toOSString()); //$NON-NLS-1$
		options.put("cmdline", cmdLine); //$NON-NLS-1$
		options.put("image", StringUtil.EMPTY); //$NON-NLS-1$

		IPath prefs = profile.append("prefs.js"); //$NON-NLS-1$
		LineNumberReader reader = null;
		PrintWriter writer = null;
		try {
			reader = new LineNumberReader(new InputStreamReader(new FileInputStream(prefs.toFile()), "UTF8")); //$NON-NLS-1$
			String line;
			ArrayList<String> lines = new ArrayList<String>();
			boolean editorsFound = false;
			boolean doWrite = false;
			Pattern prefPattern = Pattern
					.compile("^user_pref\\(\"extensions\\.firebug\\.externalEditors(.*)\", \"(.*)\"\\);$"); //$NON-NLS-1$
			while ((line = reader.readLine()) != null) {
				Matcher matcher = prefPattern.matcher(line);
				if (matcher.find()) {
					String pref = matcher.group(1);
					String value = matcher.group(2);
					if (pref.length() == 0) {
						editorsFound = true;
						String[] editors = value.split(","); //$NON-NLS-1$
						boolean addEntry = true;
						for (String editor : editors) {
							if (id.equals(editor.trim())) {
								addEntry = false;
								break;
							}
						}
						if (addEntry) {
							line = MessageFormat.format(PREF_FORMAT, pref, value + ',' + id);
							doWrite = true;
						}
					} else if (pref.charAt(0) == '.') {
						int index = pref.indexOf('.', 1);
						if (index > 0) {
							String editorId = pref.substring(1, index);
							if (id.equals(editorId)) {
								String option = pref.substring(index + 1);
								if (options.containsKey(option)) {
									if (!value.equals(options.get(option))) {
										value = (String) options.get(option);
										if (value.length() > 0) {
											line = MessageFormat.format(PREF_FORMAT, pref, value);
										} else {
											/* delete pref */
											line = null;
										}
										doWrite = true;
									}
									options.remove(option);
								}
							}
						}
					}
				}
				if (line != null) {
					lines.add(line);
				}
			}
			reader.close();
			reader = null;

			if (!editorsFound) {
				lines.add(MessageFormat.format(PREF_FORMAT, StringUtil.EMPTY, id));
				doWrite = true;
			}
			if (!options.isEmpty()) {
				for (Entry<String, String> entry : options.entrySet()) {
					if (entry.getValue().length() > 0) {
						lines.add(MessageFormat.format(PREF_FORMAT, '.' + id + '.' + entry.getKey(), entry.getValue()));
						doWrite = true;
					}
				}
			}
			if (doWrite) {
				writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(prefs.toFile()), "UTF8"))); //$NON-NLS-1$
				for (String string : lines) {
					writer.println(string);
				}
			}
		} catch (IOException e) {
			IdeLog.logError(JSDebugPlugin.getDefault(),
					MessageFormat.format("Reading '{0}' fails", prefs.toOSString()), e); //$NON-NLS-1$
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ignore) {
				}
			}
			if (writer != null) {
				writer.close();
			}
		}

		return true;
	}

}
