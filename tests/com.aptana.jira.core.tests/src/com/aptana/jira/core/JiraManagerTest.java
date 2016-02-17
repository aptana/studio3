package com.aptana.jira.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aptana.core.util.FileUtil;
import com.aptana.jetty.util.epl.ajax.JSON;
import com.aptana.jira.core.internal.JiraProjectsRegistry;
import com.aptana.jira.core.internal.JiraProjectsRegistry.JiraProjectInfo;

public class JiraManagerTest {

	private static final String TEST_USER = "studio-test";
	private static final String TEST_PASSWORD = "studio";

	private JiraManager manager;
	private Mockery context;
	private HttpURLConnection connection;
	private ISecurePreferences securePrefs;
	private JiraProjectsRegistry registry;
	private DefaultHttpClient client;
	private HttpResponse response;
	private HttpPost fFilePost;

	@Before
	public void setUp() throws Exception {

		context = new Mockery() {
			{
				setImposteriser(ClassImposteriser.INSTANCE);
			}
		};
		connection = context.mock(HttpURLConnection.class);
		securePrefs = context.mock(ISecurePreferences.class);
		registry = context.mock(JiraProjectsRegistry.class);
		client = context.mock(DefaultHttpClient.class);
		response = context.mock(HttpResponse.class);
		context.checking(new Expectations() {
			{
				// Tries to load credentials
				oneOf(securePrefs).get("username", null);
				will(returnValue(null));
				oneOf(securePrefs).get("password", null);
				will(returnValue(null));

				oneOf(registry).getProjectInfo();
				will(returnValue(new JiraProjectInfo("Aptana Studio", "APSTUD")));
			}
		});

		manager = new JiraManager() {
			@Override
			protected HttpURLConnection createConnection(String urlString,
					String username, String password)
					throws MalformedURLException, IOException {
				return connection;
			}

			@Override
			protected ISecurePreferences getSecurePreferences() {
				return securePrefs;
			}

			@Override
			protected JiraProjectsRegistry getJiraProjectsRegistry() {
				return registry;
			}

			@Override
			protected String getProjectVersion() {
				return "1.2.3";
			}

			@Override
			protected AbstractHttpClient createClient() {
				return client;
			}

			@Override
			protected HttpResponse post(AbstractHttpClient httpclient,
					HttpHost targetHost, HttpPost filePost,
					BasicHttpContext localcontext)
					throws ClientProtocolException, IOException {
				fFilePost = filePost;
				return response;
			}

			@Override
			protected void closeConnection(AbstractHttpClient httpclient) {

			}

			@Override
			protected boolean isProjectVersionExists(String projectVersion) {
				return true;
			}
		};
	}

	@After
	public void tearDown() throws Exception {
		manager = null;
		connection = null;
		context = null;
		securePrefs = null;
		registry = null;
		client = null;
		fFilePost = null;
	}

	@Test
	public void testLogin() throws Exception {
		context.checking(new Expectations() {
			{
				oneOf(connection).getResponseCode();
				will(returnValue(200));

				oneOf(connection).disconnect();

				// Tries to save credentials
				oneOf(securePrefs).put("username", TEST_USER, true);
				oneOf(securePrefs).put("password", TEST_PASSWORD, true);
				oneOf(securePrefs).flush();
			}
		});
		assertNull(manager.getUser());
		IStatus status = manager.login(TEST_USER, TEST_PASSWORD);
		assertTrue(status.isOK());
		JiraUser user = manager.getUser();
		assertNotNull(user);
		assertEquals(TEST_USER, user.getUsername());
		assertEquals(TEST_PASSWORD, user.getPassword());
		context.assertIsSatisfied();
	}

	@Test
	public void testLoginWithInvalidUser() throws Exception {
		context.checking(new Expectations() {
			{
				oneOf(connection).getResponseCode();
				will(returnValue(401));

				oneOf(connection).disconnect();

				// Never tries to save credentials
				never(securePrefs).put("username", TEST_USER, true);
				never(securePrefs).put("password", TEST_PASSWORD, true);
				never(securePrefs).flush();
			}
		});
		IStatus status = manager.login("invalid_user", TEST_PASSWORD);
		assertFalse("User is able to log into JIRA with an invalid username",
				status.isOK());
		context.assertIsSatisfied();
	}

