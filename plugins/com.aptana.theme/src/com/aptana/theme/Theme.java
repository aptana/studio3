/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.theme;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import com.aptana.scope.IScopeSelector;
import com.aptana.scope.ScopeSelector;
import com.aptana.theme.internal.ThemeManager;

/**
 * Reads in the theme from a java properties file. Intentionally similar to the Textmate themes. keys are token types,
 * values are comma delimited with hex colors and font style keywords. First hex color becomes FG, second becomes BG (if
 * there).
 * 
 * @author cwilliams
 */
public class Theme
{

	static final String DELIMETER = ","; //$NON-NLS-1$

	private static final String UNDERLINE = "underline"; //$NON-NLS-1$
	private static final String BOLD = "bold"; //$NON-NLS-1$
	private static final String ITALIC = "italic"; //$NON-NLS-1$

	public static final String THEME_NAME_PROP_KEY = "name"; //$NON-NLS-1$
	public static final String THEME_EXTENDS_PROP_KEY = "extends_theme"; //$NON-NLS-1$
	static final String FOREGROUND_PROP_KEY = "foreground"; //$NON-NLS-1$
	private static final String BACKGROUND_PROP_KEY = "background"; //$NON-NLS-1$
	private static final String SELECTION_PROP_KEY = "selection"; //$NON-NLS-1$
	private static final String LINE_HIGHLIGHT_PROP_KEY = "lineHighlight"; //$NON-NLS-1$
	private static final String CARET_PROP_KEY = "caret"; //$NON-NLS-1$

	private Map<IScopeSelector, DelayedTextAttribute> coloringRules;
	private ColorManager colorManager;
	private RGB defaultFG;
	private RGBa lineHighlight;
	private RGB defaultBG;
	private RGBa selection;
	private RGB caret;
	private String name;

	private RGB searchResultBG;

	/**
	 * Used for recursion in getDelayedTextAttribute to avoid matching same rule on scope twice
	 */
	private IScopeSelector lastSelectorMatch;

	/**
	 * A cache to memoize the ultimate TextAttribute generated for a given fully qualified scope.
	 */
	private Map<String, TextAttribute> cache;

	public Theme(ColorManager colormanager, Properties props)
	{
		this.colorManager = colormanager;
		coloringRules = new HashMap<IScopeSelector, DelayedTextAttribute>();
		cache = new HashMap<String, TextAttribute>();
		parseProps(props);
		storeDefaults();
	}

	private void parseProps(Properties props)
	{
		name = (String) props.remove(THEME_NAME_PROP_KEY);
		if (name == null)
		{
			throw new IllegalStateException("Invalid theme properties!"); //$NON-NLS-1$
		}
		// The general editor colors
		defaultFG = parseHexRGB((String) props.remove(FOREGROUND_PROP_KEY));
		defaultBG = parseHexRGB((String) props.remove(BACKGROUND_PROP_KEY));
		lineHighlight = parseHexRGBa((String) props.remove(LINE_HIGHLIGHT_PROP_KEY));
		selection = parseHexRGBa((String) props.remove(SELECTION_PROP_KEY));
		caret = parseHexRGB((String) props.remove(CARET_PROP_KEY), true);

		for (Entry<Object, Object> entry : props.entrySet())
		{
			String scopeSelector = (String) entry.getKey();
			int style = SWT.NORMAL;
			RGBa foreground = null;
			RGBa background = null;
			List<String> values = tokenize((String) entry.getValue());
			// Handle empty fg with a bg color! If first token is just an empty value followed by a comma
			int num = 0;
			boolean skipFG = false;
			for (String token : values)
			{
				token = token.trim();
				if (token.length() == 0 && num == 0)
				{
					// empty fg!
					skipFG = true;
				}
				else if (token.startsWith("#")) //$NON-NLS-1$
				{
					// it's a color!
					if (foreground == null && !skipFG)
					{
						foreground = parseHexRGBa(token);
					}
					else
					{
						background = parseHexRGBa(token);
					}
				}
				else
				{
					if (token.equalsIgnoreCase(ITALIC))
					{
						style |= SWT.ITALIC;
					}
					else if (token.equalsIgnoreCase(UNDERLINE))
					{
						style |= TextAttribute.UNDERLINE;
					}
					else if (token.equalsIgnoreCase(BOLD))
					{
						style |= SWT.BOLD;
					}
				}
				num++;
			}
			DelayedTextAttribute attribute = new DelayedTextAttribute(foreground, background, style);
			coloringRules.put(new ScopeSelector(scopeSelector), attribute);
		}
	}

