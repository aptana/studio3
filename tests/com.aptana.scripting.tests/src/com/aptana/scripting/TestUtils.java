package com.aptana.scripting;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import junit.framework.TestCase;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;

import com.aptana.util.IOUtil;

public class TestUtils extends TestCase
{
	private TestUtils()
	{
	}

	/**
	 * getContent
	 * 
	 * @param file
	 * @return
	 */
	public static String getContent(File file)
	{
		String result = "";

		try
		{
			FileInputStream input = new FileInputStream(file);

			result = IOUtil.read(input);
		}
		catch (IOException e)
		{
		}

		return result;
	}

	/**
	 * getFile
	 * 
	 * @param path
	 * @return
	 */
	public static File getFile(IPath path)
	{
		File result = null;

		try
		{
			URL url = FileLocator.find(Activator.getDefault().getBundle(), path, null);

			url = FileLocator.toFileURL(url);
			result = new File(url.toURI());
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
