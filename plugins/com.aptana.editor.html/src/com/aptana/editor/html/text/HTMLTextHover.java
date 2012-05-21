/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.text;

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextHoverExtension2;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.ui.IEditorPart;

import com.aptana.editor.common.contentassist.CommonTextHover;
import com.aptana.editor.common.hover.CustomBrowserInformationControl;
import com.aptana.editor.html.contentassist.HTMLIndexQueryHelper;
import com.aptana.editor.html.contentassist.HTMLModelFormatter;
import com.aptana.editor.html.contentassist.model.BaseElement;
import com.aptana.editor.html.parsing.ast.HTMLElementNode;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseNodeAttribute;
import com.aptana.parsing.lexer.IRange;
import com.aptana.parsing.lexer.Range;

/**
 * @author cwilliams
 */
public class HTMLTextHover extends CommonTextHover implements ITextHover, ITextHoverExtension2
{

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.ITextHoverExtension2#getHoverInfo2(org.eclipse.jface.text.ITextViewer,
	 * org.eclipse.jface.text.IRegion)
	 */
	public Object getHoverInfo2(ITextViewer textViewer, IRegion hoverRegion)
	{
		IParseNode activeNode = getActiveNode(textViewer, hoverRegion.getOffset());
		if (!(activeNode instanceof HTMLElementNode))
		{
			return null;
		}

		BaseElement element = getMatchingElement(textViewer, hoverRegion, (HTMLElementNode) activeNode);
		// To avoid duplicating work, we generate the header and documentation together here
		// and then getHeader and getDocumentation just return the values.
		if (element != null)
		{
			return getHoverInfo(element, isBrowserControlAvailable(textViewer), null, getEditor(textViewer),
					hoverRegion);
		}

		return null;

	}

	protected BaseElement getMatchingElement(ITextViewer textViewer, IRegion hoverRegion, HTMLElementNode node)
	{
		// Hover over start tag?
		IRange elementNameRange = node.getNameNode().getNameRange();
		if (!elementNameRange.contains(hoverRegion.getOffset()))
		{
			return null;
		}

		// Check if we're hovering over the tag/element name
		try
		{
			IDocument doc = textViewer.getDocument();
			String openTagContent = doc.get(elementNameRange.getStartingOffset(), elementNameRange.getLength());
			int index = openTagContent.indexOf(node.getName());
			IRange tagNameRange = new Range(elementNameRange.getStartingOffset() + index,
					elementNameRange.getStartingOffset() + index + node.getName().length());

			if (tagNameRange.contains(hoverRegion.getOffset()))
			{
				return new HTMLIndexQueryHelper().getElement(node.getElementName().toLowerCase());
			}
		}
		catch (BadLocationException e)
		{
			// ignore
		}

		// Are we hovering over an attribute?
		IParseNodeAttribute attr = node.getAttributeAtOffset(hoverRegion.getOffset());
		if (attr == null)
		{
			return null;
		}

		// Are we over the attribute name?
		IRange nameRange = attr.getNameRange();
		if (nameRange != null && nameRange.contains(hoverRegion.getOffset()))
		{
			return new HTMLIndexQueryHelper().getAttribute(node.getElementName().toLowerCase(), attr.getName());
		}

		// We must be hovering over empty space, or attribute value, show no hover
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.hover.AbstractDocumentationHover#getHeader(java.lang.Object,
	 * org.eclipse.ui.IEditorPart, org.eclipse.jface.text.IRegion)
	 */
	@Override
	public String getHeader(Object element, IEditorPart editorPart, IRegion hoverRegion)
	{
		if (!(element instanceof BaseElement))
		{
			return null;
		}
		return HTMLModelFormatter.TEXT_HOVER.getHeader((BaseElement) element);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.hover.AbstractDocumentationHover#getDocumentation(java.lang.Object,
	 * org.eclipse.ui.IEditorPart, org.eclipse.jface.text.IRegion)
	 */
	@Override
	public String getDocumentation(Object element, IEditorPart editorPart, IRegion hoverRegion)
	{
		if (!(element instanceof BaseElement))
		{
			return null;
		}
		return HTMLModelFormatter.TEXT_HOVER.getDocumentation((BaseElement) element);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.ITextHover#getHoverRegion(org.eclipse.jface.text.ITextViewer, int)
	 */
	public IRegion getHoverRegion(ITextViewer textViewer, int offset)
	{
		IParseNode activeNode = getActiveNode(textViewer, offset);
		if (!(activeNode instanceof HTMLElementNode))
		{
			return null;
		}

		// Are we over a start tag?
		HTMLElementNode node = (HTMLElementNode) activeNode;
		IRange elementNameRange = node.getNameNode().getNameRange();
		if (!elementNameRange.contains(offset))
		{
			return null;
		}

		// Check if we're hovering over the tag/element name
		try
		{
			IDocument doc = textViewer.getDocument();
			String openTagContent = doc.get(elementNameRange.getStartingOffset(), elementNameRange.getLength());
			int start = elementNameRange.getStartingOffset() + openTagContent.indexOf(node.getName());
			IRange tagNameRange = new Range(start, start + node.getName().length() - 1);

			if (tagNameRange.contains(offset))
			{
				return new Region(tagNameRange.getStartingOffset(), tagNameRange.getLength());
			}
		}
		catch (BadLocationException e)
		{
			// ignore
		}

		// Are we hovering over an attribute?
		IParseNodeAttribute attr = node.getAttributeAtOffset(offset);
		if (attr == null)
		{
			return null;
		}

		// Are we over the name of the attribute?
		IRange nameRange = attr.getNameRange();
		if (nameRange != null && nameRange.contains(offset))
		{
			return new Region(nameRange.getStartingOffset(), nameRange.getLength());
		}

		return null;
	}

	@Override
	public void populateToolbarActions(ToolBarManager tbm, CustomBrowserInformationControl iControl)
	{
		// do nothing for now
	}

}
