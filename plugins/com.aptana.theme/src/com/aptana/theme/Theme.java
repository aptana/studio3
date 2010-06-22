package com.aptana.theme;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

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

	static final String THEME_NAME_PROP_KEY = "name"; //$NON-NLS-1$
	static final String FOREGROUND_PROP_KEY = "foreground"; //$NON-NLS-1$
	private static final String BACKGROUND_PROP_KEY = "background"; //$NON-NLS-1$
	private static final String SELECTION_PROP_KEY = "selection"; //$NON-NLS-1$
	private static final String LINE_HIGHLIGHT_PROP_KEY = "lineHighlight"; //$NON-NLS-1$
	private static final String CARET_PROP_KEY = "caret"; //$NON-NLS-1$

	private Map<String, TextAttribute> map;
	private ColorManager colorManager;
	private RGB defaultFG;
	private RGB lineHighlight;
	private RGB defaultBG;
	private RGB selection;
	private RGB caret;
	private String name;

	private RGB searchResultBG;

	public Theme(ColorManager colormanager, Properties props)
	{
		this.colorManager = colormanager;
		map = new HashMap<String, TextAttribute>();
		parseProps(props);
		storeDefaults();
	}

	private void parseProps(Properties props)
	{
		name = (String) props.remove(THEME_NAME_PROP_KEY);
		if (name == null)
			throw new IllegalStateException("Invalid theme properties!"); //$NON-NLS-1$
		// The general editor colors
		defaultFG = parseHexRGB((String) props.remove(FOREGROUND_PROP_KEY));
		defaultBG = parseHexRGB((String) props.remove(BACKGROUND_PROP_KEY));
		lineHighlight = parseHexRGB((String) props.remove(LINE_HIGHLIGHT_PROP_KEY), true);
		selection = parseHexRGB((String) props.remove(SELECTION_PROP_KEY), true);
		caret = parseHexRGB((String) props.remove(CARET_PROP_KEY), true);

		for (Entry<Object, Object> entry : props.entrySet())
		{
			String tokenName = (String) entry.getKey();
			int style = SWT.NORMAL;
			Color foreground = null;
			Color background = null;
			List<String> tokens = tokenize((String) entry.getValue());
			for (String token : tokens)
			{
				if (token.startsWith("#")) //$NON-NLS-1$
				{
					// it's a color!
					if (foreground == null)
						foreground = colorManager.getColor(parseHexRGB(token));
					else
						background = colorManager.getColor(parseHexRGB(token, true));
				}
				else
				{
					if (token.equalsIgnoreCase(ITALIC))
						style |= SWT.ITALIC;
					else if (token.equalsIgnoreCase(UNDERLINE))
						style |= TextAttribute.UNDERLINE;
					else if (token.equalsIgnoreCase(BOLD))
						style |= SWT.BOLD;
				}
			}
			if (foreground == null)
				foreground = colorManager.getColor(defaultFG);
			TextAttribute attribute = new TextAttribute(foreground, background, style, null);
			map.put(tokenName, attribute);
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
		StringTokenizer tokenizer = new StringTokenizer(value, ", "); //$NON-NLS-1$
		while (tokenizer.hasMoreTokens())
		{
			tokens.add(tokenizer.nextToken());
		}
		return tokens;
	}

	private RGB parseHexRGB(String token)
	{
		return parseHexRGB(token, false);
	}

	private RGB parseHexRGB(String token, boolean alphaMergeWithBG)
	{
		if (token == null)
			return new RGB(0, 0, 0);
		if (token.length() != 7 && token.length() != 9)
		{
			ThemePlugin.logError(MessageFormat.format("Received RGB Hex value with invalid length: {0}", token), null); //$NON-NLS-1$
			if (defaultFG != null)
				return defaultFG;
			return new RGB(0, 0, 0);
		}
		String s = token.substring(1, 3);
		int r = Integer.parseInt(s, 16);
		s = token.substring(3, 5);
		int g = Integer.parseInt(s, 16);
		s = token.substring(5, 7);
		int b = Integer.parseInt(s, 16);
		if (token.length() == 9 && alphaMergeWithBG)
		{
			// Handle RGBa values by mixing against BG, etc
			s = token.substring(7, 9);
			int a = Integer.parseInt(s, 16);
			return alphaBlend(defaultBG, new RGB(r, g, b), a);
		}
		return new RGB(r, g, b);
	}

	public static RGB alphaBlend(RGB baseToBlendWith, RGB colorOnTop, int alpha)
	{
		int destRed = baseToBlendWith.red;
		int destGreen = baseToBlendWith.green;
		int destBlue = baseToBlendWith.blue;

		// Alpha blending math
		destRed += (colorOnTop.red - destRed) * alpha / 0xFF;
		destGreen += (colorOnTop.green - destGreen) * alpha / 0xFF;
		destBlue += (colorOnTop.blue - destBlue) * alpha / 0xFF;

		return new RGB(destRed, destGreen, destBlue);
	}

	public TextAttribute getTextAttribute(String tokenType)
	{
		if (map.containsKey(tokenType))
			return map.get(tokenType);
		Comparator<String> c = new Comparator<String>()
		{
			public int compare(String o1, String o2)
			{
				int blah = o2.length() - o1.length();
				if (blah != 0)
					return blah;
				return o2.compareTo(o1);
			};
		};
		// We need to sort the map keys by length, longest match wins!
		SortedSet<String> sorted = new TreeSet<String>(c);
		sorted.addAll(map.keySet());

		for (String key : sorted)
		{
			if (tokenType.startsWith(key))
				return map.get(key);
		}

		// Some tokens are special. They have fallbacks even if not in the theme! Looks like bundles can contribute
		// them?
		if (tokenType.startsWith("markup.changed")) //$NON-NLS-1$
			return new TextAttribute(colorManager.getColor(new RGB(255, 255, 255)), colorManager.getColor(new RGB(248,
					205, 14)), 0);

		if (tokenType.startsWith("markup.deleted")) //$NON-NLS-1$
			return new TextAttribute(colorManager.getColor(new RGB(255, 255, 255)), colorManager.getColor(new RGB(255,
					86, 77)), 0);

		if (tokenType.startsWith("markup.inserted")) //$NON-NLS-1$
			return new TextAttribute(colorManager.getColor(new RGB(0, 0, 0)), colorManager.getColor(new RGB(128, 250,
					120)), 0);

		if (tokenType.startsWith("meta.diff.index") || tokenType.startsWith("meta.diff.range") || tokenType.startsWith("meta.separator.diff")) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			return new TextAttribute(colorManager.getColor(new RGB(255, 255, 255)), colorManager.getColor(new RGB(65,
					126, 218)), SWT.ITALIC);

		if (tokenType.startsWith("meta.diff.header")) //$NON-NLS-1$
			return new TextAttribute(colorManager.getColor(new RGB(255, 255, 255)), colorManager.getColor(new RGB(103,
					154, 233)), 0);

		return new TextAttribute(colorManager.getColor(defaultFG));
	}

	public RGB getBackground()
	{
		return defaultBG;
	}

	public RGB getSelection()
	{
		return selection;
	}

	public RGB getForeground()
	{
		return defaultFG;
	}

	public RGB getLineHighlight()
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
		return Collections.unmodifiableMap(map);
	}

	/**
	 * Updates the TextAttribute for a token and immediately saves the theme.
	 * 
	 * @param key
	 * @param at
	 */
	public void update(String key, TextAttribute at)
	{
		map.put(key, at);
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
		for (Map.Entry<String, TextAttribute> entry : map.entrySet())
		{
			if (entry.getKey() == null)
				continue;
			StringBuilder value = new StringBuilder();
			TextAttribute attr = entry.getValue();
			Color color = attr.getForeground();
			if (color != null)
			{
				value.append(toHex(color.getRGB())).append(DELIMETER);
			}
			color = attr.getBackground();
			if (color != null)
			{
				value.append(toHex(color.getRGB())).append(DELIMETER);
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
			value.deleteCharAt(value.length() - 1);
			if (value.length() == 0)
				continue;
			props.put(entry.getKey(), value.toString());
		}
		return props;
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
			getThemeManager().setCurrentTheme(this);
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
			return;
		Properties props = new Properties();
		props.loadFromXML(new ByteArrayInputStream(xmlProps.getBytes("UTF-8"))); //$NON-NLS-1$
		map.clear();
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
	 * Removes a token from the theme.
	 * 
	 * @param key
	 */
	public void remove(String key)
	{
		map.remove(key);
	}

	/**
	 * Adds a new token entry with no font styling, no bg, same FG as default for theme.
	 */
	public void addNewDefaultToken(String name)
	{
		TextAttribute attr = new TextAttribute(colorManager.getColor(defaultFG));
		map.put(name, attr);
		save();
	}

	public void updateCaret(RGB newColor)
	{
		if (newColor == null)
			return;
		if (caret != null && caret.equals(newColor))
			return;
		caret = newColor;
		save();
	}

	public void updateFG(RGB newColor)
	{
		if (newColor == null)
			return;
		if (defaultFG != null && defaultFG.equals(newColor))
			return;
		defaultFG = newColor;
		save();
	}

	public void updateBG(RGB newColor)
	{
		if (newColor == null)
			return;
		if (defaultBG != null && defaultBG.equals(newColor))
			return;
		defaultBG = newColor;
		save();
	}

	public void updateLineHighlight(RGB newColor)
	{
		if (newColor == null)
			return;
		if (lineHighlight != null && lineHighlight.equals(newColor))
			return;
		lineHighlight = newColor;
		save();
	}

	public void updateSelection(RGB newColor)
	{
		if (newColor == null)
			return;
		if (selection != null && selection.equals(newColor))
			return;
		selection = newColor;
		save();
	}

	public Theme copy(String value)
	{
		if (value == null)
			return null;
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
	 * @param tokenType
	 * @return
	 */
	public boolean hasEntry(String tokenType)
	{
		return map.containsKey(tokenType);
	}

	public Color getForeground(String tokenType)
	{
		TextAttribute attr = getTextAttribute(tokenType);
		if (attr == null)
			return null;
		return attr.getForeground();
	}

	/**
	 * Returns the RGB value for the foreground of a specific token.
	 * 
	 * @param string
	 * @return
	 */
	public RGB getForegroundAsRGB(String tokenType)
	{
		Color fg = getForeground(tokenType);
		if (fg == null)
			return null;
		return fg.getRGB();
	}

	public Color getBackground(String tokenType)
	{
		TextAttribute attr = getTextAttribute(tokenType);
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
	public RGB getBackgroundAsRGB(String tokenType)
	{
		Color bg = getBackground(tokenType);
		if (bg == null)
			return null;
		return bg.getRGB();
	}

	public RGB getSearchResultColor()
	{
		if (searchResultBG == null)
		{
			searchResultBG = isDark(getSelection()) ? lighten(getSelection()) : darken(getSelection());
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

}
