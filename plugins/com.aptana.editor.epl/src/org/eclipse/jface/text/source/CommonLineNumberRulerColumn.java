/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Nikolay Botev <bono8106@hotmail.com> - [rulers] Shift clicking in line number column doesn't select range - https://bugs.eclipse.org/bugs/show_bug.cgi?id=32166
 *     Nikolay Botev <bono8106@hotmail.com> - [rulers] Clicking in line number ruler should not trigger annotation ruler - https://bugs.eclipse.org/bugs/show_bug.cgi?id=40889
 *******************************************************************************/
package org.eclipse.jface.text.source;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITextViewerExtension;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.IViewportListener;
import org.eclipse.jface.text.JFaceTextUtil;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.util.Util;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TypedListener;

import com.aptana.core.logging.IdeLog;
import com.aptana.editor.epl.EditorEplPlugin;


/**
 * A vertical ruler column displaying line numbers.
 * Clients usually instantiate and configure object of this class.
 *
 * @since 2.0
 */
public class CommonLineNumberRulerColumn extends LineNumberRulerColumn {

	/**
	 * Used to map document lines to widget lines and visual lines for word wrap and line numbers.
	 */
	class LineMap
	{
		int documentLine;
		int widgetLine;
		int visualLine;

		LineMap(int docLine, int widgetLine, int visualLine)
		{
			this.documentLine = docLine;
			this.widgetLine = widgetLine;
			this.visualLine = visualLine;
		}

		@Override
		public String toString()
		{
			return MessageFormat.format("LineMap <Document: {0}, widget: {1}, visual: {2}>", documentLine, widgetLine, visualLine); //$NON-NLS-1$
		}
	}

	/**
	 * Internal listener class.
	 */
	class InternalListener implements IViewportListener, ITextListener {

		/**
		 * @since 3.1
		 */
		private boolean fCachedRedrawState= true;

		/*
		 * @see IViewportListener#viewportChanged(int)
		 */
		public void viewportChanged(int verticalPosition) {
			if (fCachedRedrawState && verticalPosition != fScrollPos)
				redraw();
		}

		/*
		 * @see ITextListener#textChanged(TextEvent)
		 */
		public void textChanged(TextEvent event) {

			fCachedRedrawState= event.getViewerRedrawState();
			if (!fCachedRedrawState)
				return;

			if (updateNumberOfDigits()) {
				computeIndentations();
				layout(event.getViewerRedrawState());
				return;
			}

			boolean viewerCompletelyShown= isViewerEntirelyShown();
			if (viewerCompletelyShown || fSensitiveToTextChanges || event.getDocumentEvent() == null)
				doPostRedraw();
			fSensitiveToTextChanges= viewerCompletelyShown;
		}
	}

	/**
	 * Handles all the mouse interaction in this line number ruler column.
	 */
	class MouseHandler implements MouseListener, MouseMoveListener {

		/** The cached view port size. */
		private int fCachedViewportSize;
		/** The area of the line at which line selection started. */
		private int fStartLineOffset;
		/** The number of the line at which line selection started. */
		private int fStartLineNumber;
		/** The auto scroll direction. */
		private int fAutoScrollDirection;
		/* @since 3.2 */
		private boolean fIsListeningForMove= false;

		/*
		 * @see org.eclipse.swt.events.MouseListener#mouseUp(org.eclipse.swt.events.MouseEvent)
		 */
		public void mouseUp(MouseEvent event) {
			// see bug 45700
			if (event.button == 1) {
				stopSelecting();
				stopAutoScroll();
			}
		}

		/*
		 * @see org.eclipse.swt.events.MouseListener#mouseDown(org.eclipse.swt.events.MouseEvent)
		 */
		public void mouseDown(MouseEvent event) {
			fParentRuler.setLocationOfLastMouseButtonActivity(event.x, event.y);
			// see bug 45700
			if (event.button == 1) {
				startSelecting((event.stateMask & SWT.SHIFT) != 0);
			}
		}

		/*
		 * @see org.eclipse.swt.events.MouseListener#mouseDoubleClick(org.eclipse.swt.events.MouseEvent)
		 */
		public void mouseDoubleClick(MouseEvent event) {
			fParentRuler.setLocationOfLastMouseButtonActivity(event.x, event.y);
			stopSelecting();
			stopAutoScroll();
		}

