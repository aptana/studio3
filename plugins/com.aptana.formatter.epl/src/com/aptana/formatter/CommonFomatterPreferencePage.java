package com.aptana.formatter;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.source.ISharedTextColors;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.formatter.ui.AbstractFormatterPreferencePage;
import com.aptana.ui.preferences.PreferenceKey;

public class CommonFomatterPreferencePage extends AbstractFormatterPreferencePage
{
	@Override
	protected SourceViewerConfiguration createSimpleSourceViewerConfiguration(ISharedTextColors colorManager,
			IPreferenceStore preferenceStore, ITextEditor editor, boolean configureFormatter)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected IDialogSettings getDialogSettings()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected PreferenceKey getFormatterPreferenceKey()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void setPreferenceStore()
	{
		// TODO Auto-generated method stub

	}

}
