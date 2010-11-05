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
package com.aptana.theme.internal;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.osgi.framework.Bundle;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.theme.IThemeManager;
import com.aptana.theme.Theme;
import com.aptana.theme.ThemePlugin;
import com.aptana.theme.internal.preferences.ThemerPreferenceInitializer;
import com.aptana.theme.preferences.IPreferenceConstants;

public class ThemeManager implements IThemeManager
{
	/**
	 * Character used to separate listing of theme names stored under {@link #THEME_LIST_PREF_KEY}
	 */
	private static final String THEME_NAMES_DELIMETER = ","; //$NON-NLS-1$

	/**
	 * Preference key used to store the list of known themes.
	 */
	private static final String THEME_LIST_PREF_KEY = "themeList"; //$NON-NLS-1$

	/**
	 * Node in preferences used to store themes under. Each theme is a key value pair under this node. The key is the
	 * theme name, value is XML format java Properties object.
	 */
	public static final String THEMES_NODE = "themes"; //$NON-NLS-1$
	// TODO Don't expose this node name. Fold saving/loading of themes into this impl

	private Theme fCurrentTheme;
	private HashMap<String, Theme> fThemeMap;
	private HashSet<String> fBuiltins;

	private static ThemeManager fgInstance;

	private ThemeManager()
	{
	}

	public static ThemeManager instance()
	{
		if (fgInstance == null)
		{
			fgInstance = new ThemeManager();
		}
		return fgInstance;
	}

	private TextAttribute getTextAttribute(String name)
	{
		if (getCurrentTheme() != null)
			return getCurrentTheme().getTextAttribute(name);
		return new TextAttribute(ThemePlugin.getDefault().getColorManager().getColor(new RGB(255, 255, 255)));
	}

	public Theme getCurrentTheme()
	{
		if (fCurrentTheme == null)
		{
			String activeThemeName = Platform.getPreferencesService().getString(ThemePlugin.PLUGIN_ID,
					IPreferenceConstants.ACTIVE_THEME, ThemerPreferenceInitializer.DEFAULT_THEME, null);
			if (activeThemeName != null)
			{
				fCurrentTheme = getTheme(activeThemeName);
			}
			if (fCurrentTheme == null)
			{
				// if we can't find the default theme, just use the first one in the list
				if (!getThemeMap().values().isEmpty())
				{
					fCurrentTheme = getThemeMap().values().iterator().next();
				}
			}
			if (fCurrentTheme != null)
			{
				setCurrentTheme(fCurrentTheme);
			}
		}
		return fCurrentTheme;
	}

	public void setCurrentTheme(Theme theme)
	{
		fCurrentTheme = theme;

		// Set the find in file search color
		IEclipsePreferences prefs = new InstanceScope().getNode("org.eclipse.search"); //$NON-NLS-1$
		prefs.put("org.eclipse.search.potentialMatch.fgColor", toString(theme.getSearchResultColor())); //$NON-NLS-1$
		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			ThemePlugin.logError(e);
		}

