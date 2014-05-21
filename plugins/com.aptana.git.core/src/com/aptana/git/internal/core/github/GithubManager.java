/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.internal.core.github;

import java.net.HttpURLConnection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.json.simple.JSONObject;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;
import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.github.IGithubManager;
import com.aptana.git.core.github.IGithubRepository;
import com.aptana.git.core.github.IGithubUser;

public class GithubManager implements IGithubManager
{

	/**
	 * Property keys in response JSON for login to Github API
	 */
	private static final String USERNAME_PROPERTY = "login"; //$NON-NLS-1$

	/**
	 * Secure Storage
	 */
	private static final String SECURE_PREF_NODE = GitPlugin.PLUGIN_ID;
	private static final String USERNAME = "username"; //$NON-NLS-1$
	private static final String PASSWORD = "password"; //$NON-NLS-1$

	private IGithubUser fUser;

	public GithubManager()
	{
		loadCredentials();
	}

	public IGithubUser getUser()
	{
		return fUser;
	}

	public IStatus login(String username, String password)
	{
		HttpURLConnection connection = null;
		try
		{
			// This API can take username or email address.
			JSONObject result = (JSONObject) getAPI(new GithubUser(username, password)).get("user"); //$NON-NLS-1$
			String actualUsername = (String) result.get(USERNAME_PROPERTY);
			this.fUser = new GithubUser(actualUsername, password);
			saveCredentials();
			return new Status(IStatus.OK, GitPlugin.PLUGIN_ID, 0, null, null);
		}
		catch (CoreException ce)
		{
			return ce.getStatus();
		}
		catch (Exception e)
		{
			return new Status(IStatus.ERROR, GitPlugin.PLUGIN_ID, -1, null, e);
		}
		finally
		{
			if (connection != null)
			{
				connection.disconnect();
			}
		}
	}

	/**
	 * Logs current user out of Github in Studio.
	 */
	public IStatus logout()
	{
		ISecurePreferences prefs = getSecurePreferences();
		try
		{
			prefs.remove(USERNAME);
			prefs.remove(PASSWORD);
			prefs.flush();
			fUser = null;
			return Status.OK_STATUS;
		}
		catch (Exception e)
		{
			return new Status(IStatus.ERROR, GitPlugin.PLUGIN_ID, -1, Messages.GithubManager_LogoutFailed, e);
		}
	}

	private void loadCredentials()
	{
		ISecurePreferences prefs = getSecurePreferences();
		try
		{
			String username = prefs.get(USERNAME, null);
			String password = prefs.get(PASSWORD, null);
			if (!StringUtil.isEmpty(username) && !StringUtil.isEmpty(password))
			{
				fUser = new GithubUser(username, password);
			}
		}
		catch (Exception e)
		{
			IdeLog.logError(GitPlugin.getDefault(), "Failed to load Github user credentials", e); //$NON-NLS-1$
		}
	}

	private void saveCredentials() throws CoreException
	{
		ISecurePreferences prefs = getSecurePreferences();
		try
		{
			prefs.put(USERNAME, fUser.getUsername(), true /* encrypt */);
			prefs.put(PASSWORD, fUser.getPassword(), true /* encrypt */);
			prefs.flush();
		}
		catch (Exception e)
		{
			throw new CoreException(new Status(IStatus.ERROR, GitPlugin.PLUGIN_ID, -1,
					Messages.GithubManager_CredentialSaveFailed, e));
		}
	}

	protected ISecurePreferences getSecurePreferences()
	{
		return SecurePreferencesFactory.getDefault().node(SECURE_PREF_NODE);
	}

	public IGithubRepository getRepo(String owner, String repoName) throws CoreException
	{
		if (fUser == null)
		{
			throw new CoreException(new Status(IStatus.ERROR, GitPlugin.PLUGIN_ID, GITHUB_LOGIN_CODE,
					Messages.GithubManager_ERR_Github_NotLoggedIn, null));
		}

		JSONObject result = (JSONObject) getAPI(fUser).get("repos/" + owner + '/' + repoName); //$NON-NLS-1$
		return new GithubRepository(result);
	}

	public IGithubRepository fork(String owner, String repoName, String destination) throws CoreException
	{
		if (fUser == null)
		{
			throw new CoreException(new Status(IStatus.ERROR, GitPlugin.PLUGIN_ID, GITHUB_LOGIN_CODE,
					Messages.GithubManager_ERR_Github_NotLoggedIn, null));
		}

		String data = null;
		if (destination != null)
		{
			data = "{organization: \"" + destination + "\"}"; //$NON-NLS-1$ //$NON-NLS-2$
		}

		JSONObject result = (JSONObject) getAPI(fUser).post("repos/" + owner + '/' + repoName + "/forks", data); //$NON-NLS-1$ //$NON-NLS-2$
		return new GithubRepository(result);
	}

	protected GithubAPI getAPI(IGithubUser user)
	{
		return new GithubAPI(user);
	}
}
