package com.aptana.theme;

import java.text.MessageFormat;

import com.aptana.scope.IScopeSelector;
import com.aptana.scope.ScopeSelector;

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
		return fTextAttribute;
	}

	public String getName()
	{
		return fName;
	}

	public void setName(String newName)
	{
		this.fName = newName;
	}

	public void setTextAttribute(RGBa fg, RGBa bg, int style)
	{
		this.fTextAttribute = new DelayedTextAttribute(fg, bg, style);
	}

	public void updateFontStyle(int style)
	{
		setTextAttribute(getTextAttribute().getForeground(), getTextAttribute().getBackground(), style);
	}

	public void updateFG(RGBa fg)
	{
		setTextAttribute(fg, getTextAttribute().getBackground(), getTextAttribute().getStyle());
	}

	public void updateBG(RGBa bg)
	{
		setTextAttribute(getTextAttribute().getForeground(), bg, getTextAttribute().getStyle());
	}

	public void setScopeSelector(ScopeSelector scopeSelector)
	{
		this.fSelector = scopeSelector;
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
		return getScopeSelector().equals(EMPTY_SCOPE);
	}
}
