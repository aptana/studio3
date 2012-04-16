/*******************************************************************************
 * Copyright (c) 2011, Alex Blewitt.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alex Blewitt - initial API and implementation
 *******************************************************************************/
package com.aptana.eclipse.fullscreen.ui;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.bandlem.eclipse.objc.SO;

/**
 * Set all existing windows to have full-screen behaviour, and also permit new windows to be registered with same as
 * well.
 * 
 * @author Alex Blewitt <alex.blewit@gmail.com>
 */
public class Activator extends AbstractUIPlugin implements IWindowListener
{

	public static final String PLUGIN_ID = "com.aptana.eclipse.fullscreen.ui"; //$NON-NLS-1$

	private static Activator plugin;

	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		plugin = this;
		PlatformUI.getWorkbench().addWindowListener(this);
	}

	public void stop(BundleContext context) throws Exception
	{
		plugin = null;
		super.stop(context);
	}

	public static Activator getDefault()
	{
		return plugin;
	}

	public void windowActivated(IWorkbenchWindow window)
	{
	}

	public void windowDeactivated(IWorkbenchWindow window)
	{
	}

	public void windowClosed(IWorkbenchWindow window)
	{
	}

	public void windowOpened(IWorkbenchWindow window)
	{
		Shell shell = window.getShell();
		setWindowFullscreen(shell);
	}

	static Object getNSWindow(Shell shell)
	{
		try
		{
			Field field = shell.getClass().getField("view"); //$NON-NLS-1$
			Object view = field.get(shell);
			Method method = view.getClass().getMethod("window"); //$NON-NLS-1$
			return method.invoke(view);
		}
		catch (Exception e)
		{
			// ignores since non-OSX platforms will always throw the exception
		}
		return null;
	}

	static void setWindowFullscreen(Shell shell)
	{
		Object nsWindow = getNSWindow(shell);
		if (nsWindow != null)
		{
			SO.Reflect.executeLong(nsWindow, "setCollectionBehavior", new Class[] { SO.NSUInteger }, (int) (1 << 7)); //$NON-NLS-1$
		}
	}
}
