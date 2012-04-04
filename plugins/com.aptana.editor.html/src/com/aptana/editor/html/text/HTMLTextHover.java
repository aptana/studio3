/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.text;

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextHoverExtension2;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.ui.IEditorPart;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.contentassist.CommonTextHover;
import com.aptana.editor.common.hover.CustomBrowserInformationControl;
import com.aptana.editor.html.contentassist.HTMLIndexQueryHelper;
import com.aptana.editor.html.contentassist.HTMLModelFormatter;
import com.aptana.editor.html.contentassist.model.ElementElement;
import com.aptana.editor.html.parsing.ast.HTMLElementNode;
import com.aptana.parsing.ast.IParseNode;

/**
 * @author cwilliams
 */
public class HTMLTextHover extends CommonTextHover implements ITextHover, ITextHoverExtension2
{

	private String fDocs;
	private String fHeader;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.ITextHoverExtension2#getHoverInfo2(org.eclipse.jface.text.ITextViewer,
	 * org.eclipse.jface.text.IRegion)
	 */
	public Object getHoverInfo2(ITextViewer textViewer, IRegion hoverRegion)
	{
		try
		{
			IParseNode activeNode = getActiveNode(textViewer, hoverRegion.getOffset());
			if (!(activeNode instanceof HTMLElementNode))
			{
				return null;
			}
			HTMLElementNode node = (HTMLElementNode) activeNode;
			ElementElement element = new HTMLIndexQueryHelper().getElement(node.getElementName());

			// To avoid duplicating work, we generate the header and documentation together here
			// and then getHeader and getDocumentation just return the values.
			if (element != null)
			{
				fHeader = HTMLModelFormatter.TEXT_HOVER.getHeader(element);
				fDocs = HTMLModelFormatter.TEXT_HOVER.getDocumentation(element);
				AbstractThemeableEditor editorPart = getEditor(textViewer);
				return getHoverInfo(activeNode, isBrowserControlAvailable(textViewer), null, editorPart, hoverRegion);
			}

			return null;
		}
		finally
		{
			fHeader = null;
			fDocs = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.hover.AbstractDocumentationHover#getHeader(java.lang.Object,
	 * org.eclipse.ui.IEditorPart, org.eclipse.jface.text.IRegion)
	 */
	@Override
	public String getHeader(Object element, IEditorPart editorPart, IRegion hoverRegion)
	{
		return fHeader;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.hover.AbstractDocumentationHover#getDocumentation(java.lang.Object,
	 * org.eclipse.ui.IEditorPart, org.eclipse.jface.text.IRegion)
	 */
	@Override
	public String getDocumentation(Object element, IEditorPart editorPart, IRegion hoverRegion)
	{
		return fDocs;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.ITextHover#getHoverRegion(org.eclipse.jface.text.ITextViewer, int)
	 */
	public IRegion getHoverRegion(ITextViewer textViewer, int offset)
	{
		IParseNode activeNode = this.getActiveNode(textViewer, offset);
		if (activeNode instanceof HTMLElementNode)
		{
			HTMLElementNode node = (HTMLElementNode) activeNode;
			// TODO Be able to distinguish between hover over element name, attribute name, attribute value...
			return new Region(node.getNameNode().getNameRange().getStartingOffset(), node.getNameNode().getNameRange().getLength() );
		}

		return new Region(offset, 0);
	}

	@Override
	public void populateToolbarActions(ToolBarManager tbm, CustomBrowserInformationControl iControl)
	{
		// do nothing for now
	}

}
