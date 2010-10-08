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

import junit.framework.TestCase;

import org.eclipse.tm.terminal.model.ITerminalTextData;
import org.eclipse.tm.terminal.model.ITerminalTextDataReadOnly;
import org.eclipse.tm.terminal.model.ITerminalTextDataSnapshot;
import org.eclipse.tm.terminal.model.Style;
import org.eclipse.tm.terminal.model.StyleColor;

public class TerminalTextDataSnapshotTest extends TestCase {
	String toMultiLineText(ITerminalTextDataReadOnly term) {
		return TerminalTextTestHelper.toMultiLineText(term);
	}

	protected ITerminalTextData makeITerminalTextData() {
		return new TerminalTextData();
	}


	public void testTerminalTextDataSnapshot() {
		ITerminalTextData term=makeITerminalTextData();
		String s="12345\n" +
				 "abcde\n" +
				 "ABCDE\n" +
				 "vwxzy\n" +
				 "VWXYZ";
		TerminalTextTestHelper.fill(term,s);
		
		ITerminalTextDataSnapshot snapshot=term.makeSnapshot();
		snapshot.updateSnapshot(false);

		assertEquals(toMultiLineText(term), toMultiLineText(snapshot));
		
		// new snapshots are fully changed
		assertEquals(0,snapshot.getFirstChangedLine());
		assertEquals(term.getHeight()-1,snapshot.getLastChangedLine());
		for (int line = 0; line <= snapshot.getLastChangedLine(); line++) {
			assertTrue(snapshot.hasLineChanged(line));
		}
		// nothing has scrolled
		assertEquals(0, snapshot.getScrollWindowSize());
	}

	public void testDetach() {
		ITerminalTextData term=makeITerminalTextData();
		String s="12345\n" +
				 "abcde\n" +
				 "ABCDE\n" +
				 "vwxzy\n" +
				 "VWXYZ";
		TerminalTextTestHelper.fill(term,s);
		
		ITerminalTextDataSnapshot snapshot=term.makeSnapshot();
		snapshot.updateSnapshot(false);

		assertEquals(toMultiLineText(term),toMultiLineText(snapshot));
		snapshot.detach();
		// after detach changes to the term has no effect
		term.setChar(0, 0, '?', null);
		assertEquals(s, toMultiLineText(snapshot));
		term.setDimensions(2, 2);
		assertEquals(s, toMultiLineText(snapshot));
	}
	public void testIsOutOfDate() {
		ITerminalTextData term=makeITerminalTextData();
		String s="12345\n" +
				 "abcde\n" +
				 "ABCDE\n" +
				 "vwxzy\n" +
				 "VWXYZ";
		TerminalTextTestHelper.fill(term,s);
		
		ITerminalTextDataSnapshot snapshot=term.makeSnapshot();
		assertTrue(snapshot.isOutOfDate());
		snapshot.updateSnapshot(false);

		assertFalse(snapshot.isOutOfDate());
		
		// make a change and expect it to be changed
		term.setChar(0, 0, '?', null);
		assertTrue(snapshot.isOutOfDate());
		
		snapshot.updateSnapshot(false);
		assertFalse(snapshot.isOutOfDate());
		
		// make a change and expect it to be changed
		term.setChars(1, 1, new char[]{'?','!','.'},null);
		assertTrue(snapshot.isOutOfDate());
		
		snapshot.updateSnapshot(false);
		assertFalse(snapshot.isOutOfDate());
		
		// scroll
		term.scroll(1, 2, -1);
		assertTrue(snapshot.isOutOfDate());

		snapshot.updateSnapshot(false);
		assertFalse(snapshot.isOutOfDate());
		
		// scroll
		term.scroll(1, 2, 1);
		assertTrue(snapshot.isOutOfDate());

		snapshot.updateSnapshot(false);
		assertFalse(snapshot.isOutOfDate());

		// scroll
		term.scroll(1, 2, -1);
		assertTrue(snapshot.isOutOfDate());

		snapshot.updateSnapshot(true);
		assertFalse(snapshot.isOutOfDate());
		
		// scroll
		term.scroll(1, 2, 1);
		assertTrue(snapshot.isOutOfDate());

		snapshot.updateSnapshot(true);
		assertFalse(snapshot.isOutOfDate());
		
		// setDimensions
		term.setDimensions(2, 2);
		assertTrue(snapshot.isOutOfDate());

		snapshot.updateSnapshot(false);
		assertFalse(snapshot.isOutOfDate());
		
		// setDimensions
		term.setDimensions(20, 20);
		assertTrue(snapshot.isOutOfDate());

		snapshot.updateSnapshot(false);
		assertFalse(snapshot.isOutOfDate());
		
	}
	ITerminalTextDataSnapshot snapshot(String text, ITerminalTextData term) {
		TerminalTextTestHelper.fill(term,text);
		ITerminalTextDataSnapshot snapshot=term.makeSnapshot();
		snapshot.updateSnapshot(false);

		return snapshot;
		
	}
	public void testUpdateSnapshot() {
		ITerminalTextData term=makeITerminalTextData();
		String s="12345\n" +
				 "abcde\n" +
				 "ABCDE\n" +
				 "vwxzy\n" +
				 "VWXYZ";
		TerminalTextTestHelper.fill(term,s);
		String termString=toMultiLineText(term);
		ITerminalTextDataSnapshot snapshot=term.makeSnapshot();
		snapshot.updateSnapshot(false);

		assertEquals(termString,toMultiLineText(snapshot));
		
		// make changes and assert that the snapshot has not changed
		// then update the snapshot and expect it to be the
		// same as the changed terminal
		
		// make a change 
		term.setChar(0, 0, '?', null);
		assertEquals(termString,toMultiLineText(snapshot));
		
		snapshot.updateSnapshot(false);
		termString=toMultiLineText(term);
		assertEquals(termString,toMultiLineText(snapshot));
		
		// make a change 
		term.setChars(1, 1, new char[]{'?','!','.'},null);
		assertEquals(termString,toMultiLineText(snapshot));
		
		snapshot.updateSnapshot(false);
		termString=toMultiLineText(term);
		assertEquals(termString,toMultiLineText(snapshot));
		
		// scroll
		term.scroll(1, 2, -1);
		assertEquals(termString,toMultiLineText(snapshot));

		snapshot.updateSnapshot(false);
		termString=toMultiLineText(term);
		assertEquals(termString,toMultiLineText(snapshot));
		
		// scroll
		term.scroll(1, 2, 1);
		assertEquals(termString,toMultiLineText(snapshot));

		snapshot.updateSnapshot(false);
		termString=toMultiLineText(term);
		assertEquals(termString,toMultiLineText(snapshot));
		
		// scroll
		term.scroll(1, 2, -1);
		assertEquals(termString,toMultiLineText(snapshot));

		snapshot.updateSnapshot(true);
		termString=toMultiLineText(term);
		assertEquals(termString,toMultiLineText(snapshot));
		
		// scroll
		term.scroll(1, 2, 1);
		assertEquals(termString,toMultiLineText(snapshot));

		snapshot.updateSnapshot(true);
		termString=toMultiLineText(term);
		assertEquals(termString,toMultiLineText(snapshot));
		
		// set dimensions
		term.setDimensions(2, 2);
		assertEquals(termString,toMultiLineText(snapshot));

		snapshot.updateSnapshot(false);
		termString=toMultiLineText(term);
		assertEquals(termString,toMultiLineText(snapshot));

		// set dimensions
		term.setDimensions(20, 20);
		assertEquals(termString,toMultiLineText(snapshot));

		snapshot.updateSnapshot(false);
		termString=toMultiLineText(term);
		assertEquals(termString,toMultiLineText(snapshot));
	}

