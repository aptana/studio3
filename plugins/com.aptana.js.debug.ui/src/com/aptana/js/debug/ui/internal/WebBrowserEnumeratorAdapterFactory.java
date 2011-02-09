/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.ui.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.internal.browser.BrowserManager;
import org.eclipse.ui.internal.browser.IBrowserDescriptor;

/**
 * @author Max Stepanov
 */
@SuppressWarnings({ "rawtypes", "restriction" })
public class WebBrowserEnumeratorAdapterFactory implements IAdapterFactory {
	
	/*
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
	 */
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adapterType == Enumeration.class) {
			return Collections.enumeration(getWebBrowsers());
		}
		return null;
	}

	/*
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
	 */
	public Class[] getAdapterList() {
		return new Class[] { Enumeration.class };
	}

	private List<String> getWebBrowsers() {
		List<String> browsers = new ArrayList<String>();
		List list = BrowserManager.getInstance().getWebBrowsers();
		IBrowserDescriptor current = BrowserManager.getInstance().getCurrentWebBrowser();
		for (Iterator i = list.iterator(); i.hasNext();) {
			IBrowserDescriptor desc = (IBrowserDescriptor) i.next();
			String location = desc.getLocation();
			if (location == null) {
				continue;
			}
			if (desc == current) {
				browsers.add(0, location);
			} else {
				browsers.add(location);
			}
		}
		return browsers;
	}
}
