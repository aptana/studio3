package com.aptana.editor.common.actions;

import org.eclipse.jface.action.Action;

import com.aptana.editor.common.CommonEditorPlugin;

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
		setImageDescriptor(CommonEditorPlugin.getImageDescriptor("icons/synced.gif")); //$NON-NLS-1$
	}
}
