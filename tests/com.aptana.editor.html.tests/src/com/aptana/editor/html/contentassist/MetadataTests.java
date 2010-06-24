package com.aptana.editor.html.contentassist;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import junit.framework.TestCase;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;

import com.aptana.editor.html.Activator;
import com.aptana.editor.html.contentassist.index.HTMLMetadataReader;

public class MetadataTests extends TestCase
{
	/**
	 * testHTMLMetadata
	 */
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
		URL url = FileLocator.find(Activator.getDefault().getBundle(), new Path(resource), null);

		assertNotNull(url);
		
		InputStream stream = null;

		try
		{
			HTMLMetadataReader reader = new HTMLMetadataReader();
			
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
