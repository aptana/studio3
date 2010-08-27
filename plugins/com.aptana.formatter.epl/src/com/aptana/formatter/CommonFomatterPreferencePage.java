package com.aptana.formatter;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.source.ISharedTextColors;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.formatter.epl.FormatterPlugin;
import com.aptana.formatter.ui.AbstractFormatterPreferencePage;

public class CommonFomatterPreferencePage extends AbstractFormatterPreferencePage
{
	public CommonFomatterPreferencePage()
	{
		// Hide the global Apply and Defaults buttons. They will appear locally for each formatter on the preview pane.
		noDefaultAndApplyButton();
	}

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
		return FormatterPlugin.getDefault().getDialogSettings();
	}

	@Override
	protected void setPreferenceStore()
	{
		// TODO Auto-generated method stub

	}

}
