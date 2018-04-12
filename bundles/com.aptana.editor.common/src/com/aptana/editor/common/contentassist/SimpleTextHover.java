/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.contentassist;

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.IEditorPart;

import com.aptana.editor.common.hover.CustomBrowserInformationControl;

/**
 * A simple text hover that displays whatever it got in into constructor.
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public class SimpleTextHover extends CommonTextHover
{

	private String info;
	private String header;

	/**
	 * Constructs a new simple text hover with an info content and an optional header.
	 * 
	 * @param info
	 *            The info to display.
	 * @param header
	 *            The header to display (may be <code>null</code>)
	 */
	public SimpleTextHover(String info, String header)
	{
		this.info = info;
		this.header = header;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.ITextHover#getHoverRegion(org.eclipse.jface.text.ITextViewer, int)
	 */
	public IRegion getHoverRegion(ITextViewer textViewer, int offset)
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.ITextHoverExtension2#getHoverInfo2(org.eclipse.jface.text.ITextViewer,
	 * org.eclipse.jface.text.IRegion)
	 */
	public Object getHoverInfo2(ITextViewer textViewer, IRegion hoverRegion)
	{
		return getHoverInfo(info, isBrowserControlAvailable(textViewer), null, getEditor(), hoverRegion);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.hover.AbstractDocumentationHover#getHeader(java.lang.Object,
	 * org.eclipse.ui.IEditorPart, org.eclipse.jface.text.IRegion)
	 */
	@Override
	public String getHeader(Object element, IEditorPart editorPart, IRegion hoverRegion)
	{
		return header;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.hover.AbstractDocumentationHover#getDocumentation(java.lang.Object,
	 * org.eclipse.ui.IEditorPart, org.eclipse.jface.text.IRegion)
	 */
	@Override
	public String getDocumentation(Object element, IEditorPart editorPart, IRegion hoverRegion)
	{
		if (element == null)
		{
			return null;
		}
		return element.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.hover.AbstractDocumentationHover#populateToolbarActions(org.eclipse.jface.action.
	 * ToolBarManager, com.aptana.editor.common.hover.CustomBrowserInformationControl)
	 */
	@Override
	public void populateToolbarActions(ToolBarManager tbm, CustomBrowserInformationControl iControl)
	{
		// Nothing
	}
}
