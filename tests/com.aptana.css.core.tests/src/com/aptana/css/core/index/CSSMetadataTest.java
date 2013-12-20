/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.index;

import org.junit.Test;
import static org.junit.Assert.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import junit.framework.TestCase;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;

import com.aptana.css.core.CSSCorePlugin;
import com.aptana.css.core.internal.index.CSSMetadataReader;

public class CSSMetadataTest
{
	/**
	 * testCSSMetadata
	 */
	@Test
	public void testCSSMetadata()
	{
		this.loadMetadata("/metadata/css_metadata.xml");
	}

	/**
	 * loadMetadata
	 * 
	 * @param resource
	 */
	protected void loadMetadata(String resource)
	{
		URL url = FileLocator.find(CSSCorePlugin.getDefault().getBundle(), new Path(resource), null);

		assertNotNull(url);

		InputStream stream = null;

		try
		{
			CSSMetadataReader reader = new CSSMetadataReader();

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
