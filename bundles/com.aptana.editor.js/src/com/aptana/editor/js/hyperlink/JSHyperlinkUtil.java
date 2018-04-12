/**
 * Appcelerator Titanium Studio
 * Copyright (c) 2014 by Appcelerator, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */

package com.aptana.editor.js.hyperlink;

import java.net.URI;

import com.aptana.core.util.URIUtil;

/**
 * @author pinnamuri
 */
public class JSHyperlinkUtil
{

	/**
	 * Format the document to a relative path within the project, including the project name in the result. If the
	 * document is not within the project path, then it is returned unchanged
	 * 
	 * @param projectURI
	 *            The URI to the project containing the file in the editor associated with this instance
	 * @param document
	 *            A string representation of the document name to trim.
	 * @return Returns a trimmed or untouched version of the document parameter
	 */
	public static String getDocumentDisplayName(URI projectURI, String document)
	{
		String prefix = (projectURI != null) ? URIUtil.decodeURI(projectURI.toString()) : null;

		// back up one segment so we include the project name in the document
		if (prefix != null && prefix.length() > 2)
		{
			int index = prefix.lastIndexOf('/', prefix.length() - 2);

			if (index != -1 && index > 0)
			{
				prefix = prefix.substring(0, index - 1);
			}
		}

		String result = URIUtil.decodeURI(document);

		if (prefix != null && result.startsWith(prefix))
		{
			result = result.substring(prefix.length() + 1);
		}

		return result;
	}

	/**
	 * Determine if the document is within the specified project
	 * 
	 * @param projectURI
	 * @param document
	 * @return
	 */
	public static boolean isInCurrentProject(URI projectURI, String document)
	{
		String prefix = (projectURI != null) ? URIUtil.decodeURI(projectURI.toString()) : null;
		boolean result = false;

		String path = URIUtil.decodeURI(document);

		if (prefix != null && path.startsWith(prefix))
		{
			result = true;
		}

		return result;
	}

}
