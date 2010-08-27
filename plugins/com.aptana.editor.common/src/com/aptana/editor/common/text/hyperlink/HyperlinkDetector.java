package com.aptana.editor.common.text.hyperlink;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.URLHyperlink;
import org.eclipse.jface.text.hyperlink.URLHyperlinkDetector;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.outline.PathResolverProvider;
import com.aptana.editor.common.resolver.IPathResolver;

public class HyperlinkDetector extends URLHyperlinkDetector
{

	/*
	 * @see org.eclipse.jface.text.hyperlink.IHyperlinkDetector#detectHyperlinks(org.eclipse.jface.text.ITextViewer,
	 * org.eclipse.jface.text.IRegion, boolean)
	 */
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks)
	{
		IHyperlink[] result = super.detectHyperlinks(textViewer, region, canShowMultipleHyperlinks);
		if (result == null)
		{
			IDocument document = textViewer.getDocument();
			int offset = region.getOffset();
			if (document == null)
				return null;

			// Assume it's a src attribute value, try and grab it...
			IRegion lineInfo;
			String line;
			String value;
			IRegion hyperLinkRegion = region;
			try
			{
				lineInfo = document.getLineInformationOfOffset(offset);
				line = document.get(lineInfo.getOffset(), lineInfo.getLength());
				value = document.get(region.getOffset(), region.getLength());
			}
			catch (BadLocationException ex)
			{
				return null;
			}
			int relativeOffset = region.getOffset() - lineInfo.getOffset();
			int afterIndex = line.indexOf('"', relativeOffset);
			if (afterIndex != -1)
			{
				String prefix = line.substring(0, relativeOffset);
				int beforeIndex = prefix.lastIndexOf('"');
				value = line.substring(beforeIndex + 1, afterIndex);
				hyperLinkRegion = new Region(lineInfo.getOffset() + beforeIndex + 1, value.length());
			}
			if (value == null || value.trim().length() == 0)
			{
				return null;
			}
			// Now try and resolve the value as a URI...
			IEditorPart part = (IEditorPart) getAdapter(IEditorPart.class);
			IEditorInput input = part.getEditorInput();
			IPathResolver resolver = PathResolverProvider.getResolver(input);
			try
			{
				URI uri = resolver.resolveURI(value);
				if (uri != null)
				{
					// Create a hyperlink to open in an editor
					return new IHyperlink[] { new URIHyperlink(hyperLinkRegion, uri) };
				}
			}
			catch (Exception e)
			{
				CommonEditorPlugin.logError(e);
			}
			return null;
		}

		List<IHyperlink> ours = new ArrayList<IHyperlink>();
		for (IHyperlink link : result)
		{
			// Wrap in our own hyperlink impl, so we can try to open file in editor
			URLHyperlink hyperlink = (URLHyperlink) link;
			try
			{
				URIHyperlink wrapped = new URIHyperlink(hyperlink);
				// Don't wrap if we can't even open an editor on the file (i.e. have no editor type associated)
				if (wrapped.hasAssociatedEditor())
				{
					ours.add(wrapped);
				}
			}
			catch (Exception e)
			{
				CommonEditorPlugin.logError(e);
			}
		}
		if (ours.isEmpty())
			return null;
		return ours.toArray(new IHyperlink[ours.size()]);
	}
}
