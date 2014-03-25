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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.internal.preferences.Base64;
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
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.css.swt.theme.ITheme;
import org.eclipse.e4.ui.css.swt.theme.IThemeEngine;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.DataFormatException;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.AnnotationPreference;
import org.eclipse.ui.texteditor.MarkerAnnotationPreferences;
import org.osgi.framework.Bundle;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import com.aptana.core.IFilter;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.IOUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.scope.ScopeSelector;
import com.aptana.theme.IThemeManager;
import com.aptana.theme.Theme;
import com.aptana.theme.ThemePlugin;
import com.aptana.theme.ThemeRule;
import com.aptana.theme.internal.preferences.ThemerPreferenceInitializer;
import com.aptana.theme.preferences.IPreferenceConstants;
import com.aptana.ui.util.UIUtils;

@SuppressWarnings("restriction")
public class ThemeManager implements IThemeManager
{

	/**
	 * Node in preferences used to store themes under. Each theme is a key value pair under this node. The key is the
	 * theme name, value is XML format java Properties object.
	 */
	public static final String THEMES_NODE = "themes"; //$NON-NLS-1$
	// TODO Don't expose this node name. Fold saving/loading of themes into this impl

	private volatile Theme fCurrentTheme;
	private Set<String> fBuiltins;
	private Set<String> fThemeNames;

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
		EclipseUtil.instanceScope().getNode("org.eclipse.ui.editors").addPreferenceChangeListener( //$NON-NLS-1$
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
									EclipseUtil.setSystemForJob(job);
									job.setPriority(Job.DECORATE);
									job.schedule();
								}
								else
								{
									if (!getCurrentTheme().hasEntry(scopeSelector))
									{
										// Store that the user has overridden this annotation in this theme
										int index = getCurrentTheme().getTokens().size();
										getCurrentTheme().addNewRule(index, "Annotation Override - " + prefix, //$NON-NLS-1$
												new ScopeSelector(scopeSelector), null);
									}

								}
								break;
							}
						}
					}
				});
	}

	public synchronized static ThemeManager instance()
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
		{
			return getCurrentTheme().getTextAttribute(name);
		}
		return new TextAttribute(ThemePlugin.getDefault().getColorManager().getColor(new RGB(255, 255, 255)));
	}

	/**
	 * Lazily init the current theme.
	 */
	public Theme getCurrentTheme()
	{
		if (fCurrentTheme == null)
		{
			synchronized (this)
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
					if (!getThemeNames().isEmpty())
					{
						fCurrentTheme = getTheme(getThemeNames().iterator().next());
					}
				}
				if (fCurrentTheme != null)
				{
					setCurrentTheme(fCurrentTheme);
				}
			}
		}
		return fCurrentTheme;
	}

	/**
	 * Set the new theme to use, this involves setting prefs across a number of plugins.
	 */
	public void setCurrentTheme(final Theme theme)
	{
		fCurrentTheme = theme;

		// Set the find in file search color
		setSearchResultColor(theme);

		// Set the color for the search result annotation, the pref key is "searchResultIndicationColor"
		setAnnotationColorsToMatchTheme(theme);

		// Also set the standard eclipse editor props, like fg, bg, selection fg, bg
		setAptanaEditorColorsToMatchTheme(theme);

		// Set the diff/compare colors based on theme
		setCompareColors("com.aptana.editor.common", true); //$NON-NLS-1$
		setCompareColors("org.eclipse.ui.editors", ThemePlugin.applyToAllEditors()); //$NON-NLS-1$

		UIUtils.runInUIThread(new Runnable()
		{
			public void run()
			{
				// Also set overall theme
				IWorkbench workbench = PlatformUI.getWorkbench();
				MApplication application = (MApplication) workbench.getService(MApplication.class);
				IEclipseContext context = application.getContext();

				IThemeEngine e4ThemeEngine = context.get(IThemeEngine.class);
				ITheme selection = CollectionsUtil.find(e4ThemeEngine.getThemes(), new IFilter<ITheme>()
				{
					public boolean include(ITheme item)
					{
						return theme.getName().equals(item.getLabel());
					}
				});
				if (selection != null)
				{
					e4ThemeEngine.setTheme(selection, false);
				}

				// We notify in UI-thread because of APSTUD-7392
				// (in practice this almost always happens in the UI thread anyways, but it's
				// possible that at some circumstance this happens from a background thread).
				notifyThemeChangeListeners(fCurrentTheme);
			}
		});

		forceFontsUpToDate();
	}

	// APSTUD-4152
	private void setCompareColors(String nodeName, boolean override)
	{
		IEclipsePreferences instancePrefs = EclipseUtil.instanceScope().getNode(nodeName);

		if (override)
		{
			RGB bg = getCurrentTheme().getBackground();
			RGB inverted = new RGB(255 - bg.red, 255 - bg.green, 255 - bg.blue);

			JFaceResources.getColorRegistry().put("INCOMING_COLOR", inverted); //$NON-NLS-1$
			JFaceResources.getColorRegistry().put("OUTGOING_COLOR", inverted); //$NON-NLS-1$
			instancePrefs.put("INCOMING_COLOR", StringConverter.asString(inverted)); //$NON-NLS-1$
			instancePrefs.put("OUTGOING_COLOR", StringConverter.asString(inverted)); //$NON-NLS-1$
		}
		else
		{
			// Revert to defaults if we have them
			IEclipsePreferences defPrefs = EclipseUtil.defaultScope().getNode(nodeName);
			String value = defPrefs.get("OUTGOING_COLOR", null); //$NON-NLS-1$
			if (value != null)
			{
				try
				{
					RGB rgb = StringConverter.asRGB(value);
					if (rgb != null)
					{
						JFaceResources.getColorRegistry().put("OUTGOING_COLOR", rgb); //$NON-NLS-1$
					}
				}
				catch (DataFormatException e)
				{
					// ignore
				}
			}
			value = defPrefs.get("INCOMING_COLOR", null); //$NON-NLS-1$
			if (value != null)
			{
				try
				{
					RGB rgb = StringConverter.asRGB(value);
					if (rgb != null)
					{
						JFaceResources.getColorRegistry().put("INCOMING_COLOR", rgb); //$NON-NLS-1$
					}
				}
				catch (DataFormatException e)
				{
					// ignore
				}
			}

			// Now remove the instance prefs
			instancePrefs.remove("INCOMING_COLOR"); //$NON-NLS-1$
			instancePrefs.remove("OUTGOING_COLOR"); //$NON-NLS-1$
		}

		try
		{
			instancePrefs.flush();
		}
		catch (BackingStoreException e)
		{
			IdeLog.logError(ThemePlugin.getDefault(), e);
		}
	}

	private void setSearchResultColor(Theme theme)
	{
		IEclipsePreferences prefs = EclipseUtil.instanceScope().getNode("org.eclipse.search"); //$NON-NLS-1$
		prefs.put("org.eclipse.search.potentialMatch.fgColor", toString(theme.getSearchResultColor())); //$NON-NLS-1$
		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			IdeLog.logError(ThemePlugin.getDefault(), e);
		}
	}

	private void forceFontsUpToDate()
	{
		final String[] fontIds = new String[] { IThemeManager.VIEW_FONT_NAME, JFaceResources.TEXT_FONT,
				"org.eclipse.ui.workbench.texteditor.blockSelectionModeFont" }; //$NON-NLS-1$
		UIUtils.getDisplay().asyncExec(new Runnable()
		{

			public void run()
			{
				for (String fontId : fontIds)
				{
					Font fFont = JFaceResources.getFontRegistry().get(fontId);
					// Only set new values if they're different from existing!
					Font existing = JFaceResources.getFont(fontId);
					String existingString = StringUtil.EMPTY;
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
		});
	}

	/**
	 * Set specific pref values that we use to listen for when the theme has changed across our plugins. This ignals to
	 * them the theme has been changed and they need to update their settings to match.
	 * 
	 * @param theme
	 */
	private void notifyThemeChangeListeners(Theme theme)
	{
		IEclipsePreferences prefs = EclipseUtil.instanceScope().getNode(ThemePlugin.PLUGIN_ID);
		prefs.put(IPreferenceConstants.ACTIVE_THEME, theme.getName());
		prefs.putLong(THEME_CHANGED, System.currentTimeMillis());
		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			IdeLog.logError(ThemePlugin.getDefault(), e);
		}
	}

	/**
	 * Set the FG, BG, selection and current line colors on our editors.
	 * 
	 * @param theme
	 */
	private void setAptanaEditorColorsToMatchTheme(Theme theme)
	{
		IEclipsePreferences prefs = EclipseUtil.instanceScope().getNode("com.aptana.editor.common"); //$NON-NLS-1$
		prefs.putBoolean(AbstractTextEditor.PREFERENCE_COLOR_SELECTION_FOREGROUND_SYSTEM_DEFAULT, false);
		prefs.put(AbstractTextEditor.PREFERENCE_COLOR_SELECTION_FOREGROUND, toString(theme.getForeground()));

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
			IdeLog.logError(ThemePlugin.getDefault(), e);
		}
	}

	private void setAnnotationColorsToMatchTheme(Theme theme)
	{
		IEclipsePreferences prefs = EclipseUtil.instanceScope().getNode("org.eclipse.ui.editors"); //$NON-NLS-1$
		if (!theme.hasEntry("override.searchResultIndication")) //$NON-NLS-1$
		{
			prefs.put("searchResultIndicationColor", toString(theme.getSearchResultColor())); //$NON-NLS-1$
		}
		// TODO Use markup.changed bg color for "decoration color" in Prefs>General>Appearance>Colors and Fonts

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
			IdeLog.logError(ThemePlugin.getDefault(), e);
		}
	}

	private static String toString(RGB selection)
	{
		return StringConverter.asString(selection);
	}

	/**
	 * Attempts to find the theme with a given name, first from prefs, then from pre-packaged builtins. Will return null
	 * if no match is found.
	 */
	public Theme getTheme(String name)
	{
		// Try to see if we have a copy in prefs as a user theme
		Theme loaded = null;
		try
		{
			loaded = loadUserTheme(name);
		}
		catch (Exception e)
		{
			IdeLog.logError(ThemePlugin.getDefault(),
					MessageFormat.format("Failed to load theme {0} from preferences.", name), e); //$NON-NLS-1$
		}
		if (loaded != null)
		{
			return loaded;
		}
		// Ok, no user theme by that name, load up the builtins. Loading them once should save a copy to prefs (user)
		// for future...
		try
		{
			return loadBuiltinTheme(name);
		}
		catch (Exception e)
		{
			IdeLog.logError(ThemePlugin.getDefault(),
					MessageFormat.format("Failed to load theme {0} from builtins.", name), e); //$NON-NLS-1$
		}
		return null;
	}

	/**
	 * laziliy init the set of theme names.
	 */
	public synchronized Set<String> getThemeNames()
	{
		if (fThemeNames == null)
		{
			fThemeNames = new HashSet<String>();
			// Add names of themes from builtins...
			fThemeNames.addAll(getBuiltinThemeNames());

			// Look in prefs to see what user themes are stored there, garb their names
			IScopeContext[] scopes = new IScopeContext[] { EclipseUtil.instanceScope(), EclipseUtil.defaultScope() };
			for (IScopeContext scope : scopes)
			{
				IEclipsePreferences prefs = scope.getNode(ThemePlugin.PLUGIN_ID);
				Preferences preferences = prefs.node(ThemeManager.THEMES_NODE);
				try
				{
					String[] themeNames = preferences.keys();
					fThemeNames.addAll(Arrays.asList(themeNames));
				}
				catch (BackingStoreException e)
				{
					IdeLog.logError(ThemePlugin.getDefault(), e);
				}
			}
		}
		return fThemeNames;
	}

	private Theme loadUserTheme(String themeName)
	{
		InputStream byteStream = null;
		try
		{
			byte[] array = Platform.getPreferencesService().getByteArray(ThemePlugin.PLUGIN_ID,
					THEMES_NODE + "/" + themeName, null, null); //$NON-NLS-1$
			if (array == null)
			{
				return null;
			}
			byteStream = new ByteArrayInputStream(array);
			Properties props = new OrderedProperties();
			props.load(byteStream);
			// if it looks like the byte array was not Base64 decoded, try decoding and then running it through
			if (!props.containsKey(Theme.THEME_NAME_PROP_KEY)) // anything else we can check for this?
			{
				IdeLog.logWarning(
						ThemePlugin.getDefault(),
						MessageFormat
								.format("User theme {0} de-serialized, but was left Base64 encoded. Manually decoding and trying to load.", //$NON-NLS-1$
										themeName));
				byteStream = new ByteArrayInputStream(Base64.decode(array));
				props = new OrderedProperties();
				props.load(byteStream);
			}
			return new Theme(ThemePlugin.getDefault().getColorManager(), props);
		}
		catch (IllegalArgumentException iae)
		{
			// Fallback to load theme that was saved in prefs as XML string
			String xml = Platform.getPreferencesService().getString(ThemePlugin.PLUGIN_ID,
					THEMES_NODE + "/" + themeName, null, null); //$NON-NLS-1$
			if (xml != null)
			{
				InputStream stream = null;
				try
				{
					stream = new ByteArrayInputStream(xml.getBytes(IOUtil.UTF_8));
					Properties props = new OrderedProperties();
					props.loadFromXML(stream);
					// Now store it as byte array explicitly so we don't run into this!
					Theme theme = new Theme(ThemePlugin.getDefault().getColorManager(), props);
					theme.save();
					return theme;
				}
				catch (Exception e)
				{
					IdeLog.logError(ThemePlugin.getDefault(), e);
				}
				finally
				{
					if (stream != null)
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
			}
		}
		catch (IOException e)
		{
			IdeLog.logError(ThemePlugin.getDefault(), e);
		}
		finally
		{
			if (byteStream != null)
			{
				try
				{
					byteStream.close();
				}
				catch (IOException e)
				{
					// ignore
				}
			}
		}
		return null;
	}

	private OrderedProperties getBuiltinThemeProperties(String themeName)
	{
		Collection<URL> urls = getBuiltinThemeURLs();
		if (CollectionsUtil.isEmpty(urls))
		{
			return null;
		}

		for (URL url : urls)
		{
			try
			{
				// Try forcing the file to be extracted out from zip before we try to read it
				InputStream stream = FileLocator.toFileURL(url).openStream();
				try
				{
					OrderedProperties props = new OrderedProperties();
					props.load(stream);
					String loadedName = props.getProperty(Theme.THEME_NAME_PROP_KEY);
					if (!themeName.equals(loadedName))
					{
						continue;
					}

					String multipleThemeExtends = props.getProperty(Theme.THEME_EXTENDS_PROP_KEY);
					// If we extend one or more other themes, recursively load their properties...
					if (multipleThemeExtends != null)
					{
						OrderedProperties newProperties = new OrderedProperties();
						String[] pieces = multipleThemeExtends.split(","); //$NON-NLS-1$
						for (String themeExtends : pieces)
						{
							Properties extended = getBuiltinThemeProperties(themeExtends);
							if (extended == null)
							{
								throw new IllegalStateException(MessageFormat.format(
										Messages.ThemeManager_ERR_NoThemeFound, themeExtends, loadedName));
							}
							newProperties.putAll(extended);
						}
						newProperties.putAll(props);
						// We don't want the final extends props in the properties.
						newProperties.remove(Theme.THEME_EXTENDS_PROP_KEY);
						// Sanity check
						Assert.isTrue(newProperties.get(Theme.THEME_NAME_PROP_KEY).equals(themeName));
						return newProperties;
					}
					return props;
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
				IdeLog.logError(ThemePlugin.getDefault(), url.toString(), e);
			}
		}
		return null;
	}

	private synchronized Set<String> getBuiltinThemeNames()
	{
		if (fBuiltins == null)
		{
			fBuiltins = new HashSet<String>();
			Collection<URL> urls = getBuiltinThemeURLs();
			if (urls == null || urls.isEmpty())
			{
				return fBuiltins;
			}

			for (URL url : urls)
			{
				InputStream stream = null;
				try
				{
					// Try forcing the file to be extracted out from zip before we try to read it
					stream = FileLocator.toFileURL(url).openStream();
					OrderedProperties props = new OrderedProperties();
					props.load(stream);
					String loadedName = props.getProperty(Theme.THEME_NAME_PROP_KEY);
					// Don't include the abstract themes in the list, they're meant just for extending
					if (loadedName != null && !loadedName.startsWith("abstract_theme")) //$NON-NLS-1$
					{
						fBuiltins.add(loadedName);
					}
				}
				catch (Exception e)
				{
					IdeLog.logError(ThemePlugin.getDefault(), e);
				}
				finally
				{
					try
					{
						if (stream != null)
						{
							stream.close();
						}
					}
					catch (IOException e)
					{
						// ignore
					}
				}
			}
		}
		return fBuiltins;
	}

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
		ArrayList<URL> collection = new ArrayList<URL>();
		Enumeration<URL> enumeration = bundle.findEntries("themes", "*.properties", false); //$NON-NLS-1$ //$NON-NLS-2$
		while (enumeration.hasMoreElements())
		{
			collection.add(enumeration.nextElement());
		}
		collection.trimToSize();
		return collection;
	}

	public Theme loadBuiltinTheme(String themeName)
	{
		OrderedProperties properties = getBuiltinThemeProperties(themeName);
		if (properties == null)
		{
			return null;
		}
		return loadBuiltinTheme(properties);
	}

	private Theme loadBuiltinTheme(Properties props)
	{
		try
		{
			return new Theme(ThemePlugin.getDefault().getColorManager(), props);
		}
		catch (Exception e)
		{
			IdeLog.logError(ThemePlugin.getDefault(), e);
		}
		return null;
	}

	public IToken getToken(String scope)
	{
		return new Token(getTextAttribute(scope));
	}

	public void addTheme(Theme newTheme)
	{
		newTheme.save();
		getThemeNames().add(newTheme.getName());
	}

	public void removeTheme(Theme theme)
	{
		Theme activeTheme = getCurrentTheme();
		getThemeNames().remove(theme.getName());
		// change active theme if we just removed it
		if (activeTheme.getName().equals(theme.getName()))
		{
			// load first theme from list of names
			setCurrentTheme(getTheme(getThemeNames().iterator().next()));
		}
	}

	public boolean isBuiltinTheme(String themeName)
	{
		return getBuiltinThemeNames().contains(themeName);
	}

	public IStatus validateThemeName(String name)
	{
		if (StringUtil.isEmpty(name))
		{
			return new Status(IStatus.ERROR, ThemePlugin.PLUGIN_ID, Messages.ThemeManager_NameNonEmptyMsg);
		}
		if (getThemeNames().contains(name.trim()))
		{
			return new Status(IStatus.ERROR, ThemePlugin.PLUGIN_ID, Messages.ThemeManager_NameAlreadyExistsMsg);
		}
		return Status.OK_STATUS;
	}
}
