package com.aptana.editor.html.text.hyperlink;

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

public class CSSClassHyperlinkDetector extends IndexQueryingHyperlinkDetector
{
	public CSSClassHyperlinkDetector()
	{
		super();
	}

	private static final Pattern CSS_CLASS_PATTERN = Pattern.compile("class=[\"'Ò]([_a-zA-Z0-9-]+)[\"'Ó]"); //$NON-NLS-1$

	@Override
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
			List<QueryResult> results = index.query(new String[] { CSSIndexConstants.CLASS }, cssClass,
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
				String[] documents = result.getDocuments();
				if (documents == null || documents.length <= 0)
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
			Activator.logError(e.getMessage(), e);
		}
		if (hyperlinks.isEmpty())
		{
			return null;
		}
		return hyperlinks.toArray(new IHyperlink[hyperlinks.size()]);
	}
}
