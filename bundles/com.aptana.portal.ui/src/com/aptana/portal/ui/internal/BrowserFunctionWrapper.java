/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.portal.ui.internal;

/**
 * @author Max Stepanov
 */
public final class BrowserFunctionWrapper
{

	private Object browserFunction;

	/**
	 * 
	 */
	public BrowserFunctionWrapper(Object browserFunction)
	{
		this.browserFunction = browserFunction;
	}

	/**
	 * @see org.eclipse.swt.browser.BrowserFunction#dispose()
	 */
	public void dispose()
	{
		((org.eclipse.swt.browser.BrowserFunction) browserFunction).dispose();
	}

}
