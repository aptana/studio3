/*******************************************************************************
 * Copyright (c) 2007 Wind River Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Michael Scharf (Wind River) - initial API and implementation
 *******************************************************************************/
package org.eclipse.tm.internal.terminal.emulator;

import java.util.Stack;

import org.eclipse.tm.internal.terminal.model.TerminalTextDataStore;
import org.eclipse.tm.terminal.model.ITerminalTextData;
import org.eclipse.tm.terminal.model.Style;

/**
 *
 */
public class VT100EmulatorBackend implements IVT100EmulatorBackend {

	/**
	 * This field holds the number of the column in which the cursor is
	 * logically positioned. The leftmost column on the screen is column 0, and
	 * column numbers increase to the right. The maximum value of this field is
	 * {@link #widthInColumns} - 1. We track the cursor column using this field
	 * to avoid having to recompute it repeatly using StyledText method calls.
	 * <p>
	 * 
	 * The StyledText widget that displays text has a vertical bar (called the
	 * "caret") that appears _between_ character cells, but ANSI terminals have
	 * the concept of a cursor that appears _in_ a character cell, so we need a
	 * convention for which character cell the cursor logically occupies when
	 * the caret is physically between two cells. The convention used in this
	 * class is that the cursor is logically in column N when the caret is
	 * physically positioned immediately to the _left_ of column N.
	 * <p>
	 * 
	 * When fCursorColumn is N, the next character output to the terminal appears
	 * in column N. When a character is output to the rightmost column on a
	 * given line (column widthInColumns - 1), the cursor moves to column 0 on
	 * the next line after the character is drawn (this is how line wrapping is
	 * implemented). If the cursor is in the bottommost line when line wrapping
	 * occurs, the topmost visible line is scrolled off the top edge of the
	 * screen.
	 * <p>
	 */
	private int fCursorColumn;
	private int fCursorLine;
	private Style fDefaultStyle;
	private Style fStyle;
	int fLines;
	int fColumns;
	private int fScrollingRegionTopLine;
	private int fScrollingRegionBottomLine;
	private Stack fBufferStack = new Stack();
	private boolean wrapNewLine = false;
	
