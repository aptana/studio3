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

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.text.hyperlink.EditorSearchHyperlink;
import com.aptana.editor.common.text.hyperlink.IndexQueryingHyperlinkDetector;
import com.aptana.editor.css.Activator;
import com.aptana.editor.css.ICSSConstants;
import com.aptana.editor.css.contentassist.index.CSSIndexConstants;
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
					.getScopeAtOffset(doc, region.getOffset());
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
			List<QueryResult> results = index.query(new String[] { CSSIndexConstants.IDENTIFIER }, htmlId,
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
			Activator.logError(e.getMessage(), e);
		}
		if (hyperlinks.isEmpty())
		{
			return null;
		}
		return hyperlinks.toArray(new IHyperlink[hyperlinks.size()]);
	}
}
