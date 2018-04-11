/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.common;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;

import com.aptana.core.util.ObjectUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.ui.util.UIUtils;

/**
 * @author Max Stepanov
 */
public final class CommonUtil
{
	/**
	 * To avoid multiple double click listener registrations to the project explorer view.
	 */
	private static boolean isDoubleClickRegistered;

	/**
	 * 
	 */
	private CommonUtil()
	{
	}

	// TODO Ideally we generate a cache of tokens. We'd need a map with weak keys and soft values. Ideally we'd also
	// have some sort of reaper to clean up unused refs over time. Perhaps just use Google's Guava CacheBuilder?
	public static IToken getToken(String tokenName)
	{
		return new Token(tokenName);
	}

	/**
	 * Registering double click listener to the project explorer viewer to handle
	 * "Open no extension files with JavaScript Source Editor" preference option. Which is added under the "Editors"
	 * section. Based on the preference option while opening a file will overwrite the default editor id of a file.
	 */
	public static synchronized void handleOpenWithEditorPref()
	{
		if (!isDoubleClickRegistered)
		{
			IViewPart explorerPart = UIUtils.findView(IPageLayout.ID_PROJECT_EXPLORER);
			if (explorerPart != null)
			{
				CommonNavigator projectExplorer = (CommonNavigator) explorerPart;
				CommonViewer viewer = projectExplorer.getCommonViewer();
				viewer.addDoubleClickListener(new IDoubleClickListener()
				{
					public void doubleClick(DoubleClickEvent event)
					{
						// Need to find the better way to manage this through extensions
						IResource selectedResource = UIUtils.getSelectedResource();
						if (!(selectedResource instanceof IFile))
						{
							return;
						}
						IFile selectedFile = (IFile) selectedResource;
						if (selectedFile.getFileExtension() == null)
						{
							String selectedEditor = Platform.getPreferencesService().getString(
									CommonEditorPlugin.PLUGIN_ID,
									com.aptana.editor.common.preferences.IPreferenceConstants.OPEN_WITH_EDITOR,
									StringUtil.EMPTY, null);
							if (selectedEditor != ICommonConstants.ECLIPSE_DEFAULT_EDITOR)
							{
								IDE.setDefaultEditor(selectedFile, selectedEditor);
							}
						}
						else if (selectedFile.getFileExtension().equals("xml")) //$NON-NLS-1$
						{
							IContainer parent = selectedResource.getParent();
							boolean isView = (parent != null && parent.getName().equals("views")); //$NON-NLS-1$
							if (!isView)
							{
								parent = parent.getParent();
								isView = (parent != null && parent.getName().equals("views")); //$NON-NLS-1$
							}
							if (isView)
							{
								IDE.setDefaultEditor(selectedFile, getSelectedEditor());
							}
							else if (selectedFile.getName().equals(CommonEditorPlugin.TIAPP_XML))
							{
								IDE.setDefaultEditor(selectedFile, CommonEditorPlugin.TI_APP_EDITOR_ID);
							}
							else if (selectedFile.getName().equals(CommonEditorPlugin.TIAPP_XML))
							{
								IDE.setDefaultEditor(selectedFile, CommonEditorPlugin.TI_APP_EDITOR_ID);
							}
							else
							{
								IDE.setDefaultEditor(selectedFile, CommonEditorPlugin.ALLOY_EDITOR_ID);
							}
						}
					}
				});
				isDoubleClickRegistered = true;
			}

		}
	}

	private static String getSelectedEditor()
	{
		if (ObjectUtil.areEqual(UIUtils.getActivePerspectiveId(), "com.appcelerator.titanium.perspective.designer")) //$NON-NLS-1$
		{
			return CommonEditorPlugin.APP_DESIGNER_EDITOR_ID;
		}
		return CommonEditorPlugin.ALLOY_EDITOR_ID;
	}

}