	private List<String> tokenize(String value)
	{
		List<String> tokens = new ArrayList<String>();
		if (!value.contains(DELIMETER))
		{
			tokens.add(value);
			return tokens;
		}
		return Arrays.asList(value.split(DELIMETER));
	}

	private RGB parseHexRGB(String hex)
	{
		return parseHexRGB(hex, false);
	}

	private RGB parseHexRGB(String hex, boolean alphaMergeWithBG)
	{
		RGBa a = parseHexRGBa(hex);
		RGB rgb = a.toRGB();
		if (alphaMergeWithBG)
		{
			// Handle RGBa values by mixing against BG, etc
			return alphaBlend(defaultBG, rgb, a.getAlpha());
		}
		return rgb;
	}

	private RGBa parseHexRGBa(String hex)
	{
		if (hex == null)
			return new RGBa(0, 0, 0);
		if (hex.length() != 7 && hex.length() != 9)
		{
			ThemePlugin.logError(MessageFormat.format("Received RGBa Hex value with invalid length: {0}", hex), null); //$NON-NLS-1$
			if (defaultFG != null)
			{
				return new RGBa(defaultFG);
			}
			return new RGBa(0, 0, 0);
		}
		String s = hex.substring(1, 3);
		int r = Integer.parseInt(s, 16);
		s = hex.substring(3, 5);
		int g = Integer.parseInt(s, 16);
		s = hex.substring(5, 7);
		int b = Integer.parseInt(s, 16);
		if (hex.length() == 9)
		{
			s = hex.substring(7, 9);
			int a = Integer.parseInt(s, 16);
			return new RGBa(r, g, b, a);
		}
		return new RGBa(r, g, b);
	}

	public static RGB alphaBlend(RGB base, RGB top, int alpha)
	{
		int newRed = alphaBlend(base.red, top.red, alpha);
		int newGreen = alphaBlend(base.green, top.green, alpha);
		int newBlue = alphaBlend(base.blue, top.blue, alpha);
		return new RGB(newRed, newGreen, newBlue);
	}

	private static int alphaBlend(int base, int top, int alpha)
	{
		int oneMinusAlpha = 255 - alpha;
		int r = oneMinusAlpha * base + alpha * top + 128;
		return ((r + (r >> 8)) >> 8);
	}

	public TextAttribute getTextAttribute(String scope)
	{
		if (cache.containsKey(scope))
		{
			return cache.get(scope);
		}
		lastSelectorMatch = null;
		TextAttribute ta = toTextAttribute(getDelayedTextAttribute(scope), true);
		cache.put(scope, ta);
		return ta;
	}

