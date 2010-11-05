/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.html.preview;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IURIEditorInput;

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
	 * @see com.aptana.preview.IEditorPreviewDelegate#updatePreviewWhenChanged(org.eclipse.ui.IEditorPart)
	 */
	public boolean isEditorInputLinked(IEditorInput editorInput) {
		// checks if this HTML file includes the source the changed editor is referencing (JS or CSS file)
		IEditorInput targetEditorInput = targetEditorPart.getEditorInput();
		if (targetEditorInput instanceof IFileEditorInput && editorInput instanceof IURIEditorInput) {
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
				String includedFileToCheck = (((IURIEditorInput) editorInput).getURI()).toString();
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
