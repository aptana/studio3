package com.aptana.editor.css.formatter;

import java.net.URL;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.source.ISharedTextColors;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.css.CSSSourceViewerConfiguration;
import com.aptana.editor.css.formatter.preferences.CSSFormatterModifyDialog;
import com.aptana.formatter.AbstractScriptFormatterFactory;
import com.aptana.formatter.ui.IFormatterModifyDialog;
import com.aptana.formatter.ui.IFormatterModifyDialogOwner;
import com.aptana.formatter.ui.IScriptFormatter;
import com.aptana.ui.preferences.PreferenceKey;

public class CSSFormatterFactory extends AbstractScriptFormatterFactory
{

	private static final PreferenceKey FORMATTER_PREF_KEY = new PreferenceKey(CSSFormatterPlugin.PLUGIN_ID,
			CSSFormatterConstants.FORMATTER_ID);

	private static final String FORMATTER_PREVIEW_FILE = "formatterPreview.css"; //$NON-NLS-1$

	private static final String[] KEYS = {
			// TODO - Add more...
			CSSFormatterConstants.FORMATTER_INDENTATION_SIZE, CSSFormatterConstants.FORMATTER_TAB_CHAR,
			CSSFormatterConstants.FORMATTER_TAB_SIZE, CSSFormatterConstants.WRAP_COMMENTS,
			CSSFormatterConstants.WRAP_COMMENTS_LENGTH, CSSFormatterConstants.NEW_LINES_BEFORE_BLOCKS,
			CSSFormatterConstants.LINES_AFTER_ELEMENTS, CSSFormatterConstants.PRESERVED_LINES };

	public PreferenceKey[] getPreferenceKeys()
	{
		final PreferenceKey[] result = new PreferenceKey[KEYS.length];
		for (int i = 0; i < KEYS.length; ++i)
		{
			final String key = KEYS[i];
			result[i] = new PreferenceKey(CSSFormatterPlugin.PLUGIN_ID, key);
		}
		return result;
	}

	public IScriptFormatter createFormatter(String lineSeparator, Map<String, String> preferences)
	{
		return new CSSFormatter(lineSeparator, preferences, getMainContentType());
	}

	public URL getPreviewContent()
	{
		return getClass().getResource(FORMATTER_PREVIEW_FILE);
	}

	public IFormatterModifyDialog createDialog(IFormatterModifyDialogOwner dialogOwner)
	{
		return new CSSFormatterModifyDialog(dialogOwner, this);
	}

	public SourceViewerConfiguration createSimpleSourceViewerConfiguration(ISharedTextColors colorManager,
			IPreferenceStore preferenceStore, ITextEditor editor, boolean configureFormatter)
	{
		return new CSSSourceViewerConfiguration(preferenceStore, (AbstractThemeableEditor) editor);
	}

	public PreferenceKey getFormatterPreferenceKey()
	{
		return FORMATTER_PREF_KEY;
	}

	public IPreferenceStore getPreferenceStore()
	{
		return CSSFormatterPlugin.getDefault().getPreferenceStore();
	}

}
