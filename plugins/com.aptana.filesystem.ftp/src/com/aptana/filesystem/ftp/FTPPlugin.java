/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable com.instantiations.assist.eclipse.analysis.audit.rule.effectivejava.enforceTheSingletonPropertyWithAPrivateConstructor
// $codepro.audit.disable declaredExceptions
// $codepro.audit.disable staticFieldNamingConvention

package com.aptana.filesystem.ftp;

import java.io.OutputStream;
import java.io.PrintWriter;

import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

import com.enterprisedt.util.debug.Level;
import com.enterprisedt.util.debug.Logger;

/**
 * The activator class controls the plug-in life cycle
 */
public class FTPPlugin extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.filesystem.ftp"; //$NON-NLS-1$

	// The shared instance
	private static FTPPlugin plugin;

	private IFTPCommandLog ftpCommandLog;

	/**
	 * The constructor
	 */
	public FTPPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		if ("true".equals(Platform.getDebugOption("com.aptana.filesystem.ftp/ftplib_debug"))) { //$NON-NLS-1$ //$NON-NLS-2$
			Logger.setLevel(Level.DEBUG);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static FTPPlugin getDefault() {
		return plugin;
	}

	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}

	public PrintWriter getFTPLogWriter() {
		if (ftpCommandLog == null) {
			ftpCommandLog = (IFTPCommandLog) getContributedAdapter(IFTPCommandLog.class);
		}
		if (ftpCommandLog != null) {
			OutputStream out = ftpCommandLog.getOutputStream();
			if (out != null) {
				return new PrintWriter(out); // $codepro.audit.disable closeWhereCreated
			}
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	private Object getContributedAdapter(Class clazz) {
		Object adapter = null;
		IAdapterManager manager = Platform.getAdapterManager();
		if (manager.hasAdapter(this, clazz.getName())) {
			adapter = manager.getAdapter(this, clazz.getName());
			if (adapter == null) {
				adapter = manager.loadAdapter(this, clazz.getName());
			}
		}
		return adapter;
	}
}
