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
package com.aptana.debug.internal.core.browsers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.aptana.core.util.PlatformUtil;
import com.aptana.debug.core.JSDebugPlugin;

/**
 * @author Max Stepanov
 * 
 */
public final class Firefox {

	public static final String NAME = "Firefox"; //$NON-NLS-1$
	public static final String NEW_WINDOW = "-new-window"; //$NON-NLS-1$
	public static final String NEW_TAB = "-new-tab"; //$NON-NLS-1$

	private static final String[] WIN32_PROFILES_LOCATIONS = { "%APPDATA%\\Mozilla\\Firefox\\" //$NON-NLS-1$
	};
	private static final String[] LINUX_PROFILES_LOCATIONS = { "~/.mozilla/firefox/" //$NON-NLS-1$
	};
	private static final String[] MACOSX_PROFILES_LOCATIONS = { "~/Library/Application Support/Firefox/", //$NON-NLS-1$
			"~/Library/Mozilla/Firefox/" //$NON-NLS-1$
	};

	private static final Map<String, String[]> LOCATIONS = new HashMap<String, String[]>();
	static {
		LOCATIONS.put(Platform.OS_WIN32, WIN32_PROFILES_LOCATIONS);
		LOCATIONS.put(Platform.OS_LINUX, LINUX_PROFILES_LOCATIONS);
		LOCATIONS.put(Platform.OS_MACOSX, MACOSX_PROFILES_LOCATIONS);
	}

	private Firefox() {
	}

	/**
	 * isBrowserExecutable
	 * 
	 * @param browserExecutable
	 * @return boolean
	 */
	public static boolean isBrowserExecutable(String browserExecutable) {
		String name = new File(browserExecutable).getName();
		if (name.toLowerCase().indexOf("firefox") != -1) { //$NON-NLS-1$
			return true;
		}
		return false;
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
				JSDebugPlugin.log(MessageFormat.format(
						"Check location {0} for default profile", location)); //$NON-NLS-1$

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
				JSDebugPlugin.log(MessageFormat.format("Profiles found: {0}", sb.toString())); //$NON-NLS-1$
				// End of Debug output

				for (int j = 0; j < profiles.length; ++j) {
					File profile = profiles[j];
					if (profile.exists() && profile.isDirectory()) {
						JSDebugPlugin.log(MessageFormat.format(
								"Default profile was found at {0}", profile.toString())); //$NON-NLS-1$
						return profile;
					}
				}
			}
		}
		return null;
	}

	/**
	 * installLinkedExtension
	 * 
	 * @param extensionURL
	 * @param extensionID
	 * @param dir
	 * @return boolean
	 */
	public static boolean installLinkedExtension(URL extensionURL, String extensionID, File dir) {
		File file = new File(dir, extensionID);
		if (file.exists() && file.isDirectory()) {
			return true;
		}
		IPath base = JSDebugPlugin.getDefault().getStateLocation().addTrailingSeparator();
		boolean result = installExtension(extensionURL, extensionID, base.toFile());
		if (result) {
			String linkedPath = base.append(extensionID).toOSString();
			FileOutputStream out = null;
			try {
				out = new FileOutputStream(file);
				out.write(linkedPath.getBytes());
			} catch (IOException e) {
				JSDebugPlugin.log(e);
			} finally {
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
					}
				}
			}
		}
		return result;
	}

	/**
	 * installExtension
	 * 
	 * @param extensionURL
	 * @param extensionID
	 * @param dir
	 * @return boolean
	 */
	public static boolean installExtension(URL extensionURL, String extensionID, File dir) {
		dir = new File(dir, extensionID);
		if (dir.exists()) {
			return true;
		}
		if (!dir.mkdirs()) {
			return false;
		}

		File file = null;
		InputStream in = null;
		FileOutputStream out = null;
		try {
			file = File.createTempFile("ffe", ".zip"); //$NON-NLS-1$ //$NON-NLS-2$
			in = extensionURL.openStream();
			out = new FileOutputStream(file);
			byte[] buffer = new byte[0x1000];
			int n;
			while ((n = in.read(buffer)) > 0) {
				out.write(buffer, 0, n);
			}
		} catch (IOException e) {
			JSDebugPlugin.log(e);
			if (file != null) {
				file.delete();
			}
			return false;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}

		try {
			extract(new ZipFile(file), dir);
		} catch (IOException e) {
			JSDebugPlugin.log(e);
			return false;
		} finally {
			file.delete();
		}

		return true;
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
					JSDebugPlugin.log(e);
				}
			}
		}
		return null;
	}

	/**
	 * extract
	 * 
	 * @param zip
	 * @param path
	 * @throws IOException
	 */
	private static void extract(ZipFile zip, File path) throws IOException {
		/* Create directories first */
		for (Enumeration<? extends ZipEntry> e = zip.entries(); e.hasMoreElements();) {
			ZipEntry entry = (ZipEntry) e.nextElement();
			String name = entry.getName();
			File file = new File(path, name);
			if (entry.isDirectory() && !file.exists()) {
				file.mkdirs();
			}
		}
		byte[] buffer = new byte[0x1000];
		int n;
		/* Extract files */
		for (Enumeration<? extends ZipEntry> e = zip.entries(); e.hasMoreElements();) {
			ZipEntry entry = (ZipEntry) e.nextElement();
			String name = entry.getName();
			File file = new File(path, name);
			if (!entry.isDirectory() && !file.exists()) {
				if (!file.createNewFile()) {
					continue;
				}
				OutputStream out = new FileOutputStream(file);
				InputStream in = zip.getInputStream(entry);
				while ((n = in.read(buffer)) > 0) {
					out.write(buffer, 0, n);
				}
				in.close();
				out.close();
			}
		}
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
				for (Iterator<String> i = sections.keySet().iterator(); i.hasNext();) {
					String section = (String) i.next();
					if (section.startsWith("Profile")) { //$NON-NLS-1$
						Map<String, String> properties = (Map<String, String>) sections.get(section);
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
				JSDebugPlugin.log(MessageFormat.format(
						"Reading '{0}' fails", profilesIni.getAbsolutePath()), e); //$NON-NLS-1$
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
}
