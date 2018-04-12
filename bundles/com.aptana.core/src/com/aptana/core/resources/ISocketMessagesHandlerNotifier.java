/**
 * Appcelerator Studio
 * Copyright (c) 2016 by Appcelerator, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */
package com.aptana.core.resources;

/**
 * @author Kondal Kolipaka
 */
public interface ISocketMessagesHandlerNotifier
{
	/**
	 * @param listener
	 */
	public void addListener(ISocketMessageHandlerListener listener);

	/**
	 * @param listener
	 */
	public void removeListener(ISocketMessageHandlerListener listener);

	/**
	 * Update the registered listeners.
	 */
	public void update();
}