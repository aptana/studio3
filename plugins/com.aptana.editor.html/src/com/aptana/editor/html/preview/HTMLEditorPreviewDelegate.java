/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.html.preview;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

import com.aptana.core.util.URLEncoder;
import com.aptana.editor.html.contentassist.index.HTMLIndexConstants;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;
import com.aptana.index.core.QueryResult;
import com.aptana.preview.IEditorPreviewDelegate;

/**
 * @author Michael Xia
 * @author Max Stepanov
 *
 */
public class HTMLEditorPreviewDelegate implements IEditorPreviewDelegate {

	private IEditorPart targetEditorPart;
	
	/* (non-Javadoc)
	 * @see com.aptana.preview.IEditorPreviewDelegate#init(org.eclipse.ui.IEditorPart)
	 */
	public void init(IEditorPart targetEditorPart) {
		this.targetEditorPart = targetEditorPart;
	}

	/* (non-Javadoc)
	 * @see com.aptana.preview.IEditorPreviewDelegate#dispose()
	 */
	public void dispose() {
		targetEditorPart = null;
	}

	/* (non-Javadoc)
	 * @see com.aptana.preview.IEditorPreviewDelegate#isLinked(java.net.URI)
	 */
	public boolean isLinked(URI uri) {
		// checks if this HTML file includes the source the changed editor is referencing (JS or CSS file)
		IEditorInput targetEditorInput = targetEditorPart.getEditorInput();
		if (targetEditorInput instanceof IFileEditorInput) {
			IFile htmlFile = ((IFileEditorInput) targetEditorInput).getFile();
			Index index = IndexManager.getInstance().getIndex(htmlFile.getProject().getLocationURI());
			List<QueryResult> queryResults = null;
			try {
				queryResults = index.query(new String[] {
						HTMLIndexConstants.RESOURCE_CSS,
						HTMLIndexConstants.RESOURCE_JS }, null, 0);
			} catch (IOException e) {
				return false;
			}
			if (queryResults != null) {
				String includedFileToCheck = uri.toString();
				String includedFile;
				String htmlFileToCheck = URLEncoder.encode(htmlFile.getLocation().toPortableString(), null, null);
				for (QueryResult result : queryResults) {
					includedFile = result.getWord();
					if (includedFileToCheck.equals(includedFile)) {
						Set<String> documents = result.getDocuments();
						for (String document : documents) {
							if (document.endsWith(htmlFileToCheck)) {
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
}
