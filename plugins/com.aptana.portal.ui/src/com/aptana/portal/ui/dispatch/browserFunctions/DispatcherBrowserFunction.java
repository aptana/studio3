/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.portal.ui.dispatch.browserFunctions;

import java.text.MessageFormat;
import java.util.Map;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;
import com.aptana.jetty.util.epl.ajax.JSON;
import com.aptana.portal.ui.PortalUIPlugin;
import com.aptana.portal.ui.dispatch.BrowserInteractionRegistry;
import com.aptana.portal.ui.dispatch.BrowserNotifier;
import com.aptana.portal.ui.dispatch.IActionController;
import com.aptana.portal.ui.dispatch.IBrowserNotificationConstants;
import com.aptana.portal.ui.internal.IBrowserFunctionHandler;
import com.aptana.usage.FeatureEvent;
import com.aptana.usage.IStudioAnalytics;
import com.aptana.usage.UsagePlugin;

/**
 * This class is the main functions dispatcher for all the registered IActionControllers.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class DispatcherBrowserFunction implements IBrowserFunctionHandler
{

	/**
	 * This function should always get a single argument of a JSON request, which can be transformed into a {@link Map}
	 * by calling {@link JSON#parse(String)}.<br>
	 * The function will then analyze the request and dispatch the appropriate {@link IActionController}.<br>
	 * In case the action controller does not exist, or the requested action from this controller does not exist, the
	 * function returns an error status wrapped in a JSON string. <br>
	 * The target {@link IActionController} and its action decide whether to dispatch in a synchronous or asynchronous
	 * way.<br>
	 * The browser should call this function with a JSON string that is formed this way (arguments can be null):
	 * 
	 * <pre>
	 *   {controller:"controller_name", action:"action_name", arguments:{arguments as JSON}}
	 * Examples:
	 *   dispatch($H({controller:"portal.recentFiles", action:"getRecentFiles"}).toJSON());
	 *   dispatch($H({controller:"portal.recentFiles", action:"openRecentFiles", args:fileA.toJSON()}).toJSON());
	 *   dispatch($H({controller:"portal.recentFiles", action:"openRecentFiles", args:[fileA, fileB].toJSON()}).toJSON());
	 * </pre>
	 * 
	 * @see IBrowserNotificationConstants#JSON_ERROR
	 * @see IBrowserNotificationConstants#JSON_ERROR_WRONG_ARGUMENTS
	 * @see IBrowserNotificationConstants#JSON_ERROR_UNKNOWN_CONTROLLER
	 * @see IBrowserNotificationConstants#JSON_ERROR_UNKNOWN_ACTION
	 */
	@SuppressWarnings({ "rawtypes" })
	public Object function(Object[] arguments)
	{
		if (arguments == null || arguments.length != 1 || arguments[0] == null)
		{
			return BrowserNotifier.toJSONErrorNotification(IBrowserNotificationConstants.JSON_ERROR_WRONG_ARGUMENTS,
					Messages.DispatcherBrowserFunction_wrongOrMissingArguments);
		}
		Object jsonObject = JSON.parse(arguments[0].toString());
		if (!(jsonObject instanceof Map))
		{
			return BrowserNotifier.toJSONErrorNotification(IBrowserNotificationConstants.JSON_ERROR_WRONG_ARGUMENTS,
					Messages.DispatcherBrowserFunction_expectedJSONMap);
		}
		Map event = (Map) jsonObject;
		String controllerID = (String) event.get(IBrowserNotificationConstants.DISPATCH_CONTROLLER);
		IActionController controller = BrowserInteractionRegistry.getInstance().getActionController(controllerID);
		if (controller == null)
		{
			return BrowserNotifier.toJSONErrorNotification(IBrowserNotificationConstants.JSON_ERROR_UNKNOWN_CONTROLLER,
					Messages.DispatcherBrowserFunction_unknownController + controllerID);
		}
		String action = (String) event.get(IBrowserNotificationConstants.DISPATCH_ACTION);
		if (action == null || !controller.hasAction(action))
		{
			return BrowserNotifier.toJSONErrorNotification(IBrowserNotificationConstants.JSON_ERROR_UNKNOWN_ACTION,
					Messages.DispatcherBrowserFunction_unknownAction + action);
		}
		Object args = event.get(IBrowserNotificationConstants.DISPATCH_ARGUMENTS);
		try
		{
			if (args != null)
			{
				if (!isSupportedPlatform(args))
				{
					return StringUtil.EMPTY;
				}
				if (!(args instanceof Map))
				{
					// parse it only if it's a plain String
					args = JSON.parse(args.toString());
				}
				if (!(args instanceof Object[]))
				{
					// Make sure we pass the argument in an Object array anyway.
					// This allows the JavaScript side to pass the arguments as an array,
					// or as a simple value.
					args = new Object[] { args };
				}
				// Check if all arguments are null. In case they are, nullify the args (see TISTUD-2594).
				if (isAllNulls((Object[]) args))
				{
					// Check if all arguments are null. In case they are, nullify the args (see TISTUD-2594).
					args = null;
				}
			}
		}
		catch (IllegalStateException ise)
		{
			IdeLog.logError(PortalUIPlugin.getDefault(),
					"The dispatch arguments were probably not passed as a valid JSON." //$NON-NLS-1$
							+ " Please check your JavaScript code.", ise); //$NON-NLS-1$
			return BrowserNotifier.toJSONErrorNotification(IBrowserNotificationConstants.JSON_ERROR_WRONG_ARGUMENTS,
					ise.getMessage());
		}
		catch (Exception e)
		{
			IdeLog.logError(PortalUIPlugin.getDefault(), e);
			return BrowserNotifier.toJSONErrorNotification(IBrowserNotificationConstants.JSON_ERROR_WRONG_ARGUMENTS,
					e.getMessage());
		}
		// Send an Analytics ping
		sendEvent(new FeatureEvent(MessageFormat.format("{0}.{1}", controllerID, action), null)); //$NON-NLS-1$
		// OK... Done with the checks. Now dispatch.
		return dispatch(controller, action, args);
	}

	protected boolean isSupportedPlatform(Object browserArgs)
	{
		if (browserArgs instanceof String) 
		{
			String mobileweb = "MobileWeb"; //$NON-NLS-1$
			String format1 = MessageFormat.format("\"{0}\"", mobileweb);
			String format2 = MessageFormat.format("[\"{0}\"]", mobileweb);
			if (browserArgs.equals(format1) || browserArgs.equals(format2))
			{
				return false; //not supported from Studio 5.0. No version check required as this code goes only into studio 5.0
			}
		}
		return true;
	}

	private void sendEvent(FeatureEvent featureEvent)
	{
		UsagePlugin plugin = UsagePlugin.getDefault();
		if (plugin == null)
		{
			return;
		}
		IStudioAnalytics analytics = plugin.getStudioAnalytics();
		if (analytics == null)
		{
			return;
		}
		analytics.sendEvent(featureEvent);
	}

	/**
	 * Dispatch the action controller function in a <b>synchronous</b> way.<br>
	 * The action that is being dispatched can still create a Job that will run asynchronously and report back when
	 * ready. It's the responsible of the controller & action implementation to return a value immediately (such as
	 * {@link IBrowserNotificationConstants#JSON_OK}) and run a Job, or to return after a synchronous computation.
	 * 
	 * @param controller
	 * @param action
	 * @param arguments
	 * @return The result of this dispatch.
	 */
	protected Object dispatch(IActionController controller, String action, Object arguments)
	{
		return controller.invokeAction(action, arguments);
	}

	private static boolean isAllNulls(Object[] array)
	{
		if (array == null)
		{
			return true;
		}
		for (Object obj : array)
		{
			if (obj != null && !"null".equals(obj.toString())) //$NON-NLS-1$
			{
				return false;
			}
		}
		return true;
	}
}
