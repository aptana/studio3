/**
 * Appcelerator Studio
 * Copyright (c) 2015 by Appcelerator, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */
package com.aptana.core.resources;

/**
 * @author Kondal Kolipaka
 */
public interface ISocketMessagesHandlerNotifier
{
	public void addListener(ISocketMessageHandlerListener listener);

	public void removeListener(ISocketMessageHandlerListener listener);

	public void update();
}