	private DelayedTextAttribute getDelayedTextAttribute(String scope)
	{
		IScopeSelector match = findMatch(scope);
		if (match != null)
		{
			// This is to avoid matching the same selector multiple times when recursing up the scope! Basically our
			// match may have been many steps up our scope, not at the end!
			if (lastSelectorMatch != null && lastSelectorMatch.equals(match))
			{
				// We just matched the same rule! We need to recurse from parent scope!
				return getParent(scope);
			}
			lastSelectorMatch = match;
			DelayedTextAttribute attr = coloringRules.get(match);

			// if our coloring has no background, we should use parent's. If it has some opacity (alpha != 255), we
			// need to alpha blend
			if (attr.getBackground() == null || !attr.getBackground().isFullyOpaque())
			{
				// Need to merge bg color up the scope!
				DelayedTextAttribute parentAttr = getParent(scope);
				// Now do actual merge
				attr = merge(attr, parentAttr);
			}
			return attr;
		}

		// Some tokens are special. They have fallbacks even if not in the theme! Looks like bundles can contribute
		// them?
		if (new ScopeSelector("markup.changed").matches(scope)) //$NON-NLS-1$
		{
			return new DelayedTextAttribute(new RGBa(255, 255, 255), new RGBa(248, 205, 14), SWT.NORMAL);
		}
		if (new ScopeSelector("markup.deleted").matches(scope)) //$NON-NLS-1$
		{
			return new DelayedTextAttribute(new RGBa(255, 255, 255), new RGBa(255, 86, 77), SWT.NORMAL);
		}
		if (new ScopeSelector("markup.inserted").matches(scope)) //$NON-NLS-1$
		{
			return new DelayedTextAttribute(new RGBa(0, 0, 0), new RGBa(128, 250, 120), SWT.NORMAL);
		}
		if (new ScopeSelector("markup.underline").matches(scope)) //$NON-NLS-1$
		{
			return new DelayedTextAttribute(null, null, TextAttribute.UNDERLINE);
		}
		if (new ScopeSelector("markup.bold").matches(scope)) //$NON-NLS-1$
		{
			return new DelayedTextAttribute(null, null, SWT.BOLD);
		}
		if (new ScopeSelector("markup.italic").matches(scope)) //$NON-NLS-1$
		{
			return new DelayedTextAttribute(null, null, SWT.ITALIC);
		}
		if (new ScopeSelector("meta.diff.index").matches(scope) || new ScopeSelector("meta.diff.range").matches(scope) || new ScopeSelector("meta.separator.diff").matches(scope)) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		{
			return new DelayedTextAttribute(new RGBa(255, 255, 255), new RGBa(65, 126, 218), SWT.ITALIC);
		}
		if (new ScopeSelector("meta.diff.header").matches(scope)) //$NON-NLS-1$
		{
			return new DelayedTextAttribute(new RGBa(255, 255, 255), new RGBa(103, 154, 233), SWT.NORMAL);
		}
		if (new ScopeSelector("meta.separator").matches(scope)) //$NON-NLS-1$
		{
			return new DelayedTextAttribute(new RGBa(255, 255, 255), new RGBa(52, 103, 209), SWT.NORMAL);
		}
		return new DelayedTextAttribute(new RGBa(defaultFG));
	}

	protected DelayedTextAttribute getParent(String scope)
	{
		DelayedTextAttribute parentAttr = null;
		int index = scope.lastIndexOf(' ');
		if (index != -1)
		{
			String subType = scope.substring(0, index);
			parentAttr = getDelayedTextAttribute(subType);
		}
		if (parentAttr == null)
		{
			// If we never find a parent, use default bg
			parentAttr = new DelayedTextAttribute(new RGBa(defaultFG), new RGBa(defaultBG), 0);
		}
		return parentAttr;
	}

	private IScopeSelector findMatch(String scope)
	{
		return ScopeSelector.bestMatch(coloringRules.keySet(), scope);
	}

	private DelayedTextAttribute merge(DelayedTextAttribute childAttr, DelayedTextAttribute parentAttr)
	{
		return new DelayedTextAttribute(merge(childAttr.getForeground(), parentAttr.getForeground(), defaultFG), merge(
				childAttr.getBackground(), parentAttr.getBackground(), defaultBG), childAttr.getStyle()
				| parentAttr.getStyle());
	}

	private RGBa merge(RGBa top, RGBa bottom, RGB defaultParent)
	{
		if (top == null && bottom == null)
		{
			return new RGBa(defaultParent);
		}
		if (top == null) // for some reason there is no top.
		{
			return bottom;
		}
		if (top.isFullyOpaque()) // top has no transparency, just return it
		{
			return top;
		}
		if (bottom == null) // there is no parent, merge onto default FG/BG for theme
		{
			return new RGBa(alphaBlend(defaultParent, top.toRGB(), top.getAlpha()));
		}
		return new RGBa(alphaBlend(bottom.toRGB(), top.toRGB(), top.getAlpha()));
	}

