/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.browser.parts;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import com.aptana.browser.WebBrowserViewer;
import com.aptana.core.util.StringUtil;

/**
 * @author Max Stepanov
 */
public class WebBrowserView extends ViewPart
{

	public static final String VIEW_ID = "com.aptana.browser.views.webbrowser"; //$NON-NLS-1$

	private WebBrowserViewer browserViewer;
	private int progressWorked;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent)
	{
		browserViewer = new WebBrowserViewer(parent, WebBrowserViewer.NAVIGATION_BAR);
		Browser browser = (Browser) browserViewer.getBrowser();
		browser.addProgressListener(new ProgressListener()
		{
			public void changed(ProgressEvent event)
			{
				if (event.total == 0)
				{
					return;
				}
				if (event.current == 0)
				{
					IProgressMonitor progressMonitor = getStatusBarProgressMonitor();
					progressMonitor.done();
					progressMonitor.beginTask(StringUtil.EMPTY, event.total);
					progressWorked = 0;
				}
				if (progressWorked < event.current)
				{
					getStatusBarProgressMonitor().worked(event.current - progressWorked);
					progressWorked = event.current;
				}
			}

			public void completed(ProgressEvent event)
			{
				getStatusBarProgressMonitor().done();
			}
		});
		browser.addTitleListener(new TitleListener()
		{
			public void changed(TitleEvent event)
			{
				setTitleToolTip(event.title);
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus()
	{
		browserViewer.setFocus();
	}

	private IProgressMonitor getStatusBarProgressMonitor()
	{
		IStatusLineManager statusLineManager = getViewSite().getActionBars().getStatusLineManager();
		return statusLineManager.getProgressMonitor();
	}

	public boolean close()
	{
		try
		{
			getSite().getPage().hideView(this);
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}

	public void setURL(String url)
	{
		browserViewer.setURL(url);
	}

}
