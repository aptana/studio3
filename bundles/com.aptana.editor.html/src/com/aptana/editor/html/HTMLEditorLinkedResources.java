/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.html;

import java.net.URI;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

import com.aptana.core.util.URLEncoder;
import com.aptana.editor.common.IEditorLinkedResources;
import com.aptana.editor.html.contentassist.index.IHTMLIndexConstants;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;
import com.aptana.index.core.IndexPlugin;
import com.aptana.index.core.QueryResult;

/**
 * @author Michael Xia
 * @author Max Stepanov
 */
public class HTMLEditorLinkedResources implements IEditorLinkedResources
{

	private final IEditorPart editorPart;

	/**
	 * @param targetEditorPart
	 */
	public HTMLEditorLinkedResources(IEditorPart editorPart)
	{
		this.editorPart = editorPart;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.IEditorLinkedResources#hasReference(java.net.URI)
	 */
	public boolean hasReference(URI uri)
	{
		// checks if this HTML file includes the source the changed editor is
		// referencing (JS or CSS file)
		IEditorInput targetEditorInput = editorPart.getEditorInput();
		if (targetEditorInput instanceof IFileEditorInput)
		{
			IFile htmlFile = ((IFileEditorInput) targetEditorInput).getFile();
			Index index = getIndexManager().getIndex(htmlFile.getProject().getLocationURI());
			List<QueryResult> queryResults = index.query(new String[] { IHTMLIndexConstants.RESOURCE_CSS,
					IHTMLIndexConstants.RESOURCE_JS }, null, 0);
			if (queryResults != null)
			{
				String includedFileToCheck = uri.toString();
				String includedFile;
				String htmlFileToCheck = URLEncoder.encode(htmlFile.getLocation().toPortableString(), null, null);
				for (QueryResult result : queryResults)
				{
					includedFile = result.getWord();
					if (includedFileToCheck.equals(includedFile))
					{
						Set<String> documents = result.getDocuments();
						for (String document : documents)
						{
							if (document.endsWith(htmlFileToCheck))
							{
								return true;
							}
						}
						return false;
					}
				}
			}
		}
		return false;
	}

	protected IndexManager getIndexManager()
	{
		return IndexPlugin.getDefault().getIndexManager();
	}
}
