/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.jira.core;

import java.net.URL;
import java.text.MessageFormat;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;
import org.osgi.framework.Version;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;
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
	private static final String PARAM_PROJECT = "--project"; //$NON-NLS-1$
	private static final String PARAM_VERSION = "--affectsVersions"; //$NON-NLS-1$
	private static final String PARAM_TYPE = "--type"; //$NON-NLS-1$
	private static final String PARAM_PRIORITY = "--priority"; //$NON-NLS-1$
	private static final String PARAM_SUMMARY = "--summary"; //$NON-NLS-1$
	private static final String PARAM_DESCRIPTION = "--description"; //$NON-NLS-1$
	private static final String PARAM_ISSUE = "--issue"; //$NON-NLS-1$
	private static final String PARAM_FILE = "--file"; //$NON-NLS-1$
	private static final String ACTION_LOGIN = "login"; //$NON-NLS-1$
	private static final String ACTION_CREATE_ISSUE = "createIssue"; //$NON-NLS-1$
	private static final String ACTION_ADD_ATTACHMENT = "addAttachment"; //$NON-NLS-1$

	private static final String SECURE_PREF_NODE = "com.aptana.jira.core"; //$NON-NLS-1$
	private static final String USERNAME = "username"; //$NON-NLS-1$
	private static final String PASSWORD = "password"; //$NON-NLS-1$

	private static final Pattern PATTERN_SUCCESS = Pattern.compile("(.*) created with id (.*). URL: (.*)"); //$NON-NLS-1$
	private static final String FAILED_REASON_START = "Exception: "; //$NON-NLS-1$

	private static IPath jiraExecutable;
	// could override using setProjectInfo()
	private static String projectName = "Aptana Studio"; //$NON-NLS-1$
	private static String projectKey = "APSTUD"; //$NON-NLS-1$

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
		if (jiraExecutable == null)
		{
			throw new JiraException(Messages.JiraManager_ERR_NoJiraExecutable);
		}
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
				int index = output.lastIndexOf(FAILED_REASON_START);
				if (index > -1)
				{
					String reason = output.substring(index + FAILED_REASON_START.length()).trim();
					throw new JiraException(reason);
				}
				else
				{
					throw new JiraException(output);
				}
			}
		}
	}

	/**
	 * Creates a JIRA ticket.
	 * 
	 * @param type
	 *            the issue type (bug, feature, or improvement)
	 * @param priority
	 *            the issue's priority
	 * @param summary
	 *            the summary of the ticket
	 * @param description
	 *            the description of the ticket
	 * @return the JIRA issue created
	 * @throws JiraException
	 */
	public JiraIssue createIssue(JiraIssueType type, JiraIssuePriority priority, String summary, String description)
			throws JiraException
	{
		if (user == null)
		{
			throw new JiraException(Messages.JiraManager_ERR_NotLoggedIn);
		}
		IPath jiraExecutable = getJiraExecutable();

		String output = ProcessUtil.outputForCommand(jiraExecutable.toOSString(), jiraExecutable.removeLastSegments(1),
				PARAM_ACTION, ACTION_CREATE_ISSUE, PARAM_USERNAME, user.getUsername(), PARAM_PASSWORD,
				user.getPassword(), PARAM_PROJECT, projectKey, PARAM_VERSION, getProjectVersion(), PARAM_TYPE,
				type.getParameterName(), PARAM_PRIORITY, priority.toString(), PARAM_SUMMARY, summary,
				PARAM_DESCRIPTION, description);
		Matcher m = PATTERN_SUCCESS.matcher(output);
		if (m.find())
		{
			String issueName = m.group(1);
			String issueId = m.group(2);
			String issueUrl = m.group(3);
			return new JiraIssue(issueName, issueId, issueUrl);
		}
		// failed to create the ticket
		throw new JiraException(output);
	}

	/**
	 * Adds an attachment to a JIRA ticket.
	 * 
	 * @param path
	 *            the path of the file to be attached
	 * @param issue
	 *            the JIRA ticket
	 * @throws JiraException
	 */
	public void addAttachment(IPath path, JiraIssue issue) throws JiraException
	{
		if (path == null || issue == null)
		{
			return;
		}
		if (user == null)
		{
			throw new JiraException(Messages.JiraManager_ERR_NotLoggedIn);
		}
		IPath jiraExecutable = getJiraExecutable();

		String output = ProcessUtil.outputForCommand(jiraExecutable.toOSString(), jiraExecutable.removeLastSegments(1),
				PARAM_ACTION, ACTION_ADD_ATTACHMENT, PARAM_USERNAME, user.getUsername(), PARAM_PASSWORD,
				user.getPassword(), PARAM_ISSUE, issue.getId(), PARAM_FILE, path.toOSString());
		if (!StringUtil.isEmpty(output) && output.indexOf("added to") < 0) //$NON-NLS-1$
		{
			throw new JiraException(output);
		}
	}

	/**
	 * Specifies the JIRA project to log the ticket against. By default Aptana Studio will be used.
	 * 
	 * @param projectName
	 *            the name of the JIRA project (e.g. Aptana Studio)
	 * @param projectKey
	 *            the key of the JIRA project (e.g. APSTUD for Aptana Studio)
	 */
	public static void setProjectInfo(String projectName, String projectKey)
	{
		if (!StringUtil.isEmpty(projectName))
		{
			JiraManager.projectName = projectName;
		}
		if (!StringUtil.isEmpty(projectKey))
		{
			JiraManager.projectKey = projectKey;
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
					url = FileLocator.toFileURL(url);
					jiraExecutable = Path.fromOSString(url.getFile());
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

	private static String getProjectVersion()
	{
		String versionStr = EclipseUtil.getStudioVersion();
		// we don't need the qualifier
		Version version = new Version(versionStr);
		return MessageFormat.format("{0} {1}.{2}.{3}", projectName, version.getMajor(), version.getMinor(), //$NON-NLS-1$
				version.getMicro());
	}

	private static ISecurePreferences getSecurePreferences()
	{
		return SecurePreferencesFactory.getDefault().node(SECURE_PREF_NODE);
	}
}
