/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.portal.ui.dispatch;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import com.aptana.portal.ui.PortalUIPlugin;
import com.aptana.portal.ui.dispatch.browserNotifications.AbstractBrowserNotification;
import com.aptana.portal.ui.dispatch.browserNotifications.BrowserNotificationProxy;

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
			if (isControllerElement)
			{
				IActionController actionController = new ActionControllerProxy(element);
				controllers.put(id, actionController);
			}
			else
			{
				AbstractBrowserNotification notification = new BrowserNotificationProxy(element);
				notifiers.put(id, notification);
			}
		}
	}
}
