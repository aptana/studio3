package com.aptana.editor.common.extensions;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IPaintPositionManager;
import org.eclipse.jface.text.IPainter;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.custom.LineBackgroundEvent;
import org.eclipse.swt.custom.LineBackgroundListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.scripting.IDocumentScopeManager;
import com.aptana.theme.Theme;
import com.aptana.theme.ThemePlugin;

/**
 * A class that colors the entire line in token bg if there's only one background color specified in styling. This
 * extends block comment bg colors to entire line in the most common use case, rather than having the bg color revert to
 * the editor bg on the preceding spaces and trailing newline and empty space.
 * 
 * @author cwilliams
 */
public class LineBackgroundPainter implements IPainter, LineBackgroundListener
{

	private ISourceViewer fViewer;
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
			fIsActive = true;
		}
	}

	@Override
	public void setPositionManager(IPaintPositionManager manager)
	{
		// do nothing
	}

	@Override
	public void lineGetBackground(LineBackgroundEvent event)
	{
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
			// When we do this, we need to explicitly set the bg color for ranges with no bg color!
			
			// First, for perf reasons, bail out early if there are no style ranges, or there's too many
			StyleRange[] ranges = textWidget.getStyleRanges(offset, lineRegion.getLength(), false);
			if (ranges == null || ranges.length <= 0 || ranges.length > 100)
			{
				return;
			}
			// for perf reasons, only do ranges up to 160th column!
			int length = Math.min(160, lineRegion.getLength());
			ranges = textWidget.getStyleRanges(offset, length, true);
			if (ranges == null || ranges.length <= 0)
			{
				return;
			}

			int x = 0;
			int[] pairs = new int[ranges.length * 2];
			for (StyleRange range : ranges)
			{
				// FIXME This is rather hacky. We still don't play nice 100% of the time... What about offsets with
				// no style, or with a bg matching theme BG (because of alpha?)
				if (range.background == null)
				{
					range.background = getThemeBG();
				}
				pairs[x] = range.start;
				pairs[x + 1] = range.length;
				x += 2;
			}
			textWidget.setStyleRanges(offset, length, pairs, ranges);
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
}
