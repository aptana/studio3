/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.text.hyperlink;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;

import com.aptana.core.logging.IdeLog;
import com.aptana.css.core.ICSSConstants;
import com.aptana.css.core.index.ICSSIndexConstants;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.text.hyperlink.EditorSearchHyperlink;
import com.aptana.editor.common.text.hyperlink.IndexQueryingHyperlinkDetector;
import com.aptana.editor.css.CSSPlugin;
import com.aptana.index.core.Index;
import com.aptana.index.core.QueryResult;
import com.aptana.index.core.SearchPattern;
import com.aptana.scope.IScopeSelector;
import com.aptana.scope.ScopeSelector;

/**
 * Links IDs in CSS to usages in HTML.
 * 
 * @author cwilliams
 */
public class HTMLIDHyperlinkDetector extends IndexQueryingHyperlinkDetector
{

	private static final Pattern HTML_ID = Pattern.compile("#[_a-zA-Z0-9-]+"); //$NON-NLS-1$

	public HTMLIDHyperlinkDetector()
	{
		super();
	}

	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks)
	{
		List<IHyperlink> hyperlinks = new ArrayList<IHyperlink>();
		try
		{
			IDocument doc = textViewer.getDocument();
			String scope = CommonEditorPlugin.getDefault().getDocumentScopeManager()
					.getScopeAtOffset(textViewer, region.getOffset());
			// Match scope against source.css
			IScopeSelector selector = new ScopeSelector(ICSSConstants.CSS_SCOPE);
			if (!selector.matches(scope))
			{
				return null;
			}

			IRegion lineRegion = doc.getLineInformationOfOffset(region.getOffset());
			String line = doc.get(lineRegion.getOffset(), lineRegion.getLength());
			Matcher m = HTML_ID.matcher(line);
			if (!m.find())
			{
				return null;
			}
			Index index = getIndex();
			if (index == null)
			{
				return null;
			}

			String htmlId = m.group().substring(1);
			List<QueryResult> results = index.query(new String[] { ICSSIndexConstants.IDENTIFIER }, htmlId,
					SearchPattern.EXACT_MATCH | SearchPattern.CASE_SENSITIVE);
			if (results == null || results.isEmpty())
			{
				return null;
			}
			int start = m.start() + lineRegion.getOffset();
			int length = m.end() - m.start();
			IRegion linkRegion = new Region(start, length);
			for (QueryResult result : results)
			{
				Set<String> documents = result.getDocuments();
				if (documents == null || documents.isEmpty())
				{
					continue;
				}

				for (String filepath : documents)
				{
					// FIXME Don't suggest current file/occurrence
					// FIXME Don't suggest usages in embedded CSS inside HTML
					hyperlinks.add(new EditorSearchHyperlink(linkRegion, htmlId, new URI(filepath)));
					if (!canShowMultipleHyperlinks)
					{
						return new IHyperlink[] { hyperlinks.get(0) };
					}
				}
			}
		}
		catch (Exception e)
		{
			IdeLog.logError(CSSPlugin.getDefault(), e);
		}
		if (hyperlinks.isEmpty())
		{
			return null;
		}
		return hyperlinks.toArray(new IHyperlink[hyperlinks.size()]);
	}
}