	private TextAttribute toTextAttribute(DelayedTextAttribute attr, boolean forceColor)
	{
		Color fg = null;
		if (attr.getForeground() != null || forceColor)
		{
			fg = colorManager.getColor(merge(attr.getForeground(), null, defaultFG).toRGB());
		}
		Color bg = null;
		if (attr.getBackground() != null || forceColor)
		{
			bg = colorManager.getColor(merge(attr.getBackground(), null, defaultBG).toRGB());
		}
		return new TextAttribute(fg, bg, attr.getStyle());
	}

	public RGB getBackground()
	{
		return defaultBG;
	}

	public RGBa getSelection()
	{
		return selection;
	}

	public RGB getForeground()
	{
		return defaultFG;
	}

	public RGBa getLineHighlight()
	{
		return lineHighlight;
	}

	public RGB getCaret()
	{
		return caret;
	}

	public String getName()
	{
		return name;
	}

	public Map<String, TextAttribute> getTokens()
	{
		Map<String, TextAttribute> tokens = new HashMap<String, TextAttribute>();
		for (Map.Entry<IScopeSelector, DelayedTextAttribute> entry : coloringRules.entrySet())
		{
			tokens.put(entry.getKey().toString(), toTextAttribute(entry.getValue(), false));
		}
		return tokens;
	}

	/**
	 * Updates the TextAttribute for a token and immediately saves the theme. TODO take in a ScopeSelector, not a
	 * String!
	 * 
	 * @param scopeSelector
	 * @param at
	 */
	public void update(String scopeSelector, TextAttribute at)
	{
		RGBa fg = null;
		if (at.getForeground() != null)
		{
			fg = new RGBa(at.getForeground().getRGB());
		}
		RGBa bg = null;
		if (at.getBackground() != null)
		{
			bg = new RGBa(at.getBackground().getRGB());
		}
		coloringRules.put(new ScopeSelector(scopeSelector), new DelayedTextAttribute(fg, bg, at.getStyle()));
		wipeCache();
		save();
	}

	public Properties toProps()
	{
		Properties props = new Properties();
		props.put(THEME_NAME_PROP_KEY, getName());
		props.put(SELECTION_PROP_KEY, toHex(getSelection()));
		props.put(LINE_HIGHLIGHT_PROP_KEY, toHex(getLineHighlight()));
		props.put(FOREGROUND_PROP_KEY, toHex(getForeground()));
		props.put(BACKGROUND_PROP_KEY, toHex(getBackground()));
		props.put(CARET_PROP_KEY, toHex(caret));
		for (Map.Entry<IScopeSelector, DelayedTextAttribute> entry : coloringRules.entrySet())
		{
			if (entry.getKey() == null)
				continue;
			StringBuilder value = new StringBuilder();
			DelayedTextAttribute attr = entry.getValue();
			RGBa color = attr.getForeground();
			if (color != null)
			{
				value.append(toHex(color)).append(DELIMETER);
			}
			color = attr.getBackground();
			if (color != null)
			{
				value.append(toHex(color)).append(DELIMETER);
			}
			int style = attr.getStyle();
			if ((style & SWT.ITALIC) != 0)
			{
				value.append(ITALIC).append(DELIMETER);
			}
			if ((style & TextAttribute.UNDERLINE) != 0)
			{
				value.append(UNDERLINE).append(DELIMETER);
			}
			if ((style & SWT.BOLD) != 0)
			{
				value.append(BOLD).append(DELIMETER);
			}
			if (value.length() > 0)
			{
				value.deleteCharAt(value.length() - 1);
			}
			if (value.length() == 0)
				continue;
			props.put(entry.getKey().toString(), value.toString());
		}
		return props;
	}

	private String toHex(RGBa color)
	{
		String rgbString = toHex(color.toRGB());
		if (color.getAlpha() == 0)
		{
			return rgbString;
		}
		return rgbString + pad(Integer.toHexString(color.getAlpha()), 2, '0');
	}

	private String toHex(RGB rgb)
	{
		return MessageFormat.format("#{0}{1}{2}", pad(Integer.toHexString(rgb.red), 2, '0'), pad(Integer //$NON-NLS-1$
				.toHexString(rgb.green), 2, '0'), pad(Integer.toHexString(rgb.blue), 2, '0'));
	}

