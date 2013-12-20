/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.index;

import org.junit.Test;
import static org.junit.Assert.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import junit.framework.TestCase;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;

import com.aptana.js.core.JSCorePlugin;

public class MetadataTest
{
	/**
	 * testDOM0
	 */
	@Test
	public void testDOM0()
	{
		this.loadMetadata("/metadata/dom_0.xml");
	}

	/**
	 * testDOM2
	 */
	@Test
	public void testDOM2()
	{
		this.loadMetadata("/metadata/dom_2.xml");
	}

	/**
	 * testDOM3
	 */
	@Test
	public void testDOM3()
	{
		this.loadMetadata("/metadata/dom_3.xml");
	}

	/**
	 * testDOM5
	 */
	@Test
	public void testDOM5()
	{
		this.loadMetadata("/metadata/dom_5.xml");
	}

	/**
	 * testJSCore
	 */
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
