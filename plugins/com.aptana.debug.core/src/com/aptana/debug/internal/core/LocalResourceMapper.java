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
package com.aptana.debug.internal.core;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.aptana.core.util.URLEncoder;
import com.aptana.debug.core.DebugCorePlugin;

/**
 * @author Max Stepanov
 * 
 */
public class LocalResourceMapper {

	/**
	 * 
	 * @author Max Stepanov
	 * 
	 */
	private class Entry {
		private URI baseURI;
		private File rootDir;
		/* -- */
		private String baseUrlString;
		private URI rootDirURI;

		/**
		 * Entry
		 * 
		 * @param baseURL
		 * @param rootDir
		 * @throws MalformedURLException
		 * @throws URISyntaxException
		 */
		protected Entry(URL baseURL, File rootDir) throws MalformedURLException, URISyntaxException {
			baseUrlString = URLEncoder.encode(baseURL).toExternalForm();
			if (!baseUrlString.endsWith("/")) { //$NON-NLS-1$
				baseUrlString = baseUrlString + "/"; //$NON-NLS-1$
			}
			this.baseURI = new URI(baseUrlString);
			setRootDir(rootDir);
		}

		protected void setRootDir(File rootDir) {
			this.rootDir = rootDir;
			this.rootDirURI = rootDir.toURI();
		}

		/**
		 * resolveServerURL
		 * 
		 * @param url
		 * @return File
		 */
		protected File resolveServerURL(URL url) {
			String string = url.toExternalForm();
			if (string.startsWith(baseUrlString)) {
				string = string.substring(baseUrlString.length());
				string = Util.decodeURL(string);
				File file = new File(rootDir, string);
				if (file.exists() && file.isFile()) {
					return file;
				}
			}
			return null;
		}

		/**
		 * resolveLocalURI
		 * 
		 * @param uri
		 * @return URI
		 */
		protected URI resolveLocalURI(URI uri) {
			URI relative = rootDirURI.relativize(uri);
			if (relative != uri) {
				return baseURI.resolve(relative);
			}
			return null;
		}

	}

	private List<Entry> entries = new ArrayList<Entry>();

	/**
	 * addMapping
	 * 
	 * @param baseURL
	 * @param rootDir
	 * @throws MalformedURLException
	 */
	public void addMapping(URL baseURL, File rootDir) throws MalformedURLException {
		DebugCorePlugin.log("LocalResourceMapper: " + baseURL.toExternalForm() + " = " + rootDir); //$NON-NLS-1$ //$NON-NLS-2$
		try {
			Entry newEntry = new Entry(baseURL, rootDir);
			int index = 0;
			for (Iterator<Entry> i = entries.iterator(); i.hasNext(); ++index) {
				Entry entry = (Entry) i.next();
				int cmp = newEntry.baseUrlString.compareTo(entry.baseUrlString);
				if (cmp > 0) {
					entries.add(index, newEntry);
					return;
				} else if (cmp == 0) {
					entry.setRootDir(rootDir);
					return;
				}
			}
			entries.add(0, newEntry);
		} catch (URISyntaxException e) {
			DebugCorePlugin.log(e);
		}
	}

	/**
	 * resolveServerURL
	 * 
	 * @param url
	 * @return File
	 */
	public File resolveServerURL(URL url) {
		// Remove query/fragment parts from URL
		try {
			url = new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getPath());
		} catch (MalformedURLException e) {
			DebugCorePlugin.log(e);
		}
		File file = null;
		for (Iterator<Entry> i = entries.iterator(); file == null && i.hasNext();) {
			file = ((Entry) i.next()).resolveServerURL(url);
		}
		DebugCorePlugin.log("LocalResourceMapper: " + url.toExternalForm() + " -> " + file); //$NON-NLS-1$ //$NON-NLS-2$
		return file;
	}

	/**
	 * resolveLocalURI
	 * 
	 * @param localURI
	 * @return URI
	 */
	public URI resolveLocalURI(URI localURI) {
		if ("file".equals(localURI.getScheme())) { //$NON-NLS-1$
			URI remoteURI = null;
			for (Iterator<Entry> i = entries.iterator(); remoteURI == null && i.hasNext();) {
				remoteURI = ((Entry) i.next()).resolveLocalURI(localURI);
			}
			DebugCorePlugin.log("LocalResourceMapper: " + localURI + " -> " + remoteURI); //$NON-NLS-1$ //$NON-NLS-2$
			if (remoteURI != null) {
				return remoteURI;
			}
		}
		return localURI;
	}

}
