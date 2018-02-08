/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;

public abstract class CommonDocumentProvider extends TextFileDocumentProvider
{

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.editors.text.TextFileDocumentProvider#connect(java.lang.Object)
	 */
	@Override
	public void connect(Object element) throws CoreException
	{
		super.connect(element);

		IDocument document = getDocument(element);

		if (document != null)
		{
			String fileName = null;

			if (element instanceof IFileEditorInput)
			{
				IFileEditorInput input = (IFileEditorInput) element;
				IFile file = input.getFile();

				fileName = file.getName();
			}
			else if (element instanceof IPathEditorInput)
			{
				IPathEditorInput input = (IPathEditorInput) element;

				fileName = input.getPath().lastSegment();
			}
			else if (element instanceof IURIEditorInput)
			{
				IURIEditorInput input = (IURIEditorInput) element;

				fileName = new Path(input.getURI().getPath()).lastSegment();
			}

			CommonEditorPlugin.getDefault().getDocumentScopeManager()
					.setDocumentScope(document, getDefaultContentType(fileName), fileName);
		}
	}

	@Override
	protected IAnnotationModel createAnnotationModel(IFile file)
	{
		return new CommonAnnotationModelFactory().createAnnotationModel(file.getFullPath());
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.editors.text.TextFileDocumentProvider#disconnect(java.lang.Object)
	 */
	@Override
	public void disconnect(Object element)
	{
		FileInfo fileInfo = getFileInfo(element);

		if (fileInfo != null && fileInfo.fCount == 1)
		{
			IDocument document = getDocument(element);

			if (document != null)
			{
				IDocumentPartitioner partitioner = document.getDocumentPartitioner();

				if (partitioner != null)
				{
					partitioner.disconnect();
					document.setDocumentPartitioner(null);
				}
			}
		}

		super.disconnect(element);
	}

	/**
	 * getDefaultContentType
	 * 
	 * @param filename
	 * @return
	 */
	protected abstract String getDefaultContentType(String filename);
}
