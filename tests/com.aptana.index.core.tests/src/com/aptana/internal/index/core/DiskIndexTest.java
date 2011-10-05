package com.aptana.internal.index.core;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import junit.framework.TestCase;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

import com.aptana.core.util.ResourceUtil;

public class DiskIndexTest extends TestCase
{

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
