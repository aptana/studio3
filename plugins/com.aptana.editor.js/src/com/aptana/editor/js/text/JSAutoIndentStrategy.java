/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.text;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

import com.aptana.editor.common.text.RubyRegexpAutoIndentStrategy;
import com.aptana.editor.js.contentassist.ParseUtil;

/**
 * Amends the default auto indent strategy with JavaScript-specific changes
 */
public class JSAutoIndentStrategy extends RubyRegexpAutoIndentStrategy
{

	public JSAutoIndentStrategy(String contentType, SourceViewerConfiguration configuration,
			ISourceViewer sourceViewer, IPreferenceStore prefStore)
	{
		super(contentType, configuration, sourceViewer, prefStore);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.editor.common.text.CommonAutoIndentStrategy#getAutoIndentAfterNewLine(org.eclipse.jface.text.IDocument
	 * , org.eclipse.jface.text.DocumentCommand)
	 */
	@Override
	protected String getAutoIndentAfterNewLine(IDocument d, DocumentCommand c)
	{
		return super.getAutoIndentAfterNewLine(d, c);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.text.CommonAutoIndentStrategy#getAdditionalComments(java.lang.StringBuilder)
	 */
	@Override
	protected List<String> getAdditionalComments(IDocument d, DocumentCommand c)
	{
		return createJSDocTags(d, c.offset);
	}

	/**
	 * Creates the Javadoc tags for newly inserted comments.
	 * 
	 * @param document
	 *            the document
	 * @param offset
	 *            the offset into the document where we're editing
	 */
	private List<String> createJSDocTags(IDocument document, int offset)
	{
		List<String> params = ParseUtil.getFunctionParameters(document, offset);
		ArrayList<String> result = new ArrayList<String>();

		for (int i = 0; i < params.size(); i++)
		{
			result.add("@param {Object} " + params.get(i)); //$NON-NLS-1$
		}

		return result;
	}

}
