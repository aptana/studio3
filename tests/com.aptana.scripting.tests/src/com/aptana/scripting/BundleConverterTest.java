package com.aptana.scripting;

import junit.framework.TestCase;

public class BundleConverterTest extends TestCase
{
	public void testConvertkeybindingTurnsShiftPlusLowercaseLetterIntoUppercaseLetter() throws Exception
	{
		assertEquals("M4+Q", BundleConverter.convertKeyBinding("^q"));
	}
	
	public void testConvertkeybindingF1() throws Exception
	{
		assertEquals("F1", BundleConverter.convertKeyBinding(""));
	}
	
	public void testConvertkeybindingF3() throws Exception
	{
		assertEquals("F3", BundleConverter.convertKeyBinding(""));
	}
	
	public void testConvertkeybindingF5() throws Exception
	{
		assertEquals("F5", BundleConverter.convertKeyBinding(""));
	}
	
	public void testAltEscape() throws Exception
	{
		assertEquals("M3+ESCAPE", BundleConverter.convertKeyBinding("~"));
	}
	
	public void testShiftReturn() throws Exception
	{
		assertEquals("M2+ENTER", BundleConverter.convertKeyBinding("$\n"));
	}
	
	public void testControlAltDelete() throws Exception
	{
		assertEquals("M4+M3+DEL", BundleConverter.convertKeyBinding("^~"));
	}
	
}
