/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.coffee;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.osgi.framework.BundleContext;

public class CoffeeScriptEditorPlugin extends AbstractUIPlugin
{
	public static final String PLUGIN_ID = "com.aptana.editor.coffee"; //$NON-NLS-1$

	private static CoffeeScriptEditorPlugin plugin;

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static CoffeeScriptEditorPlugin getDefault()
	{
		return plugin;
	}

	private CoffeeDocumentProvider coffeeDocumentProvider;

	/**
	 * The constructor
	 */
	public CoffeeScriptEditorPlugin()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		plugin = null;
		coffeeDocumentProvider = null;
		super.stop(context);
	}

	/**
	 * Returns CoffeeScript document provider
	 * 
	 * @return
	 */
	public synchronized IDocumentProvider getCoffeeDocumentProvider()
	{
		if (coffeeDocumentProvider == null)
		{
			coffeeDocumentProvider = new CoffeeDocumentProvider();
		}
		return coffeeDocumentProvider;
	}

	@Override
	protected void initializeImageRegistry(ImageRegistry reg)
	{
		String[] paths = new String[] { ICoffeeUIConstants.PROPERTY_ICON, ICoffeeUIConstants.FUNCTION_ICON,
				ICoffeeUIConstants.BOOLEAN_ICON, ICoffeeUIConstants.REGEX_ICON, ICoffeeUIConstants.STRING_ICON,
				ICoffeeUIConstants.NULL_ICON, ICoffeeUIConstants.NUMBER_ICON, ICoffeeUIConstants.OBJECT_ICON,
				ICoffeeUIConstants.ARRAY_ICON, ICoffeeUIConstants.CLASS_ICON };
		for (String imageFilePath : paths)
		{
			reg.put(imageFilePath, imageDescriptorFromPlugin(PLUGIN_ID, imageFilePath));
		}
	}
}
