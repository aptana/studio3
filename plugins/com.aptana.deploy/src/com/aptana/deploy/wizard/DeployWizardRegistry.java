package com.aptana.deploy.wizard;

import org.eclipse.ui.internal.wizards.AbstractExtensionWizardRegistry;

import com.aptana.deploy.DeployPlugin;

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
	public DeployWizardRegistry()
	{
		super();
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
		return DeployPlugin.getPluginIdentifier();
	}
}
