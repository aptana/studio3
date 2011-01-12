/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
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
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IPath;

import com.aptana.core.util.FirefoxUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.debug.core.DebugCorePlugin;

/**
 * @author Max Stepanov
 * 
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
						for (int i = 0; i < editors.length; ++i) {
							if (id.equals(editors[i].trim())) {
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
				for (Iterator<String> i = options.keySet().iterator(); i.hasNext();) {
					String option = (String) i.next();
					String value = (String) options.get(option);
					if (value.length() > 0) {
						lines.add(MessageFormat.format(PREF_FORMAT, '.' + id + '.' + option, value));
						doWrite = true;
					}
				}
			}
			if (doWrite) {
				writer = new PrintWriter(
						new BufferedWriter(new OutputStreamWriter(new FileOutputStream(prefs.toFile()), "UTF8"))); //$NON-NLS-1$
				for (Iterator<String> i = lines.iterator(); i.hasNext();) {
					writer.println((String) i.next());
				}
			}
		} catch (IOException e) {
			DebugCorePlugin.log(MessageFormat.format("Reading '{0}' fails", prefs.toOSString()), e); //$NON-NLS-1$
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
