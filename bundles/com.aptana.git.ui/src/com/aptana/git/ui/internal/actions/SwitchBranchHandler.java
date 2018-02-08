/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.internal.actions;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;

import com.aptana.core.util.ProcessStatus;
import com.aptana.git.core.model.GitRepository;
import com.aptana.ui.MenuDialogItem;
import com.aptana.ui.QuickMenuDialog;
import com.aptana.ui.util.UIUtils;

public class SwitchBranchHandler extends AbstractGitHandler
{

	@Override
	protected Object doExecute(ExecutionEvent event) throws ExecutionException
	{
		final GitRepository repo = getSelectedRepository();
		if (repo == null)
		{
			return null;
		}

		String currentBranch = repo.currentBranch();
		List<MenuDialogItem> listOfMaps = new ArrayList<MenuDialogItem>();
		for (String branch : repo.localBranches())
		{
			if (branch.equals(currentBranch))
			{
				continue;
			}
			listOfMaps.add(new MenuDialogItem(branch));
		}
		if (!listOfMaps.isEmpty())
		{
			QuickMenuDialog dialog = new QuickMenuDialog(getShell(), Messages.SwitchBranchHandler_PopupTitle);
			dialog.setInput(listOfMaps);
			if (dialog.open() != -1)
			{
				MenuDialogItem item = listOfMaps.get(dialog.getReturnCode());
				switchBranch(repo, item.getText());
			}
		}
		return null;
	}

	public static void switchBranch(final GitRepository repo, final String branchName)
	{
		String text = MessageFormat.format(Messages.SwitchBranchAction_BranchSwitch_Msg, branchName);
		IStatus switchStatus = repo.switchBranch(branchName, new NullProgressMonitor());
		if (!switchStatus.isOK())
		{
			// If we couldn't switch, surface up the output
			if (switchStatus instanceof ProcessStatus)
			{
				text = ((ProcessStatus) switchStatus).getStdErr();
			}
			else
			{
				text = switchStatus.getMessage();
			}
		}
		// Now show a tooltip "toast" for 3 seconds to announce success
		final Shell shell = UIUtils.getActiveShell();
		DefaultToolTip toolTip = new DefaultToolTip(shell)
		{
			@Override
			public Point getLocation(Point size, Event event)
			{
				final Rectangle workbenchWindowBounds = shell.getBounds();
				int xCoord = workbenchWindowBounds.x + workbenchWindowBounds.width - size.x - 10;
				int yCoord = workbenchWindowBounds.y + workbenchWindowBounds.height - size.y - 10;
				return new Point(xCoord, yCoord);
			}
		};
		toolTip.setHideDelay(UIUtils.DEFAULT_TOOLTIP_TIME);
		toolTip.setText(text);
		toolTip.show(new Point(0, 0));
	}
}
