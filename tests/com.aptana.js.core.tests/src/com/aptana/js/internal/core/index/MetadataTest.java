/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.index;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.junit.Test;

import com.aptana.js.core.JSCorePlugin;

public class MetadataTest
{
	@Test
	public void testDOM0()
	{
		this.loadMetadata("/metadata/dom_0.xml");
	}

	@Test
	public void testDOM2()
	{
		this.loadMetadata("/metadata/dom_2.xml");
	}

	@Test
	public void testDOM3()
	{
		this.loadMetadata("/metadata/dom_3.xml");
	}

	@Test
	public void testDOM5()
	{
		this.loadMetadata("/metadata/dom_5.xml");
	}

	@Test
	public void testJSCore()
	{
		this.loadMetadata("/metadata/js_core.xml");
	}

	/**
	 * loadMetadata
	 * 
	 * @param resource
	 */
	protected void loadMetadata(String resource)
	{
		URL url = FileLocator.find(JSCorePlugin.getDefault().getBundle(), new Path(resource), null);

		assertNotNull(url);

		InputStream stream = null;

		try
		{
			JSMetadataReader reader = new JSMetadataReader();

			stream = url.openStream();

			reader.loadXML(stream, url.toString());
		}
		catch (IOException e)
		{
			fail(e.getMessage());
		}
		catch (Throwable t)
		{
			fail(t.getMessage());
		}
		finally
		{
			if (stream != null)
			{
				try
				{
					stream.close();
				}
				catch (IOException e)
				{
				}
			}
		}
	}
}
