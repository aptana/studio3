package com.aptana.git.ui.internal.actions;

import junit.framework.TestCase;

public class CreatePullRequestHandlerTest extends TestCase
{

	protected void setUp() throws Exception
	{
		super.setUp();
	}

	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	public void testSSHGithubURL() throws Exception
	{
		assertEquals("titanium_studio",
				CreatePullRequestHandler.getGithubRepoName("git@github.com:appcelerator/titanium_studio.git"));
	}

	public void testHTTPSGithubURL() throws Exception
	{
		assertEquals("titanium_studio",
				CreatePullRequestHandler.getGithubRepoName("https://github.com/appcelerator/titanium_studio.git"));
	}

	public void testPeriodInRepoName() throws Exception
	{
		assertEquals("html.ruble", CreatePullRequestHandler.getGithubRepoName("git@github.com:aptana/html.ruble.git"));
	}

	public void testDeprecatedGitReadOnlyGithubURL() throws Exception
	{
		assertEquals("repo", CreatePullRequestHandler.getGithubRepoName("git://github.com/user/repo.git"));
	}

}
