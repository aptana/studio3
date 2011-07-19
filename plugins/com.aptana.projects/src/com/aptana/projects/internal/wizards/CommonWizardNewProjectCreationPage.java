/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.projects.internal.wizards;

import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

/**
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public class CommonWizardNewProjectCreationPage extends WizardNewProjectCreationPage implements
		IWizardProjectCreationPage
{

	/**
	 * Constructs a new common new project creation page.
	 * 
	 * @param pageName
	 */
	public CommonWizardNewProjectCreationPage(String pageName)
	{
		super(pageName);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.projects.internal.wizards.IWizardProjectCreationPage#isCloneFromGit()
	 */
	public boolean isCloneFromGit()
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.projects.internal.wizards.IWizardProjectCreationPage#getCloneURI()
	 */
	public String getCloneURI()
	{
		return null;
	}
}
