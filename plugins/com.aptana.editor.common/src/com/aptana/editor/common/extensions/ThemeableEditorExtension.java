/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.extensions;

import java.lang.ref.WeakReference;
import java.text.MessageFormat;
import java.util.Iterator;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.text.ITextViewerExtension2;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRulerColumn;
import org.eclipse.jface.text.source.LineNumberRulerColumn;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.preferences.IPreferenceConstants;
import com.aptana.theme.IThemeManager;
import com.aptana.theme.ThemePlugin;

/**
 * Used to override the colors of the editor (ruler, background, caret, etc.)
 */
public class ThemeableEditorExtension
{

	/**
	 * The original parent of the editor.
	 */
	private Composite fParent;

	/**
	 * The line column
	 */
	private LineNumberRulerColumn fLineColumn;

	/**
	 * This paints the entire line in the background color when there's only one bg color used on that line. To make
	 * things like block comments with a different bg color look more like Textmate.
	 */
	private LineBackgroundPainter fFullLineBackgroundPainter;

	/**
	 * A weak reference to the editor (so that it doesn't have to be passed on all methods of this class).
	 */
	private WeakReference<IThemeableEditor> fEditor;

	public ThemeableEditorExtension(IThemeableEditor editor)
	{
		this.fEditor = new WeakReference<IThemeableEditor>(editor);
	}

	// Public interface (clients are responsible for calling these methods as needed).

	public void overrideThemeColors()
	{
		IThemeableEditor editor = this.fEditor.get();
		if (editor != null)
		{
			ThemePlugin.getDefault().getControlThemerFactory().apply(editor.getISourceViewer());
		}

		setCharacterPairColor(getThemeManager().getCurrentTheme().getCharacterPairColor());
		overrideRulerColors();
	}

	public void initializeLineNumberRulerColumn(LineNumberRulerColumn rulerColumn)
	{
		this.fLineColumn = rulerColumn;
	}

	public void setParent(Composite parent)
	{
		this.fParent = parent;
	}

	public void handlePreferenceStoreChanged(PropertyChangeEvent event)
	{
		if (event.getProperty().equals(IThemeManager.THEME_CHANGED))
		{
			IThemeableEditor editor = this.fEditor.get();
			overrideThemeColors();
			if (editor != null)
			{
				editor.getISourceViewer().invalidateTextPresentation();
			}
		}
		else if (event.getProperty().equals(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE))
		{
			Object newValue = event.getNewValue();
			if (newValue instanceof Boolean)
			{
				boolean on = (Boolean) newValue;
				fFullLineBackgroundPainter.setHighlightLineEnabled(on);
			}
		}
		else if (event.getProperty().equals(AbstractTextEditor.PREFERENCE_COLOR_SELECTION_BACKGROUND_SYSTEM_DEFAULT))
		{
			overrideRulerColors();
		}
	}

	public void createBackgroundPainter(ISourceViewer viewer)
	{
		if (fFullLineBackgroundPainter == null)
		{
			if (viewer instanceof ITextViewerExtension2)
			{
				boolean lineHighlight = Platform.getPreferencesService().getBoolean(EditorsUI.PLUGIN_ID,
						AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE, true, null);
				fFullLineBackgroundPainter = new LineBackgroundPainter(viewer);
				fFullLineBackgroundPainter.setHighlightLineEnabled(lineHighlight);
				ITextViewerExtension2 extension = (ITextViewerExtension2) viewer;
				extension.addPainter(fFullLineBackgroundPainter);
			}
		}
	}

	public void dispose()
	{
		fLineColumn = null;
		if (fEditor != null)
		{
			fEditor.clear();
			fEditor = null;
		}
		fParent = null;
		if (fFullLineBackgroundPainter != null)
		{
			fFullLineBackgroundPainter.deactivate(true);
			fFullLineBackgroundPainter.dispose();
			fFullLineBackgroundPainter = null;
		}
	}

	protected IThemeManager getThemeManager()
	{
		return ThemePlugin.getDefault().getThemeManager();
	}

	@SuppressWarnings("unchecked")
	private void overrideRulerColors()
	{
		IThemeableEditor editor = this.fEditor.get();

		// default to bg color of surrounding composite
		Color bg = fParent == null ? null : fParent.getBackground();
		// Use editor background color if we can
		if (editor != null)
		{
			ISourceViewer sv = editor.getISourceViewer();
			if (sv != null)
			{
				StyledText text = sv.getTextWidget();
				bg = text.getBackground();
			}

			// force the colors for all the ruler columns (specifically so we force the folding bg to match).
			Iterator<IVerticalRulerColumn> iter = ((CompositeRuler) editor.getIVerticalRuler()).getDecoratorIterator();
			while (iter.hasNext())
			{
				IVerticalRulerColumn column = iter.next();
				column.getControl().setBackground(bg);
			}
		}

		if (fLineColumn != null)
		{
			fLineColumn.setBackground(bg);
		}
	}

	private void setCharacterPairColor(RGB rgb)
	{
		IEclipsePreferences prefs = EclipseUtil.instanceScope().getNode(CommonEditorPlugin.PLUGIN_ID);
		prefs.put(IPreferenceConstants.CHARACTER_PAIR_COLOR,
				MessageFormat.format("{0},{1},{2}", rgb.red, rgb.green, rgb.blue)); //$NON-NLS-1$
		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			IdeLog.logError(CommonEditorPlugin.getDefault(), e);
		}
	}
}
