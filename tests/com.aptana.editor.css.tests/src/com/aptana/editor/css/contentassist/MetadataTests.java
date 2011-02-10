/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.contentassist;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import junit.framework.TestCase;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;

import com.aptana.editor.css.CSSPlugin;
import com.aptana.editor.css.contentassist.index.CSSMetadataReader;

public class MetadataTests extends TestCase
{
	/**
	 * testCSSMetadata
	 */
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
		URL url = FileLocator.find(CSSPlugin.getDefault().getBundle(), new Path(resource), null);

		assertNotNull(url);
		
		InputStream stream = null;

		try
		{
			CSSMetadataReader reader = new CSSMetadataReader();
			
			stream = url.openStream();

			reader.loadXML(stream);
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
