package com.aptana.editor.common;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;

public class CommonDocumentProvider extends TextFileDocumentProvider
{

	@Override
	public void connect(Object element) throws CoreException
	{
		super.connect(element);

		// TODO Handle IPathEditorInput or IURI...
		if (element instanceof IFileEditorInput)
		{
			IFileEditorInput input = (IFileEditorInput) element;
			IFile file = input.getFile();
			String fileName = file.getName();
			// Now we need to do some matching against the filenames/extensions that bundles have registered.
			IDocument document = getDocument(element);
			if (document != null)
			{
				DocumentContentTypeManager.getInstance().setDocumentContentType(document, getDefaultContentType(), fileName);
			}
		}
	}

	protected String getDefaultContentType()
	{
		return "source"; //$NON-NLS-1$
	}

}
