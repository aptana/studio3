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

public class TerminalTextDataSnapshotWindowTest extends TestCase {
	String toMultiLineText(ITerminalTextDataReadOnly term) {
		return TerminalTextTestHelper.toMultiLineText(term);
	}
	String toSimpleText(ITerminalTextDataReadOnly term) {
		return TerminalTextTestHelper.toSimple(term);
	}

	protected ITerminalTextData makeITerminalTextData() {
		return new TerminalTextData();
	}
	ITerminalTextDataSnapshot snapshotSimple(String text, ITerminalTextData term) {
		TerminalTextTestHelper.fillSimple(term,text);
		ITerminalTextDataSnapshot snapshot=term.makeSnapshot();
		return snapshot;
		
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

	public void testSetInterestWindow() {
		ITerminalTextData term=makeITerminalTextData();
		ITerminalTextDataSnapshot snapshot=snapshotSimple("0123456789",term);
		assertEquals(0, snapshot.getInterestWindowStartLine());
		assertEquals(-1, snapshot.getInterestWindowSize());
		snapshot.setInterestWindow(2, 3);
		assertEquals(2, snapshot.getInterestWindowStartLine());
		assertEquals(3, snapshot.getInterestWindowSize());
		snapshot.updateSnapshot(false);
		assertChangedLines(snapshot,"0011100000");
	}
	public void testSetChar() {
		ITerminalTextData term=makeITerminalTextData();
		ITerminalTextDataSnapshot snapshot=snapshotSimple("0123456789",term);
		snapshot.setInterestWindow(2, 3);
		snapshot.updateSnapshot(false);
		assertEquals("  234     ", toSimpleText(snapshot));
		
		term.setChar(0, 0, 'x', null);
		assertFalse(snapshot.isOutOfDate());
		snapshot.updateSnapshot(false);
		assertChangedLines(snapshot,"0000000000");
		
		term.setChar(1, 0, 'x', null);
		assertFalse(snapshot.isOutOfDate());
		
		term.setChar(2, 0, 'x', null);
		assertTrue(snapshot.isOutOfDate());
		snapshot.updateSnapshot(false);
		assertChangedLines(snapshot,"0010000000");
		
		term.setChar(3, 0, 'x', null);
		assertTrue(snapshot.isOutOfDate());
		snapshot.updateSnapshot(false);
		assertChangedLines(snapshot,"0001000000");

		term.setChar(4, 0, 'x', null);
		assertTrue(snapshot.isOutOfDate());
		snapshot.updateSnapshot(false);
		assertChangedLines(snapshot,"0000100000");

		term.setChar(5, 0, 'x', null);
		assertFalse(snapshot.isOutOfDate());
		
		term.setChar(6, 0, 'x', null);
		assertFalse(snapshot.isOutOfDate());
		
		for (int i = 0; i < 9; i++) {
			term.setChar(i, 0, (char)('a'+i), null);
		}
		assertTrue(snapshot.isOutOfDate());
		snapshot.updateSnapshot(false);
		assertChangedLines(snapshot,"0011100000");
	}

	public void testSetChars() {
		ITerminalTextData term=makeITerminalTextData();
		ITerminalTextDataSnapshot snapshot=snapshotSimple("0123456789",term);
		snapshot.setInterestWindow(2, 3);
		snapshot.updateSnapshot(false);
		assertEquals("  234     ", toSimpleText(snapshot));
		
		term.setChars(0, 0, "x".toCharArray(), null);
		assertFalse(snapshot.isOutOfDate());
		snapshot.updateSnapshot(false);
		assertChangedLines(snapshot,"0000000000");
		
		term.setChars(1, 0, "x".toCharArray(), null);
		assertFalse(snapshot.isOutOfDate());
		
		term.setChars(2, 0, "x".toCharArray(), null);
		assertTrue(snapshot.isOutOfDate());
		snapshot.updateSnapshot(false);
		assertChangedLines(snapshot,"0010000000");
		
		term.setChars(3, 0, "x".toCharArray(), null);
		assertTrue(snapshot.isOutOfDate());
		snapshot.updateSnapshot(false);
		assertChangedLines(snapshot,"0001000000");

		term.setChars(4, 0, "x".toCharArray(), null);
		assertTrue(snapshot.isOutOfDate());
		snapshot.updateSnapshot(false);
		assertChangedLines(snapshot,"0000100000");

		term.setChars(5, 0, "x".toCharArray(), null);
		assertFalse(snapshot.isOutOfDate());
		
		term.setChars(6, 0, "x".toCharArray(), null);
		assertFalse(snapshot.isOutOfDate());
		for (int i = 0; i < 9; i++) {
			term.setChars(i, 0, (i+"").toCharArray(), null);
		}
		assertTrue(snapshot.isOutOfDate());
		snapshot.updateSnapshot(false);
		assertChangedLines(snapshot,"0011100000");
	}
	public void testSetChars2() {
		ITerminalTextData term=makeITerminalTextData();
		ITerminalTextDataSnapshot snapshot=snapshotSimple("0123456789",term);
		snapshot.setInterestWindow(2, 3);
		snapshot.updateSnapshot(false);
		assertEquals("  234     ", toSimpleText(snapshot));
		
		term.setChars(0, 0, "abcdef".toCharArray(),2,1, null);
		assertFalse(snapshot.isOutOfDate());
		snapshot.updateSnapshot(false);
		assertChangedLines(snapshot,"0000000000");
		
		term.setChars(1, 0, "abcdef".toCharArray(),2 ,1, null);
		assertFalse(snapshot.isOutOfDate());
		
		term.setChars(2, 0, "abcdef".toCharArray(),2 ,1, null);
		assertTrue(snapshot.isOutOfDate());
		snapshot.updateSnapshot(false);
		assertChangedLines(snapshot,"0010000000");
		
		term.setChars(3, 0, "abcdef".toCharArray(),2 ,1, null);
		assertTrue(snapshot.isOutOfDate());
		snapshot.updateSnapshot(false);
		assertChangedLines(snapshot,"0001000000");

		term.setChars(4, 0, "abcdef".toCharArray(),2 ,1, null);
		assertTrue(snapshot.isOutOfDate());
		snapshot.updateSnapshot(false);
		assertChangedLines(snapshot,"0000100000");

		term.setChars(5, 0, "abcdef".toCharArray(),2 ,1, null);
		assertFalse(snapshot.isOutOfDate());
		
		term.setChars(6, 0, "abcdef".toCharArray(),2 ,1, null);
		assertFalse(snapshot.isOutOfDate());
		for (int i = 0; i < 9; i++) {
			term.setChars(i, 0, ("ab"+i+"def").toCharArray(),2 ,1, null);
		}
		assertTrue(snapshot.isOutOfDate());
		snapshot.updateSnapshot(false);
		assertChangedLines(snapshot,"0011100000");
	}
}
