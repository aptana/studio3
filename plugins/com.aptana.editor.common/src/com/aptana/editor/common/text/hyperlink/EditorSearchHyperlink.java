/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.text.hyperlink;

import java.net.URI;
import java.text.MessageFormat;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.jface.text.IFindReplaceTarget;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import com.aptana.core.logging.IdeLog;
import com.aptana.editor.common.CommonEditorPlugin;

/**
 * Opens an editor on the given URI and searches for the first occurrence of searchString.
 * 
 * @author cwilliams
 */
public class EditorSearchHyperlink implements IHyperlink
{

	private final IRegion region;
	private final URI document;
	private final String searchString;
	private final boolean caseSensitive;
	private final boolean wholeWord;

	public EditorSearchHyperlink(IRegion region, String searchString, URI document)
	{
		this(region, searchString, document, true, true);
	}

	public EditorSearchHyperlink(IRegion region, String searchString, URI document, boolean caseSensitive,
			boolean wholeWord)
	{
		this.region = region;
		this.searchString = searchString;
		this.document = document;
		this.caseSensitive = caseSensitive;
		this.wholeWord = wholeWord;
	}

	public String getSearchString()
	{
		return searchString;
	}

	public boolean isCaseSensitive()
	{
		return caseSensitive;
	}

	public URI getURI()
	{
		return document;
	}

	public boolean isWholeWord()
	{
		return wholeWord;
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
		return MessageFormat.format("Open in {0}", document.toString()); //$NON-NLS-1$
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
				target.findAndSelect(0, searchString, true, caseSensitive, wholeWord);
			}
		}
		catch (Exception e)
		{
			IdeLog.logError(CommonEditorPlugin.getDefault(), e);
		}
	}
}
