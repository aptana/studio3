/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable questionableAssignment

package com.aptana.browser.support;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.internal.browser.DefaultBrowserSupport;
import org.eclipse.ui.internal.browser.InternalBrowserEditorInstance;
import org.eclipse.ui.internal.browser.InternalBrowserInstance;
import org.eclipse.ui.internal.browser.InternalBrowserViewInstance;

/**
 * @author Max Stepanov
 */
@SuppressWarnings("restriction")
public class WorkbenchBrowserSupport extends DefaultBrowserSupport
{

	private static final String DEFAULT_ID_BASE = "com.aptana.browser.defaultBrowser"; //$NON-NLS-1$ // $codepro.audit.disable hidingInheritedFields
	/**
	 * Flag that force to use internal browser without webkit.
	 */
	public static final int AS_INTERNAL = 1 << 0;

	/**
	 * 
	 */
	public WorkbenchBrowserSupport()
	{
		super();
		setDefaultInstance(this);
	}

	private static void setDefaultInstance(DefaultBrowserSupport object)
	{
		instance = object;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.browser.IWorkbenchBrowserSupport#createBrowser(int, java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public IWebBrowser createBrowser(int style, String browserId, String name, String tooltip) throws PartInitException
	{
		if (browserId == null)
		{
			browserId = getDefaultId();
		}
		if (getExistingWebBrowser(browserId) instanceof InternalBrowserInstance)
		{
			return super.createBrowser(style, browserId, name, tooltip);
		}
		if ((style & AS_EXTERNAL) != 0)
		{
			return super.createBrowser(style, browserId, name, tooltip);
		}
		IWebBrowser webBrowser = null;
		if ((style & IWorkbenchBrowserSupport.AS_VIEW) != 0)
		{
			if ((style & AS_INTERNAL) != 0)
			{
				webBrowser = new InternalBrowserViewInstance(browserId, style, name, tooltip);
			}
			else
			{
				webBrowser = new BrowserViewInstance(browserId, style, name, tooltip);
			}
		}
		else
		{
			if ((style & AS_INTERNAL) != 0)
			{
				webBrowser = new InternalBrowserEditorInstance(browserId, style, name, tooltip);
			}
			else
			{
				webBrowser = new BrowserEditorInstance(browserId, style, name, tooltip);
			}
		}
		// we should only share internal browsers within one workbench window. Each workbench window can have a shared
		// browser with the same id
		IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		Integer key = Integer.valueOf(workbenchWindow.hashCode());
		Map wmap = (Map) browserIdMap.get(browserId);
		if (wmap == null)
		{
			wmap = new HashMap();
			browserIdMap.put(browserId, wmap);
		}
		wmap.put(key, webBrowser);
		return webBrowser;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.browser.AbstractWorkbenchBrowserSupport#isInternalWebBrowserAvailable()
	 */
	@Override
	public boolean isInternalWebBrowserAvailable()
	{
		return true;
	}

	private String getDefaultId()
	{ // $codepro.audit.disable overridingPrivateMethod
		String id = null;
		for (int i = 0; i < Integer.MAX_VALUE; i++)
		{
			id = DEFAULT_ID_BASE + i;
			if (browserIdMap.get(id) == null)
				break;
		}
		return id;
	}

}
