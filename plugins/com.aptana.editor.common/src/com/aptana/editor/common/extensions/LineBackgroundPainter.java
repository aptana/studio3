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
package com.aptana.editor.common.extensions;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IPaintPositionManager;
import org.eclipse.jface.text.IPainter;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewerExtension2;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.custom.LineBackgroundEvent;
import org.eclipse.swt.custom.LineBackgroundListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.scripting.IDocumentScopeManager;
import com.aptana.theme.ColorManager;
import com.aptana.theme.RGBa;
import com.aptana.theme.Theme;
import com.aptana.theme.ThemePlugin;

/**
 * A class that colors the entire line in token bg if there's only one background color specified in styling. This
 * extends block comment bg colors to entire line in the most common use case, rather than having the bg color revert to
 * the editor bg on the preceding spaces and trailing newline and empty space. This class now also handles painting
 * current line highlights in a modified manner from CursorLinePainter. This impl handles line highlights with alpha and
 * handles when tokens on a line have a non-null bg color of their own with an opaque line highlight.
 * 
 * @author cwilliams
 */
public class LineBackgroundPainter implements IPainter, LineBackgroundListener, PaintListener
{

	private ISourceViewer fViewer;
	/** The paint position manager for managing the line coordinates */
	private IPaintPositionManager fPositionManager;

	/** Keeps track of the line to be painted */
	private Position fCurrentLine = new Position(0, 0);
	/** Keeps track of the line to be cleared */
	private Position fLastLine = new Position(0, 0);
	/** Keeps track of the line number of the last painted line */
	private int fLastLineNumber = -1;

	private boolean fIsActive;
	private Point fLastSelection = new Point(0, 0);
	private boolean fEnabled;

	public LineBackgroundPainter(ISourceViewer viewer)
	{
		this.fViewer = viewer;
	}

	public void deactivate(boolean redraw)
	{
		if (fIsActive)
		{
			fIsActive = false;

			/*
			 * on turning off the feature one has to paint the currently highlighted line with the standard background
			 * color
			 */
			if (redraw)
				drawHighlightLine(fCurrentLine);

			fViewer.getTextWidget().removeLineBackgroundListener(this);
			fViewer.getTextWidget().removePaintListener(this);

			if (fPositionManager != null)
				fPositionManager.unmanagePosition(fCurrentLine);

			fLastLineNumber = -1;
			fCurrentLine.offset = 0;
			fCurrentLine.length = 0;
		}
	}

	/*
	 * @see IPainter#dispose()
	 */
	public void dispose()
	{
	}

	/*
	 * @see IPainter#paint(int)
	 */
	public void paint(int reason)
	{
		if (fViewer == null)
		{
			return;
		}
		if (fViewer.getDocument() == null)
		{
			deactivate(false);
			return;
		}

		// initialization
		if (!fIsActive)
		{
			StyledText textWidget = fViewer.getTextWidget();
			textWidget.addLineBackgroundListener(this);
			textWidget.addPaintListener(this);
			fPositionManager.managePosition(fCurrentLine);
			fIsActive = true;
		}

		// This forces redraw of the line highlight
		if (updateHighlightLine())
		{
			// clear last line
			drawHighlightLine(fLastLine);
			// draw new line
			drawHighlightLine(fCurrentLine);
		}
	}

	/**
	 * Returns the location of the caret as offset in the source viewer's input document.
	 * 
	 * @return the caret location
	 */
	private int getModelCaret()
	{
		int widgetCaret = fViewer.getTextWidget().getCaretOffset();
		if (fViewer instanceof ITextViewerExtension5)
		{
			ITextViewerExtension5 extension = (ITextViewerExtension5) fViewer;
			return extension.widgetOffset2ModelOffset(widgetCaret);
		}
		IRegion visible = fViewer.getVisibleRegion();
		return widgetCaret + visible.getOffset();
	}

