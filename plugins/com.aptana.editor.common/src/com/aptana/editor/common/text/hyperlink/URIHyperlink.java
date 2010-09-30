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

import com.aptana.core.util.URLEncoder;
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

	public URIHyperlink(URLHyperlink hyperlink) throws URISyntaxException, MalformedURLException
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
