/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.internal.index.core;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.junit.Test;

import com.aptana.core.util.ResourceUtil;

@SuppressWarnings("nls")
public class DiskIndexTest
{

	@Test
	public void testAPSTUD3393() throws Exception
	{
		URL url = FileLocator.find(Platform.getBundle("com.aptana.index.core.tests"),
				Path.fromPortableString("files/apstud3393.index"), null);
		File file = ResourceUtil.resourcePathToFile(url);

		try
		{
			DiskIndex index = new DiskIndex(file.getAbsolutePath());
			index.initialize(true);
			fail("Expected an IOException!");
		}
		catch (IOException e)
		{
			assertTrue(true);
		}
		catch (NegativeArraySizeException e)
		{
			fail("Expected an IOException, so that we'd catch it up the stack and clean up the index. Instead we got a NegativeArraySizeException!");
		}
	}
}
