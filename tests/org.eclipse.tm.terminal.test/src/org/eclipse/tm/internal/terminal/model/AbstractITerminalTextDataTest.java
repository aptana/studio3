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
import org.eclipse.tm.terminal.model.LineSegment;
import org.eclipse.tm.terminal.model.Style;
import org.eclipse.tm.terminal.model.StyleColor;

abstract public class AbstractITerminalTextDataTest extends TestCase {
	abstract protected ITerminalTextData makeITerminalTextData();

	protected void setUp() throws Exception {
		try {
			assert false;
			throw new Error("No Assertions! Run this code with assertions enabled! (vmargs: -ea)");
		} catch(AssertionError e) {
			// OK, assertions are enabled!
		}
		super.setUp();
	}
	protected String toSimple(ITerminalTextData term) {
		return TerminalTextTestHelper.toSimple(term);
	}
	protected String toMultiLineText(ITerminalTextDataReadOnly term) {
		return TerminalTextTestHelper.toMultiLineText(term);
	}
	
	protected void fill(ITerminalTextData term, String s) {
		TerminalTextTestHelper.fill(term,s);
	}

	protected void fill(ITerminalTextData term, int i, int j, String s) {
		TerminalTextTestHelper.fill(term,i,j,s);
	}

	protected void fillSimple(ITerminalTextData term, String s) {
		TerminalTextTestHelper.fillSimple(term, s);
	}


	/**
	 * Used for multi line text
	 * @param expected
	 * @param actual
	 */
	protected void assertEqualsTerm(String expected,String actual) {
		assertEquals(expected, actual);
	}
	/**
	 * Used for simple text
	 * @param expected
	 * @param actual
	 */
	protected void assertEqualsSimple(String expected,String actual) {
		assertEquals(-1,actual.indexOf('\n'));
		assertEquals(expected, actual);
	}
	public void testGetWidth() {
		ITerminalTextData term=makeITerminalTextData();
		assertEquals(0, term.getWidth());
		term.setDimensions(term.getHeight(), 10);
		assertEquals(10, term.getWidth());
		term.setDimensions(term.getHeight(), 0);
		assertEquals(0, term.getWidth());
	}

	public void testAddLine() {
		String s=
			"111\n" +
			"222\n" +
			"333\n" +
			"444\n" +
			"555";
		ITerminalTextData term=makeITerminalTextData();
		fill(term, s);
		term.setMaxHeight(5);
		term.addLine();
		assertEqualsTerm(
				"222\n" +
				"333\n" +
				"444\n" +
				"555\n" +
				"\000\000\000", toMultiLineText(term));
	}
	public void testCleanLine() {
		String s=
			"111\n" +
			"222\n" +
			"333\n" +
			"444\n" +
			"555";
		ITerminalTextData term=makeITerminalTextData();
		fill(term, s);
		term.cleanLine(0);
		assertEqualsTerm(
				"\000\000\000\n" +
				"222\n" +
				"333\n" +
				"444\n" +
				"555", toMultiLineText(term));
		
		fill(term, s);
		term.cleanLine(4);
		assertEqualsTerm(
				"111\n" +
				"222\n" +
				"333\n" +
				"444\n" +
				"\000\000\000", toMultiLineText(term));
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
		fill(term, s);
		assertEquals(5, term.getHeight());
		assertEquals(8, term.getMaxHeight());
		term.addLine();
		assertEquals(6, term.getHeight());
		assertEqualsTerm(
				"111\n" +
				"222\n" +
				"333\n" +
				"444\n" +
				"555\n" +
				"\000\000\000", toMultiLineText(term));
		term.addLine();
		assertEquals(7, term.getHeight());
		assertEqualsTerm(
				"111\n" +
				"222\n" +
				"333\n" +
				"444\n" +
				"555\n" +
				"\000\000\000\n" +
				"\000\000\000", toMultiLineText(term));
		term.addLine();
		assertEquals(8, term.getHeight());
		assertEqualsTerm(
				"111\n" +
				"222\n" +
				"333\n" +
				"444\n" +
				"555\n" +
				"\000\000\000\n" +
				"\000\000\000\n" +
				"\000\000\000", toMultiLineText(term));
		term.addLine();
		assertEquals(8, term.getHeight());
		assertEqualsTerm(
				"222\n" +
				"333\n" +
				"444\n" +
				"555\n" +
				"\000\000\000\n" +
				"\000\000\000\n" +
				"\000\000\000\n" +
				"\000\000\000", toMultiLineText(term));
		term.addLine();
		assertEquals(8, term.getHeight());
		assertEqualsTerm(
				"333\n" +
				"444\n" +
				"555\n" +
				"\000\000\000\n" +
				"\000\000\000\n" +
				"\000\000\000\n" +
				"\000\000\000\n" +
				"\000\000\000", toMultiLineText(term));
	}
	public void testGetHeight() {
		ITerminalTextData term=makeITerminalTextData();
		assertEquals(0, term.getHeight());
		term.setDimensions(10, term.getWidth());
		assertEquals(10, term.getHeight());
		term.setDimensions(0, term.getWidth());
		assertEquals(0, term.getHeight());
	}

