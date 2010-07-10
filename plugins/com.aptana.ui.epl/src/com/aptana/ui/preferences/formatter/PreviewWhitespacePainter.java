package com.aptana.ui.preferences.formatter;

/***********************************************************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/cpl-v10.html Contributors: Sebastian Davids <sdavids@gmx.de> - initial API
 * and implementation
 **********************************************************************************************************************/

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IPaintPositionManager;
import org.eclipse.jface.text.IPainter;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

/**
 * Decorates the peer character if it is a whitespace. Clients instantiate and configure object of this class.
 * 
 * @since 3.0
 */
public final class PreviewWhitespacePainter implements IPainter, PaintListener // , IFileContextListener
{
	/*
	 * Fields
	 */

	/**
	 * Indicates whether this painter is active
	 */
	private boolean _isActive = false;

	/**
	 * The source viewer this painter is associated with
	 */
	private ITextViewer _sourceViewer;

	/**
	 * The viewer's widget
	 */
	private StyledText _textWidget;

	/**
	 * The color in which to draw the whitespace characters
	 */
	private Color _color;

	/**
	 * The decorator for space characters
	 */
	private String _spaceCharacter;

	/**
	 * The decorator for tab characters
	 */
	private String _tabCharacter;

	private int _maxColorizingColumns;

	/*
	 * Constructors
	 */

	/**
	 * Creates a new WhitespacePainter for the given source viewer.
	 * 
	 * @param sourceViewer
	 *            the source viewer
	 * @param spaceChar
	 * @param tabChar
	 * @param maxColorizingColumns
	 */
	public PreviewWhitespacePainter(ITextViewer sourceViewer, String spaceChar, String tabChar, int maxColorizingColumns)
	{
		this._sourceViewer = sourceViewer;
		this._textWidget = sourceViewer.getTextWidget();

		this._spaceCharacter = spaceChar; // middot
		this._tabCharacter = tabChar; // >>
		_maxColorizingColumns = maxColorizingColumns;
	}

	/*
	 * Methods
	 */

	/**
	 * @see org.eclipse.jface.text.IPainter#deactivate(boolean)
	 */
	public void deactivate(boolean redraw)
	{
		if (this._isActive)
		{
			this._isActive = false;

			if (this._textWidget != null && this._textWidget.isDisposed() == false)
			{
				this._textWidget.removePaintListener(this);

				if (redraw)
				{
					handleDrawRequest(null);
				}
			}
		}
	}

	/**
	 * @see org.eclipse.jface.text.IPainter#dispose()
	 */
	public void dispose()
	{
	}

	/**
	 * Decorates the given whitespace.
	 * 
	 * @param gc
	 *            the gc to draw into
	 * @param offset
	 *            the offset of the whitespace
	 * @param whitespace
	 *            the decorator for this whitespace
	 */
	private void draw(GC gc, int offset, String whitespace)
	{
		Point point = this._textWidget.getLocationAtOffset(offset);

		gc.setForeground(this._color);
		gc.drawText(whitespace, point.x, point.y, true);
	}

	/**
	 * Handles a redraw request.
	 * 
	 * @param gc
	 *            the gc to draw into.
	 */
	public void handleDrawRequest(GC gc)
	{
		if (gc == null)
		{
			this._textWidget.redraw();
			return;
		}

		int offset = getInclusiveTopIndexStartOffset();

		// http://bugs.eclipse.org/bugs/show_bug.cgi?id=17147
		int len = getExclusiveBottomIndexEndOffset() - offset;

		if (offset + len > this._textWidget.getCharCount())
		{
			len = this._textWidget.getCharCount() - offset;
		}

		try
		{
			String visibleText = this._textWidget.getText(offset, offset + len - 1);

			// fSourceViewer.getDocument().get(offset, len);

			int line = -1;
			int prevLine = line;

			for (int i = 0; i < len; ++i)
			{
				int thisLine = _textWidget.getLineAtOffset(i);

				if (thisLine == prevLine)
				{
					int column = i - _textWidget.getOffsetAtLine(thisLine);
					if (column >= _maxColorizingColumns)
					{
						continue;
					}
				}
				else
				{
					prevLine = thisLine;
				}

				char c = visibleText.charAt(i);

				switch (c)
				{
					case ' ':
						draw(gc, offset + i, this._spaceCharacter);
						break;

					case '\t':
						draw(gc, offset + i, this._tabCharacter);
						break;

					case '\r':
						if (i + 1 < len && visibleText.charAt(i + 1) == '\n')
						{
							draw(gc, offset + i, "\\r\\n"); //$NON-NLS-1$
							i++;
						}
						else
						{
							draw(gc, offset + i, "\\r"); //$NON-NLS-1$
						}
						break;

					case '\n':
						draw(gc, offset + i, "\\n"); //$NON-NLS-1$
						break;

					default:
						break;
				}
			}
		}
		catch (Exception e)
		{
			return;
		}
	}

