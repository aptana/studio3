/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.internal.preferences;

import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.IPreferenceConstants;
import com.aptana.git.core.model.GitExecutable;

public class GitExecutableLocationPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{

	private FileFieldEditor fileEditor;
	private BooleanFieldEditor pullIndicatorEditor;

	public GitExecutableLocationPage()
	{
		super();
	}

	public void init(IWorkbench workbench)
	{
	}

	@Override
	protected void createFieldEditors()
	{
		// Git Executable location
		fileEditor = new FileFieldEditor(IPreferenceConstants.GIT_EXECUTABLE_PATH, Messages.GitExecutableLocationPage_LocationLabel, true,
				FileFieldEditor.VALIDATE_ON_KEY_STROKE, getFieldEditorParent())
		{
			@Override
			protected boolean checkState()
			{
				boolean ok = super.checkState();
				if (!ok)
					return ok;

				// Now check that the executable is ok
				String text = getTextControl().getText();
				if (text != null && text.trim().length() > 0)
				{
					if (!GitExecutable.acceptBinary(Path.fromOSString(text)))
					{
						showErrorMessage(NLS.bind(Messages.GitExecutableLocationPage_InvalidLocationErrorMessage,
								GitExecutable.MIN_GIT_VERSION));
						return false;
					}
				}

				clearErrorMessage();
				return true;
			}
		};
		// Git pull indicator
		pullIndicatorEditor = new BooleanFieldEditor(IPreferenceConstants.GIT_CALCULATE_PULL_INDICATOR,
				Messages.GitExecutableLocationPage_CalculatePullIndicatorLabel, getFieldEditorParent());
		addField(fileEditor);
		addField(pullIndicatorEditor);
	}

	@Override
	protected IPreferenceStore doGetPreferenceStore()
	{
		return new ScopedPreferenceStore(new InstanceScope(), GitPlugin.getPluginId());
	}
}
