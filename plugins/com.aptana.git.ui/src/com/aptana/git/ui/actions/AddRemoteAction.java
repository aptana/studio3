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
package com.aptana.git.ui.actions;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.window.Window;

import com.aptana.git.ui.dialogs.AddRemoteDialog;

public class AddRemoteAction extends SimpleGitCommandAction
{

	@SuppressWarnings("nls")
	@Override
	protected String[] getCommand()
	{
		// Pop open a dialog like create branch!
		String name = "origin";
		String url = "";
		String username = System.getProperty("user.name"); //$NON-NLS-1$
		if (username == null || username.length() == 0)
			username = "user"; //$NON-NLS-1$
		String reponame = "repo";
		IPath wd = getSelectedRepository().workingDirectory();
		reponame = wd.lastSegment();
		if (reponame.endsWith(".git"))
			reponame = reponame.substring(0, reponame.length() - 4);
		url = MessageFormat.format("git://github.com/{0}/{1}.git", username, reponame); //$NON-NLS-1$

		AddRemoteDialog dialog = new AddRemoteDialog(getTargetPart().getSite().getShell(), name, url);
		if (dialog.open() == Window.OK)
		{
			name = dialog.getValue().trim();
			url = dialog.getRemoteURL();
			return new String[] { "remote", "add", name, url };
		}
		return null; // don't let this action run...
	}

	@Override
	protected void postLaunch()
	{
		// do nothing
	}

}
