package com.aptana.scripting;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.URIUtil;

import com.aptana.scripting.model.Messages;

public class ResourceUtils
{
	private ResourceUtils()
	{
	}
	
	/**
	 * resourcePathToString
	 * 
	 * @param url
	 * @return
	 */
	static public String resourcePathToString(URL url)
	{
		String result = null;
		
		try
		{
			URL fileURL = FileLocator.toFileURL(url);
			URI fileURI = URIUtil.toURI(fileURL);	// Use Eclipse to get around Java 1.5 bug on Windows
			File file = new File(fileURI);
			
			result = file.getAbsolutePath();
		}
		catch (IOException e)
		{
			String message = MessageFormat.format(
					Messages.BundleManager_Cannot_Locate_Built_Ins_Directory,
					new Object[] { url.toString() }
			);
			
			Activator.logError(message, e);
		}
		catch (URISyntaxException e)
		{
			String message = MessageFormat.format(
					Messages.BundleManager_Malformed_Built_Ins_URI,
					new Object[] { url.toString() }
			);
			
			Activator.logError(message, e);
		}
		
		return result;
	}
}
