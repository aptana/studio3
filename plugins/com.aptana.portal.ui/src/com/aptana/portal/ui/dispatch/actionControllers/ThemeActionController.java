/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.portal.ui.dispatch.actionControllers;

import java.text.MessageFormat;
import java.util.Set;

import com.aptana.configurations.processor.ConfigurationStatus;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;
import com.aptana.jetty.util.epl.ajax.JSON;
import com.aptana.portal.ui.PortalUIPlugin;
import com.aptana.portal.ui.dispatch.IBrowserNotificationConstants;
import com.aptana.theme.IThemeManager;
import com.aptana.theme.Theme;
import com.aptana.theme.ThemePlugin;

/**
 * Action controller that provides Theme functionalities.
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public class ThemeActionController extends AbstractActionController
{
	private static final String UNKNOWN = "Unknown"; //$NON-NLS-1$
	private IThemeManager themeManager;

	public ThemeActionController()
	{
		themeManager = ThemePlugin.getDefault().getThemeManager();
	}

	// ############## Actions ###############
	/**
	 * Returns a list of theme names.
	 * 
	 * <pre>
	 *   <b>Sample JS code:</b>
	 *   <code>result = dispatch($H({controller:'portal.themes', action:"getThemes"}).toJSON());</code>
	 * </pre>
	 */
	@ControllerAction
	public Object getThemes()
	{
		Set<String> themeNames = themeManager.getThemeNames();
		return JSON.toString(themeNames.toArray(new String[themeNames.size()]));
	}

	/**
	 * Returns the active theme name.
	 * 
	 * <pre>
	 *   <b>Sample JS code:</b>
	 *   <code>result = dispatch($H({controller:'portal.themes', action:"getActiveTheme"}).toJSON());</code>
	 * </pre>
	 */
	@ControllerAction
	public Object getActiveTheme()
	{
		Theme currentTheme = themeManager.getCurrentTheme();
		return JSON.toString(currentTheme != null ? currentTheme.getName() : UNKNOWN);
	}

	/**
	 * Set the active theme.
	 * 
	 * <pre>
	 *   <b>Sample JS code:</b>
	 *   <code>result = dispatch($H({controller:'portal.themes', action:"setActiveTheme", args:["theme-name"]}).toJSON());</code>
	 * </pre>
	 */
	@ControllerAction
	public Object setActiveTheme(Object attributes)
	{
		final String themeName = getThemeName(attributes);
		if (!StringUtil.isEmpty(themeName))
		{
			// FIXME this is a bit of a hack, and assumes we'll have an editor and overall theme with the exact same
			// name
			// Set editor theme
			Theme theme = themeManager.getTheme(themeName);
			themeManager.setCurrentTheme(theme);

			return IBrowserNotificationConstants.JSON_OK;
		}
		return IBrowserNotificationConstants.JSON_ERROR;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.configurations.processor.IConfigurationProcessorListener#configurationStateChanged(com.aptana.
	 * configurations.processor.ConfigurationStatus, java.util.Set)
	 */
	public void configurationStateChanged(ConfigurationStatus status, Set<String> attributesChanged)
	{
		// Nothing to do here...
	}

	/**
	 * Extracts a theme name from the given attributes, and returns a {@link Theme} instance that match it.
	 * 
	 * @param attributes
	 * @return A {@link Theme}; <code>null</code> if there is an error, or there is not theme with the given name.
	 */
	private String getThemeName(Object attributes)
	{
		if (attributes instanceof Object[])
		{
			Object[] arr = (Object[]) attributes;
			if (arr.length == 1 && arr[0] != null)
			{
				return (String) arr[0];
			}
			String message = MessageFormat
					.format("Wrong argument count passed to ThemeActionController::setActiveTheme. Expected 1 and got {0}", arr.length); //$NON-NLS-1$
			IdeLog.logError(PortalUIPlugin.getDefault(), new Exception(message));
		}
		else
		{
			String message = MessageFormat
					.format("Wrong argument type passed to ThemeActionController::setActiveTheme. Expected Object[] and got {0}", //$NON-NLS-1$
							((attributes == null) ? "null" : attributes.getClass().getName())); //$NON-NLS-1$
			IdeLog.logError(PortalUIPlugin.getDefault(), new Exception(message));
		}
		return null;
	}
}
