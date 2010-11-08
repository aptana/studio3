/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
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

import com.aptana.swt.webkitbrowser.WebKitBrowser;

/**
 * @author Max Stepanov
 *
 */
public final class BrowserWrapper {

	private Object browser;
	
	/**
	 * 
	 */
	public BrowserWrapper(Object browser) {
		this.browser = browser;
	}
	
	public BrowserFunctionWrapper createBrowserFunction(String name, final IBrowserFunctionHandler handler) {
		if (browser instanceof WebKitBrowser) {
			return new BrowserFunctionWrapper(new com.aptana.swt.webkitbrowser.BrowserFunction((WebKitBrowser)browser, name) {
				@Override
				public Object function(Object[] arguments) {
					return handler.function(arguments);
				}
				
			});
			
		} else {
			return new BrowserFunctionWrapper(new org.eclipse.swt.browser.BrowserFunction((Browser)browser, name) {
				@Override
				public Object function(Object[] arguments) {
					return handler.function(arguments);
				}
				
			});
		}
	}
	
	private Object callMethod(String name, Class<?>[] parameterTypes, Object[] arguments) {
		try {
			Method method = browser.getClass().getMethod(name, parameterTypes);
			return method.invoke(browser, arguments);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	private Object callMethod(String name, Class<?> parameterType, Object argument) {
		return callMethod(name, new Class[] { parameterType }, new Object[] { argument });
	}

	private Object callMethod(String name) {
		return callMethod(name, new Class[0], new Object[0]);
	}

	/**
	 * @param listener
	 * @see org.eclipse.swt.browser.Browser#addTitleListener(org.eclipse.swt.browser.TitleListener)
	 */
	public void addTitleListener(TitleListener listener) {
		callMethod("addTitleListener", TitleListener.class, listener); //$NON-NLS-1$
	}

	/**
	 * @param listener
	 * @see org.eclipse.swt.browser.Browser#addProgressListener(org.eclipse.swt.browser.ProgressListener)
	 */
	public void addProgressListener(ProgressListener listener) {
		callMethod("addProgressListener", ProgressListener.class, listener); //$NON-NLS-1$
	}

	/**
	 * @param listener
	 * @see org.eclipse.swt.browser.Browser#addLocationListener(org.eclipse.swt.browser.LocationListener)
	 */
	public void addLocationListener(LocationListener listener) {
		callMethod("addLocationListener", LocationListener.class, listener); //$NON-NLS-1$
	}

	/**
	 * @param listener
	 * @see org.eclipse.swt.widgets.Widget#addDisposeListener(org.eclipse.swt.events.DisposeListener)
	 */
	public void addDisposeListener(DisposeListener listener) {
		callMethod("addDisposeListener", DisposeListener.class, listener); //$NON-NLS-1$
	}

	/**
	 * @return
	 * @see org.eclipse.swt.widgets.Widget#isDisposed()
	 */
	public boolean isDisposed() {
		return (Boolean) callMethod("isDisposed"); //$NON-NLS-1$
	}

	/**
	 * @param script
	 * @return
	 * @see org.eclipse.swt.browser.Browser#execute(java.lang.String)
	 */
	public boolean execute(String script) {
		return (Boolean)callMethod("execute", String.class, script); //$NON-NLS-1$
	}

	/**
	 * @param enabled
	 * @see org.eclipse.swt.browser.Browser#setJavascriptEnabled(boolean)
	 */
	public void setJavascriptEnabled(boolean enabled) {
		callMethod("setJavascriptEnabled", boolean.class, enabled); //$NON-NLS-1$
	}

	/**
	 * @return
	 * @see org.eclipse.swt.browser.Browser#getUrl()
	 */
	public String getUrl() {
		return (String) callMethod("getUrl"); //$NON-NLS-1$
	}

}
