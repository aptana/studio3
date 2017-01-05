/**
 * Appcelerator Studio
 * Copyright (c) 2015 by Appcelerator, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */
package com.aptana.core.resources;

import java.util.ArrayList;
import java.util.List;

import com.aptana.core.resources.ISocketMessageHandlerListener;
import com.aptana.core.resources.ISocketMessagesHandler;

/**
 * @author Kondal Kolipaka
 */
public abstract class SocketMessagesHandler implements ISocketMessagesHandler
{
	private List<ISocketMessageHandlerListener> listeners;

	public SocketMessagesHandler()
	{
		listeners = new ArrayList<ISocketMessageHandlerListener>();
	}

	public void addListener(ISocketMessageHandlerListener listener)
	{
		listeners.add(listener);
	}

	public void update()
	{
		for (ISocketMessageHandlerListener listener : listeners)
		{
			listener.notifyRequestCancelled();
		}
	}

	public void removeListener(ISocketMessageHandlerListener listener)
	{
		listeners.remove(listener);
	}
}