	public void testMaxSize() {
		String s=
			"111\n" +
			"222\n" +
			"333\n" +
			"444\n" +
			"555";
		ITerminalTextData term=makeITerminalTextData();
		term.setMaxHeight(8);
		TerminalTextTestHelper.fill(term, s);
		ITerminalTextDataSnapshot snapshot=term.makeSnapshot();
		snapshot.updateSnapshot(false);

		term.addLine();
		assertTrue(snapshot.isOutOfDate());
		snapshot.updateSnapshot(false);
		assertEquals(toMultiLineText(term), toMultiLineText(snapshot));

		term.addLine();
		assertTrue(snapshot.isOutOfDate());
		snapshot.updateSnapshot(false);
		assertEquals(toMultiLineText(term), toMultiLineText(snapshot));

		term.addLine();
		assertTrue(snapshot.isOutOfDate());
		snapshot.updateSnapshot(false);
		assertEquals(toMultiLineText(term), toMultiLineText(snapshot));

		term.addLine();
		assertTrue(snapshot.isOutOfDate());
		snapshot.updateSnapshot(false);
		assertEquals(toMultiLineText(term), toMultiLineText(snapshot));

		term.addLine();
		assertTrue(snapshot.isOutOfDate());
		snapshot.updateSnapshot(false);
		assertEquals(toMultiLineText(term), toMultiLineText(snapshot));

	}

	
	public void testGetChar() {
		ITerminalTextData term=makeITerminalTextData();
		String s="12345\n" +
				 "abcde\n" +
				 "ABCDE\n" +
				 "vwxzy\n" +
				 "VWXYZ";
		TerminalTextTestHelper.fill(term,s);
		ITerminalTextData termUnchanged=makeITerminalTextData();
		TerminalTextTestHelper.fill(termUnchanged,s);
		
		ITerminalTextDataSnapshot snapshot=term.makeSnapshot();
		snapshot.updateSnapshot(false);

		for (int line = 0; line < snapshot.getHeight(); line++) {
			for (int column = 0; column < snapshot.getWidth(); column++) {
				assertEquals(term.getChar(line, column),snapshot.getChar(line, column));
			}
		}
		// make a change 
		term.setChar(0, 0, '?', null);
		// check against unchanged data
		for (int line = 0; line < snapshot.getHeight(); line++) {
			for (int column = 0; column < snapshot.getWidth(); column++) {
				assertEquals(termUnchanged.getChar(line, column),snapshot.getChar(line, column));
			}
		}
		// update and compare against the terminal
		snapshot.updateSnapshot(true);
		for (int line = 0; line < snapshot.getHeight(); line++) {
			for (int column = 0; column < snapshot.getWidth(); column++) {
				assertEquals(term.getChar(line, column),snapshot.getChar(line, column));
			}
		}
		
	}

	public void testGetHeight() {
		ITerminalTextData term=makeITerminalTextData();
		String s="12345\n" +
				 "abcde\n" +
				 "ABCDE\n" +
				 "vwxzy\n" +
				 "VWXYZ";
		TerminalTextTestHelper.fill(term,s);
		
		ITerminalTextDataSnapshot snapshot=term.makeSnapshot();
		snapshot.updateSnapshot(false);

		int expectedHeight=term.getHeight();
		assertEquals(expectedHeight, snapshot.getHeight());
		term.setDimensions(term.getHeight()-1, term.getWidth());
		assertEquals(expectedHeight, snapshot.getHeight());
		
		//
		snapshot.updateSnapshot(false);
		expectedHeight=term.getHeight();
		assertEquals(expectedHeight, snapshot.getHeight());
		term.setDimensions(term.getHeight()-1, term.getWidth());
		assertEquals(expectedHeight, snapshot.getHeight());
	}
//
//	public void testGetLineSegments() {
//		fail("Not yet implemented");
//	}
//
	public void testGetStyle() {
		ITerminalTextData term=makeITerminalTextData();
		Style style=Style.getStyle(StyleColor.getStyleColor("fg"), StyleColor.getStyleColor("bg"), false, false, false, false);
		term.setDimensions(6, 3);
		for (int line = 0; line < term.getHeight(); line++) {
			for (int column = 0; column < term.getWidth(); column++) {
				char c=(char)('a'+column+line);
				term.setChar(line, column, c, style.setForground(StyleColor.getStyleColor(""+c)));
			}
		}
		ITerminalTextDataSnapshot snapshot=term.makeSnapshot();
		snapshot.updateSnapshot(false);

		
		for (int line = 0; line < term.getHeight(); line++) {
			for (int column = 0; column < term.getWidth(); column++) {
				char c=(char)('a'+column+line);
				assertSame(style.setForground(StyleColor.getStyleColor(""+c)), snapshot.getStyle(line, column));
			}
		}
		
	}

