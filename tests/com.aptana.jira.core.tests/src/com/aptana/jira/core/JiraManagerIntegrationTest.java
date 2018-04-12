package com.aptana.jira.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.IStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.aptana.testing.categories.IntegrationTests;

@Category({ IntegrationTests.class })
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

}
