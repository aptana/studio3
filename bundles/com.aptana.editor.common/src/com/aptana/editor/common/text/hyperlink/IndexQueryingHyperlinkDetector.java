/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.text.hyperlink;

import java.net.URI;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IURIEditorInput;

import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;
import com.aptana.index.core.IndexPlugin;

public abstract class IndexQueryingHyperlinkDetector extends AbstractHyperlinkDetector
{

	protected Index getIndex()
	{
		// Now try and resolve the value as a URI...
		IEditorInput input = getEditorInput();
		// TODO What about IFileStoreEditorInputs? IURIEditorInputs?
		if (input instanceof IFileEditorInput)
		{
			IFile file = ((IFileEditorInput) input).getFile();
			IProject project = file.getProject();
			return getIndexManager().getIndex(project.getLocationURI());
		}
		return null;
	}

	protected IndexManager getIndexManager()
	{
		return IndexPlugin.getDefault().getIndexManager();
	}

	protected IEditorPart getEditor()
	{
		return (IEditorPart) getAdapter(IEditorPart.class);
	}

	protected IEditorInput getEditorInput()
	{
		IEditorPart part = getEditor();
		if (part == null)
		{
			return null;
		}
		return part.getEditorInput();
	}

	protected URI getURI()
	{
		// Now try and resolve the value as a URI...
		IEditorInput input = getEditorInput();
		if (input instanceof IURIEditorInput)
		{
			return ((IURIEditorInput) input).getURI();
		}
		if (input instanceof IFileEditorInput)
		{
			IFile file = ((IFileEditorInput) input).getFile();
			return file.getLocationURI();
		}
		return null;
	}
}