	public void testGetWidth() {
		ITerminalTextData term=makeITerminalTextData();
		String s="12345\n" +
				 "abcde\n" +
				 "ABCDE\n" +
				 "vwxzy\n" +
				 "VWXYZ";
		TerminalTextTestHelper.fill(term,s);
		
		ITerminalTextDataSnapshot snapshot=term.makeSnapshot();
		snapshot.updateSnapshot(false);

		int expectedWidth=term.getWidth();
		assertEquals(expectedWidth, snapshot.getWidth());
		term.setDimensions(term.getHeight(), term.getWidth()-1);
		assertEquals(expectedWidth, snapshot.getWidth());
		
		//
		snapshot.updateSnapshot(false);
		expectedWidth=term.getWidth();
		assertEquals(expectedWidth, snapshot.getWidth());
		term.setDimensions(term.getHeight(), term.getWidth()-1);
		assertEquals(expectedWidth, snapshot.getWidth());
	}

	public void testGetFirstChangedLine() {
		ITerminalTextData term=makeITerminalTextData();
		String s="12345\n" +
				 "abcde\n" +
				 "ABCDE\n" +
				 "vwxzy\n" +
				 "VWXYZ";
		ITerminalTextDataSnapshot snapshot=snapshot(s,term);

		
		assertEquals(0, snapshot.getFirstChangedLine());
		
		// if nothing has changed the first changed line i height
		snapshot.updateSnapshot(false);
		assertEquals(Integer.MAX_VALUE, snapshot.getFirstChangedLine());
		
		snapshot=snapshot(s,term);
		term.setChar(0, 0, 'x', null);
		snapshot.updateSnapshot(false);
		assertEquals(0, snapshot.getFirstChangedLine());
		
		snapshot=snapshot(s,term);		
		term.setChar(3, 0, 'x', null);
		term.setChar(4, 0, 'x', null);
		snapshot.updateSnapshot(false);
		assertEquals(3, snapshot.getFirstChangedLine());
		
		snapshot=snapshot(s,term);
		term.scroll(0, 1, -1);
		snapshot.updateSnapshot(false);
		assertEquals(0, snapshot.getFirstChangedLine());
		
		snapshot=snapshot(s,term);
		term.scroll(2, 2, -1);
		snapshot.updateSnapshot(false);
		assertEquals(2, snapshot.getFirstChangedLine());
		
		// when scrolling the end of the region 'has changed'
		snapshot=snapshot(s,term);
		term.scroll(2, 2, -1);
		snapshot.updateSnapshot(true);
		assertEquals(3, snapshot.getFirstChangedLine());
		
		// when scrolling the end of the region 'has changed'
		snapshot=snapshot(s,term);
		term.scroll(2, 2, -1);
		term.setChar(1, 0, 'x', null);
		snapshot.updateSnapshot(true);
		assertEquals(1, snapshot.getFirstChangedLine());
		
	}
	public void testGetLastChangedLine() {
		ITerminalTextData term=makeITerminalTextData();
		String s="12345\n" +
				 "abcde\n" +
				 "ABCDE\n" +
				 "vwxzy\n" +
				 "VWXYZ";
		ITerminalTextDataSnapshot snapshot=snapshot(s,term);

		
		assertEquals(4, snapshot.getLastChangedLine());
		
		// if nothing has changed the first changed line i height
		snapshot.updateSnapshot(false);
		assertEquals(-1, snapshot.getLastChangedLine());
		
		snapshot=snapshot(s,term);
		term.setChar(0, 0, 'x', null);
		snapshot.updateSnapshot(false);
		assertEquals(0, snapshot.getLastChangedLine());
		
		snapshot=snapshot(s,term);
		term.cleanLine(1);
		snapshot.updateSnapshot(false);
		assertEquals(1, snapshot.getLastChangedLine());

		snapshot=snapshot(s,term);		
		term.setChar(3, 0, 'x', null);
		term.setChar(4, 0, 'x', null);
		snapshot.updateSnapshot(false);
		assertEquals(4, snapshot.getLastChangedLine());
		
		snapshot=snapshot(s,term);
		term.scroll(0, 1, -1);
		snapshot.updateSnapshot(false);
		assertEquals(0, snapshot.getLastChangedLine());
		
		snapshot=snapshot(s,term);
		term.scroll(2, 2, -1);
		snapshot.updateSnapshot(false);
		assertEquals(3, snapshot.getLastChangedLine());
		
		// when scrolling the end of the region 'has changed'
		snapshot=snapshot(s,term);
		term.scroll(2, 2, -1);
		snapshot.updateSnapshot(true);
		assertEquals(3, snapshot.getLastChangedLine());
		
		// when scrolling the end of the region 'has changed'
		snapshot=snapshot(s,term);
		term.scroll(2, 2, -1);
		term.setChar(1, 0, 'x', null);
		snapshot.updateSnapshot(true);
		assertEquals(3, snapshot.getLastChangedLine());
		
	}
	/**
	 * @param snapshot
	 * @param expected a string of 0 and 1 (1 means changed)
	 */
	void assertChangedLines(ITerminalTextDataSnapshot snapshot, String expected) {
		assertEquals(expected.length(),snapshot.getHeight());
		StringBuffer buffer=new StringBuffer();
		for (int line = 0; line < expected.length(); line++) {
			if(snapshot.hasLineChanged(line))
				buffer.append('1');
			else
				buffer.append('0');
		}
		assertEquals(expected, buffer.toString());
	}
	public void testHasLineChangedScroll() {
		ITerminalTextData term=makeITerminalTextData();
		String s="00\n" +
				 "11\n" +
				 "22\n" +
				 "33\n" +
				 "44\n" +
				 "55\n" +
				 "66\n" +
				 "77\n" +
				 "88\n" +
				 "99";
		ITerminalTextDataSnapshot snapshot=snapshot(s,term);
		
		term.scroll(2,3,-1);
		snapshot.updateSnapshot(true);
		assertChangedLines(snapshot, "0000100000");
		
		snapshot=snapshot(s,term);
		term.scroll(2,3,-2);
		snapshot.updateSnapshot(true);
		assertChangedLines(snapshot, "0001100000");

		snapshot=snapshot(s,term);
		term.scroll(2,4,-1);
		term.scroll(2,4,-1);
		snapshot.updateSnapshot(true);
		assertChangedLines(snapshot, "0000110000");

		term.scroll(2,3,1);
		snapshot.updateSnapshot(true);
		assertChangedLines(snapshot, "0011100000");
		
		snapshot=snapshot(s,term);
		term.scroll(2,3,2);
		snapshot.updateSnapshot(true);
		assertChangedLines(snapshot, "0011100000");

		snapshot=snapshot(s,term);
		term.scroll(2,4,1);
		term.scroll(2,4,1);
		snapshot.updateSnapshot(true);
		assertChangedLines(snapshot, "0011110000");

		
		snapshot=snapshot(s,term);
		term.scroll(2,3,-1);
		snapshot.updateSnapshot(false);
		assertChangedLines(snapshot, "0011100000");
		
		snapshot=snapshot(s,term);
		term.scroll(2,3,-2);
		snapshot.updateSnapshot(false);
		assertChangedLines(snapshot, "0011100000");

		snapshot=snapshot(s,term);
		term.scroll(2,4,-1);
		term.scroll(2,4,-1);
		snapshot.updateSnapshot(false);
		assertChangedLines(snapshot, "0011110000");
	}
	public void testMultiScrollWithDifferentSizes() {
		ITerminalTextData term=makeITerminalTextData();
		String s="00\n" +
				 "11\n" +
				 "22\n" +
				 "33\n" +
				 "44\n" +
				 "55\n" +
				 "66\n" +
				 "77\n" +
				 "88\n" +
				 "99";
		ITerminalTextDataSnapshot snapshot;

		snapshot=snapshot(s,term);
		term.scroll(2,6,-1);
		term.scroll(2,5,-1);
		snapshot.updateSnapshot(false);
		assertChangedLines(snapshot, "0011111100");
		assertEquals(2, snapshot.getFirstChangedLine());
		assertEquals(7, snapshot.getLastChangedLine());
		assertEquals(0, snapshot.getScrollWindowSize());
		assertEquals(0, snapshot.getScrollWindowStartLine());
		assertEquals(0, snapshot.getScrollWindowShift());
		
		// scrolls with different ranges cause no scroll
		// optimization
		snapshot=snapshot(s,term);
		term.scroll(2,6,-1);
		term.scroll(2,5,-1);
		snapshot.updateSnapshot(true);
		assertChangedLines(snapshot, "0011111100");
		assertEquals(2, snapshot.getFirstChangedLine());
		assertEquals(7, snapshot.getLastChangedLine());
		assertEquals(0, snapshot.getScrollWindowShift());
		assertEquals(0, snapshot.getScrollWindowSize());
		assertEquals(0, snapshot.getScrollWindowStartLine());
	}
	public void testHasLineChanged() {
		ITerminalTextData term=makeITerminalTextData();
		String s="000000\n" +
				"111111\n" +
				"222222\n" +
				"333333\n" +
				"444444\n" +
				"555555\n" +
				"666666\n" +
				"777777\n" +
				"888888\n" +
				"999999";
		ITerminalTextDataSnapshot snapshot;
		
		snapshot=snapshot(s,term);
		term.scroll(2,3,-1);
		term.setChar(7, 0, '.', null);
		snapshot.updateSnapshot(true);
		assertChangedLines(snapshot, "0000100100");
		
		snapshot=snapshot(s,term);
		term.scroll(2,3,-2);
		term.setChar(9, 0, '.', null);
		term.setChars(0, 0, new char[]{'.','!'}, null);
		snapshot.updateSnapshot(true);
		assertChangedLines(snapshot, "1001100001");

		snapshot=snapshot(s,term);
		term.scroll(2,4,-1);
		term.scroll(2,4,-1);
		term.setChars(2, 2, new char[]{'.','!','*'},1,1, null);
		snapshot.updateSnapshot(true);
		assertChangedLines(snapshot, "0010110000");

		snapshot=snapshot(s,term);
		term.scroll(2,7,-1);
		term.setChar(5, 2, '.', null);
		term.scroll(2,7,-2);
		snapshot.updateSnapshot(true);
		assertChangedLines(snapshot, "0001001110");

		
		snapshot=snapshot(s,term);
		term.scroll(2,7,-1);
		term.setChar(5, 2, '.', null);
		term.scroll(2,7,-2);
		snapshot.updateSnapshot(false);
		assertChangedLines(snapshot, "0011111110");

	}

