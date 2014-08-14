/**
 * Appcelerator Titanium Studio
 * Copyright (c) 2014 by Appcelerator, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */

package com.aptana.editor.common.text.hyperlink;

import java.io.File;
import java.net.URI;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import com.aptana.core.logging.IdeLog;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.util.EditorUtil;

/**
 * The hyperlink contains information about the exact location to be selected in the target document.
 * 
 * @author pinnamuri
 */
public class EditorTargetRegionHyperlink implements IHyperlink
{

	private final IRegion hyperlinkRegion;
	private final URI targetDocument;
	private final IRegion targetRegion;
	private String typeLabel;

	public EditorTargetRegionHyperlink(IRegion hyperlinkRegion, URI targetDocument, String typeLabel,
			IRegion targetRegion)
	{
		this.hyperlinkRegion = hyperlinkRegion;
		this.targetDocument = targetDocument;
		this.typeLabel = typeLabel;
		this.targetRegion = targetRegion;
	}

	public IRegion getHyperlinkRegion()
	{
		return hyperlinkRegion;
	}

	public String getTypeLabel()
	{
		return typeLabel;
	}

	public String getHyperlinkText()
	{
		return null;
	}

	public void open()
	{
		if (targetDocument == null || targetRegion == null)
		{
			return;
		}
		try
		{
			File file = new File(targetDocument);
			IEditorPart editor = EditorUtil.openInEditor(file);
			if (editor instanceof AbstractTextEditor)
			{
				((AbstractTextEditor) editor).selectAndReveal(targetRegion.getOffset(), targetRegion.getLength());
			}
		}
		catch (Exception e)
		{
			IdeLog.logError(CommonEditorPlugin.getDefault(), e);
		}
	}

}
