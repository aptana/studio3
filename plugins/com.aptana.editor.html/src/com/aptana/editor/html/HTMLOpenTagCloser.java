package com.aptana.editor.html;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.swt.events.VerifyEvent;

import com.aptana.editor.html.parsing.HTMLParseState;
import com.aptana.editor.html.preferences.IPreferenceContants;
import com.aptana.editor.xml.OpenTagCloser;

public class HTMLOpenTagCloser extends OpenTagCloser
{

	public HTMLOpenTagCloser(ITextViewer textViewer)
	{
		super(textViewer);
	}

	protected boolean isEmptyTagType(IDocument doc, String tagName)
	{
		HTMLParseState state = new HTMLParseState();
		state.setEditState(doc.get(), null, 0, 0);
		return state.isEmptyTagType(tagName);
	}

	protected boolean shouldAutoClose(IDocument document, int offset, VerifyEvent event)
	{
		// FIX for when we're inserting '>' as last character of file, backtrack an additional character to see if it's
		// HTML
		int docLength = document.getLength();
		if (offset == docLength)
		{
			offset -= 1;
		}

		// Only auto-close in HTML
		ITypedRegion[] typedRegions = document.getDocumentPartitioner().computePartitioning(offset, 0);
		if (typedRegions != null && typedRegions.length > 0)
		{
			if (typedRegions[0].getType().startsWith(HTMLSourceConfiguration.PREFIX))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	protected boolean isAutoInsertEnabled()
	{
		return HTMLPlugin.getDefault().getPreferenceStore().getBoolean(IPreferenceContants.HTML_AUTO_CLOSE_TAG_PAIRS);
	}
}
