/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.contentassist;

import org.eclipse.jface.text.IAutoIndentStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IEventConsumer;
import org.eclipse.jface.text.IFindReplaceTarget;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextInputListener;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.IUndoManager;
import org.eclipse.jface.text.IViewportListener;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;

@SuppressWarnings("deprecation")
public class TestTextViewer implements ITextViewer
{
	private IDocument _document;

	public TestTextViewer(IDocument document)
	{
		this._document = document;
	}

	public void activatePlugins()
	{
	}

	public void addTextInputListener(ITextInputListener listener)
	{
	}

	public void addTextListener(ITextListener listener)
	{
	}

	public void addViewportListener(IViewportListener listener)
	{
	}

	public void changeTextPresentation(TextPresentation presentation, boolean controlRedraw)
	{
	}

	public int getBottomIndex()
	{
		return 0;
	}

	public int getBottomIndexEndOffset()
	{
		return 0;
	}

	public IDocument getDocument()
	{
		return this._document;
	}

	public IFindReplaceTarget getFindReplaceTarget()
	{
		return null;
	}

	public Point getSelectedRange()
	{
		return null;
	}

	public ISelectionProvider getSelectionProvider()
	{
		return null;
	}

	public ITextOperationTarget getTextOperationTarget()
	{
		return null;
	}

	public StyledText getTextWidget()
	{
		return null;
	}

	public int getTopIndex()
	{
		return 0;
	}

	public int getTopIndexStartOffset()
	{
		return 0;
	}

	public int getTopInset()
	{
		return 0;
	}

	public IRegion getVisibleRegion()
	{
		return null;
	}

	public void invalidateTextPresentation()
	{
	}

	public boolean isEditable()
	{
		return false;
	}

	public boolean overlapsWithVisibleRegion(int offset, int length)
	{
		return false;
	}

	public void removeTextInputListener(ITextInputListener listener)
	{
	}

	public void removeTextListener(ITextListener listener)
	{
	}

	public void removeViewportListener(IViewportListener listener)
	{
	}

	public void resetPlugins()
	{
	}

	public void resetVisibleRegion()
	{
	}

	public void revealRange(int offset, int length)
	{
	}

	public void setAutoIndentStrategy(IAutoIndentStrategy strategy, String contentType)
	{
	}

	public void setDefaultPrefixes(String[] defaultPrefixes, String contentType)
	{
	}

	public void setDocument(IDocument document)
	{
	}

	public void setDocument(IDocument document, int modelRangeOffset, int modelRangeLength)
	{
	}

	public void setEditable(boolean editable)
	{
	}

	public void setEventConsumer(IEventConsumer consumer)
	{
	}

	public void setIndentPrefixes(String[] indentPrefixes, String contentType)
	{
	}

	public void setSelectedRange(int offset, int length)
	{
	}

	public void setTextColor(Color color)
	{
	}

	public void setTextColor(Color color, int offset, int length, boolean controlRedraw)
	{
	}

	public void setTextDoubleClickStrategy(ITextDoubleClickStrategy strategy, String contentType)
	{
	}

	public void setTextHover(ITextHover textViewerHover, String contentType)
	{
	}

	public void setTopIndex(int index)
	{
	}

	public void setUndoManager(IUndoManager undoManager)
	{
	}

	public void setVisibleRegion(int offset, int length)
	{
	}
}
