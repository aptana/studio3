package com.aptana.editor.common;

import junit.framework.TestCase;

import org.eclipse.jface.text.rules.IWhitespaceDetector;

public class WhitespaceDetectorTest extends TestCase
{

	public void testIsWhitespace()
	{
		IWhitespaceDetector detector = new WhitespaceDetector();
		assertTrue(detector.isWhitespace(' '));
		assertTrue(detector.isWhitespace('\t'));
		assertTrue(detector.isWhitespace('\n'));
		assertTrue(detector.isWhitespace('\r'));
		assertTrue(detector.isWhitespace('\f'));
		assertFalse(detector.isWhitespace('c'));
		assertFalse(detector.isWhitespace('1'));
		assertFalse(detector.isWhitespace('-'));
		assertFalse(detector.isWhitespace('_'));
		assertFalse(detector.isWhitespace('$'));
	}
}
