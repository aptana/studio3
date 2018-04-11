/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ui.dialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.IInternalDebugUIConstants;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.eclipse.ui.model.AdaptableList;

import com.aptana.core.util.StringUtil;

public class SaveAndLaunchPromptDialog extends ListSelectionDialog
{

	private Button savePref;
	private List<IResource> dirtyResources;

	public SaveAndLaunchPromptDialog(Shell parentShell, Set<IResource> input,
			IStructuredContentProvider contentProvider, ILabelProvider labelProvider, String message)
	{
		super(parentShell, new AdaptableList(input), contentProvider, labelProvider, message);
		this.dirtyResources = new ArrayList<IResource>(input);
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite comp = (Composite) super.createDialogArea(parent);

		savePref = new Button(comp, SWT.CHECK);
		savePref.setText(Messages.SaveAndLaunchPromptDialog_SaveMessage);
		savePref.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				getViewer().setAllChecked(savePref.getSelection());
			}
		});
		return comp;
	}

	protected void okPressed()
	{
		IPreferenceStore store = DebugUIPlugin.getDefault().getPreferenceStore();
		String val = (savePref.getSelection() ? MessageDialogWithToggle.ALWAYS : MessageDialogWithToggle.PROMPT);
		store.setValue(IInternalDebugUIConstants.PREF_SAVE_DIRTY_EDITORS_BEFORE_LAUNCH, val);
		super.okPressed();
	}

	@Override
	public int open()
	{
		String saveDirty = Platform.getPreferencesService().getString(IDebugUIConstants.PLUGIN_ID,
				IInternalDebugUIConstants.PREF_SAVE_DIRTY_EDITORS_BEFORE_LAUNCH, StringUtil.EMPTY,
				new IScopeContext[] { InstanceScope.INSTANCE, DefaultScope.INSTANCE });
		if (saveDirty.equals(MessageDialogWithToggle.ALWAYS))
		{
			setResult(dirtyResources);
			return Window.OK;
		}
		return super.open();
	}

}
