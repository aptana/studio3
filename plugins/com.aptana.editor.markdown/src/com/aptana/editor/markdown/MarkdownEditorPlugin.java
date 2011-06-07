/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.markdown;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class MarkdownEditorPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.editor.markdown"; //$NON-NLS-1$

	// The shared instance
	private static MarkdownEditorPlugin plugin;
	
	private IDocumentProvider markdownDocumentProvider;


	/**
	 * The constructor
	 */
	public MarkdownEditorPlugin() {
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
	public static MarkdownEditorPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns Markdown document provider
	 * @return
	 */
	public synchronized IDocumentProvider getMarkdownDocumentProvider() {
		if (markdownDocumentProvider == null) {
			markdownDocumentProvider = new MarkdownDocumentProvider();
		}
		return markdownDocumentProvider;
	}

}
