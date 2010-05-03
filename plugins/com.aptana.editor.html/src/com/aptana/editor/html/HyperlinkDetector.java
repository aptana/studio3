package com.aptana.editor.html;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
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

import com.aptana.editor.common.outline.IPathResolver;
import com.aptana.editor.common.outline.PathResolverProvider;

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
			try
			{
				lineInfo = document.getLineInformationOfOffset(offset);
				line = document.get(lineInfo.getOffset(), lineInfo.getLength());
			}
			catch (BadLocationException ex)
			{
				return null;
			}
			int relativeOffset = region.getOffset() - lineInfo.getOffset();
			int afterIndex = line.indexOf('"', relativeOffset);
			String prefix = line.substring(0, relativeOffset);
			int beforeIndex = prefix.lastIndexOf('"');
			String value = line.substring(beforeIndex + 1, afterIndex);
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
					IRegion hyperLinkRegion = new Region(lineInfo.getOffset() + beforeIndex + 1, value.length());
					return new IHyperlink[] { new URIHyperlink(hyperLinkRegion, uri) };
				}
			}
			catch (Exception e)
			{
				log(e);
			}
			return null;
		}

		for (int i = 0; i < result.length; i++)
		{
			// Wrap in our own hyperlink impl, so we can try to open file in editor
			URLHyperlink hyperlink = (URLHyperlink) result[i];
			try
			{
				// TODO Don't wrap if we can't even open an editor on the file (i.e. have no editor type associated)
				result[i] = new URIHyperlink(hyperlink);
			}
			catch (URISyntaxException e)
			{
				log(e);
			}
		}
		return result;
	}

	private static void log(Exception e)
	{
		Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
	}

}
