package com.aptana.editor.common.text.rules;

import junit.framework.TestCase;

import org.eclipse.jface.text.rules.IWordDetector;

import com.aptana.editor.common.text.rules.WordDetector;

public class WordDetectorTest extends TestCase
{

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
