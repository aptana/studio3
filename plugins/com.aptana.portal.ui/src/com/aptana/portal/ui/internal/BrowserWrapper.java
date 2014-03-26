/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.portal.ui.internal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.events.DisposeListener;

/**
 * @author Max Stepanov
 */
public final class BrowserWrapper
{

	private final Object browser;

	/**
	 * 
	 */
	public BrowserWrapper(Object browser)
	{
		this.browser = browser;
	}

	public BrowserFunctionWrapper createBrowserFunction(String name, final IBrowserFunctionHandler handler)
	{
		return new BrowserFunctionWrapper(new org.eclipse.swt.browser.BrowserFunction((Browser) browser, name)
		{
			@Override
			public Object function(Object[] arguments)
			{
				return handler.function(arguments);
			}

		});
	}

	private Object callMethod(String name, Class<?>[] parameterTypes, Object[] arguments)
	{
		try
		{
			Method method = browser.getClass().getMethod(name, parameterTypes);
			return method.invoke(browser, arguments);
		}
		catch (SecurityException e)
		{
			throw new RuntimeException(e);
		}
		catch (NoSuchMethodException e)
		{
			throw new RuntimeException(e);
		}
		catch (IllegalArgumentException e)
		{
			throw new RuntimeException(e);
		}
		catch (IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
		catch (InvocationTargetException e)
		{
			throw new RuntimeException(e);
		}
	}

	private Object callMethod(String name, Class<?> parameterType, Object argument)
	{
		return callMethod(name, new Class[] { parameterType }, new Object[] { argument });
	}

	private Object callMethod(String name)
	{
		return callMethod(name, new Class[0], new Object[0]);
	}

	/**
	 * @param listener
	 * @see org.eclipse.swt.browser.Browser#addTitleListener(org.eclipse.swt.browser.TitleListener)
	 */
	public void addTitleListener(TitleListener listener)
	{
		callMethod("addTitleListener", TitleListener.class, listener); //$NON-NLS-1$
	}

	/**
	 * @param listener
	 * @see org.eclipse.swt.browser.Browser#addProgressListener(org.eclipse.swt.browser.ProgressListener)
	 */
	public void addProgressListener(ProgressListener listener)
	{
		callMethod("addProgressListener", ProgressListener.class, listener); //$NON-NLS-1$
	}

	/**
	 * @param listener
	 * @see org.eclipse.swt.browser.Browser#addLocationListener(org.eclipse.swt.browser.LocationListener)
	 */
	public void addLocationListener(LocationListener listener)
	{
		callMethod("addLocationListener", LocationListener.class, listener); //$NON-NLS-1$
	}

	/**
	 * @param listener
	 * @see org.eclipse.swt.widgets.Widget#addDisposeListener(org.eclipse.swt.events.DisposeListener)
	 */
	public void addDisposeListener(DisposeListener listener)
	{
		callMethod("addDisposeListener", DisposeListener.class, listener); //$NON-NLS-1$
	}

	/**
	 * @return
	 * @see org.eclipse.swt.widgets.Widget#isDisposed()
	 */
	public boolean isDisposed()
	{
		return (Boolean) callMethod("isDisposed"); //$NON-NLS-1$
	}

	/**
	 * @param script
	 * @return
	 * @see org.eclipse.swt.browser.Browser#execute(java.lang.String)
	 */
	public boolean execute(String script)
	{
		return (Boolean) callMethod("execute", String.class, script); //$NON-NLS-1$
	}

	/**
	 * @param enabled
	 * @see org.eclipse.swt.browser.Browser#setJavascriptEnabled(boolean)
	 */
	public void setJavascriptEnabled(boolean enabled)
	{
		callMethod("setJavascriptEnabled", boolean.class, enabled); //$NON-NLS-1$
	}

	/**
	 * @return
	 * @see org.eclipse.swt.browser.Browser#getUrl()
	 */
	public String getUrl()
	{
		return (String) callMethod("getUrl"); //$NON-NLS-1$
	}

	/**
	 * @param url
	 * @return
	 * @see org.eclipse.swt.browser.Browser#setUrl(java.lang.String)
	 */
	public boolean setUrl(String url)
	{
		return (Boolean) callMethod("setUrl", String.class, url); //$NON-NLS-1$
	}

	/**
	 * @return
	 * @see org.eclipse.swt.browser.Browser#setFocus()
	 */
	public boolean setFocus()
	{
		return (Boolean) callMethod("setFocus"); //$NON-NLS-1$
	}

}
