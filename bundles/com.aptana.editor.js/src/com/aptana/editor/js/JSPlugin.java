/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class JSPlugin extends AbstractUIPlugin
{
	public static final String PLUGIN_ID = "com.aptana.editor.js"; //$NON-NLS-1$
	private static JSPlugin PLUGIN;

	private IDocumentProvider jsDocumentProvider;

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static JSPlugin getDefault()
	{
		return PLUGIN;
	}

	/**
	 * getImage
	 * 
	 * @param path
	 * @return
	 */
	public static Image getImage(String path)
	{
		ImageRegistry registry = PLUGIN.getImageRegistry();
		Image image = registry.get(path);
		if (image == null)
		{
			ImageDescriptor id = getImageDescriptor(path);
			if (id == null)
			{
				return null;
			}
			registry.put(path, id);
			image = registry.get(path);
		}
		return image;
	}

	/**
	 * getImageDescriptor
	 * 
	 * @param path
	 * @return
	 */
	public static ImageDescriptor getImageDescriptor(String path)
	{
		return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	/**
	 * The constructor
	 */
	public JSPlugin()
	{ // $codepro.audit.disable
		// com.instantiations.assist.eclipse.analysis.audit.rule.effectivejava.enforceTheSingletonPropertyWithAPrivateConstructor
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception // $codepro.audit.disable declaredExceptions
	{
		super.start(context);
		PLUGIN = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception // $codepro.audit.disable declaredExceptions
	{
		PLUGIN = null;
		super.stop(context);
	}

	/**
	 * Returns JS document provider
	 * 
	 * @return
	 */
	public synchronized IDocumentProvider getJSDocumentProvider()
	{
		if (jsDocumentProvider == null)
		{
			jsDocumentProvider = new JSDocumentProvider();
		}
		return jsDocumentProvider;
	}
}
