/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.findbar.impl;

import java.util.ResourceBundle;

import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.texteditor.FindReplaceAction;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.editor.findbar.api.IFindBarDecorated;
import com.aptana.editor.findbar.api.IFindBarDecorator;

public class ShowFindBarAction extends FindReplaceAction
{

	private final ITextEditor textEditor;

	public ShowFindBarAction(ITextEditor textEditor)
	{
		super(ResourceBundle.getBundle(ShowFindBarAction.class.getName()), ShowFindBarAction.class.getSimpleName()
				+ ".", textEditor); //$NON-NLS-1$
		this.textEditor = textEditor;
		setActionDefinitionId(ActionFactory.FIND.create(textEditor.getSite().getWorkbenchWindow())
				.getActionDefinitionId());
	}

	@Override
	public void run()
	{

		IFindBarDecorated findBarDecorated = (IFindBarDecorated) textEditor.getAdapter(IFindBarDecorated.class);
		if (findBarDecorated != null)
		{
			IFindBarDecorator findBarDecorator = findBarDecorated.getFindBarDecorator();
			if (((FindBarDecorator) findBarDecorator).isFindTextActive())
			{
				super.run();
			}
			else
			{
				findBarDecorator.setVisible(true);
			}
		}
		else
		{
			super.run();
		}
	}
}
