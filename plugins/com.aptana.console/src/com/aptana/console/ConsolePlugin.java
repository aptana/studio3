/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable declaredExceptions
// $codepro.audit.disable staticFieldNamingConvention
// $codepro.audit.disable com.instantiations.assist.eclipse.analysis.audit.rule.effectivejava.enforceTheSingletonPropertyWithAPrivateConstructor

package com.aptana.console;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.aptana.console.internal.expressions.ExpressionManager;

/**
 * The activator class controls the plug-in life cycle
 */
public class ConsolePlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.console"; //$NON-NLS-1$

	// The shared instance
	private static ConsolePlugin plugin;
	
	private ExpressionManager expressionManager;

	/**
	 * The constructor
	 */
	public ConsolePlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
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
	public static ConsolePlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns instance of Expression Manager
	 * @return
	 */
	public ExpressionManager getExpressionManager() {
		if (expressionManager == null) {
			expressionManager = new ExpressionManager();
		}
		return expressionManager;
	}
}
