/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITypedRegion;

import com.aptana.editor.html.core.preferences.IPreferenceConstants;
import com.aptana.editor.html.parsing.HTMLParseState;
import com.aptana.editor.xml.OpenTagCloser;

public class HTMLOpenTagCloser extends OpenTagCloser
{

	public HTMLOpenTagCloser(ITextViewer textViewer)
	{
		super(textViewer);
	}

	protected boolean isEmptyTagType(IDocument doc, String tagName)
	{
		return HTMLParseState.isEmptyTagType(tagName);
	}

	protected boolean validPartition(ITypedRegion partition)
	{
		return partition.getType().startsWith(HTMLSourceConfiguration.PREFIX);
	}

	@Override
	protected boolean isAutoInsertEnabled()
	{
		return HTMLPlugin.getDefault().getPreferenceStore().getBoolean(IPreferenceConstants.HTML_AUTO_CLOSE_TAG_PAIRS);
	}
}
