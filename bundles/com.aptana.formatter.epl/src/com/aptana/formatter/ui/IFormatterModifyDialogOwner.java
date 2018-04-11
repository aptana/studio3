/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package com.aptana.formatter.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public interface IFormatterModifyDialogOwner
{

	Shell getShell();

	ISourceViewer createPreview(Composite composite);

	IDialogSettings getDialogSettings();

	/**
	 * Returns a project reference in case the owner dialog is for a project-specific setting.
	 * 
	 * @return An {@link IProject}, or null.
	 */
	IProject getProject();
}
