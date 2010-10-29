package com.aptana.git.ui.internal.actions;

import java.text.MessageFormat;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.aptana.git.core.model.GitRepository;
import com.aptana.git.ui.internal.dialogs.BranchDialog;

public class SwitchBranchHandler extends AbstractGitHandler
{

	private static final int TOOLTIP_LIFETIME = 3000;

	@Override
	protected Object doExecute(ExecutionEvent event) throws ExecutionException
	{
		final GitRepository repo = getSelectedRepository();
		if (repo == null)
		{
			return null;
		}
		BranchDialog dialog = new BranchDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), repo, true,
				false);
		if (dialog.open() == Window.OK)
		{
			switchBranch(repo, dialog.getBranch());
		}
		return null;
	}

	public static void switchBranch(final GitRepository repo, final String branchName)
	{
		if (!repo.switchBranch(branchName))
			return;
		// Now show a tooltip "toast" for 3 seconds to announce success
		final Shell shell = Display.getDefault().getActiveShell();
		String text = MessageFormat.format(Messages.SwitchBranchAction_BranchSwitch_Msg, branchName);
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
		toolTip.setHideDelay(TOOLTIP_LIFETIME);
		toolTip.setText(text);
		toolTip.show(new Point(0, 0));
	}
}