	public void testScroll() {
		ITerminalTextData term=makeITerminalTextData();
		String s="00\n" +
				 "11\n" +
				 "22\n" +
				 "33\n" +
				 "44\n" +
				 "55\n" +
				 "66\n" +
				 "77\n" +
				 "88\n" +
				 "99";
		ITerminalTextDataSnapshot snapshot=snapshot(s,term);
		
		term.scroll(2,3,-1);
		snapshot.updateSnapshot(true);
		assertEquals(2, snapshot.getScrollWindowStartLine());
		assertEquals(3, snapshot.getScrollWindowSize());
		assertEquals(-1, snapshot.getScrollWindowShift());
		assertEquals(4, snapshot.getFirstChangedLine());
		assertEquals(4, snapshot.getLastChangedLine());
		
		term.scroll(2,3,-2);
		snapshot.updateSnapshot(true);
		assertEquals(2, snapshot.getScrollWindowStartLine());
		assertEquals(3, snapshot.getScrollWindowSize());
		assertEquals(-2, snapshot.getScrollWindowShift());
		assertEquals(3, snapshot.getFirstChangedLine());
		assertEquals(4, snapshot.getLastChangedLine());

		term.scroll(2,4,-1);
		term.scroll(2,4,-1);
		snapshot.updateSnapshot(true);
		assertEquals(2, snapshot.getScrollWindowStartLine());
		assertEquals(4, snapshot.getScrollWindowSize());
		assertEquals(-2, snapshot.getScrollWindowShift());
		assertEquals(4, snapshot.getFirstChangedLine());
		assertEquals(5, snapshot.getLastChangedLine());

		
		snapshot=snapshot(s,term);
		term.scroll(2,3,-1);
		snapshot.updateSnapshot(false);
		assertEquals(0, snapshot.getScrollWindowStartLine());
		assertEquals(0, snapshot.getScrollWindowSize());
		assertEquals(0, snapshot.getScrollWindowShift());
		assertEquals(2, snapshot.getFirstChangedLine());
		assertEquals(4, snapshot.getLastChangedLine());
		
	}
	public void testDisjointScroll() {
		ITerminalTextData term=makeITerminalTextData();
		String s="000000\n" +
				"111111\n" +
				"222222\n" +
				"333333\n" +
				"444444\n" +
				"555555\n" +
				"666666\n" +
				"777777\n" +
				"888888\n" +
				"999999";
		ITerminalTextDataSnapshot snapshot;
		
		snapshot=snapshot(s,term);
		term.scroll(0,2,-1);
		term.scroll(4,2,-1);
		snapshot.updateSnapshot(true);
		assertChangedLines(snapshot, "1100110000");
		assertEquals(0, snapshot.getScrollWindowStartLine());
		assertEquals(0, snapshot.getScrollWindowSize());
		assertEquals(0, snapshot.getScrollWindowShift());

		snapshot=snapshot(s,term);
		term.scroll(0,3,-1);
		term.scroll(2,2,-2);
		snapshot.updateSnapshot(true);
		assertChangedLines(snapshot, "1111000000");
		assertEquals(0, snapshot.getScrollWindowStartLine());
		assertEquals(0, snapshot.getScrollWindowSize());
		assertEquals(0, snapshot.getScrollWindowShift());

		snapshot=snapshot(s,term);
		term.scroll(0,3,-1);
		term.scroll(2,2,-2);
		term.scroll(0,3,-1);
		snapshot.updateSnapshot(true);
		assertChangedLines(snapshot, "1111000000");
		assertEquals(0, snapshot.getScrollWindowStartLine());
		assertEquals(0, snapshot.getScrollWindowSize());
		assertEquals(0, snapshot.getScrollWindowShift());

		snapshot=snapshot(s,term);
		term.scroll(0,3,-1);
		term.scroll(2,2,-2);
		term.scroll(0,3,-10);
		snapshot.updateSnapshot(true);
		assertChangedLines(snapshot, "1111000000");
		assertEquals(0, snapshot.getScrollWindowStartLine());
		assertEquals(0, snapshot.getScrollWindowSize());
		assertEquals(0, snapshot.getScrollWindowShift());

		snapshot=snapshot(s,term);
		term.scroll(1,3,-1);
		term.scroll(1,3,1);
		snapshot.updateSnapshot(true);
		assertChangedLines(snapshot, "0111000000");
		assertEquals(0, snapshot.getScrollWindowStartLine());
		assertEquals(0, snapshot.getScrollWindowSize());
		assertEquals(0, snapshot.getScrollWindowShift());
	}
	public void testResize() {
		ITerminalTextData term=makeITerminalTextData();
		String s="000000\n" +
				"111111\n" +
				"222222\n" +
				"333333";
		ITerminalTextDataSnapshot snapshot;
		
		snapshot=snapshot(s,term);
		term.setDimensions(term.getHeight(), term.getWidth()+1);
		snapshot.updateSnapshot(true);
		assertChangedLines(snapshot, "1111");
		assertEquals(0, snapshot.getFirstChangedLine());
		assertEquals(3, snapshot.getLastChangedLine());
		assertEquals(0, snapshot.getScrollWindowStartLine());
		assertEquals(0, snapshot.getScrollWindowSize());
		assertEquals(0, snapshot.getScrollWindowShift());

		snapshot=snapshot(s,term);
		term.setDimensions(term.getHeight()+1, term.getWidth());
		snapshot.updateSnapshot(true);
		assertChangedLines(snapshot, "11111");
		assertEquals(0, snapshot.getFirstChangedLine());
		assertEquals(4, snapshot.getLastChangedLine());
		assertEquals(0, snapshot.getScrollWindowStartLine());
		assertEquals(0, snapshot.getScrollWindowSize());
		assertEquals(0, snapshot.getScrollWindowShift());
	
		snapshot=snapshot(s,term);
		term.setDimensions(term.getHeight()-1, term.getWidth());
		snapshot.updateSnapshot(true);
		assertChangedLines(snapshot, "111");
		assertEquals(0, snapshot.getFirstChangedLine());
		assertEquals(2, snapshot.getLastChangedLine());
		assertEquals(0, snapshot.getScrollWindowStartLine());
		assertEquals(0, snapshot.getScrollWindowSize());
		assertEquals(0, snapshot.getScrollWindowShift());
	
		snapshot=snapshot(s,term);
		term.setDimensions(0, 0);
		snapshot.updateSnapshot(true);
		assertChangedLines(snapshot, "");
		assertEquals(0, snapshot.getFirstChangedLine());
		assertEquals(-1, snapshot.getLastChangedLine());
		assertEquals(0, snapshot.getScrollWindowStartLine());
		assertEquals(0, snapshot.getScrollWindowSize());
		assertEquals(0, snapshot.getScrollWindowShift());
	
	}
	public void testResizeAfterScroll() {
		ITerminalTextData term=makeITerminalTextData();
		String s="000000\n" +
				"111111\n" +
				"222222\n" +
				"333333\n" +
				"444444\n" +
				"555555\n" +
				"666666\n" +
				"777777\n" +
				"888888\n" +
				"999999";
		ITerminalTextDataSnapshot snapshot;
		
		snapshot=snapshot(s,term);
		term.scroll(1,2,-1);
		term.setDimensions(5, 4);
		snapshot.updateSnapshot(true);
		assertChangedLines(snapshot, "11111");
		assertEquals(0, snapshot.getScrollWindowStartLine());
		assertEquals(0, snapshot.getScrollWindowSize());
		assertEquals(0, snapshot.getScrollWindowShift());

		snapshot=snapshot(s,term);
		term.scroll(1,2,-1);
		term.setDimensions(7, 2);
		term.scroll(4,2,-1);
		snapshot.updateSnapshot(true);
		assertChangedLines(snapshot, "1111111");
		assertEquals(0, snapshot.getScrollWindowStartLine());
		assertEquals(0, snapshot.getScrollWindowSize());
		assertEquals(0, snapshot.getScrollWindowShift());
		snapshot=snapshot(s,term);

		term.scroll(1,2,-1);
		term.setDimensions(term.getHeight(),term.getWidth()+1);
		snapshot.updateSnapshot(true);
		assertChangedLines(snapshot, "1111111111");
		assertEquals(0, snapshot.getScrollWindowStartLine());
		assertEquals(0, snapshot.getScrollWindowSize());
		assertEquals(0, snapshot.getScrollWindowShift());
	}
	public void testScrollAfterResize() {
		ITerminalTextData term=makeITerminalTextData();
		String s="000000\n" +
				"111111\n" +
				"222222\n" +
				"333333\n" +
				"444444\n" +
				"555555\n" +
				"666666\n" +
				"777777\n" +
				"888888\n" +
				"999999";
		ITerminalTextDataSnapshot snapshot;
		
		snapshot=snapshot(s,term);
		term.setDimensions(14, 6);
		term.scroll(0,14,-1);
		snapshot.updateSnapshot(true);
		assertChangedLines(snapshot, "11111111111111");
		assertEquals(0, snapshot.getScrollWindowStartLine());
		assertEquals(0, snapshot.getScrollWindowSize());
		assertEquals(0, snapshot.getScrollWindowShift());
	}
	private final class SnapshotListener implements ITerminalTextDataSnapshot.SnapshotOutOfDateListener {
		int N;
		public void snapshotOutOfDate(ITerminalTextDataSnapshot snapshot) {
			N++;
		}
		public void reset() {
			N=0;
		}
	}

