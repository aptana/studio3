/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
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
