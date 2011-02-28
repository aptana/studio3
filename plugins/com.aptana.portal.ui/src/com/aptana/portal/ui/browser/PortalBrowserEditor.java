/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.portal.ui.browser;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.ProgressAdapter;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;

import com.aptana.portal.ui.PortalUIPlugin;
import com.aptana.portal.ui.dispatch.BrowserNotifier;
import com.aptana.portal.ui.dispatch.IBrowserNotificationConstants;
import com.aptana.portal.ui.dispatch.browserFunctions.DispatcherBrowserFunction;
import com.aptana.portal.ui.internal.BrowserFunctionWrapper;
import com.aptana.portal.ui.internal.BrowserWrapper;
import com.aptana.portal.ui.internal.Portal;
import com.aptana.portal.ui.internal.WebBrowserEditorStub;

/**
 * A portal browser editor. We extends the Eclipse internal WebBrowserEditor. Although not a great act, it solves the
 * protected attributes access limitations which require reflections when we needed to access them.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
@SuppressWarnings("restriction")
public class PortalBrowserEditor extends WebBrowserEditorStub
{
	public static final String WEB_BROWSER_EDITOR_ID = "com.aptana.portal.ui.browser.portal"; //$NON-NLS-1$

	private static final String TITLE_TTP = "Aptana Portal"; //$NON-NLS-1$

	private List<BrowserFunctionWrapper> browserFunctions;

	/**
	 * Set the URL to display in the browser.
	 * 
	 * @param url
	 */
	public void setURL(URL url)
	{
		if (url != null)
		{
			this.webBrowser.setURL(url.toString());
		}
		else
		{
			PortalUIPlugin.logWarning("Ignoring a null URL that was passed to the Aptana Portal"); //$NON-NLS-1$
		}
	}

	/**
	 * Adds a dispose listener on the internal web browser.
	 * 
	 * @param listener
	 */
	public void addDisposeListener(DisposeListener listener)
	{
		this.webBrowser.addDisposeListener(listener);
	}

	@Override
	public void createPartControl(Composite parent)
	{
		super.createPartControl(parent);
		final BrowserWrapper browser = new BrowserWrapper(webBrowser.getBrowser());
		browser.setJavascriptEnabled(true);

		// Usually, we would just listen to a location change. However, since IE does not
		// behave well with notifying us when hitting refresh (F5), we have to do it on
		// a title change (which should work for all browsers)
		browser.addTitleListener(new PortalTitleListener(browser));

		// Register a location listener anyway, just to make sure that the functions are
		// removed when we have a location change.
		// The title-listener will place them back in when the TitleEvent is fired.
		browser.addProgressListener(new ProgressAdapter()
		{
			public void completed(ProgressEvent event)
			{
				browser.addLocationListener(new LocationAdapter()
				{
					public void changed(LocationEvent event)
					{
						// browser.removeLocationListener(this);
						refreshBrowserRegistration(browser);

					}
				});
			}
		});
		// Register this browser to receive notifications from any Browser-Notifier that was
		// added to do so through the browserInteractions extension point.
		BrowserNotifier.getInstance().registerBrowser(getEditorId(), browser);
	}

	@Override
	public String getTitleToolTip()
	{
		return TITLE_TTP;
	}

	/**
	 * Returns the editor-id for this browser editor.
	 * 
	 * @return The editor-id
	 */
	protected String getEditorId()
	{
		return WEB_BROWSER_EDITOR_ID;
	}

	/**
	 * Returns the base URL prefix that will be used to verify the location of the page and register the dispatcher in
	 * case the page is under this path.
	 */
	protected String getBaseURLPrefix()
	{
		return Portal.BASE_URL_PREFIX;
	}

	/**
	 * Register the browser functions into the given browser.
	 * 
	 * @param browser
	 */
	protected synchronized void registerBrowserFunctions(final BrowserWrapper browser)
	{
		browserFunctions = new ArrayList<BrowserFunctionWrapper>();
		// For now, we register a single browser function that dispatch all the
		// JavaScript requests through the browser-action-controller extensions.
		BrowserFunctionWrapper dispatcherFunction = browser.createBrowserFunction(
				IBrowserNotificationConstants.DISPATCH_FUNCTION_NAME, new DispatcherBrowserFunction());
		browserFunctions.add(dispatcherFunction);

		boolean executionResult = browser
				.execute("console = {}; " //$NON-NLS-1$
						+ "console.log   = function(msg) {dispatch($H({controller:\"console\", action:\"log\", args:msg.toJSON()}).toJSON()); return false;};" //$NON-NLS-1$
						+ "console.debug = function(msg) {dispatch($H({controller:\"console\", action:\"log\", args:msg.toJSON()}).toJSON()); return false;};"); //$NON-NLS-1$
		/*
		 * This custom error handler is needed when the Portal is viewed in the Studio internal browser. We also make a
		 * call to window.onerror=customErrorHandler to hook the window.onerror event to this handler. We return false,
		 * so the error will also propagate to other error handlers, in case registered.
		 */
		executionResult = browser.execute("function customErrorHandler(desc,page,line) { " + //$NON-NLS-1$
				"dispatch($H({controller:\"console\", action:\"error\", args:[desc,page,line].toJSON()}).toJSON());" + //$NON-NLS-1$
				"return false;};"); //$NON-NLS-1$
		// Make sure that all the Javascript errors are being surfaced out of the internal browser.
		executionResult = browser.execute("window.onerror=customErrorHandler;"); //$NON-NLS-1$
		if (!executionResult)
		{
			PortalUIPlugin.logError("Error registering the Portal browser functions", new IllegalStateException()); //$NON-NLS-1$
		}
	}

	/**
	 * Refresh the browser functions by removing them from the given browser and re-installing them if the browser URL
	 * is legal.
	 * 
	 * @param browser
	 */
	protected synchronized void refreshBrowserRegistration(BrowserWrapper browser)
	{
		unregisterBrowserFunctions();
		String url = browser.getUrl();
		if (url != null && (url.startsWith(getBaseURLPrefix()) || url.startsWith("file:"))) //$NON-NLS-1$
		{
			registerBrowserFunctions(browser);
		}
	}

	/**
	 * Un-register the browser functions.
	 */
	protected synchronized void unregisterBrowserFunctions()
	{
		if (browserFunctions != null)
		{
			for (BrowserFunctionWrapper bf : browserFunctions)
			{
				bf.dispose();
			}
			browserFunctions = null;
		}
	}

	private class PortalTitleListener implements TitleListener
	{
		private final BrowserWrapper browser;

		public PortalTitleListener(BrowserWrapper browser)
		{
			this.browser = browser;
		}

		public void changed(TitleEvent event)
		{
			// Dispose all BrowserFunctions when the location of the browser is no longer under
			// Aptana.com or the local machine.
			refreshBrowserRegistration(browser);
		}
	}
}