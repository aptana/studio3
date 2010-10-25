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
		if ("true".equals(Platform.getDebugOption("com.aptana.filesystem.ftp/ftplib_debug"))) {
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
				return new PrintWriter(out);
			}
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	private Object getContributedAdapter(Class clazz) {
		Object adapter = null;
		IAdapterManager manager = Platform.getAdapterManager();
		if (manager.hasAdapter(this, clazz.getName())) {
			adapter = manager.getAdapter(this,clazz.getName());
			if (adapter == null) {
				adapter = manager.loadAdapter(this, clazz.getName());
			}
		}
		return adapter;
	}	
}
