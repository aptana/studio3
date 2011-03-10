/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
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

	private static final String VALUE_PATTERN = "^(.[^=]*)=(.*)$"; //$NON-NLS-1$
	private static final String SECTION_PATTERN = "^\\x5B(.*)\\x5D$"; //$NON-NLS-1$
	
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
	 * Find location of user's default(current) Firefox profile.
	 * 
	 * @return IPath
	 */
	public static IPath findDefaultProfileLocation() {
		String[] locations = (String[]) LOCATIONS.get(Platform.getOS());
		if (locations != null) {
			for (int i = 0; i < locations.length; ++i) {
				String location = PlatformUtil.expandEnvironmentStrings(locations[i]);
				File dir = new File(location);
				if (!dir.isDirectory()) {
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
					if (profile.isDirectory()) {
						CorePlugin.log(MessageFormat.format("Default profile was found at {0}", profile.toString())); //$NON-NLS-1$
						return Path.fromOSString(profile.getAbsolutePath());
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
	protected static File[] readProfiles(File dir) {
		List<File> list = new ArrayList<File>();
		File profilesIni = new File(dir, "profiles.ini"); //$NON-NLS-1$
		if (profilesIni.exists()) {
			LineNumberReader r = null;
			try {
				r = new LineNumberReader(new FileReader(profilesIni));
				String line;
				Map<String, Map<String, String>> sections = new LinkedHashMap<String, Map<String, String>>();
				Map<String, String> last = null;
				Pattern sectionPattern = Pattern.compile(SECTION_PATTERN);
				Pattern valuePattern = Pattern.compile(VALUE_PATTERN);
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
				for (Entry<String, Map<String, String>> entry : sections.entrySet()) {
					if (entry.getKey().startsWith("Profile")) { //$NON-NLS-1$
						Map<String, String> properties = entry.getValue();
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
	 * Get version for the specified Firefox extension ID
	 * 
	 * @param extensionID
	 * @param profileDir
	 * @return
	 */
	public static String getExtensionVersion(String extensionID, IPath profileDir) {
		IPath dir = profileDir.append("extensions").append(extensionID); //$NON-NLS-1$
		if (dir.toFile().exists()) {
			File installRdf = dir.append("install.rdf").toFile(); //$NON-NLS-1$
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

	/**
	 * Install an extension using linked method
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
		IPath base = CorePlugin.getDefault().getStateLocation().addTrailingSeparator();
		boolean result = installExtension(extensionURL, extensionID, base.toFile());
		if (result) {
			String linkedPath = base.append(extensionID).toOSString();
			FileOutputStream out = null;
			try {
				out = new FileOutputStream(file);
				out.write(linkedPath.getBytes());
			} catch (IOException e) {
				CorePlugin.log(e);
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
	 * Install an extension directly into profile location
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
			CorePlugin.log(e);
			if (file != null) {
				if (!file.delete()) {
					file.deleteOnExit();
				}
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
			ZipUtil.extract(new ZipFile(file), dir);
		} catch (IOException e) {
			CorePlugin.log(e);
			return false;
		} finally {
			if (!file.delete()) {
				file.deleteOnExit();
			}
		}

		return true;
	}

}
