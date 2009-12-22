package com.aptana.editor.common.theme;

import java.util.Properties;

import junit.framework.TestCase;

import org.eclipse.swt.graphics.RGB;

public class ThemeTest extends TestCase
{

	public void testBasics()
	{
		Properties props = new Properties();
		props.put("background", "#ffffff");
		props.put("foreground", "#ff0000");
		props.put("caret", "#00ff00");
		props.put("selection", "#0000ff");
		props.put("lineHighlight", "#ff00ff");
		props.put("name", "chris");
		props.put("constant", "#00ff00,#ff00ff,italic");
		props.put("constant.language.js", "#000000,#ff0000,bold");
		Theme theme = new Theme(new ColorManager(), props)
		{
			@Override
			protected void storeDefaults()
			{
				// do nothing
			}
		};
		assertEquals(new RGB(255, 255, 255), theme.getBackground());
		assertEquals(new RGB(255, 0, 0), theme.getForeground());
		assertEquals(new RGB(0, 255, 0), theme.getCaret());
		assertEquals(new RGB(0, 0, 255), theme.getSelection());
		assertEquals(new RGB(255, 0, 255), theme.getLineHighlight());
		assertEquals("chris", theme.getName());
		// Now check tokens
		assertTrue(theme.hasEntry("constant.language.js"));
		assertFalse(theme.hasEntry("constant.language"));
		assertTrue(theme.hasEntry("constant"));

		// Check "constant" token colors
		assertEquals(new RGB(0, 255, 0), theme.getTextAttribute("constant").getForeground().getRGB());
		assertEquals(new RGB(255, 0, 255), theme.getTextAttribute("constant").getBackground().getRGB());
		// "constant.langauge inherits "constant" token's colors
		assertEquals(theme.getTextAttribute("constant"), theme.getTextAttribute("constant.language"));
		// "constant.langauge.js" has overridden parents, so it has it's own colors
		assertEquals(new RGB(0, 0, 0), theme.getTextAttribute("constant.language.js").getForeground().getRGB());
		assertEquals(new RGB(255, 0, 0), theme.getTextAttribute("constant.language.js").getBackground().getRGB());
	}
	// TODO Add tests for copy, delete, addNeDefaultToken, update methods
}
