/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.core;

import java.net.Socket;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.IConfigurationElementProcessor;
import com.aptana.js.debug.core.internal.ProtocolLogger;
import com.aptana.js.debug.core.model.IJSConnection;

/**
 * A registry class for {@link IJSConnection} extensions.
 * 
 * @author sgibly@appcelerator.com
 */
public class JSConnectionInitializersRegistry
{
	private static final String EXTENSION_POINT_ID = "jsConnectionInitializers"; //$NON-NLS-1$
	private static final String ELEMENT_TYPE = "connectionInitializer"; //$NON-NLS-1$
	private static final String CLASS_ATTR = "class"; //$NON-NLS-1$
	private static final String ID_ATTR = "id"; //$NON-NLS-1$
	private static final String PRIORITY_ATTR = "priority"; //$NON-NLS-1$
	private static final String MODE_ATTR = "mode"; //$NON-NLS-1$

	private Map<String, IConfigurationElement> jsConnections;
	private static JSConnectionInitializersRegistry instance;

	private JSConnectionInitializersRegistry()
	{
		load();
	}

	public synchronized static IJSConnection getConnection(String mode, Socket socket, ProtocolLogger logger)
	{
		if (instance == null)
		{
			instance = new JSConnectionInitializersRegistry();
		}
		return instance.createJSConnection(mode, socket, logger);
	}

	private IJSConnection createJSConnection(String mode, final Socket socket, final ProtocolLogger logger)
	{
		final IConfigurationElement element = jsConnections.get(mode);
		if (element == null)
		{
			return null;
		}
		final IJSConnection[] connection = new IJSConnection[1];
		SafeRunner.run(new ISafeRunnable()
		{

			public void run() throws Exception
			{
				connection[0] = (IJSConnection) element.createExecutableExtension(CLASS_ATTR);
				connection[0].initialize(socket, logger);
			}

			public void handleException(Throwable exception)
			{
				IdeLog.logError(JSDebugPlugin.getDefault(),
						MessageFormat.format("Error loading an IJSConnection - id={0}", //$NON-NLS-1$
								element.getAttribute(ID_ATTR)), exception);
			}
		});
		return connection[0];
	}

	private void load()
	{
		if (jsConnections == null)
		{
			jsConnections = new HashMap<String, IConfigurationElement>();

			EclipseUtil.processConfigurationElements(JSDebugPlugin.PLUGIN_ID, EXTENSION_POINT_ID,
					new IConfigurationElementProcessor()
					{

						public void processElement(IConfigurationElement element)
						{
							String mode = element.getAttribute(MODE_ATTR).trim();
							int priority = getPriority(element);
							// check if we have another record with a lower priority. If so, replace it with this one.
							IConfigurationElement existingElement = jsConnections.get(mode);
							if (existingElement != null)
							{
								if (priority > getPriority(existingElement))
								{
									// replace the element
									jsConnections.put(mode, element);
								}
							}
							else
							{
								jsConnections.put(mode, element);
							}
						}

						public Set<String> getSupportElementNames()
						{
							return CollectionsUtil.newSet(ELEMENT_TYPE);
						}
					});
		}
	}

	private int getPriority(IConfigurationElement element)
	{
		String priorityStr = element.getAttribute(PRIORITY_ATTR);
		int priority = 0;
		try
		{
			priority = Integer.parseInt(priorityStr);
		}
		catch (NumberFormatException e)
		{
			// ignore
		}
		return priority;
	}
}