		/*
		 * @see org.eclipse.swt.events.MouseMoveListener#mouseMove(org.eclipse.swt.events.MouseEvent)
		 */
		public void mouseMove(MouseEvent event) {
			if (fIsListeningForMove && !autoScroll(event)) {
				int newLine= fParentRuler.toDocumentLineNumber(event.y);
				expandSelection(newLine);
			}
			fParentRuler.setLocationOfLastMouseButtonActivity(event.x, event.y);
		}

		/**
		 * Called when line drag selection started. Adds mouse move and track
		 * listeners to this column's control.
		 *
		 * @param expandExistingSelection if <code>true</code> the existing selection will be expanded,
		 * 			otherwise a new selection is started
		 */
		private void startSelecting(boolean expandExistingSelection) {
			try {

				// select line
				IDocument document= fCachedTextViewer.getDocument();
				int lineNumber= fParentRuler.getLineOfLastMouseButtonActivity();
				final StyledText textWidget= fCachedTextViewer.getTextWidget();
				if (textWidget != null && !textWidget.isFocusControl())
					textWidget.setFocus();
				if (expandExistingSelection && fCachedTextViewer instanceof ITextViewerExtension5 && textWidget != null) {
					ITextViewerExtension5 extension5= ((ITextViewerExtension5)fCachedTextViewer);
					// Find model cursor position
					int widgetCaret= textWidget.getCaretOffset();
					int modelCaret= extension5.widgetOffset2ModelOffset(widgetCaret);
					// Find model selection range
					Point selection= fCachedTextViewer.getSelectedRange();
					// Start from tail of selection range (opposite of cursor position)
					int startOffset= modelCaret == selection.x ? selection.x + selection.y : selection.x;

					fStartLineNumber= document.getLineOfOffset(startOffset);
					fStartLineOffset= startOffset;

					expandSelection(lineNumber);
				} else {
					fStartLineNumber= lineNumber;
					fStartLineOffset= document.getLineInformation(fStartLineNumber).getOffset();
					fCachedTextViewer.setSelectedRange(fStartLineOffset, 0);
				}
				fCachedViewportSize= getVisibleLinesInViewport();

				// prepare for drag selection
				fIsListeningForMove= true;

			} catch (BadLocationException x) {
			}
		}

		/**
		 * Called when line drag selection stopped. Removes all previously
		 * installed listeners from this column's control.
		 */
		private void stopSelecting() {
			// drag selection stopped
			fIsListeningForMove= false;
		}

		/**
		 * Expands the line selection from the remembered start line to the
		 * given line.
		 *
		 * @param lineNumber the line to which to expand the selection
		 */
		private void expandSelection(int lineNumber) {
			try {

				IDocument document= fCachedTextViewer.getDocument();
				IRegion lineInfo= document.getLineInformation(lineNumber);

				Display display= fCachedTextWidget.getDisplay();
				Point absolutePosition= display.getCursorLocation();
				Point relativePosition= fCachedTextWidget.toControl(absolutePosition);

				int offset;

				if (relativePosition.x < 0)
					offset= lineInfo.getOffset();
				else {
					try {
						int widgetOffset= fCachedTextWidget.getOffsetAtLocation(relativePosition);
						Point p= fCachedTextWidget.getLocationAtOffset(widgetOffset);
						if (p.x > relativePosition.x)
							widgetOffset--;

						// Convert to model offset
						if (fCachedTextViewer instanceof ITextViewerExtension5) {
							ITextViewerExtension5 extension= (ITextViewerExtension5)fCachedTextViewer;
							offset= extension.widgetOffset2ModelOffset(widgetOffset);
						} else
							offset= widgetOffset + fCachedTextViewer.getVisibleRegion().getOffset();

					} catch (IllegalArgumentException ex) {
						int lineEndOffset= lineInfo.getOffset() + lineInfo.getLength();

						// Convert to widget offset
						int lineEndWidgetOffset;
						if (fCachedTextViewer instanceof ITextViewerExtension5) {
							ITextViewerExtension5 extension= (ITextViewerExtension5)fCachedTextViewer;
							lineEndWidgetOffset= extension.modelOffset2WidgetOffset(lineEndOffset);
						} else
							lineEndWidgetOffset= lineEndOffset - fCachedTextViewer.getVisibleRegion().getOffset();

						Point p= fCachedTextWidget.getLocationAtOffset(lineEndWidgetOffset);
						if (p.x < relativePosition.x)
							offset= lineEndOffset;
						else
							offset= lineInfo.getOffset();
					}
				}

				int start= Math.min(fStartLineOffset, offset);
				int end= Math.max(fStartLineOffset, offset);

				if (lineNumber < fStartLineNumber)
					fCachedTextViewer.setSelectedRange(end, start - end);
				else
					fCachedTextViewer.setSelectedRange(start, end - start);

			} catch (BadLocationException x) {
			}
		}

