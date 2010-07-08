/*******************************************************************************
 * Copyright (c) 2007, 2010 Wind River Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Michael Scharf (Wind River) - initial API and implementation
 * Michael Scharf (Wind River) - [240098] The cursor should not blink when the terminal is disconnected
 * Uwe Stieber (Wind River) - [281328] The very first few characters might be missing in the terminal control if opened and connected programmatically
 * Martin Oberhuber (Wind River) - [294327] After logging in, the remote prompt is hidden
 * Anton Leherbauer (Wind River) - [294468] Fix scroller and text line rendering
 * Uwe Stieber (Wind River) - [205486] Fix ScrollLock always moving to line 1
 *******************************************************************************/
package org.eclipse.tm.internal.terminal.textcanvas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.URLHyperlink;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.tm.terminal.model.ITerminalTextDataReadOnly;

/**
 * A cell oriented Canvas. Maintains a list of "cells". It can either be vertically or horizontally scrolled. The
 * CellRenderer is responsible for painting the cell.
 */
public class TextCanvas extends GridCanvas
{
	// Base URL detection
//	private static final Pattern URL_DETECT_PATTERN = Pattern
//			.compile("\\b(https?|ftp|file)://[\\-A-Z0-9\\+&@#/%\\?=~_\\|!:,\\.;]*[A-Z0-9\\+&@#/%=~_\\|]"); //$NON-NLS-1$
	
	/**
	 * Detect URLs with protocol, or bare hostnames
	 */
	private static final Pattern URL_DETECT_PATTERN = Pattern.compile("\\b\n" +
"  # Match the leading part (proto://hostname, or just hostname)\n" +
"  (\n" +
"    # http://, or https:// leading part\n" +
"    (https?)://[-\\w]+(\\.\\w[-\\w]*)+\n" +
"  |\n" +
"    # or, try to find a hostname with more specific sub-expression\n" +
"    (?i: [a-z0-9] (?:[-a-z0-9]*[a-z0-9])? \\. )+ # sub domains\n" +
"    # Now ending .com, etc. For these, require lowercase\n" +
"    (?-i: com\\b\n" +
"        | edu\\b\n" +
"        | biz\\b\n" +
"        | gov\\b\n" +
"        | in(?:t|fo)\\b # .int or .info\n" +
"        | mil\\b\n" +
"        | net\\b\n" +
"        | org\\b\n" +
"        | [a-z][a-z]\\.[a-z][a-z]\\b # two-letter country code\n" +
"    )\n" +
"  )\n" +
"\n" +
"  # Allow an optional port number\n" +
"  ( : \\d+ )?\n" +
"		  \n" +
"  # The rest of the URL is optional, and begins with /\n" +
"  (\n" +
"    /\n" +
"    # The rest are heuristics for what seems to work well\n" +
"    [^.!,?;\"\\'<>()\\[\\]\\{\\}\\s\\x7F-\\xFF]*\n" +
"    (\n" +
"      [.!,?]+ [^.!,?;\"\\'<>()\\[\\]\\{\\}\\s\\x7F-\\xFF]+\n" +
"    )*\n" +
"  )?", Pattern.CASE_INSENSITIVE | Pattern.COMMENTS);
	
	protected final ITextCanvasModel fCellCanvasModel;
	/** Renders the cells */
	private final ILinelRenderer fCellRenderer;
	private boolean fScrollLock;
	private Point fDraggingStart;
	private Point fDraggingEnd;
	private boolean fHasSelection;
	private ResizeListener fResizeListener;

	// The minSize is meant to determine the minimum size of the backing store
	// (grid) into which remote data is rendered. If the viewport is smaller
	// than that minimum size, the backing store size remains at the minSize,
	// and a scrollbar is shown instead. In reality, this has the following
	// issues or effects today:
	// (a) Bug 281328: For very early data coming in before the widget is
	// realized, the minSize determines into what initial grid that is
	// rendered. See also @link{#addResizeHandler(ResizeListener)}.
	// (b) Bug 294468: Since we have redraw and size computation problems
	// with horizontal scrollers, for now the minColumns must be small
	// enough to avoid a horizontal scroller appearing in most cases.
	// (b) Bug 294327: since we have problems with the vertical scroller
	// showing the correct location, minLines must be small enough
	// to avoid a vertical scroller or new data may be rendered off-screen.
	// As a compromise, we have been working with a 20x4 since the Terminal
	// inception, though many users would want a 80x24 minSize and backing
	// store. Pros and cons of the small minsize:
	// + consistent "remote size==viewport size", vi works as expected
	// - dumb terminals which expect 80x24 render garbled on small viewport.
	// If bug 294468 were resolved, an 80 wide minSize would be preferrable
	// since it allows switching the terminal viewport small/large as needed,
	// without destroying the backing store. For a complete solution,
	// Bug 196462 tracks the request for a user-defined fixed-widow-size-mode.
	private int fMinColumns = 80;
	private int fMinLines = 4;
	private boolean fCursorEnabled;

