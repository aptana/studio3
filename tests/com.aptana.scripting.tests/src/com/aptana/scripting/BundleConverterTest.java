package com.aptana.scripting;

import junit.framework.TestCase;

public class BundleConverterTest extends TestCase
{
	public void testConvertkeybindingTurnsShiftPlusLowercaseLetterIntoUppercaseLetter() throws Exception
	{
		assertEquals("CONTROL+Q", BundleConverter.convertKeyBinding("^q"));
	}
	
	public void testConvertkeybindingF5() throws Exception
	{
		assertEquals("F5", BundleConverter.convertKeyBinding(""));
	}
}
