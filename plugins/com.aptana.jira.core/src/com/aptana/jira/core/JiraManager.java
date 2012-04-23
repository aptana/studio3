/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.jira.core;

import java.io.File;
import java.net.URL;
import java.text.MessageFormat;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.ProcessUtil;
import com.aptana.core.util.StringUtil;

/**
 * @author Michael Xia (mxia@appcelerator.com)
 */
public class JiraManager
{

	private static final String LIBRARY_PLUGIN_ID = "org.swift.jira.cli"; //$NON-NLS-1$
	private static final String JIRA_WIN = "jira.bat"; //$NON-NLS-1$
	private static final String JIRA_UNIX = "jira.sh"; //$NON-NLS-1$

	private static final String PARAM_ACTION = "-a"; //$NON-NLS-1$
	private static final String PARAM_USERNAME = "-u"; //$NON-NLS-1$
	private static final String PARAM_PASSWORD = "-p"; //$NON-NLS-1$
	private static final String ACTION_LOGIN = "login"; //$NON-NLS-1$

	private static final String SECURE_PREF_NODE = "com.aptana.jira.core"; //$NON-NLS-1$
	private static final String USERNAME = "username"; //$NON-NLS-1$
	private static final String PASSWORD = "password"; //$NON-NLS-1$

	private static IPath jiraExecutable;

	private JiraUser user;

	JiraManager()
	{
		loadCredentials();
	}

	/**
	 * @return the currently JIRA user, or null if there has not been a successful login attempt
	 */
	public JiraUser getUser()
	{
		return user;
	}

	/**
	 * Logs into JIRA with a username and password.
	 * 
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 * @throws JiraException
	 */
	public void login(String username, String password) throws JiraException
	{
		IPath jiraExecutable = getJiraExecutable();
		String output = ProcessUtil.outputForCommand(jiraExecutable.toOSString(), jiraExecutable.removeLastSegments(1),
				PARAM_ACTION, ACTION_LOGIN, PARAM_USERNAME, username, PARAM_PASSWORD, password);
		if (!StringUtil.isEmpty(output))
		{
			output = output.trim();
			StringTokenizer tk = new StringTokenizer(output);
			if (tk.countTokens() == 1)
			{
				// a valid token is returned; the login is successful
				user = new JiraUser(username, password);
				// save the credentials
				saveCredentials();
			}
			else
			{
				// an error
				throw new JiraException(output);
			}
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
				user = new JiraUser(username, password);
			}
		}
		catch (StorageException e)
		{
			IdeLog.logError(JiraCorePlugin.getDefault(), "Failed to load Jira user credentials", e); //$NON-NLS-1$
		}
	}

	private void saveCredentials()
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
			IdeLog.logError(JiraCorePlugin.getDefault(), "Failed to save Jira user credentials", e); //$NON-NLS-1$
		}
	}

	private static IPath getJiraExecutable()
	{
		if (jiraExecutable == null)
		{
			File file;
			IPath path;
			try
			{
				if (Platform.OS_WIN32.equals(Platform.getOS()))
				{
					path = Path.fromOSString(JIRA_WIN);
				}
				else
				{
					path = Path.fromOSString(JIRA_UNIX);
				}
				URL url = FileLocator.find(Platform.getBundle(LIBRARY_PLUGIN_ID), path, null);
				if (url != null)
				{
					file = URIUtil.toFile(FileLocator.toFileURL(url).toURI());
					jiraExecutable = Path.fromOSString(file.getAbsolutePath());
				}
			}
			catch (Exception e)
			{
				IdeLog.logError(JiraCorePlugin.getDefault(),
						MessageFormat.format("Failed to find plugin {0}", LIBRARY_PLUGIN_ID), e); //$NON-NLS-1$
			}
		}
		return jiraExecutable;
	}

	private static ISecurePreferences getSecurePreferences()
	{
		return SecurePreferencesFactory.getDefault().node(SECURE_PREF_NODE);
	}
}
