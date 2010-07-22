package com.aptana.editor.css.text.hyperlink;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;

import com.aptana.editor.common.text.hyperlink.EditorSearchHyperlink;
import com.aptana.editor.common.text.hyperlink.IndexQueryingHyperlinkDetector;
import com.aptana.editor.css.Activator;
import com.aptana.editor.css.contentassist.index.CSSIndexConstants;
import com.aptana.index.core.Index;
import com.aptana.index.core.QueryResult;
import com.aptana.index.core.SearchPattern;

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

	@Override
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks)
	{
		List<IHyperlink> hyperlinks = new ArrayList<IHyperlink>();
		try
		{
			// TODO Only look in CSS and style tags in HTML type files. Maybe we can grab scope and bail out if we're not in CSS?
			IDocument doc = textViewer.getDocument();
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
				String[] documents = result.getDocuments();
				if (documents == null || documents.length <= 0)
				{
					continue;
				}

				for (String filepath : documents)
				{
					// FIXME Don't suggest current file/occurrence
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
