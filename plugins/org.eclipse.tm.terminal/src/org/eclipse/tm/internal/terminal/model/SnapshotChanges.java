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


/**
 * Collects the changes of the {@link ITerminalTextData}
 *
 */
public class SnapshotChanges implements ISnapshotChanges {
	/**
	 * The first line changed
	 */
	private int fFirstChangedLine;
	/**
	 * The last line changed
	 */
	private int fLastChangedLine;
	private int fScrollWindowStartLine;
	private int fScrollWindowSize;
	private int fScrollWindowShift;
	/**
	 * true, if scrolling should not tracked anymore
	 */
	private boolean fScrollDontTrack;
	/**
	 * The lines that need to be copied
	 * into the snapshot (lines that have
	 * not changed don't have to be copied)
	 */
	private boolean[] fChangedLines;
	
	private int fInterestWindowSize;
	private int fInterestWindowStartLine;
	private boolean fDimensionsChanged;
	private boolean fTerminalHasChanged;
	private boolean fCursorHasChanged;
	
	public SnapshotChanges(int nLines) {
		setChangedLinesLength(nLines);
		fFirstChangedLine=Integer.MAX_VALUE;
		fLastChangedLine=-1;
	}
	public SnapshotChanges(int windowStart, int windowSize) {
		setChangedLinesLength(windowStart+windowSize);
		fFirstChangedLine=Integer.MAX_VALUE;
		fLastChangedLine=-1;
		fInterestWindowStartLine=windowStart;
		fInterestWindowSize=windowSize;

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
	 * @param size
	 * @return true if the range overlaps with the interest window
	 */
	boolean isInInterestWindow(int line, int size) {
		if(fInterestWindowSize<=0)
			return true;
		if(line+size<=fInterestWindowStartLine || line>=fInterestWindowStartLine+fInterestWindowSize)
			return false;
		return true;
	}
	/**
	 * @param line
	 * @return true if the line is within the interest window
	 */
	boolean isInInterestWindow(int line) {
		if(fInterestWindowSize<=0)
			return true;
		if(line<fInterestWindowStartLine || line>=fInterestWindowStartLine+fInterestWindowSize)
			return false;
		return true;
	}
	/**
	 * @param line
	 * @return the line within the window
	 */
	int fitLineToWindow(int line) {
		if(fInterestWindowSize<=0)
			return line;
		if(line<fInterestWindowStartLine)
			return fInterestWindowStartLine;
		return line;
	}
	/**
	 * The result is only defined if {@link #isInInterestWindow(int, int)} returns true!
	 * @param line the line <b>before</b> {@link #fitLineToWindow(int)} has been called!
	 * @param size
	 * @return the adjusted size. 
	 * <p>Note:</p> {@link #fitLineToWindow(int)} has to be called on the line to
	 * move the window correctly!
	 */
	int fitSizeToWindow(int line, int size) {
		if(fInterestWindowSize<=0)
			return size;
		if(line<fInterestWindowStartLine) {
			size-=fInterestWindowStartLine-line;
			line=fInterestWindowStartLine;
		}
		if(line+size>fInterestWindowStartLine+fInterestWindowSize)
			size=fInterestWindowStartLine+fInterestWindowSize-line;
		return size;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.model.ISnapshotChanges#markLineChanged(int)
	 */
	public void markLineChanged(int line) {
		if(!isInInterestWindow(line))
			return;
		line=fitLineToWindow(line);
		if(line<fFirstChangedLine)
			fFirstChangedLine=line;
		if(line>fLastChangedLine)
			fLastChangedLine=line;
		// in case the terminal got resized we expand 
		// don't remember the changed line because
		// there is nothing to copy
		if(line<getChangedLineLength()) {
			setChangedLine(line,true);
		}
	}
	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.model.ISnapshotChanges#markLinesChanged(int, int)
	 */
	public void markLinesChanged(int line, int n) {
		if(n<=0 || !isInInterestWindow(line,n))
			return;
		// do not exceed the bounds of fChangedLines
		// the terminal might have been resized and 
		// we can only keep changes for the size of the
		// previous terminal
		n=fitSizeToWindow(line, n);
		line=fitLineToWindow(line);
		int m=Math.min(line+n, getChangedLineLength());
		for (int i = line; i < m; i++) {
			setChangedLine(i,true);
		}
		// this sets fFirstChangedLine as well
		markLineChanged(line);
		// this sets fLastChangedLine as well
		markLineChanged(line+n-1);
	}
	public void markCursorChanged() {
		fCursorHasChanged=true;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.model.ISnapshotChanges#convertScrollingIntoChanges()
	 */
	public void convertScrollingIntoChanges() {
		markLinesChanged(fScrollWindowStartLine,fScrollWindowSize);
		fScrollWindowStartLine=0;
		fScrollWindowSize=0;
		fScrollWindowShift=0;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.model.ISnapshotChanges#hasChanged()
	 */
	public boolean hasChanged() {
		if(fFirstChangedLine!=Integer.MAX_VALUE || fLastChangedLine>0 || fScrollWindowShift!=0 ||fDimensionsChanged || fCursorHasChanged)
			return true;
		return false;
	}
	public void markDimensionsChanged() {
		fDimensionsChanged=true;
	}
	public boolean hasDimensionsChanged() {
		return fDimensionsChanged;
	}
	public boolean hasTerminalChanged() {
		return fTerminalHasChanged;
	}
	public void setTerminalChanged() {
		fTerminalHasChanged=true;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.model.ISnapshotChanges#scroll(int, int, int)
	 */
	public void scroll(int startLine, int size, int shift) {
		size=fitSizeToWindow(startLine, size);
		startLine=fitLineToWindow(startLine);
		// let's track only negative shifts
		if(fScrollDontTrack) {
			// we are in a state where we cannot track scrolling
			// so let's simply mark the scrolled lines as changed
			markLinesChanged(startLine, size);
		} else if(shift>=0) {
			// we cannot handle positive scroll
			// forget about clever caching of scroll events
			doNotTrackScrollingAnymore();
			// mark all lines inside the scroll region as changed
			markLinesChanged(startLine, size);
		} else {
			// we have already scrolled
			if(fScrollWindowShift<0) {
				// we have already scrolled
				if(fScrollWindowStartLine==startLine && fScrollWindowSize==size) {
					// we are scrolling the same region again?
					fScrollWindowShift+=shift;
					scrollChangesLinesWithNegativeShift(startLine,size,shift);
				} else {
					// mark all lines in the old scroll region as changed
					doNotTrackScrollingAnymore();
					// mark all lines changed, because
					markLinesChanged(startLine, size);
				}
			} else {
				// first scroll in this change -- we just notify it
				fScrollWindowStartLine=startLine;
				fScrollWindowSize=size;
				fScrollWindowShift=shift;
				scrollChangesLinesWithNegativeShift(startLine,size,shift);
			}
		}
	}
	/**
	 * Some incompatible scrolling occurred. We cannot do the
	 * scroll optimization anymore...
	 */
	private void doNotTrackScrollingAnymore() {
		if(fScrollWindowSize>0) {
			// convert the current scrolling into changes
			markLinesChanged(fScrollWindowStartLine, fScrollWindowSize);
			fScrollWindowStartLine=0;
			fScrollWindowSize=0;
			fScrollWindowShift=0;
		}
		// don't be clever on scrolling anymore
		fScrollDontTrack=true;
	}
	/**
	 * Scrolls the changed lines data
	 *
	 * @param line
	 * @param n
	 * @param shift must be negative!
	 */
	private void scrollChangesLinesWithNegativeShift(int line, int n, int shift) {
		assert shift <0 || throwRuntimeException();
		// scroll the region
		// don't run out of bounds!
		int m=Math.min(line+n+shift,getChangedLineLength()+shift);
		for (int i = line; i < m; i++) {
			setChangedLine(i, hasLineChanged(i-shift));
			// move the first changed line up.
			// We don't have to move the maximum down,
			// because with a shift scroll, the max is moved
			// my the next loop in this method
			if(i<fFirstChangedLine && hasLineChanged(i)) {
				fFirstChangedLine=i;
			}
		}
		// mark the "opened" lines as changed
		for (int i = Math.max(0,line+n+shift); i < line+n; i++) {
			markLineChanged(i);
		}
	}
	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.model.ISnapshotChanges#setAllChanged(int)
	 */
	public void setAllChanged(int height) {
		fScrollWindowStartLine=0;
		fScrollWindowSize=0;
		fScrollWindowShift=0;
		fFirstChangedLine=fitLineToWindow(0);
		fLastChangedLine=fFirstChangedLine+fitSizeToWindow(0, height)-1;
		// no need to keep an array of changes anymore
		setChangedLinesLength(0);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.model.ISnapshotChanges#getFirstChangedLine()
	 */
	public int getFirstChangedLine() {
		return fFirstChangedLine;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.model.ISnapshotChanges#getLastChangedLine()
	 */
	public int getLastChangedLine() {
		return fLastChangedLine;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.model.ISnapshotChanges#getScrollWindowStartLine()
	 */
	public int getScrollWindowStartLine() {
		return fScrollWindowStartLine;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.model.ISnapshotChanges#getScrollWindowSize()
	 */
	public int getScrollWindowSize() {
		return fScrollWindowSize;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.model.ISnapshotChanges#getScrollWindowShift()
	 */
	public int getScrollWindowShift() {
		return fScrollWindowShift;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.model.ISnapshotChanges#copyChangedLines(org.eclipse.tm.terminal.model.ITerminalTextData, org.eclipse.tm.terminal.model.ITerminalTextData)
	 */
	public void copyChangedLines(ITerminalTextData dest, ITerminalTextData source) {
		int n=Math.min(fLastChangedLine+1,source.getHeight());
		for (int i = fFirstChangedLine; i < n ; i++) {
			if(hasLineChanged(i))
				dest.copyLine(source,i,i);
		}
	}
	
	public int getInterestWindowSize() {
		return fInterestWindowSize;
	}

	public int getInterestWindowStartLine() {
		return fInterestWindowStartLine;
	}

	public void setInterestWindow(int startLine, int size) {
		int oldStartLine=fInterestWindowStartLine;
		int oldSize=fInterestWindowSize;
		fInterestWindowStartLine=startLine;
		fInterestWindowSize=size;
		if(oldSize>0) {
			int shift=oldStartLine-startLine;
			if(shift==0) {
				if(size>oldSize) {
					// add lines to the end
					markLinesChanged(oldStartLine+oldSize, size-oldSize);
				}
				// else no lines within the window have changed
					
			} else if(Math.abs(shift)<size) {
				if(shift<0) {
					// we can scroll
					scroll(startLine, oldSize, shift);
					// mark the lines at the end as new
					for (int i = oldStartLine+oldSize; i < startLine+size; i++) {
						markLineChanged(i);
					}
				} else {
					// we cannot shift positive -- mark all changed
					markLinesChanged(startLine, size);
				}
			} else {
				// no scrolling possible
				markLinesChanged(startLine, size);
			}
				
		}
	}
	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.model.ISnapshotChanges#hasLineChanged(int)
	 */
	public boolean hasLineChanged(int line) {
		if(line<fChangedLines.length)
			return fChangedLines[line];
		// since the height of the terminal could
		// have changed but we have tracked only changes
		// of the previous terminal height, any line outside
		// the the range of the previous height has changed
		return isInInterestWindow(line);
	}
	int getChangedLineLength() {
		return fChangedLines.length;
	}
	void setChangedLine(int line,boolean changed){
		fChangedLines[line]=changed;
	}
	void setChangedLinesLength(int length) {
		fChangedLines=new boolean[length];
	}
}
