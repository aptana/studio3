/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.actions;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class DefaultNavigatorContributionItem extends ContributionItem
{

	private DefaultNavigatorActionProvider actionProvider;
	private ToolItem toolItem;

	public DefaultNavigatorContributionItem(DefaultNavigatorActionProvider actionProvider)
	{
		super(actionProvider.getActionId());
		this.actionProvider = actionProvider;
	}

	public DefaultNavigatorActionProvider getActionProvider()
	{
		return actionProvider;
	}

	public void setEnabled(boolean enabled)
	{
		if (toolItem != null)
		{
			toolItem.setEnabled(enabled);
		}
	}

	@Override
	public void fill(final ToolBar parent, int index)
	{
		toolItem = new ToolItem(parent, SWT.DROP_DOWN);
		toolItem.setImage(actionProvider.getImage());
		// toolItem.setDisabledImage(actionProvider.getDisabledImage());
		// toolItem.setHotImage(actionProvider.getHotImage());
		toolItem.setToolTipText(actionProvider.getToolTip());

		toolItem.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(SelectionEvent selectionEvent)
			{
				actionProvider.run(parent);
			}
		});
	}
}
