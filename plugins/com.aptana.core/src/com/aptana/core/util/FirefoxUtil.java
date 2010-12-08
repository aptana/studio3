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
package com.aptana.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.runtime.Platform;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.aptana.core.CorePlugin;

/**
 * @author Max Stepanov
 * 
 */
public final class FirefoxUtil {

	private static final String[] WIN32_PROFILES_LOCATIONS = {
		"%APPDATA%\\Mozilla\\Firefox\\" //$NON-NLS-1$
	};
	private static final String[] LINUX_PROFILES_LOCATIONS = {
		"~/.mozilla/firefox/" //$NON-NLS-1$
	};
	private static final String[] MACOSX_PROFILES_LOCATIONS = {
		"~/Library/Application Support/Firefox/", //$NON-NLS-1$
		"~/Library/Mozilla/Firefox/" //$NON-NLS-1$
	};

	private static final Map<String, String[]> LOCATIONS = new HashMap<String, String[]>();
	
	static {
		LOCATIONS.put(Platform.OS_WIN32, WIN32_PROFILES_LOCATIONS);
		LOCATIONS.put(Platform.OS_LINUX, LINUX_PROFILES_LOCATIONS);
		LOCATIONS.put(Platform.OS_MACOSX, MACOSX_PROFILES_LOCATIONS);
	}

	/**
	 * 
	 */
	private FirefoxUtil() {
	}

	/**
	 * findDefaultProfileLocation
	 * 
	 * @return File
	 */
	public static File findDefaultProfileLocation() {
		String[] locations = (String[]) LOCATIONS.get(Platform.getOS());
		if (locations != null) {
			for (int i = 0; i < locations.length; ++i) {
				String location = PlatformUtil.expandEnvironmentStrings(locations[i]);
				File dir = new File(location);
				if (!dir.isDirectory() || !dir.exists()) {
					continue;
				}
				CorePlugin.log(MessageFormat.format("Check location {0} for default profile", location)); //$NON-NLS-1$

				File[] profiles = readProfiles(dir);
				if (profiles.length == 0) {
					File dirProfiles = new File(dir, "Profiles"); //$NON-NLS-1$
					if (!dirProfiles.exists() || !dirProfiles.isDirectory()) {
						dirProfiles = dir;
					}
					profiles = dirProfiles.listFiles(new FilenameFilter() {
						public boolean accept(File dir, String name) {
							return name.endsWith(".default"); //$NON-NLS-1$
						}
					});
				}

				// Debug output
				StringBuffer sb = new StringBuffer();
				for (int j = 0; j < profiles.length; ++j) {
					if (j != 0) {
						sb.append(',');
					}
					sb.append(profiles[j].toString());
				}
				CorePlugin.log(MessageFormat.format("Profiles found: {0}", sb.toString())); //$NON-NLS-1$
				// End of Debug output

				for (int j = 0; j < profiles.length; ++j) {
					File profile = profiles[j];
					if (profile.exists() && profile.isDirectory()) {
						CorePlugin.log(MessageFormat.format("Default profile was found at {0}", profile.toString())); //$NON-NLS-1$
						return profile;
					}
				}
			}
		}
		return null;
	}

	/**
	 * readProfiles
	 * 
	 * @param file
	 * @return File[]
	 */
	private static File[] readProfiles(File dir) {
		List<File> list = new ArrayList<File>();
		File profilesIni = new File(dir, "profiles.ini"); //$NON-NLS-1$
		if (profilesIni.exists()) {
			LineNumberReader r = null;
			try {
				r = new LineNumberReader(new FileReader(profilesIni));
				String line;
				Map<String, Map<String, String>> sections = new HashMap<String, Map<String, String>>();
				Map<String, String> last = null;
				Pattern sectionPattern = Pattern.compile("^\\x5B(.*)\\x5D$"); //$NON-NLS-1$
				Pattern valuePattern = Pattern.compile("^(.[^=]*)=(.*)$"); //$NON-NLS-1$
				while ((line = r.readLine()) != null) {
					Matcher matcher = sectionPattern.matcher(line);
					if (matcher.find()) {
						last = new HashMap<String, String>();
						sections.put(matcher.group(1), last);
						continue;
					} else if (last == null) {
						continue;
					}
					matcher = valuePattern.matcher(line);
					if (matcher.find()) {
						last.put(matcher.group(1), matcher.group(2));
					}
				}
				for (String section : sections.keySet()) {
					if (section.startsWith("Profile")) { //$NON-NLS-1$
						Map<String, String> properties = sections.get(section);
						String path = (String) properties.get("Path"); //$NON-NLS-1$
						String isRelative = (String) properties.get("IsRelative"); //$NON-NLS-1$
						File profile;
						if (isRelative != null && "1".equals(isRelative)) { //$NON-NLS-1$
							profile = new File(dir, path);
						} else {
							profile = new File(path); // TODO: base64 decode ?
						}
						boolean def = properties.containsKey("Default"); //$NON-NLS-1$
						if (def) {
							list.add(0, profile);
						} else {
							list.add(profile);
						}
					}
				}
			} catch (IOException e) {
				CorePlugin.log(MessageFormat.format("Reading '{0}' fails", profilesIni.getAbsolutePath()), e); //$NON-NLS-1$
			} finally {
				if (r != null) {
					try {
						r.close();
					} catch (IOException ignore) {
					}
				}
			}
		}
		return (File[]) list.toArray(new File[list.size()]);
	}

	/**
	 * Get extension version
	 * 
	 * @param extensionID
	 * @param profileDir
	 * @return
	 */
	public static String getExtensionVersion(String extensionID, File profileDir) {
		File dir = new File(new File(profileDir, "extensions"), extensionID); //$NON-NLS-1$
		if (dir.exists()) {
			File installRdf = new File(dir, "install.rdf"); //$NON-NLS-1$
			if (installRdf.exists()) {
				try {
					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
					DocumentBuilder parser = factory.newDocumentBuilder();
					Document document = parser.parse(new FileInputStream(installRdf));
					Node node = document.getDocumentElement().getFirstChild();
					while (node != null) {
						if ("description".equals(node.getNodeName().toLowerCase()) //$NON-NLS-1$
								|| "rdf:description".equals(node.getNodeName().toLowerCase())) { //$NON-NLS-1$
							NamedNodeMap attrs = node.getAttributes();
							Node about = attrs.getNamedItem("about"); //$NON-NLS-1$
							if (about == null) {
								about = attrs.getNamedItem("RDF:about"); //$NON-NLS-1$
							}
							if (about != null) {
								if ("urn:mozilla:install-manifest".equals(about.getNodeValue())) { //$NON-NLS-1$
									break;
								}
							}
						}
						node = node.getNextSibling();
					}
					if (node != null) {
						NamedNodeMap attrs = node.getAttributes();
						Node version = attrs.getNamedItem("em:version"); //$NON-NLS-1$
						if (version != null) {
							return version.getNodeValue();
						}
						node = node.getFirstChild();
					}
					while (node != null) {
						if ("em:version".equals(node.getNodeName().toLowerCase())) { //$NON-NLS-1$
							break;
						}
						node = node.getNextSibling();
					}
					if (node != null) {
						return node.getTextContent();
					}
				} catch (Exception e) {
					CorePlugin.log(e);
				}
			}
		}
		return null;
	}

}
