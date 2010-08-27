package com.aptana.editor.common;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;

public class CommonDocumentProvider extends TextFileDocumentProvider
{

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
			CommonEditorPlugin.getDefault().getDocumentScopeManager().setDocumentScope(document,
					getDefaultContentType(fileName), fileName);
		}
	}

	protected String getDefaultContentType(String filename)
	{
		return "source"; //$NON-NLS-1$
	}

}
