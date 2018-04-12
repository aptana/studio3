/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable variableDeclaredInLoop

package com.aptana.terminal.internal.emulator;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.tm.internal.terminal.textcanvas.ILinelRenderer;
import org.eclipse.tm.internal.terminal.textcanvas.ITextCanvasModel;

import com.aptana.terminal.internal.hyperlink.HyperlinkManager;

/**
 * @author Max Stepanov
 * @author Chris Williams
 */
public class TextCanvas extends org.eclipse.tm.internal.terminal.textcanvas.TextCanvas
{

	private HyperlinkManager linkManager;

	/**
	 * @param parent
	 * @param model
	 * @param style
	 * @param cellRenderer
	 */
	public TextCanvas(Composite parent, ITextCanvasModel model, int style, ILinelRenderer cellRenderer,
			HyperlinkManager linkManager)
	{
		super(parent, model, style, cellRenderer);
		this.linkManager = linkManager;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.textcanvas.TextCanvas#findHyperlink(org.eclipse.swt.graphics.Point)
	 */
	@Override
	protected IHyperlink findHyperlink(Point cellCoords)
	{
		if (cellCoords == null)
		{
			return null;
		}
		IHyperlink[] links = linkManager.searchLineForHyperlinks(cellCoords.y);
		if (links == null)
		{
			return null;
		}
		for (int i = 0; i < links.length; i++)
		{
			IHyperlink link = links[i];
			IRegion region = link.getHyperlinkRegion();

			int col = region.getOffset();
			int endCol = region.getOffset() + region.getLength() - 1;
			// clicked between start and end col
			if (cellCoords.x <= endCol && cellCoords.x >= col)
			{
				return link;
			}
		}
		return null;
	}
}