	/**
	 * Returns the document offset of the upper left corner of the source viewer's view port, possibly including
	 * partially visible lines.
	 * 
	 * @return the document offset if the upper left corner of the view port
	 */
	private int getInclusiveTopIndexStartOffset()
	{
		if (this._textWidget != null && !this._textWidget.isDisposed())
		{
			int top = -1;

			if (this._sourceViewer instanceof ITextViewerExtension5)
			{
				top = this._textWidget.getTopIndex();

				if ((this._textWidget.getTopPixel() % this._textWidget.getLineHeight()) != 0)
				{
					top--;
				}

				ITextViewerExtension5 extension = (ITextViewerExtension5) this._sourceViewer;
				top = extension.widgetLine2ModelLine(top);
			}
			else
			{
				top = this._sourceViewer.getTopIndex();

				if ((this._textWidget.getTopPixel() % this._textWidget.getLineHeight()) != 0)
				{
					top--;
				}
			}

			try
			{
				IDocument document = this._sourceViewer.getDocument();

				return document.getLineOffset(top);
			}
			catch (BadLocationException x)
			{
			}
		}

		return -1;
	}

	/**
	 * Returns the first invisible document offset of the lower right corner of the source viewer's view port, possibly
	 * including partially visible lines.
	 * 
	 * @return the first invisible document offset of the lower right corner of the view port
	 */
	private int getExclusiveBottomIndexEndOffset()
	{
		if (this._textWidget != null && !this._textWidget.isDisposed())
		{
			int bottom = this._sourceViewer.getBottomIndex();

			if (((this._textWidget.getTopPixel() + this._textWidget.getClientArea().height) % this._textWidget
					.getLineHeight()) != 0)
			{
				bottom++;
			}

			try
			{
				IDocument document = this._sourceViewer.getDocument();

				if (bottom >= document.getNumberOfLines())
				{
					bottom = document.getNumberOfLines() - 1;
				}

				return document.getLineOffset(bottom) + document.getLineLength(bottom);
			}
			catch (BadLocationException x)
			{
			}
		}

		return -1;
	}

	/**
	 * @see org.eclipse.jface.text.IPainter#paint(int)
	 */
	public void paint(int reason)
	{
		IDocument document = this._sourceViewer.getDocument();

		if (document == null)
		{
			deactivate(false);
			return;
		}

		if (!this._isActive)
		{
			this._isActive = true;
			this._textWidget.addPaintListener(this);
			handleDrawRequest(null);
		}
		else
		{
			// if ((IPainter.CONFIGURATION == reason) || (IPainter.TEXT_CHANGE == reason))
			// {
			// // handleDrawRequest(null);
			// }
		}
	}

	/**
	 * @see org.eclipse.swt.events.PaintListener#paintControl(org.eclipse.swt.events.PaintEvent)
	 */
	public void paintControl(PaintEvent event)
	{
		if (this._textWidget != null)
		{
			handleDrawRequest(event.gc);
		}
	}

	/**
	 * Sets the color in which to draw whitespaces.
	 * 
	 * @param color
	 *            the color
	 */
	public void setColor(Color color)
	{
		this._color = color;
	}

	/**
	 * @see org.eclipse.jface.text.IPainter#setPositionManager(org.eclipse.jface.text.IPaintPositionManager)
	 */
	public void setPositionManager(IPaintPositionManager manager)
	{
	}
}