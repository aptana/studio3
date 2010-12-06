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
package com.aptana.formatter.ui.preferences;

import org.eclipse.jface.dialogs.IDialogSettings;

import com.aptana.formatter.epl.FormatterPlugin;

public class CommonFomatterPreferencePage extends AbstractFormatterPreferencePage
{
	public CommonFomatterPreferencePage()
	{
		// Hide the global Apply and Defaults buttons. They will appear locally for each formatter on the preview pane.
		noDefaultAndApplyButton();
	}

	@Override
	protected IDialogSettings getDialogSettings()
	{
		return FormatterPlugin.getDefault().getDialogSettings();
	}

	@Override
	protected void setPreferenceStore()
	{
		setPreferenceStore(FormatterPlugin.getDefault().getPreferenceStore());
	}

}
