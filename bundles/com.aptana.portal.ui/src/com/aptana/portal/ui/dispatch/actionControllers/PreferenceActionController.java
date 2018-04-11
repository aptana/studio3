/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.portal.ui.dispatch.actionControllers;

import java.util.Map;
import java.util.Set;

import org.eclipse.jface.preference.IPreferenceStore;

import com.aptana.configurations.processor.ConfigurationStatus;
import com.aptana.core.logging.IdeLog;
import com.aptana.portal.ui.PortalUIPlugin;
import com.aptana.portal.ui.dispatch.BrowserNotifier;
import com.aptana.portal.ui.dispatch.IBrowserNotificationConstants;

/**
 * An action controller for setting and retrieving preferences values.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class PreferenceActionController extends AbstractActionController
{

	private static final String VALUE = "value"; //$NON-NLS-1$
	private static final String KEY = "key"; //$NON-NLS-1$

	// ############## Actions ###############
	@SuppressWarnings("unchecked")
	/**
	 * Returns a preference value for the key given with the attributes.
	 */
	@ControllerAction
	public Object getPreferenceValue(Object arguments)
	{
		Object check = checkArguments(arguments);
		if (check != null)
		{
			return check;
		}
		IPreferenceStore preferenceStore = PortalUIPlugin.getDefault().getPreferenceStore();
		try
		{
			Map<String, String> map = (Map<String, String>) ((Object[]) arguments)[0];
			return preferenceStore.getString(map.get(KEY));
		}
		catch (Throwable t)
		{
			IdeLog.logError(PortalUIPlugin.getDefault(), t);
			return IBrowserNotificationConstants.JSON_ERROR;
		}
	}

	/**
	 * Set a preference value.<br>
	 * We expect an array that contains a Map which hold the preferences key and it's value.
	 * 
	 * @param arguments
	 *            An array that holds a Map
	 * @return A JSON status for the action.
	 */
	@SuppressWarnings("unchecked")
	@ControllerAction
	public Object setPreferenceValue(Object arguments)
	{
		Object check = checkArguments(arguments);
		if (check != null)
		{
			return check;
		}
		IPreferenceStore preferenceStore = PortalUIPlugin.getDefault().getPreferenceStore();
		try
		{
			Map<String, String> map = (Map<String, String>) ((Object[]) arguments)[0];
			preferenceStore.setValue(map.get(KEY), Boolean.parseBoolean(map.get(VALUE)));
		}
		catch (Throwable t)
		{
			IdeLog.logError(PortalUIPlugin.getDefault(), t);
			return IBrowserNotificationConstants.JSON_ERROR;
		}
		return IBrowserNotificationConstants.JSON_OK;
	}

	private Object checkArguments(Object arguments)
	{
		if (!(arguments instanceof Object[]))
		{
			String message = "Wrong argument type passed to PreferenceActionController::setPreferenceValue. Expected Object[] and got " //$NON-NLS-1$
					+ ((arguments == null) ? "null" : arguments.getClass().getName()); //$NON-NLS-1$
			IdeLog.logError(PortalUIPlugin.getDefault(), new Exception(message));
			return BrowserNotifier.toJSONErrorNotification(IBrowserNotificationConstants.JSON_ERROR_WRONG_ARGUMENTS,
					null);
		}
		Object[] arr = (Object[]) arguments;
		if (arr.length != 1 || !(arr[0] instanceof Map))
		{
			String message = "Wrong argument type passed to PreferenceActionController::setPreferenceValue. Expected a Map in the object array"; //$NON-NLS-1$
			IdeLog.logError(PortalUIPlugin.getDefault(), new Exception(message));
			return BrowserNotifier.toJSONErrorNotification(IBrowserNotificationConstants.JSON_ERROR_WRONG_ARGUMENTS,
					null);
		}
		return null;
	}

	public void configurationStateChanged(ConfigurationStatus status, Set<String> attributesChanged)
	{
		// Do nothing
	}

}
