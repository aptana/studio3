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

import java.net.URI;
import java.text.MessageFormat;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IFindReplaceTarget;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import com.aptana.editor.common.CommonEditorPlugin;

/**
 * Opens an editor on the given URI and searches for the first occurrence of searchString.
 * 
 * @author cwilliams
 */
public class EditorSearchHyperlink implements IHyperlink
{

	private IRegion region;
	private URI document;
	private String searchString;

	public EditorSearchHyperlink(IRegion region, String searchString, URI document)
	{
		this.region = region;
		this.searchString = searchString;
		this.document = document;
	}

	public IRegion getHyperlinkRegion()
	{
		return region;
	}

	public String getTypeLabel()
	{
		return null;
	}

	public String getHyperlinkText()
	{
		return MessageFormat.format("Open in {0}", document.toString());
	}

	public void open()
	{
		try
		{
			final IFileStore store = EFS.getStore(document);
			// Now open an editor to this file (and highlight the occurrence if possible)
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IEditorPart part = IDE.openEditorOnFileStore(page, store);
			// Now select the occurrence if we can
			IFindReplaceTarget target = (IFindReplaceTarget) part.getAdapter(IFindReplaceTarget.class);
			if (target != null && target.canPerformFind())
			{
				target.findAndSelect(0, searchString, true, true, true);
			}
		}
		catch (CoreException e)
		{
			CommonEditorPlugin.logError(e);
		}
		catch (Exception e)
		{
			CommonEditorPlugin.logError(e.getMessage(), e);
		}
	}

}
