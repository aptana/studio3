/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.theme;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.eclipse.ui.console.TextConsole;

import com.aptana.core.logging.IdeLog;

/**
 * This extension will make sure that the colors for the console are always following the theme preferences.
 */
@SuppressWarnings("rawtypes")
public class ConsoleThemer
{

	// Usually (default eclipse colors, so, white-based background):
	// error = red
	// output = foreground
	// input = green
	// prompt = blue

	public static final String CONSOLE_ERROR = "console.error"; //$NON-NLS-1$
	public static final String CONSOLE_OUTPUT = "console.output"; //$NON-NLS-1$
	public static final String CONSOLE_INPUT = "console.input"; //$NON-NLS-1$
	public static final String CONSOLE_PROMPT = "console.prompt"; //$NON-NLS-1$

	// Colors for a message console (no input from the user)
	// warning = yellow
	// trace = prompt
	// info = input
	public static final String CONSOLE_WARNING = "console.warning"; //$NON-NLS-1$
	public static final String CONSOLE_TRACE = CONSOLE_PROMPT;
	public static final String CONSOLE_INFO = CONSOLE_INPUT;

	private IPreferenceChangeListener fThemeChangeListener;

	private TextConsole fConsole;
	private Map fThemeConsoleStreamToColor;

	/**
	 * Should be called in the UI thread. Usually, there's no way to create this extension from any console, as the
	 * ConsoleThemePageParticipant takes care of that for all consoles (provided they are properly configured).
	 * 
	 * @see com.aptana.theme.extensions.ConsoleThemePageParticipant
	 * @param textConsole
	 *            console with the streams.
	 * @param themeConsoleStreamToColor
	 *            a map with the stream to the related color name (one of the CONSOLE_XXX constants in this class).
	 */
	public ConsoleThemer(TextConsole textConsole, Map themeConsoleStreamToColor)
	{
		this.fConsole = textConsole;
		this.fThemeConsoleStreamToColor = themeConsoleStreamToColor;

		this.listenForThemeChanges();

		// apply theme
		this.applyTheme();
	}

	/**
	 * applyTheme
	 */
	private void applyTheme()
	{
		IWorkbench workbench = null;

		try
		{
			workbench = PlatformUI.getWorkbench();
		}
		catch (IllegalStateException e)
		{
			IdeLog.logError(ThemePlugin.getDefault(), e);
		}

		if (workbench != null)
		{
			final Display display = workbench.getDisplay();

			display.syncExec(new Runnable()
			{
				@SuppressWarnings("unchecked")
				public void run()
				{
					// set colors
					ThemePlugin plugin = ThemePlugin.getDefault();
					ColorManager colorManager = plugin.getColorManager();
					Theme theme = plugin.getThemeManager().getCurrentTheme();

					// set background color
					// NOTE: we have to force the background color to change; otherwise, even
					// with a forced redraw, the background will not be drawn
					fConsole.setBackground(null);
					fConsole.setBackground(colorManager.getColor(theme.getBackground()));

					// set default stream colors
					// Note that some colors are repeated because they're used in different scenarios.
					HashMap<String, Color> colorNameToDefault = new HashMap<String, Color>();
					Color blue = display.getSystemColor(SWT.COLOR_DARK_BLUE);
					Color green = display.getSystemColor(SWT.COLOR_DARK_GREEN);
					Color yellow = display.getSystemColor(SWT.COLOR_DARK_YELLOW);
					Color red = display.getSystemColor(SWT.COLOR_DARK_RED);

					colorNameToDefault.put(CONSOLE_ERROR, red);

					// Info is the same as input color
					colorNameToDefault.put(CONSOLE_INFO, green);
					colorNameToDefault.put(CONSOLE_INPUT, green);

					// For CONSOLE_OUTPUT stream we should use the foreground color of the theme
					colorNameToDefault.put(CONSOLE_OUTPUT, colorManager.getColor(theme.getForeground()));

					// Prompt is the same as trace color.
					colorNameToDefault.put(CONSOLE_PROMPT, blue);
					colorNameToDefault.put(CONSOLE_TRACE, blue);
					colorNameToDefault.put(CONSOLE_WARNING, yellow);

					Set<Map.Entry> entrySet = fThemeConsoleStreamToColor.entrySet();
					for (Map.Entry entry : entrySet)
					{
						if (entry.getValue() instanceof String && entry.getKey() instanceof IOConsoleOutputStream)
						{
							String colorName = (String) entry.getValue();
							IOConsoleOutputStream stream = (IOConsoleOutputStream) entry.getKey();
							applyTheme(colorName, stream, colorNameToDefault.get(colorName));
						}
					}

					refresh();
				}

			});
		}
	}

	/**
	 * refresh
	 */
	public void refresh()
	{
		// refresh the display
		ConsolePlugin.getDefault().getConsoleManager().refresh(fConsole);
	}

	/**
	 * applyTheme
	 * 
	 * @param name
	 * @param stream
	 * @param defaultColor
	 * @return
	 */
	private void applyTheme(String name, IOConsoleOutputStream stream, Color defaultColor)
	{
		Theme theme = ThemePlugin.getDefault().getThemeManager().getCurrentTheme();
		Color color = defaultColor;
		int style = SWT.NONE;

		// grab theme values, if they exist
		if (theme.hasEntry(name))
		{
			TextAttribute attr = theme.getTextAttribute(name);

			color = theme.getForeground(name);
			style = attr.getStyle();
		}

		// apply new values
		stream.setColor(color);
		stream.setFontStyle(style);
	}

	/**
	 * listenForThemeChanges
	 */
	private void listenForThemeChanges()
	{
		this.fThemeChangeListener = new IPreferenceChangeListener()
		{
			public void preferenceChange(PreferenceChangeEvent event)
			{
				if (event.getKey().equals(IThemeManager.THEME_CHANGED))
				{
					applyTheme();
				}
			}
		};

		InstanceScope.INSTANCE.getNode(ThemePlugin.PLUGIN_ID).addPreferenceChangeListener(this.fThemeChangeListener);
	}

	/**
	 * Stop listening changes (and dispose of what's needed).
	 */
	public void dispose()
	{
		InstanceScope.INSTANCE.getNode(ThemePlugin.PLUGIN_ID).removePreferenceChangeListener(this.fThemeChangeListener);
		this.fConsole = null;
		this.fThemeConsoleStreamToColor = null;
	}
}