	private IHyperlink[] fLinks = new IHyperlink[0];

	/**
	 * Create a new CellCanvas with the given SWT style bits. (SWT.H_SCROLL and SWT.V_SCROLL are automatically added).
	 */
	public TextCanvas(Composite parent, ITextCanvasModel model, int style, ILinelRenderer cellRenderer)
	{
		super(parent, style | SWT.H_SCROLL | SWT.V_SCROLL);
		fCellRenderer = cellRenderer;
		setCellWidth(fCellRenderer.getCellWidth());
		setCellHeight(fCellRenderer.getCellHeight());
		fCellCanvasModel = model;
		fCellCanvasModel.addCellCanvasModelListener(new ITextCanvasModelListener()
		{
			public void rangeChanged(int col, int line, int width, int height)
			{
				repaintRange(col, line, width, height);
			}

			public void dimensionsChanged(int cols, int rows)
			{
				calculateGrid();
			}

			public void terminalDataChanged()
			{
				if (isDisposed())
					return;
				// scroll to end (unless scroll lock is active)
				scrollToEnd();
				// TODO Grab the content of the terminal, forward it to listeners who can then mark certain regions as
				// hyperlinks!
				StringBuilder builder = new StringBuilder();
				ITerminalTextDataReadOnly text = fCellCanvasModel.getTerminalText();
				int lines = text.getHeight();
				for (int i = 0; i < lines; i++)
				{
					char[] chars = text.getChars(i);
					chars = pad(chars, text.getWidth());
					if (chars != null)
					{
						builder.append(chars);
					}
					builder.append("\n");
				}
				fLinks = detectHyperlinks(builder.toString());
			}
		});
		// let the cursor blink if the text canvas gets the focus...
		addFocusListener(new FocusListener()
		{
			public void focusGained(FocusEvent e)
			{
				fCellCanvasModel.setCursorEnabled(fCursorEnabled);
			}

			public void focusLost(FocusEvent e)
			{
				fCellCanvasModel.setCursorEnabled(false);
			}
		});
		addMouseListener(new MouseListener()
		{
			public void mouseDoubleClick(MouseEvent e)
			{
			}

			public void mouseDown(MouseEvent e)
			{
				if (e.button == 1)
				{ // left button
					fDraggingStart = screenPointToCell(e.x, e.y);
					fHasSelection = false;
					if ((e.stateMask & SWT.SHIFT) != 0)
					{
						Point anchor = fCellCanvasModel.getSelectionAnchor();
						if (anchor != null)
							fDraggingStart = anchor;
					}
					else
					{
						fCellCanvasModel.setSelectionAnchor(fDraggingStart);
					}
					fDraggingEnd = null;
				}
			}

			public void mouseUp(MouseEvent e)
			{
				if (e.button == 1)
				{ // left button
					updateHasSelection(e);
					if (fHasSelection)
					{
						setSelection(screenPointToCell(e.x, e.y));
					}
					else
					{
						fCellCanvasModel.setSelection(-1, -1, -1, -1);

						// Detect clicking on hyperlinks
						for (int i = 0; i < fLinks.length; i++)
						{
							IHyperlink link = fLinks[i];
							IRegion region = link.getHyperlinkRegion();
							int line = region.getOffset() / fCellCanvasModel.getTerminalText().getWidth();
							if (fDraggingStart.y >= line)
							{
								int col = region.getOffset() % fCellCanvasModel.getTerminalText().getWidth();
								int endLine = (region.getOffset() + region.getLength())
										/ fCellCanvasModel.getTerminalText().getWidth();
								int endCol = (region.getOffset() + region.getLength())
										% fCellCanvasModel.getTerminalText().getWidth();

								if ((fDraggingStart.y == line && fDraggingStart.x >= col)
										|| (fDraggingStart.y == endLine && fDraggingStart.x <= endCol)
										|| (fDraggingStart.y > line && fDraggingStart.y < endLine))
								{
									link.open();
									break;
								}
							}
						}
					}
					fDraggingStart = null;
				}
			}
		});
		addMouseMoveListener(new MouseMoveListener()
		{

			public void mouseMove(MouseEvent e)
			{
				if (fDraggingStart != null)
				{
					updateHasSelection(e);
					setSelection(screenPointToCell(e.x, e.y));
				}
			}
		});
		serVerticalBarVisible(true);
		setHorizontalBarVisible(false);
	}