		/**
		 * Called when auto scrolling stopped. Clears the auto scroll direction.
		 */
		private void stopAutoScroll() {
			fAutoScrollDirection= SWT.NULL;
		}

		/**
		 * Called on drag selection.
		 *
		 * @param event the mouse event caught by the mouse move listener
		 * @return <code>true</code> if scrolling happened, <code>false</code> otherwise
		 */
		private boolean autoScroll(MouseEvent event) {
			Rectangle area= fCanvas.getClientArea();

			if (event.y > area.height) {
				autoScroll(SWT.DOWN);
				return true;
			}

			if (event.y < 0) {
				autoScroll(SWT.UP);
				return true;
			}

			stopAutoScroll();
			return false;
		}

		/**
		 * Scrolls the viewer into the given direction.
		 *
		 * @param direction the scroll direction
		 */
		private void autoScroll(int direction) {

			if (fAutoScrollDirection == direction)
				return;

			final int TIMER_INTERVAL= 5;
			final Display display= fCanvas.getDisplay();
			Runnable timer= null;
			switch (direction) {
				case SWT.UP:
					timer= new Runnable() {
						public void run() {
							if (fAutoScrollDirection == SWT.UP) {
								int top= getInclusiveTopIndex();
								if (top > 0) {
									fCachedTextViewer.setTopIndex(top -1);
									expandSelection(top -1);
									display.timerExec(TIMER_INTERVAL, this);
								}
							}
						}
					};
					break;
				case  SWT.DOWN:
					timer= new Runnable() {
						public void run() {
							if (fAutoScrollDirection == SWT.DOWN) {
								int top= getInclusiveTopIndex();
								fCachedTextViewer.setTopIndex(top +1);
								expandSelection(top +1 + fCachedViewportSize);
								display.timerExec(TIMER_INTERVAL, this);
							}
						}
					};
					break;
			}

			if (timer != null) {
				fAutoScrollDirection= direction;
				display.timerExec(TIMER_INTERVAL, timer);
			}
		}

		/**
		 * Returns the viewer's first visible line, even if only partially visible.
		 *
		 * @return the viewer's first visible line
		 */
		private int getInclusiveTopIndex() {
			if (fCachedTextWidget != null && !fCachedTextWidget.isDisposed()) {
				return JFaceTextUtil.getPartialTopIndex(fCachedTextViewer);
			}
			return -1;
		}
	}

	/**
	 * <code>true</code> if we're on a Mac, where "new GC(canvas)" is expensive.
	 * @see <a href="https://bugs.eclipse.org/298936">bug 298936</a>
	 * @since 3.6
	 */
	private static final boolean IS_MAC= Util.isMac();

	/** This column's parent ruler */
	private CompositeRuler fParentRuler;
	/** Cached text viewer */
	private ITextViewer fCachedTextViewer;
	/** Cached text widget */
	private StyledText fCachedTextWidget;
	/** The columns canvas */
	private Canvas fCanvas;
	/** Cache for the actual scroll position in pixels */
	private int fScrollPos;
	/** The drawable for double buffering */
	private Image fBuffer;
	/** The internal listener */
	private InternalListener fInternalListener= new InternalListener();
	/** The font of this column */
	private Font fFont;
	/** The indentation cache */
	private int[] fIndentation;
	/** Indicates whether this column reacts on text change events */
	private boolean fSensitiveToTextChanges= false;
	/** The foreground color */
	private Color fForeground;
	/** The background color */
	private Color fBackground;
	/** Cached number of displayed digits */
	private int fCachedNumberOfDigits= -1;
	/** Flag indicating whether a relayout is required */
	private boolean fRelayoutRequired= false;
	/**
	 * Redraw runnable lock
	 * @since 3.0
	 */
	private Object fRunnableLock= new Object();
	/**
	 * Redraw runnable state
	 * @since 3.0
	 */
	private boolean fIsRunnablePosted= false;
	/**
	 * Redraw runnable
	 * @since 3.0
	 */
	private Runnable fRunnable= new Runnable() {
		public void run() {
			synchronized (fRunnableLock) {
				fIsRunnablePosted= false;
			}
			redraw();
		}
	};
	/* @since 3.2 */
	private MouseHandler fMouseHandler;