	public void testAddListener() {
		ITerminalTextData term=makeITerminalTextData();
		String s="12345\n" +
				 "abcde\n" +
				 "ABCDE\n" +
				 "vwxzy\n" +
				 "VWXYZ";
		TerminalTextTestHelper.fill(term,s);
		
		ITerminalTextDataSnapshot snapshot=term.makeSnapshot();
		snapshot.updateSnapshot(false);

		SnapshotListener listener=new SnapshotListener();
		snapshot.addListener(listener);
		assertEquals(0, listener.N);
		
		// make a change and expect it to be changed
		term.setChar(0, 0, '?', null);
		assertEquals(1, listener.N);
		term.setChar(1, 1, '?', null);
		assertEquals(1, listener.N);
		
		snapshot.updateSnapshot(false);
		assertEquals(1, listener.N);
		listener.reset();

		// make a change and expect it to be changed
		term.setChars(1, 1, new char[]{'?','!','.'},null);
		assertEquals(1, listener.N);
		term.setChars(2, 1, new char[]{'?','!','.'},null);
		assertEquals(1, listener.N);
		
		snapshot.updateSnapshot(false);
		assertEquals(1, listener.N);
		listener.reset();
		
		// scroll
		term.scroll(1, 2, -1);
		assertEquals(1, listener.N);
		term.scroll(1, 2, -1);
		assertEquals(1, listener.N);

		snapshot.updateSnapshot(false);
		assertEquals(1, listener.N);
		listener.reset();
		
		// scroll
		term.scroll(1, 2, 1);
		assertEquals(1, listener.N);
		term.scroll(1, 2, 1);
		assertEquals(1, listener.N);

		snapshot.updateSnapshot(false);
		assertEquals(1, listener.N);
		listener.reset();

		// scroll
		term.scroll(1, 2, -1);
		assertEquals(1, listener.N);
		term.scroll(1, 2, -1);
		assertEquals(1, listener.N);

		snapshot.updateSnapshot(false);
		assertEquals(1, listener.N);
		listener.reset();
		
		// scroll
		term.scroll(1, 2, 1);
		assertEquals(1, listener.N);

		snapshot.updateSnapshot(false);
		assertEquals(1, listener.N);
		listener.reset();
		
		// setDimensions
		term.setDimensions(2, 2);
		assertEquals(1, listener.N);

		snapshot.updateSnapshot(false);
		assertEquals(1, listener.N);
		listener.reset();
		
		// setDimensions
		term.setDimensions(20, 20);
		assertEquals(1, listener.N);

		snapshot.updateSnapshot(false);
		assertFalse(snapshot.isOutOfDate());
	}

