/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.text;

import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

import com.aptana.core.IMap;
import com.aptana.core.util.CollectionsUtil;
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
	 * @see com.aptana.editor.common.text.CommonAutoIndentStrategy#getAdditionalComments(java.lang.StringBuilder)
	 */
	@Override
	protected List<String> getAdditionalComments(IDocument d, DocumentCommand c)
	{
		List<String> params = ParseUtil.getFunctionParameters(d, c.offset);
		return CollectionsUtil.map(params, new IMap<String, String>()
		{
			public String map(String item)
			{
				return "@param {Object} " + item; //$NON-NLS-1$
			}
		});
	}
}
