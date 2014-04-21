package com.aptana.jira.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JiraManagerIntegrationTest
{

	private static final String TEST_USER = "studio-test";
	private static final String TEST_PASSWORD = "studio";

	private JiraManager manager;

	@Before
	public void setUp() throws Exception
	{
		manager = JiraCorePlugin.getDefault().getJiraManager();
	}

	@After
	public void tearDown() throws Exception
	{
		manager = null;
	}

	@Test
	public void testLogin()
	{
		IStatus status = manager.login(TEST_USER, TEST_PASSWORD);
		assertTrue(status.isOK());
	}

	@Test
	public void testLoginWithInvalidUser()
	{
		IStatus status = manager.login("invalid_user", TEST_PASSWORD);
		assertFalse("User is able to log into JIRA with an invalid username", status.isOK());
	}

	@Test
	public void testLoginWithInvalidPassword()
	{
		IStatus status = manager.login(TEST_USER, "invalid_password");
		assertFalse("User is able to log into JIRA with an invalid password", status.isOK());
	}

	/**
	 * For manual testing/debugging of creating issues!
	 */
	// @Test
	// public void testCreateIssue() throws Exception
	// {
	// manager.login(TEST_USER, TEST_PASSWORD);
	// JiraIssue issue = manager.createIssue(JiraIssueType.BUG, JiraIssueSeverity.TRIVIAL,
	// "Testing creating an issue", "This is a test");
	// System.out.println(issue.getUrl());
	// }

	/**
	 * For manual testing/debugging of attachments!
	 */
	@Test
	public void testAttachFile() throws JiraException
	{
		manager.login(TEST_USER, TEST_PASSWORD);
		manager.addAttachment(Path.fromOSString("/Users/cwilliams/Documents/debug.txt"), new JiraIssue("TC-4030", "something",
				"http://jira.appcelerator.org/browse/TC-4030"));
	}
}
