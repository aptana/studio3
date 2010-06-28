package com.aptana.editor.common.extensions;

import org.eclipse.jface.text.IPaintPositionManager;
import org.eclipse.jface.text.IPainter;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.custom.LineBackgroundEvent;
import org.eclipse.swt.custom.LineBackgroundListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;

/**
 * A class that colors the entire line in token bg if there's only one background color specified in styling. This
 * extends block comment bg colors to entire line in the most common use case, rather than having the bg color
 * revert to the editor bg on the preceding spaces and trailing newline and empty space.
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
		// FIXME What about when there's other style ranges but we begin and end on same bg color? Do we color the
		// line background anyways and force style ranges with null bg colors to specify the editor bg?
		StyledText textWidget = fViewer.getTextWidget();
		if (textWidget == null)
			return;
		String text = event.lineText;
		if (text == null || text.length() == 0)
			return;
		int offset = event.lineOffset;
		int leadingWhitespace = 0;
		while (Character.isWhitespace(text.charAt(0)))
		{
			leadingWhitespace++;
			text = text.substring(1);
			if (text.length() <= 0)
				break;
		}
		int length = text.length();
		if (length > 0)
		{
			StyleRange[] ranges = textWidget.getStyleRanges(offset + leadingWhitespace, length);

			if (ranges != null && ranges.length == 1)
			{
				event.lineBackground = ranges[0].background;
			}
		}
	}
}
