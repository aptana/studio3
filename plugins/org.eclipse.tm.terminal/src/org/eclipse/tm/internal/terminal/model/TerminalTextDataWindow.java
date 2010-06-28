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
package org.eclipse.tm.internal.terminal.model;

import org.eclipse.tm.terminal.model.ITerminalTextData;
import org.eclipse.tm.terminal.model.ITerminalTextDataSnapshot;
import org.eclipse.tm.terminal.model.LineSegment;
import org.eclipse.tm.terminal.model.Style;

/**
 * This class stores the data only within a window {@link #setWindow(int, int)} and 
 * {@link #getWindowStartLine()} and {@link #getWindowSize()}. Everything outside 
 * the is <code>char=='\000'</code> and <code>style=null</code>.
 *
 */
public class TerminalTextDataWindow implements ITerminalTextData {
	final ITerminalTextData fData;
	int fWindowStartLine;
	int fWindowSize;
	int fHeight;
	int fMaxHeight;
	public TerminalTextDataWindow(ITerminalTextData data) {
		fData=data;
	}
	public TerminalTextDataWindow() {
		this(new TerminalTextDataStore());
	}
	/**
	 * This is used in asserts to throw an {@link RuntimeException}.
	 * This is useful for tests.
	 * @return never -- throws an exception
	 */
	private boolean throwRuntimeException() {
		throw new RuntimeException();
	}
	/**
	 * @param line
	 * @return true if the line is within the window
	 */
	boolean isInWindow(int line) {
		return line>=fWindowStartLine && line<fWindowStartLine+fWindowSize;
	}
	public char getChar(int line, int column) {
		if(!isInWindow(line))
			return 0;
		return fData.getChar(line-fWindowStartLine, column);
	}

	public char[] getChars(int line) {
		if(!isInWindow(line))
			return null;
		return fData.getChars(line-fWindowStartLine);
	}

	public int getHeight() {
		return fHeight;
	}

	public LineSegment[] getLineSegments(int line, int startCol, int numberOfCols) {
		if(!isInWindow(line))
			return new LineSegment[]{new LineSegment(startCol,new String(new char[numberOfCols]),null)};
		return fData.getLineSegments(line-fWindowStartLine, startCol, numberOfCols);
	}

	public int getMaxHeight() {
		return fMaxHeight;
	}

	public Style getStyle(int line, int column) {
		if(!isInWindow(line))
			return null;
		return fData.getStyle(line-fWindowStartLine, column);
	}

	public Style[] getStyles(int line) {
		if(!isInWindow(line))
			return null;
		return fData.getStyles(line-fWindowStartLine);
	}

	public int getWidth() {
		return fData.getWidth();
	}

	public ITerminalTextDataSnapshot makeSnapshot() {
		throw new UnsupportedOperationException();
	}
	public void addLine() {
		if(fMaxHeight>0 && getHeight()<fMaxHeight) {
			setDimensions(getHeight()+1, getWidth());
		} else {
			scroll(0,getHeight(),-1);
		}
	}
	public void copy(ITerminalTextData source) {
		// we inherit the dimensions of the source
		setDimensions(source.getHeight(), source.getWidth());
		int n=Math.min(fWindowSize, source.getHeight()-fWindowStartLine);
		if(n>0)
			fData.copyRange(source, fWindowStartLine, 0, n);
	}
	public void copyRange(ITerminalTextData source, int sourceStartLine, int destStartLine, int length) {
		int n=length;
		int dStart=destStartLine-fWindowStartLine;
		int sStart=sourceStartLine;
		// if start outside our range, cut the length to copy
		if(dStart<0) {
			n+=dStart;
			sStart-=dStart;
			dStart=0;
		}
		// do not exceed the window size
		n=Math.min(n,fWindowSize);
		if(n>0)
			fData.copyRange(source, sStart, dStart, n);
		
	}
	public void copyLine(ITerminalTextData source, int sourceLine, int destLine) {
		if(isInWindow(destLine))
			fData.copyLine(source, sourceLine, destLine-fWindowStartLine);
	}
	public void scroll(int startLine, int size, int shift) {
		assert (startLine>=0 && startLine+size<=fHeight) || throwRuntimeException();
		int n=size;
		int start=startLine-fWindowStartLine;
		// if start outside our range, cut the length to copy
		if(start<0) {
			n+=start;
			start=0;
		}
		n=Math.min(n,fWindowSize-start);
		// do not exceed the window size
		if(n>0)
			fData.scroll(start, n, shift);
	}
	public void setChar(int line, int column, char c, Style style) {
		if(!isInWindow(line))
			return;
		fData.setChar(line-fWindowStartLine, column, c, style);
	}
	public void setChars(int line, int column, char[] chars, int start, int len, Style style) {
		if(!isInWindow(line))
			return;
		fData.setChars(line-fWindowStartLine, column, chars, start, len, style);
	}
	public void setChars(int line, int column, char[] chars, Style style) {
		if(!isInWindow(line))
			return;
		fData.setChars(line-fWindowStartLine, column, chars, style);
	}
	public void setDimensions(int height, int width) {
		assert height>=0 || throwRuntimeException();
		fData.setDimensions(fWindowSize, width);
		fHeight=height;
	}
	public void setMaxHeight(int height) {
		fMaxHeight=height;
	}
	public void setWindow(int startLine, int size) {
//		assert startLine+size<=getHeight()||throwRuntimeException();
		fWindowStartLine=startLine;
		fWindowSize=size;
		fData.setDimensions(fWindowSize, getWidth());
	}
	public int getWindowStartLine() {
		return fWindowStartLine;
	}
	public int getWindowSize() {
		return fWindowSize;
	}
	public void setHeight(int height) {
		fHeight = height;
	}
	public void cleanLine(int line) {
		if(isInWindow(line))
			fData.cleanLine(line-fWindowStartLine);
	}
	public int getCursorColumn() {
		return fData.getCursorColumn();
	}
	public int getCursorLine() {
		return fData.getCursorLine();
	}
	public void setCursorColumn(int column) {
		fData.setCursorColumn(column);
	}
	public void setCursorLine(int line) {
		fData.setCursorLine(line);
	}
}