	private String pad(String string, int desiredLength, char padChar)
	{
		while (string.length() < desiredLength)
			string = padChar + string;
		return string;
	}

	protected void storeDefaults()
	{
		// Only save to defaults if it has never been saved there. Basically take a snapshot of first version and
		// use that as the "default"
		IEclipsePreferences prefs = new DefaultScope().getNode(ThemePlugin.PLUGIN_ID);
		if (prefs == null)
			return; // TODO Log something?
		Preferences preferences = prefs.node(ThemeManager.THEMES_NODE);
		if (preferences == null)
			return;
		String value = preferences.get(getName(), null);
		if (value == null)
		{
			save(new DefaultScope());
		}
	}

	public void save()
	{
		save(new InstanceScope());
		if (getThemeManager().getCurrentTheme().equals(this))
		{
			getThemeManager().setCurrentTheme(this);
		}
	}

	protected IThemeManager getThemeManager()
	{
		return ThemePlugin.getDefault().getThemeManager();
	}

	private void save(IScopeContext scope)
	{
		try
		{
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			toProps().storeToXML(os, null);
			IEclipsePreferences prefs = scope.getNode(ThemePlugin.PLUGIN_ID);
			Preferences preferences = prefs.node(ThemeManager.THEMES_NODE);
			preferences.put(getName(), os.toString());
			prefs.flush();
		}
		catch (Exception e)
		{
			ThemePlugin.logError(e);
		}
	}

	public void loadFromDefaults() throws InvalidPropertiesFormatException, UnsupportedEncodingException, IOException
	{
		IEclipsePreferences prefs = new DefaultScope().getNode(ThemePlugin.PLUGIN_ID);
		Preferences preferences = prefs.node(ThemeManager.THEMES_NODE);
		String xmlProps = preferences.get(getName(), null);
		if (xmlProps == null)
		{
			return;
		}
		Properties props = new Properties();
		props.loadFromXML(new ByteArrayInputStream(xmlProps.getBytes("UTF-8"))); //$NON-NLS-1$
		coloringRules.clear();
		wipeCache();
		parseProps(props);
		deleteCustomVersion();
	}

	/**
	 * Removes the saved instance version of theme.
	 */
	private void deleteCustomVersion()
	{
		delete(new InstanceScope());
	}

	private void deleteDefaultVersion()
	{
		delete(new DefaultScope());
	}

	private void delete(IScopeContext context)
	{
		try
		{
			IEclipsePreferences prefs = context.getNode(ThemePlugin.PLUGIN_ID);
			Preferences preferences = prefs.node(ThemeManager.THEMES_NODE);
			preferences.remove(getName());
			preferences.flush();
		}
		catch (BackingStoreException e)
		{
			ThemePlugin.logError(e);
		}
	}

	/**
	 * Removes a scope selector rule from the theme. TODO take in a ScopeSelector, not a String!
	 * 
	 * @param scopeSelector
	 */
	public void remove(String scopeSelector)
	{
		coloringRules.remove(new ScopeSelector(scopeSelector));
		wipeCache();
		save();
	}

	/**
	 * Adds a new token entry with no font styling, no bg, same FG as default for theme. TODO take in a ScopeSelector,
	 * not a String!
	 */
	public void addNewDefaultToken(String scopeSelector)
	{
		DelayedTextAttribute attr = new DelayedTextAttribute(null);
		coloringRules.put(new ScopeSelector(scopeSelector), attr);
		wipeCache();
		save();
	}

	public void updateCaret(RGB newColor)
	{
		if (newColor == null || (caret != null && caret.equals(newColor)))
		{
			return;
		}
		caret = newColor;
		save();
	}

	public void updateFG(RGB newColor)
	{
		if (newColor == null || (defaultFG != null && defaultFG.equals(newColor)))
		{
			return;
		}
		defaultFG = newColor;
		save();
	}

	public void updateBG(RGB newColor)
	{
		if (newColor == null || (defaultBG != null && defaultBG.equals(newColor)))
		{
			return;
		}
		wipeCache();
		defaultBG = newColor;
		save();
	}

