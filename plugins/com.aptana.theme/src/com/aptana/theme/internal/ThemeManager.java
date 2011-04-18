/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
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
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.AnnotationPreference;
import org.eclipse.ui.texteditor.MarkerAnnotationPreferences;
import org.osgi.framework.Bundle;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.core.util.EclipseUtil;
import com.aptana.scope.ScopeSelector;
import com.aptana.theme.IThemeManager;
import com.aptana.theme.Theme;
import com.aptana.theme.ThemePlugin;
import com.aptana.theme.ThemeRule;
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

	/**
	 * The common prefixes of prefs related to annotations that we typically modify
	 */
	private static final String[] annotationKeyPrefixes = new String[] { "pydevOccurrenceIndication", //$NON-NLS-1$
			"searchResultIndication", //$NON-NLS-1$
			"xmlTagPairOccurrenceIndication", //$NON-NLS-1$
			"htmlTagPairOccurrenceIndication", //$NON-NLS-1$
			"rubyBlockPairOccurrenceIndication", //$NON-NLS-1$
	};

	private ThemeManager()
	{
		new InstanceScope().getNode("org.eclipse.ui.editors").addPreferenceChangeListener( //$NON-NLS-1$
				new IPreferenceChangeListener()
				{

					public void preferenceChange(PreferenceChangeEvent event)
					{
						// Listen to see if the user is modifying the annotations through Annotations pref page
						for (String prefix : annotationKeyPrefixes)
						{
							if (event.getKey().startsWith(prefix))
							{
								final String scopeSelector = "override." + prefix; //$NON-NLS-1$
								// If it's color and getting set to null, then it probably means that user
								// chose to restore defaults. Does that mean we should remove override?
								if (event.getNewValue() == null && event.getKey().endsWith("Color")) //$NON-NLS-1$
								{
									// Do we need to run this in a delayed job to avoid clashes when the other pref
									// changes come through at same time...?
									Job job = new UIJob("Restoring overrides of Annotation") //$NON-NLS-1$
									{
										@Override
										public IStatus runInUIThread(IProgressMonitor monitor)
										{
											ThemeRule rule = getCurrentTheme().getRuleForSelector(
													new ScopeSelector(scopeSelector));
											if (rule != null)
											{
												getCurrentTheme().remove(rule);
											}
											return Status.OK_STATUS;
										}
									};
									job.setSystem(!EclipseUtil.showSystemJobs());
									job.setPriority(Job.DECORATE);
									job.schedule();
								}
								else
								{
									if (!getCurrentTheme().hasEntry(scopeSelector))
									{
										// Store that the user has overridden this annotation in this theme
										int index = getCurrentTheme().getTokens().size();
										getCurrentTheme().addNewRule(index, "Annotation Override - " + prefix,
												new ScopeSelector(scopeSelector), null);
									}

								}
								break;
							}
						}
					}
				});
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
		if (!theme.hasEntry("override.searchResultIndication")) //$NON-NLS-1$
		{
			prefs.put("searchResultIndicationColor", toString(theme.getSearchResultColor())); //$NON-NLS-1$
		}
		// TODO Move this stuff over to theme change listeners in the XML/HTML/Ruby editor plugins?
		if (!theme.hasEntry("override.xmlTagPairOccurrenceIndication")) //$NON-NLS-1$
		{
			prefs.putBoolean("xmlTagPairOccurrenceIndicationHighlighting", false); //$NON-NLS-1$
			prefs.putBoolean("xmlTagPairOccurrenceIndication", true); //$NON-NLS-1$
			prefs.put("xmlTagPairOccurrenceIndicationColor", toString(theme.getOccurenceHighlightColor())); //$NON-NLS-1$
			prefs.put("xmlTagPairOccurrenceIndicationTextStyle", AnnotationPreference.STYLE_BOX); //$NON-NLS-1$
		}
		if (!theme.hasEntry("override.htmlTagPairOccurrenceIndication")) //$NON-NLS-1$
		{
			prefs.putBoolean("htmlTagPairOccurrenceIndicationHighlighting", false); //$NON-NLS-1$
			prefs.putBoolean("htmlTagPairOccurrenceIndication", true); //$NON-NLS-1$
			prefs.put("htmlTagPairOccurrenceIndicationColor", toString(theme.getOccurenceHighlightColor())); //$NON-NLS-1$
			prefs.put("htmlTagPairOccurrenceIndicationTextStyle", AnnotationPreference.STYLE_BOX); //$NON-NLS-1$
		}
		if (!theme.hasEntry("override.rubyBlockPairOccurrenceIndication")) //$NON-NLS-1$
		{
			prefs.putBoolean("rubyBlockPairOccurrenceIndicationHighlighting", false); //$NON-NLS-1$
			prefs.putBoolean("rubyBlockPairOccurrenceIndication", true); //$NON-NLS-1$
			prefs.put("rubyBlockPairOccurrenceIndicationColor", toString(theme.getOccurenceHighlightColor())); //$NON-NLS-1$
			prefs.put("rubyBlockPairOccurrenceIndicationTextStyle", AnnotationPreference.STYLE_BOX); //$NON-NLS-1$
		}
		// PyDev Occurrences (com.python.pydev.occurrences)
		// Override them if pydev is set to use our themes
		if (Platform.getPreferencesService().getBoolean("org.python.pydev.red_core", "PYDEV_USE_APTANA_THEMES", true, //$NON-NLS-1$ //$NON-NLS-2$
				null))
		{
			if (!theme.hasEntry("override.pydevOccurrenceIndication")) //$NON-NLS-1$
			{
				MarkerAnnotationPreferences preferences = new MarkerAnnotationPreferences();
				AnnotationPreference pydevOccurPref = null;
				for (Object obj : preferences.getAnnotationPreferences())
				{
					AnnotationPreference pref = (AnnotationPreference) obj;
					Object type = pref.getAnnotationType();
					if ("com.python.pydev.occurrences".equals(type)) //$NON-NLS-1$
					{
						pydevOccurPref = pref;
					}
				}
				if (pydevOccurPref != null)
				{
					if (pydevOccurPref.getTextStylePreferenceKey() != null)
					{
						// Now that pydev supports text style, use the box style and don't highlight.
						prefs.putBoolean("pydevOccurrenceHighlighting", false); //$NON-NLS-1$
						prefs.putBoolean("pydevOccurrenceIndication", true); //$NON-NLS-1$
						prefs.put("pydevOccurrenceIndicationColor", toString(theme.getOccurenceHighlightColor())); //$NON-NLS-1$
						prefs.put("pydevOccurrenceIndicationTextStyle", AnnotationPreference.STYLE_BOX); //$NON-NLS-1$
					}
					else
					{
						// Must use highlighting, since we're against older pydev that had no text style
						prefs.putBoolean("pydevOccurrenceHighlighting", true); //$NON-NLS-1$
						prefs.putBoolean("pydevOccurrenceIndication", true); //$NON-NLS-1$
						prefs.put("pydevOccurrenceIndicationColor", toString(theme.getSearchResultColor())); //$NON-NLS-1$
					}
				}
			}
		}

		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			ThemePlugin.logError(e);
		}

		// Set the bg/fg/selection colors for compare editors
		prefs = new InstanceScope().getNode("org.eclipse.compare"); //$NON-NLS-1$
		prefs.putBoolean(AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND_SYSTEM_DEFAULT, false);
		prefs.put(AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND, StringConverter.asString(theme.getBackground()));
		prefs.putBoolean(AbstractTextEditor.PREFERENCE_COLOR_FOREGROUND_SYSTEM_DEFAULT, false);
		prefs.put(AbstractTextEditor.PREFERENCE_COLOR_FOREGROUND, StringConverter.asString(theme.getForeground()));
		prefs.putBoolean(AbstractTextEditor.PREFERENCE_COLOR_SELECTION_BACKGROUND_SYSTEM_DEFAULT, false);
		prefs.put(AbstractTextEditor.PREFERENCE_COLOR_SELECTION_BACKGROUND,
				StringConverter.asString(theme.getSelectionAgainstBG()));
		prefs.putBoolean(AbstractTextEditor.PREFERENCE_COLOR_SELECTION_FOREGROUND_SYSTEM_DEFAULT, false);
		prefs.put(AbstractTextEditor.PREFERENCE_COLOR_SELECTION_FOREGROUND,
				StringConverter.asString(theme.getForeground()));
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

		// Force font
		// FIXME We need to run this in the UI thread(?)
		final String[] fontIds = new String[] { IThemeManager.VIEW_FONT_NAME, JFaceResources.TEXT_FONT,
				"org.eclipse.ui.workbench.texteditor.blockSelectionModeFont" }; //$NON-NLS-1$
		for (String fontId : fontIds)
		{
			Font fFont = JFaceResources.getFontRegistry().get(fontId);
			// Only set new values if they're different from existing!
			Font existing = JFaceResources.getFont(fontId);
			String existingString = ""; //$NON-NLS-1$
			if (!existing.isDisposed())
			{
				existingString = PreferenceConverter.getStoredRepresentation(existing.getFontData());
			}
			String fdString = PreferenceConverter.getStoredRepresentation(fFont.getFontData());
			if (!existingString.equals(fdString))
			{
				// put in registry...
				JFaceResources.getFontRegistry().put(fontId, fFont.getFontData());
			}
		}
	}

	private static String toString(RGB selection)
	{
		return StringConverter.asString(selection);
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
			String themeName = tokenizer.nextToken();
			Theme theme = loadUserTheme(themeName);
			if (theme == null)
			{
				continue;
			}
			fThemeMap.put(theme.getName(), theme);
		}
	}

	private Theme loadUserTheme(String themeName)
	{
		try
		{
			byte[] array = Platform.getPreferencesService().getByteArray(ThemePlugin.PLUGIN_ID,
					THEMES_NODE + "/" + themeName, null, null); //$NON-NLS-1$
			if (array == null)
			{
				return null;
			}
			Properties props = new OrderedProperties();
			props.load(new ByteArrayInputStream(array));
			Theme theme = new Theme(ThemePlugin.getDefault().getColorManager(), props);
			return theme;
		}
		catch (IllegalArgumentException iae)
		{
			// Fallback to load theme that was saved in prefs as XML string
			String xml = Platform.getPreferencesService().getString(ThemePlugin.PLUGIN_ID,
					THEMES_NODE + "/" + themeName, null, null); //$NON-NLS-1$
			if (xml != null)
			{
				try
				{
					Properties props = new OrderedProperties();
					props.loadFromXML(new ByteArrayInputStream(xml.getBytes("UTF-8"))); //$NON-NLS-1$
					// Now store it as byte array explicitly so we don't run into this!
					Theme theme = new Theme(ThemePlugin.getDefault().getColorManager(), props);
					theme.save();
					return theme;
				}
				catch (IOException e)
				{
					ThemePlugin.logError(e);
				}
			}
		}
		catch (IOException e)
		{
			ThemePlugin.logError(e);
		}
		return null;
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
				// Try forcing the file to be extracted out from zip before we try to read it
				InputStream stream = FileLocator.toFileURL(url).openStream();
				try
				{
					Properties props = new OrderedProperties();
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
					Properties newProperties = new OrderedProperties();
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
