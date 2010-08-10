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

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.scripting.IDocumentScopeManager;
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
			return;
		String text = event.lineText;
		if (text == null || text.length() == 0)
			return;

		int offset = event.lineOffset;

		// We're coloring whole line based on what the trailing end bg color should be.
		try
		{
			IDocument document = fViewer.getDocument();
			int line = document.getLineOfOffset(offset);
			IRegion lineRegion = document.getLineInformation(line);
			String endOfLineScope = getScopeManager().getScopeAtOffset(document, lineRegion.getLength() + offset);

			String commonPrefix = getScope(document, line, endOfLineScope);

			TextAttribute at = ThemePlugin.getDefault().getThemeManager().getCurrentTheme()
					.getTextAttribute(commonPrefix);
			event.lineBackground = at.getBackground();
			// When we do this, we need to explicitly set the bg color for ranges with no bg color!
			StyleRange[] ranges = textWidget.getStyleRanges(offset, lineRegion.getLength(), true);
			if (ranges != null && ranges.length > 0)
			{
				for (StyleRange range : ranges)
				{
					// FIXME This is rather hacky. We still don't play nice 100% of the time...
					if (range.background == null)
					{
						range.background = ThemePlugin.getDefault().getColorManager()
								.getColor(ThemePlugin.getDefault().getThemeManager().getCurrentTheme().getBackground());
						textWidget.setStyleRange(range);
					}
				}
			}
		}
		catch (BadLocationException e)
		{
			CommonEditorPlugin.logError(e);
		}
	}

	protected IDocumentScopeManager getScopeManager()
	{
		return CommonEditorPlugin.getDefault().getDocumentScopeManager();
	}

	private String getScope(IDocument document, int line, String endOfLineScope) throws BadLocationException
	{
		int lines = document.getNumberOfLines();
		if (line + 1 >= lines)
		{
			return endOfLineScope;
		}
		int offsetOfNextLine = document.getLineOffset(line + 1);
		String startOfNextLineScope = getScopeManager().getScopeAtOffset(document, offsetOfNextLine);

		// Calculate the common prefix between the two!
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < endOfLineScope.length(); i++)
		{
			if (i >= startOfNextLineScope.length())
				break;
			char c = endOfLineScope.charAt(i);
			char o = startOfNextLineScope.charAt(i);
			if (c == o)
				builder.append(c);
		}
		return builder.toString();
	}
}