	@Test
	public void testLoginWithInvalidPassword() throws Exception {
		context.checking(new Expectations() {
			{
				oneOf(connection).getResponseCode();
				will(returnValue(403));

				oneOf(connection).disconnect();

				// Never tries to save credentials
				never(securePrefs).put("username", TEST_USER, true);
				never(securePrefs).put("password", TEST_PASSWORD, true);
				never(securePrefs).flush();
			}
		});
		IStatus status = manager.login(TEST_USER, "invalid_password");
		assertFalse("User is able to log into JIRA with an invalid password",
				status.isOK());
		context.assertIsSatisfied();
	}

	@Test
	public void testCreateIssueLoggedOutFails() throws Exception {
		final String summary = "Testing creating an issue";
		final String description = "This is a test";
		
		//config data
		JiraIssueConfig jiraConfig = new JiraIssueConfig();
		jiraConfig.type = JiraIssueType.BUG;
		jiraConfig.severity = JiraIssueSeverity.TRIVIAL;
		jiraConfig.summary = summary;
		jiraConfig.description = description;
		
		try {
			manager.createIssue(jiraConfig);
			fail("Shouldn't allow ticket creation with no user!");
		} catch (JiraException e) {
			// expected
		}

		context.assertIsSatisfied();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCreateIssue() throws Exception {
		testLogin();

		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		String response = "{\"id\":\"129500\",\"key\":\"APSTUD-4030\",\"self\":\"https://jira.appcelerator.org/rest/api/2/issue/129500\"}";
		final InputStream in = new ByteArrayInputStream(response.getBytes());
		context.checking(new Expectations() {
			{
				oneOf(connection).setRequestMethod("POST");
				oneOf(connection).setDoOutput(true);
				oneOf(connection).getOutputStream();
				will(returnValue(out));

				oneOf(connection).getResponseCode();
				will(returnValue(201));

				oneOf(connection).getInputStream();
				will(returnValue(in));

				oneOf(connection).disconnect();
			}
		});

		final String summary = "Testing creating an issue";
		final String description = "This is a test";
		
		// Config data
		JiraIssueConfig jiraConfig = new JiraIssueConfig();
		jiraConfig.type = JiraIssueType.BUG;
		jiraConfig.severity = JiraIssueSeverity.TRIVIAL;
		jiraConfig.summary = summary;
		jiraConfig.description = description;
			
		JiraIssue issue = manager.createIssue(jiraConfig);

		// Verify the ticket we generated
		assertEquals("https://jira.appcelerator.org/browse/APSTUD-4030",
				issue.getUrl());
		assertEquals("129500", issue.getId());
		assertEquals("APSTUD-4030", issue.getName());

		// Verify the request we sent out!
		String request = out.toString();
		Map<String, Object> reqJSON = (Map<String, Object>) JSON.parse(request);

		// Verify key parts of the request JSON
		Map<String, Object> fields = (Map<String, Object>) reqJSON
				.get("fields");

		Map<String, Object> project = (Map<String, Object>) fields
				.get("project");
		assertEquals("APSTUD", project.get("key"));

		assertEquals(summary, fields.get("summary"));
		assertEquals(description, fields.get("description"));

		// check issue type
		Map<String, Object> issuetype = (Map<String, Object>) fields
				.get("issuetype");
		assertEquals("Bug", issuetype.get("name"));

		// check severity
		Map<String, Object> severity = (Map<String, Object>) fields
				.get("customfield_10090");
		assertEquals("Trivial", severity.get("value"));

		// check version
		Object[] versions = (Object[]) fields.get("versions");
		Map<String, Object> version = (Map<String, Object>) versions[0];
		assertEquals("1.2.3", version.get("name"));

		context.assertIsSatisfied();
	}

	@Test
	public void testAttachFile() throws Exception {
		testLogin();

		final StatusLine sl = context.mock(StatusLine.class);
		final HttpEntity entity = context.mock(HttpEntity.class);

		File file = FileUtil.createTempFile("debug", ".txt");
		IPath path = Path.fromOSString(file.getAbsolutePath());

		context.checking(new Expectations() {
			{
				oneOf(response).getStatusLine();
				will(returnValue(sl));

				oneOf(sl).getStatusCode();
				will(returnValue(200));

				oneOf(response).getEntity();
				will(returnValue(entity));

				oneOf(entity).getContent();
			}
		});

		manager.addAttachment(path, new JiraIssue("TC-1289", "something",
				"http://jira.appcelerator.org/browse/TC-1289"));

		// verify the post follows some rules...
		// special cross-domain header
		Header h = fFilePost.getFirstHeader("X-Atlassian-Token");
		assertEquals(
				new URI(
						"https://jira.appcelerator.org/rest/api/2/issue/TC-1289/attachments"),
				fFilePost.getURI());
		assertEquals("nocheck", h.getValue());

		HttpEntity e = fFilePost.getEntity();
		assertTrue(e instanceof MultipartEntity);
		// TODO Test that the entity has a part named "file"
		// MultipartEntity mpe = (MultipartEntity) e;
		// mpe.

		context.assertIsSatisfied();
	}

	@Test
	public void testGetProjectVersions() throws Exception {
		testLogin();

		String response = "[{\"self\":\"https://jira.appcelerator.org/rest/api/2/version/17032\",\"id\":\"17032\",\"name\":\"Appcelerator Studio 4.3.0\",\"archived\":false,\"released\":false,\"projectId\":12217},{\"self\":\"https://jira.appcelerator.org/rest/api/2/version/17033\",\"id\":\"17033\",\"name\":\"Appcelerator Studio 4.3.1\",\"archived\":false,\"released\":false,\"projectId\":12217},{\"self\":\"https://jira.appcelerator.org/rest/api/2/version/17034\",\"id\":\"17034\",\"name\":\"Appcelerator Studio 4.3.2\",\"archived\":false,\"released\":false,\"projectId\":12217},{\"self\":\"https://jira.appcelerator.org/rest/api/2/version/17035\",\"id\":\"17035\",\"name\":\"Appcelerator Studio 4.4.0\",\"archived\":false,\"released\":false,\"projectId\":12217},{\"self\":\"https://jira.appcelerator.org/rest/api/2/version/17036\",\"id\":\"17036\",\"name\":\"Appcelerator Studio 4.4.1\",\"archived\":false,\"released\":false,\"projectId\":12217},{\"self\":\"https://jira.appcelerator.org/rest/api/2/version/17037\",\"id\":\"17037\",\"name\":\"Appcelerator Studio 4.4.2\",\"archived\":false,\"released\":false,\"projectId\":12217},{\"self\":\"https://jira.appcelerator.org/rest/api/2/version/17038\",\"id\":\"17038\",\"name\":\"Appcelerator Studio 4.5.0\",\"archived\":false,\"released\":false,\"projectId\":12217}]\r\n";
		final InputStream in = new ByteArrayInputStream(response.getBytes());
		context.checking(new Expectations() {
			{
				oneOf(connection).setRequestMethod("GET");

				oneOf(connection).getResponseCode();
				will(returnValue(201));

				oneOf(connection).getInputStream();
				will(returnValue(in));

				oneOf(connection).disconnect();
			}
		});

		List<String> projectVersions = manager.getProjectVersions();
		assertTrue(projectVersions.contains("Appcelerator Studio 4.3.0"));
		assertTrue(projectVersions.contains("Appcelerator Studio 4.3.1"));
		assertTrue(projectVersions.contains("Appcelerator Studio 4.3.2"));
		
		assertFalse(projectVersions.contains("Appcelerator Studio 4.3.3"));
		
		assertTrue(projectVersions.contains("Appcelerator Studio 4.4.0"));
		assertTrue(projectVersions.contains("Appcelerator Studio 4.4.1"));
		assertTrue(projectVersions.contains("Appcelerator Studio 4.4.2"));
		assertTrue(projectVersions.contains("Appcelerator Studio 4.5.0"));
		
		assertFalse(projectVersions.contains("Appcelerator Studio 5.0.0"));
		

	}
}
