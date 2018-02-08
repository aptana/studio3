/**
 * Appcelerator Studio
 * Copyright (c) 2016 by Appcelerator, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */
package com.aptana.core.resources;

/**
 * @author Kondal Kolipaka
 */
public interface ISocketMessageHandlerListener
{
	/**
	 * Upon request cancelled, update the listeners which are registered.
	 */
	public void notifyRequestCancelled();
}