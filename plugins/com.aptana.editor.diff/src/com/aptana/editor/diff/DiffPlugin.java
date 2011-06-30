/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.diff;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.osgi.framework.BundleContext;

public class DiffPlugin extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "com.aptana.editor.diff"; //$NON-NLS-1$
	private static DiffPlugin plugin;
	
	private IDocumentProvider diffDocumentProvider;


	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static DiffPlugin getDefault() {
		return plugin;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns Diff document provider
	 * @return
	 */
	public synchronized IDocumentProvider getDiffDocumentProvider() {
		if (diffDocumentProvider == null) {
			diffDocumentProvider = new DiffDocumentProvider();
		}
		return diffDocumentProvider;
	}

}
