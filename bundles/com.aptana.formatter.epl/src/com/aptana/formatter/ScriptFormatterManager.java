/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package com.aptana.formatter;

import com.aptana.formatter.epl.FormatterPlugin;

public class ScriptFormatterManager extends ContributionExtensionManager
{

	private static ScriptFormatterManager instance = null;

	public static synchronized ScriptFormatterManager getInstance()
	{
		if (instance == null)
		{
			instance = new ScriptFormatterManager();
		}
		return instance;
	}

	private static final String EXTPOINT = FormatterPlugin.PLUGIN_ID + ".formatterFactory"; //$NON-NLS-1$

	protected String getContributionElementName()
	{
		return "formatterFactory"; //$NON-NLS-1$
	}

	protected String getExtensionPoint()
	{
		return EXTPOINT;
	}

	protected boolean isValidContribution(Object object)
	{
		return object instanceof IScriptFormatterFactory && ((IScriptFormatterFactory) object).isValid();
	}

	public static boolean hasFormatterFor(final String contentType)
	{
		return getInstance().hasContributions(contentType);
	}

	public static IScriptFormatterFactory getSelected(String contentType)
	{
		return (IScriptFormatterFactory) getInstance().getSelectedContribution(contentType);
	}

	/**
	 * Returns the main Content-Type that this factory is set for. The content-type that is returned is the one defined
	 * in the extension contribution of the given factory.
	 * 
	 * @param factory
	 *            An {@link IScriptFormatterFactory}
	 * @return the main Content-Type that the given factory
	 */
	public static String getContentTypeByFactory(IScriptFormatterFactory factory)
	{
		return getInstance().getContentTypeByContribution(factory);
	}
}
