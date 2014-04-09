/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.text.hyperlink;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
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

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.URLEncoder;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.IDebugScopes;

public class URIHyperlink extends URLHyperlink
{

	private URI uri;
	private boolean wrapped;

	URIHyperlink(IRegion region, URI uri)
	{
		super(region, uri.toString());
		this.uri = uri;
	}

	URIHyperlink(URLHyperlink hyperlink) throws URISyntaxException, MalformedURLException
	{
		this(hyperlink.getHyperlinkRegion(), URLEncoder.encode(new URL(hyperlink.getURLString())).toURI());
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
				// IFileStore store = EFS.getStore(uri);
				// if (store.getFileSystem() != EFS.getLocalFileSystem())
				// {
				// File file = store.toLocalFile(EFS.CACHE, new NullProgressMonitor());
				// uri = file.toURI();
				// }
				IDE.openEditor(page, uri, IEditorRegistry.SYSTEM_EXTERNAL_EDITOR_ID, true);
				return;
			}
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IDE.openEditor(page, uri, desc.getId(), true);
		}
		catch (Exception e)
		{
			IdeLog.logInfo(CommonEditorPlugin.getDefault(),
					MessageFormat.format("Unable to open the url ''{0}'' in an editor", uri), e, IDebugScopes.DEBUG); //$NON-NLS-1$
			if (wrapped)
			{
				super.open();
			}
		}
	}

	boolean hasAssociatedEditor()
	{
		return getEditorDescriptor() != null;
	}

	private IEditorDescriptor getEditorDescriptor()
	{
		IEditorRegistry editorReg = PlatformUI.getWorkbench().getEditorRegistry();
		if (uri.getPath() == null || uri.getPath().equals("/") || uri.getPath().trim().equals("")) //$NON-NLS-1$ //$NON-NLS-2$
		{
			return null;
		}
		IPath path = new Path(uri.getPath());
		return editorReg.getDefaultEditor(path.lastSegment());
	}

	public String getHyperlinkText()
	{
		return MessageFormat.format("Open {0} in editor", getURLString()); //$NON-NLS-1$
	}
}
