/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.theme;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

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

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.IOUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.scope.IScopeSelector;
import com.aptana.scope.ISelectorNode;
import com.aptana.scope.ScopeSelector;
import com.aptana.theme.internal.OrderedProperties;
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

	/**
	 * Delimiter used to append the scope selector after the fg/bg/fontStyle for a rule.
	 */
	static final String SELECTOR_DELIMITER = "^"; //$NON-NLS-1$

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

	private List<ThemeRule> coloringRules;
	private ColorManager colorManager;
	private RGB defaultFG;
	private RGBa lineHighlight;
	private RGB defaultBG;
	private RGBa selection;
	private RGB caret;
	private String name;

	private RGB searchResultBG;

	/**
	 * Access to get the text attribute. May cache internal information, so, must be recreated when the theme changes.
	 */
	private ThemeGetTextAttribute themeGetTextAttribute;

	public Theme(ColorManager colormanager, Properties props)
	{
		this.colorManager = colormanager;
		coloringRules = new ArrayList<ThemeRule>();
		parseProps(props);
		storeDefaults();
	}

	private ThemeGetTextAttribute obtainGetThemeTextAttribute()
	{
		if (themeGetTextAttribute == null)
		{
			themeGetTextAttribute = new ThemeGetTextAttribute(this);
		}
		return themeGetTextAttribute;
	}

	private void parseProps(Properties props)
	{
		name = (String) props.remove(THEME_NAME_PROP_KEY);
		if (name == null)
		{
			// Log the properties
			String properties = StringUtil.EMPTY;
			PrintWriter pw = null;
			try
			{
				StringWriter sw = new StringWriter(); // $codepro.audit.disable closeWhereCreated
				pw = new PrintWriter(sw);
				props.list(pw);
				properties = sw.toString();
			}
			catch (Exception e)
			{
				// ignore
			}
			finally
			{
				if (pw != null)
				{
					pw.close();
				}
			}
			throw new IllegalStateException(
					"Invalid theme properties. No theme 'name' provided. Properties may be corrupted: " + properties); //$NON-NLS-1$
		}
		// The general editor colors
		// FIXME Add fallback rgb colors to use! black on white, etc.
		defaultFG = parseHexRGB((String) props.remove(FOREGROUND_PROP_KEY));
		defaultBG = parseHexRGB((String) props.remove(BACKGROUND_PROP_KEY));
		lineHighlight = parseHexRGBa((String) props.remove(LINE_HIGHLIGHT_PROP_KEY));
		selection = parseHexRGBa((String) props.remove(SELECTION_PROP_KEY));
		caret = parseHexRGB((String) props.remove(CARET_PROP_KEY), true);

		Set<Object> propertyNames = props.keySet();
		for (Object key : propertyNames)
		{
			String displayName = (String) key;
			int style = SWT.NORMAL;
			RGBa foreground = null;
			RGBa background = null;
			String value = props.getProperty(displayName);
			String scopeSelector = displayName;
			int selectorIndex = value.indexOf(SELECTOR_DELIMITER);
			if (selectorIndex != -1)
			{
				scopeSelector = value.substring(selectorIndex + 1);
				if ("null".equals(scopeSelector)) //$NON-NLS-1$
				{
					scopeSelector = null;
				}
				value = value.substring(0, selectorIndex);
			}
			List<String> values = tokenize(value);
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
				else if (token.length() > 0 && token.charAt(0) == '#')
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
			coloringRules.add(new ThemeRule(displayName, scopeSelector == null ? null
					: new ScopeSelector(scopeSelector), attribute));
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
			IdeLog.logError(ThemePlugin.getDefault(),
					MessageFormat.format("Received RGBa Hex value with invalid length: {0}", hex)); //$NON-NLS-1$
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
		ThemeGetTextAttribute themeGetTextAttribute = obtainGetThemeTextAttribute();
		return themeGetTextAttribute.getTextAttribute(scope);
	}

	ThemeRule winningRule(String scope)
	{
		ThemeGetTextAttribute themeGetTextAttribute = obtainGetThemeTextAttribute();
		IScopeSelector match = themeGetTextAttribute.findMatch(scope);
		if (match == null)
		{
			return null;
		}
		return getRuleForSelector(match);
	}

	public ThemeRule getRuleForSelector(IScopeSelector match)
	{
		// See APSTUD-2790. In Textmate the last matching rule wins, so to get that behavior we reverse the rule list
		// before matching.
		List<ThemeRule> reversed = new ArrayList<ThemeRule>(coloringRules);
		Collections.reverse(reversed);
		for (ThemeRule rule : reversed)
		{
			if (rule.isSeparator())
			{
				continue;
			}
			if (rule.getScopeSelector().equals(match))
			{
				return rule;
			}
		}
		return null;
	}

	/**
	 * The background color to use for the editor and any themed views.
	 * 
	 * @return
	 */
	public RGB getBackground()
	{
		return defaultBG;
	}

	/**
	 * Return the RGBa values for the selection color bg.
	 * 
	 * @return
	 */
	public RGBa getSelection()
	{
		return selection;
	}

	/**
	 * The foreground color for editor text and any themed views.
	 * 
	 * @return
	 */
	public RGB getForeground()
	{
		return defaultFG;
	}

	/**
	 * Color to be used to highlight the current line in the editor.
	 * 
	 * @return
	 */
	public RGBa getLineHighlight()
	{
		return lineHighlight;
	}

	/**
	 * Color used for the caret/cursor in the text editor.
	 * 
	 * @return
	 */
	public RGB getCaret()
	{
		return caret;
	}

	/**
	 * Color that should be used to highlight character pairs.
	 * 
	 * @return
	 */
	public RGB getCharacterPairColor()
	{
		return alphaBlend(defaultBG, getCaret(), 128);
	}

	/**
	 * Color that should be used for occurrence indications (i.e. html/xml tag pairs). Same as
	 * {@link #getCharacterPairColor()} for now.
	 * 
	 * @return
	 */
	public RGB getOccurenceHighlightColor()
	{
		return getCharacterPairColor();
	}

	/**
	 * The unique name for this theme.
	 * 
	 * @return
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * The Map from scope selectors (as strings) to the FG/BG/Font styles (TextAttributes) that should be applied for
	 * them. Clients should never need to use this, this is meant for the preference page and testing!
	 * 
	 * @return
	 */
	public List<ThemeRule> getTokens()
	{
		return Collections.unmodifiableList(coloringRules);
	}

	/**
	 * A Java Properties file serialization of this theme.
	 * 
	 * @return
	 */
	public Properties toProps()
	{
		Properties props = new OrderedProperties();
		props.put(THEME_NAME_PROP_KEY, getName());
		props.put(SELECTION_PROP_KEY, toHex(getSelection()));
		props.put(LINE_HIGHLIGHT_PROP_KEY, toHex(getLineHighlight()));
		props.put(FOREGROUND_PROP_KEY, toHex(getForeground()));
		props.put(BACKGROUND_PROP_KEY, toHex(getBackground()));
		props.put(CARET_PROP_KEY, toHex(caret));
		for (ThemeRule rule : coloringRules)
		{
			if (rule == null)
			{
				continue;
			}
			StringBuilder value = new StringBuilder();
			DelayedTextAttribute attr = rule.getTextAttribute();
			RGBa color = attr.foreground;
			if (color != null)
			{
				value.append(toHex(color)).append(DELIMETER);
			}
			color = attr.background;
			if (color != null)
			{
				value.append(toHex(color)).append(DELIMETER);
			}
			int style = attr.style;
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
			// Append the scope selector
			value.append(SELECTOR_DELIMITER);
			value.append(rule.getScopeSelector().toString());
			props.put(rule.getName(), value.toString());
		}
		return props;
	}

	static String toHex(RGBa color)
	{
		String rgbString = toHex(color.toRGB());
		if (color.getAlpha() == 0)
		{
			return rgbString;
		}
		return rgbString + pad(Integer.toHexString(color.getAlpha()), 2, '0');
	}

	public static String toHex(RGB rgb)
	{
		return MessageFormat.format("#{0}{1}{2}", pad(Integer.toHexString(rgb.red), 2, '0'), pad(Integer //$NON-NLS-1$
				.toHexString(rgb.green), 2, '0'), pad(Integer.toHexString(rgb.blue), 2, '0'));
	}

	private static String pad(String string, int desiredLength, char padChar)
	{
		while (string.length() < desiredLength)
		{
			string = padChar + string;
		}
		return string;
	}

	protected void storeDefaults()
	{
		// Don't store builtin themes default copy in prefs!
		if (getThemeManager().isBuiltinTheme(getName()))
		{
			return;
		}
		// Only save to defaults if it has never been saved there. Basically take a snapshot of first version and
		// use that as the "default"
		IEclipsePreferences prefs = DefaultScope.INSTANCE.getNode(ThemePlugin.PLUGIN_ID);
		if (prefs == null)
		{
			return; // TODO Log something?
		}
		Preferences preferences = prefs.node(ThemeManager.THEMES_NODE);
		if (preferences == null)
		{
			return;
		}
		String value = preferences.get(getName(), null);
		if (value == null)
		{
			save(DefaultScope.INSTANCE);
		}
	}

	public void save()
	{
		save(InstanceScope.INSTANCE);
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
		ByteArrayOutputStream os = null;
		try
		{
			os = new ByteArrayOutputStream();
			toProps().store(os, null);
			IEclipsePreferences prefs = scope.getNode(ThemePlugin.PLUGIN_ID);
			Preferences preferences = prefs.node(ThemeManager.THEMES_NODE);
			preferences.putByteArray(getName(), os.toByteArray());
			prefs.flush();
		}
		catch (Exception e)
		{
			IdeLog.logError(ThemePlugin.getDefault(), e);
		}
		finally
		{
			if (os != null)
			{
				try
				{
					os.close();
				}
				catch (IOException e)
				{
					// ignore
				}
			}
		}
	}

	public void loadFromDefaults() throws InvalidPropertiesFormatException, UnsupportedEncodingException, IOException
	{
		Properties props = null;
		if (getThemeManager().isBuiltinTheme(getName()))
		{
			Theme builtin = ((ThemeManager) getThemeManager()).loadBuiltinTheme(getName());
			props = builtin.toProps();
		}
		else
		{
			IEclipsePreferences prefs = DefaultScope.INSTANCE.getNode(ThemePlugin.PLUGIN_ID);
			Preferences preferences = prefs.node(ThemeManager.THEMES_NODE);
			ByteArrayInputStream byteStream = null;
			try
			{
				byte[] array = preferences.getByteArray(getName(), null);
				if (array == null)
				{
					return;
				}
				props = new OrderedProperties();
				byteStream = new ByteArrayInputStream(array);
				props.load(byteStream);
			}
			catch (IllegalArgumentException iae)
			{
				// Fallback to load theme that was saved in prefs as XML string
				String xml = preferences.get(getName(), null);
				if (xml == null)
				{
					return;
				}
				ByteArrayInputStream xmlStream = null;
				try
				{
					xmlStream = new ByteArrayInputStream(xml.getBytes(IOUtil.UTF_8));
					props = new OrderedProperties();
					props.loadFromXML(xmlStream);
					save(DefaultScope.INSTANCE);
				}
				finally
				{
					if (xmlStream != null)
					{
						try
						{
							xmlStream.close();
						}
						catch (Exception e)
						{
							// ignore
						}
					}
				}
			}
			finally
			{
				if (byteStream != null)
				{
					try
					{
						byteStream.close();
					}
					catch (Exception e)
					{
						// ignore
					}
				}
			}
		}
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
		delete(InstanceScope.INSTANCE);
	}

	private void deleteDefaultVersion()
	{
		delete(DefaultScope.INSTANCE);
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
			IdeLog.logError(ThemePlugin.getDefault(), e);
		}
	}

	public void reorderRule(int startIndex, int endIndex)
	{
		if (endIndex > startIndex)
		{
			endIndex--;
		}
		ThemeRule selected = coloringRules.remove(startIndex);
		coloringRules.add(endIndex, selected);
		save();
	}

	public void addNewDefaultToken(int index, String newTokenName)
	{
		addNewRule(index, newTokenName, null, new DelayedTextAttribute(null));
	}

	public void addNewRule(int index, String ruleName, ScopeSelector selector, DelayedTextAttribute attr)
	{
		coloringRules.add(index, new ThemeRule(ruleName, selector, attr));
		wipeCache();
		save();
	}

	public void updateRule(int index, ThemeRule newRule)
	{
		coloringRules.remove(index);
		if (index >= coloringRules.size())
		{
			coloringRules.add(newRule);
		}
		else
		{
			coloringRules.add(index, newRule);
		}
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
		wipeCache();
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
		this.themeGetTextAttribute = null;
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
		searchResultBG = null;
		save();
	}

	public Theme copy(String value)
	{
		if (value == null)
		{
			return null;
		}
		try
		{
			Properties props = toProps();
			props.setProperty(THEME_NAME_PROP_KEY, value);
			Theme newTheme = new Theme(colorManager, props);
			addTheme(newTheme);
			return newTheme;
		}
		catch (Exception e)
		{
			IdeLog.logError(ThemePlugin.getDefault(), e);
			return null;
		}
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
		IScopeSelector selector = new ScopeSelector(scopeSelector);
		ThemeRule rule = getRuleForSelector(selector);
		return rule != null;
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

	/**
	 * Based on the selection color. If the selection color is found to be "dark", we lighten it some, otherwise we
	 * darken it some.
	 * 
	 * @return
	 */
	public RGB getSearchResultColor()
	{
		if (searchResultBG == null)
		{
			searchResultBG = isDark(getSelectionAgainstBG()) ? lighten(getSelectionAgainstBG())
					: darken(getSelectionAgainstBG());
		}
		return searchResultBG;
	}

	public RGB lighten(RGB color)
	{
		return lighten(color, (float) 0.15);
	}

	public RGB lighten(RGB color, float amount)
	{
		float[] hsb = color.getHSB();
		return new RGB(hsb[0], hsb[1], Math.min(1, hsb[2] + amount));
	}

	public RGB darken(RGB color)
	{
		return darken(color, (float) 0.15);
	}

	public RGB darken(RGB color, float amount)
	{
		float[] hsb = color.getHSB();
		return new RGB(hsb[0], hsb[1], Math.max(0, hsb[2] - amount));
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

	public void remove(ThemeRule entry)
	{
		coloringRules.remove(entry);
		wipeCache();
		save();
	}

	public Color getForegroundColor()
	{
		return getColorManager().getColor(getForeground());
	}

	public Color getBackgroundColor()
	{
		return getColorManager().getColor(getBackground());
	}

	protected ColorManager getColorManager()
	{
		return ThemePlugin.getDefault().getColorManager();
	}

	/**
	 * Helper function just to print information on the theme.
	 */
	public void printSummary(boolean complete)
	{
		Map<String, Integer> counts = new HashMap<String, Integer>();
		int total = 0;
		for (ThemeRule rule : getTokens())
		{
			if (rule.isSeparator())
			{
				continue;
			}
			total += 1;
			ISelectorNode root = ((ScopeSelector) rule.getScopeSelector()).getRoot();
			Class<? extends ISelectorNode> class1 = root.getClass();
			Integer i = counts.get(class1.getName());
			if (i == null)
			{
				i = 0;
			}
			counts.put(class1.getName(), i + 1);

			if (complete)
			{
				System.out.println();
				System.out.println(rule);
				System.out.println(root.toString());
				System.out.println(class1);
			}
		}
		System.out.println("Theme: " + this.getName()); //$NON-NLS-1$
		System.out.println("Non-separator rules: " + total); //$NON-NLS-1$
		for (Map.Entry<String, Integer> entry : counts.entrySet())
		{
			System.out.println(entry.getKey() + ": " + entry.getValue()); //$NON-NLS-1$
		}
	}
}