	protected char[] pad(char[] chars, int width)
	{
		if (chars != null && chars.length == width)
		{
			return chars;
		}
		char[] newChars = new char[width];
		Arrays.fill(newChars, ' ');
		if (chars != null)
		{
			System.arraycopy(chars, 0, newChars, 0, chars.length);
		}
		return newChars;
	}

	
	protected IHyperlink[] detectHyperlinks(String contents)
	{
		Matcher m = URL_DETECT_PATTERN.matcher(contents);
		int start = 0;
		List list = new ArrayList();
		while (m.find(start))
		{
			String urlString = new String(m.group().trim());
			start = m.end();
			IRegion region = new Region(m.start(), urlString.length());
			list.add(new URLHyperlink(region, urlString));
		}
		return (IHyperlink[]) list.toArray(new IHyperlink[0]);
	}

	/**
	 * The user has to drag the mouse to at least one character to make a selection. Once this is done, even a one char
	 * selection is OK.
	 * 
	 * @param e
	 */
	private void updateHasSelection(MouseEvent e)
	{
		if (fDraggingStart != null)
		{
			Point p = screenPointToCell(e.x, e.y);
			if (fDraggingStart.x != p.x || fDraggingStart.y != p.y)
				fHasSelection = true;
		}
	}

	void setSelection(Point p)
	{
		if (fDraggingStart != null && !p.equals(fDraggingEnd))
		{
			fDraggingEnd = p;
			if (compare(p, fDraggingStart) < 0)
			{
				fCellCanvasModel.setSelection(p.y, fDraggingStart.y, p.x, fDraggingStart.x);
			}
			else
			{
				fCellCanvasModel.setSelection(fDraggingStart.y, p.y, fDraggingStart.x, p.x);

			}
		}
	}

	int compare(Point p1, Point p2)
	{
		if (p1.equals(p2))
			return 0;
		if (p1.y == p2.y)
		{
			if (p1.x > p2.x)
				return 1;
			else
				return -1;
		}
		if (p1.y > p2.y)
		{
			return 1;
		}
		else
		{
			return -1;
		}
	}

	public ILinelRenderer getCellRenderer()
	{
		return fCellRenderer;
	}

	public int getMinColumns()
	{
		return fMinColumns;
	}

	public void setMinColumns(int minColumns)
	{
		fMinColumns = minColumns;
	}

	public int getMinLines()
	{
		return fMinLines;
	}

	public void setMinLines(int minLines)
	{
		fMinLines = minLines;
	}

	protected void onResize(boolean init)
	{
		if (fResizeListener != null)
		{
			Rectangle bonds = getClientArea();
			int cellHeight = getCellHeight();
			int cellWidth = getCellWidth();
			int lines = bonds.height / cellHeight;
			int columns = bonds.width / cellWidth;
			// when the view is minimised, its size is set to 0
			// we don't sent this to the terminal!
			if ((lines > 0 && columns > 0) || init)
			{
				if (columns < fMinColumns)
				{
					if (!isHorizontalBarVisble())
					{
						setHorizontalBarVisible(true);
						bonds = getClientArea();
						lines = bonds.height / cellHeight;
					}
					columns = fMinColumns;
				}
				else if (columns >= fMinColumns && isHorizontalBarVisble())
				{
					setHorizontalBarVisible(false);
					bonds = getClientArea();
					lines = bonds.height / cellHeight;
					columns = bonds.width / cellWidth;
				}
				if (lines < fMinLines)
					lines = fMinLines;
				fResizeListener.sizeChanged(lines, columns);
			}
		}
		super.onResize();
		calculateGrid();
	}

	protected void onResize()
	{
		onResize(false);
	}

	private void calculateGrid()
	{
		setVirtualExtend(getCols() * getCellWidth(), getRows() * getCellHeight());
		setRedraw(false);
		try
		{
			// scroll to end (unless scroll lock is active)
			scrollToEnd();
			getParent().layout();
		}
		finally
		{
			setRedraw(true);
		}
	}

