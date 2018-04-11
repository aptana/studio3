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

import org.eclipse.tm.terminal.model.Style;

public interface IVT100EmulatorBackend {

	/**
	 * This method erases all text from the Terminal view. Including the history
	 */
	void clearAll();

	/**
	 * Sets the Dimensions of the addressable scroll space of the screen....
	 * Keeps the cursor position relative to the bottom of the screen!
	 * @param lines
	 * @param cols
	 */
	void setDimensions(int lines, int cols);

	/**
	 * This method makes room for N characters on the current line at the cursor
	 * position. Text under the cursor moves right without wrapping at the end
	 * of the line.
	 * 01234
	 * 0 123
	 */
	void insertCharacters(int charactersToInsert);

	/**
	 * 	Erases from cursor to end of screen, including cursor position. Cursor does not move.
	 */
	void eraseToEndOfScreen();

	/**
	 * Erases from beginning of screen to cursor, including cursor position. Cursor does not move.
	 */
	void eraseToCursor();

	/**
	 * Erases complete display. All lines are erased and changed to single-width. Cursor does not move.
	 */
	void eraseAll();

	/**
	 * Erases complete line.
	 */
	void eraseLine();

	/**
	 * Erases from cursor to end of line, including cursor position.
	 */
	void eraseLineToEnd();

	/**
	 * Erases from beginning of line to cursor, including cursor position.
	 */
	void eraseLineToCursor();

	/**
	 * Inserts n lines at line with cursor. Lines displayed below cursor move down. 
	 * Lines moved past the bottom margin are lost. This sequence is ignored when 
	 * cursor is outside scrolling region.
	 * @param n the number of lines to insert
	 */
	void insertLines(int n);

	/**
	 * Deletes n characters, starting with the character at cursor position. 
	 * When a character is deleted, all characters to the right of cursor move 
	 * left. This creates a space character at right margin. This character 
	 * has same character attribute as the last character moved left.
	 * @param n
	 * 012345
	 * 0145xx
	 */
	void deleteCharacters(int n);

	/**
	 * Deletes n lines, starting at line with cursor. As lines are deleted, 
	 * lines displayed below cursor move up. Lines added to bottom of screen 
	 * have spaces with same character attributes as last line moved up. This 
	 * sequence is ignored when cursor is outside scrolling region.
	 * @param n the number of lines to delete
	 */
	void deleteLines(int n);

	Style getDefaultStyle();

	void setDefaultStyle(Style defaultStyle);

	Style getStyle();

	/**
	 * Sets the style to be used from now on
	 * @param style
	 */
	void setStyle(Style style);

	/**
	 * This method displays a subset of the newly-received text in the Terminal
	 * view, wrapping text at the right edge of the screen and overwriting text
	 * when the cursor is not at the very end of the screen's text.
	 * <p>
	 * 
	 * There are never any ANSI control characters or escape sequences in the
	 * text being displayed by this method (this includes newlines, carriage
	 * returns, and tabs).
	 * <p>
	 */
	void appendString(String buffer);

	/**
	 * Process a newline (Control-J) character. A newline (NL) character just
	 * moves the cursor to the same column on the next line, creating new lines
	 * when the cursor reaches the bottom edge of the terminal. This is
	 * counter-intuitive, especially to UNIX programmers who are taught that
	 * writing a single NL to a terminal is sufficient to move the cursor to the
	 * first column of the next line, as if a carriage return (CR) and a NL were
	 * written.
	 * <p>
	 * 
	 * UNIX terminals typically display a NL character as a CR followed by a NL
	 * because the terminal device typically has the ONLCR attribute bit set
	 * (see the termios(4) man page for details), which causes the terminal
	 * device driver to translate NL to CR + NL on output. The terminal itself
	 * (i.e., a hardware terminal or a terminal emulator, like xterm or this
	 * code) _always_ interprets a CR to mean "move the cursor to the beginning
	 * of the current line" and a NL to mean "move the cursor to the same column
	 * on the next line".
	 * <p>
	 */
	void processNewline();

	/**
	 * This method returns the relative line number of the line containing the
	 * cursor. The returned line number is relative to the topmost visible line,
	 * which has relative line number 0.
	 * 
	 * @return The relative line number of the line containing the cursor.
	 */
	int getCursorLine();

	int getCursorColumn();

	/**
	 * This method moves the cursor to the specified line and column. Parameter
	 * <i>targetLine</i> is the line number of a screen line, so it has a
	 * minimum value of 0 (the topmost screen line) and a maximum value of
	 * heightInLines - 1 (the bottommost screen line). A line does not have to
	 * contain any text to move the cursor to any column in that line.
	 */
	void setCursor(int targetLine, int targetColumn);

	void setCursorColumn(int targetColumn);

	void setCursorLine(int targetLine);

	int getLines();

	int getColumns();

	void setScrollingRegion(int topLine, int bottomLine);
	
	int getScrollingRegionTopLine();
	int getScrollingRegionBottomLine();
	
	void setAlternativeScreenBuffer(boolean enable);

}