	public void testSetDimensions() {
		ITerminalTextData term=makeITerminalTextData();
		assertEquals(0, term.getHeight());
		term.setDimensions(10, 5);
		assertEquals(5, term.getWidth());
		assertEquals(10, term.getHeight());
		term.setDimensions(5, 10);
		assertEquals(10, term.getWidth());
		assertEquals(5, term.getHeight());
		term.setDimensions(15, 0);
		assertEquals(0, term.getWidth());
		assertEquals(15, term.getHeight());
		term.setDimensions(0, 12);
		assertEquals(12, term.getWidth());
		assertEquals(0, term.getHeight());
		term.setDimensions(0, 0);
		assertEquals(0, term.getWidth());
		assertEquals(0, term.getHeight());
	}
	public void testResize() {
		ITerminalTextData term=makeITerminalTextData();
		term.setDimensions(3, 5);
		String s="12345\n" +
				 "abcde\n" +
				 "ABCDE";
		fill(term,0,0,s);
		assertEqualsTerm(s, toMultiLineText(term));
		term.setDimensions(3, 4);
		assertEqualsTerm(
				 "1234\n" +
				 "abcd\n" +
				 "ABCD", toMultiLineText(term));
		// the columns should be restored
		term.setDimensions(3, 5);
		assertEqualsTerm(
				 "12345\n" +
				 "abcde\n" +
				 "ABCDE", toMultiLineText(term));
		term.setDimensions(3, 6);
		assertEqualsTerm(
				 "12345\000\n" +
				 "abcde\000\n" +
				 "ABCDE\000", toMultiLineText(term));
		term.setChar(0, 5, 'x', null);
		term.setChar(1, 5, 'y', null);
		term.setChar(2, 5, 'z', null);
		assertEqualsTerm(
				 "12345x\n" +
				 "abcdey\n" +
				 "ABCDEz", toMultiLineText(term));
		term.setDimensions(2, 4);
		assertEqualsTerm(
				 "1234\n" +
				 "abcd", toMultiLineText(term));
	}

	public void testResizeFailure() {
		ITerminalTextData term=makeITerminalTextData();
		term.setDimensions(3, 5);
		String s="12345\n" +
				 "abcde\n" +
				 "ABCDE";
		fill(term,0,0,s);
		assertEqualsTerm(s, toMultiLineText(term));
		try {
			term.setDimensions(-3, 4);
			fail();
		} catch (RuntimeException e) {
			// OK
		}
//		assertEquals(5, term.getWidth());
//		assertEquals(3, term.getHeight());
//		assertEquals(s, toSimpleText(term));
	}
	
