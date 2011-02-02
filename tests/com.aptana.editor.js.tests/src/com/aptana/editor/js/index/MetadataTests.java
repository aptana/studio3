/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.index;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import junit.framework.TestCase;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;

import com.aptana.editor.js.JSPlugin;
import com.aptana.editor.js.contentassist.index.JSMetadataReader;


public class MetadataTests extends TestCase
{
	/**
	 * testDOM0
	 */
	public void testDOM0()
	{
		this.loadMetadata("/metadata/dom_0.xml");
	}
	
	/**
	 * testDOM2
	 */
	public void testDOM2()
	{
		this.loadMetadata("/metadata/dom_2.xml");
	}
	
	/**
	 * testDOM3
	 */
	public void testDOM3()
	{
		this.loadMetadata("/metadata/dom_3.xml");
	}
	
	/**
	 * testDOM5
	 */
	public void testDOM5()
	{
		this.loadMetadata("/metadata/dom_5.xml");
	}
	
	/**
	 * testJSCore
	 */
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
		URL url = FileLocator.find(JSPlugin.getDefault().getBundle(), new Path(resource), null);

		assertNotNull(url);
		
		InputStream stream = null;

		try
		{
			JSMetadataReader reader = new JSMetadataReader();
			
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
