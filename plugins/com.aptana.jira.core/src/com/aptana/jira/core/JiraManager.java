/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.jira.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Map;
import java.util.regex.Matcher;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.BasicHttpContext;
import org.eclipse.core.internal.preferences.Base64;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.osgi.framework.Version;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.IOUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.jetty.util.epl.ajax.JSON;
import com.aptana.jira.core.internal.JiraProjectsRegistry;
import com.aptana.jira.core.internal.JiraProjectsRegistry.JiraProjectInfo;

/**
 * @author cwilliams
 * @author Michael Xia (mxia@appcelerator.com)
 */
public class JiraManager
{

	/**
	 * Authorization header name. Used for Basic Auth over HTTP/S.
	 */
	private static final String AUTHORIZATION_HEADER = "Authorization"; //$NON-NLS-1$

	/**
	 * User agent header name. Used to identify studio as the "client".
	 */
	private static final String USER_AGENT = "User-Agent"; //$NON-NLS-1$

	/**
	 * Content Type Accept header. Tells JIRA we can accept given content types. We default to JSON.
	 */
	private static final String ACCEPT_HEADER = "Accept"; //$NON-NLS-1$
	private static final String ACCEPT_CONTENT_TYPES = "application/json"; //$NON-NLS-1$
	private static final String CONTENT_TYPE = "Content-Type"; //$NON-NLS-1$

	/**
	 * Secure Storage
	 */
	private static final String SECURE_PREF_NODE = "com.aptana.jira.core"; //$NON-NLS-1$
	private static final String USERNAME = "username"; //$NON-NLS-1$
	private static final String PASSWORD = "password"; //$NON-NLS-1$

	/**
	 * Project Keys for Aptana and Titanium
	 */
	static final String APTANA_STUDIO = "APSTUD"; //$NON-NLS-1$
	static final String TITANIUM_COMMUNITY = "AC"; //$NON-NLS-1$

	/**
	 * Ticket field names.
	 */
	private static final String PARAM_ENVIRONMENT = "environment"; //$NON-NLS-1$
	private static final String PARAM_VERSION = "versions"; //$NON-NLS-1$

	// could override using setProjectInfo()
	private static String projectName = "Aptana Studio"; //$NON-NLS-1$
	private static String projectKey = APTANA_STUDIO;

	/**
	 * REST API URLs
	 */
	private static final String HOST_NAME = "jira.appcelerator.org"; //$NON-NLS-1$
	private static final String HOST_URL = "https://" + HOST_NAME; //$NON-NLS-1$
	private static final String REST_API_ENDPOINT = HOST_URL + "/rest/api/2/"; //$NON-NLS-1$

	private JiraUser user;

	JiraManager()
	{
		loadCredentials();
		loadProjectInfo();
	}

	private void loadProjectInfo()
	{
		JiraProjectsRegistry projectsRegistry = getJiraProjectsRegistry();
		JiraProjectInfo projectProvider = projectsRegistry.getProjectInfo();
		if (projectProvider != null)
		{
			setProjectInfo(projectProvider.getProjectName(), projectProvider.getProjectCode());
		}
	}

