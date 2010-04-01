package com.aptana.terminal.preferences;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import com.aptana.terminal.Activator;

public class TerminalPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{
	BooleanFieldEditor _closeOnExitEditor;
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	protected void createFieldEditors()
	{
		Composite parent = this.getFieldEditorParent();
		String key = IPreferenceConstants.CLOSE_VIEW_ON_EXIT;
		String label = Messages.TerminalPreferencePage_Close_View_On_Exit;
		
		this._closeOnExitEditor = new BooleanFieldEditor(key, label, parent);
		
		this.addField(this._closeOnExitEditor);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench)
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#doGetPreferenceStore()
	 */
	protected IPreferenceStore doGetPreferenceStore()
	{
//		return Activator.getDefault().getPreferenceStore();
		return new ScopedPreferenceStore(new InstanceScope(), Activator.PLUGIN_ID);
	}
}
