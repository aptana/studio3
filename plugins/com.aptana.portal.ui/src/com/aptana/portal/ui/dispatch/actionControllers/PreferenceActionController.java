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
package com.aptana.portal.ui.dispatch.actionControllers;

import java.util.Map;
import java.util.Set;

import org.eclipse.jface.preference.IPreferenceStore;

import com.aptana.configurations.processor.ConfigurationStatus;
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
			PortalUIPlugin.logError(t);
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
			PortalUIPlugin.logError(t);
			return IBrowserNotificationConstants.JSON_ERROR;
		}
		return IBrowserNotificationConstants.JSON_OK;
	}

	private Object checkArguments(Object arguments)
	{
		if (!(arguments instanceof Object[]))
		{
			PortalUIPlugin.logError(new Exception(
					"Wrong argument type passed to PreferenceActionController::setPreferenceValue. Expected Object[] and got " //$NON-NLS-1$
							+ ((arguments == null) ? "null" : arguments.getClass().getName()))); //$NON-NLS-1$
			return BrowserNotifier.toJSONErrorNotification(IBrowserNotificationConstants.JSON_ERROR_WRONG_ARGUMENTS,
					null);
		}
		Object[] arr = (Object[]) arguments;
		if (arr.length != 1 || !(arr[0] instanceof Map))
		{
			PortalUIPlugin
					.logError(new Exception(
							"Wrong argument type passed to PreferenceActionController::setPreferenceValue. Expected a Map in the object array")); //$NON-NLS-1$
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
