package com.aptana.git.internal.core.github;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aptana.core.util.IOUtil;
import com.aptana.git.core.github.IGithubRepository;

public class GithubOrganizationTest
{

	private GithubOrganization org;
	private Mockery context;
	private GithubAPI api;
	private GithubManager manager;
	private IGithubRepository repo;

	@Before
	public void setUp() throws Exception
	{
		context = new Mockery()
		{
			{
				setImposteriser(ClassImposteriser.INSTANCE);
			}
		};
		api = context.mock(GithubAPI.class);
		String jsonString = IOUtil.read(FileLocator.openStream(Platform.getBundle("com.aptana.git.core.tests"),
				Path.fromPortableString("test_files/repository.json"), false));
		JSONObject json = (JSONObject) new JSONParser().parse(jsonString);
		JSONObject orgJSON = (JSONObject) json.get("organization");
		org = new GithubOrganization(orgJSON)
		{

			@Override
			protected GithubAPI getAPI()
			{
				return api;
			}
		};
	}

	@After
	public void tearDown() throws Exception
	{
		context = null;
		api = null;
		org = null;
	}

	@Test
	public void testOrganization() throws IOException, ParseException, CoreException
	{
		assertEquals(1L, org.getID());
		assertEquals("octocat", org.getName());
		assertEquals("https://api.github.com/users/octocat", org.getURL());
	}

	@Test
	public void testGetRepos() throws IOException, ParseException, CoreException
	{
		final JSONArray array = new JSONArray();
		context.checking(new Expectations()
		{
			{
				oneOf(api).get("orgs/octocat/repos");
				will(returnValue(array));
			}
		});

		List<IGithubRepository> repos = org.getRepos();
		assertNotNull(repos);
		assertEquals(0, repos.size());
		context.assertIsSatisfied();
	}
}