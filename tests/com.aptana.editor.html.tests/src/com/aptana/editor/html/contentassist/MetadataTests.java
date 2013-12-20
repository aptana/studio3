/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.contentassist;

import org.junit.Test;
import static org.junit.Assert.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import junit.framework.TestCase;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;

import com.aptana.editor.html.HTMLPlugin;
import com.aptana.editor.html.contentassist.index.HTMLMetadataReader;

public class MetadataTests
{
	/**
	 * testHTMLMetadata
	 */
	@Test
	public void testHTMLMetadata()
	{
		this.loadMetadata("/metadata/html_metadata.xml");
	}

	/**
	 * loadMetadata
	 * 
	 * @param resource
	 */
	protected void loadMetadata(String resource)
	{
		URL url = FileLocator.find(HTMLPlugin.getDefault().getBundle(), new Path(resource), null);

		assertNotNull(url);

		InputStream stream = null;

		try
		{
			HTMLMetadataReader reader = new HTMLMetadataReader();

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