	void scrollToEnd()
	{
		if (!fScrollLock)
		{
			int y = -(getRows() * getCellHeight() - getClientArea().height);
			if (y > 0)
			{
				y = 0;
			}
			Rectangle v = getViewRectangle();
			if (v.y != -y)
			{
				setVirtualOrigin(v.x, y);
			}
			// make sure the scroll area is correct:
			scrollY(getVerticalBar());
			scrollX(getHorizontalBar());
		}
	}

	/**
	 * @return true if the cursor should be shown on output....
	 */
	public boolean isScrollLock()
	{
		return fScrollLock;
	}

	/**
	 * If set then if the size changes
	 */
	public void setScrollLock(boolean scrollLock)
	{
		fScrollLock = scrollLock;
	}

	protected void repaintRange(int col, int line, int width, int height)
	{
		Point origin = cellToOriginOnScreen(col, line);
		Rectangle r = new Rectangle(origin.x, origin.y, width * getCellWidth(), height * getCellHeight());
		repaint(r);
	}

	protected void drawLine(GC gc, int line, int x, int y, int colFirst, int colLast)
	{
		fCellRenderer.drawLine(fCellCanvasModel, gc, line, x, y, colFirst, colLast);
	}

	protected Color getTerminalBackgroundColor()
	{
		return fCellRenderer.getDefaultBackgroundColor();
	}

	protected void visibleCellRectangleChanged(int x, int y, int width, int height)
	{
		fCellCanvasModel.setVisibleRectangle(y, x, height, width);
		update();
	}

	protected int getCols()
	{
		return fCellCanvasModel.getTerminalText().getWidth();
	}

	protected int getRows()
	{
		return fCellCanvasModel.getTerminalText().getHeight();
	}

	public String getSelectionText()
	{
		// TODO -- create a hasSelectionMethod!
		return fCellCanvasModel.getSelectedText();
	}

	public void copy()
	{
		Clipboard clipboard = new Clipboard(getDisplay());
		clipboard.setContents(new Object[] { getSelectionText() }, new Transfer[] { TextTransfer.getInstance() });
		clipboard.dispose();
	}

	public void selectAll()
	{
		fCellCanvasModel.setSelection(0, fCellCanvasModel.getTerminalText().getHeight(), 0, fCellCanvasModel
				.getTerminalText().getWidth());
		fCellCanvasModel.setSelectionAnchor(new Point(0, 0));
	}

	public boolean isEmpty()
	{
		return false;
	}

	/**
	 * Gets notified when the visible size of the terminal changes. This should update the model!
	 */
	public interface ResizeListener
	{
		void sizeChanged(int lines, int columns);
	}

	/**
	 * @param listener
	 *            this listener gets notified, when the size of the widget changed. It should change the dimensions of
	 *            the underlying terminaldata
	 */
	public void addResizeHandler(ResizeListener listener)
	{
		if (fResizeListener != null)
			throw new IllegalArgumentException("There can be at most one listener at the moment!"); //$NON-NLS-1$
		fResizeListener = listener;

		// Bug 281328: [terminal] The very first few characters might be missing in
		// the terminal control if opened and connected programmatically
		//
		// In case the terminal had not been visible yet or is too small (less than one
		// line visible), the terminal should have a minimum size to avoid RuntimeExceptions.
		Rectangle bonds = getClientArea();
		if (bonds.height < getCellHeight() || bonds.width < getCellWidth())
		{
			// Widget not realized yet, or minimized to < 1 item:
			// Just tell the listener our min size
			fResizeListener.sizeChanged(getMinLines(), getMinColumns());
		}
		else
		{
			// Widget realized: compute actual size and force telling the listener
			onResize(true);
		}
	}

	public void onFontChange()
	{
		fCellRenderer.onFontChange();
		setCellWidth(fCellRenderer.getCellWidth());
		setCellHeight(fCellRenderer.getCellHeight());
		calculateGrid();
	}

	public void setInvertedColors(boolean invert)
	{
		fCellRenderer.setInvertedColors(invert);
		redraw();
	}

	/**
	 * @return true if the cursor is enabled (blinking). By default the cursor is not enabled.
	 */
	public boolean isCursorEnabled()
	{
		return fCursorEnabled;
	}

	/**
	 * @param enabled
	 *            enabling means that the cursor blinks
	 */
	public void setCursorEnabled(boolean enabled)
	{
		if (enabled != fCursorEnabled)
		{
			fCursorEnabled = enabled;
			fCellCanvasModel.setCursorEnabled(fCursorEnabled);
		}

	}

}