	public void testGetLineSegments() {
		Style s1=getDefaultStyle();
		Style s2=s1.setBold(true);
		Style s3=s1.setUnderline(true);
		ITerminalTextData term=makeITerminalTextData();
		term.setDimensions(8, 8);
		LineSegment[] segments;
		
		term.setChars(0, 0,"0123".toCharArray(), s1);
		term.setChars(0, 4,"abcd".toCharArray(), null);
		segments=term.getLineSegments(0, 0, term.getWidth());
		assertEquals(2, segments.length);
		assertSegment(0, "0123", s1, segments[0]);
		assertSegment(4, "abcd", null, segments[1]);
		
		
		segments=term.getLineSegments(0, 4, term.getWidth()-4);
		assertEquals(1, segments.length);
		assertSegment(4, "abcd", null, segments[0]);
		
		segments=term.getLineSegments(0, 3, 2);
		assertEquals(2, segments.length);
		assertSegment(3, "3", s1, segments[0]);
		assertSegment(4, "a", null, segments[1]);
		
		segments=term.getLineSegments(0, 7, 1);
		assertEquals(1, segments.length);
		assertSegment(7, "d", null, segments[0]);
		
		segments=term.getLineSegments(0, 0, 1);
		assertEquals(1, segments.length);
		assertSegment(0, "0", s1, segments[0]);
		
		// line 1
		term.setChars(1, 0,"x".toCharArray(), s1);
		term.setChars(1, 1,"y".toCharArray(), s2);
		term.setChars(1, 2,"z".toCharArray(), s3);
		
		segments=term.getLineSegments(1, 0, term.getWidth());
		assertEquals(4, segments.length);
		assertSegment(0, "x", s1, segments[0]);
		assertSegment(1, "y", s2, segments[1]);
		assertSegment(2, "z", s3, segments[2]);
		assertSegment(3, "\000\000\000\000\000", null, segments[3]);
		
		// line 2
		term.setChars(2, 4,"klm".toCharArray(), s1);		
		segments=term.getLineSegments(2, 0, term.getWidth());
		assertEquals(3, segments.length);
		assertSegment(0, "\000\000\000\000", null, segments[0]);
		assertSegment(4, "klm", s1, segments[1]);
		assertSegment(7, "\000", null, segments[2]);
		
		// line 3
		segments=term.getLineSegments(3, 0, term.getWidth());
		assertEquals(1, segments.length);
		assertSegment(0, "\000\000\000\000\000\000\000\000", null, segments[0]);
		
	}
	public void testGetLineSegmentsNull() {
		ITerminalTextData term=makeITerminalTextData();
		term.setDimensions(8, 8);
		LineSegment[] segments=term.getLineSegments(0, 0, term.getWidth());
		assertEquals(1, segments.length);
	}
	public void testGetLineSegmentsOutOfBounds() {
		ITerminalTextData term=makeITerminalTextData();
		term.setDimensions(1, 8);
		term.setChars(0,0,"xx".toCharArray(),null);
		LineSegment[] segments=term.getLineSegments(0, 5, 2);
		assertEquals(1, segments.length);
		
		
	}
	void assertSegment(int col,String text, Style style,LineSegment segment) {
		assertEquals(col, segment.getColumn());
		assertEqualsTerm(text, segment.getText());
		assertEquals(style, segment.getStyle());
		
	}
	public void testGetChar() {
		String s="12345\n" +
		 "abcde\n" +
		 "ABCDE";
		ITerminalTextData term=makeITerminalTextData();
		fill(term, s);
		assertEquals('1', term.getChar(0,0));
		assertEquals('2', term.getChar(0,1));
		assertEquals('3', term.getChar(0,2));
		assertEquals('4', term.getChar(0,3));
		assertEquals('5', term.getChar(0,4));
		assertEquals('a', term.getChar(1,0));
		assertEquals('b', term.getChar(1,1));
		assertEquals('c', term.getChar(1,2));
		assertEquals('d', term.getChar(1,3));
		assertEquals('e', term.getChar(1,4));
		assertEquals('A', term.getChar(2,0));
		assertEquals('B', term.getChar(2,1));
		assertEquals('C', term.getChar(2,2));
		assertEquals('D', term.getChar(2,3));
		assertEquals('E', term.getChar(2,4));
		try {
			term.getChar(0,-1);
			fail();
		} catch (RuntimeException e) {
		}
		try {
			term.getChar(-1,-1);
			fail();
		} catch (RuntimeException e) {
		}
		try {
			term.getChar(-1,0);
			fail();
		} catch (RuntimeException e) {
		}
		try {
			term.getChar(0,5);
			fail();
		} catch (RuntimeException e) {
		}
		try {
			term.getChar(3,5);
			fail();
		} catch (RuntimeException e) {
		}
		try {
			term.getChar(3,0);
			fail();
		} catch (RuntimeException e) {
		}
	}

