package com.aptana.ui.commands;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;

import com.aptana.ui.UIPlugin;

public class OpenFileManagerContributionItem extends CompoundContributionItem
{

	public OpenFileManagerContributionItem()
	{
		super();
	}

	public OpenFileManagerContributionItem(String id)
	{
		super(id);
	}

	@Override
	protected IContributionItem[] getContributionItems()
	{
		CommandContributionItemParameter ccip = new CommandContributionItemParameter(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow(), null, "com.aptana.ui.command.ShowInFileManager", //$NON-NLS-1$
				CommandContributionItem.STYLE_PUSH);
		if (Platform.getOS().equals(Platform.OS_MACOSX))
		{
			ccip.label = Messages.OpenFileManagerContributionItem_FinderLabel;
			ccip.icon = UIPlugin.getImageDescriptor("icons/finder.png"); //$NON-NLS-1$
		}
		else if (Platform.getOS().equals(Platform.OS_WIN32))
		{
			ccip.label = Messages.OpenFileManagerContributionItem_WindowsExplorerLabel;
			ccip.icon = UIPlugin.getImageDescriptor("icons/windows_explorer.png"); //$NON-NLS-1$
		}
		else
		{
			ccip.label = Messages.OpenFileManagerContributionItem_FileBrowserLabel;
		}
		return new IContributionItem[] { new CommandContributionItem(ccip) };
	}

}