	private void wipeCache()
	{
		cache.clear();
	}

	public void updateLineHighlight(RGB newColor)
	{
		if (newColor == null || (lineHighlight != null && lineHighlight.toRGB().equals(newColor)))
		{
			return;
		}
		lineHighlight = new RGBa(newColor);
		save();
	}

	public void updateSelection(RGB newColor)
	{
		if (newColor == null || (selection != null && selection.toRGB().equals(newColor)))
		{
			return;
		}
		selection = new RGBa(newColor);
		save();
	}

	public Theme copy(String value)
	{
		if (value == null)
		{
			return null;
		}
		Properties props = toProps();
		props.setProperty(THEME_NAME_PROP_KEY, value);
		Theme newTheme = new Theme(colorManager, props);
		addTheme(newTheme);
		return newTheme;
	}

	protected void addTheme(Theme newTheme)
	{
		getThemeManager().addTheme(newTheme);
	}

	public void delete()
	{
		removeTheme();
		deleteCustomVersion();
		deleteDefaultVersion();
	}

	protected void removeTheme()
	{
		getThemeManager().removeTheme(this);
	}

	/**
	 * Determines if the theme defines this exact token type (not checking parents by dropping periods).
	 * 
	 * @param scopeSelector
	 * @return
	 */
	public boolean hasEntry(String scopeSelector)
	{
		return coloringRules.containsKey(new ScopeSelector(scopeSelector));
	}

	public Color getForeground(String scope)
	{
		TextAttribute attr = getTextAttribute(scope);
		if (attr == null)
		{
			return null;
		}
		return attr.getForeground();
	}

	/**
	 * Returns the RGB value for the foreground of a specific token.
	 * 
	 * @param string
	 * @return
	 */
	public RGB getForegroundAsRGB(String scope)
	{
		Color fg = getForeground(scope);
		if (fg == null)
		{
			return null;
		}
		return fg.getRGB();
	}

	public Color getBackground(String scope)
	{
		TextAttribute attr = getTextAttribute(scope);
		if (attr == null)
		{
			return null;
		}
		return attr.getBackground();
	}

	/**
	 * Returns the RGB value for the background of a specific token.
	 * 
	 * @param string
	 * @return
	 */
	public RGB getBackgroundAsRGB(String scope)
	{
		Color bg = getBackground(scope);
		if (bg == null)
		{
			return null;
		}
		return bg.getRGB();
	}

	public RGB getSearchResultColor()
	{
		if (searchResultBG == null)
		{
			searchResultBG = isDark(getSelectionAgainstBG()) ? lighten(getSelectionAgainstBG())
					: darken(getSelectionAgainstBG());
		}
		return searchResultBG;
	}

	private RGB lighten(RGB color)
	{
		float[] hsb = color.getHSB();
		return new RGB(hsb[0], hsb[1], (float) (hsb[2] + 0.15));
	}

	private RGB darken(RGB color)
	{
		float[] hsb = color.getHSB();
		return new RGB(hsb[0], hsb[1], (float) (hsb[2] - 0.15));
	}

	public boolean hasDarkBG()
	{
		return isDark(getBackground());
	}

	public boolean hasLightFG()
	{
		return !isDark(getForeground());
	}

	private boolean isDark(RGB color)
	{
		// Convert to grayscale
		double grey = 0.3 * color.red + 0.59 * color.green + 0.11 * color.blue;
		return grey <= 128;
	}

	/**
	 * Returns the selection color alpha blended with the theme bg to give a good estimate of correct RGB value
	 * 
	 * @return
	 */
	public RGB getSelectionAgainstBG()
	{
		return alphaBlend(defaultBG, selection.toRGB(), selection.getAlpha());
	}

	/**
	 * Returns the line highlight color alpha blended with the theme bg to give a good estimate of correct RGB value
	 * 
	 * @return
	 */
	public RGB getLineHighlightAgainstBG()
	{
		return alphaBlend(defaultBG, lineHighlight.toRGB(), lineHighlight.getAlpha());
	}

}
