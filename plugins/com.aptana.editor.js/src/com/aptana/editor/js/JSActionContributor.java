/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js;

import java.util.ResourceBundle;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.RetargetTextEditorAction;

import com.aptana.editor.common.CommonTextEditorActionContributor;
import com.aptana.editor.js.actions.IJSActions;

/**
 * JSActionContributor
 */
public class JSActionContributor extends CommonTextEditorActionContributor
{
	private RetargetTextEditorAction fOpenDeclaration;

	/**
	 * JSActionContributor
	 */
	public JSActionContributor()
	{
		// Note that this messages bundle is used when constructing the actions.
		// Make sure no string are removed unintentionally from the properties file...
		ResourceBundle resourceBundle = Messages.getResourceBundle();

		fOpenDeclaration = new RetargetTextEditorAction(resourceBundle, "openDeclaration."); //$NON-NLS-1$
		fOpenDeclaration.setActionDefinitionId(IJSActions.OPEN_DECLARATION);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.texteditor.BasicTextEditorActionContributor#contributeToMenu(org.eclipse.jface.action.IMenuManager
	 * )
	 */
	@Override
	public void contributeToMenu(IMenuManager menu)
	{
		super.contributeToMenu(menu);

		IMenuManager navigateMenu = menu.findMenuUsingPath(IWorkbenchActionConstants.M_NAVIGATE);

		if (navigateMenu != null)
		{
			navigateMenu.appendToGroup(IWorkbenchActionConstants.OPEN_EXT, fOpenDeclaration);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.CommonTextEditorActionContributor#setActiveEditor(org.eclipse.ui.IEditorPart)
	 */
	@Override
	public void setActiveEditor(IEditorPart part)
	{
		super.setActiveEditor(part);

		if (part instanceof ITextEditor)
		{
			ITextEditor editor = (ITextEditor) part;

			fOpenDeclaration.setAction(getAction(editor, IJSActions.OPEN_DECLARATION));
		}
	}
}

