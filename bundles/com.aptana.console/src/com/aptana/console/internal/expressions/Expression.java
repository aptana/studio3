/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.console.internal.expressions;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import com.aptana.theme.Theme;
import com.aptana.theme.ThemePlugin;

/**
 * @author Max Stepanov
 */
public final class Expression
{

	private boolean enabled = true;
	private String label;
	private String expression;
	private SortedMap<Integer, String> groupScopes;

	private Pattern pattern;

	/**
	 * @param label
	 * @param expression
	 * @param groupScopes
	 */
	public Expression(String label, String expression, SortedMap<Integer, String> groupScopes)
	{
		this.label = label;
		this.expression = expression;
		this.groupScopes = groupScopes;
	}

	/**
	 * Calculate line styles
	 * 
	 * @param lineOffset
	 * @param lineText
	 * @return
	 */
	public StyleRange[] calculateStyleRanges(int lineOffset, String lineText)
	{
		if (enabled && groupScopes != null && !groupScopes.isEmpty())
		{
			Matcher matcher = getPattern().matcher(lineText);
			if (matcher.matches())
			{
				List<StyleRange> result = new ArrayList<StyleRange>();
				int groupCount = Math.min(matcher.groupCount(), groupScopes.lastKey());
				if (groupCount == 0)
				{
					TextAttribute textAttribute = getTextAttribute(groupScopes.get(groupScopes.firstKey()));
					result.add(createStyleRange(lineOffset, lineText.length(), textAttribute));
				}
				else
				{
					TextAttribute defaultTextAttribute = getTextAttribute(groupScopes.get(groupScopes.firstKey()));
					int previous = 0;
					int offset, length;
					for (int group = 1; group <= groupCount; ++group)
					{
						offset = matcher.start(group);
						length = matcher.group(group).length();
						if (offset > previous)
						{
							result.add(createStyleRange(lineOffset + previous, offset - previous, defaultTextAttribute));
						}
						previous = offset + length;
						if (groupScopes.containsKey(group))
						{
							result.add(createStyleRange(lineOffset + offset, length,
									getTextAttribute(groupScopes.get(group))));
						}
						else
						{
							previous = offset;
						}
					}
					if (previous < lineText.length())
					{
						result.add(createStyleRange(lineOffset + previous, lineText.length() - previous,
								defaultTextAttribute));
					}
				}
				return result.toArray(new StyleRange[result.size()]);
			}
		}
		return null;
	}

	private TextAttribute getTextAttribute(String scope)
	{
		TextAttribute ta = getCurrentTheme().getTextAttribute(scope);
		RGB defaultRGB = getCurrentTheme().getForeground();
		if (ta.getForeground().getRGB().equals(defaultRGB))
		{
			return new TextAttribute(null, ta.getBackground(), ta.getStyle());
		}
		return ta;
	}

	/**
	 * Calculate line background
	 * 
	 * @param lineText
	 * @return
	 */
	public Color calculateBackground(String lineText)
	{
		if (enabled && getPattern().matcher(lineText).matches())
		{
			return getCurrentTheme().getBackground(groupScopes.get(groupScopes.firstKey()));
		}
		return null;
	}

	/**
	 * @return the enabled
	 */
	public boolean isEnabled()
	{
		return enabled;
	}

	/**
	 * @param enabled
	 *            the enabled to set
	 */
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	/**
	 * @return the label
	 */
	public String getLabel()
	{
		return label;
	}

	/**
	 * @param label
	 *            the label to set
	 */
	public void setLabel(String label)
	{
		this.label = label;
	}

	/**
	 * @return the expression
	 */
	public String getExpression()
	{
		return expression;
	}

	/**
	 * @param expression
	 *            the expression to set
	 */
	public void setExpression(String expression)
	{
		this.expression = expression;
		this.pattern = null;
	}

	/**
	 * @return the groupScopes
	 */
	public SortedMap<Integer, String> getGroupScopes()
	{
		return groupScopes;
	}

	/**
	 * @param groupScopes
	 *            the groupScopes to set
	 */
	public void setGroupScopes(SortedMap<Integer, String> groupScopes)
	{
		this.groupScopes = groupScopes;
	}

	private Pattern getPattern()
	{
		if (pattern == null)
		{
			pattern = Pattern.compile(expression);
		}
		return pattern;
	}

	private static StyleRange createStyleRange(int offset, int length, TextAttribute textAttribute)
	{
		int style = textAttribute.getStyle();
		int fontStyle = style & (SWT.ITALIC | SWT.BOLD | SWT.NORMAL);
		StyleRange styleRange = new StyleRange(offset, length, textAttribute.getForeground(),
				textAttribute.getBackground(), fontStyle);
		styleRange.strikeout = (style & TextAttribute.STRIKETHROUGH) != 0;
		styleRange.underline = (style & TextAttribute.UNDERLINE) != 0;
		return styleRange;
	}

	protected Theme getCurrentTheme()
	{
		return ThemePlugin.getDefault().getThemeManager().getCurrentTheme();
	}

}
