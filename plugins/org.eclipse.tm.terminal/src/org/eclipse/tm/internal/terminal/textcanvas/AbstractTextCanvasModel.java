/*******************************************************************************
 * Copyright (c) 2007, 2008 Wind River Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Michael Scharf (Wind River) - initial API and implementation
 * Martin Oberhuber (Wind River) - [168197] Fix Terminal for CDC-1.1/Foundation-1.1
 *******************************************************************************/
package org.eclipse.tm.internal.terminal.textcanvas;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.graphics.Point;
import org.eclipse.tm.terminal.model.ITerminalTextDataReadOnly;
import org.eclipse.tm.terminal.model.ITerminalTextDataSnapshot;

abstract public class AbstractTextCanvasModel implements ITextCanvasModel {
	protected List fListeners = new ArrayList();
	private int fCursorLine;
	private int fCursorColumn;
	private boolean fShowCursor;
	private long fCursorTime;
	private boolean fCursorIsEnabled;
	private final ITerminalTextDataSnapshot fSnapshot;
	private int fLines;

	private int fSelectionStartLine=-1;
	private int fSeletionEndLine;
	private int fSelectionStartCoumn;
	private int fSelectionEndColumn;
	private ITerminalTextDataSnapshot fSelectionSnapshot;
	private String fCurrentSelection=""; //$NON-NLS-1$
	private final Point fSelectionAnchor=new Point(0,0);
	/**
	 * do not update while update is running
	 */
	boolean fInUpdate;
	private int fCols;

	public AbstractTextCanvasModel(ITerminalTextDataSnapshot snapshot) {
		fSnapshot=snapshot;
		fLines=fSnapshot.getHeight();
	}
	public void addCellCanvasModelListener(ITextCanvasModelListener listener) {
		fListeners.add(listener);
	}

	public void removeCellCanvasModelListener(ITextCanvasModelListener listener) {
		fListeners.remove(listener);
	}

	protected void fireCellRangeChanged(int x, int y, int width, int height) {
		for (Iterator iter = fListeners.iterator(); iter.hasNext();) {
			ITextCanvasModelListener listener = (ITextCanvasModelListener) iter.next();
			listener.rangeChanged(x, y, width, height);
		}
	}
	protected void fireDimensionsChanged( int width,int height) {
		for (Iterator iter = fListeners.iterator(); iter.hasNext();) {
			ITextCanvasModelListener listener = (ITextCanvasModelListener) iter.next();
			listener.dimensionsChanged(width,height);
		}

	}
	protected void fireTerminalDataChanged() {
		for (Iterator iter = fListeners.iterator(); iter.hasNext();) {
			ITextCanvasModelListener listener = (ITextCanvasModelListener) iter.next();
			listener.terminalDataChanged();
		}

	}

	public ITerminalTextDataReadOnly getTerminalText() {
		return fSnapshot;
	}
	protected ITerminalTextDataSnapshot getSnapshot() {
		return fSnapshot;
	}
	protected void updateSnapshot() {
		if(!fInUpdate && fSnapshot.isOutOfDate()) {
			fInUpdate=true;
			try {
				fSnapshot.updateSnapshot(false);
				if(fSnapshot.hasTerminalChanged())
					fireTerminalDataChanged();
				// TODO why does hasDimensionsChanged not work??????
				//			if(fSnapshot.hasDimensionsChanged())
				//				fireDimensionsChanged();
				if(fLines!=fSnapshot.getHeight() || fCols!=fSnapshot.getWidth()) {
					fireDimensionsChanged(fSnapshot.getWidth(),fSnapshot.getHeight());
					fLines=fSnapshot.getHeight();
					fCols=fSnapshot.getWidth();
				}
				int y=fSnapshot.getFirstChangedLine();
				// has any line changed?
				if(y<Integer.MAX_VALUE) {
					int height=fSnapshot.getLastChangedLine()-y+1;
					fireCellRangeChanged(0, y, fSnapshot.getWidth(), height);
				}

			} finally {
				fInUpdate=false;
			}
		}
	}
	/**
	 * must be called from the UI thread
	 */
	public void update() {
		// do the poll....
		updateSnapshot();
		updateSelection();
		updateCursor();
	}


