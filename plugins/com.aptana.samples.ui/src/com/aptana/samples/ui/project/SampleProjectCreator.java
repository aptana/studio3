/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.samples.ui.project;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;

import com.aptana.samples.model.IProjectSample;
import com.aptana.ui.util.SWTUtils;
import com.aptana.ui.util.UIUtils;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class SampleProjectCreator
{

	/**
	 * Opens a wizard for creating sample project.
	 * 
	 * @param sample
	 *            the sample
	 */
	public static void createSampleProject(IProjectSample sample)
	{
		openWizard(new NewSampleProjectWizard(sample));
	}

	private static void openWizard(NewSampleProjectWizard wizard)
	{
		wizard.init(PlatformUI.getWorkbench(), null);
		WizardDialog dialog = new WizardDialog(UIUtils.getActiveShell(), wizard);
		dialog.create();
		SWTUtils.centerAndPack(dialog.getShell(), UIUtils.getActiveWorkbenchWindow().getShell());
		dialog.open();
	}
}
