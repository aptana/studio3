/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.theme;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.junit.Before;
import org.junit.Test;

import com.aptana.scope.ScopeSelector;

@SuppressWarnings("nls")
public class ThemeTest
{

	private Theme theme;
	private ColorManager colormanager;

	@Before
	public void setUp() throws Exception
	{
		Map<String, String> props = new LinkedHashMap<String, String>();
		props.put("background", "#ffffff");
		props.put("foreground", "#ff0000");
		props.put("caret", "#00ff00");
		props.put("selection", "#0000ff");
		props.put("lineHighlight", "#ff00ff");
		props.put("name", "chris");
		props.put("constant", "#00ff00,#ff00ff,italic");
		props.put("constant.language.js", "#000000,#ff0000,bold");
		colormanager = new ColorManager();
		theme = new Theme(colormanager, props)
		{
			@Override
			protected void storeDefaults()
			{
				// do nothing
			}

			@Override
			public void save()
			{
				// do nothing
			}

			protected void addTheme(Theme newTheme)
			{
				// do nothing
			};
		};
	}

	@Test
	public void testBasics()
	{
		assertEquals("chris", theme.getName());
		assertBasics(theme);
	}

	private void assertBasics(Theme theme)
	{
		assertEquals(new RGB(255, 255, 255), theme.getBackground());
		assertEquals(new RGB(255, 0, 0), theme.getForeground());
		assertEquals(new RGB(0, 255, 0), theme.getCaret());
		assertEquals(new RGBa(0, 0, 255, 255), theme.getSelection());
		assertEquals(new RGBa(255, 0, 255, 255), theme.getLineHighlight());
		// Now check tokens
		assertTrue(theme.hasEntry("constant.language.js"));
		assertFalse(theme.hasEntry("constant.language"));
		assertTrue(theme.hasEntry("constant"));

		// Check "constant" token colors
		assertEquals(new RGB(0, 255, 0), theme.getTextAttribute("constant").getForeground().getRGB());
		assertEquals(new RGB(0, 255, 0), theme.getForegroundAsRGB("constant"));
		assertEquals(new RGB(255, 0, 255), theme.getTextAttribute("constant").getBackground().getRGB());
		assertEquals(new RGB(255, 0, 255), theme.getBackgroundAsRGB("constant"));
		// "constant.langauge inherits "constant" token's colors
		assertEquals(theme.getTextAttribute("constant"), theme.getTextAttribute("constant.language"));
		assertEquals(new RGB(0, 255, 0), theme.getForegroundAsRGB("constant.language"));
		assertEquals(new RGB(255, 0, 255), theme.getBackgroundAsRGB("constant.language"));
		// "constant.language.js" has overridden parents, so it has it's own colors
		assertEquals(new RGB(0, 0, 0), theme.getTextAttribute("constant.language.js").getForeground().getRGB());
		assertEquals(new RGB(0, 0, 0), theme.getForegroundAsRGB("constant.language.js"));
		assertEquals(new RGB(255, 0, 0), theme.getTextAttribute("constant.language.js").getBackground().getRGB());
		assertEquals(new RGB(255, 0, 0), theme.getBackgroundAsRGB("constant.language.js"));

	}

	@Test
	public void testAddingTokens()
	{
		assertFalse(theme.hasEntry("chris"));
		theme.addNewDefaultToken(0, "chris");
		ThemeRule rule = theme.getTokens().get(0);
		theme.updateRule(0, rule.setScopeSelector(new ScopeSelector("chris")));
		assertTrue(theme.hasEntry("chris"));
		assertEquals(new RGB(255, 0, 0), theme.getForegroundAsRGB("chris"));
	}

	@Test
	public void testModifyingTokens()
	{
		ThemeRule rule = theme.getRuleForSelector(new ScopeSelector("constant"));
		int index = theme.getTokens().indexOf(rule);
		theme.updateRule(index,
				rule.setTextAttribute(new RGBa(128, 128, 128), new RGBa(64, 0, 64), TextAttribute.UNDERLINE));
		assertEquals(new RGB(128, 128, 128), theme.getForegroundAsRGB("constant.language"));
		assertEquals(new RGB(64, 0, 64), theme.getBackgroundAsRGB("constant.language"));
	}

