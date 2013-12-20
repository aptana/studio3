/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.text.rules;

import org.junit.Test;
import static org.junit.Assert.*;
import junit.framework.TestCase;

import org.eclipse.jface.text.rules.IWhitespaceDetector;

import com.aptana.editor.common.text.rules.WhitespaceDetector;

public class WhitespaceDetectorTest
{

	@Test
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