	public void testRemoveListener() {
		ITerminalTextData term=makeITerminalTextData();
		String s="12345\n" +
				 "abcde\n" +
				 "ABCDE\n" +
				 "vwxzy\n" +
				 "VWXYZ";
		TerminalTextTestHelper.fill(term,s);
		
		ITerminalTextDataSnapshot snapshot=term.makeSnapshot();
		snapshot.updateSnapshot(false);

		SnapshotListener listener1=new SnapshotListener();
		SnapshotListener listener2=new SnapshotListener();
		SnapshotListener listener3=new SnapshotListener();
		snapshot.addListener(listener1);
		snapshot.addListener(listener2);
		snapshot.addListener(listener3);
		assertEquals(0, listener1.N);
		
		// make a change and expect it to be changed
		term.setChar(0, 0, '?', null);
		assertEquals(1, listener1.N);
		assertEquals(1, listener2.N);
		assertEquals(1, listener3.N);
		term.setChar(1, 1, '?', null);
		assertEquals(1, listener1.N);
		assertEquals(1, listener2.N);
		assertEquals(1, listener3.N);
		
		snapshot.updateSnapshot(false);
		assertEquals(1, listener1.N);
		assertEquals(1, listener2.N);
		assertEquals(1, listener3.N);
		listener1.reset();
		listener2.reset();
		listener3.reset();

		// make a change and expect it to be changed
		term.setChars(1, 1, new char[]{'?','!','.'},null);
		assertEquals(1, listener1.N);
		assertEquals(1, listener2.N);
		assertEquals(1, listener3.N);
		term.setChars(2, 1, new char[]{'?','!','.'},null);
		assertEquals(1, listener1.N);
		assertEquals(1, listener2.N);
		assertEquals(1, listener3.N);

		
		snapshot.updateSnapshot(false);
		assertEquals(1, listener1.N);
		assertEquals(1, listener2.N);
		assertEquals(1, listener3.N);

		listener1.reset();
		listener2.reset();
		listener3.reset();

		snapshot.removeListener(listener2);

		// scroll
		term.scroll(1, 2, -1);
		assertEquals(1, listener1.N);
		assertEquals(0, listener2.N);
		assertEquals(1, listener3.N);

		term.scroll(1, 2, -1);
		assertEquals(1, listener1.N);
		assertEquals(0, listener2.N);
		assertEquals(1, listener3.N);


		snapshot.updateSnapshot(false);
		assertEquals(1, listener1.N);
		assertEquals(0, listener2.N);
		assertEquals(1, listener3.N);

		snapshot.addListener(listener2);
		listener1.reset();
		listener2.reset();
		listener3.reset();

		
		snapshot.removeListener(listener3);
		// scroll
		term.scroll(1, 2, 1);
		assertEquals(1, listener1.N);
		assertEquals(1, listener2.N);
		assertEquals(0, listener3.N);

		term.scroll(1, 2, 1);
		assertEquals(1, listener1.N);
		assertEquals(1, listener2.N);
		assertEquals(0, listener3.N);


		snapshot.updateSnapshot(false);
		assertEquals(1, listener1.N);
		assertEquals(1, listener2.N);
		assertEquals(0, listener3.N);

		snapshot.addListener(listener3);
		listener1.reset();
		listener2.reset();
		listener3.reset();

		// add listener multiple times
		snapshot.addListener(listener3);
		
		// scroll
		term.scroll(1, 2, -1);
		assertEquals(1, listener1.N);
		assertEquals(1, listener2.N);
		assertEquals(2, listener3.N);

		term.scroll(1, 2, -1);
		assertEquals(1, listener1.N);
		assertEquals(1, listener2.N);
		assertEquals(2, listener3.N);


		snapshot.updateSnapshot(false);
		assertEquals(1, listener1.N);
		assertEquals(1, listener2.N);
		assertEquals(2, listener3.N);

		listener1.reset();
		listener2.reset();
		listener3.reset();
		// remove the duplicate listener
		snapshot.removeListener(listener3);

		
		// scroll
		term.scroll(1, 2, 1);
		assertEquals(1, listener1.N);
		assertEquals(1, listener2.N);
		assertEquals(1, listener3.N);


		snapshot.updateSnapshot(false);
		assertEquals(1, listener1.N);
		assertEquals(1, listener2.N);
		assertEquals(1, listener3.N);

		listener1.reset();
		listener2.reset();
		listener3.reset();

		
		// setDimensions
		term.setDimensions(2, 2);
		assertEquals(1, listener1.N);
		assertEquals(1, listener2.N);
		assertEquals(1, listener3.N);


		snapshot.updateSnapshot(false);
		assertEquals(1, listener1.N);
		assertEquals(1, listener2.N);
		assertEquals(1, listener3.N);

		listener1.reset();
		listener2.reset();
		listener3.reset();

		
		// setDimensions
		term.setDimensions(20, 20);
		assertEquals(1, listener1.N);
		assertEquals(1, listener2.N);
		assertEquals(1, listener3.N);


		snapshot.updateSnapshot(false);
		assertFalse(snapshot.isOutOfDate());
	}
	public void testWindowOfInterest() {
		ITerminalTextData term=makeITerminalTextData();
		TerminalTextTestHelper.fillSimple(term,"0123456789");
		ITerminalTextDataSnapshot snapshot=term.makeSnapshot();
		snapshot.updateSnapshot(false);
		snapshot.setInterestWindow(7, 4);
		snapshot.setInterestWindow(9, 4);
		snapshot.updateSnapshot(false);
	}
	public void testWindowOfInterest2() {
		ITerminalTextData term=makeITerminalTextData();
		TerminalTextTestHelper.fillSimple(term,"0123456789");
		ITerminalTextDataSnapshot snapshot=term.makeSnapshot();
		snapshot.updateSnapshot(false);
		term.scroll(7, 3,-1);
		snapshot.setInterestWindow(9, 4);
		snapshot.updateSnapshot(false);
	}
	public void testAddLine() {
		ITerminalTextData term=makeITerminalTextData();
		TerminalTextTestHelper.fillSimple(term,"0123456789");
		ITerminalTextDataSnapshot snapshot=term.makeSnapshot();
		term.setMaxHeight(20);
		snapshot.updateSnapshot(false);
		assertEquals(10,term.getHeight());
		assertEquals(20,term.getMaxHeight());
		assertFalse(snapshot.isOutOfDate());
		term.addLine();
		assertTrue(snapshot.isOutOfDate());
		assertEquals(11,term.getHeight());
		assertEquals(10,snapshot.getHeight());
		snapshot.updateSnapshot(false);
		assertEquals(11,term.getHeight());
		assertEquals(11,snapshot.getHeight());
		assertEquals(20,term.getMaxHeight());
		
		term.addLine();
		term.addLine();
		assertEquals(11,snapshot.getHeight());
		assertEquals(13,term.getHeight());
		assertTrue(snapshot.isOutOfDate());
		snapshot.updateSnapshot(false);
		assertEquals(13,snapshot.getHeight());
		assertEquals(13,term.getHeight());
		assertEquals(20,term.getMaxHeight());
	}
	public void testHasDimensionsChanged() {
		ITerminalTextData term=makeITerminalTextData();
		TerminalTextTestHelper.fillSimple(term,"0123456789");
		ITerminalTextDataSnapshot snapshot=term.makeSnapshot();
		term.setMaxHeight(20);
		snapshot.setInterestWindow(3, 4);
		snapshot.updateSnapshot(false);
		assertEquals(10,term.getHeight());
		assertEquals(20,term.getMaxHeight());
		assertFalse(snapshot.isOutOfDate());
		term.addLine();
		assertTrue(snapshot.isOutOfDate());
		assertEquals(11,term.getHeight());
		assertEquals(10,snapshot.getHeight());
		snapshot.updateSnapshot(false);
		assertTrue(snapshot.hasDimensionsChanged());
		assertEquals(11,term.getHeight());
		assertEquals(11,snapshot.getHeight());
		assertEquals(20,term.getMaxHeight());
		
		term.addLine();
		term.addLine();
		assertEquals(11,snapshot.getHeight());
		assertEquals(13,term.getHeight());
		assertTrue(snapshot.isOutOfDate());
		snapshot.updateSnapshot(false);
		assertTrue(snapshot.hasDimensionsChanged());
		assertEquals(13,snapshot.getHeight());
		assertEquals(13,term.getHeight());
		assertEquals(20,term.getMaxHeight());
	}
	public void testCursor() {
		ITerminalTextData term=makeITerminalTextData();
		TerminalTextTestHelper.fillSimple(term,"0123456789");
		ITerminalTextDataSnapshot snapshot=term.makeSnapshot();
		term.setMaxHeight(20);
		snapshot.setInterestWindow(3, 4);
		snapshot.updateSnapshot(false);
		term.setCursorLine(2);
		term.setCursorColumn(1);
		snapshot.updateSnapshot(false);
		assertEquals(2, snapshot.getCursorLine());
		assertEquals(1, snapshot.getCursorColumn());
		term.setCursorLine(3);
		term.setCursorColumn(2);
		snapshot.updateSnapshot(false);
		assertEquals(3, snapshot.getCursorLine());
		assertEquals(2, snapshot.getCursorColumn());
	}
	public void testCursor2() {
		ITerminalTextData term=makeITerminalTextData();
		TerminalTextTestHelper.fillSimple(term,"0123456789");
		ITerminalTextDataSnapshot snapshot=term.makeSnapshot();
		term.setMaxHeight(20);
		snapshot.setInterestWindow(3, 4);
		snapshot.updateSnapshot(false);
		term.setCursorLine(2);
		assertTrue(snapshot.isOutOfDate());
		snapshot.updateSnapshot(false);
		term.setCursorColumn(1);
		assertTrue(snapshot.isOutOfDate());
	}
	public void testHasTerminalChanged() {
		ITerminalTextData term=makeITerminalTextData();
		String s="12345\n" +
				 "abcde\n" +
				 "ABCDE\n" +
				 "vwxzy\n" +
				 "VWXYZ";
		TerminalTextTestHelper.fill(term,s);
		
		ITerminalTextDataSnapshot snapshot=term.makeSnapshot();
		assertTrue(snapshot.hasTerminalChanged());
		snapshot.updateSnapshot(false);

		assertTrue(snapshot.hasTerminalChanged());
		
		// make a change and expect it to be changed
		term.setChar(0, 0, '?', null);
		snapshot.updateSnapshot(false);
		assertTrue(snapshot.hasTerminalChanged());
		
		// make a change and expect it to be changed
		term.setChars(1, 1, new char[]{'?','!','.'},null);
		snapshot.updateSnapshot(false);
		assertTrue(snapshot.hasTerminalChanged());
		
		// scroll
		term.scroll(1, 2, -1);
		snapshot.updateSnapshot(false);
		assertTrue(snapshot.hasTerminalChanged());
		
		// scroll
		term.scroll(1, 2, 1);
		snapshot.updateSnapshot(false);
		assertTrue(snapshot.hasTerminalChanged());

		// scroll
		term.scroll(1, 2, -1);
		snapshot.updateSnapshot(true);
		assertTrue(snapshot.hasTerminalChanged());
		
		// scroll
		term.scroll(1, 2, 1);
		snapshot.updateSnapshot(true);
		assertTrue(snapshot.hasTerminalChanged());
		
		// setDimensions
		term.setDimensions(2, 2);
		snapshot.updateSnapshot(false);
		assertTrue(snapshot.hasTerminalChanged());
		
		// setDimensions
		term.setDimensions(20, 20);
		snapshot.updateSnapshot(false);
		assertTrue(snapshot.hasTerminalChanged());

		snapshot.updateSnapshot(false);
		assertFalse(snapshot.hasTerminalChanged());

		// window of interest changes should NOT set hasTerminalChanged
		snapshot.updateSnapshot(false);
		snapshot.setInterestWindow(7, 4);

		assertFalse(snapshot.hasTerminalChanged());
	}
	public void testGetTerminalTextData() {
		ITerminalTextData term=makeITerminalTextData();
		ITerminalTextDataSnapshot snapshot=term.makeSnapshot();
		assertSame(term, snapshot.getTerminalTextData());
	}
}
