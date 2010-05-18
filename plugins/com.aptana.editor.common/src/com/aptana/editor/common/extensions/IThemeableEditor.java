package com.aptana.editor.common.extensions;

import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.viewers.ISelectionProvider;

/**
 * Interface that provides what's needed to the ThemeableEditorColorsExtension
 * to configure the editor for using the color themes. 
 */
public interface IThemeableEditor {

	/**
	 * @return the source viewer. It'll be used to invalidate the presentation and override the
	 * cursor, colors, etc.
	 */
	ISourceViewer getISourceViewer();

	/**
	 * @return the selection provider. It'll be used to override the selection color.
	 */
	ISelectionProvider getSelectionProvider();

	/**
	 * @return the vertical ruler (used to override its color).
	 */
	IVerticalRuler getIVerticalRuler();

}