	/**
	 * Constructs a new vertical ruler column.
	 */
	public CommonLineNumberRulerColumn() {
	}

	/**
	 * Sets the foreground color of this column.
	 *
	 * @param foreground the foreground color
	 */
	public void setForeground(Color foreground) {
		fForeground= foreground;
	}

	/**
	 * Returns the foreground color being used to print the line numbers.
	 *
	 * @return the configured foreground color
	 * @since 3.0
	 */
	protected Color getForeground() {
		return fForeground;
	}

	/**
	 * Sets the background color of this column.
	 *
	 * @param background the background color
	 */
	public void setBackground(Color background) {
		fBackground= background;
		if (fCanvas != null && !fCanvas.isDisposed())
			fCanvas.setBackground(getBackground(fCanvas.getDisplay()));
	}

	/**
	 * Returns the System background color for list widgets.
	 *
	 * @param display the display
	 * @return the System background color for list widgets
	 */
	protected Color getBackground(Display display) {
		if (fBackground == null)
			return display.getSystemColor(SWT.COLOR_LIST_BACKGROUND);
		return fBackground;
	}

	/*
	 * @see IVerticalRulerColumn#getControl()
	 */
	public Control getControl() {
		return fCanvas;
	}

	/*
	 * @see IVerticalRuleColumnr#getWidth
	 */
	public int getWidth() {
		return fIndentation[0];
	}

	/**
	 * Computes the number of digits to be displayed. Returns
	 * <code>true</code> if the number of digits changed compared
	 * to the previous call of this method. If the method is called
	 * for the first time, the return value is also <code>true</code>.
	 *
	 * @return whether the number of digits has been changed
	 * @since 3.0
	 */
	protected boolean updateNumberOfDigits() {
		if (fCachedTextViewer == null)
			return false;

		int digits= computeNumberOfDigits();

		if (fCachedNumberOfDigits != digits) {
			fCachedNumberOfDigits= digits;
			return true;
		}

		return false;
	}

	/**
	 * Does the real computation of the number of digits. Subclasses may override this method if
	 * they need extra space on the line number ruler.
	 *
	 * @return the number of digits to be displayed on the line number ruler.
	 */
	protected int computeNumberOfDigits() {
		IDocument document= fCachedTextViewer.getDocument();
		int lines= document == null ? 0 : document.getNumberOfLines();

		int digits= 2;
		while (lines > Math.pow(10, digits) -1) {
			++digits;
		}
		return digits;
	}

	/**
	 * Layouts the enclosing viewer to adapt the layout to changes of the
	 * size of the individual components.
	 *
	 * @param redraw <code>true</code> if this column can be redrawn
	 */
	protected void layout(boolean redraw) {
		if (!redraw) {
			fRelayoutRequired= true;
			return;
		}

		fRelayoutRequired= false;
		if (fCachedTextViewer instanceof ITextViewerExtension) {
			ITextViewerExtension extension= (ITextViewerExtension) fCachedTextViewer;
			Control control= extension.getControl();
			if (control instanceof Composite && !control.isDisposed()) {
				Composite composite= (Composite) control;
				composite.layout(true);
			}
		}
	}

	/**
	 * Computes the indentations for the given font and stores them in
	 * <code>fIndentation</code>.
	 */
	protected void computeIndentations() {
		if (fCanvas == null || fCanvas.isDisposed())
			return;

		GC gc= new GC(fCanvas);
		try {

			gc.setFont(fCanvas.getFont());

			fIndentation= new int[fCachedNumberOfDigits + 1];

			char[] nines= new char[fCachedNumberOfDigits];
			Arrays.fill(nines, '9');
			String nineString= new String(nines);
			Point p= gc.stringExtent(nineString);
			fIndentation[0]= p.x;

			for (int i= 1; i <= fCachedNumberOfDigits; i++) {
				p= gc.stringExtent(nineString.substring(0, i));
				fIndentation[i]= fIndentation[0] - p.x;
			}

		} finally {
			gc.dispose();
		}
	}

