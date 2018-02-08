/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
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

import com.aptana.core.util.StringUtil;
import com.aptana.css.core.parsing.ast.CSSSimpleSelectorNode;
import com.aptana.editor.common.contentassist.CommonTextHover;
import com.aptana.editor.common.hover.CustomBrowserInformationControl;
import com.aptana.editor.html.contentassist.HTMLIndexQueryHelper;
import com.aptana.editor.html.contentassist.HTMLModelFormatter;
import com.aptana.editor.html.contentassist.model.ElementElement;
import com.aptana.parsing.ast.IParseNode;

/**
 * @author cwilliams
 */
public class CSSElementSelectorHover extends CommonTextHover implements ITextHover, ITextHoverExtension2
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
			if (!(activeNode instanceof CSSSimpleSelectorNode))
			{
				return null;
			}
			CSSSimpleSelectorNode node = (CSSSimpleSelectorNode) activeNode;
			ElementElement element = new HTMLIndexQueryHelper().getElement(node.getTypeSelector().toLowerCase());

			// To avoid duplicating work, we generate the header and documentation together here
			// and then getHeader and getDocumentation just return the values.
			if (element != null)
			{
				fHeader = HTMLModelFormatter.TEXT_HOVER.getHeader(element);
				fDocs = HTMLModelFormatter.TEXT_HOVER.getDocumentation(element);
				return getHoverInfo(element, isBrowserControlAvailable(textViewer), null, getEditor(textViewer),
						hoverRegion);
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
		IParseNode activeNode = getActiveNode(textViewer, offset);
		if (activeNode instanceof CSSSimpleSelectorNode)
		{
			CSSSimpleSelectorNode node = (CSSSimpleSelectorNode) activeNode;
			// Verify that this is an HTML element selector
			String typeSelector = node.getTypeSelector();
			if (!StringUtil.isEmpty(typeSelector))
			{
				ElementElement element = new HTMLIndexQueryHelper().getElement(typeSelector.toLowerCase());
				if (element != null)
				{
					return new Region(node.getStartingOffset(), node.getLength());
				}
			}
		}

		return null;
	}

	@Override
	public void populateToolbarActions(ToolBarManager tbm, CustomBrowserInformationControl iControl)
	{
		// do nothing for now
	}
}
