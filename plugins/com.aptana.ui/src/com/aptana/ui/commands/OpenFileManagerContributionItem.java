/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
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
