/**
 * Appcelerator Studio
 * Copyright (c) 2016 by Appcelerator, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */
package com.aptana.core.resources;

/**
 * @author Kondal Kolipaka
 */
public class RequestCancelledException extends Exception
{
	private static final long serialVersionUID = 1998245433598939429L;

	public RequestCancelledException()
	{
	}

	public RequestCancelledException(String message)
	{
		super(message);
	}

	public RequestCancelledException(Exception e)
	{
		super(e);
	}

	public RequestCancelledException(String message, Exception e)
	{
		super(message, e);
	}
}