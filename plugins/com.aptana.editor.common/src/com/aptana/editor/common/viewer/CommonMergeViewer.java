/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.viewer;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.contentmergeviewer.TextMergeViewer;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.CursorLinePainter;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TypedListener;

import com.aptana.theme.ColorManager;
import com.aptana.theme.IThemeManager;
import com.aptana.theme.Theme;
import com.aptana.theme.ThemePlugin;
import com.aptana.ui.util.UIUtils;

/**
 * Intended for our specific languages to subclass. This enforces the correct selection colors in the Compare Editors.
 * 
 * @author cwilliams
 */
public abstract class CommonMergeViewer extends TextMergeViewer
{
	protected CommonMergeViewer(Composite parent, CompareConfiguration configuration)
	{
		super(parent, configuration);
	}

	@Override
	protected void configureTextViewer(final TextViewer textViewer)
	{
		setBackgroundColor(getCurrentTheme().getBackground());
		setForegroundColor(getCurrentTheme().getForeground());
		setSelectionColors(textViewer);

		// Force line highlight color. We need to perform this after the line painter is attached, which happens after
		// the return of this method. Scheduling async seems to work.
		UIUtils.getDisplay().asyncExec(new Runnable()
		{

			public void run()
			{
				CursorLinePainter p = getCursorLinePainterInstalled(textViewer);
				if (p != null)
				{
					p.setHighlightColor(getColorManager().getColor(getCurrentTheme().getLineHighlightAgainstBG()));
				}
			}
		});
	}

	private CursorLinePainter getCursorLinePainterInstalled(TextViewer viewer)
	{
		Listener[] listeners = viewer.getTextWidget().getListeners(3001/* StyledText.LineGetBackground */);
		for (Listener listener : listeners)
		{
			if (listener instanceof TypedListener)
			{
				TypedListener typedListener = (TypedListener) listener;
				if (typedListener.getEventListener() instanceof CursorLinePainter)
				{
					return (CursorLinePainter) typedListener.getEventListener();
				}
			}
		}
		return null;
	}

	@Override
	protected String getDocumentPartitioning()
	{
		return IDocumentExtension3.DEFAULT_PARTITIONING;
	}

	private void setSelectionColors(TextViewer sourceViewer)
	{
		// TODO Combine with code from ThemeableEditorExtension
		// Force selection color
		StyledText textWidget = sourceViewer.getTextWidget();
		Color existingSelectionBG = textWidget.getSelectionBackground();
		RGB selectionRGB = getCurrentTheme().getSelectionAgainstBG();
		if (!existingSelectionBG.getRGB().equals(selectionRGB))
		{
			textWidget.setSelectionBackground(getColorManager().getColor(selectionRGB));
		}

		if (!Platform.OS_MACOSX.equals(Platform.getOS()))
		{
			// Linux and windows need selection fg set or we just see a block of color.
			textWidget.setSelectionForeground(getColorManager().getColor(getCurrentTheme().getForeground()));
		}
	}

	protected Theme getCurrentTheme()
	{
		return getThemeManager().getCurrentTheme();
	}

	protected IThemeManager getThemeManager()
	{
		return ThemePlugin.getDefault().getThemeManager();
	}

	protected ColorManager getColorManager()
	{
		return ThemePlugin.getDefault().getColorManager();
	}
}