	public void testGetStyle() {
		ITerminalTextData term=makeITerminalTextData();
		Style style=getDefaultStyle();
		term.setDimensions(6, 3);
		for (int line = 0; line < term.getHeight(); line++) {
			for (int column = 0; column < term.getWidth(); column++) {
				char c=(char)('a'+column+line);
				term.setChar(line, column, c, style.setForground(StyleColor.getStyleColor(""+c)));
			}
		}
		for (int line = 0; line < term.getHeight(); line++) {
			for (int column = 0; column < term.getWidth(); column++) {
				char c=(char)('a'+column+line);
				assertSame(style.setForground(StyleColor.getStyleColor(""+c)), term.getStyle(line, column));
			}
		}
		
	}

	protected Style getDefaultStyle() {
		return Style.getStyle(StyleColor.getStyleColor("fg"), StyleColor.getStyleColor("bg"), false, false, false, false);
	}

	public void testSetChar() {
		ITerminalTextData term=makeITerminalTextData();
		term.setDimensions(6, 3);
		for (int line = 0; line < term.getHeight(); line++) {
			for (int column = 0; column < term.getWidth(); column++) {
				term.setChar(line, column, (char)('a'+column+line), null);
			}
		}
		for (int line = 0; line < term.getHeight(); line++) {
			for (int column = 0; column < term.getWidth(); column++) {
				char c=(char)('a'+column+line);
				assertEquals(c, term.getChar(line,column));
			}
		}
		assertEqualsTerm(
				  "abc\n"
				+ "bcd\n"
				+ "cde\n"
				+ "def\n"
				+ "efg\n"
				+ "fgh", toMultiLineText(term));
	}
	public void testSetChars() {
		ITerminalTextData term=makeITerminalTextData();
		term.setDimensions(6, 3);
		for (int line = 0; line < term.getHeight(); line++) {
			char[] chars=new char[term.getWidth()];
			for (int column = 0; column < term.getWidth(); column++) {
				chars[column]=(char)('a'+column+line);
			}
			term.setChars(line, 0, chars, null);
		}
		for (int line = 0; line < term.getHeight(); line++) {
			for (int column = 0; column < term.getWidth(); column++) {
				char c=(char)('a'+column+line);
				assertEquals(c, term.getChar(line,column));
			}
		}
		assertEqualsTerm(
				  "abc\n"
				+ "bcd\n"
				+ "cde\n"
				+ "def\n"
				+ "efg\n"
				+ "fgh", toMultiLineText(term));
	
		term.setChars(3, 1, new char[]{'1','2'}, null);
		assertEqualsTerm(
				  "abc\n"
				+ "bcd\n"
				+ "cde\n"
				+ "d12\n"
				+ "efg\n"
				+ "fgh", toMultiLineText(term));
		try {
			// check if we cannot exceed the range
			term.setChars(4, 1, new char[]{'1','2','3','4','5'}, null);
			fail();
		} catch (RuntimeException e) {}
	
	}
	public void testSetCharsLen() {
		ITerminalTextData term=makeITerminalTextData();
		String s= "ZYXWVU\n"
				+ "abcdef\n"
				+ "ABCDEF";
		fill(term, s);
		char[] chars=new char[]{'1','2','3','4','5','6','7','8'};
		term.setChars(1, 0, chars, 0, 6,null);
		assertEqualsTerm(
				  "ZYXWVU\n"
				+ "123456\n"
				+ "ABCDEF", toMultiLineText(term));

		fill(term, s);
		term.setChars(1, 0, chars, 0, 5, null);
		assertEqualsTerm("ZYXWVU\n"
				+ "12345f\n"
				+ "ABCDEF", toMultiLineText(term));

		fill(term, s);
		term.setChars(1, 0, chars, 1, 5, null);
		assertEqualsTerm("ZYXWVU\n"
				+ "23456f\n"
				+ "ABCDEF", toMultiLineText(term));

		fill(term, s);
		term.setChars(1, 1, chars, 1, 4, null);
		assertEqualsTerm("ZYXWVU\n"
				+ "a2345f\n"
				+ "ABCDEF", toMultiLineText(term));


		
		fill(term, s);
		term.setChars(1, 2, chars, 3, 4, null);
		assertEqualsTerm("ZYXWVU\n"
				+ "ab4567\n"
				+ "ABCDEF", toMultiLineText(term));

		fill(term, s);
		try {
			term.setChars(1, 0, chars, 7, 10, null);
			fail();
		} catch (RuntimeException e) {}
		fill(term, s);
		try {
			term.setChars(1, -1, chars, 0, 2, null);
			fail();
		} catch (RuntimeException e) {}
		try {
			term.setChars(-1, 1, chars, 0, 2, null);
			fail();
		} catch (RuntimeException e) {}
		try {
			term.setChars(1, 10, chars, 0, 2, null);
			fail();
		} catch (RuntimeException e) {}
		try {
			term.setChars(10, 1, chars, 0, 2, null);
			fail();
		} catch (RuntimeException e) {}
//		assertEquals(s, toSimpleText(term));
	}
	public void testSetCopyInto() {
		ITerminalTextData term=makeITerminalTextData();
		term.setDimensions(3, 5);
		String s="12345\n" +
				 "abcde\n" +
				 "ABCDE";
		fill(term,0,0,s);
		ITerminalTextData termCopy=makeITerminalTextData();
		termCopy.copy(term);
		assertEqualsTerm(s, toMultiLineText(termCopy));
		assertEqualsTerm(s, toMultiLineText(term));
		
		termCopy.setChar(1, 1, 'X', null);
		assertEqualsTerm(s, toMultiLineText(term));
		term.setDimensions(2, 4);
		assertEquals(5, termCopy.getWidth());
		assertEquals(3, termCopy.getHeight());
		
		assertEqualsTerm("12345\n" +
				 "aXcde\n" +
				 "ABCDE", toMultiLineText(termCopy));

		assertEquals(4, term.getWidth());
		assertEquals(2, term.getHeight());
	}
	public void testSetCopyLines() {
		ITerminalTextData term=makeITerminalTextData();
		String s="012345";
		fillSimple(term, s);
		ITerminalTextData termCopy=makeITerminalTextData();
		String sCopy="abcde";
		fillSimple(termCopy, sCopy);
		termCopy.copyRange(term,0,0,0);
		assertEqualsSimple(s, toSimple(term));
		assertEqualsSimple(sCopy, toSimple(termCopy));

		fillSimple(termCopy, sCopy);
		termCopy.copyRange(term,0,0,5);
		assertEqualsSimple(s, toSimple(term));
		assertEqualsSimple("01234", toSimple(termCopy));
	
		fillSimple(termCopy, sCopy);
		termCopy.copyRange(term,0,0,2);
		assertEqualsSimple(s, toSimple(term));
		assertEqualsSimple("01cde", toSimple(termCopy));

		fillSimple(termCopy, sCopy);
		termCopy.copyRange(term,0,1,2);
		assertEqualsSimple(s, toSimple(term));
		assertEqualsSimple("a01de", toSimple(termCopy));

		fillSimple(termCopy, sCopy);
		termCopy.copyRange(term,1,1,2);
		assertEqualsSimple(s, toSimple(term));
		assertEqualsSimple("a12de", toSimple(termCopy));

		fillSimple(termCopy, sCopy);
		termCopy.copyRange(term,1,1,4);
		assertEqualsSimple(s, toSimple(term));
		assertEqualsSimple("a1234", toSimple(termCopy));

		fillSimple(termCopy, sCopy);
		termCopy.copyRange(term,2,1,4);
		assertEqualsSimple(s, toSimple(term));
		assertEqualsSimple("a2345", toSimple(termCopy));

		try {
			fillSimple(termCopy, sCopy);
			termCopy.copyRange(term,1,1,5);
			fail();
		} catch (RuntimeException e) {}
		try {
			fillSimple(termCopy, sCopy);
			termCopy.copyRange(term,0,0,6);
			fail();
		} catch (RuntimeException e) {}
		try {
			fillSimple(termCopy, sCopy);
			termCopy.copyRange(term,7,0,1);
			fail();
		} catch (RuntimeException e) {}
		try {
			fillSimple(termCopy, sCopy);
			termCopy.copyRange(term,0,7,1);
			fail();
		} catch (RuntimeException e) {}
	}
	public void testCopyLine() {
		ITerminalTextData term=makeITerminalTextData();
		String s=
			"111\n" +
			"222\n" +
			"333\n" +
			"444\n" +
			"555";
		fill(term, s);
		ITerminalTextData dest=makeITerminalTextData();
		String sCopy=
			"aaa\n" +
			"bbb\n" +
			"ccc\n" +
			"ddd\n" +
			"eee";
		fill(dest, sCopy);
		copySelective(dest,term,0,0,new boolean []{true,true,false,false,true});
		assertEqualsTerm(s, toMultiLineText(term));
		assertEqualsTerm(			
				"111\n" +
				"222\n" +
				"ccc\n" +
				"ddd\n" +
				"555", toMultiLineText(dest));

		fill(dest, sCopy);
		copySelective(dest,term,0,0,new boolean []{true,true,true,true,true});
		assertEqualsTerm(s, toMultiLineText(term));
		assertEqualsTerm(s, toMultiLineText(dest));
	
		fill(dest, sCopy);
		copySelective(dest,term,0,0,new boolean []{false,false,false,false,false});
		assertEqualsTerm(s, toMultiLineText(term));
		assertEqualsTerm(sCopy, toMultiLineText(dest));
	}
	protected void copySelective(ITerminalTextData dest, ITerminalTextData source, int sourceStartLine, int destStartLine, boolean[] linesToCopy) {
		for (int i = 0; i < linesToCopy.length; i++) {
			if(linesToCopy[i]) {
				dest.copyLine(source, i+sourceStartLine, i+destStartLine);
			}
		}
	}

