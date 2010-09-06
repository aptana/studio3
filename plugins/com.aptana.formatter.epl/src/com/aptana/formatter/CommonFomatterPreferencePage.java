package com.aptana.formatter;

import org.eclipse.jface.dialogs.IDialogSettings;

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
