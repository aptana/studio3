/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.projects.wizards;

import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.wizard.IWizardPage;

/**
 * @author Nam Le <nle@appcelerator.com>
 */
public interface IProjectWizardContributor extends IExecutableExtension
{

	/**
	 * Reponsible for creating the project wizard pages
	 * 
	 * @return
	 */
	public IWizardPage createWizardPage();

	/**
	 * Returns the check whether this nature ids passed matches the contributor natureid
	 * 
	 * @param natureIds
	 * @return
	 */
	public boolean hasNatureId(String[] natureIds);

	/**
	 * Performs the work to finish the wizard
	 * 
	 * @return
	 */
	public IStatus performWizardFinish();
}