	@Test
	public void testUpdateGlobalBGColor()
	{
		theme.updateBG(new RGB(128, 128, 128));
		assertEquals(new RGB(128, 128, 128), theme.getBackground());
		theme.updateBG(null);
		assertEquals(new RGB(128, 128, 128), theme.getBackground());
	}

	@Test
	public void testGetBackAsRGBReturnsThemeBackgroundIfNoBackGroundSpecified()
	{
		assertEquals(theme.getBackground(), theme.getBackgroundAsRGB("something.that.inherits"));
	}

	@Test
	public void testUpdateGlobalFGColor()
	{
		theme.updateFG(new RGB(128, 128, 128));
		assertEquals(new RGB(128, 128, 128), theme.getForeground());
		theme.updateFG(null);
		assertEquals(new RGB(128, 128, 128), theme.getForeground());
		assertEquals(new RGB(128, 128, 128), theme.getForegroundAsRGB("something.that.inherits"));
	}

	@Test
	public void testUpdateGlobalCaretColor()
	{
		theme.updateCaret(new RGB(128, 128, 128));
		assertEquals(new RGB(128, 128, 128), theme.getCaret());
		theme.updateCaret(null);
		assertEquals(new RGB(128, 128, 128), theme.getCaret());
	}

	@Test
	public void testUpdateGlobalLineHighlightColor()
	{
		theme.updateLineHighlight(new RGB(128, 128, 128));
		assertEquals(new RGBa(128, 128, 128, 255), theme.getLineHighlight());
		theme.updateLineHighlight(null);
		assertEquals(new RGBa(128, 128, 128, 255), theme.getLineHighlight());
	}

	@Test
	public void testUpdateGlobalSelectionColor()
	{
		theme.updateSelection(new RGB(128, 128, 128));
		assertEquals(new RGBa(128, 128, 128, 255), theme.getSelection());
		theme.updateSelection(null);
		assertEquals(new RGBa(128, 128, 128, 255), theme.getSelection());
	}

	@Test
	public void testGetTokens()
	{
		List<ThemeRule> tokens = theme.getTokens();
		assertEquals(2, tokens.size());

		assertTrue(tokens.contains(new ThemeRule("constant", new ScopeSelector("constant"), new DelayedTextAttribute(
				new RGBa(0, 255, 0), new RGBa(255, 0, 255), SWT.ITALIC))));
		assertTrue(tokens.contains(new ThemeRule("constant.language.js", new ScopeSelector("constant.language.js"),
				new DelayedTextAttribute(new RGBa(0, 0, 0), new RGBa(255, 0, 0), SWT.BOLD))));

		assertFalse(tokens.contains(new ThemeRule("whatever", new ScopeSelector("whatever"), new DelayedTextAttribute(
				new RGBa(0, 0, 0), new RGBa(255, 0, 0), SWT.BOLD))));
	}

	@Test
	public void testCopy()
	{
		// TODO What if we try to copy with a name that's already taken!
		Theme copy = theme.copy("chris_copy");
		assertEquals("chris_copy", copy.getName());
		assertBasics(copy);
	}

	@Test
	public void testCopyWithNullArgument()
	{
		assertNull(theme.copy(null));
	}

	// TODO Add test for delete