	final private ITerminalTextData fTerminal;
	public VT100EmulatorBackend(ITerminalTextData terminal) {
		fTerminal=terminal;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.emulator.IVT100EmulatorBackend#clearAll()
	 */
	public void clearAll() {
		synchronized (fTerminal) {
			// clear the history
			int n=fTerminal.getHeight();
			for (int line = 0; line < n; line++) {
				fTerminal.cleanLine(line);
			}
			fTerminal.setDimensions(fLines, fTerminal.getWidth());
			setStyle(getDefaultStyle());
			setCursor(0, 0);
		}
	}
	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.emulator.IVT100EmulatorBackend#setDimensions(int, int)
	 */
	public void setDimensions(int lines, int cols) {
		synchronized (fTerminal) {
			if(lines==fLines && cols==fColumns)
				return; // nothing to do
			// cursor line from the bottom
			int cl=lines-(fLines-getCursorLine());
			int cc=getCursorColumn();
			int newLines=Math.max(lines,fTerminal.getHeight());
			// if the terminal has no history, then resize by
			// setting the size to the new size
			if(fTerminal.getHeight()==fLines) {
				if(lines<fLines) {
					cl+=fLines-lines;
					newLines=lines;
					// shrink by cutting empty lines at the bottom
//					int firstNoneEmptyLine;
//					for (firstNoneEmptyLine = fTerminal.getHeight(); firstNoneEmptyLine <= 0; firstNoneEmptyLine--) {
//						LineSegment[] segments = fTerminal.getLineSegments(firstNoneEmptyLine, 0, fTerminal.getWidth());
//						if(segments.length>1)
//							break;
//						// is the line empty?
//						if(segments[0].getText().replaceAll("[\000 ]+", "").length()==0)
//							break;
//					}
				} else {
					cl+=fLines-lines;
				}
			}
			fLines=lines;
			fColumns=cols;
			setScrollingRegion(1, fLines);
			// make the terminal at least as high as we need lines
			fTerminal.setDimensions(newLines, fColumns);
			setCursor(cl, cc);
		}
	}
	
	int toAbsoluteLine(int line) {
		synchronized (fTerminal) {
			return fTerminal.getHeight()-fLines+line;
		}
	}
	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.emulator.IVT100EmulatorBackend#insertCharacters(int)
	 */
	public void insertCharacters(int charactersToInsert) {
		synchronized (fTerminal) {
			int line=toAbsoluteLine(fCursorLine);
			int n=charactersToInsert;
			for (int col = fColumns-1; col >=fCursorColumn+n; col--) {
				char c=fTerminal.getChar(line, col-n);
				Style style=fTerminal.getStyle(line, col-n);
				fTerminal.setChar(line, col,c, style);
			}
			int last=Math.min(fCursorColumn+n, fColumns);
			for (int col = fCursorColumn; col <last; col++) {
				fTerminal.setChar(line, col,'\000', null);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.emulator.IVT100EmulatorBackend#eraseToEndOfScreen()
	 */
	public void eraseToEndOfScreen() {
		synchronized (fTerminal) {
			eraseLineToEnd();
			for (int line = toAbsoluteLine(fCursorLine+1); line < toAbsoluteLine(fLines); line++) {
				fTerminal.cleanLine(line);
			}
		}
		
	}
	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.emulator.IVT100EmulatorBackend#eraseToCursor()
	 */
	public void eraseToCursor() {
		synchronized (fTerminal) {
			for (int line = toAbsoluteLine(0); line < toAbsoluteLine(fCursorLine); line++) {
				fTerminal.cleanLine(line);
			}
			eraseLineToCursor();
		}
	}
	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.emulator.IVT100EmulatorBackend#eraseAll()
	 */
	public void eraseAll() {
		synchronized (fTerminal) {
			for (int line = toAbsoluteLine(0); line < toAbsoluteLine(fLines); line++) {
				fTerminal.cleanLine(line);
			}
		}
	}
	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.emulator.IVT100EmulatorBackend#eraseLine()
	 */
	public void eraseLine() {
		synchronized (fTerminal) {
			fTerminal.cleanLine(toAbsoluteLine(fCursorLine));
		}
	}
	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.emulator.IVT100EmulatorBackend#eraseLineToEnd()
	 */
	public void eraseLineToEnd() {
		synchronized (fTerminal) {
			int line=toAbsoluteLine(fCursorLine);
			for (int col = fCursorColumn; col < fColumns; col++) {
				fTerminal.setChar(line, col, '\000', null);
			}
		}
	}	
	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.emulator.IVT100EmulatorBackend#eraseLineToCursor()
	 */
	public void eraseLineToCursor() {
		synchronized (fTerminal) {
			int line=toAbsoluteLine(fCursorLine);
			for (int col = 0; col <= fCursorColumn; col++) {
				fTerminal.setChar(line, col, '\000', null);
			}
		}
	}	

	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.emulator.IVT100EmulatorBackend#insertLines(int)
	 */
	public void insertLines(int n) {
		synchronized (fTerminal) {
			if(!isCusorInScrollingRegion())
				return;
			assert n>0;
			int line=toAbsoluteLine(fCursorLine);
			int nLines=toAbsoluteLine(fScrollingRegionBottomLine)+1-line;
			fTerminal.scroll(line, nLines, n);
		}
	}
	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.emulator.IVT100EmulatorBackend#deleteCharacters(int)
	 */
	public void deleteCharacters(int n) {
		synchronized (fTerminal) {
			int line=toAbsoluteLine(fCursorLine);
			for (int col = fCursorColumn+n; col < fColumns; col++) {
				char c=fTerminal.getChar(line, col);
				Style style=fTerminal.getStyle(line, col);
				fTerminal.setChar(line, col-n,c, style);
			}
			int first=Math.max(fCursorColumn, fColumns-n);
			for (int col = first; col <fColumns; col++) {
				fTerminal.setChar(line, col,'\000', null);
			}
		}
	}
	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.emulator.IVT100EmulatorBackend#deleteLines(int)
	 */
	public void deleteLines(int n) {
		synchronized (fTerminal) {
			if(!isCusorInScrollingRegion())
				return;
			assert n>0;
			int line=toAbsoluteLine(fCursorLine);
			int nLines=toAbsoluteLine(fScrollingRegionBottomLine)+1-line;
			fTerminal.scroll(line, nLines, -n);
		}
	}
	
	private boolean isCusorInScrollingRegion() {
		return (fScrollingRegionTopLine <= fCursorLine && fCursorLine <= fScrollingRegionBottomLine);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.emulator.IVT100EmulatorBackend#getDefaultStyle()
	 */
	public Style getDefaultStyle() {
		synchronized (fTerminal) {
			return fDefaultStyle;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.emulator.IVT100EmulatorBackend#setDefaultStyle(org.eclipse.tm.terminal.model.Style)
	 */
	public void setDefaultStyle(Style defaultStyle) {
		synchronized (fTerminal) {
			fDefaultStyle = defaultStyle;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.emulator.IVT100EmulatorBackend#getStyle()
	 */
	public Style getStyle() {
		synchronized (fTerminal) {
			if(fStyle==null)
				return fDefaultStyle;
			return fStyle;
		}
	}
	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.emulator.IVT100EmulatorBackend#setStyle(org.eclipse.tm.terminal.model.Style)
	 */
	public void setStyle(Style style) {
		synchronized (fTerminal) {
			fStyle=style;
		}
	}
	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.emulator.IVT100EmulatorBackend#appendString(java.lang.String)
	 */
	public void appendString(String buffer) {
		synchronized (fTerminal) {
			char[] chars=buffer.toCharArray();
			int i=0;
			while (i < chars.length) {
				if (wrapNewLine) {
					doNewline();
				}
				int line=toAbsoluteLine(fCursorLine);
				int n=Math.min(fColumns-fCursorColumn,chars.length-i);
				fTerminal.setChars(line, fCursorColumn, chars, i, n, fStyle);
				int col=fCursorColumn+n;
				i+=n;
				// wrap needed?
				if(col>=fColumns) {
					setCursorColumn(0);
					wrapNewLine = true;
				} else {
					setCursorColumn(col);
				}
			}
		}
	}

	/**
	 * MUST be called from a synchronized block!
	 */
	private void doNewline() {
		wrapNewLine = false;
		if(fCursorLine+1>=fLines) {
			int h=fTerminal.getHeight();
			fTerminal.addLine();
			if(h!=fTerminal.getHeight())
				setCursorLine(fCursorLine+1);
		} else if (fScrollingRegionTopLine != 0 || fScrollingRegionBottomLine != fLines-1) {
			fTerminal.scroll(toAbsoluteLine(fScrollingRegionTopLine), fScrollingRegionBottomLine-fScrollingRegionTopLine+1, -1);
		} else {
			setCursorLine(fCursorLine+1);
		}
	}
	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.emulator.IVT100EmulatorBackend#processNewline()
	 */
	public void processNewline() {
		synchronized (fTerminal) {
			doNewline();
		}
	}
	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.emulator.IVT100EmulatorBackend#getCursorLine()
	 */
	public int getCursorLine() {
		synchronized (fTerminal) {
			return fCursorLine;
		}
	}
	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.emulator.IVT100EmulatorBackend#getCursorColumn()
	 */
	public int getCursorColumn() {
		synchronized (fTerminal) {
			return fCursorColumn;
		}
	}
	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.emulator.IVT100EmulatorBackend#setCursor(int, int)
	 */
	public void setCursor(int targetLine, int targetColumn) {
		synchronized (fTerminal) {
			setCursorLine(targetLine);
			setCursorColumn(targetColumn);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.emulator.IVT100EmulatorBackend#setCursorColumn(int)
	 */
	public void setCursorColumn(int targetColumn) {
		synchronized (fTerminal) {
			wrapNewLine = false;
			if(targetColumn<0)
				targetColumn=0;
			else if(targetColumn>=fColumns)
				targetColumn=fColumns-1;
			fCursorColumn=targetColumn;
			// We make the assumption that nobody is changing the
			// terminal cursor except this class!
			// This assumption gives a huge performance improvement
			fTerminal.setCursorColumn(targetColumn);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.emulator.IVT100EmulatorBackend#setCursorLine(int)
	 */
	public void setCursorLine(int targetLine) {
		synchronized (fTerminal) {
			wrapNewLine = false;
			if(targetLine<0)
				targetLine=0;
			else if(targetLine>=fLines)
				targetLine=fLines-1;
			fCursorLine=targetLine;
			// We make the assumption that nobody is changing the
			// terminal cursor except this class!
			// This assumption gives a huge performance improvement
			fTerminal.setCursorLine(toAbsoluteLine(targetLine));
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.emulator.IVT100EmulatorBackend#getLines()
	 */
	public int getLines() {
		synchronized (fTerminal) {
			return fLines;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.emulator.IVT100EmulatorBackend#getColumns()
	 */
	public int getColumns() {
		synchronized (fTerminal) {
			return fColumns;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.emulator.IVT100EmulatorBackend#setScrollingRegion(int, int)
	 */
	public void setScrollingRegion(int topLine, int bottomLine) {
		fScrollingRegionTopLine=topLine-1;
		fScrollingRegionBottomLine=bottomLine-1;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.emulator.IVT100EmulatorBackend#getScrollingRegionBottomLine()
	 */
	public int getScrollingRegionBottomLine() {
		return fScrollingRegionBottomLine;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.emulator.IVT100EmulatorBackend#getScrollingRegionTopLine()
	 */
	public int getScrollingRegionTopLine() {
		return fScrollingRegionTopLine;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.emulator.IVT100EmulatorBackend#setAlternativeScreenBuffer(boolean)
	 */
	public void setAlternativeScreenBuffer(boolean enable) {
		synchronized (fTerminal) {
			wrapNewLine = false;
			if (enable) {
				ITerminalTextData data = new TerminalTextDataStore();
				data.copy(fTerminal);
				fBufferStack.push(data);
			} else if (!fBufferStack.isEmpty()) {
				ITerminalTextData data = (ITerminalTextData) fBufferStack.pop();
				if (fTerminal.getWidth() != data.getWidth()) {
					data.setDimensions(data.getHeight(), fTerminal.getWidth());
				}
				fTerminal.copy(data);
			}
		}
	}
}
