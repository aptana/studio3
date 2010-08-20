package com.aptana.editor.common.extensions;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IPaintPositionManager;
import org.eclipse.jface.text.IPainter;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.custom.LineBackgroundEvent;
import org.eclipse.swt.custom.LineBackgroundListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.scripting.IDocumentScopeManager;
import com.aptana.theme.RGBa;
import com.aptana.theme.Theme;
import com.aptana.theme.ThemePlugin;

/**
 * A class that colors the entire line in token bg if there's only one background color specified in styling. This
 * extends block comment bg colors to entire line in the most common use case, rather than having the bg color revert to
 * the editor bg on the preceding spaces and trailing newline and empty space.
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

	public LineBackgroundPainter(ISourceViewer viewer)
	{
		this.fViewer = viewer;
	}

	@Override
	public void deactivate(boolean redraw)
	{
		// do nothing
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

		StyledText textWidget = fViewer.getTextWidget();
		// initialization
		if (!fIsActive)
		{
			textWidget.addLineBackgroundListener(this);
			textWidget.addPaintListener(this);
			fPositionManager.managePosition(fCurrentLine);
			fIsActive = true;
		}

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

			// redraw if the current line number is different from the last line number we painted
			// initially fLastLineNumber is -1
			if (lineNumber != fLastLineNumber || !fCurrentLine.overlapsWith(modelCaret, 0))
			{

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
					fCurrentLine.length = document.getLength() - fCurrentLine.offset;
				else
					fCurrentLine.length = document.getLineOffset(lineNumber + 1) - fCurrentLine.offset;

				fLastLineNumber = lineNumber;
				return true;
			}
			// Force redraw if there's a non-empty selection!
			Point selection = fViewer.getTextWidget().getSelection();
			if (selection.y != 0)
			{
				return true;
			}
		}
		catch (BadLocationException e)
		{
		}

		return false;
	}

	protected Position getCurrentLinePosition()
	{
		Point selection = fViewer.getTextWidget().getSelectionRange();
		if (selection.y != 0)
			return null;
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
			return null;

		int widgetOffset = 0;
		if (fViewer instanceof ITextViewerExtension5)
		{

			ITextViewerExtension5 extension = (ITextViewerExtension5) fViewer;
			widgetOffset = extension.modelOffset2WidgetOffset(position.getOffset());
			if (widgetOffset == -1)
				return null;

		}
		else
		{

			IRegion visible = fViewer.getVisibleRegion();
			widgetOffset = position.getOffset() - visible.getOffset();
			if (widgetOffset < 0 || visible.getLength() < widgetOffset)
				return null;
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
	@Override
	public void setPositionManager(IPaintPositionManager manager)
	{
		fPositionManager = manager;
	}

	@Override
	public void lineGetBackground(LineBackgroundEvent event)
	{
		if (fViewer == null)
		{
			return;
		}
		StyledText textWidget = fViewer.getTextWidget();
		if (textWidget == null)
		{
			return;
		}

		// We're coloring whole line based on what the trailing end bg color should be.
		try
		{
			int offset = event.lineOffset;
			IDocument document = fViewer.getDocument();
			int line = document.getLineOfOffset(offset);
			
			// Check if we need to use a fully opaque current line highlight here.
			int modelCaret = getModelCaret();
			int lineNumber = document.getLineOfOffset(modelCaret);
			if (line == lineNumber)
			{
				// current line!
				RGBa lineHighlight = getCurrentTheme().getLineHighlight();
				if (lineHighlight.isFullyOpaque())
				{
					// FIXME In this case, we should be overriding the bg of the style ranges for the line too!
					event.lineBackground = ThemePlugin.getDefault().getColorManager().getColor(lineHighlight.toRGB());
					return;
				}
			}

			IRegion lineRegion = document.getLineInformation(line);
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

	protected Color getThemeBG()
	{
		return ThemePlugin.getDefault().getColorManager().getColor(getCurrentTheme().getBackground());
	}

	protected Theme getCurrentTheme()
	{
		return ThemePlugin.getDefault().getThemeManager().getCurrentTheme();
	}

	protected IDocumentScopeManager getScopeManager()
	{
		return CommonEditorPlugin.getDefault().getDocumentScopeManager();
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
	 * Draws the current line highlight (over top using theme colors and alpha).
	 */
	@Override
	public void paintControl(PaintEvent e)
	{
		Rectangle rect = new Rectangle(e.x, e.y, e.width, e.height);
		Rectangle lineRect = getLineRectangle(getCurrentLinePosition());
		if (lineRect == null || !lineRect.intersects(rect))
		{
			return;
		}

		// Only paint the part of lineRect that is contained in rect!
		Rectangle intersection = lineRect.intersection(rect);
		RGBa lineHighlight = getCurrentTheme().getLineHighlight();

		// FIXME If there's no alpha value for the line highlight, then we need to force the bg color of the whole line
		// to the rgb value!
		if (lineHighlight.isFullyOpaque())
		{
			// For now, maybe we should just assume an alpha value of 128?
			return;
		}

		e.gc.setAlpha(lineHighlight.getAlpha());
		e.gc.setBackground(ThemePlugin.getDefault().getColorManager().getColor(lineHighlight.toRGB()));
		e.gc.fillRectangle(intersection);
	}
}
