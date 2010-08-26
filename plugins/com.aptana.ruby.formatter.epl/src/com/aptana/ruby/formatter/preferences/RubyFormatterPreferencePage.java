package com.aptana.ruby.formatter.preferences;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.source.ISharedTextColors;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.ruby.RubySourceViewerConfiguration;
import com.aptana.formatter.ui.AbstractFormatterPreferencePage;
import com.aptana.ruby.formatter.RubyFormatterConstants;
import com.aptana.ruby.formatter.RubyFormatterPlugin;
import com.aptana.ui.preferences.PreferenceKey;

/**
 * Preference page for Ruby debugging engines
 */
public class RubyFormatterPreferencePage extends AbstractFormatterPreferencePage
{

	private static final PreferenceKey FORMATTER = new PreferenceKey(RubyFormatterPlugin.PLUGIN_ID,
			RubyFormatterConstants.FORMATTER_ID);

	protected PreferenceKey getFormatterPreferenceKey()
	{
		return FORMATTER;
	}

	protected IDialogSettings getDialogSettings()
	{
		return RubyFormatterPlugin.getDefault().getDialogSettings();
	}

	protected String getPreferencePageId()
	{
		return "com.aptana.ruby.preferences.formatter"; //$NON-NLS-1$
	}

	protected String getPropertyPageId()
	{
		return "com.aptana.ruby.propertyPage.formatter"; //$NON-NLS-1$
	}

	protected SourceViewerConfiguration createSimpleSourceViewerConfiguration(ISharedTextColors colorManager,
			IPreferenceStore preferenceStore, ITextEditor editor, boolean configureFormatter)
	{
		// TODO: Shalom  - Wrap this in 'simple' implementation?
		// return new SimpleRubySourceViewerConfiguration(colorManager,
		// preferenceStore, editor, IRubyPartitions.RUBY_PARTITIONING,
		// configureFormatter);
		return new RubySourceViewerConfiguration(preferenceStore, (AbstractThemeableEditor) editor);
	}

	protected void setPreferenceStore()
	{
		setPreferenceStore(RubyFormatterPlugin.getDefault().getPreferenceStore());
	}
}
