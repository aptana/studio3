package com.aptana.editor.common.theme;

import java.util.Set;

import org.eclipse.jface.text.rules.IToken;

public interface IThemeManager
{

	// TODO Make arg the string id, rather than the theme object
	public void setActiveTheme(Theme theme);

	public Theme getActiveTheme();

	public void addTheme(Theme theme);

	public void removeTheme(Theme theme);

	public boolean isBuiltinTheme(String themeName);

	public Set<String> getThemeNames();

	public Theme getTheme(String name);

	public IToken getToken(String name);

}