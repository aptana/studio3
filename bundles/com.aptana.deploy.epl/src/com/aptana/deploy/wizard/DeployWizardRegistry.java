/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.deploy.wizard;

import org.eclipse.ui.internal.wizards.AbstractExtensionWizardRegistry;

import com.aptana.deploy.epl.DeployEplPlugin;

@SuppressWarnings("restriction")
public class DeployWizardRegistry extends AbstractExtensionWizardRegistry
{

	private static final String EXT_PT = "deployWizards"; //$NON-NLS-1$

	private static DeployWizardRegistry singleton;

	/**
	 * Return the singleton instance of this class.
	 * 
	 * @return the singleton instance of this class
	 */
	public static synchronized DeployWizardRegistry getInstance()
	{
		if (singleton == null)
		{
			singleton = new DeployWizardRegistry();
		}
		return singleton;
	}

	/**
	 * 
	 */
	private DeployWizardRegistry()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.internal.wizards.AbstractExtensionWizardRegistry#getExtensionPoint()
	 */
	protected String getExtensionPoint()
	{
		return EXT_PT;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.internal.wizards.AbstractExtensionWizardRegistry#getPlugin()
	 */
	protected String getPlugin()
	{
		return DeployEplPlugin.PLUGIN_ID;
	}
}
