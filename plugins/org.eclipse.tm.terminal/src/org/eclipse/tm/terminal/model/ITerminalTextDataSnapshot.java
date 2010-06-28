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
 * This class maintains a snapshot of an instance of {@link ITerminalTextData}.
 * While the {@link ITerminalTextData} continues changing, the snapshot remains
 * unchanged until the next snapshot is taken by calling
 * {@link #updateSnapshot(boolean)}. This is important, because the
 * {@link ITerminalTextData} might get modified by another thread. Suppose you
 * would want to draw the content of the {@link ITerminalTextData} using the
 * following loop:
 *
 * <pre>
 * for (int line = 0; line &lt; term.getHeight(); line++)
 * 	for (int column = 0; column &lt; term.getWidth(); column++)
 * 		drawCharacter(column, line, term.getChar(column, line), term.getStyle(column, line));
 * </pre>
 *
 * This might fail because the background thread could change the dimensions of
 * the {@link ITerminalTextData} while you iterate the loop. One solution would
 * be to put a <code>synchronized(term){}</code> statement around the code. This
 * has two problems: 1. you would have to know about the internals of the
 * synchronisation of {@link ITerminalTextData}. 2. The other thread that
 * changes {@link ITerminalTextData} is blocked while the potentially slow
 * drawing is done.
 * <p>
 * <b>Solution:</b> Take a snapshot of the terminal and use the snapshot to draw
 * the content. There is no danger that the data structure get changed while you
 * draw. There are also methods to find out what has changed to minimize the
 * number of lines that get redrawn.
 * </p>
 *
 * <p>
 * <b>Drawing optimization</b>: To optimize redrawing of changed lines, this
 * class keeps track of lines that have changed since the previous snapshot.
 * </p>
 *
 * <pre>
 * // iterate over the potentially changed lines
 * for (int line = snap.getFirstChangedLine(); line &lt;= snap.getLastChangedLine(); line++)
 * 	// redraw only if the line has changed
 * 	if (snap.hasLineChanged(line))
 * 		for (int column = 0; column &lt; snap.getWidth(); column++)
 * 			drawCharacter(column, line, snap.getChar(column, line), snap.getStyle(column, line));
 * </pre>
 *
 * <p>
 * <b>Scroll optimization:</b> Often new lines are appended at the bottom of the
 * terminal and the rest of the lines are scrolled up. In this case all lines
 * would be marked as changed. To optimize for this case,
 * {@link #updateSnapshot(boolean)} can be called with <code>true</code> for the
 * <code>detectScrolling</code> parameter. The object will keep track of
 * scrolling. The UI must <b>first</b> handle the scrolling and then use the
 * {@link #hasLineChanged(int)} method to determine scrolling:
 *
 * <pre>
 * // scroll the visible region of the UI &lt;b&gt;before&lt;/b&gt; drawing the changed lines.
 * doUIScrolling(snap.getScrollChangeY(), snap.getScrollChangeN(), snap.getScrollChangeShift());
 * // iterate over the potentially changed lines
 * for (int line = snap.getFirstChangedLine(); line &lt;= snap.getFirstChangedLine(); line++)
 * 	// redraw only if the line has changed
 * 	if (snap.hasLineChanged(line))
 * 		for (int column = 0; column &lt; snap.getWidth(); column++)
 * 			drawCharacter(column, line, snap.getChar(column, line), snap.getStyle(column, line));
 * </pre>
 *
 * </p>
 * <p>
 * <b>Threading Note</b>: This class is not thread safe! All methods have to be
 * called by the a same thread, that created the instance by calling
 * {@link ITerminalTextDataReadOnly#makeSnapshot()}.
 * </p>
 *
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface ITerminalTextDataSnapshot extends ITerminalTextDataReadOnly {
	/**
	 * This listener gets called when the current snapshot
	 * is out of date. Calling {@link ITerminalTextDataSnapshot#updateSnapshot(boolean)}
	 * will have an effect. Once the {@link #snapshotOutOfDate(ITerminalTextDataSnapshot)} method is called,
	 * it will not be called until {@link ITerminalTextDataSnapshot#updateSnapshot(boolean)}
	 * is called and a new snapshot needs to be updated again.
	 * <p>
	 * A typical terminal view would not update the snapshot immediately
	 * after the {@link #snapshotOutOfDate(ITerminalTextDataSnapshot)} has been called. It would introduce a
	 * delay to update the UI (and the snapshot} 10 or 20 times per second.
	 *
	 * <p>Make sure you don't spend too much time in this method.
	 */
	interface SnapshotOutOfDateListener {
		/**
		 * Gets called when the snapshot is out of date. To get the snapshot up to date,
		 * call {@link ITerminalTextDataSnapshot#updateSnapshot(boolean)}.
		 * @param snapshot The snapshot that is out of date
		 */
		void snapshotOutOfDate(ITerminalTextDataSnapshot snapshot);
	}
	void addListener(SnapshotOutOfDateListener listener);
	void removeListener(SnapshotOutOfDateListener listener);

	/**
	 * Ends the listening to the {@link ITerminalTextData}. After this
	 * has been called no new snapshot data is collected.
	 */
	void detach();
	/**
	 * @return true if the data has changed since the previous snapshot.
	 */
	boolean isOutOfDate();

	/**
	 * The window of interest is the region the snapshot should track.
	 * Changes outside this region are ignored. The change takes effect after
	 * an update!
	 * @param startLine -1 means track the end of the data
	 * @param size number of lines to track. A size of -1 means track all.
	 */
	void setInterestWindow(int startLine, int size);
	int getInterestWindowStartLine();
	int getInterestWindowSize();

	/**
	 * Create a new snapshot of the {@link ITerminalTextData}. It will efficiently
	 * copy the data of the {@link ITerminalTextData} into an internal representation.
	 * The snapshot also keeps track of the changes since the previous snapshot.
	 * <p>With the methods {@link #getFirstChangedLine()}, {@link #getLastChangedLine()} and
	 * {@link #hasLineChanged(int)}
	 * you can find out what has changed in the current snapshot since the previous snapshot.
	 * @param detectScrolling if <code>true</code> the snapshot tries to identify scroll
	 * changes since the last snapshot. In this case the information about scrolling
	 * can be retrieved using the following methods:
	 * {@link #getScrollWindowStartLine()}, {@link #getScrollWindowSize()} and {@link #getScrollWindowShift()}
	 * <br><b>Note:</b> The method {@link #hasLineChanged(int)} returns changes <b>after</b> the
	 * scrolling has been applied.
	 */
	void updateSnapshot(boolean detectScrolling);

	/**
	 * @return The first line changed in this snapshot compared
	 * to the previous snapshot.
	 *
	 * <p><b>Note:</b> If no line has changed, this
	 * returns {@link Integer#MAX_VALUE}
	 *
	 * <p><b>Note:</b> if {@link #updateSnapshot(boolean)} has been called with <code>true</code>,
	 * then this does not include lines that only have been scrolled. This is the
	 * first line that has changed <b>after</b> the scroll has been applied.
	 */
	int getFirstChangedLine();

	/**
	 * @return The last line changed in this snapshot compared
	 * to the previous snapshot. If the height has changed since the
	 * last update of the snapshot, then the returned value is within
	 * the new dimensions.
	 *
	 * <p><b>Note:</b> If no line has changed, this returns <code>-1</code>
	 *
	 * <p><b>Note:</b> if {@link #updateSnapshot(boolean)} has been called with <code>true</code>,
	 * then this does not include lines that only have been scrolled. This is the
	 * last line that has changed <b>after</b> the scroll has been applied.
	 *
	 * <p>A typical for loop using this method would look like this (note the <code>&lt;=</code> in the for loop):
	 * <pre>
	 * for(int line=snap.{@link #getFirstChangedLine()}; line <b>&lt;=</b> snap.getLastChangedLine(); line++)
	 *    if(snap.{@link #hasLineChanged(int) hasLineChanged(line)})
	 *       doSomething(line);
	 * </pre>
	 */
	int getLastChangedLine();

	/**
	 * @param line
	 * @return true if the line has changed since the previous snapshot
	 */
	boolean hasLineChanged(int line);

	boolean hasDimensionsChanged();

	/**
	 * @return true if the terminal has changed (and not just the
	 * window of interest)
	 */
	boolean hasTerminalChanged();
	/**
	 * If {@link #updateSnapshot(boolean)} was called with <code>true</code>, then this method
	 * returns the top of the scroll region.
	 * @return The first line scrolled in this snapshot compared
	 * to the previous snapshot. See also {@link ITerminalTextData#scroll(int, int, int)}.
	 */
	int getScrollWindowStartLine();

	/**
	 * If {@link #updateSnapshot(boolean)} was called with <code>true</code>, then this method
	 * returns the size of the scroll region.
	 * @return The number of lines scrolled  in this snapshot compared
	 * to the previous snapshot. See also {@link ITerminalTextData#scroll(int, int, int)}
	 * If nothing has changed, 0 is returned.
	 */
	int getScrollWindowSize();

	/**
	 * If {@link #updateSnapshot(boolean)} was called with <code>true</code>, then this method
	 * returns number of lines moved by the scroll region.
	 * @return The the scroll shift of this snapshot compared
	 * to the previous snapshot. See also {@link ITerminalTextData#scroll(int, int, int)}
	 */
	int getScrollWindowShift();

	/**
	 * @return The {@link ITerminalTextData} on that this instance is observing.
	 */
	ITerminalTextData getTerminalTextData();

}
