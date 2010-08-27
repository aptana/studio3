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

import java.io.PrintStream;

import org.eclipse.tm.terminal.model.Style;

public class VT100BackendTraceDecorator implements IVT100EmulatorBackend {
	final IVT100EmulatorBackend fBackend;
	final PrintStream fWriter;
	public VT100BackendTraceDecorator(IVT100EmulatorBackend backend, PrintStream out) {
		fBackend = backend;
		fWriter=out;
	}

	public void appendString(String buffer) {
		fWriter.println("appendString(\""+buffer+"\")"); //$NON-NLS-1$ //$NON-NLS-2$
		fBackend.appendString(buffer);
	}

	public void clearAll() {
		fWriter.println("clearAll()"); //$NON-NLS-1$
		fBackend.clearAll();
	}

	public void deleteCharacters(int n) {
		fWriter.println("deleteCharacters("+n+")"); //$NON-NLS-1$ //$NON-NLS-2$
		fBackend.deleteCharacters(n);
	}

	public void deleteLines(int n) {
		fWriter.println("deleteLines("+n+")"); //$NON-NLS-1$ //$NON-NLS-2$
		fBackend.deleteLines(n);
	}

	public void eraseAll() {
		fWriter.println("eraseAll()"); //$NON-NLS-1$
		fBackend.eraseAll();
	}

	public void eraseLine() {
		fWriter.println("eraseLine()"); //$NON-NLS-1$
		fBackend.eraseLine();
	}

	public void eraseLineToCursor() {
		fWriter.println("eraseLineToCursor()"); //$NON-NLS-1$
		fBackend.eraseLineToCursor();
	}

	public void eraseLineToEnd() {
		fWriter.println("eraseLineToEnd()"); //$NON-NLS-1$
		fBackend.eraseLineToEnd();
	}

	public void eraseToCursor() {
		fWriter.println("eraseToCursor()"); //$NON-NLS-1$
		fBackend.eraseToCursor();
	}

	public void eraseToEndOfScreen() {
		fWriter.println("eraseToEndOfScreen()"); //$NON-NLS-1$
		fBackend.eraseToEndOfScreen();
	}

	public int getColumns() {
		return fBackend.getColumns();
	}

	public int getCursorColumn() {
		return fBackend.getCursorColumn();
	}

	public int getCursorLine() {
		return fBackend.getCursorLine();
	}

	public Style getDefaultStyle() {
		return fBackend.getDefaultStyle();
	}

	public int getLines() {
		return fBackend.getLines();
	}

	public Style getStyle() {
		return fBackend.getStyle();
	}

	public void insertCharacters(int charactersToInsert) {
		fWriter.println("insertCharacters("+charactersToInsert+")"); //$NON-NLS-1$ //$NON-NLS-2$
		fBackend.insertCharacters(charactersToInsert);
	}

	public void insertLines(int n) {
		fWriter.println("insertLines("+n+")"); //$NON-NLS-1$ //$NON-NLS-2$
		fBackend.insertLines(n);
	}

	public void processNewline() {
		fWriter.println("processNewline()"); //$NON-NLS-1$
		fBackend.processNewline();
	}

	public void setCursor(int targetLine, int targetColumn) {
		fWriter.println("setCursor("+targetLine+", "+targetColumn+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		fBackend.setCursor(targetLine, targetColumn);
	}

	public void setCursorColumn(int targetColumn) {
		fWriter.println("setCursorColumn("+targetColumn+")"); //$NON-NLS-1$ //$NON-NLS-2$
		fBackend.setCursorColumn(targetColumn);
	}

	public void setCursorLine(int targetLine) {
		fWriter.println("setCursorLine("+targetLine+")"); //$NON-NLS-1$ //$NON-NLS-2$
		fBackend.setCursorLine(targetLine);
	}

	public void setDefaultStyle(Style defaultStyle) {
		fWriter.println("setDefaultStyle("+defaultStyle+")"); //$NON-NLS-1$ //$NON-NLS-2$
		fBackend.setDefaultStyle(defaultStyle);
	}

	public void setDimensions(int lines, int cols) {
		fWriter.println("setDimensions("+lines+","+cols+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		fBackend.setDimensions(lines, cols);
	}

	public void setStyle(Style style) {
		fWriter.println("setStyle("+style+")"); //$NON-NLS-1$ //$NON-NLS-2$
		fBackend.setStyle(style);
	}

	public void setScrollingRegion(int topLine, int bottomLine) {
		fWriter.println("setScrollingRegion("+topLine+", "+bottomLine+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		fBackend.setScrollingRegion(topLine, bottomLine);
	}

	public void setAlternativeScreenBuffer(boolean enable) {
		fWriter.println("setAlternativeScreenBuffer("+enable+")"); //$NON-NLS-1$ //$NON-NLS-2$
		fBackend.setAlternativeScreenBuffer(enable);
	}

	public int getScrollingRegionBottomLine() {
		fWriter.println("getScrollingRegionBottomLine()"); //$NON-NLS-1$
		return fBackend.getScrollingRegionBottomLine();
	}

	public int getScrollingRegionTopLine() {
		fWriter.println("getScrollingRegionTopLine()"); //$NON-NLS-1$
		return fBackend.getScrollingRegionTopLine();
	}

}
