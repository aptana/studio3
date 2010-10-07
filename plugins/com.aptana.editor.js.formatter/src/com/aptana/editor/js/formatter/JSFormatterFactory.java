package com.aptana.editor.js.formatter;

import java.net.URL;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.source.ISharedTextColors;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.js.JSSourceViewerConfiguration;
import com.aptana.editor.js.formatter.preferences.JSFormatterModifyDialog;
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
public class JSFormatterFactory extends AbstractScriptFormatterFactory
{

	private static final PreferenceKey FORMATTER_PREF_KEY = new PreferenceKey(JSFormatterPlugin.PLUGIN_ID,
			JSFormatterConstants.FORMATTER_ID);

	private static final String FORMATTER_PREVIEW_FILE = "formatterPreview.html"; //$NON-NLS-1$

	private static final String[] KEYS = {
			// TODO - Add more...
			JSFormatterConstants.FORMATTER_INDENTATION_SIZE, JSFormatterConstants.FORMATTER_TAB_CHAR,
			JSFormatterConstants.FORMATTER_TAB_SIZE, JSFormatterConstants.WRAP_COMMENTS,
			JSFormatterConstants.WRAP_COMMENTS_LENGTH, JSFormatterConstants.INDENT_EXCLUDED_TAGS,
			JSFormatterConstants.NEW_LINES_EXCLUDED_TAGS, JSFormatterConstants.LINES_AFTER_ELEMENTS,
			JSFormatterConstants.LINES_AFTER_NON_HTML_ELEMENTS,
			JSFormatterConstants.LINES_BEFORE_NON_HTML_ELEMENTS, JSFormatterConstants.PRESERVED_LINES };

	public PreferenceKey[] getPreferenceKeys()
	{
		final PreferenceKey[] result = new PreferenceKey[KEYS.length];
		for (int i = 0; i < KEYS.length; ++i)
		{
			final String key = KEYS[i];
			result[i] = new PreferenceKey(JSFormatterPlugin.PLUGIN_ID, key);
		}
		return result;
	}

	public IScriptFormatter createFormatter(String lineSeparator, Map<String, String> preferences)
	{
		return new JSFormatter(lineSeparator, preferences, getMainContentType());
	}

	public URL getPreviewContent()
	{
		return getClass().getResource(FORMATTER_PREVIEW_FILE);
	}

	public IFormatterModifyDialog createDialog(IFormatterModifyDialogOwner dialogOwner)
	{
		return new JSFormatterModifyDialog(dialogOwner, this);
	}

	public SourceViewerConfiguration createSimpleSourceViewerConfiguration(ISharedTextColors colorManager,
			IPreferenceStore preferenceStore, ITextEditor editor, boolean configureFormatter)
	{
		return new JSSourceViewerConfiguration(preferenceStore, (AbstractThemeableEditor) editor);
	}

	public PreferenceKey getFormatterPreferenceKey()
	{
		return FORMATTER_PREF_KEY;
	}

	public IPreferenceStore getPreferenceStore()
	{
		return JSFormatterPlugin.getDefault().getPreferenceStore();
	}
}
