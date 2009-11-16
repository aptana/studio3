package com.aptana.radrails.editor.common.theme;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

/**
 * Reads in the theme from a java properties file. Intentionally similar to the Textmate themes. keys are token types,
 * values are comma delimited with hex colors and font style keywords.
 * 
 * First hex color becomes FG, second becomes BG (if there).
 * 
 * @author cwilliams
 */
public class Theme
{

	Map<String, TextAttribute> map;
	private ColorManager colorManager;
	private RGB defaultFG;
	private RGB lineHighlight;
	private RGB defaultBG;
	private RGB selection;
	private RGB caret;
	private String name;

	public Theme(ColorManager colormanager, Properties props)
	{
		this.colorManager = colormanager;
		map = new HashMap<String, TextAttribute>();
		parseProps(props);
	}

	private void parseProps(Properties props)
	{
		name = (String) props.remove("name");
		// The general editor colors
		defaultFG = parseHexRGB((String) props.remove("foreground"));
		lineHighlight = parseHexRGB((String) props.remove("lineHighlight"));
		defaultBG = parseHexRGB((String) props.remove("background"));
		selection = parseHexRGB((String) props.remove("selection"));
		caret = parseHexRGB((String) props.remove("caret"));

		for (Entry<Object, Object> entry : props.entrySet())
		{
			String tokenName = (String) entry.getKey();
			int style = SWT.NORMAL;
			Color foreground = null;
			Color background = null;
			List<String> tokens = tokenize((String) entry.getValue());
			for (String token : tokens)
			{
				if (token.startsWith("#"))
				{
					// it's a color!
					RGB rgb = parseHexRGB(token);
					if (foreground == null)
						foreground = colorManager.getColor(rgb);
					else
						background = colorManager.getColor(rgb);
				}
				else
				{
					if (token.equalsIgnoreCase("italic"))
						style |= SWT.ITALIC;
					else if (token.equalsIgnoreCase("underline"))
						style |= TextAttribute.UNDERLINE;
					else if (token.equalsIgnoreCase("bold"))
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
		if (!value.contains(","))
		{
			tokens.add(value);
			return tokens;
		}
		StringTokenizer tokenizer = new StringTokenizer(value, ", ");
		while (tokenizer.hasMoreTokens())
		{
			tokens.add(tokenizer.nextToken());
		}
		return tokens;
	}

	private RGB parseHexRGB(String token)
	{
		String s = token.substring(1, 3);
		int r = Integer.parseInt(s, 16);
		s = token.substring(3, 5);
		int g = Integer.parseInt(s, 16);
		s = token.substring(5, 7);
		int b = Integer.parseInt(s, 16);
		return new RGB(r, g, b);
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

	public String getName()
	{
		return name;
	}

}