		// Set the color for the search result annotation, the pref key is "searchResultIndicationColor"
		prefs = new InstanceScope().getNode("org.eclipse.ui.editors"); //$NON-NLS-1$
		prefs.put("searchResultIndicationColor", toString(theme.getSearchResultColor())); //$NON-NLS-1$
		prefs.put("htmlTagPairOccurrenceIndicationColor", toString(theme.getSearchResultColor())); //$NON-NLS-1$
		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			ThemePlugin.logError(e);
		}

		// Also set the standard eclipse editor props, like fg, bg, selection fg, bg
		prefs = new InstanceScope().getNode("com.aptana.editor.common"); //$NON-NLS-1$
		prefs.putBoolean(AbstractTextEditor.PREFERENCE_COLOR_SELECTION_FOREGROUND_SYSTEM_DEFAULT, false);
		prefs.put(AbstractTextEditor.PREFERENCE_COLOR_SELECTION_FOREGROUND, toString(theme.getSelectionAgainstBG()));

		prefs.putBoolean(AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND_SYSTEM_DEFAULT, false);
		prefs.put(AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND, toString(theme.getBackground()));

		prefs.putBoolean(AbstractTextEditor.PREFERENCE_COLOR_FOREGROUND_SYSTEM_DEFAULT, false);
		prefs.put(AbstractTextEditor.PREFERENCE_COLOR_FOREGROUND, toString(theme.getForeground()));

		prefs.put(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE_COLOR,
				toString(theme.getLineHighlightAgainstBG()));
		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			ThemePlugin.logError(e);
		}

		prefs = new InstanceScope().getNode(ThemePlugin.PLUGIN_ID);
		prefs.put(IPreferenceConstants.ACTIVE_THEME, theme.getName());
		prefs.putLong(THEME_CHANGED, System.currentTimeMillis());
		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			ThemePlugin.logError(e);
		}
	}

	private static String toString(RGB selection)
	{
		StringBuilder builder = new StringBuilder();
		builder.append(selection.red).append(THEME_NAMES_DELIMETER).append(selection.green)
				.append(THEME_NAMES_DELIMETER).append(selection.blue);
		return builder.toString();
	}

	public Theme getTheme(String name)
	{
		return getThemeMap().get(name);
	}

	private Map<String, Theme> getThemeMap()
	{
		if (fThemeMap == null)
		{
			loadThemes();
		}
		return fThemeMap;
	}

	public Set<String> getThemeNames()
	{
		return getThemeMap().keySet();
	}

	private void loadThemes()
	{
		fThemeMap = new HashMap<String, Theme>();
		// Load builtin themes stored in properties files
		loadBuiltinThemes();
		// Load themes from the preferences
		loadUserThemes();

		saveThemeList();
	}

	private void saveThemeList()
	{
		StringBuilder builder = new StringBuilder();
		for (String themeName : fThemeMap.keySet())
		{
			// FIXME What if the themeName contains our delimeter?!
			builder.append(themeName).append(THEME_NAMES_DELIMETER);
		}
		builder.deleteCharAt(builder.length() - 1);
		IEclipsePreferences prefs = new InstanceScope().getNode(ThemePlugin.PLUGIN_ID);
		prefs.put(THEME_LIST_PREF_KEY, builder.toString());
		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			ThemePlugin.logError(e);
		}
	}

	private void loadUserThemes()
	{
		String themeNames = Platform.getPreferencesService().getString(ThemePlugin.PLUGIN_ID, THEME_LIST_PREF_KEY,
				null, null);
		if (themeNames == null)
			return;
		StringTokenizer tokenizer = new StringTokenizer(themeNames, THEME_NAMES_DELIMETER);
		while (tokenizer.hasMoreElements())
		{
			try
			{
				String themeName = tokenizer.nextToken();
				String xmlProps = Platform.getPreferencesService().getString(ThemePlugin.PLUGIN_ID,
						THEMES_NODE + "/" + themeName, null, null); //$NON-NLS-1$
				if (xmlProps == null || xmlProps.trim().length() == 0)
					continue;
				Properties props = new Properties();
				props.loadFromXML(new ByteArrayInputStream(xmlProps.getBytes("UTF-8"))); //$NON-NLS-1$
				Theme theme = new Theme(ThemePlugin.getDefault().getColorManager(), props);
				fThemeMap.put(theme.getName(), theme);
			}
			catch (Exception e)
			{
				ThemePlugin.logError(e);
			}
		}
	}

	private void loadBuiltinThemes()
	{
		fBuiltins = new HashSet<String>();
		Collection<URL> urls = getBuiltinThemeURLs();
		if (urls == null || urls.isEmpty())
		{
			return;
		}
		Map<String, Properties> nameToThemeProperties = new HashMap<String, Properties>();
		for (URL url : urls)
		{
			try
			{
				InputStream stream = url.openStream();
				try
				{
					Properties props = new Properties();
					props.load(stream);
					String themeName = props.getProperty(Theme.THEME_NAME_PROP_KEY);
					if (themeName != null)
					{
						if (!nameToThemeProperties.containsKey(themeName))
						{
							nameToThemeProperties.put(themeName, props);
						}
						else
						{
							throw new IllegalStateException(MessageFormat.format(
									Messages.ThemeManager_ERR_DuplicateTheme, themeName));
						}
					}
					else
					{
						throw new IllegalStateException(Messages.ThemeManager_ERR_ThemeNoName);
					}
				}
				finally
				{
					try
					{
						stream.close();
					}
					catch (IOException e)
					{
						// ignore
					}
				}

			}
			catch (Exception e)
			{
				ThemePlugin.logError(url.toString(), e);
			}
		}

		// Handle a theme extending another theme
		for (Properties props : new ArrayList<Properties>(nameToThemeProperties.values())) // iterate in a copy!
		{
			try
			{
				String multipleThemeExtends = props.getProperty(Theme.THEME_EXTENDS_PROP_KEY);
				if (multipleThemeExtends != null)
				{
					Properties newProperties = new Properties();
					StringTokenizer tokenizer = new StringTokenizer(multipleThemeExtends, ","); //$NON-NLS-1$
					String name = props.getProperty(Theme.THEME_NAME_PROP_KEY);
					while (tokenizer.hasMoreTokens())
					{
						String themeExtends = tokenizer.nextToken();
						Properties extended = nameToThemeProperties.get(themeExtends);
						if (extended == null)
						{
							throw new IllegalStateException(MessageFormat.format(
									Messages.ThemeManager_ERR_NoThemeFound, themeExtends, name));
						}
						newProperties.putAll(extended);
					}
					newProperties.putAll(props);
					// We don't want the final extends props in the properties.
					newProperties.remove(Theme.THEME_EXTENDS_PROP_KEY);
					Assert.isTrue(newProperties.get(Theme.THEME_NAME_PROP_KEY).equals(name));
					nameToThemeProperties.put(name, newProperties);
				}
			}
			catch (Exception e)
			{
				ThemePlugin.logError(e);
			}
		}

		for (Properties props : nameToThemeProperties.values())
		{
			String name = props.getProperty(Theme.THEME_NAME_PROP_KEY);
			if (name.startsWith("abstract_theme")) //$NON-NLS-1$
			{
				continue;
			}
			try
			{
				loadTheme(props);
			}
			catch (Exception e)
			{
				ThemePlugin.logError(e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private Collection<URL> getBuiltinThemeURLs()
	{
		ThemePlugin themePlugin = ThemePlugin.getDefault();
		if (themePlugin == null)
		{
			return Collections.emptyList();
		}
		Bundle bundle = themePlugin.getBundle();
		if (bundle == null)
		{
			return Collections.emptyList();
		}
		List<URL> collection = new ArrayList<URL>();
		Enumeration<URL> enumeration = bundle.findEntries("themes", "*.properties", false); //$NON-NLS-1$ //$NON-NLS-2$
		while (enumeration.hasMoreElements())
		{
			collection.add(enumeration.nextElement());
		}
		return collection;
	}

	private void loadTheme(Properties props)
	{
		Theme theme = new Theme(ThemePlugin.getDefault().getColorManager(), props);
		fThemeMap.put(theme.getName(), theme);
		fBuiltins.add(theme.getName());
	}

	public IToken getToken(String scope)
	{
		return new Token(getTextAttribute(scope));
	}

	public void addTheme(Theme newTheme)
	{
		getThemeMap().put(newTheme.getName(), newTheme);
		newTheme.save();
		saveThemeList();
	}

	public void removeTheme(Theme theme)
	{
		Theme activeTheme = getCurrentTheme();
		getThemeMap().remove(theme.getName());
		saveThemeList();
		// change active theme if we just removed it
		if (activeTheme.getName().equals(theme.getName()))
		{
			setCurrentTheme(fThemeMap.values().iterator().next());
		}
	}

	public boolean isBuiltinTheme(String themeName)
	{
		return fBuiltins.contains(themeName);
	}

	public IStatus validateThemeName(String name)
	{
		if (name == null || name.trim().length() == 0)
			return new Status(IStatus.ERROR, ThemePlugin.PLUGIN_ID, Messages.ThemeManager_NameNonEmptyMsg);
		if (getThemeNames().contains(name.trim()))
			return new Status(IStatus.ERROR, ThemePlugin.PLUGIN_ID, Messages.ThemeManager_NameAlreadyExistsMsg);
		if (name.contains(THEME_NAMES_DELIMETER))
			return new Status(IStatus.ERROR, ThemePlugin.PLUGIN_ID, MessageFormat.format(
					Messages.ThemeManager_InvalidCharInThemeName, THEME_NAMES_DELIMETER));
		return Status.OK_STATUS;
	}
}
