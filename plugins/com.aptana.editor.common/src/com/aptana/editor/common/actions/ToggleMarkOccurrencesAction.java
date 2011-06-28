/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.actions;

import java.util.ResourceBundle;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.preferences.IPreferenceConstants;

/**
 * ToggleMarkOccurrencesAction
 */
public class ToggleMarkOccurrencesAction extends TextEditorAction implements IPropertyChangeListener
{
	private IPreferenceStore prefStore;

	/**
	 * ToggleMarkOccurrencesAction
	 * 
	 * @param resourceBundle
	 */
	public ToggleMarkOccurrencesAction(ResourceBundle resourceBundle)
	{
		super(resourceBundle, "ToggleMarkOccurrencesAction.", null, IAction.AS_CHECK_BOX); //$NON-NLS-1$

		update();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event)
	{
		if (event.getProperty().equals(IPreferenceConstants.EDITOR_MARK_OCCURRENCES))
		{
			setChecked(Boolean.valueOf(event.getNewValue().toString()).booleanValue());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run()
	{
		if (prefStore != null)
		{
			prefStore.setValue(IPreferenceConstants.EDITOR_MARK_OCCURRENCES, isChecked());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.TextEditorAction#setEditor(org.eclipse.ui.texteditor.ITextEditor)
	 */
	@Override
	public void setEditor(ITextEditor editor)
	{
		super.setEditor(editor);

		if (editor != null)
		{
			if (prefStore == null)
			{
				prefStore = (IPreferenceStore) editor.getAdapter(IPreferenceStore.class);

				if (prefStore != null)
				{
					prefStore.addPropertyChangeListener(this);
				}
			}
		}
		else if (prefStore != null)
		{
			prefStore.removePropertyChangeListener(this);
			prefStore = null;
		}

		update();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.TextEditorAction#update()
	 */
	@Override
	public void update()
	{
		ITextEditor editor = getTextEditor();
		boolean showMarkOccurrences = false;

		if (editor instanceof AbstractThemeableEditor)
		{
			showMarkOccurrences = ((AbstractThemeableEditor) editor).isMarkingOccurrences();
		}

		setEnabled(editor != null);
		setChecked(showMarkOccurrences);
	}
}
