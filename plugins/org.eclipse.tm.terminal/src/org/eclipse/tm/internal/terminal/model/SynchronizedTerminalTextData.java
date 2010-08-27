/*******************************************************************************
 * Copyright (c) 2007 Wind River Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse synchronized public License v1.0 
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
 * This is a decorator to make all access to
 * ITerminalTextData synchronized
 *
 */
public class SynchronizedTerminalTextData implements ITerminalTextData {
	final ITerminalTextData fData;
	public SynchronizedTerminalTextData(ITerminalTextData data) {
		fData=data;
	}
	synchronized public void addLine() {
		fData.addLine();
	}
	synchronized public void cleanLine(int line) {
		fData.cleanLine(line);
	}
	synchronized public void copy(ITerminalTextData source) {
		fData.copy(source);
	}
	synchronized public void copyLine(ITerminalTextData source, int sourceLine, int destLine) {
		fData.copyLine(source, sourceLine, destLine);
	}
	synchronized public void copyRange(ITerminalTextData source, int sourceStartLine, int destStartLine, int length) {
		fData.copyRange(source, sourceStartLine, destStartLine, length);
	}
	synchronized public char getChar(int line, int column) {
		return fData.getChar(line, column);
	}
	synchronized public char[] getChars(int line) {
		return fData.getChars(line);
	}
	synchronized public int getCursorColumn() {
		return fData.getCursorColumn();
	}
	synchronized public int getCursorLine() {
		return fData.getCursorLine();
	}
	synchronized public int getHeight() {
		return fData.getHeight();
	}
	synchronized public LineSegment[] getLineSegments(int line, int startCol, int numberOfCols) {
		return fData.getLineSegments(line, startCol, numberOfCols);
	}
	synchronized public int getMaxHeight() {
		return fData.getMaxHeight();
	}
	synchronized public Style getStyle(int line, int column) {
		return fData.getStyle(line, column);
	}
	synchronized public Style[] getStyles(int line) {
		return fData.getStyles(line);
	}
	synchronized public int getWidth() {
		return fData.getWidth();
	}
	synchronized public ITerminalTextDataSnapshot makeSnapshot() {
		return fData.makeSnapshot();
	}
	synchronized public void scroll(int startLine, int size, int shift) {
		fData.scroll(startLine, size, shift);
	}
	synchronized public void setChar(int line, int column, char c, Style style) {
		fData.setChar(line, column, c, style);
	}
	synchronized public void setChars(int line, int column, char[] chars, int start, int len, Style style) {
		fData.setChars(line, column, chars, start, len, style);
	}
	synchronized public void setChars(int line, int column, char[] chars, Style style) {
		fData.setChars(line, column, chars, style);
	}
	synchronized public void setCursorColumn(int column) {
		fData.setCursorColumn(column);
	}
	synchronized public void setCursorLine(int line) {
		fData.setCursorLine(line);
	}
	synchronized public void setDimensions(int height, int width) {
		fData.setDimensions(height, width);
	}
	synchronized public void setMaxHeight(int height) {
		fData.setMaxHeight(height);
	}
}
