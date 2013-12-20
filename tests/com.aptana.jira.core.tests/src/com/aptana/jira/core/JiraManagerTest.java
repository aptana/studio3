package com.aptana.jira.core;

import org.junit.After;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import junit.framework.TestCase;

public class JiraManagerTest
{

	private static final String TEST_USER = "studio-test";
	private static final String TEST_PASSWORD = "studio";

	private JiraManager manager;

//	@Override
	@Before
	public void setUp() throws Exception
	{
		manager = JiraCorePlugin.getDefault().getJiraManager();
	}

//	@Override
	@After
	public void tearDown() throws Exception
	{
		manager = null;
	}

	@Test
	public void testLogin()
	{
		try
		{
			manager.login(TEST_USER, TEST_PASSWORD);
		}
		catch (JiraException e)
		{
			fail("Valid user failed to log into JIRA due to reason '" + e.getMessage() + "'");
		}
	}

	@Test
	public void testLoginWithInvalidUser()
	{
		try
		{
			manager.login("invalid_user", TEST_PASSWORD);
			fail("User is able to log into JIRA with an invalid username");
		}
		catch (JiraException e)
		{
		}
	}

	@Test
	public void testLoginWithInvalidPassword()
	{
		try
		{
			manager.login(TEST_USER, "invalid_password");
			fail("User is able to log into JIRA with an invalid password");
		}
		catch (JiraException e)
		{
		}
	}

	// /**
	// * For manual testing/debugging of creating issues!
	// */
	// public void testCreateIssue() throws Exception
	// {
	// manager.login(TEST_USER, TEST_PASSWORD);
	// JiraIssue issue = manager.createIssue(JiraIssueType.BUG, JiraIssueSeverity.TRIVIAL,
	// "Testing creating an issue", "This is a test");
	// System.out.println(issue.getUrl());
	// }
	//
	// /**
	// * For manual testing/debugging of attachments!
	// */
	// public void testAttachFile() throws JiraException
	// {
	// manager.login(TEST_USER, TEST_PASSWORD);
	// manager.addAttachment(Path.fromOSString("~/Documents/debug.txt"), new JiraIssue("TC-1289", "something",
	// "http://jira.appcelerator.org/browse/TC-1289"));
	// }
}
