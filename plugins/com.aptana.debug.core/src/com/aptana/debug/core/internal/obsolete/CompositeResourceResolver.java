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

package com.aptana.debug.core.internal.obsolete;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Max Stepanov
 * 
 */
public class CompositeResourceResolver /*implements IHttpResourceResolver*/ {

	/**
	 * Entry
	 */
	private class Entry {
		String path;
		File dir;

		Entry(String path, File dir) {
			this.path = path;
			this.dir = dir;
		}
	}

	private ArrayList<Entry> paths = new ArrayList<Entry>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.aptana.ide.server.resolvers.IHttpResourceResolver#getResource(com.aptana.ide.server.http.RequestLineParser)
	 */
	/*
	public IHttpResource getResource(RequestLineParser requestLine) throws HttpServerException {
		String request = requestLine.getUri();
		for (Iterator i = paths.iterator(); i.hasNext();) {
			Entry entry = (Entry) i.next();
			String path = entry.path;
			if (request.startsWith(path)) {
				String subpath = request.substring(path.length());
				File dir = entry.dir;
				if (dir.exists()) {
					File file = new File(dir, subpath.replace('/', File.separatorChar));
					if (file.exists() && file.isFile()) {
						return new FileHttpResource(file);
					}
				}
			}
		}
		return null;
	}
	*/

	public void addPath(String path, File dir) {
		if (path.length() == 0 || path.charAt(0) != '/') {
			path = '/' + path;
		}
		if (path.length() > 1 && path.charAt(path.length() - 1) == '/') {
			path = path.substring(0, path.length() - 1);
		}

		int index = 0;
		for (Iterator i = paths.iterator(); i.hasNext(); ++index) {
			Entry entry = (Entry) i.next();
			int cmp = path.compareTo(entry.path);
			if (cmp > 0) {
				paths.add(index, new Entry(path, dir));
				return;
			} else if (cmp == 0) {
				entry.dir = dir;
				return;
			}
		}
		paths.add(new Entry(path, dir));
	}

}