	/**
	 * Updates all the cached information about the lines to be painted and to be cleared. Returns <code>true</code> if
	 * the line number of the cursor line has changed.
	 * 
	 * @return <code>true</code> if cursor line changed
	 */
	private boolean updateHighlightLine()
	{
		try
		{

			IDocument document = fViewer.getDocument();
			int modelCaret = getModelCaret();
			int lineNumber = document.getLineOfOffset(modelCaret);
			Point selection = fViewer.getTextWidget().getSelectionRange();

			// redraw if the current line number is different from the last line number we painted
			// initially fLastLineNumber is -1
			if (lineNumber != fLastLineNumber || !overlaps(fCurrentLine, modelCaret) || (selection.y != 0))
			{
				// Handle non-empty selections (turn off highlight line)
				if (selection.y != 0 && fLastLine.equals(fCurrentLine))
				{
					if (fLastSelection.equals(selection)) // selection didn't change
					{
						return false; // don't redraw the highlight line
					}
					fLastSelection = selection;
					return true; // selection changed
				}
				fLastSelection = selection;
				// Update the current and last lines
				fLastLine.offset = fCurrentLine.offset;
				fLastLine.length = fCurrentLine.length;
				fLastLine.isDeleted = fCurrentLine.isDeleted;

				if (fCurrentLine.isDeleted)
				{
					fCurrentLine.isDeleted = false;
					fPositionManager.managePosition(fCurrentLine);
				}

				fCurrentLine.offset = document.getLineOffset(lineNumber);
				if (lineNumber == document.getNumberOfLines() - 1)
				{
					fCurrentLine.length = document.getLength() - fCurrentLine.offset;
				}
				else
				{
					fCurrentLine.length = document.getLineOffset(lineNumber + 1) - fCurrentLine.offset;
				}

				fLastLineNumber = lineNumber;
				return true;
			}
		}
		catch (BadLocationException e)
		{
		}

		return false;
	}

	private boolean overlaps(Position currentLine, int modelCaret)
	{
		if (currentLine.overlapsWith(modelCaret, 0))
			return true;
		if (modelCaret == (currentLine.getOffset() + currentLine.getLength()))
		{
			return true;
		}
		return false;
	}

	protected Position getCurrentLinePosition()
	{
		Point selection = fViewer.getTextWidget().getSelectionRange();
		if (selection.y != 0)
		{
			return null;
		}
		try
		{
			int line = fViewer.getDocument().getLineOfOffset(selection.x);
			return new Position(fViewer.getDocument().getLineOffset(line), 0);
		}
		catch (BadLocationException e)
		{
			return null;
		}
	}

	/**
	 * Assumes the given position to specify offset and length of a line to be painted.
	 * 
	 * @param position
	 *            the specification of the line to be painted
	 */
	private void drawHighlightLine(Position position)
	{
		// Don't draw if highlight current line is turned off
		if (!fEnabled)
		{
			return;
		}

		RGBa lineHighlight = getCurrentTheme().getLineHighlight();
		if (lineHighlight.isFullyOpaque())
		{
			if (fViewer instanceof ITextViewerExtension2)
			{
				ITextViewerExtension2 ext = (ITextViewerExtension2) fViewer;
				ext.invalidateTextPresentation(position.getOffset(), position.getLength());
			}
			return;
		}
		Rectangle rect = getLineRectangle(position);
		if (rect == null)
		{
			return;
		}
		fViewer.getTextWidget().redraw(rect.x, rect.y, rect.width, rect.height, true);
	}

