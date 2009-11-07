package com.aptana.radrails.editor.common;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;

import com.aptana.radrails.editor.common.theme.ThemeUtil;

/**
 * Provides a way to override the editor fg, bg and selection fg, bg from what is set in global text editor color prefs.
 * TODO Need a way to override the caret color!
 * 
 * @author cwilliams
 */
@SuppressWarnings("restriction")
public abstract class AbstractThemeableEditor extends AbstractDecoratedTextEditor
{

	/**
	 * AbstractThemeableEditor
	 */
	public AbstractThemeableEditor()
	{
		super();
	}

	@Override
	protected void initializeEditor()
	{
		setPreferenceStore(new ChainedPreferenceStore(new IPreferenceStore[] {
				CommonEditorPlugin.getDefault().getPreferenceStore(), EditorsPlugin.getDefault().getPreferenceStore() }));
	}

	@Override
	protected void initializeViewerColors(ISourceViewer viewer)
	{
		ThemeUtil.getActiveTheme();
		super.initializeViewerColors(viewer);
	}

	@Override
	protected void handlePreferenceStoreChanged(PropertyChangeEvent event)
	{
		super.handlePreferenceStoreChanged(event);
		if (event.getProperty().equals(ThemeUtil.ACTIVE_THEME))
		{
			getSourceViewer().invalidateTextPresentation();
		}
	}
}
