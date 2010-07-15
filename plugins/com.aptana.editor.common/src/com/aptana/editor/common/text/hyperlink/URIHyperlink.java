package com.aptana.editor.common.text.hyperlink;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.URLHyperlink;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import com.aptana.editor.common.CommonEditorPlugin;

public class URIHyperlink extends URLHyperlink
{

	private URI uri;
	private boolean wrapped;

	public URIHyperlink(IRegion region, URI uri)
	{
		super(region, uri.toString());
		this.uri = uri;
	}

	public URIHyperlink(URLHyperlink hyperlink) throws URISyntaxException
	{
		this(hyperlink.getHyperlinkRegion(), new URI(hyperlink.getURLString()));
		wrapped = true;
	}

	public void open()
	{
		// Open in an editor if we can!
		try
		{
			IEditorDescriptor desc = getEditorDescriptor();
			if (desc == null)
			{
				if (wrapped)
				{
					super.open();
					return;
				}
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
//				IFileStore store = EFS.getStore(uri);
//				if (store.getFileSystem() != EFS.getLocalFileSystem())
//				{
//					File file = store.toLocalFile(EFS.CACHE, new NullProgressMonitor());
//					uri = file.toURI();
//				}
				IDE.openEditor(page, uri, IEditorRegistry.SYSTEM_EXTERNAL_EDITOR_ID, true);
				return;
			}
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IDE.openEditor(page, uri, desc.getId(), true);
		}
		catch (Exception e)
		{
			CommonEditorPlugin.logError(e);
			if (wrapped)
				super.open();
		}
	}

	public boolean hasAssociatedEditor()
	{
		return getEditorDescriptor() != null;
	}

	protected IEditorDescriptor getEditorDescriptor()
	{
		IEditorRegistry editorReg = PlatformUI.getWorkbench().getEditorRegistry();
		if (uri.getPath() == null || uri.getPath().equals("/") || uri.getPath().trim().equals("")) //$NON-NLS-1$ //$NON-NLS-2$
			return null;
		IPath path = new Path(uri.getPath());
		return editorReg.getDefaultEditor(path.lastSegment());
	}

	public String getHyperlinkText()
	{
		return MessageFormat.format("Open {0} in editor", getURLString()); //$NON-NLS-1$
	}

}
