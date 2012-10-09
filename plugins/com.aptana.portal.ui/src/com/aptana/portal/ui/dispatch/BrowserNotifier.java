/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.portal.ui.dispatch;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.ui.progress.UIJob;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;
import com.aptana.jetty.util.epl.ajax.JSON;
import com.aptana.portal.ui.IDebugScopes;
import com.aptana.portal.ui.PortalUIPlugin;
import com.aptana.portal.ui.dispatch.browserNotifications.AbstractBrowserNotification;
import com.aptana.portal.ui.internal.BrowserWrapper;

/**
 * This class is used to generate JavaScript notifications that are to be executed in a Browser instance. The Portal
 * JavaScript side will handle these notifications and will update its UI accordingly. As long as there are registered
 * browsers, the BrowserNotification classes will be set to listen. Ones the last browser is removed from this notifier,
 * all of the BrowserNotifiers will be instructed to stop their listening.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class BrowserNotifier
{
	// The JavaScript notification string pattern.
	// We replace the {0} with the generated JSON string.
	private static final String NOTIFICATION_PATTERN = "if (typeof(eventsDispatcher) !== 'undefined') eventsDispatcher.notify(''{0}'');"; //$NON-NLS-1$
	private static final String DATA_PATTERN_PREFIX = '\"' + IBrowserNotificationConstants.EVENT_DATA + "\":"; //$NON-NLS-1$
	private static BrowserNotifier instance = null;
	private Map<String, BrowserWrapper> browsers;

	// Private constructor
	private BrowserNotifier()
	{
		browsers = new HashMap<String, BrowserWrapper>();
	}

	/**
	 * Returns an instance of this BrowserNotifier
	 * 
	 * @return A BrowserNotifier instance
	 */
	public static BrowserNotifier getInstance()
	{
		if (instance == null)
		{
			instance = new BrowserNotifier();
		}
		return instance;
	}

	/**
	 * Register a Browser with an ID to be notified when a BrowserNotification is fired and the ID match. Registering a
	 * disposed browser will not do a thing.
	 * 
	 * @param id
	 * @param browser
	 *            A non-disposed browser.
	 */
	public void registerBrowser(final String id, BrowserWrapper browser)
	{
		if (!browser.isDisposed())
		{
			synchronized (browsers)
			{
				browsers.put(id, browser);
				// Make sure that if this browser is disposed, we remove it from our list.
				browser.addDisposeListener(new DisposeListener()
				{
					public void widgetDisposed(DisposeEvent e)
					{
						unregisterBrowser(id);
					}
				});
				if (browsers.size() == 1)
				{
					startNotifiers();
				}
			}

		}
	}

	/**
	 * Unregister a Browser. This Browser will be removed from the list of browsers that wish to be notified when a
	 * BrowserNotification is fired. Note that unregistering the browser is not a must. The browser will be
	 * automatically unregistered when the browser is disposed.
	 * 
	 * @param id
	 */
	public void unregisterBrowser(String id)
	{
		synchronized (browsers)
		{
			browsers.remove(id);
			if (browsers.size() == 0)
			{
				stopNotifiers();
			}
		}
	}

	/**
	 * Create a pure JSON string that represents the given event and its details.
	 * 
	 * @param eventName
	 *            The identifier of this event (its name)
	 * @param eventType
	 *            The type (see {@link IBrowserNotificationConstants})
	 * @param eventData
	 *            The event data as JSON string (can be null)
	 * @return A JSON string in a form of {event:eventName, eventType:eventType, data:{eventData}}
	 */
	public static String toJSONNotification(String eventName, String eventType, String eventData)
	{
		Map<String, String> event = new LinkedHashMap<String, String>();
		event.put(IBrowserNotificationConstants.EVENT, eventName);
		event.put(IBrowserNotificationConstants.EVENT_TYPE, eventType);
		// We need to make sure that the event data, which already arrive as a JSON, is not wrapped in quotes!
		// Otherwise, the JavaScript side will get an error when doing evalJSON on the string.
		// Therefore, we set the data to nothing, and if needed we replace it with the JSON data.
		event.put(IBrowserNotificationConstants.EVENT_DATA, ""); //$NON-NLS-1$
		String json = JSON.toString(event);
		if (eventData != null)
		{
			json = json.replaceAll(DATA_PATTERN_PREFIX + "\"\"", DATA_PATTERN_PREFIX + eventData); //$NON-NLS-1$
		}
		return json;
	}

	/**
	 * Returns a JSON error notification.<br>
	 * The returned JSON string will be in this form:
	 * 
	 * <pre>
	 * {event:error,eventType:errorType,data:{errorDetails:detailedMessage}}
	 * </pre>
	 * 
	 * @param errorType
	 *            As defined at {@link IBrowserNotificationConstants}
	 * @param detailedMessage
	 *            A detailed message from the Studio as a simple string (can be null)
	 * @return A JSON error notification in a form of
	 *         {event:error,eventType:errorType,data:{errorDetails:detailedMessage}}
	 */
	public static String toJSONErrorNotification(String errorType, String detailedMessage)
	{
		String json = null;
		if (detailedMessage == null)
		{
			json = toJSONNotification(IBrowserNotificationConstants.ERROR_STRING, errorType, null);
		}
		else
		{
			// Wrap this error message in as a JSON map
			Map<String, String> error = new HashMap<String, String>();
			error.put(IBrowserNotificationConstants.ERROR_DETAILS, detailedMessage);
			json = toJSONNotification(IBrowserNotificationConstants.ERROR_STRING, errorType, JSON.toString(error));
		}
		return json;
	}

	/**
	 * Create a notification string that can be executed in a Browser.execute command later on.<br>
	 * This notification will be in a form of:
	 * 
	 * <pre>
	 * eventsDispatcher.notify('{json-content}');
	 * </pre>
	 * 
	 * @param eventName
	 *            The identifier of this event (its name)
	 * @param eventType
	 *            The type (see {@link IBrowserNotificationConstants})
	 * @param eventData
	 *            The event data as JSON string (can be null)
	 * @return A notification string to be executed in a Browser instance.
	 */
	public static String createBrowserNotification(String eventName, String eventType, String eventData)
	{
		String json = toJSONNotification(eventName, eventType, eventData);
		return MessageFormat.format(NOTIFICATION_PATTERN, json);
	}

	/**
	 * A convenient way to execute a notification on a given Browser. This function calls the
	 * {@link #createBrowserNotification(String, String, String)} and then do a {@link Browser#execute(String)}.
	 * 
	 * @param browser
	 *            A Browser instance
	 * @param eventName
	 *            The identifier of this event (its name)
	 * @param eventType
	 *            The type (see {@link IBrowserNotificationConstants})
	 * @param eventData
	 *            The event data as JSON string (can be null)
	 * @return true if the operation was successful and false otherwise
	 */
	public boolean notifyBrowser(BrowserWrapper browser, String eventName, String eventType, String eventData)
	{
		String notification = createBrowserNotification(eventName, eventType, eventData);
		return browser.execute(notification);
	}

	/**
	 * Execute a notification on several target browsers. The browsers that are selected are ones that exist in a
	 * BrowserEditor that was registered with this BrowserNotifier and have an ID that matches one of the id's in the
	 * given notificationTargets list. In case the given notificationTargets list is empty, the notification will be
	 * sent to all the registered BrowserEditors.
	 * 
	 * @param notificationTargets
	 *            A list of BrowserEditors ID's that need to be notified with this event.
	 * @param eventName
	 *            The identifier of this event (its name)
	 * @param eventType
	 *            The type (see {@link IBrowserNotificationConstants})
	 * @param eventData
	 *            The event data as JSON string (can be null)
	 * @return true if the operation was successful on <b>all</b> executions; False, otherwise.
	 */
	public boolean notifyBrowser(List<String> notificationTargets, String eventName, String eventType, String eventData)
	{
		String notification = createBrowserNotification(eventName, eventType, eventData);
		IdeLog.logInfo(PortalUIPlugin.getDefault(),
				"Notifying the portal with: " + notification, IDebugScopes.START_PAGE); //$NON-NLS-1$
		boolean notifyAll = notificationTargets.isEmpty();
		boolean result = true;
		for (String id : browsers.keySet())
		{
			if (notifyAll || notificationTargets.contains(id))
			{
				BrowserWrapper b = browsers.get(id);
				if (!b.isDisposed())
				{
					result &= b.execute(notification);
				}
			}
		}
		return result;
	}

	/**
	 * Execute a notification on several target browsers in a UI job. This call is doing exactly what
	 * {@link #notifyBrowser(List, String, String, String)} does, but wrap the call in a UIJob. It's useful when a
	 * notification is needed to be made from a non-UI thread.
	 * 
	 * @param notificationTargets
	 * @param eventName
	 * @param eventType
	 * @param eventData
	 * @see #notifyBrowser(Browser, String, String, String)
	 */
	public void notifyBrowserInUIThread(final List<String> notificationTargets, final String eventName,
			final String eventType, final String eventData)
	{
		UIJob uiJob = new UIJob("Browser Notify") //$NON-NLS-1$
		{
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				notifyBrowser(notificationTargets, eventName, eventType, eventData);
				return Status.OK_STATUS;
			}
		};
		EclipseUtil.setSystemForJob(uiJob);
		uiJob.schedule();
	}

	/**
	 * Start all the registered AbstractBrowserNotifications
	 */
	protected void startNotifiers()
	{
		AbstractBrowserNotification[] notifications = BrowserInteractionRegistry.getInstance()
				.getBrowserNotifications();
		for (AbstractBrowserNotification notification : notifications)
		{
			notification.start();
		}
	}

	/**
	 * Stop all the registered AbstractBrowserNotifications
	 */
	protected void stopNotifiers()
	{
		AbstractBrowserNotification[] notifications = BrowserInteractionRegistry.getInstance()
				.getBrowserNotifications();
		for (AbstractBrowserNotification notification : notifications)
		{
			notification.stop();
		}
	}
}