	/*
	 * @see IVerticalRulerColumn#createControl(CompositeRuler, Composite)
	 */
	public Control createControl(CompositeRuler parentRuler, Composite parentControl) {

		fParentRuler= parentRuler;
		fCachedTextViewer= parentRuler.getTextViewer();
		fCachedTextWidget= fCachedTextViewer.getTextWidget();

		fCanvas= new Canvas(parentControl, SWT.NO_FOCUS ) {
 			/*
 			 * @see org.eclipse.swt.widgets.Control#addMouseListener(org.eclipse.swt.events.MouseListener)
 			 * @since 3.4
 			 */
 			public void addMouseListener(MouseListener listener) {
				// see bug 40889, bug 230073 and AnnotationRulerColumn#isPropagatingMouseListener()
				if (listener == fMouseHandler)
					super.addMouseListener(listener);
				else {
					TypedListener typedListener= null;
					if (listener != null)
						typedListener= new TypedListener(listener);
					addListener(SWT.MouseDoubleClick, typedListener);
				}
			}
		};
		fCanvas.setBackground(getBackground(fCanvas.getDisplay()));
		fCanvas.setForeground(fForeground);

		fCanvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent event) {
				if (fCachedTextViewer != null)
					doubleBufferPaint(event.gc);
			}
		});

		fCanvas.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				handleDispose();
				fCachedTextViewer= null;
				fCachedTextWidget= null;
			}
		});

		fMouseHandler= new MouseHandler();
		fCanvas.addMouseListener(fMouseHandler);
		fCanvas.addMouseMoveListener(fMouseHandler);

		if (fCachedTextViewer != null) {

			fCachedTextViewer.addViewportListener(fInternalListener);
			fCachedTextViewer.addTextListener(fInternalListener);

			if (fFont == null) {
				if (fCachedTextWidget != null && !fCachedTextWidget.isDisposed())
					fFont= fCachedTextWidget.getFont();
			}
		}

		if (fFont != null)
			fCanvas.setFont(fFont);

		updateNumberOfDigits();
		computeIndentations();
		return fCanvas;
	}

	/**
	 * Disposes the column's resources.
	 */
	protected void handleDispose() {

		if (fCachedTextViewer != null) {
			fCachedTextViewer.removeViewportListener(fInternalListener);
			fCachedTextViewer.removeTextListener(fInternalListener);
		}

		if (fBuffer != null) {
			fBuffer.dispose();
			fBuffer= null;
		}
	}

	/**
	 * Double buffer drawing.
	 *
	 * @param dest the GC to draw into
	 */
	private void doubleBufferPaint(GC dest) {

		Point size= fCanvas.getSize();

		if (size.x <= 0 || size.y <= 0)
			return;

		if (fBuffer != null) {
			Rectangle r= fBuffer.getBounds();
			if (r.width != size.x || r.height != size.y) {
				fBuffer.dispose();
				fBuffer= null;
			}
		}
		if (fBuffer == null)
			fBuffer= new Image(fCanvas.getDisplay(), size.x, size.y);

		GC gc= new GC(fBuffer);
		gc.setFont(fCanvas.getFont());
		if (fForeground != null)
			gc.setForeground(fForeground);

		try {
			gc.setBackground(getBackground(fCanvas.getDisplay()));
			gc.fillRectangle(0, 0, size.x, size.y);

			ILineRange visibleLines= JFaceTextUtil.getVisibleModelLines(fCachedTextViewer);
			if (visibleLines == null)
				return;
			fScrollPos= fCachedTextWidget.getTopPixel();
			doPaint(gc, visibleLines);
		} finally {
			gc.dispose();
		}

		dest.drawImage(fBuffer, 0, 0);
	}

	/**
	 * Returns the view port height in lines.
	 *
	 * @return the view port height in lines
	 * @deprecated as of 3.2 the number of lines in the viewport cannot be computed because
	 *             StyledText supports variable line heights
	 */
	protected int getVisibleLinesInViewport() {
		return getVisibleLinesInViewport(fCachedTextWidget);
	}


	/**
	 * Returns <code>true</code> if the viewport displays the entire viewer contents, i.e. the
	 * viewer is not vertically scrollable.
	 *
	 * @return <code>true</code> if the viewport displays the entire contents, <code>false</code> otherwise
	 * @since 3.2
	 */
	protected final boolean isViewerEntirelyShown() {
		return JFaceTextUtil.isShowingEntireContents(fCachedTextWidget);
	}

	void doPaint(GC gc, ILineRange visibleLines)
	{

		Display display = fCachedTextWidget.getDisplay();

		// draw diff info
		int y = -JFaceTextUtil.getHiddenTopLinePixels(fCachedTextWidget);
		List<LineMap> lineMap = getLineMap(fCachedTextWidget, visibleLines);
		int lineCount = fCachedTextWidget.getLineCount();
		int lineMapSize = lineMap.size();
		for (int i = 0; i < lineMapSize; i++)
		{
			LineMap map = lineMap.get(i);
			int line = map.documentLine;
			int widgetLine = map.widgetLine;
			try
			{
				int lineHeight = fCachedTextWidget.getLineHeight(fCachedTextWidget.getOffsetAtLine(widgetLine));
				paintLine(line, y, lineHeight, gc, display);

				int distanceFromPrevious = 1;
				if (line < lineCount - 1 && i + 1 < lineMapSize)
				{
					distanceFromPrevious = lineMap.get(i + 1).visualLine - map.visualLine;
				}

				y += (lineHeight * distanceFromPrevious);
			}
			catch (Exception e)
			{
				IdeLog.logError(EditorEplPlugin.getDefault(), MessageFormat.format("Error painting line number: {0}", map), e); //$NON-NLS-1$
			}
		}

	}

	List<LineMap> getLineMap(StyledText text, ILineRange visibleLines)
	{
		List<LineMap> lineMap = new ArrayList<LineMap>();
		int endLine = end(visibleLines);
		int firstVisibleLineY = 0;
		int lineHeight = 1;
		try
		{
			lineHeight = text.getLineHeight();
			int widgetLine = JFaceTextUtil.modelLineToWidgetLine(fCachedTextViewer, visibleLines.getStartLine());
			firstVisibleLineY = text.getLocationAtOffset(text.getOffsetAtLine(widgetLine)).y;
		}
		catch (Exception e)
		{
			IdeLog.logError(EditorEplPlugin.getDefault(), "Error translating starting line number to y offset in text widget", e); //$NON-NLS-1$
		}
		for (int i = visibleLines.getStartLine(); i < endLine; i++)
		{
			if (i == 0)
			{
				lineMap.add(new LineMap(0, 0, 0));
			}
			else
			{
				int widgetI = JFaceTextUtil.modelLineToWidgetLine(fCachedTextViewer, i);
				if (widgetI == -1)
				{
					// Line is hidden/folded
					continue;
				}
				try
				{
					if (widgetI >= 0 && widgetI < text.getLineCount())
					{
						int lineY = text.getLocationAtOffset(text.getOffsetAtLine(widgetI)).y;
						lineMap.add(new LineMap(i, widgetI, visibleLines.getStartLine()
								+ ((lineY - firstVisibleLineY) / lineHeight)));
					}
				}
				catch (Exception e)
				{
					IdeLog.logError(EditorEplPlugin.getDefault(), MessageFormat.format("Error translating line number {0} to y offset in text widget", i), e); //$NON-NLS-1$
				}
			}
		}

		return lineMap;
	}

	/* @since 3.2 */
	private static int end(ILineRange range) {
		return range.getStartLine() + range.getNumberOfLines();
	}

	/**
	 * Computes the string to be printed for <code>line</code>. The default implementation returns
	 * <code>Integer.toString(line + 1)</code>.
	 *
	 * @param line the line number for which the line number string is generated
	 * @return the string to be printed on the line number bar for <code>line</code>
	 * @since 3.0
	 */
	protected String createDisplayString(int line) {
		return Integer.toString(line + 1);
	}

	/**
	 * Returns the difference between the baseline of the widget and the
	 * baseline as specified by the font for <code>gc</code>. When drawing
	 * line numbers, the returned bias should be added to obtain text lined up
	 * on the correct base line of the text widget.
	 *
	 * @param gc the <code>GC</code> to get the font metrics from
	 * @param widgetLine the widget line
	 * @return the baseline bias to use when drawing text that is lined up with
	 *         <code>fCachedTextWidget</code>
	 * @since 3.2
	 */
	private int getBaselineBias(GC gc, int widgetLine) {
		/*
		 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=62951
		 * widget line height may be more than the font height used for the
		 * line numbers, since font styles (bold, italics...) can have larger
		 * font metrics than the simple font used for the numbers.
		 */
		int offset= fCachedTextWidget.getOffsetAtLine(widgetLine);
		int widgetBaseline= fCachedTextWidget.getBaseline(offset);

		FontMetrics fm= gc.getFontMetrics();
		int fontBaseline= fm.getAscent() + fm.getLeading();
		int baselineBias= widgetBaseline - fontBaseline;
		return Math.max(0, baselineBias);
	}

	/**
	 * Paints the line. After this method is called the line numbers are painted on top
	 * of the result of this method.
	 *
	 * @param line the line of the document which the ruler is painted for
	 * @param y the y-coordinate of the box being painted for <code>line</code>, relative to <code>gc</code>
	 * @param lineheight the height of one line (and therefore of the box being painted)
	 * @param gc the drawing context the client may choose to draw on.
	 * @param display the display the drawing occurs on
	 * @since 3.0
	 */
	protected void paintLine(int line, int y, int lineheight, GC gc, Display display) {
		int widgetLine= JFaceTextUtil.modelLineToWidgetLine(fCachedTextViewer, line);

		String s= createDisplayString(line);
		int indentation= fIndentation[s.length()];
		int baselineBias= getBaselineBias(gc, widgetLine);
		gc.drawString(s, indentation, y + baselineBias, true);
	}

	/**
	 * Triggers a redraw in the display thread.
	 *
	 * @since 3.0
	 */
	protected final void doPostRedraw() {
		if (fCanvas != null && !fCanvas.isDisposed()) {
			Display d= fCanvas.getDisplay();
			if (d != null) {
				synchronized (fRunnableLock) {
					if (fIsRunnablePosted)
						return;
					fIsRunnablePosted= true;
				}
				d.asyncExec(fRunnable);
			}
		}
	}

	/*
	 * @see IVerticalRulerColumn#redraw()
	 */
	public void redraw() {

		if (fRelayoutRequired) {
			layout(true);
			return;
		}

		if (fCachedTextViewer != null && fCanvas != null && !fCanvas.isDisposed()) {
			if (IS_MAC) {
				fCanvas.redraw();
				fCanvas.update();
			} else {
				GC gc= new GC(fCanvas);
				doubleBufferPaint(gc);
				gc.dispose();
			}
		}
	}

	/*
	 * @see IVerticalRulerColumn#setModel(IAnnotationModel)
	 */
	public void setModel(IAnnotationModel model) {
	}

	/*
	 * @see IVerticalRulerColumn#setFont(Font)
	 */
	public void setFont(Font font) {
		fFont= font;
		if (fCanvas != null && !fCanvas.isDisposed()) {
			fCanvas.setFont(fFont);
			updateNumberOfDigits();
			computeIndentations();
		}
	}

	/**
	 * Returns the parent (composite) ruler of this ruler column.
	 *
	 * @return the parent ruler
	 * @since 3.0
	 */
	protected CompositeRuler getParentRuler() {
		return fParentRuler;
	}


	/**
	 * Returns the number of lines in the view port.
	 *
	 * @param textWidget the styled text widget
	 * @return the number of lines visible in the view port <code>-1</code> if there's no client
	 *         area
	 * @deprecated this method should not be used - it relies on the widget using a uniform line
	 *             height
	 */
	static int getVisibleLinesInViewport(StyledText textWidget) {
		if (textWidget != null) {
			Rectangle clArea= textWidget.getClientArea();
			if (!clArea.isEmpty()) {
				int firstPixel= 0;
				int lastPixel= clArea.height - 1; // XXX: what about margins? don't take trims as they include scrollbars
				int first= JFaceTextUtil.getLineIndex(textWidget, firstPixel);
				int last= JFaceTextUtil.getLineIndex(textWidget, lastPixel);
				return last - first;
			}
		}
		return -1;
	}

}
