/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable assignmentInCondition
// $codepro.audit.disable closeWhereCreated
// $codepro.audit.disable closeInFinally

package com.aptana.webserver.internal.core.builtin;

import org.junit.Test;
import static org.junit.Assert.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import junit.framework.TestCase;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * @author Max Stepanov
 */
public class LocalWebServerTest
{

	private static final String PAGE_CONTENTS = "<html><head><title>Test</title></head><body><h1>Test Page</h1></body>"; //$NON-NLS-1$

	@Test
	public void testBasicGet() throws IOException, CoreException
	{
		File dir = File.createTempFile(getClass().getSimpleName(), "temp"); //$NON-NLS-1$
		assertTrue(dir.delete());
		assertTrue(dir.mkdir());
		File file = new File(dir, "index.html"); //$NON-NLS-1$
		assertTrue(file.createNewFile());
		OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(file));
		w.write(PAGE_CONTENTS);
		w.close();

		LocalWebServer webServer = null;
		try
		{
			webServer = new LocalWebServer(EFS.getLocalFileSystem().fromLocalFile(dir).toURI());
			URL url = webServer.getBaseURL();
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setAllowUserInteraction(false);
			connection.setInstanceFollowRedirects(true);
			connection.setUseCaches(false);
			InputStreamReader in = new InputStreamReader((InputStream) connection.getContent());
			StringBuffer sb = new StringBuffer();
			int n;
			char[] cbuf = new char[1024];
			while ((n = in.read(cbuf)) > 0)
			{
				sb.append(new String(cbuf, 0, n));
			}
			in.close();
			assertEquals(PAGE_CONTENTS, sb.toString());
		}
		finally
		{
			if (webServer != null)
			{
				webServer.stop(true, new NullProgressMonitor());
			}
		}
	}
}
