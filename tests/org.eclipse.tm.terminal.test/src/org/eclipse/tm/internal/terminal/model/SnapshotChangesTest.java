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

import junit.framework.TestCase;

public class SnapshotChangesTest extends TestCase {
	/**
	 * @param change
	 * @param expected a string of 0 and 1 (1 means changed)
	 */
	void assertChangedLines(ISnapshotChanges change, String expected) {
		StringBuffer buffer=new StringBuffer();
		for (int line = 0; line < expected.length(); line++) {
			if(change.hasLineChanged(line))
				buffer.append('1');
			else
				buffer.append('0');
		}
		assertEquals(expected, buffer.toString());
	}

	public void testSnapshotChanges() {
		SnapshotChanges changes=new SnapshotChanges(1);
		assertEquals(0, changes.getInterestWindowStartLine());
		assertEquals(0, changes.getInterestWindowSize());
	}
	public void testSnapshotChangesWithWindow() {
		SnapshotChanges changes=new SnapshotChanges(2,5);
		assertEquals(2, changes.getInterestWindowStartLine());
		assertEquals(5, changes.getInterestWindowSize());
	}

	public void testIsInInterestWindowIntInt() {
		SnapshotChanges changes=new SnapshotChanges(2,5);
		assertFalse(changes.isInInterestWindow(0, 1));
		assertFalse(changes.isInInterestWindow(0, 2));
		assertTrue(changes.isInInterestWindow(0, 3));
		assertTrue(changes.isInInterestWindow(0, 4));
		assertTrue(changes.isInInterestWindow(0, 5));
		assertTrue(changes.isInInterestWindow(0, 6));
		assertTrue(changes.isInInterestWindow(0, 10));
		assertTrue(changes.isInInterestWindow(2, 5));
		assertTrue(changes.isInInterestWindow(6, 0));
		assertTrue(changes.isInInterestWindow(6, 1));
		assertTrue(changes.isInInterestWindow(6, 10));
		assertFalse(changes.isInInterestWindow(7, 0));
		assertFalse(changes.isInInterestWindow(7, 1));
		assertFalse(changes.isInInterestWindow(8, 10));
	}
	public void testIsInInterestWindowIntIntNoWindow() {
		SnapshotChanges changes=new SnapshotChanges(3);
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				assertTrue(changes.isInInterestWindow(i,j));				
			}			
		}
	}

	public void testIsInInterestWindowInt() {
		SnapshotChanges changes=new SnapshotChanges(3,1);
		assertFalse(changes.isInInterestWindow(0));
		assertFalse(changes.isInInterestWindow(1));
		assertFalse(changes.isInInterestWindow(2));
		assertTrue(changes.isInInterestWindow(3));
		assertFalse(changes.isInInterestWindow(4));
		assertFalse(changes.isInInterestWindow(5));
	}

	public void testIsInInterestWindowIntNoWindow() {
		SnapshotChanges changes=new SnapshotChanges(3);
		for (int i = 0; i < 10; i++) {
			assertTrue(changes.isInInterestWindow(i));
		}
	}

	public void testFitLineToWindow() {
		SnapshotChanges changes=new SnapshotChanges(2,5);
		assertEquals(2, changes.fitLineToWindow(0));
		assertEquals(2, changes.fitLineToWindow(1));
		assertEquals(2, changes.fitLineToWindow(2));
		assertEquals(3, changes.fitLineToWindow(3));
		
		assertTrue(changes.isInInterestWindow(4));
		assertEquals(4, changes.fitLineToWindow(4));

		assertTrue(changes.isInInterestWindow(5));
		assertEquals(5, changes.fitLineToWindow(5));

		assertTrue(changes.isInInterestWindow(6));
		assertEquals(6, changes.fitLineToWindow(6));
		
		assertFalse(changes.isInInterestWindow(7));
		// value undefined!
		assertEquals(7, changes.fitLineToWindow(7));

		assertFalse(changes.isInInterestWindow(8));
		// value undefined!
		assertEquals(8, changes.fitLineToWindow(8));
	}

	public void testFitLineToWindowNoWindow() {
		SnapshotChanges changes=new SnapshotChanges(5);
		assertEquals(0, changes.fitLineToWindow(0));
		assertEquals(1, changes.fitLineToWindow(1));
		assertEquals(2, changes.fitLineToWindow(2));
		assertEquals(3, changes.fitLineToWindow(3));
		assertEquals(4, changes.fitLineToWindow(4));
		assertEquals(5, changes.fitLineToWindow(5));
		assertEquals(6, changes.fitLineToWindow(6));
		assertEquals(7, changes.fitLineToWindow(7));
	}
	public void testFitSizeToWindow() {
		SnapshotChanges changes=new SnapshotChanges(2,3);
		assertFalse(changes.isInInterestWindow(0, 1));
		assertFalse(changes.isInInterestWindow(0, 2));
		assertTrue(changes.isInInterestWindow(0, 3));
		assertEquals(1, changes.fitSizeToWindow(0,3));
		assertEquals(2, changes.fitSizeToWindow(0,4));
		assertEquals(3, changes.fitSizeToWindow(0,5));
		assertEquals(3, changes.fitSizeToWindow(0,6));
		assertEquals(3, changes.fitSizeToWindow(0,7));
		assertEquals(3, changes.fitSizeToWindow(0,8));
		assertEquals(3, changes.fitSizeToWindow(0,9));
		assertEquals(3, changes.fitSizeToWindow(1,9));
		assertEquals(3, changes.fitSizeToWindow(2,9));
		assertEquals(3, changes.fitSizeToWindow(2,3));
		assertEquals(2, changes.fitSizeToWindow(2,2));
		assertEquals(1, changes.fitSizeToWindow(2,1));
		assertEquals(2, changes.fitSizeToWindow(3,9));
		assertEquals(2, changes.fitSizeToWindow(3,2));
		assertEquals(1, changes.fitSizeToWindow(3,1));
		assertEquals(2, changes.fitSizeToWindow(3,2));
		assertEquals(2, changes.fitSizeToWindow(3,3));
		assertEquals(1, changes.fitSizeToWindow(4,1));
		assertEquals(1, changes.fitSizeToWindow(4,2));
		assertFalse(changes.isInInterestWindow(5, 1));

	}
	public void testFitSizeToWindowNoWindow() {
		SnapshotChanges changes=new SnapshotChanges(3);
		assertEquals(1, changes.fitSizeToWindow(0,1));
		assertEquals(2, changes.fitSizeToWindow(0,2));
		assertEquals(3, changes.fitSizeToWindow(0,3));
		assertEquals(4, changes.fitSizeToWindow(0,4));
		assertEquals(5, changes.fitSizeToWindow(0,5));

		assertEquals(5, changes.fitSizeToWindow(1,5));
		assertEquals(3, changes.fitSizeToWindow(2,3));
		assertEquals(2, changes.fitSizeToWindow(1,2));
		assertEquals(10, changes.fitSizeToWindow(5,10));
	}

	public void testMarkLineChanged() {
		SnapshotChanges changes=new SnapshotChanges(2,3);
		assertFalse(changes.hasChanged());
		changes.markLineChanged(0);
		assertFalse(changes.hasChanged());
		changes.markLineChanged(1);
		assertFalse(changes.hasChanged());
		changes.markLineChanged(2);
		assertTrue(changes.hasChanged());
		
		changes=new SnapshotChanges(2,3);
		assertFalse(changes.hasChanged());
		changes.markLineChanged(3);
		assertTrue(changes.hasChanged());

		assertLineChange(false,2,3,0);
		assertLineChange(false,2,3,1);
		assertLineChange(true,2,3,2);
		assertLineChange(true,2,3,3);
		assertLineChange(true,2,3,4);
		assertLineChange(false,2,3,5);
		assertLineChange(false,2,3,6);

		assertLineChange(true,2,4,5);
	}
	void assertLineChange(boolean expected, int windowStart, int windowSize, int changedLine) {
		SnapshotChanges changes=new SnapshotChanges(windowStart,windowSize);
		assertFalse(changes.hasChanged());
		changes.markLineChanged(changedLine);
		if(expected) {
			assertEquals(changedLine, changes.getFirstChangedLine());
			assertEquals(changedLine, changes.getLastChangedLine());
		} else {
			assertEquals(Integer.MAX_VALUE, changes.getFirstChangedLine());
			assertEquals(-1, changes.getLastChangedLine());
			
		}
		assertEquals(expected,changes.hasChanged());
		for (int i = 0; i < windowStart+windowSize+5; i++) {
			boolean e= i==changedLine && i>=windowStart && i<windowStart+windowSize;
			assertEquals(e, changes.hasLineChanged(i));
		}
		
	}
	public void testMarkLinesChanged() {
		SnapshotChanges changes=new SnapshotChanges(2,3);
		assertFalse(changes.hasChanged());
		assertEquals(Integer.MAX_VALUE, changes.getFirstChangedLine());
		assertEquals(-1, changes.getLastChangedLine());
		changes.markLinesChanged(0, 1);
		assertChangedLines(changes, "00000000000");
		assertFalse(changes.hasChanged());
		assertEquals(Integer.MAX_VALUE, changes.getFirstChangedLine());
		assertEquals(-1, changes.getLastChangedLine());
		changes.markLinesChanged(0, 2);
		assertChangedLines(changes, "00000000000");
		assertFalse(changes.hasChanged());
		assertEquals(Integer.MAX_VALUE, changes.getFirstChangedLine());
		assertEquals(-1, changes.getLastChangedLine());
		changes.markLinesChanged(0, 3);
		assertEquals(2, changes.getFirstChangedLine());
		assertEquals(2, changes.getLastChangedLine());
		assertTrue(changes.hasChanged());
		assertChangedLines(changes, "00100000000");
		
		changes=new SnapshotChanges(2,3);
		changes.markLinesChanged(1, 3);
		assertTrue(changes.hasChanged());
		assertEquals(2, changes.getFirstChangedLine());
		assertEquals(3, changes.getLastChangedLine());
		assertChangedLines(changes, "00110000000");

		changes=new SnapshotChanges(2,3);
		changes.markLinesChanged(1, 4);
		assertEquals(2, changes.getFirstChangedLine());
		assertEquals(4, changes.getLastChangedLine());
		assertTrue(changes.hasChanged());
		assertChangedLines(changes, "00111000000");

		changes=new SnapshotChanges(2,3);
		changes.markLinesChanged(1, 4);
		assertEquals(2, changes.getFirstChangedLine());
		assertEquals(4, changes.getLastChangedLine());
		assertTrue(changes.hasChanged());
		assertChangedLines(changes, "00111000000");

		changes=new SnapshotChanges(2,3);
		changes.markLinesChanged(2, 4);
		assertEquals(2, changes.getFirstChangedLine());
		assertEquals(4, changes.getLastChangedLine());
		assertTrue(changes.hasChanged());
		assertChangedLines(changes, "00111000000");

	
		changes=new SnapshotChanges(2,3);
		changes.markLinesChanged(3, 4);
		assertEquals(3, changes.getFirstChangedLine());
		assertEquals(4, changes.getLastChangedLine());
		assertTrue(changes.hasChanged());
		assertChangedLines(changes, "00011000000");

		changes=new SnapshotChanges(2,3);
		changes.markLinesChanged(3, 1);
		assertEquals(3, changes.getFirstChangedLine());
		assertEquals(3, changes.getLastChangedLine());
		assertTrue(changes.hasChanged());
		assertChangedLines(changes, "00010000000");

		changes=new SnapshotChanges(2,3);
		changes.markLinesChanged(4, 1);
		assertEquals(4, changes.getFirstChangedLine());
		assertEquals(4, changes.getLastChangedLine());
		assertTrue(changes.hasChanged());
		assertChangedLines(changes, "00001000000");
		
		changes=new SnapshotChanges(2,3);
		changes.markLinesChanged(5, 1);
		assertEquals(Integer.MAX_VALUE, changes.getFirstChangedLine());
		assertEquals(-1, changes.getLastChangedLine());
		assertFalse(changes.hasChanged());
		assertChangedLines(changes, "00000000000");
	}
	public void testMarkLinesChangedNoWindow() {
		SnapshotChanges changes=new SnapshotChanges(10);
		assertFalse(changes.hasChanged());
		assertEquals(Integer.MAX_VALUE, changes.getFirstChangedLine());
		assertEquals(-1, changes.getLastChangedLine());
		
		changes.markLinesChanged(0, 1);
		assertTrue(changes.hasChanged());
		assertEquals(0, changes.getFirstChangedLine());
		assertEquals(0, changes.getLastChangedLine());
		assertChangedLines(changes, "1000000000");
		
		changes=new SnapshotChanges(10);
		changes.markLinesChanged(0, 5);
		assertTrue(changes.hasChanged());
		assertEquals(0, changes.getFirstChangedLine());
		assertEquals(4, changes.getLastChangedLine());
		assertChangedLines(changes, "1111100000");

		changes=new SnapshotChanges(3);
		changes.markLinesChanged(1, 6);
		assertTrue(changes.hasChanged());
		assertEquals(1, changes.getFirstChangedLine());
		assertEquals(6, changes.getLastChangedLine());
		assertChangedLines(changes, "011");

		
		changes=new SnapshotChanges(10);
		changes.markLinesChanged(5, 6);
		assertTrue(changes.hasChanged());
		assertEquals(5, changes.getFirstChangedLine());
		assertEquals(10, changes.getLastChangedLine());
		assertChangedLines(changes, "0000011111");

	}

	public void testHasChanged() {
		SnapshotChanges changes=new SnapshotChanges(0);
		assertFalse(changes.hasChanged());
		changes=new SnapshotChanges(1);
		assertFalse(changes.hasChanged());
		changes=new SnapshotChanges(1,9);
		assertFalse(changes.hasChanged());
	}

	public void testSetAllChanged() {
		SnapshotChanges changes;
		changes=new SnapshotChanges(2,3);
		changes.setAllChanged(10);
		assertEquals(2, changes.getFirstChangedLine());
		assertEquals(4, changes.getLastChangedLine());
		assertTrue(changes.hasChanged());
		assertChangedLines(changes, "00111000000");

		changes=new SnapshotChanges(2,3);
		changes.setAllChanged(3);
		assertEquals(2, changes.getFirstChangedLine());
		assertEquals(2, changes.getLastChangedLine());
		assertTrue(changes.hasChanged());
		assertChangedLines(changes, "00111000000");

		changes=new SnapshotChanges(2,3);
		changes.setAllChanged(4);
		assertEquals(2, changes.getFirstChangedLine());
		assertEquals(3, changes.getLastChangedLine());
		assertTrue(changes.hasChanged());
		assertChangedLines(changes, "00111000000");

		changes=new SnapshotChanges(2,3);
		changes.setAllChanged(5);
		assertEquals(2, changes.getFirstChangedLine());
		assertEquals(4, changes.getLastChangedLine());
		assertTrue(changes.hasChanged());
		assertChangedLines(changes, "00111000000");

		changes=new SnapshotChanges(2,3);
		changes.setAllChanged(6);
		assertEquals(2, changes.getFirstChangedLine());
		assertEquals(4, changes.getLastChangedLine());
		assertTrue(changes.hasChanged());
		assertChangedLines(changes, "00111000000");
	}
	public void testSetAllChangedNoWindow() {
		SnapshotChanges changes;
		changes=new SnapshotChanges(5);
		changes.setAllChanged(10);
		assertEquals(0, changes.getFirstChangedLine());
		assertEquals(9, changes.getLastChangedLine());
		assertTrue(changes.hasChanged());
		assertChangedLines(changes, "1111111111");

		changes=new SnapshotChanges(5);
		changes.setAllChanged(3);
		assertEquals(0, changes.getFirstChangedLine());
		assertEquals(2, changes.getLastChangedLine());
		assertTrue(changes.hasChanged());
		assertChangedLines(changes, "1111111111");

	}

	public void testConvertScrollingIntoChanges() {
		SnapshotChanges changes;
		changes=new SnapshotChanges(2,3);
		changes.scroll(0, 4, -1);
		assertTrue(changes.hasChanged());
		assertChangedLines(changes, "000100");
		changes.convertScrollingIntoChanges();
		assertEquals(2, changes.getFirstChangedLine());
		assertEquals(3, changes.getLastChangedLine());
		assertEquals(0, changes.getScrollWindowStartLine());
		assertEquals(0, changes.getScrollWindowSize());
		assertEquals(0, changes.getScrollWindowShift());
		assertChangedLines(changes, "001100");
	}

	public void testConvertScrollingIntoChangesNoWindow() {
		SnapshotChanges changes;
		changes=new SnapshotChanges(7);
		changes.scroll(0, 4, -1);
		assertTrue(changes.hasChanged());
		assertChangedLines(changes, "000100");
		changes.convertScrollingIntoChanges();
		assertEquals(0, changes.getFirstChangedLine());
		assertEquals(3, changes.getLastChangedLine());
		assertEquals(0, changes.getScrollWindowStartLine());
		assertEquals(0, changes.getScrollWindowSize());
		assertEquals(0, changes.getScrollWindowShift());
		assertChangedLines(changes, "111100");
	}
	public void testScrollNoWindow() {
		SnapshotChanges changes;
		changes=new SnapshotChanges(7);
		changes.scroll(0, 3, -2);
		assertEquals(1, changes.getFirstChangedLine());
		assertEquals(2, changes.getLastChangedLine());
		assertEquals(0, changes.getScrollWindowStartLine());
		assertEquals(3, changes.getScrollWindowSize());
		assertEquals(-2, changes.getScrollWindowShift());
		assertTrue(changes.hasChanged());
		assertChangedLines(changes, "0110000");

		changes=new SnapshotChanges(7);
		changes.scroll(0, 3, -1);
		changes.scroll(0, 3, -1);
		assertEquals(1, changes.getFirstChangedLine());
		assertEquals(2, changes.getLastChangedLine());
		assertEquals(0, changes.getScrollWindowStartLine());
		assertEquals(3, changes.getScrollWindowSize());
		assertEquals(-2, changes.getScrollWindowShift());
		assertTrue(changes.hasChanged());
		assertChangedLines(changes, "0110000");
		
		changes=new SnapshotChanges(7);
		changes.scroll(0, 7, -1);
		changes.scroll(0, 7, -1);
		assertEquals(5, changes.getFirstChangedLine());
		assertEquals(6, changes.getLastChangedLine());
		assertEquals(0, changes.getScrollWindowStartLine());
		assertEquals(7, changes.getScrollWindowSize());
		assertEquals(-2, changes.getScrollWindowShift());
		assertTrue(changes.hasChanged());
		assertChangedLines(changes, "0000011");
		
		// positive scrolls cannot be optimized at the moment
		changes=new SnapshotChanges(7);
		changes.scroll(0, 7, 1);
		changes.scroll(0, 7, 1);
		assertEquals(0, changes.getFirstChangedLine());
		assertEquals(6, changes.getLastChangedLine());
		assertEquals(0, changes.getScrollWindowStartLine());
		assertEquals(0, changes.getScrollWindowSize());
		assertEquals(0, changes.getScrollWindowShift());
		assertTrue(changes.hasChanged());
		assertChangedLines(changes, "1111111");

	}
	public void testScroll() {
		SnapshotChanges changes;
		changes=new SnapshotChanges(2,3);
		changes.scroll(0, 7, -1);
		assertEquals(4, changes.getFirstChangedLine());
		assertEquals(4, changes.getLastChangedLine());
		assertEquals(2, changes.getScrollWindowStartLine());
		assertEquals(3, changes.getScrollWindowSize());
		assertEquals(-1, changes.getScrollWindowShift());
		assertTrue(changes.hasChanged());
		assertChangedLines(changes, "0000100000");

		changes=new SnapshotChanges(2,3);
		changes.scroll(0, 7, -2);
		assertEquals(3, changes.getFirstChangedLine());
		assertEquals(4, changes.getLastChangedLine());
		assertEquals(2, changes.getScrollWindowStartLine());
		assertEquals(3, changes.getScrollWindowSize());
		assertEquals(-2, changes.getScrollWindowShift());
		assertTrue(changes.hasChanged());
		assertChangedLines(changes, "0001100000");
	}
	public void testScrollNergative() {
		SnapshotChanges changes;
		changes=new SnapshotChanges(2,3);
		changes.scroll(0, 7, -1);
		changes.scroll(0, 7, -1);
		assertEquals(3, changes.getFirstChangedLine());
		assertEquals(4, changes.getLastChangedLine());
		assertEquals(2, changes.getScrollWindowStartLine());
		assertEquals(3, changes.getScrollWindowSize());
		assertEquals(-2, changes.getScrollWindowShift());
		assertTrue(changes.hasChanged());
		assertChangedLines(changes, "0001100000");
	
	}
	public void testScrollPositive() {
		SnapshotChanges changes;
		changes=new SnapshotChanges(2,3);
		changes.scroll(0, 7, 1);
		changes.scroll(0, 7, 1);
		assertEquals(2, changes.getFirstChangedLine());
		assertEquals(4, changes.getLastChangedLine());
		assertEquals(0, changes.getScrollWindowStartLine());
		assertEquals(0, changes.getScrollWindowSize());
		assertEquals(0, changes.getScrollWindowShift());
		assertTrue(changes.hasChanged());
		assertChangedLines(changes, "0011100000");
	
		changes=new SnapshotChanges(2,3);
		changes.scroll(0, 3, 1);
		changes.scroll(0, 3, 1);
		assertEquals(2, changes.getFirstChangedLine());
		assertEquals(2, changes.getLastChangedLine());
		assertEquals(0, changes.getScrollWindowStartLine());
		assertEquals(0, changes.getScrollWindowSize());
		assertEquals(0, changes.getScrollWindowShift());
		assertTrue(changes.hasChanged());
		assertChangedLines(changes, "0010000000");
		
		changes=new SnapshotChanges(2,3);
		changes.scroll(0, 4, 1);
		changes.scroll(0, 4, 1);
		assertEquals(2, changes.getFirstChangedLine());
		assertEquals(3, changes.getLastChangedLine());
		assertEquals(0, changes.getScrollWindowStartLine());
		assertEquals(0, changes.getScrollWindowSize());
		assertEquals(0, changes.getScrollWindowShift());
		assertTrue(changes.hasChanged());
		assertChangedLines(changes, "0011000000");
		
		changes=new SnapshotChanges(2,3);
		changes.scroll(0, 5, 1);
		changes.scroll(0, 5, 1);
		assertEquals(2, changes.getFirstChangedLine());
		assertEquals(4, changes.getLastChangedLine());
		assertEquals(0, changes.getScrollWindowStartLine());
		assertEquals(0, changes.getScrollWindowSize());
		assertEquals(0, changes.getScrollWindowShift());
		assertTrue(changes.hasChanged());
		assertChangedLines(changes, "0011100000");
		
		changes=new SnapshotChanges(2,3);
		changes.scroll(3, 5, 1);
		changes.scroll(3, 5, 1);
		assertEquals(3, changes.getFirstChangedLine());
		assertEquals(4, changes.getLastChangedLine());
		assertEquals(0, changes.getScrollWindowStartLine());
		assertEquals(0, changes.getScrollWindowSize());
		assertEquals(0, changes.getScrollWindowShift());
		assertTrue(changes.hasChanged());
		assertChangedLines(changes, "0001100000");
	}

	public void testCopyChangedLines() {
		SnapshotChanges changes;
		changes=new SnapshotChanges(2,3);
		changes.markLineChanged(3);
		ITerminalTextData source=new TerminalTextDataStore();
		TerminalTextTestHelper.fillSimple(source, "01234567890");
		ITerminalTextData dest=new TerminalTextDataStore();
		TerminalTextTestHelper.fillSimple(dest, "abcdefghijk");
		
		changes.copyChangedLines(dest, source);
		assertEquals("abc3efghijk",TerminalTextTestHelper.toSimple(dest));
		
		changes=new SnapshotChanges(2,3);
		changes.setAllChanged(7);
		source=new TerminalTextDataStore();
		TerminalTextTestHelper.fillSimple(source, "01234567890");
		dest=new TerminalTextDataStore();
		TerminalTextTestHelper.fillSimple(dest, "abcdefghijk");
		
		changes.copyChangedLines(dest, source);
		assertEquals("ab234fghijk",TerminalTextTestHelper.toSimple(dest));

		changes=new SnapshotChanges(2,3);
		changes.scroll(0,7,-1);
		source=new TerminalTextDataStore();
		TerminalTextTestHelper.fillSimple(source, "01234567890");
		dest=new TerminalTextDataStore();
		TerminalTextTestHelper.fillSimple(dest, "abcdefghijk");
		// only one line has changed! The other lines are scrolled!
		assertChangedLines(changes,"00001000");
		changes.copyChangedLines(dest, source);
		assertEquals("abcd4fghijk",TerminalTextTestHelper.toSimple(dest));
	}
	public void testCopyChangedLinesWithSmallSource() {
		SnapshotChanges changes;
		changes=new SnapshotChanges(2,3);
		changes.markLineChanged(3);
		ITerminalTextData source=new TerminalTextDataStore();
		source.setDimensions(2, 2);
		TerminalTextDataWindow dest=new TerminalTextDataWindow();
		dest.setWindow(2, 2);
		changes.copyChangedLines(dest, source);
	}
	public void testCopyChangedLinesWithSmallSource1() {
		SnapshotChanges changes;
		changes=new SnapshotChanges(2,3);
		changes.markLineChanged(3);
		ITerminalTextData source=new TerminalTextDataStore();
		TerminalTextTestHelper.fillSimple(source, "01");
		ITerminalTextData dest=new TerminalTextDataStore();
		changes.copyChangedLines(dest, source);
	}

	public void testSetInterestWindowSize() {
		SnapshotChanges changes;
		changes=new SnapshotChanges(2,3);
		// move the window
		changes.setInterestWindow(3, 3);
		// only one line has changed! The other lines are scrolled!
		assertEquals(3, changes.getScrollWindowStartLine());
		assertEquals(3, changes.getScrollWindowSize());
		assertEquals(-1, changes.getScrollWindowShift());
		
		assertChangedLines(changes,"0000010");
		changes.convertScrollingIntoChanges();
		assertChangedLines(changes,"0001110");

		changes=new SnapshotChanges(2,3);
		// move the window
		changes.setInterestWindow(3, 4);
		// only one line has changed! The other lines are scrolled!
		assertEquals(3, changes.getScrollWindowStartLine());
		assertEquals(3, changes.getScrollWindowSize());
		assertEquals(-1, changes.getScrollWindowShift());
		
		assertChangedLines(changes,"0000011");
		changes.convertScrollingIntoChanges();
		assertChangedLines(changes,"0001111");

	
		changes=new SnapshotChanges(2,3);
		// move the window
		changes.setInterestWindow(6, 3);
		// cannot scroll
		assertEquals(0, changes.getScrollWindowStartLine());
		assertEquals(0, changes.getScrollWindowSize());
		assertEquals(0, changes.getScrollWindowShift());
		assertChangedLines(changes,"000000111000");

		changes=new SnapshotChanges(2,3);
		// expand the window
		changes.setInterestWindow(2, 5);
		// cannot scroll
		assertEquals(0, changes.getScrollWindowStartLine());
		assertEquals(0, changes.getScrollWindowSize());
		assertEquals(0, changes.getScrollWindowShift());
		
		assertChangedLines(changes,"0000011000");
	}
	public void testSetInterestWindowSize2() {
		SnapshotChanges changes;
		changes=new SnapshotChanges(2,3);
		// move the window
		changes.setInterestWindow(1, 3);
		assertChangedLines(changes,"0111000");

		changes=new SnapshotChanges(2,3);
		// move the window
		changes.setInterestWindow(1, 4);
		assertChangedLines(changes,"01111000");

	
		changes=new SnapshotChanges(2,3);
		// expand the window
		changes.setInterestWindow(6, 3);
		assertChangedLines(changes,"000000111000");

		changes=new SnapshotChanges(2,3);
		// expand the window
		changes.setInterestWindow(1, 2);
		assertChangedLines(changes,"0110000");
	}

}
