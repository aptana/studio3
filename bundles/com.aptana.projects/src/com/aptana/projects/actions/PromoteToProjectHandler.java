/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.projects.actions;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import com.aptana.projects.internal.wizards.PromoteToProjectWizard;
import com.aptana.ui.util.UIUtils;

public class PromoteToProjectHandler extends AbstractHandler
{

	private IStructuredSelection fSelection;

	public PromoteToProjectHandler()
	{
	}

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof IStructuredSelection)
		{
			fSelection = (IStructuredSelection) selection;
		}

		if (fSelection == null)
		{
			return null;
		}
		Object obj = fSelection.getFirstElement();

		File file = null;
		if (obj instanceof IAdaptable)
		{
			file = (File) ((IAdaptable) obj).getAdapter(File.class);
		}
		if (file != null)
		{
			// uses the parent folder if the file is not a directory
			String path = file.isDirectory() ? file.getPath() : file.getParentFile().getPath();

			PromoteToProjectWizard wizard = new PromoteToProjectWizard(path);
			WizardDialog dialog = new WizardDialog(UIUtils.getActiveShell(), wizard);
			dialog.open();
		}

		return null;
	}
}
