package com.aptana.theme;

import com.aptana.scope.IScopeSelector;
import com.aptana.scope.ScopeSelector;

public class ThemeRule
{

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
}
