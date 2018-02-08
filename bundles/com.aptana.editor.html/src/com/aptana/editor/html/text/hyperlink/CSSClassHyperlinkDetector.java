/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.text.hyperlink;

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
import com.aptana.css.core.index.ICSSIndexConstants;
import com.aptana.editor.common.text.hyperlink.EditorSearchHyperlink;
import com.aptana.editor.common.text.hyperlink.IndexQueryingHyperlinkDetector;
import com.aptana.editor.html.HTMLPlugin;
import com.aptana.index.core.Index;
import com.aptana.index.core.QueryResult;
import com.aptana.index.core.SearchPattern;

public class CSSClassHyperlinkDetector extends IndexQueryingHyperlinkDetector
{

	private static final Pattern CSS_CLASS_PATTERN = Pattern.compile("class=[\"'�]([_a-zA-Z0-9-]+)[\"'�]"); //$NON-NLS-1$

	public CSSClassHyperlinkDetector()
	{
	}

	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks)
	{
		List<IHyperlink> hyperlinks = new ArrayList<IHyperlink>();
		try
		{
			IDocument doc = textViewer.getDocument();
			IRegion lineRegion = doc.getLineInformationOfOffset(region.getOffset());

			String line = doc.get(lineRegion.getOffset(), lineRegion.getLength());
			Matcher m = CSS_CLASS_PATTERN.matcher(line);
			if (!m.find())
			{
				return null;
			}
			Index index = getIndex();
			if (index == null)
			{
				return null;
			}

			// FIXME What if it uses multiple classes?
			String cssClass = m.group(1);
			// TODO Make this smarter, find the best match (i.e. div.class or #some-id.class)?
			List<QueryResult> results = index.query(new String[] { ICSSIndexConstants.CLASS }, cssClass,
					SearchPattern.EXACT_MATCH | SearchPattern.CASE_SENSITIVE);
			if (results == null || results.isEmpty())
			{
				return null;
			}
			int start = m.start(1) + lineRegion.getOffset();
			int length = m.end(1) - m.start(1);
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
					hyperlinks.add(new EditorSearchHyperlink(linkRegion, "." + cssClass, new URI(filepath))); //$NON-NLS-1$
					if (!canShowMultipleHyperlinks)
					{
						return new IHyperlink[] { hyperlinks.get(0) };
					}
				}
			}
		}
		catch (Exception e)
		{
			IdeLog.logError(HTMLPlugin.getDefault(), e);
		}
		if (hyperlinks.isEmpty())
		{
			return null;
		}
		return hyperlinks.toArray(new IHyperlink[hyperlinks.size()]);
	}
}
