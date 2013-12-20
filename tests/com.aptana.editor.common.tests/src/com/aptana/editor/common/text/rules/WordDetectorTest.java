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

import org.eclipse.jface.text.rules.IWordDetector;

import com.aptana.editor.common.text.rules.WordDetector;

public class WordDetectorTest
{

	@Test
	public void testWordStart()
	{
		IWordDetector detector = new WordDetector();
		assertTrue(detector.isWordStart('_'));
		assertFalse(detector.isWordStart('-')); // no hyphens
		assertTrue(detector.isWordStart('$'));
		for (int i = 'a'; i <= 'z'; i++)
		{
			assertTrue(detector.isWordStart((char) i));
		}
		for (int i = 'A'; i <= 'Z'; i++)
		{
			assertTrue(detector.isWordStart((char) i));
		}
	}

	@Test
	public void testWordPart()
	{
		IWordDetector detector = new WordDetector();
		assertTrue(detector.isWordPart('_'));
		assertFalse(detector.isWordPart('-')); // no hyphens
		assertTrue(detector.isWordPart('$'));
		for (int i = 'a'; i <= 'z'; i++)
		{
			assertTrue(detector.isWordPart((char) i));
		}
		for (int i = 'A'; i <= 'Z'; i++)
		{
			assertTrue(detector.isWordPart((char) i));
		}
		for (int i = '0'; i <= '9'; i++)
		{
			assertTrue(detector.isWordPart((char) i));
		}
	}
}
