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
