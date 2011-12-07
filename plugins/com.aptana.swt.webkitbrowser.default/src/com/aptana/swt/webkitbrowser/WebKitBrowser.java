/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.swt.webkitbrowser;

import org.eclipse.swt.SWTException;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.CloseWindowListener;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.OpenWindowListener;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Max Stepanov
 */
public class WebKitBrowser extends Browser
{

	/**
	 * @param parent
	 * @param style
	 */
	public WebKitBrowser(Composite parent, int style)
	{
		super(parent, style);
	}

	@Override
	protected void checkSubclass()
	{
	}

	public void addCloseWindowListener(CloseWindowListener listener)
	{
		super.addCloseWindowListener(listener);
	}

	public void addLocationListener(LocationListener listener)
	{
		super.addLocationListener(listener);
	}

	public void addOpenWindowListener(OpenWindowListener listener)
	{
		super.addOpenWindowListener(listener);
	}

	public void addProgressListener(ProgressListener listener)
	{
		super.addProgressListener(listener);
	}

	public void addStatusTextListener(StatusTextListener listener)
	{
		super.addStatusTextListener(listener);
	}

	public void addTitleListener(TitleListener listener)
	{
		super.addTitleListener(listener);
	}

	public boolean back()
	{
		return super.back();
	}

	public void checkWidget()
	{
		super.checkWidget();
	}

	public boolean execute(String script)
	{
		return super.execute(script);
	}

	public Object evaluate(String script) throws SWTException
	{
		return super.evaluate(script);
	}

	public boolean forward()
	{
		return super.forward();
	}

	public String getText()
	{
		return super.getText();
	}

	public String getUrl()
	{
		return super.getUrl();
	}

	public boolean isBackEnabled()
	{
		return super.isBackEnabled();
	}

	public boolean isForwardEnabled()
	{
		return super.isForwardEnabled();
	}

	public void refresh()
	{
		super.refresh();
	}

	public void removeCloseWindowListener(CloseWindowListener listener)
	{
		super.removeCloseWindowListener(listener);
	}

	public void removeLocationListener(LocationListener listener)
	{
		super.removeLocationListener(listener);
	}

	public void removeOpenWindowListener(OpenWindowListener listener)
	{
		super.removeOpenWindowListener(listener);
	}

	public void removeProgressListener(ProgressListener listener)
	{
		super.removeProgressListener(listener);
	}

	public void removeStatusTextListener(StatusTextListener listener)
	{
		super.removeStatusTextListener(listener);
	}

	public void removeTitleListener(TitleListener listener)
	{
		super.removeTitleListener(listener);
	}

	public boolean setText(String html)
	{
		return super.setText(html);
	}

	public boolean setUrl(String url)
	{
		return super.setUrl(url != null ? url : ""); //$NON-NLS-1$
	}

	public void stop()
	{
		super.stop();
	}
}
