package com.aptana.editor.html.formatter;

import java.net.URL;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.source.ISharedTextColors;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.html.HTMLSourceViewerConfiguration;
import com.aptana.editor.html.formatter.preferences.HTMLFormatterModifyDialog;
import com.aptana.formatter.AbstractScriptFormatterFactory;
import com.aptana.formatter.ui.IFormatterModifyDialog;
import com.aptana.formatter.ui.IFormatterModifyDialogOwner;
import com.aptana.formatter.ui.IScriptFormatter;
import com.aptana.ui.preferences.PreferenceKey;

/**
 * HTML formatter factory
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class HTMLFormatterFactory extends AbstractScriptFormatterFactory
{
	private static final PreferenceKey FORMATTER_PREF_KEY = new PreferenceKey(HTMLFormatterPlugin.PLUGIN_ID,
			HTMLFormatterConstants.FORMATTER_ID);
	
	private static final String FORMATTER_PREVIEW_FILE = "formatterPreview.html"; //$NON-NLS-1$

	private static final String[] KEYS = {
		// TODO - Add more...
		HTMLFormatterConstants.FORMATTER_INDENTATION_SIZE,
		HTMLFormatterConstants.FORMATTER_TAB_CHAR,
		HTMLFormatterConstants.FORMATTER_TAB_SIZE
	};

	public PreferenceKey[] getPreferenceKeys()
	{
		final PreferenceKey[] result = new PreferenceKey[KEYS.length];
		for (int i = 0; i < KEYS.length; ++i)
		{
			final String key = KEYS[i];
			result[i] = new PreferenceKey(HTMLFormatterPlugin.PLUGIN_ID, key);
		}
		return result;
	}

	public IScriptFormatter createFormatter(String lineDelimiter, Map<String, String> preferences)
	{
		return new HTMLFormatter(preferences);
	}

	public URL getPreviewContent()
	{
		return getClass().getResource(FORMATTER_PREVIEW_FILE);
	}

	public IFormatterModifyDialog createDialog(IFormatterModifyDialogOwner dialogOwner)
	{
		return new HTMLFormatterModifyDialog(dialogOwner, this);
	}

	@Override
	public SourceViewerConfiguration createSimpleSourceViewerConfiguration(ISharedTextColors colorManager,
			IPreferenceStore preferenceStore, ITextEditor editor, boolean configureFormatter)
	{
		return new HTMLSourceViewerConfiguration(preferenceStore, (AbstractThemeableEditor) editor);
	}

	@Override
	public PreferenceKey getFormatterPreferenceKey()
	{
		return FORMATTER_PREF_KEY;
	}

	@Override
	public IPreferenceStore getPreferenceStore()
	{
		return HTMLFormatterPlugin.getDefault().getPreferenceStore();
	}
}