	public int getCursorColumn() {
		return fCursorColumn;
	}

	public int getCursorLine() {
		return fCursorLine;
	}

	public boolean isCursorOn() {
		return fShowCursor && fCursorIsEnabled;
	}
	/**
	 * should be called regularly to draw an update of the
	 * blinking cursor
	 */
	protected void updateCursor() {
		if(!fCursorIsEnabled)
			return;
		int cursorLine=getSnapshot().getCursorLine();
		int cursorColumn=getSnapshot().getCursorColumn();
		// if cursor at the end put it to the end of the
		// last line...
		if(cursorLine>=getSnapshot().getHeight()) {
			cursorLine=getSnapshot().getHeight()-1;
			cursorColumn=getSnapshot().getWidth()-1;
		}
		// has the cursor moved?
		if(fCursorLine!=cursorLine || fCursorColumn!=cursorColumn) {
			// hide the old cursor!
			fShowCursor=false;
			// clean the previous cursor
			// bug 206363: paint also the char to the left and right of the cursor - see also below
			int col=fCursorColumn;
			int width=2;
			if(col>0) {
				col--;
				width++;
			}
			fireCellRangeChanged(col, fCursorLine, width, 1);
			// the cursor is shown when it moves!
			fShowCursor=true;
			fCursorTime=System.currentTimeMillis();
			fCursorLine=cursorLine;
			fCursorColumn=cursorColumn;
			// and draw the new cursor
			fireCellRangeChanged(fCursorColumn, fCursorLine, 1, 1);
		} else {
			long t=System.currentTimeMillis();
			// TODO make the cursor blink time customisable
			if(t-fCursorTime>500) {
				fShowCursor=!fShowCursor;
				fCursorTime=t;
				// on some windows machines, there is some left
				// over when updating the cursor .
				// https://bugs.eclipse.org/bugs/show_bug.cgi?id=206363
				int col=fCursorColumn;
				int width=2;
				if(col>0) {
					col--;
					width++;
				}
				fireCellRangeChanged(col, fCursorLine, width, 1);
			}
		}
	}
	public void setVisibleRectangle(int startLine, int startCol, int height, int width) {
		fSnapshot.setInterestWindow(Math.max(0,startLine), Math.max(1,height));
		update();
	}
	protected void showCursor(boolean show) {
		fShowCursor=true;
	}
	public void setCursorEnabled(boolean visible) {
		fCursorTime=System.currentTimeMillis();
		fShowCursor=visible;
		fCursorIsEnabled=visible;
		fireCellRangeChanged(fCursorColumn, fCursorLine, 1, 1);
	}
	public boolean isCursorEnabled() {
		return fCursorIsEnabled;
	}

	public Point getSelectionEnd() {
		if(fSelectionStartLine<0)
			return null;
		else
			return new Point(fSelectionEndColumn, fSeletionEndLine);
	}

	public Point getSelectionStart() {
		if (fSelectionStartLine < 0)
			return null;
		else
			return new Point(fSelectionStartCoumn,fSelectionStartLine);
	}
	public Point getSelectionAnchor() {
		if(fSelectionStartLine<0)
			return null;
		return new Point(fSelectionAnchor.x,fSelectionAnchor.y);
	}
	public void setSelectionAnchor(Point anchor) {
		fSelectionAnchor.x=anchor.x;
		fSelectionAnchor.y=anchor.y;
	}

