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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.ProgressAdapter;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.internal.browser.WebBrowserEditorInput;
import org.eclipse.ui.part.EditorPart;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;
import com.aptana.portal.ui.PortalUIPlugin;
import com.aptana.portal.ui.dispatch.BrowserNotifier;
import com.aptana.portal.ui.dispatch.IBrowserNotificationConstants;
import com.aptana.portal.ui.dispatch.browserFunctions.DispatcherBrowserFunction;
import com.aptana.portal.ui.internal.BrowserFunctionWrapper;
import com.aptana.portal.ui.internal.BrowserViewerWrapper;
import com.aptana.portal.ui.internal.BrowserWrapper;
import com.aptana.portal.ui.internal.startpage.IStartPageUISystemProperties;

/**
 * A portal browser editor.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 * @author Max Stepanov
 */
@SuppressWarnings("restriction")
public abstract class AbstractPortalBrowserEditor extends EditorPart
{

	private static final String BROWSER_SWT = "swt"; //$NON-NLS-1$
	private static final String BROWSER_CHROMIUM = "chromium"; //$NON-NLS-1$

	private BrowserViewerWrapper browserViewer;
	private BrowserWrapper browser;
	private List<BrowserFunctionWrapper> browserFunctions;
	private String initialURL;
	private Image image;
	private boolean disposed;

	/**
	 * Set the URL to display in the browser.
	 * 
	 * @param url
	 */
	public void setURL(URL url)
	{
		if (url != null)
		{
			browser.setUrl(url.toString());
		}
		else
		{
			IdeLog.logWarning(PortalUIPlugin.getDefault(), "Ignoring a null URL that was passed to the Aptana Portal"); //$NON-NLS-1$
		}
	}

	/**
	 * Adds a dispose listener on the internal web browser.
	 * 
	 * @param listener
	 */
	public void addDisposeListener(DisposeListener listener)
	{
		browser.addDisposeListener(listener);
	}

	@Override
	public void createPartControl(Composite parent)
	{
		browserViewer = createBrowserViewer(parent);
		browser = new BrowserWrapper(browserViewer.getBrowser());
		browser.setJavascriptEnabled(true);

		// Usually, we would just listen to a location change. However, since IE
		// does not
		// behave well with notifying us when hitting refresh (F5), we have to
		// do it on
		// a title change (which should work for all browsers)
		browser.addTitleListener(new PortalTitleListener());

		// Register a location listener anyway, just to make sure that the
		// functions are
		// removed when we have a location change.
		// The title-listener will place them back in when the TitleEvent is
		// fired.
		browser.addProgressListener(new ProgressAdapter()
		{
			public void completed(ProgressEvent event)
			{
				browser.addLocationListener(new LocationAdapter()
				{
					public void changed(LocationEvent event)
					{
						// browser.removeLocationListener(this);
						refreshBrowserRegistration();

					}
				});
			}
		});
		browser.setUrl(initialURL);
		// Register this browser to receive notifications from any
		// Browser-Notifier that was
		// added to do so through the browserInteractions extension point.
		BrowserNotifier.getInstance().registerBrowser(getSite().getId(), browser);
	}

	private static BrowserViewerWrapper createBrowserViewer(Composite parent)
	{
		String browserType = getConfiguredBrowserType();
		if (BROWSER_CHROMIUM.equals(browserType))
		{
			return BrowserViewerWrapper.createWebkitBrowserViewer(parent, 0);
		}
		else
		{
			return BrowserViewerWrapper.createSWTBrowserViewer(parent, 0);
		}
	}

	private static String getConfiguredBrowserType()
	{
		String browserType = EclipseUtil.getSystemProperty(IStartPageUISystemProperties.PORTAL_BROWSER);
		if (BROWSER_CHROMIUM.equals(browserType) && !isChromiumWebkitSupported())
		{
			browserType = BROWSER_SWT;
		}
		else if (browserType == null && isChromiumWebkitSupported())
		{
			browserType = BROWSER_CHROMIUM;
		}
		return browserType;
	}

	private static boolean isChromiumWebkitSupported()
	{
		if (Platform.ARCH_X86.equals(Platform.getOSArch()))
		{
			return /*Platform.OS_WIN32.equals(Platform.getOS())  || Platform.OS_MACOSX.equals(Platform.getOS()) 
					||*/ Platform.OS_LINUX.equals(Platform.getOS());
		}
		else if (Platform.ARCH_X86_64.equals(Platform.getOSArch()))
		{
			return Platform.OS_LINUX.equals(Platform.getOS());
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor)
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	@Override
	public void doSaveAs()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException
	{
		setSite(site);
		setInput(input);
		if (input instanceof WebBrowserEditorInput)
		{
			WebBrowserEditorInput wbei = (WebBrowserEditorInput) input;
			initialURL = null;
			if (wbei.getURL() != null)
				initialURL = wbei.getURL().toExternalForm();
			if (browser != null)
			{
				browser.setUrl(initialURL);
				site.getWorkbenchWindow().getActivePage().activate(this);
			}

			setPartName(wbei.getName());
			setTitleToolTip(wbei.getToolTipText());
			Image oldImage = image;
			ImageDescriptor id = wbei.getImageDescriptor();
			image = id.createImage();

			setTitleImage(image);
			if (oldImage != null && !oldImage.isDisposed())
				oldImage.dispose();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isDirty()
	 */
	@Override
	public boolean isDirty()
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed()
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus()
	{
		browser.setFocus();
	}

	public boolean isDisposed()
	{
		return disposed;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose()
	{
		if (image != null && !image.isDisposed())
		{
			image.dispose();
			image = null;
		}
		super.dispose();
		disposed = true;
	}

	/**
	 * Returns the base URL prefix that will be used to verify the location of the page and register the dispatcher in
	 * case the page is under this path.
	 */
	protected abstract String getBaseURLPrefix();

	/**
	 * Register the browser functions into the given browser.
	 */
	private synchronized void registerBrowserFunctions()
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
				"dispatch($H({controller:\"console\", action:\"error\", args:[desc,page,line].toJSON()}).toJSON());" //$NON-NLS-1$
				+ "return false;};"); //$NON-NLS-1$
		// Make sure that all the Javascript errors are being surfaced out of
		// the internal browser.
		executionResult = browser.execute("window.onerror=customErrorHandler;"); //$NON-NLS-1$
		if (!executionResult)
		{
			IdeLog.logError(PortalUIPlugin.getDefault(),
					"Error registering the Portal browser functions", new IllegalStateException()); //$NON-NLS-1$
		}
	}

	/**
	 * Refresh the browser functions by removing them from the given browser and re-installing them if the browser URL
	 * is legal.
	 */
	private synchronized void refreshBrowserRegistration()
	{
		unregisterBrowserFunctions();
		String url = browser.getUrl();
		if (url != null && (url.startsWith(getBaseURLPrefix()) || url.startsWith("file:"))) { //$NON-NLS-1$
			registerBrowserFunctions();
		}
	}

	/**
	 * Un-register the browser functions.
	 */
	private synchronized void unregisterBrowserFunctions()
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

		public void changed(TitleEvent event)
		{
			// Dispose all BrowserFunctions when the location of the browser is
			// no longer under
			// Aptana.com or the local machine.
			refreshBrowserRegistration();
		}
	}
}