	public void testCopyLineWithOffset() {
		ITerminalTextData term=makeITerminalTextData();
		String s=
			"111\n" +
			"222\n" +
			"333\n" +
			"444\n" +
			"555";
		fill(term, s);
		ITerminalTextData dest=makeITerminalTextData();
		String sCopy=
			"aaa\n" +
			"bbb\n" +
			"ccc\n" +
			"ddd\n" +
			"eee";
		fill(dest, sCopy);
		copySelective(dest,term,1,0,new boolean []{true,false,false,true});
		assertEqualsTerm(s, toMultiLineText(term));
		assertEqualsTerm(			
				"222\n" +
				"bbb\n" +
				"ccc\n" +
				"555\n" +
				"eee", toMultiLineText(dest));

		fill(dest, sCopy);
		copySelective(dest,term,2,0,new boolean []{true,true});
		assertEqualsTerm(s, toMultiLineText(term));
		assertEqualsTerm(			
				"333\n" +
				"444\n" +
				"ccc\n" +
				"ddd\n" +
				"eee", toMultiLineText(dest));

		fill(dest, sCopy);
		copySelective(dest,term,0,0,new boolean []{true,true,true,true,true});
		assertEqualsTerm(s, toMultiLineText(term));
		assertEqualsTerm(s, toMultiLineText(dest));
	
		fill(dest, sCopy);
		copySelective(dest,term,0,0,new boolean []{false,false,false,false,false});
		assertEqualsTerm(s, toMultiLineText(term));
		assertEqualsTerm(sCopy, toMultiLineText(dest));
	}
	public void testScrollNoop() {
		scrollTest(0,0,0, "012345","012345");
		scrollTest(0,1,0, "012345","012345");
		scrollTest(0,6,0, "012345","012345");
	}
	public void testScrollAll() {
		scrollTest(0,6,1,  "012345"," 01234");
		scrollTest(0,6,-1, "012345","12345 ");
		scrollTest(0,6,2,  "012345","  0123");
		scrollTest(0,6,-2, "012345","2345  ");
	}
	public void testScrollNegative() {
		scrollTest(0,2,-1,"012345","1 2345");
		scrollTest(0,1,-1,"012345"," 12345");
		scrollTest(0,6,-1,"012345","12345 ");
		scrollTest(0,6,-6,"012345","      ");
		scrollTest(0,6,-7,"012345","      ");
		scrollTest(0,6,-8,"012345","      ");
		scrollTest(0,6,-2,"012345","2345  ");
		scrollTest(1,1,-1,"012345","0 2345");
		scrollTest(1,1,-1,"012345","0 2345");
		scrollTest(1,2,-1,"012345","02 345");
		scrollTest(5,1,-1,"012345","01234 ");
		scrollTest(5,1,-1,"012345","01234 ");
	}
	public void testScrollNegative2() {
		scrollTest(0,2,-1,"  23  ","  23  ");
		scrollTest(0,1,-1,"  23  ","  23  ");
		scrollTest(0,6,-1,"  23  "," 23   ");
		scrollTest(0,6,-6,"  23  ","      ");
		scrollTest(0,6,-7,"  23  ","      ");
		scrollTest(0,6,-8,"  23  ","      ");
		scrollTest(0,6,-2,"  23  ","23    ");
		scrollTest(1,1,-1,"  23  ","  23  ");
		scrollTest(1,2,-1,"  23  "," 2 3  ");
		scrollTest(5,1,-1,"  23  ","  23  ");
		scrollTest(5,1,-1,"  23  ","  23  ");
	}
	public void testScrollNegative3() {
		scrollTest(1,5,-7,"012345","0     ");
	}
	public void testScrollPositive2() {
		scrollTest(2,8,20, "0123456789", "01        ");
	}
	public void testScrollPositive() {
		scrollTest(0,2,1, "012345",     " 02345");
		scrollTest(0,2,2, "012345",     "  2345");
		scrollTest(2,4,2, "012345",     "01  23");
		scrollTest(2,4,2, "0123456",    "01  236");
		scrollTest(0,7,6, "0123456",    "      0");
		scrollTest(0,7,8, "0123456",    "       ");
		scrollTest(0,7,9, "0123456",    "       ");
		scrollTest(2,4,2, "0123456",    "01  236");
		scrollTest(2,5,3, "0123456789", "01   23789");
		scrollTest(2,7,3, "0123456789", "01   23459");
		scrollTest(2,8,3, "0123456789", "01   23456");
		scrollTest(2,8,5, "0123456789", "01     234");
		scrollTest(2,8,9, "0123456789", "01        ");
		scrollTest(0,10,9,"0123456789", "         0");
		scrollTest(0,6,6, "012345",     "      ");
	}
	public void testScrollFail() {
		try {
			scrollTest(5,2,-1,"012345","012345");
			fail();
		} catch (RuntimeException e) {
		}
		try {
			scrollTest(0,7,1,"012345","      ");
			fail();
		} catch (RuntimeException e) {
		}
	}
	/**
	 * Makes a simple shift test
	 * @param line scroll start
	 * @param n number of lines to be scrolled
	 * @param shift amount of lines to be shifted
	 * @param start the original data
	 * @param result the expected result
	 */
	void scrollTest(int line,int n, int shift, String start,String result) {
		ITerminalTextData term=makeITerminalTextData();
		fillSimple(term,start);
		term.scroll(line, n, shift);
		assertEqualsSimple(result, toSimple(term));
		
	}
}
