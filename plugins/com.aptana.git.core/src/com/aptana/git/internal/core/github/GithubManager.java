/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.internal.core.github;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;

import org.eclipse.core.internal.preferences.Base64;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.osgi.framework.Version;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.IOUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.github.IGithubManager;
import com.aptana.git.core.github.IGithubUser;

public class GithubManager implements IGithubManager
{
	// TODO Add support to get the list of orgs for a user
	// http://developer.github.com/v3/orgs/
	// TODO Add support to get the list of repos for the user:
	// http://developer.github.com/v3/repos/

	/**
	 * Authorization header name. Used for Basic Auth over HTTP/S.
	 */
	private static final String AUTHORIZATION_HEADER = "Authorization"; //$NON-NLS-1$

	/**
	 * User agent header name. Used to identify studio as the "client".
	 */
	private static final String USER_AGENT = "User-Agent"; //$NON-NLS-1$

	/**
	 * Content Type Accept header. Tells Github we can accept given content types. We default to JSON.
	 */
	private static final String ACCEPT_HEADER = "Accept"; //$NON-NLS-1$
	private static final String ACCEPT_CONTENT_TYPES = "application/json"; //$NON-NLS-1$
	private static final String CONTENT_TYPE = "Content-Type"; //$NON-NLS-1$

	private static final String BASE_URL = "https://api.github.com/"; //$NON-NLS-1$

	/**
	 * Secure Storage
	 */
	private static final String SECURE_PREF_NODE = GitPlugin.PLUGIN_ID;
	private static final String USERNAME = "username"; //$NON-NLS-1$
	private static final String PASSWORD = "password"; //$NON-NLS-1$

	private IGithubUser user;

	public GithubManager()
	{
		loadCredentials();
	}

	public IGithubUser getUser()
	{
		return user;
	}

	public IStatus login(String username, String password)
	{
		HttpURLConnection connection = null;
		try
		{
			// This API can take username or email address.
			connection = createConnection(BASE_URL + "user", username, password); //$NON-NLS-1$
			int code = connection.getResponseCode();
			String response;
			if (code == HttpURLConnection.HTTP_OK)
			{
				// If user entered an email address we can't use that in building URLs. Let's parse the response and
				// grab the actual username
				response = IOUtil.read(connection.getInputStream());
				JSONParser parser = new JSONParser();
				JSONObject result = (JSONObject) parser.parse(response);
				String actualUsername = (String) result.get("login"); //$NON-NLS-1$
				this.user = new GithubUser(actualUsername, password);
				saveCredentials();
				return new Status(IStatus.OK, GitPlugin.PLUGIN_ID, code, null, null);
			}
			// read error message from response
			response = IOUtil.read(connection.getErrorStream());
			JSONParser parser = new JSONParser();
			JSONObject result = (JSONObject) parser.parse(response);
			String msg = (String) result.get("message"); //$NON-NLS-1$
			return new Status(IStatus.ERROR, GitPlugin.PLUGIN_ID, code, msg, null);
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

	@SuppressWarnings("restriction")
	protected HttpURLConnection createConnection(String urlString, String username, String password)
			throws MalformedURLException, IOException
	{
		HttpURLConnection connection;
		URL url = new URL(urlString);
		connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty(USER_AGENT, getUserAgentString());
		connection.setRequestProperty(ACCEPT_HEADER, ACCEPT_CONTENT_TYPES);
		connection.setRequestProperty(CONTENT_TYPE, ACCEPT_CONTENT_TYPES);
		connection.setUseCaches(false);
		connection.setAllowUserInteraction(false);
		String usernamePassword = username + ":" + password; //$NON-NLS-1$
		connection.setRequestProperty(AUTHORIZATION_HEADER,
				"Basic " + new String(Base64.encode(usernamePassword.getBytes()))); //$NON-NLS-1$
		return connection;
	}

	private String getUserAgentString()
	{
		String versionStr = EclipseUtil.getStudioVersion();
		// we don't need the qualifier
		Version version = new Version(versionStr);
		return MessageFormat.format(
				"{0} {1}.{2}.{3}", EclipseUtil.getProductName(), version.getMajor(), version.getMinor(), //$NON-NLS-1$
				version.getMicro());
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
			user = null;
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
				user = new GithubUser(username, password);
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
			prefs.put(USERNAME, user.getUsername(), true /* encrypt */);
			prefs.put(PASSWORD, user.getPassword(), true /* encrypt */);
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

}
