/*******************************************************************************
 * Copyright (c) 2007, 2008 Wind River Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Michael Scharf (Wind River) - initial API and implementation
 * Martin Oberhuber (Wind River) - [168197] Fix Terminal for CDC-1.1/Foundation-1.1
 *******************************************************************************/
package org.eclipse.tm.internal.terminal.model;

import java.util.ArrayList;

import org.eclipse.tm.terminal.model.ITerminalTextData;
import org.eclipse.tm.terminal.model.LineSegment;
import org.eclipse.tm.terminal.model.Style;
import org.eclipse.tm.terminal.model.StyleColor;

@SuppressWarnings({"unchecked", "rawtypes"})
public class TerminalTextDataWindowTest extends AbstractITerminalTextDataTest {
	int fOffset;
	int fSize;
	public TerminalTextDataWindowTest() {
		fOffset=2;
		fSize=2;
	}
	protected ITerminalTextData makeITerminalTextData() {
		TerminalTextDataWindow term=new TerminalTextDataWindow();
		term.setWindow(fOffset,fSize);
		return term;
	}
	/**
	 * Used for multi line text
	 * @param expected
	 * @param actual
	 */
	protected void assertEqualsTerm(String expected,String actual) {
		assertEquals(stripMultiLine(expected), stripMultiLine(actual));
	}
	private String stripMultiLine(String s) {
		StringBuffer b=new StringBuffer();
		// String[] lines=s.split("\n");
		// <J2ME CDC-1.1 Foundation-1.1 variant>
		ArrayList l = new ArrayList();
		int j = 0;
		for (int k = 0; k < s.length(); k++) {
			if (s.charAt(k) == '\n') {
				l.add(s.substring(j, k));
				j = k;
			}
		}
		j = l.size() - 1;
		while (j >= 0 && "".equals(l.get(j))) {
			j--;
		}
		String[] lines = new String[j + 1];
		while (j >= 0) {
			lines[j] = (String) l.get(j);
			j--;
		}
		// </J2ME CDC-1.1 Foundation-1.1 variant>
		for (int i = 0; i < lines.length; i++) {
			if(i>0)
				b.append("\n"); //$NON-NLS-1$
			if(i>=fOffset && i<fOffset+fSize)
				b.append(lines[i]);
			else
				b.append(new String(new char[lines[i].length()]));
		}
		return b.toString();
	}
	/**
	 * Used for simple text
	 * @param expected
	 * @param actual
	 */
	protected void assertEqualsSimple(String expected,String actual) {
		assertEquals(stripSimple(expected), stripSimple(actual));
	}
	String stripSimple(String s) {
		StringBuffer b=new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			if(i>=fOffset && i<fOffset+fSize)
				b.append(s.charAt(i));
			else
				b.append(' ');
		}
		return b.toString();
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
		term.addLine();
		assertEqualsTerm(
				"222\n" +
				"333\n" +
				"444\n" +
				"\0\0\0\n" +
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
				"\000\000\000\n" +
				"\000\000\000\n" +
				"\000\000\000\n" +
				"\000\000\000\n" +
				"\000\000\000", toMultiLineText(term));
	}

	public void testGetLineSegments() {
		Style s1=getDefaultStyle();
		Style s2=s1.setBold(true);
		Style s3=s1.setUnderline(true);
		ITerminalTextData term=makeITerminalTextData();
		term.setDimensions(8, 8);
		LineSegment[] segments;

		term.setChars(2, 0,"0123".toCharArray(), s1);
		term.setChars(2, 4,"abcd".toCharArray(), null);
		segments=term.getLineSegments(2, 0, term.getWidth());
		assertEquals(2, segments.length);
		assertSegment(0, "0123", s1, segments[0]);
		assertSegment(4, "abcd", null, segments[1]);


		segments=term.getLineSegments(2, 4, term.getWidth()-4);
		assertEquals(1, segments.length);
		assertSegment(4, "abcd", null, segments[0]);

		segments=term.getLineSegments(2, 3, 2);
		assertEquals(2, segments.length);
		assertSegment(3, "3", s1, segments[0]);
		assertSegment(4, "a", null, segments[1]);

		segments=term.getLineSegments(2, 7, 1);
		assertEquals(1, segments.length);
		assertSegment(7, "d", null, segments[0]);

		segments=term.getLineSegments(2, 0, 1);
		assertEquals(1, segments.length);
		assertSegment(0, "0", s1, segments[0]);

		// line 1
		term.setChars(1, 0,"x".toCharArray(), s1);
		term.setChars(1, 1,"y".toCharArray(), s2);
		term.setChars(1, 2,"z".toCharArray(), s3);

		segments=term.getLineSegments(1, 0, term.getWidth());
		assertEquals(1, segments.length);
		assertSegment(0, "\000\000\000\000\000\000\000\000", null, segments[0]);

		// line 3
		segments=term.getLineSegments(3, 0, term.getWidth());
		assertEquals(1, segments.length);
		assertSegment(0, "\000\000\000\000\000\000\000\000", null, segments[0]);

	}
	public void testGetChar() {
		String s="12345\n" +
		 "abcde\n" +
		 "ABCDE";
		ITerminalTextData term=makeITerminalTextData();
		fill(term, s);
		assertEquals('\000', term.getChar(0,0));
		assertEquals('\000', term.getChar(0,1));
		assertEquals('\000', term.getChar(0,2));
		assertEquals('\000', term.getChar(0,3));
		assertEquals('\000', term.getChar(0,4));
		assertEquals('\000', term.getChar(1,0));
		assertEquals('\000', term.getChar(1,1));
		assertEquals('\000', term.getChar(1,2));
		assertEquals('\000', term.getChar(1,3));
		assertEquals('\000', term.getChar(1,4));
		assertEquals('A', term.getChar(2,0));
		assertEquals('B', term.getChar(2,1));
		assertEquals('C', term.getChar(2,2));
		assertEquals('D', term.getChar(2,3));
		assertEquals('E', term.getChar(2,4));
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
				Style s=null;
				if(line>=fOffset&&line<fOffset+fSize)
					s=style.setForground(StyleColor.getStyleColor(""+c));
				assertSame(s, term.getStyle(line, column));
			}
		}

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
				char c=0;
				if(line>=fOffset&&line<fOffset+fSize)
					c=(char)('a'+column+line);
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
				char c=0;
				if(line>=fOffset&&line<fOffset+fSize)
					c=(char)('a'+column+line);
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
		// check if chars are correctly chopped
		term.setChars(4, 1, new char[]{'1','2','3','4','5'}, null);
		assertEqualsTerm(
				  "abc\n"
				+ "bcd\n"
				+ "cde\n"
				+ "d12\n"
				+ "e12\n"
				+ "fgh", toMultiLineText(term));

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
	}
	public void testSetCopyLines() {
		ITerminalTextData term=new TerminalTextDataStore();
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
	}
	public void testScrollNegative() {
		scrollTest(0,2,-1,"  23  ","  23  ");
		scrollTest(0,1,-1,"  23  ","  23  ");
		scrollTest(0,6,-1,"  23  ","  3   ");
		scrollTest(0,6,-6,"  23  ","      ");
		scrollTest(0,6,-7,"  23  ","      ");
		scrollTest(0,6,-8,"  23  ","      ");
		scrollTest(0,6,-2,"  23  ","      ");
		scrollTest(1,1,-1,"  23  ","  23  ");
		scrollTest(1,2,-1,"  23  ","   3  ");
		scrollTest(5,1,-1,"  23  ","  23  ");
		scrollTest(5,1,-1,"  23  ","  23  ");
	}
	public void testScrollAll() {
		scrollTest(0,6,1,  "  2345","   2  ");
		scrollTest(0,6,-1, "  2345","  3   ");
		scrollTest(0,6,2,  "  2345","      ");
		scrollTest(0,6,-2, "  2345","      ");
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
				"\00\00\00\n" +
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
	public void testCopy() {
		ITerminalTextData term=makeITerminalTextData();
		term.setDimensions(3, 1);
		ITerminalTextData data=new TerminalTextData();
		fillSimple(data,"abcd");
		term.copy(data);


	}
}
