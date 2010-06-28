package com.aptana.core.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

import com.aptana.core.CorePlugin;

public class ResourceUtil
{
	private static final String UNC_PREFIX = "//"; //$NON-NLS-1$
	private static final String SCHEME_FILE = "file"; //$NON-NLS-1$

	private ResourceUtil()
	{
	}

	/**
	 * resourcePathToFile
	 * 
	 * @param url
	 * @return
	 */
	static public File resourcePathToFile(URL url)
	{
		File result = null;

		if (url != null)
		{
			try
			{
				URL fileURL = FileLocator.toFileURL(url);
				URI fileURI = toURI(fileURL); // Use Eclipse to get around Java 1.5 bug on Windows
				result = new File(fileURI);
			}
			catch (IOException e)
			{
				String message = MessageFormat.format(
					Messages.ResourceUtils_URL_To_File_URL_Conversion_Error,
					new Object[] { url }
				);
				
				CorePlugin.logError(message, e);
			}
			catch (URISyntaxException e)
			{
				String message = MessageFormat.format(
					Messages.ResourceUtils_File_URL_To_URI_Conversion_Error,
					new Object [] { url }
				);
				
				CorePlugin.logError(message, e);
			}
		}

		return result;
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
		File file = resourcePathToFile(url);
		
		if (file != null)
		{
			result = file.getAbsolutePath();
		}

		return result;
	}

	/**
	 * Returns the URL as a URI. This method will handle URLs that are not properly encoded (for example they contain
	 * unencoded space characters).
	 * 
	 * @param url
	 *            The URL to convert into a URI
	 * @return A URI representing the given URL
	 */
	public static URI toURI(URL url) throws URISyntaxException
	{
		// URL behaves differently across platforms so for file: URLs we parse from string form
		if (SCHEME_FILE.equals(url.getProtocol()))
		{
			String pathString = url.toExternalForm().substring(5);
			
			// ensure there is a leading slash to handle common malformed URLs such as file:c:/tmp
			if (pathString.indexOf('/') != 0)
			{
				pathString = '/' + pathString;
			}
			else if (pathString.startsWith(UNC_PREFIX) && !pathString.startsWith(UNC_PREFIX, 2))
			{
				// URL encodes UNC path with two slashes, but URI uses four (see bug 207103)
				pathString = ensureUNCPath(pathString);
			}
			
			return new URI(SCHEME_FILE, null, pathString, null);
		}
		try
		{
			return new URI(url.toExternalForm());
		}
		catch (URISyntaxException e)
		{
			// try multi-argument URI constructor to perform encoding
			return new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
		}
	}

	/**
	 * Ensures the given path string starts with exactly four leading slashes.
	 */
	private static String ensureUNCPath(String path)
	{
		int len = path.length();
		StringBuffer result = new StringBuffer(len);
		
		for (int i = 0; i < 4; i++)
		{
			if (i >= len || path.charAt(i) != '/')
			{
				result.append('/');
			}
		}
		
		result.append(path);
		
		return result.toString();
	}
	
	/**
	 * Returns the value that is currently stored for the line separator. In case an IProject reference is given, the
	 * returned value will be the one that was, potentially, set specifically to that project.
	 * 
	 * @param project
	 *            An {@link IProject} reference. Can be null.
	 * @return the currently stored line separator
	 */
	public static String getLineSeparatorValue(IProject project)
	{
		IScopeContext scope;
		if (project != null)
		{
			scope = new ProjectScope(project);
		}
		else
		{
			scope = new InstanceScope();
		}

		IScopeContext[] scopeContext = new IScopeContext[] { scope };
		IEclipsePreferences node = scopeContext[0].getNode(Platform.PI_RUNTIME);
		return node.get(Platform.PREF_LINE_SEPARATOR, System.getProperty("line.separator")); //$NON-NLS-1$
	}
}
