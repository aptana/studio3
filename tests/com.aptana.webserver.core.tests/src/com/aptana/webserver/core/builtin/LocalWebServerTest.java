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

package com.aptana.webserver.core.builtin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.runtime.CoreException;

import junit.framework.TestCase;

/**
 * @author Max Stepanov
 * 
 */
public class LocalWebServerTest extends TestCase {
	private static final String PAGE_CONTENTS = "<html><head><title>Test</title></head><body><h1>Test Page</h1></body>";

	public void testBasicGet() throws IOException, CoreException {
		File dir = File.createTempFile(getClass().getSimpleName(), "temp");
		assertTrue(dir.delete());
		assertTrue(dir.mkdir());
		File file = new File(dir, "index.html");
		assertTrue(file.createNewFile());
		OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(file));
		w.write(PAGE_CONTENTS);
		w.close();

		LocalWebServer webServer = null;
		try {
			webServer = new LocalWebServer(EFS.getLocalFileSystem().fromLocalFile(dir).toURI());
			URL url = webServer.getConfiguration().getBaseURL();
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setAllowUserInteraction(false);
			connection.setInstanceFollowRedirects(true);
			connection.setUseCaches(false);
			InputStreamReader in = new InputStreamReader((InputStream) connection.getContent());
			StringBuffer sb = new StringBuffer();
			int n;
			char[] cbuf = new char[1024];
			while ((n = in.read(cbuf)) > 0) {
				sb.append(new String(cbuf, 0, n));
			}
			in.close();
			assertEquals(PAGE_CONTENTS, sb.toString());
		} finally {
			if (webServer != null) {
				webServer.dispose();
			}
		}
	}
}
