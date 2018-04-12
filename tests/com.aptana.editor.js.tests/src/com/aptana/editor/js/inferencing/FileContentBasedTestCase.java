/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.inferencing;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;

import com.aptana.core.util.IOUtil;
import com.aptana.core.util.ResourceUtil;
import com.aptana.editor.js.JSPlugin;

abstract class FileContentBasedTestCase
{
	/**
	 * getContent
	 * 
	 * @param file
	 * @return
	 */
	protected String getContent(File file)
	{
		String result = "";

		try
		{
			FileInputStream input = new FileInputStream(file);

			result = IOUtil.read(input);
		}
		catch (IOException e)
		{
			fail(e.getMessage());
		}

		return result;
	}

	/**
	 * getFile
	 * 
	 * @param path
	 * @return
	 */
	protected File getFile(IPath path)
	{
		File result = null;

		try
		{
			URL url = FileLocator.find(JSPlugin.getDefault().getBundle(), path, null);
			URL fileURL = FileLocator.toFileURL(url);
			URI fileURI = ResourceUtil.toURI(fileURL);

			result = new File(fileURI);
		}
		catch (IOException e)
		{
			fail(e.getMessage());
		}
		catch (URISyntaxException e)
		{
			fail(e.getMessage());
		}

		assertNotNull(result);
		assertTrue(result.exists());

		return result;
	}
}
