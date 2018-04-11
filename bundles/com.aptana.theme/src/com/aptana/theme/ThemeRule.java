/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.theme;

import java.text.MessageFormat;

import com.aptana.scope.IScopeSelector;
import com.aptana.scope.ScopeSelector;

/**
 * An umodifiable value object. If you want to update a field, we return a new object that's been copied with the
 * modification requested. It is then up to you to update the theme with the new rule!
 * 
 * @author cwilliams
 */
public class ThemeRule
{

	private static final ScopeSelector EMPTY_SCOPE = new ScopeSelector(""); //$NON-NLS-1$
	private IScopeSelector fSelector;
	private String fName;
	private DelayedTextAttribute fTextAttribute;

	ThemeRule(String name, IScopeSelector selector, DelayedTextAttribute attr)
	{
		this.fSelector = selector;
		this.fName = name;
		this.fTextAttribute = attr;
	}

	public IScopeSelector getScopeSelector()
	{
		if (fSelector == null)
		{
			return EMPTY_SCOPE;
		}
		return fSelector;
	}

	public DelayedTextAttribute getTextAttribute()
	{
		if (fTextAttribute == null)
		{
			return new DelayedTextAttribute(null);
		}
		return fTextAttribute;
	}

	public String getName()
	{
		return fName;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof ThemeRule))
		{
			return false;
		}
		ThemeRule other = (ThemeRule) obj;
		if (!getName().equals(other.getName()))
		{
			return false;
		}
		if (!getScopeSelector().equals(other.getScopeSelector()))
		{
			return false;
		}
		if (!getTextAttribute().equals(other.getTextAttribute()))
		{
			return false;
		}
		return true;
	}

	@Override
	public int hashCode()
	{
		return toString().hashCode();
	}

	@Override
	public String toString()
	{
		return MessageFormat.format(
				"name: {0}; scope: {1}; style: {2}", getName(), getScopeSelector(), getTextAttribute()); //$NON-NLS-1$
	}

	public boolean isSeparator()
	{
		IScopeSelector scopeSelector = getScopeSelector();
		if (scopeSelector.equals(EMPTY_SCOPE))
		{
			return true;
		}
		if ("dont.match".equals(scopeSelector.toString())) //$NON-NLS-1$
		{
			// Special case: our themes use dont.match for titles
			// See: abstract_theme_dark.properties and abstract_theme_light.properties
			return true;

		}
		return false;
	}

	public ThemeRule setName(String newName)
	{
		return new ThemeRule(newName, getScopeSelector(), getTextAttribute());
	}

	public ThemeRule setTextAttribute(RGBa fg, RGBa bg, int style)
	{
		return new ThemeRule(getName(), getScopeSelector(), new DelayedTextAttribute(fg, bg, style));
	}

	public ThemeRule updateFontStyle(int style)
	{
		return setTextAttribute(getTextAttribute().foreground, getTextAttribute().background, style);
	}

	public ThemeRule updateFG(RGBa fg)
	{
		return setTextAttribute(fg, getTextAttribute().background, getTextAttribute().style);
	}

	public ThemeRule updateBG(RGBa bg)
	{
		return setTextAttribute(getTextAttribute().foreground, bg, getTextAttribute().style);
	}

	public ThemeRule setScopeSelector(ScopeSelector scopeSelector)
	{
		return new ThemeRule(getName(), scopeSelector, getTextAttribute());
	}
}