	public void setSelection(int startLine, int endLine, int startColumn, int endColumn) {
//		System.err.println(startLine+","+endLine+","+startColumn+","+endColumn);
		doSetSelection(startLine, endLine, startColumn, endColumn);
		fCurrentSelection=extractSelectedText();
	}
	private void doSetSelection(int startLine, int endLine, int startColumn, int endColumn) {
		assert(startLine<0 || startLine<=endLine);
		if(startLine>=0) {
			if(fSelectionSnapshot==null) {
				fSelectionSnapshot=fSnapshot.getTerminalTextData().makeSnapshot();
				fSelectionSnapshot.updateSnapshot(true);
			}
		} else if(fSelectionSnapshot!=null) {
			fSelectionSnapshot.detach();
			fSelectionSnapshot=null;
		}
		int oldStart=fSelectionStartLine;
		int oldEnd=fSeletionEndLine;
		fSelectionStartLine = startLine;
		fSeletionEndLine = endLine;
		fSelectionStartCoumn = startColumn;
		fSelectionEndColumn = endColumn;
		if(fSelectionSnapshot!=null) {
			fSelectionSnapshot.setInterestWindow(0, fSelectionSnapshot.getHeight());
		}
		int changedStart;
		int changedEnd;
		if(oldStart<0) {
			changedStart=fSelectionStartLine;
			changedEnd=fSeletionEndLine;
		} else if(fSelectionStartLine<0) {
			changedStart=oldStart;
			changedEnd=oldEnd;
		} else {
			changedStart=Math.min(oldStart, fSelectionStartLine);
			changedEnd=Math.max(oldEnd, fSeletionEndLine);
		}
		if(changedStart>=0) {
			fireCellRangeChanged(0, changedStart, fSnapshot.getWidth(), changedEnd-changedStart+1);
		}
	}

	public boolean hasLineSelection(int line) {
		if (fSelectionStartLine < 0)
			return false;
		else
			return line >= fSelectionStartLine && line <= fSeletionEndLine;
	}

	public String getSelectedText() {
		return fCurrentSelection;
	}
	/**
	 * Calculates the currently selected text
	 * @return the currently selected text
	 */
	private String extractSelectedText() {
		if(fSelectionStartLine<0 || fSelectionStartCoumn<0 || fSelectionEndColumn<0 || fSelectionSnapshot==null)
			return ""; //$NON-NLS-1$
		StringBuffer buffer=new StringBuffer();
		for (int line = fSelectionStartLine; line <= fSeletionEndLine; line++) {
			String text;
			char[] chars=fSelectionSnapshot.getChars(line);
			if(chars!=null) {
				text=new String(chars);
				if(line==fSeletionEndLine)
					text=text.substring(0, Math.min(fSelectionEndColumn+1,text.length()));
				if(line==fSelectionStartLine)
					text=text.substring(Math.min(fSelectionStartCoumn,text.length()));
				// get rid of the empty space at the end of the lines
				// text=text.replaceAll("\000+$","");  //$NON-NLS-1$//$NON-NLS-2$
				// <J2ME-CDC-1.1 version>
				int i = text.length() - 1;
				while (i >= 0 && text.charAt(i) == '\000') {
					i--;
				}
				text = text.substring(0, i + 1);
				// </J2ME-CDC-1.1 version>
				// null means space
				text=text.replace('\000', ' ');
			} else {
				text=""; //$NON-NLS-1$
			}
			buffer.append(text);
			if(line < fSeletionEndLine)
				buffer.append('\n');
		}
		return buffer.toString();
	}
	private void updateSelection() {
		if (fSelectionSnapshot != null && fSelectionSnapshot.isOutOfDate()) {
			fSelectionSnapshot.updateSnapshot(true);
			// has the selection moved?
			if (fSelectionSnapshot != null && fSelectionStartLine >= 0 && fSelectionSnapshot.getScrollWindowSize() > 0) {
				int start = fSelectionStartLine + fSelectionSnapshot.getScrollWindowShift();
				int end = fSeletionEndLine + fSelectionSnapshot.getScrollWindowShift();
				if (start < 0)
					if (end >= 0)
						start = 0;
					else
						start = -1;
				doSetSelection(start, end, fSelectionStartCoumn, fSelectionEndColumn);
			}
			// check if the content of the selection has changed. If the content has
			// changed, clear the selection
			if (fCurrentSelection.length()>0 && fSelectionSnapshot != null
					&& fSelectionSnapshot.getFirstChangedLine() <= fSeletionEndLine
					&& fSelectionSnapshot.getLastChangedLine() >= fSelectionStartLine) {
				// has the selected text changed?
				if (!fCurrentSelection.equals(extractSelectedText())) {
					setSelection(-1, -1, -1, -1);
				}
			}
			// update the observed window...
			if (fSelectionSnapshot != null)
				// todo make -1 to work!
				fSelectionSnapshot.setInterestWindow(0, fSelectionSnapshot.getHeight());
		}
	}

}