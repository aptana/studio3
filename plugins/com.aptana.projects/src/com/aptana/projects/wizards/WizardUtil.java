/**
 * Appcelerator Titanium Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */
package com.aptana.projects.wizards;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.wizard.IWizardPage;


/**
 * Utilities for JFace Wizards
 * 
 * @author Nam Le <nle@appcelerator.com>
 */
public class WizardUtil
{
	/**
	 * Iterates through the wizard pages and initializes the step indicators
	 * 
	 * @param wizardPages
	 */
	public static void initStepIndicatorPages(IWizardPage[] wizardPages)
	{
		// Add different pages whether it is a full or reduced set wizard
		List<IStepIndicatorWizardPage> stepPages = new ArrayList<IStepIndicatorWizardPage>();
		List<String> stepNames = new ArrayList<String>();

		for (IWizardPage page : wizardPages)
		{
			if (page instanceof IStepIndicatorWizardPage)
			{
				IStepIndicatorWizardPage stepIndicatorWizardPage = (IStepIndicatorWizardPage) page;
				stepPages.add(stepIndicatorWizardPage);
				stepNames.add(stepIndicatorWizardPage.getStepName());
			}
		}

		String[] names = stepNames.toArray(new String[stepNames.size()]);
		for (IStepIndicatorWizardPage page : stepPages)
		{
			page.initStepIndicator(names);
		}
	}
}