	private Rectangle getLineRectangle(Position position)
	{
		if (position == null)
		{
			return null;
		}

		// if the position that is about to be drawn was deleted then we can't
		if (position.isDeleted())
		{
			return null;
		}

		int widgetOffset = 0;
		if (fViewer instanceof ITextViewerExtension5)
		{

			ITextViewerExtension5 extension = (ITextViewerExtension5) fViewer;
			widgetOffset = extension.modelOffset2WidgetOffset(position.getOffset());
			if (widgetOffset == -1)
			{
				return null;
			}
		}
		else
		{

			IRegion visible = fViewer.getVisibleRegion();
			widgetOffset = position.getOffset() - visible.getOffset();
			if (widgetOffset < 0 || visible.getLength() < widgetOffset)
			{
				return null;
			}
		}

		StyledText textWidget = fViewer.getTextWidget();
		// check for https://bugs.eclipse.org/bugs/show_bug.cgi?id=64898
		// this is a guard against the symptoms but not the actual solution
		if (0 <= widgetOffset && widgetOffset <= textWidget.getCharCount())
		{
			Point upperLeft = textWidget.getLocationAtOffset(widgetOffset);
			int width = textWidget.getClientArea().width + textWidget.getHorizontalPixel();
			int height = textWidget.getLineHeight(widgetOffset);
			return new Rectangle(0, upperLeft.y, width, height);
		}

		return null;
	}

	/*
	 * @see IPainter#setPositionManager(IPaintPositionManager)
	 */
	public void setPositionManager(IPaintPositionManager manager)
	{
		fPositionManager = manager;
	}

	public void lineGetBackground(LineBackgroundEvent event)
	{
		if (fViewer == null)
		{
			return;
		}
		final StyledText textWidget = fViewer.getTextWidget();
		if (textWidget == null)
		{
			return;
		}

		try
		{
			final int offset = event.lineOffset;
			IDocument document = fViewer.getDocument();
			int line = document.getLineOfOffset(offset);
			final IRegion lineRegion = document.getLineInformation(line);

			// Handle fully opaque line highlight here. A modified approach from CursorLinePainter.
			if (shouldDrawCurrentLine(line))
			{
				drawCurrentLine(event, lineRegion);
				return;
			}

			// Not drawing an opaque line highlight, so we need to do our normal line coloring here.
			// This extends the bg color out for a given line based on it's end scope.
			String endOfLineScope = getScopeManager().getScopeAtOffset(document, lineRegion.getLength() + offset);
			String commonPrefix = getScope(document, line, endOfLineScope);
			TextAttribute at = getCurrentTheme().getTextAttribute(commonPrefix);
			// If the background is null or matches the theme BG, we should just return early!
			if (at.getBackground() == null || at.getBackground().equals(getThemeBG()))
			{
				return;
			}
			event.lineBackground = at.getBackground();
		}
		catch (BadLocationException e)
		{
			CommonEditorPlugin.logError(e);
		}
	}

	private void drawCurrentLine(LineBackgroundEvent event, final IRegion lineRegion)
	{
		final StyledText textWidget = fViewer.getTextWidget();
		final int offset = event.lineOffset;
		final RGBa lineHighlight = getCurrentTheme().getLineHighlight();
		event.lineBackground = getColorManager().getColor(lineHighlight.toRGB());

		// In this case, we should be overriding the bg of the style ranges for the line too!
		if (textWidget.isDisposed())
		{
			return;
		}
		// FIXME Only change bg colors of visible ranges!
		int replaceLength = 160;
		if (lineRegion != null)
		{
			replaceLength = Math.min(replaceLength, lineRegion.getLength());
		}

		// be safe about offsets
		int charCount = textWidget.getCharCount();
		if (offset + replaceLength > charCount)
		{
			replaceLength = charCount - offset;
			if (replaceLength < 0)
			{
				// Just playing safe here
				replaceLength = 0;
			}
		}
		final StyleRange[] ranges = textWidget.getStyleRanges(offset, replaceLength, true);
		if (ranges == null || ranges.length == 0)
		{
			return;
		}
		Color background = textWidget.getBackground();
		final int[] positions = new int[ranges.length * 2];
		int x = 0;
		boolean apply = false;
		for (StyleRange range : ranges)
		{
			if (range.background != null)
			{
				if (!range.background.equals(background))
				{
					positions[x] = range.start;
					positions[x + 1] = range.length;
					x += 2;
					continue;
				}
				apply = true;
			}
			range.background = null;
			positions[x] = range.start;
			positions[x + 1] = range.length;
			x += 2;
		}

		if (apply)
		{
			textWidget.setStyleRanges(offset, replaceLength, positions, ranges);
		}
	}

