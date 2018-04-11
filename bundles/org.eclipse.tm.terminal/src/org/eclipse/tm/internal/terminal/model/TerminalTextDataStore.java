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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.tm.terminal.model.ITerminalTextData;
import org.eclipse.tm.terminal.model.ITerminalTextDataSnapshot;
import org.eclipse.tm.terminal.model.LineSegment;
import org.eclipse.tm.terminal.model.Style;

/**
 * This class is thread safe.
 *
 */
public class TerminalTextDataStore implements ITerminalTextData {
	private char[][] fChars;
	private Style[][] fStyle;
	private int fWidth;
	private int fHeight;
	private int fMaxHeight;
	private int fCursorColumn;
	private int fCursorLine;
	public TerminalTextDataStore() {
		fChars=new char[0][];
		fStyle=new Style[0][];
		fWidth=0;
	}
	/**
	 * This is used in asserts to throw an {@link RuntimeException}.
	 * This is useful for tests.
	 * @return never -- throws an exception
	 */
	private boolean throwRuntimeException() {
		throw new RuntimeException();
	}
	

	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.text.ITerminalTextData#getWidth()
	 */
	public int getWidth() {
		return fWidth;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.text.ITerminalTextData#getHeight()
	 */
	public int getHeight() {
		return fHeight;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.text.ITerminalTextData#setDimensions(int, int)
	 */
	public void setDimensions(int height, int width) {
		assert height>=0 || throwRuntimeException();
		assert width>=0  || throwRuntimeException();
		// just extend the region
		if(height>fChars.length) {
			int h=4*height/3;
			if(fMaxHeight>0 && h>fMaxHeight)
				h=fMaxHeight;
			fStyle=(Style[][]) resizeArray(fStyle, height);
			fChars=(char[][]) resizeArray(fChars, height);
		}
		// clean the new lines
		if(height>fHeight) {
			for (int i = fHeight; i < height; i++) {
				fStyle[i]=null;
				fChars[i]=null;
			}
		}
		// set dimensions after successful resize!
		fWidth=width;
		fHeight=height;
	}
	/**
	 * Reallocates an array with a new size, and copies the contents of the old
	 * array to the new array.
	 * 
	 * @param origArray the old array, to be reallocated.
	 * @param newSize the new array size.
	 * @return A new array with the same contents (chopped off if needed or filled with 0 or null).
	 */
	private Object resizeArray(Object origArray, int newSize) {
		int oldSize = Array.getLength(origArray);
		if(oldSize==newSize)
			return origArray;
		Class elementType = origArray.getClass().getComponentType();
		Object newArray = Array.newInstance(elementType, newSize);
		int preserveLength = Math.min(oldSize, newSize);
		if (preserveLength > 0)
			System.arraycopy(origArray, 0, newArray, 0, preserveLength);
		return newArray;
	}


	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.text.ITerminalTextData#getLineSegments(int, int, int)
	 */
	public LineSegment[] getLineSegments(int line, int column, int len) {
		// get the styles and chars for this line
		Style[] styles=fStyle[line];
		char[] chars=fChars[line];
		int col=column;
		int n=column+len;
		
		// expand the line if needed....
		if(styles==null)
			styles=new Style[n];
		else if(styles.length<n)
			styles=(Style[]) resizeArray(styles, n);

		if(chars==null)
			chars=new char[n];
		else if(chars.length<n)
			chars=(char[]) resizeArray(chars, n);
	
		// and create the line segments
		Style style=styles[column];
		List segments=new ArrayList();
		for (int i = column; i < n; i++) {
			if(styles[i]!=style) {
				segments.add(new LineSegment(col,new String(chars,col,i-col),style));
				style=styles[i];
				col=i;
			}
		}
		if(col < n) {
			segments.add(new LineSegment(col,new String(chars,col,n-col),style));
		}
		return (LineSegment[]) segments.toArray(new LineSegment[segments.size()]);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.text.ITerminalTextData#getChar(int, int)
	 */
	public char getChar(int line, int column) {
		assert column<fWidth || throwRuntimeException();
		if(fChars[line]==null||column>=fChars[line].length)
			return 0;
		return fChars[line][column];
	}
	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.text.ITerminalTextData#getStyle(int, int)
	 */
	public Style getStyle(int line, int column) {
		assert column<fWidth || throwRuntimeException();
		if(fStyle[line]==null || column>=fStyle[line].length)
			return null;
		return fStyle[line][column];
	}
	
	void ensureLineLength(int iLine, int length) {
		if(length>fWidth)
			throw new RuntimeException();
		if(fChars[iLine]==null) {
			fChars[iLine]=new char[length];
		} else if(fChars[iLine].length<length) {
			fChars[iLine]=(char[]) resizeArray(fChars[iLine],length);
		}
		if(fStyle[iLine]==null) {
			fStyle[iLine]=new Style[length];
		} else if(fStyle[iLine].length<length) {
			fStyle[iLine]=(Style[]) resizeArray(fStyle[iLine],length);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.text.ITerminalTextData#setChar(int, int, char, org.eclipse.tm.internal.terminal.text.Style)
	 */
	public void setChar(int line, int column, char c, Style style) {
		ensureLineLength(line,column+1);
		fChars[line][column]=c;
		fStyle[line][column]=style;		
	}
	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.text.ITerminalTextData#setChars(int, int, char[], org.eclipse.tm.internal.terminal.text.Style)
	 */
	public void setChars(int line, int column, char[] chars, Style style) {
		setChars(line,column,chars,0,chars.length,style);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.text.ITerminalTextData#setChars(int, int, char[], int, int, org.eclipse.tm.internal.terminal.text.Style)
	 */
	public void setChars(int line, int column, char[] chars, int start, int len, Style style) {
		ensureLineLength(line,column+len);
		for (int i = 0; i < len; i++) {
			fChars[line][column+i]=chars[i+start];
			fStyle[line][column+i]=style;		
		}
	}
	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.text.ITerminalTextData#scroll(int, int, int)
	 */
	public void scroll(int startLine, int size, int shift) {
		assert startLine+size <= getHeight() || throwRuntimeException();
		if(shift<0) {
			// move the region up
			// shift is negative!!
			for (int i = startLine; i < startLine+size+shift; i++) {
				fChars[i]=fChars[i-shift];
				fStyle[i]=fStyle[i-shift];
			}
			// then clean the opened lines
			cleanLines(Math.max(startLine, startLine+size+shift),Math.min(-shift, getHeight()-startLine));
//			cleanLines(Math.max(0, startLine+size+shift),Math.min(-shift, getHeight()-startLine));
		} else {
			for (int i = startLine+size-1; i >=startLine && i-shift>=0; i--) {
				fChars[i]=fChars[i-shift];
				fStyle[i]=fStyle[i-shift];
			}
			cleanLines(startLine, Math.min(shift, getHeight()-startLine));
		}
	}
	/**
	 * Replaces the lines with new empty data
	 * @param line
	 * @param len
	 */
	private void cleanLines(int line, int len) {
		for (int i = line; i < line+len; i++) {
			fChars[i]=null;
			fStyle[i]=null;
		}
	}
	
	/*
	 * @return a text representation of the object.
	 * Lines are separated by '\n'. No style information is returned.
	 */
	public String toString() {
		StringBuffer buff=new StringBuffer();
		for (int line = 0; line < getHeight(); line++) {
			if(line>0)
				buff.append("\n"); //$NON-NLS-1$
			for (int column = 0; column < fWidth; column++) {
				buff.append(getChar(line, column));
			}
		}
		return buff.toString();
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
		fWidth=source.getWidth();
		int n=source.getHeight();
		if(getHeight()!=n) {
			fChars=new char[n][];
			fStyle=new Style[n][];
		}
		for (int i = 0; i < n; i++) {
			fChars[i]=source.getChars(i);
			fStyle[i]=source.getStyles(i);
		}
		fHeight=n;
		fCursorLine=source.getCursorLine();
		fCursorColumn=source.getCursorColumn();
	}
	public void copyRange(ITerminalTextData source, int sourceStartLine, int destStartLine,int length) {
		for (int i = 0; i < length; i++) {
			fChars[i+destStartLine]=source.getChars(i+sourceStartLine);
			fStyle[i+destStartLine]=source.getStyles(i+sourceStartLine);
		}
	}

	public void copyLine(ITerminalTextData source, int sourceLine, int destLine) {
		fChars[destLine]=source.getChars(sourceLine);
		fStyle[destLine]=source.getStyles(sourceLine);
	}

	public char[] getChars(int line) {
		if(fChars[line]==null)
			return null;
		return (char[]) fChars[line].clone();
	}

	public Style[] getStyles(int line) {
		if(fStyle[line]==null)
			return null;
		return (Style[]) fStyle[line].clone();
	}

	public void setLine(int line, char[] chars, Style[] styles) {
		fChars[line]=(char[]) chars.clone();
		fStyle[line]=(Style[]) styles.clone();
	}

	public void setMaxHeight(int height) {
		fMaxHeight=height;
	}

	public int getMaxHeight() {
		return fMaxHeight;
	}

	public void cleanLine(int line) {
		fChars[line]=null;
		fStyle[line]=null;
	}
	public int getCursorColumn() {
		return fCursorColumn;
	}
	public int getCursorLine() {
		return fCursorLine;
	}
	public void setCursorColumn(int column) {
		fCursorColumn=column;
	}
	public void setCursorLine(int line) {
		fCursorLine=line;
	}
}
