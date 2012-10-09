/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.text.hyperlink;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
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

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.IDebugScopes;
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
			{
				return null;
			}

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
			if (StringUtil.isEmpty(value))
			{
				return null;
			}
			// Now try and resolve the value as a URI...
			IEditorPart part = (IEditorPart) getAdapter(IEditorPart.class);
			if (part == null)
			{
				return null;
			}
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
				IdeLog.logInfo(CommonEditorPlugin.getDefault(),
						MessageFormat.format("Failed to resolve ''{0}'' as hyperlink url", value), e, //$NON-NLS-1$
						IDebugScopes.DEBUG);
			}
			return null;
		}

		List<IHyperlink> ours = new ArrayList<IHyperlink>(result.length);
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
			catch (URISyntaxException use)
			{
				IdeLog.logInfo(
						CommonEditorPlugin.getDefault(),
						MessageFormat.format("Failed to resolve URI: {0}", hyperlink.getURLString()), use, IDebugScopes.DEBUG); //$NON-NLS-1$
			}
			catch (MalformedURLException mue)
			{
				IdeLog.logInfo(
						CommonEditorPlugin.getDefault(),
						MessageFormat.format("Failed to resolve URI: {0}", hyperlink.getURLString()), mue, IDebugScopes.DEBUG); //$NON-NLS-1$
			}
		}
		if (ours.isEmpty())
		{
			return null;
		}
		return ours.toArray(new IHyperlink[ours.size()]);
	}
}
