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

package com.aptana.ide.core.io.auth;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;

import com.aptana.ide.core.io.CoreIOPlugin;

/**
 * @author Max Stepanov
 *
 */
public final class AuthenticationManager implements IAuthenticationManager {

	private static final String SECURITY_NODE = "com.aptana.ide.core.io.auth"; //$NON-NLS-1$
	private static final String PROP_PASSWORD = "password"; //$NON-NLS-1$
	
	private static AuthenticationManager instance;
	
	private transient Map<String, char[]> sessionPasswords = new HashMap<String, char[]>();
		
	/**
	 * 
	 */
	private AuthenticationManager() {
	}

	public static AuthenticationManager getInstance() {
		if (instance == null) {
			instance = new AuthenticationManager();
		}
		return instance;
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.core.io.auth.IAuthenticationManager#hasPersistent(java.lang.String)
	 */
	public boolean hasPersistent(String authId) {
		ISecurePreferences preferences = getSecurePreferences();
		if (preferences.nodeExists(authId)) {
			ISecurePreferences node = preferences.node(authId);
			return Arrays.asList(node.keys()).contains(PROP_PASSWORD);
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.core.io.auth.IAuthenticationManager#getPassword(java.lang.String)
	 */
	public char[] getPassword(String authId) {
		ISecurePreferences preferences = getSecurePreferences();
		if (preferences.nodeExists(authId)) {
			try {
				ISecurePreferences node = preferences.node(authId);
				String password = node.get(PROP_PASSWORD, null);
				if (password != null) {
					return password.toCharArray();
				}
			} catch (StorageException e) {
				CoreIOPlugin.log(new Status(IStatus.WARNING, CoreIOPlugin.PLUGIN_ID, Messages.AuthenticationManager_FailedGetSecurePreference, e));
			}
		}
		if (sessionPasswords.containsKey(authId)) {
			return sessionPasswords.get(authId);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.core.io.auth.IAuthenticationManager#promptPassword(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public char[] promptPassword(String authId, String login, String title, String message) {
		IAuthenticationPrompt authPrompt = (IAuthenticationPrompt) Platform.getAdapterManager()
					.getAdapter(this, IAuthenticationPrompt.class);
		if (authPrompt == null && Platform.getAdapterManager().hasAdapter(this, IAuthenticationPrompt.class.getName())) {
			authPrompt = (IAuthenticationPrompt) Platform.getAdapterManager().loadAdapter(this, IAuthenticationPrompt.class.getName());
		}
		if (authPrompt != null) {
			if (authPrompt.promptPassword(this, authId, login, title, message)) {
				return sessionPasswords.get(authId);
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.core.io.auth.IAuthenticationManager#resetPassword(java.lang.String)
	 */
	public void resetPassword(String authId) {
		ISecurePreferences preferences = getSecurePreferences();
		if (preferences.nodeExists(authId)) {
			ISecurePreferences node = preferences.node(authId);
			node.removeNode();
		}
		sessionPasswords.remove(authId);
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.core.io.auth.IAuthenticationManager#setAuth(java.lang.String, char[], boolean)
	 */
	public void setPassword(String authId, char[] password, boolean persistent) {
		ISecurePreferences node = getSecurePreferences().node(authId);
		try {
			sessionPasswords.remove(authId);
			if (password != null) {
				if (persistent) {
					node.put(PROP_PASSWORD, String.copyValueOf(password), true);
				} else {
					node.removeNode();
				}
				sessionPasswords.put(authId, password);
			}
			node.flush();
		} catch (Exception e) {
			CoreIOPlugin.log(new Status(IStatus.WARNING, CoreIOPlugin.PLUGIN_ID, Messages.AuthenticationManager_FailedSaveSecurePreference, e));
		}
	}

	private ISecurePreferences getSecurePreferences() {
		return SecurePreferencesFactory.getDefault().node(SECURITY_NODE);
	}
}
