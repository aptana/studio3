package com.aptana.editor.common.text.hyperlink;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;

public abstract class IndexQueryingHyperlinkDetector extends AbstractHyperlinkDetector
{

	protected Index getIndex()
	{
		// Now try and resolve the value as a URI...
		IEditorPart part = (IEditorPart) getAdapter(IEditorPart.class);
		IEditorInput input = part.getEditorInput();
		// TODO What about IFileStoreEditorInputs? IURIEditorInputs?
		if (input instanceof IFileEditorInput)
		{
			IFile file = ((IFileEditorInput) input).getFile();
			IProject project = file.getProject();
			return IndexManager.getInstance().getIndex(project.getLocationURI());
		}
		return null;
	}
}
