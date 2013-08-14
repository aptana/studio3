/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.internal.core.github;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;

import org.eclipse.core.internal.preferences.Base64;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.osgi.framework.Version;

import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.IOUtil;
import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.github.IGithubUser;

@SuppressWarnings("restriction")
class GithubAPI
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

	private static final String ATTR_MESSAGE = "message"; //$NON-NLS-1$

	private final String username;
	private final String password;

	GithubAPI(String username, String password)
	{
		this.username = username;
		this.password = password;
	}

	GithubAPI(IGithubUser user)
	{
		this(user.getUsername(), user.getPassword());
	}

	/**
	 * Helper method that issues a GET against the Github API.
	 * 
	 * @param url
	 * @return
	 * @throws CoreException
	 */
	protected Object get(String url) throws CoreException
	{
		checkCredentials();
		HttpURLConnection connection = null;
		try
		{
			connection = createConnection(BASE_URL + url);
			int code = connection.getResponseCode();
			JSONParser parser = new JSONParser();
			if (code == HttpURLConnection.HTTP_OK)
			{
				String response = IOUtil.read(connection.getInputStream());
				return parser.parse(response);
			}
			// read error message from response
			String response = IOUtil.read(connection.getErrorStream());
			JSONObject result = (JSONObject) parser.parse(response);
			String msg = (String) result.get(ATTR_MESSAGE);
			throw new CoreException(new Status(IStatus.ERROR, GitPlugin.PLUGIN_ID, code, msg, null));
		}
		catch (CoreException ce)
		{
			throw ce;
		}
		catch (Exception e)
		{
			throw new CoreException(new Status(IStatus.ERROR, GitPlugin.PLUGIN_ID, -1, null, e));
		}
		finally
		{
			if (connection != null)
			{
				connection.disconnect();
			}
		}
	}

	private void checkCredentials() throws CoreException
	{
		if (username == null || password == null)
		{
			throw new CoreException(new Status(IStatus.ERROR, GitPlugin.PLUGIN_ID, -1,
					Messages.GithubManager_ERR_Github_NotLoggedIn, null));
		}
	}

	protected Object post(String url, String data) throws CoreException
	{
		checkCredentials();
		HttpURLConnection connection = null;
		try
		{
			connection = createConnection(BASE_URL + url);
			connection.setRequestMethod("POST"); //$NON-NLS-1$
			connection.setDoInput(true);
			connection.setDoOutput(true);

			OutputStream out = connection.getOutputStream();
			IOUtil.write(out, data);
			out.close();

			int code = connection.getResponseCode();
			JSONParser parser = new JSONParser();
			// Returns 201 created for quick ops, 202 for async ops like forking
			if (code == HttpURLConnection.HTTP_CREATED || code == HttpURLConnection.HTTP_ACCEPTED)
			{
				String response = IOUtil.read(connection.getInputStream());
				return parser.parse(response);
			}
			// read error message from response
			String response = IOUtil.read(connection.getErrorStream());
			JSONObject result = (JSONObject) parser.parse(response);
			String msg = (String) result.get(ATTR_MESSAGE);
			throw new CoreException(new Status(IStatus.ERROR, GitPlugin.PLUGIN_ID, code, msg, null));
		}
		catch (CoreException ce)
		{
			throw ce;
		}
		catch (Exception e)
		{
			throw new CoreException(new Status(IStatus.ERROR, GitPlugin.PLUGIN_ID, -1, null, e));
		}
		finally
		{
			if (connection != null)
			{
				connection.disconnect();
			}
		}
	}

	private HttpURLConnection createConnection(String urlString) throws MalformedURLException, IOException
	{
		URL url = new URL(urlString);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
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
}