	protected ColorManager getColorManager()
	{
		return ThemePlugin.getDefault().getColorManager();
	}

	/**
	 * Must have no selection, caret must be on the line, be fully opaque
	 * 
	 * @param line
	 * @return
	 */
	private boolean shouldDrawCurrentLine(int line)
	{
		// Don't draw if highlight current line is turned off
		if (!fEnabled)
		{
			return false;
		}

		// If there's a selection we "turn off" line highlight.
		// Note: disabled this as the line was still being highlighted and opaque backgrounds had a strange behaviour.  
        //Point selection = fViewer.getTextWidget().getSelectionRange();
        //if (selection.y != 0)
        //    return false;

		// If there's transparency, we handle/color that in a different way, in #paintControl.
		RGBa lineHighlight = getCurrentTheme().getLineHighlight();
		if (!lineHighlight.isFullyOpaque())
			return false;

		// Now we make sure that this really is the current line.
		try
		{
			int lineNumber = fViewer.getDocument().getLineOfOffset(getModelCaret());
			return line == lineNumber; // current line!
		}
		catch (BadLocationException e)
		{
			return false;
		}
	}

	/**
	 * Calculates the common scope between the end of one line and the beginning of the next.
	 * 
	 * @param document
	 * @param line
	 * @param endOfLineScope
	 * @return
	 * @throws BadLocationException
	 */
	private String getScope(IDocument document, int line, String endOfLineScope) throws BadLocationException
	{
		// if this is the last line, just use the scope at the end of it.
		int lines = document.getNumberOfLines();
		if (line + 1 >= lines)
		{
			return endOfLineScope;
		}

		// now grab the scope at the beginning of the next line...
		IRegion nextLine = document.getLineInformation(line + 1);
		// If the next line is empty, take our end of line scope
		if (nextLine.getLength() == 0)
		{
			return endOfLineScope;
		}
		String startOfNextLineScope = getScopeManager().getScopeAtOffset(document, nextLine.getOffset());

		// Calculate the common prefix between the two!
		StringBuilder builder = new StringBuilder();
		int length = Math.min(endOfLineScope.length(), startOfNextLineScope.length());
		for (int i = 0; i < length; i++)
		{
			char c = endOfLineScope.charAt(i);
			char o = startOfNextLineScope.charAt(i);
			if (c == o)
			{
				builder.append(c);
			}
		}
		return builder.toString();
	}

	/**
	 * Draws the current line highlight (over top using theme colors and alpha). If the line highlight is fully opaque,
	 * then this method will not do anything and we'll fall back to using the mechanism eclipse does in
	 * CursorLinePainter with a little modification.
	 */
	public void paintControl(PaintEvent e)
	{
		// If there's no alpha value for the line highlight, then we need to force the bg color of the whole line
		// to the rgb value!
		RGBa lineHighlight = getCurrentTheme().getLineHighlight();
		if (lineHighlight.isFullyOpaque())
		{
			return;
		}

		Rectangle rect = new Rectangle(e.x, e.y, e.width, e.height);
		Rectangle lineRect = getLineRectangle(getCurrentLinePosition());
		if (lineRect == null || !lineRect.intersects(rect))
		{
			return;
		}

		// Only paint the part of lineRect that is contained in rect!
		Rectangle intersection = lineRect.intersection(rect);
		e.gc.setAlpha(lineHighlight.getAlpha());
		e.gc.setBackground(getColorManager().getColor(lineHighlight.toRGB()));
		e.gc.fillRectangle(intersection);
	}

	protected Color getThemeBG()
	{
		return getColorManager().getColor(getCurrentTheme().getBackground());
	}

	protected Theme getCurrentTheme()
	{
		return ThemePlugin.getDefault().getThemeManager().getCurrentTheme();
	}

	protected IDocumentScopeManager getScopeManager()
	{
		return CommonEditorPlugin.getDefault().getDocumentScopeManager();
	}

	public void setHighlightLineEnabled(boolean on)
	{
		fEnabled = on;
	}
}
