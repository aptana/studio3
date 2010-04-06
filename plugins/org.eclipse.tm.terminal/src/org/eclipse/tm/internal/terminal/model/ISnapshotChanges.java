/*******************************************************************************
 * Copyright (c) 2007, 2008 Wind River Systems, Inc. and others.
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

public interface ISnapshotChanges {

	/**
	 * @param line might bigger than the number of lines....
	 */
	void markLineChanged(int line);

	/**
	 * Marks all lines in the range as changed
	 * @param line >=0
	 * @param n might be out of range
	 */
	void markLinesChanged(int line, int n);

	/**
	 * Marks all lines within the scrolling region
	 * changed and resets the scrolling information
	 */
	void convertScrollingIntoChanges();

	/**
	 * @return true if something has changed
	 */
	boolean hasChanged();

	/**
	 * @param startLine
	 * @param size
	 * @param shift
	 */
	void scroll(int startLine, int size, int shift);

	/**
	 * Mark all lines changed
	 * @param height if no window is set this is the number of 
	 * lines that are marked as changed
	 */
	void setAllChanged(int height);

	int getFirstChangedLine();

	int getLastChangedLine();

	int getScrollWindowStartLine();

	int getScrollWindowSize();

	int getScrollWindowShift();

	boolean hasLineChanged(int line);

	void markDimensionsChanged();
	boolean hasDimensionsChanged();
	void markCursorChanged();

	/**
	 * @return true if the terminal data has changed
	 */
	boolean hasTerminalChanged();
	/**
	 * mark the terminal as changed
	 */
	void setTerminalChanged();


	void copyChangedLines(ITerminalTextData dest, ITerminalTextData source);

	/**
	 * @param startLine -1 means follow the end of the data
	 * @param size number of lines to follow
	 */
	void setInterestWindow(int startLine, int size);
	int getInterestWindowStartLine();
	int getInterestWindowSize();

}
