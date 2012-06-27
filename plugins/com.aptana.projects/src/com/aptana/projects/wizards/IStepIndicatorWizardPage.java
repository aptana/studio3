/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.projects.wizards;

/**
 * Interface to define a page that contributes a step indicator composite
 * 
 * @author Nam Le <nle@appcelerator.com>
 */
public interface IStepIndicatorWizardPage
{
	/**
	 * Returns the name used to represent this page in the step indicator
	 * 
	 * @return
	 */
	public String getStepName();

	/**
	 * Sets the steps for the step indicator
	 * 
	 * @param stepNames
	 */
	public void initStepIndicator(String[] stepNames);
}
