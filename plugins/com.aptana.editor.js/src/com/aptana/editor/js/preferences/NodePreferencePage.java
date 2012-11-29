/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.preferences;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.js.core.JSCorePlugin;
import com.aptana.js.core.node.INodeJSService;
import com.aptana.js.core.preferences.IPreferenceConstants;

public class NodePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{

	public static final String ID = "com.aptana.editor.js.nodejs.page"; //$NON-NLS-1$

	private StringFieldEditor sfe;
	private Composite fep;

	public void init(IWorkbench workbench)
	{
	}

	@Override
	protected void createFieldEditors()
	{
		// Node Executable location
		FileFieldEditor fileEditor = new FileFieldEditor(IPreferenceConstants.NODEJS_EXECUTABLE_PATH,
				Messages.NodePreferencePage_LocationLabel, true, FileFieldEditor.VALIDATE_ON_KEY_STROKE,
				getFieldEditorParent())
		{
			@Override
			protected boolean checkState()
			{
				boolean ok = super.checkState();
				if (!ok)
				{
					return false;
				}

				// Now check that the executable is ok
				String text = getTextControl().getText();
				if (!StringUtil.isEmpty(text))
				{
					IStatus status = getNodeService().acceptBinary(Path.fromOSString(text));
					if (!status.isOK())
					{
						showErrorMessage(status.getMessage());
						return false;
					}
				}

				clearErrorMessage();
				return true;
			}
		};
		addField(fileEditor);

		sfe = new StringFieldEditor(
				"some_non_existent_pref_key", Messages.NodePreferencePage_DetectedPathLabel, fep = getFieldEditorParent()); //$NON-NLS-1$
		addField(sfe);
	}

	@Override
	protected void initialize()
	{
		super.initialize();

		IPath path = getDetectedPath();
		sfe.setStringValue(path == null ? Messages.NodePreferencePage_NotDetected : path.toOSString());
		sfe.setEnabled(false, fep);
	}

	private IPath getDetectedPath()
	{
		return getNodeService().find();
	}

	protected INodeJSService getNodeService()
	{
		return JSCorePlugin.getDefault().getNodeJSService();
	}

	@Override
	protected IPreferenceStore doGetPreferenceStore()
	{
		return new ScopedPreferenceStore(EclipseUtil.instanceScope(), JSCorePlugin.PLUGIN_ID);
	}
}
