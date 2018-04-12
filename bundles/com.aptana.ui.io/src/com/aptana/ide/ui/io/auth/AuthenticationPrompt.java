/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ide.ui.io.auth;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;

import com.aptana.ide.core.io.auth.IAuthenticationManager;
import com.aptana.ide.core.io.auth.IAuthenticationPrompt;
import com.aptana.ui.dialogs.PasswordPromptDialog;

/**
 * @author Max Stepanov
 */
public class AuthenticationPrompt implements IAuthenticationPrompt
{

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.ide.core.io.auth.IAuthenticationPrompt#promptPassword(com.aptana.ide.core.io.auth.IAuthenticationManager
	 * , java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean promptPassword(final IAuthenticationManager authManager, final String authId, final String login,
			final String title, final String message)
	{
		final boolean[] result = new boolean[] { false };
		if (PlatformUI.isWorkbenchRunning())
		{
			PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable()
			{
				public void run()
				{
					PasswordPromptDialog dlg = new PasswordPromptDialog(PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow().getShell(), title, message);
					dlg.setLogin(login);
					dlg.setPassword(authManager.getPassword(authId));
					dlg.setSavePassword(authManager.hasPersistent(authId));
					if (dlg.open() == Window.OK)
					{
						authManager.setPassword(authId, dlg.getPassword(), dlg.getSavePassword());
						result[0] = true;
					}
				}
			});

			return result[0];
		}

		return false;
	}

	@SuppressWarnings("rawtypes")
	public static class Factory implements IAdapterFactory
	{

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
		 */
		public Object getAdapter(Object adaptableObject, Class adapterType)
		{
			if (IAuthenticationPrompt.class.equals(adapterType))
			{
				return new AuthenticationPrompt();
			}
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
		 */
		public Class[] getAdapterList()
		{
			return new Class[] { IAuthenticationPrompt.class };
		}
	}

}