	protected JiraProjectsRegistry getJiraProjectsRegistry()
	{
		return new JiraProjectsRegistry();
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
	 * @return {@link IStatus} indicating the result. OK represents successful login. Otherwise the status code holds
	 *         the HTTP response code, and the body may hold the response body
	 */
	public IStatus login(String username, String password)
	{
		HttpURLConnection connection = null;
		try
		{
			connection = createConnection(getUserURL(username), username, password);
			int code = connection.getResponseCode();
			if (code == HttpURLConnection.HTTP_OK)
			{
				this.user = new JiraUser(username, password);
				saveCredentials();
				return Status.OK_STATUS;
			}

			if (code == HttpURLConnection.HTTP_UNAUTHORIZED || code == HttpURLConnection.HTTP_FORBIDDEN)
			{
				return new Status(IStatus.ERROR, JiraCorePlugin.PLUGIN_ID, code,
						Messages.JiraManager_BadCredentialsErrMsg, null);
			}
			String msg = IOUtil.read(connection.getInputStream());
			return new Status(IStatus.ERROR, JiraCorePlugin.PLUGIN_ID, code,
					Messages.JiraManager_UnknownErrMsg + ": " + msg, null);
		}
		catch (Exception e)
		{
			return new Status(IStatus.ERROR, JiraCorePlugin.PLUGIN_ID, e.getMessage(), e);
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
	 * The URL to get details on a user.
	 *
	 * @param username
	 * @return
	 */
	protected String getUserURL(String username)
	{
		return REST_API_ENDPOINT + "user?username=" + username; //$NON-NLS-1$
	}

	@SuppressWarnings("restriction")
	protected HttpURLConnection createConnection(String urlString, String username, String password)
			throws MalformedURLException, IOException
	{
		HttpURLConnection connection;
		URL url = new URL(urlString);
		connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty(USER_AGENT, getProjectVersion());
		connection.setRequestProperty(ACCEPT_HEADER, ACCEPT_CONTENT_TYPES);
		connection.setRequestProperty(CONTENT_TYPE, ACCEPT_CONTENT_TYPES);
		connection.setUseCaches(false);
		connection.setAllowUserInteraction(false);
		String usernamePassword = username + ":" + password; //$NON-NLS-1$
		connection.setRequestProperty(AUTHORIZATION_HEADER,
				"Basic " + new String(Base64.encode(usernamePassword.getBytes()))); //$NON-NLS-1$
		return connection;
	}

	/**
	 * Logs current user out of JIRA in Studio.
	 */
	public void logout()
	{
		ISecurePreferences prefs = getSecurePreferences();
		try
		{
			prefs.remove(USERNAME);
			prefs.remove(PASSWORD);
			prefs.flush();
			user = null;
		}
		catch (Exception e)
		{
			IdeLog.logError(JiraCorePlugin.getDefault(), "Failed to log out Jira user", e); //$NON-NLS-1$
		}
	}

	/**
	 * Creates a JIRA ticket.
	 *
	 * @param type
	 *            the issue type (bug, feature, or improvement)
	 * @param priority
	 *            the issue's priority
	 * @param severity
	 *            the issue's severity (Blocker, Major, Minor, Trivial, None)
	 * @param summary
	 *            the summary of the ticket
	 * @param description
	 *            the description of the ticket
	 * @return the JIRA issue created
	 * @throws JiraException
	 * @throws IOException
	 */
	public JiraIssue createIssue(JiraIssueType type, JiraIssueSeverity severity, String summary, String description)
			throws JiraException, IOException
	{
		if (user == null)
		{
			throw new JiraException(Messages.JiraManager_ERR_NotLoggedIn);
		}

		HttpURLConnection connection = null;
		try
		{
			connection = createConnection(getCreateIssueURL(), user.getUsername(), user.getPassword());
			connection.setRequestMethod("POST"); //$NON-NLS-1$
			connection.setDoOutput(true);
			String severityJSON;
			String versionString;
			if ((TITANIUM_COMMUNITY.equals(projectKey) && type == JiraIssueType.IMPROVEMENT)
					|| (!TITANIUM_COMMUNITY.equals(projectKey) && type != JiraIssueType.BUG))
			{
				// Improvements in TC don't get Severities, nor do Story or Improvements in Aptana Studio!
				severityJSON = StringUtil.EMPTY;
			}
			else
			{
				severityJSON = severity.getParameterValue() + ",\n"; //$NON-NLS-1$
			}

			// If we're submitting against TC, we can't do version, we need to stuff that into the Environment
			if (TITANIUM_COMMUNITY.equals(projectKey))
			{
				versionString = MessageFormat.format("\"{0}\": \"{1}\"", PARAM_ENVIRONMENT, getProjectVersion()); //$NON-NLS-1$
			}
			else
			{
				versionString = "\"" + PARAM_VERSION + "\": [{\"name\": \"" + getProjectVersion() + "\"}]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			// @formatter:off
			String data = "{\n" + //$NON-NLS-1$
			"    \"fields\": {\n" + //$NON-NLS-1$
			"       \"project\":\n" + //$NON-NLS-1$
			"       { \n" + //$NON-NLS-1$
			"          \"key\": \"" + projectKey + "\"\n" + //$NON-NLS-1$ //$NON-NLS-2$
			"       },\n" + //$NON-NLS-1$
			"       \"summary\": \"" + summary.replaceAll("\"", "'") + "\",\n" + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			"       \"description\": \"" + description.replaceAll("\"", "'").replaceAll("\n", Matcher.quoteReplacement("\\n")) + "\",\n" + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			"       \"issuetype\": {\n" + //$NON-NLS-1$
			"          \"name\": \"" + type.getParameterValue(projectKey) + "\"\n" + //$NON-NLS-1$ //$NON-NLS-2$
			"       },\n" + //$NON-NLS-1$
			severityJSON +
			"       " + versionString + "\n" + //$NON-NLS-1$ //$NON-NLS-2$
			"   }\n" + //$NON-NLS-1$
			"}"; //$NON-NLS-1$
			// @formatter:on

			OutputStream out = connection.getOutputStream();
			IOUtil.write(out, data);
			out.close();

			int responseCode = connection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED)
			{
				String output = IOUtil.read(connection.getInputStream());
				return createIssueFromJSON(output);
			}
			// failed to create the ticket
			// TODO Parse the response as JSON!
			throw new JiraException(IOUtil.read(connection.getErrorStream()));
		}
		catch (JiraException je)
		{
			throw je;
		}
		catch (Exception e)
		{
			throw new JiraException(e.getMessage(), e);
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
	 * @param output
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected JiraIssue createIssueFromJSON(String output)
	{
		Map<String, Object> map = (Map<String, Object>) JSON.parse(output);
		String issueKey = (String) map.get("key"); //$NON-NLS-1$
		String issueId = (String) map.get("id"); //$NON-NLS-1$
		String issueUrl = HOST_URL + "/browse/" + issueKey; //$NON-NLS-1$
		return new JiraIssue(issueKey, issueId, issueUrl);
	}

	protected String getCreateIssueURL()
	{
		return REST_API_ENDPOINT + "issue"; //$NON-NLS-1$
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

		// Use Apache HTTPClient to POST the file
		AbstractHttpClient httpclient = createClient();

		// Set up pre-emptive basic auth
		AuthCache authCache = new BasicAuthCache();
		BasicScheme basicAuth = new BasicScheme();
		HttpHost targetHost = new HttpHost(HOST_NAME, 443, "https"); //$NON-NLS-1$
		authCache.put(targetHost, basicAuth);
		BasicHttpContext localcontext = new BasicHttpContext();
		localcontext.setAttribute(ClientContext.AUTH_CACHE, authCache);

		HttpPost filePost = null;
		try
		{
			filePost = new HttpPost(createAttachmentURL(issue));
			File file = path.toFile();

			MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			// MUST USE "file" AS THE NAME!!!
			reqEntity.addPart("file", new FileBody(file)); //$NON-NLS-1$
			filePost.setEntity(reqEntity);
			// Special header to tell JIRA not to do XSFR checking
			filePost.addHeader(new BasicHeader("X-Atlassian-Token", "nocheck")); //$NON-NLS-1$ //$NON-NLS-2$

			HttpResponse response = post(httpclient, targetHost, filePost, localcontext);
			StatusLine sl = response.getStatusLine();
			int responseCode = sl.getStatusCode();
			HttpEntity respEntity = response.getEntity();
			InputStream in = respEntity.getContent();
			if (responseCode != HttpURLConnection.HTTP_OK && responseCode != HttpURLConnection.HTTP_CREATED)
			{
				// TODO This is a JSON response that we should parse out "errorMessages" value(s) (its an array of
				// strings).
				throw new JiraException(IOUtil.read(in));
			}
			String json = IOUtil.read(in);
			IdeLog.logInfo(JiraCorePlugin.getDefault(), json);
		}
		catch (JiraException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new JiraException(e.getMessage(), e);
		}
		finally
		{
			closeConnection(httpclient);
		}
	}

	protected void closeConnection(AbstractHttpClient httpclient)
	{
		if (httpclient == null)
		{
			return;
		}
		httpclient.getConnectionManager().shutdown();
	}

	protected HttpResponse post(AbstractHttpClient httpclient, HttpHost targetHost, HttpPost filePost,
			BasicHttpContext localcontext) throws ClientProtocolException, IOException
	{
		return httpclient.execute(targetHost, filePost, localcontext);
	}

	protected AbstractHttpClient createClient()
	{
		DefaultHttpClient httpclient = new DefaultHttpClient();
		UsernamePasswordCredentials creds = new UsernamePasswordCredentials(user.getUsername(), user.getPassword());
		httpclient.getCredentialsProvider().setCredentials(new AuthScope(HOST_NAME, 443), creds);
		httpclient.getParams().setBooleanParameter(ClientPNames.HANDLE_AUTHENTICATION, true);

		return httpclient;
	}

	protected String createAttachmentURL(JiraIssue issue)
	{
		return REST_API_ENDPOINT + "issue/" + issue.getName() + "/attachments"; //$NON-NLS-1$ //$NON-NLS-2$
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
		catch (Exception e)
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

	protected String getProjectVersion()
	{
		String versionStr = EclipseUtil.getStudioVersion();
		// we don't need the qualifier
		Version version = new Version(versionStr);
		return MessageFormat.format("{0} {1}.{2}.{3}", projectName, version.getMajor(), version.getMinor(), //$NON-NLS-1$
				version.getMicro());
	}

	protected ISecurePreferences getSecurePreferences()
	{
		return SecurePreferencesFactory.getDefault().node(SECURE_PREF_NODE);
	}
}
