package com.aptana.radrails.editor.common.theme;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.radrails.editor.common.CommonEditorPlugin;

public abstract class ThemeUtil
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
	 * Preference key used to save the active theme.
	 */
	public static final String ACTIVE_THEME = "ACTIVE_THEME"; //$NON-NLS-1$

	/**
	 * Node in preferences used to store themes under. Each theme is a key value pair under this node. The key is the
	 * theme name, value is XML format java Properties object.
	 */
	public static final String THEMES_NODE = "themes"; //$NON-NLS-1$

	private static Theme fgTheme;
	private static HashMap<String, Theme> fgThemeMap;
	private static Map<WeakReference<Token>, String> fgTokens = new HashMap<WeakReference<Token>, String>();

	private static Theme loadTheme(InputStream stream)
	{
		try
		{
			Properties props = new Properties();
			props.load(stream);
			return new Theme(CommonEditorPlugin.getDefault().getColorManager(), props);
		}
		catch (Exception e)
		{
			// ignore
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
		return null;
	}

	private static TextAttribute getTextAttribute(String name)
	{
		if (getActiveTheme() != null)
			return getActiveTheme().getTextAttribute(name);
		return new TextAttribute(CommonEditorPlugin.getDefault().getColorManager().getColor(new RGB(255, 255, 255)));
	}

	public static Theme getActiveTheme()
	{
		if (fgTheme == null)
		{
			String activeThemeName = Platform.getPreferencesService().getString(CommonEditorPlugin.PLUGIN_ID,
					ACTIVE_THEME, null, null);
			if (activeThemeName != null)
				fgTheme = getTheme(activeThemeName);
			if (fgTheme == null)
				setActiveTheme(getThemeMap().values().iterator().next());
		}
		return fgTheme;
	}

	public static void setActiveTheme(Theme theme)
	{
		fgTheme = theme;
		adaptTokens();
		IEclipsePreferences prefs = new InstanceScope().getNode(CommonEditorPlugin.PLUGIN_ID);
		prefs.put(ACTIVE_THEME, theme.getName());

		// Also set the standard eclipse editor props, like fg, bg, selection fg, bg
		prefs.putBoolean(AbstractTextEditor.PREFERENCE_COLOR_SELECTION_FOREGROUND_SYSTEM_DEFAULT, false);
		prefs.put(AbstractTextEditor.PREFERENCE_COLOR_SELECTION_FOREGROUND, toString(theme.getSelection()));

		prefs.putBoolean(AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND_SYSTEM_DEFAULT, false);
		prefs.put(AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND, toString(theme.getBackground()));

		prefs.put(AbstractTextEditor.PREFERENCE_COLOR_FOREGROUND, toString(theme.getForeground()));
		prefs.putBoolean(AbstractTextEditor.PREFERENCE_COLOR_FOREGROUND_SYSTEM_DEFAULT, false);

		prefs.put(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE_COLOR, toString(theme
				.getLineHighlight()));
		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			// ignore
		}
	}

	private static String toString(RGB selection)
	{
		StringBuilder builder = new StringBuilder();
		builder.append(selection.red).append(THEME_NAMES_DELIMETER).append(selection.green).append(
				THEME_NAMES_DELIMETER).append(selection.blue);
		return builder.toString();
	}

	public static Theme getTheme(String name)
	{
		return getThemeMap().get(name);
	}

	private static Map<String, Theme> getThemeMap()
	{
		if (fgThemeMap == null)
		{
			loadThemes();
		}
		return fgThemeMap;
	}

	public static Set<String> getThemeNames()
	{
		return getThemeMap().keySet();
	}

	private static void loadThemes()
	{
		fgThemeMap = new HashMap<String, Theme>();
		// Load builtin themes stored in properties files
		loadBuiltinThemes();
		// Load themes from the preferences
		loadUserThemes();

		StringBuilder builder = new StringBuilder();
		for (String themeName : fgThemeMap.keySet())
		{
			// FIXME What if the themeName contains our delimeter?!
			builder.append(themeName).append(THEME_NAMES_DELIMETER);
		}
		builder.deleteCharAt(builder.length() - 1);
		IEclipsePreferences prefs = new InstanceScope().getNode(CommonEditorPlugin.PLUGIN_ID);
		prefs.put(THEME_LIST_PREF_KEY, builder.toString());

	}

	private static void loadUserThemes()
	{
		String themeNames = Platform.getPreferencesService().getString(CommonEditorPlugin.PLUGIN_ID,
				THEME_LIST_PREF_KEY, null, null);
		if (themeNames == null)
			return;
		StringTokenizer tokenizer = new StringTokenizer(themeNames, THEME_NAMES_DELIMETER);
		while (tokenizer.hasMoreElements())
		{
			try
			{
				String themeName = tokenizer.nextToken();
				String xmlProps = Platform.getPreferencesService().getString(CommonEditorPlugin.PLUGIN_ID,
						THEMES_NODE + "/" + themeName, null, null); //$NON-NLS-1$
				if (xmlProps == null || xmlProps.trim().length() == 0)
					continue;
				Properties props = new Properties();
				props.loadFromXML(new ByteArrayInputStream(xmlProps.getBytes("UTF-8"))); //$NON-NLS-1$
				Theme theme = new Theme(CommonEditorPlugin.getDefault().getColorManager(), props);
				fgThemeMap.put(theme.getName(), theme);
			}
			catch (Exception e)
			{
				CommonEditorPlugin.logError(e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static void loadBuiltinThemes()
	{
		Enumeration<URL> urls = CommonEditorPlugin.getDefault().getBundle()
				.findEntries("themes", "*.properties", false); //$NON-NLS-1$ //$NON-NLS-2$
		if (urls == null)
			return;
		while (urls.hasMoreElements())
		{
			URL url = urls.nextElement();
			try
			{
				Theme theme = loadTheme(url.openStream());
				if (theme != null)
				{
					fgThemeMap.put(theme.getName(), theme);
				}
			}
			catch (Exception e)
			{
				CommonEditorPlugin.logError(url.toString(), e);
			}
		}
	}

	public static IToken getToken(String string)
	{
		Token token = new Token(getTextAttribute(string));
		fgTokens.put(new WeakReference<Token>(token), string);
		return token;
	}

	private static void adaptTokens()
	{
		for (Map.Entry<WeakReference<Token>, String> entry : fgTokens.entrySet())
		{
			Token token = entry.getKey().get();
			if (token != null)
				token.setData(getTextAttribute(entry.getValue()));
		}
	}
}
