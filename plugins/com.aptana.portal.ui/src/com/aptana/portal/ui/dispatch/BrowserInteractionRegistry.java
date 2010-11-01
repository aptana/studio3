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
package com.aptana.portal.ui.dispatch;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import com.aptana.portal.ui.PortalUIPlugin;
import com.aptana.portal.ui.dispatch.browserNotifications.AbstractBrowserNotification;

/**
 * A registry class for the browser-interaction extensions that were registered through the 'browserInteractions'
 * extension point.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class BrowserInteractionRegistry
{
	private static final String EXTENSION_POINT_ID = PortalUIPlugin.PLUGIN_ID + ".browserInteractions"; //$NON-NLS-1$
	private static final String TAG_CONTROLLER = "actionController"; //$NON-NLS-1$
	private static final String TAG_NOTIFICATION = "browserNotification"; //$NON-NLS-1$
	private static final String ATT_ID = "id"; //$NON-NLS-1$
	private static final String ATT_CLASS = "class"; //$NON-NLS-1$
	private static final String ATT_NOTIFICATION_TARGET = "notificationTarget"; //$NON-NLS-1$
	private static final String ATT_CONFGURATION_PROCESSOR_ID = "configurationProcessor"; //$NON-NLS-1$

	private static BrowserInteractionRegistry instance = null;
	private Map<String, IActionController> controllers = new HashMap<String, IActionController>();
	private Map<String, AbstractBrowserNotification> notifiers = new HashMap<String, AbstractBrowserNotification>();

	// Initialize the registry
	private BrowserInteractionRegistry()
	{
		readExtensionRegistry();
	}

	/**
	 * Returns an instance of this registry.
	 * 
	 * @return a BrowserInteractionRegistry instance
	 */
	public static BrowserInteractionRegistry getInstance()
	{
		if (instance == null)
		{
			instance = new BrowserInteractionRegistry();
		}
		return instance;
	}

	/**
	 * Returns all the registered browser action controllers.
	 * 
	 * @return An array of all loaded controllers.
	 */
	public IActionController[] getActionControllers()
	{
		return controllers.values().toArray(new IActionController[controllers.size()]);
	}

	/**
	 * Returns the IActionController with the given id.
	 * 
	 * @param id
	 *            The id of the IActionController, as was registered in the extension.
	 * @return An instance of IActionController, or null.
	 */
	public IActionController getActionController(String id)
	{
		return controllers.get(id);
	}

	/**
	 * Returns true if and only if there is an action controller with the same given id.
	 * 
	 * @param id
	 *            The id of the action controller
	 * @return true if there is a controller with that id; false otherwise.
	 */
	public boolean hasActionController(String id)
	{
		return getActionController(id) != null;
	}

	/**
	 * Returns an array of all the browser action controllers ids that were registered.
	 * 
	 * @return An array of ID's.
	 */
	public String[] getActionControllersIDs()
	{
		return controllers.keySet().toArray(new String[controllers.size()]);
	}

	/**
	 * Returns an array of installed browser notifications.
	 */
	public AbstractBrowserNotification[] getBrowserNotifications()
	{
		return notifiers.values().toArray(new AbstractBrowserNotification[notifiers.size()]);
	}

	/**
	 * Returns an array of all the registered browser notifications ids.
	 * 
	 * @return An array of ID's.
	 */
	public String[] getBrowserNotificationsIDs()
	{
		return notifiers.keySet().toArray(new String[notifiers.size()]);
	}

	/**
	 * Returns the AbstractBrowserNotification with the given id.
	 * 
	 * @param id
	 *            The id of the AbstractBrowserNotification, as was registered in the extension.
	 * @return An instance of an AbstractBrowserNotification implementation, or null.
	 */
	public AbstractBrowserNotification getBrowserNotification(String id)
	{
		return notifiers.get(id);
	}

	// Load the extensions
	private void readExtensionRegistry()
	{
		IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(
				EXTENSION_POINT_ID);
		for (int i = 0; i < elements.length; ++i)
		{
			readElement(elements[i]);
		}
	}

	private void readElement(IConfigurationElement element)
	{
		boolean isControllerElement = TAG_CONTROLLER.equals(element.getName());
		boolean isNotificationElement = TAG_NOTIFICATION.equals(element.getName());
		if (isControllerElement || isNotificationElement)
		{
			String id = element.getAttribute(ATT_ID);
			if (id == null || id.length() == 0)
			{
				return;
			}
			String clazz = element.getAttribute(ATT_CLASS);
			if (clazz == null || clazz.length() == 0)
			{
				return;
			}
			try
			{
				if (isControllerElement)
				{
					IActionController actionController = (IActionController) element.createExecutableExtension(ATT_CLASS);
					actionController.setConfigurationProcessorId(element.getAttribute(ATT_CONFGURATION_PROCESSOR_ID));
					controllers.put(id, actionController);
				}
				else
				{
					AbstractBrowserNotification notification = (AbstractBrowserNotification) element
							.createExecutableExtension(ATT_CLASS);
					notifiers.put(id, notification);
					notification.setNotificationTargets(element.getAttribute(ATT_NOTIFICATION_TARGET));
				}
			}
			catch (CoreException e)
			{
				PortalUIPlugin.logError("Failed creating a browser action contoller extension", e); //$NON-NLS-1$
			}
		}
	}
}
