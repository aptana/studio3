/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.internal;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IPlaceholderFolderLayout;
import org.eclipse.ui.console.IConsoleConstants;

public class WebPerspectiveFactory implements IPerspectiveFactory
{

	private static final String APP_EXPLORER_ID = "com.aptana.explorer.view"; //$NON-NLS-1$
	private static final String TERMINAL_VIEW = "com.aptana.terminal.views.terminal"; //$NON-NLS-1$

	public void createInitialLayout(IPageLayout layout)
	{
		// Get the editor area
		String editorArea = layout.getEditorArea();

		// Left
		IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT, 0.25f, editorArea); //$NON-NLS-1$
		left.addView(APP_EXPLORER_ID);

		// Bottom right: Console. Had to leave this programmatic to get the Console appear in bottom right
		IPlaceholderFolderLayout consoleArea = layout.createPlaceholderFolder(
				"terminalArea", IPageLayout.BOTTOM, 0.75f, //$NON-NLS-1$
				editorArea);
		consoleArea.addPlaceholder(IConsoleConstants.ID_CONSOLE_VIEW);
		consoleArea.addPlaceholder(TERMINAL_VIEW+":*"); //$NON-NLS-1$
	}
}
