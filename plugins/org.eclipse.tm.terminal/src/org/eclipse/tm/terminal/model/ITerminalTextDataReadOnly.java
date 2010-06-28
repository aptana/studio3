/*******************************************************************************
 * Copyright (c) 2007, 2009 Wind River Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Michael Scharf (Wind River) - initial API and implementation
 * Martin Oberhuber (Wind River) - [261486][api][cleanup] Mark @noimplement interfaces as @noextend
 *******************************************************************************/
package org.eclipse.tm.terminal.model;

/**
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface ITerminalTextDataReadOnly {

	/**
	 * @return the width of the terminal
	 */
	int getWidth();

	/**
	 * @return the height of the terminal
	 */
	int getHeight();

	/**
	 * @param line be >=0 and < height
	 * @param startCol must be >=0 and < width
	 * @param numberOfCols must be > 0
	 * @return a the line segments of the specified range
	 */
	LineSegment[] getLineSegments(int line, int startCol, int numberOfCols);

	/**
	 * @param line must be >=0 and < height
	 * @param column must be >=0 and < width
	 * @return the character at column,line
	 */
	char getChar(int line, int column);

	/**
	 * @param line must be >=0 and < height
	 * @param column must be >=0 and < width
	 * @return style at column,line or null
	 */
	Style getStyle(int line, int column);

	/**
	 * Creates a new instance of {@link ITerminalTextDataSnapshot} that
	 * can be used to track changes. Make sure to call {@link ITerminalTextDataSnapshot#detach()}
	 * if you don't need the snapshots anymore.
	 * <p><b>Note: </b>A new snapshot is empty and needs a call to {@link ITerminalTextDataSnapshot#updateSnapshot(boolean)} to
	 * get its initial values. You might want to setup the snapshot to your needs by calling
	 * {@link ITerminalTextDataSnapshot#setInterestWindow(int, int)}.
	 * </p>
	 * @return a new instance of {@link ITerminalTextDataSnapshot} that "listens" to changes of
	 * <code>this</code>.
	 */
	public ITerminalTextDataSnapshot makeSnapshot();

	char[] getChars(int line);
	Style[] getStyles(int line);

	/**
	 * @return the line in which the cursor is at the moment
	 */
	int getCursorLine();
	/**
	 * @return the column at which the cursor is at the moment
	 */
	int getCursorColumn();
}