/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * This is an action template for actions that toggle whether it links its selection to the active editor.
 */
public class BaseToggleLinkingAction extends Action
{

	/**
	 * Constructs a new action.
	 */
	public BaseToggleLinkingAction()
	{
		super(Messages.AbstractToggleLinkingAction_LBL);
		setDescription(Messages.AbstractToggleLinkingAction_Description);
		setToolTipText(Messages.AbstractToggleLinkingAction_TTP);
		setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_ELCL_SYNCED));
	}
}