	@Test
	public void testRemove()
	{
		// Now check tokens
		assertTrue(theme.hasEntry("constant.language.js"));
		assertTrue(theme.hasEntry("constant"));

		// Check "constant" token colors
		assertEquals(new RGB(0, 255, 0), theme.getTextAttribute("constant").getForeground().getRGB());
		assertEquals(new RGB(0, 255, 0), theme.getForegroundAsRGB("constant"));
		assertEquals(new RGB(255, 0, 255), theme.getTextAttribute("constant").getBackground().getRGB());
		assertEquals(new RGB(255, 0, 255), theme.getBackgroundAsRGB("constant"));
		// "constant.language.js" has overridden parents, so it has it's own colors
		assertEquals(new RGB(0, 0, 0), theme.getTextAttribute("constant.language.js").getForeground().getRGB());
		assertEquals(new RGB(0, 0, 0), theme.getForegroundAsRGB("constant.language.js"));
		assertEquals(new RGB(255, 0, 0), theme.getTextAttribute("constant.language.js").getBackground().getRGB());
		assertEquals(new RGB(255, 0, 0), theme.getBackgroundAsRGB("constant.language.js"));

		ThemeRule rule = theme.getRuleForSelector(new ScopeSelector("constant.language.js"));
		theme.remove(rule);

		// Now check tokens
		assertFalse(theme.hasEntry("constant.language.js"));
		assertTrue(theme.hasEntry("constant"));

		// Check "constant" token colors
		assertEquals(new RGB(0, 255, 0), theme.getTextAttribute("constant").getForeground().getRGB());
		assertEquals(new RGB(0, 255, 0), theme.getForegroundAsRGB("constant"));
		assertEquals(new RGB(255, 0, 255), theme.getTextAttribute("constant").getBackground().getRGB());
		assertEquals(new RGB(255, 0, 255), theme.getBackgroundAsRGB("constant"));
		// "constant.language.js" is removed, so it should pick up "constant"'s colors now
		assertEquals(new RGB(0, 255, 0), theme.getTextAttribute("constant.language.js").getForeground().getRGB());
		assertEquals(new RGB(0, 255, 0), theme.getForegroundAsRGB("constant.language.js"));
		assertEquals(new RGB(255, 0, 255), theme.getTextAttribute("constant.language.js").getBackground().getRGB());
		assertEquals(new RGB(255, 0, 255), theme.getBackgroundAsRGB("constant.language.js"));
	}

	@Test
	public void testDarkenDoesntGoOutOfRange()
	{
		assertEquals(new RGB(0, 0, 0), theme.darken(new RGB(0, 0, 0)));
	}

	@Test
	public void testLightenDoesntGoOutOfRange()
	{
		assertEquals(new RGB(255, 255, 255), theme.lighten(new RGB(255, 255, 255)));
	}

	@Test
	public void testDarken()
	{
		// TODO Keep darkening until we hit black?
		assertEquals(new RGB(90, 90, 90), theme.darken(new RGB(128, 128, 128)));
	}

	@Test
	public void testLighten()
	{
		// TODO Keep lightening until we hit white?
		assertEquals(new RGB(166, 166, 166), theme.lighten(new RGB(128, 128, 128)));
	}

	@Test
	public void testAPSTUD2790_Doctype()
	{
		// Empty the theme
		for (ThemeRule entry : theme.getTokens())
		{
			theme.remove(entry);
		}

		theme.addNewRule(0, "Tag name", new ScopeSelector("entity.name.tag"), new DelayedTextAttribute(
				new RGBa(0, 0, 0)));
		theme.addNewRule(
				1,
				"Doctype",
				new ScopeSelector(
						"entity.name.tag.doctype.html, meta.tag.sgml.html, string.quoted.double.doctype.identifiers-and-DTDs.html"),
				new DelayedTextAttribute(new RGBa(255, 255, 255)));

		ThemeRule rule = theme
				.winningRule("text.html.basic meta.tag.sgml.html meta.tag.sgml.doctype.html entity.name.tag.doctype.html");

		assertEquals("Doctype", rule.getName());
	}

	@Test
	public void testAPSTUD2790_CSSBody()
	{
		// Empty the theme
		for (ThemeRule entry : theme.getTokens())
		{
			theme.remove(entry);
		}

		theme.addNewRule(0, "CSS: Property", new ScopeSelector(
				"entity.name.tag.css, support.type.property-name.css, meta.property-name.css"),
				new DelayedTextAttribute(new RGBa(0, 0, 0)));
		theme.addNewRule(1, "CSS: Tag", new ScopeSelector("entity.name.tag.css"), new DelayedTextAttribute(new RGBa(
				255, 255, 255)));

		String scope = "source.css entity.name.tag.css";
		ThemeRule rule = theme.winningRule(scope);
		assertEquals("CSS: Tag", rule.getName());
	}

}
